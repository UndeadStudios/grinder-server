package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.content.skill.skillable.impl.magic.Spell;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * TODO: remove all duplicate code junk
 */
public class MagicOnPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int playerIndex = packetReader.readShortA();
        int spellId = packetReader.readLEShort() & 0xFFFF;

        if (playerIndex < 0 || playerIndex > World.getPlayers().capacity())
            return;

        final Player victimPlayer = World.getPlayers().get(playerIndex);

        if (cannotCastSpell(player, victimPlayer))
            return;

        SkillUtil.stopSkillable(player);
        player.getCombat().reset(false);
        player.setEntityInteraction(victimPlayer);

        final Optional<CombatSpellType> optionalCombatSpellType = CombatSpellType.getCombatSpells(spellId);

        if (optionalCombatSpellType.isPresent()) {
            final CombatSpellType combatSpellType = optionalCombatSpellType.get();
            final CombatSpell combatSpell = combatSpellType.getSpell();
            player.getCombat().setCastSpell(combatSpell);
            player.getCombat().setCastSpellType(combatSpellType);
            player.getCombat().initiateCombat(victimPlayer, true,  () -> {
                player.getMotion().clearSteps();
            });
            return;
        }

        final Optional<InteractiveSpell> optionalNonCombatSpellType = InteractiveSpell.forSpellId(spellId);

        if (optionalNonCombatSpellType.isPresent()) {

            final InteractiveSpell interactiveSpellType = optionalNonCombatSpellType.get();
            final Spell interactiveSpell = interactiveSpellType.getSpell();

            if (interactiveSpellType.isTeleOther()) { // Tele-other spells
                player.setWalkToTask(new WalkToAction<>(player, victimPlayer, 8, 0, () -> {
                    player.setPositionToFace(victimPlayer.getPosition());
                    player.getMotion().reset();
                    player.getPacketSender().sendMinimapFlagRemoval();
                    player.getMotion().followTarget(null);
                    player.getMotion().clearSteps();

                    if (!interactiveSpell.canCast(player, null, false))
                        return;

                    if (canNotCastInteractiveSpell(player, victimPlayer))
                        return;

                    player.performAnimation(new Animation(1818));
                    player.performGraphic(new Graphic(343));
                    player.getPacketSender().sendAreaPlayerSound(199);
                    player.getSkillManager().addExperience(Skill.MAGIC, interactiveSpell.baseExperience() / 300);
                    victimPlayer.setTeleportToCaster(player.getAsPlayer());
                    victimPlayer.getPacketSender().sendInterface(12468);
                    victimPlayer.getPacketSender().sendString("" + PlayerUtil.getImages(player) + "" + player.getUsername() + "", 12558);
                    if (victimPlayer instanceof BotPlayer) {
                        final int millisTillCancel = Misc.random((int) TimeUnit.MINUTES.toMillis(15), (int) TimeUnit.MINUTES.toMillis(35));
                        final int ticksTillCancel = TimeUtil.MILLIS.toGameCycles(millisTillCancel);
                        TaskManager.submit(ticksTillCancel, () -> victimPlayer.getPacketSender().sendInterfaceRemoval());
                    }

                    interactiveSpellType.getSpell().deleteItemsRequired(player);

                    switch (interactiveSpellType) {
                        case TELEPORT_OTHER_LUMBRIDGE:
                            victimPlayer.getPacketSender().sendMessage("You have been offered a Lumbridge teleport by @dre@" + player.getUsername() + "</col>!");
                            victimPlayer.getPacketSender().sendString("Lumbridge", 12560);
                            victimPlayer.setTeleportDestination(new Position(3222 + Misc.getRandomInclusive(3), 3217 + Misc.getRandomInclusive(3), 0));
                            break;
                        case TELEPORT_OTHER_FALADOR:
                            victimPlayer.getPacketSender().sendMessage("You have been offered a Falador teleport by @dre@" + player.getUsername() + "</col>!");
                            victimPlayer.getPacketSender().sendString("Falador", 12560);
                            victimPlayer.setTeleportDestination(new Position(2965 + Misc.getRandomInclusive(3), 3388 + Misc.getRandomInclusive(3), 0));
                            break;
                        case TELEPORT_OTHER_CAMELOT:
                            victimPlayer.getPacketSender().sendMessage("You have been offered a Camelot teleport by @dre@" + player.getUsername() + "</col>!");
                            victimPlayer.getPacketSender().sendString("Camelot", 12560);
                            victimPlayer.setTeleportDestination(new Position(2756 + Misc.getRandomInclusive(3), 3476 + Misc.getRandomInclusive(3), 0));
                            break;
                    }
                }, WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
            }
        }
    }

    private boolean cannotCastSpell(Player player, Player victimPlayer) {
        if (player == null || player.getHitpoints() <= 0)
            return true;

        if (player.busy()) {
            player.sendMessage("You can't do that right now.");
            return true;
        }
        if (player.isInTutorial())
            return true;
        if (victimPlayer.isInTutorial())
            return true;
        if (player.BLOCK_ALL_BUT_TALKING)
            return true;
        if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }
        if (player.isTeleporting()) {
            return true;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            return true;
        }
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            return true;
        }

        if (player.getMotion().hasFollowTarget())
            player.getMotion().resetTargetFollowing();

        if (victimPlayer.getDueling().inDuel()) {
            if (player.getDueling().inDuel() == false) {
                player.setPositionToFace(victimPlayer.getPosition());
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.sendMessage("You don't have an active duel target.");
                return true;
            }
        }
        if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) {
            player.getPacketSender().sendMessage("You can't use this spell while your on sled!", 1000);
            player.setPositionToFace(victimPlayer.getPosition());
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            player.setEntityInteraction(null);
            return true;
        }
        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true)){
            player.setPositionToFace(victimPlayer.getPosition());
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            player.setEntityInteraction(null);
            return true;
        }
        if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 20056) {
            player.getPacketSender().sendMessage("You can't use this spell while holding Ale of the gods!", 1000);
            player.setPositionToFace(victimPlayer.getPosition());
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            player.setEntityInteraction(null);
            return true;
        }
        if (victimPlayer.equals(player)) {
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            return true;
        }
        if (victimPlayer.getHitpoints() <= 0) {
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            return true;
        }
        if (player.getDueling().inDuel()) {
            if (!player.getDueling().getInteract().getUsername().equals(victimPlayer.getUsername())) {
                player.setPositionToFace(victimPlayer.getPosition());
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.sendMessage("That isn't your opponent.");
                return true;
            }
        }

        if (victimPlayer.getDueling().inDuel()) {
            if (player.getDueling().inDuel() == false) {
                player.setPositionToFace(victimPlayer.getPosition());
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                player.sendMessage("That isn't your opponent.");
                return true;
            }
        }
        if (Math.abs(player.getPosition().getX() - victimPlayer.getPosition().getX()) > 15 || Math.abs(player.getPosition().getY() - victimPlayer.getPosition().getY()) > 15) {
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            player.sendMessage("I can't reach that!");
            return true;
        }
        return false;
    }

    private boolean canNotCastInteractiveSpell(Player player, Player victimPlayer) {
        if (!player.getClickDelay().elapsed(800)) {
            player.sendMessage("You must wait a few seconds before you can cast the next spell.");
            player.getClickDelay().reset();
            return true;
        }
        if (victimPlayer.busy() || victimPlayer.getInterfaceId() > 0) {
            player.sendMessage("The player is currently busy and can't be teleported.");
            return true;
        }
        if (victimPlayer.getRelations().getIgnoreList().contains(player.getLongUsername())) {
            player.sendMessage("You can't teleport players that have you on their ignore list.");
            return true;
        }
        if (victimPlayer.getDueling().inDuel()) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.TRADING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.isJailed()) {
            player.sendMessage("You can't teleport players that are jailed!");
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.BANKING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.DUELING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.PRICE_CHECKING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.BLOCK_ALL_BUT_TALKING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.isInTutorial()) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (EntityExtKt.getBoolean(victimPlayer, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(victimPlayer, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.getCombat().isInCombat()) {
            player.sendMessage("The player is currently in combat and can't be teleported.", 1000);
            return true;
        }
        if (AreaManager.inWilderness(victimPlayer) && victimPlayer.getWildernessLevel() >= 20) {
            player.sendMessage("You can't teleport other players that are in deeper than level 20 Wilderness.");
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.sendMessage("<img=779> You can't teleport players that are AFK.", 1000);
            return true;
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
            return true;

        if (victimPlayer.getStatus() == PlayerStatus.TRADING) {
            player.sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        if (victimPlayer.getStatus() == PlayerStatus.DUELING) {
            player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
            return true;
        }
        return false;
    }
}
