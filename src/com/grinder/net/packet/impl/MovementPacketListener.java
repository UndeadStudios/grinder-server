package com.grinder.net.packet.impl;

import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.minigame.chamberoxeric.room.COXPassage;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.Catapult;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.content.miscellaneous.randomevent.RandomEvents;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.movement.PlayerMotion;
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.message.decoder.MovementMessageDecoder;
import com.grinder.game.message.impl.MovementMessage;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Boundaries;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.PestControlArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

/**
 * This packet listener is called when a player has clicked on either the
 * mini-map or the actual game map to move around.
 *
 * @author Gabriel Hannason
 */
public class MovementPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader reader, int packetOpcode) {

        final MovementMessage message = MovementMessageDecoder.Companion.decode(reader);

		if (!player.getTimerRepository().has(TimerKey.LAST_SOUND) && player.getArea() != null) {
            if (AreaManager.BANK_AREAS.contains(player) && Misc.getRandomInclusive(65) == 1) {
                player.getPacketSender().sendSound(Misc.randomInt(Sounds.SOMETHING_IN_GRAND_EXCHANGE));
            } else if (AreaManager.inside(player.getPosition(), Boundaries.HOME_AREAS) && Misc.getRandomInclusive(100) == 1) {
                player.getPacketSender().sendSound(Misc.randomInt(Sounds.RANDOM_SOUND_WHILE_WALKING));
            } else if (Misc.getRandomInclusive(65) == 1 && AreaManager.inside(player.getPosition(), Boundaries.GREEN_TREE_AREAS)
            && player.getPosition().getZ() == 0
            && !player.getCombat().isInCombat()
            && !AreaManager.inside(player.getPosition(), AreaManager.DUEL_ARENA)) {
                        player.getPacketSender().sendSound(Misc.randomInt(Sounds.WALKING_BETWEEN_TREES));
            }
            player.getTimerRepository().register(TimerKey.LAST_SOUND, 60);
		}

        // Pest control random sounds
        if (!player.getTimerRepository().has(TimerKey.LAST_PESTCONTROL_SOUND)) {
            if (player.getArea() instanceof PestControlArea && Misc.getRandomInclusive(3) == 1) {
                player.getPacketSender().sendSound(Misc.randomInt(Sounds.PEST_CONTROL_AREA_SOUNDS));
                player.getTimerRepository().register(TimerKey.LAST_PESTCONTROL_SOUND, 5);
            }
        }


        if (!canMove(player, packetOpcode)) {
            player.getPacketSender().sendMinimapFlagRemoval();
            player.getMotion().cancelTask();
            return;
        }
        if (player.getInterfaceId() != 54000 && player.getInterfaceId() != Presetables.INTERFACE_ID)
            player.getPacketSender().sendInterfaceRemoval();

        SkillUtil.stopSkillable(player);
        final PlayerCombat combat = player.getCombat();

        //if(player.busy() || combat.isInActiveCombat())
        if (!player.hasPendingMovement()) {
            player.subscribe(event -> {
                if(event == PlayerEvents.WALK){
                    if(player.getForceMovement() == null && !combat.isUnderAttack()){
                        player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                    }
                }
                return true;
            });
        }

        combat.setCastSpell(null);
        combat.reset(false);
        final PlayerMotion motion = player.getMotion();

        final Position[] steps = message.getSteps();

        if(steps.length > 0 && !steps[0].sameAs(player.getPosition())){
            motion.resetTargetFollowing();
            player.setEntityInteraction(null);
            player.notify(PlayerEvents.WALK);
        }

        boolean shiftTeleport = message.getTeleport();

        handleMiscellaneous(player);

        for(Position step: steps){
            step.setZ(player.getPosition().getZ());
        }

        final Position destination = steps[steps.length - 1];

        // Validate shift teleport..
        if (shiftTeleport) {
            if (!player.getRights().anyMatch(PlayerRights.ADMINISTRATOR, PlayerRights.CO_OWNER, PlayerRights.DEVELOPER, PlayerRights.OWNER)) {
                shiftTeleport = false;
            }
        }

        motion.cancelTask();

        if (!shiftTeleport) {

            if (player.getPosition().getDistance(destination) >= 64)
                return;

            Position finalDest = PathFinder.INSTANCE.find(player, destination);

            if (finalDest == null)
                player.getPacketSender().sendMinimapFlagRemoval();
            else
                player.getPacketSender().sendMinimapFlag(finalDest);

            motion.markDestination(destination);

        } else
            player.moveTo(destination);


        COXPassage.pass(player);
    }

    private void handleMiscellaneous(Player player) {
        if(AreaManager.inside(player.getPosition(),AreaManager.WARRIORS_GUILD_AREA)){
            if(Catapult.inside(player)){
                if(Catapult.isShieldEquipped(player))
                    Catapult.unEquipShield(player);
            }
        }

        if (player.getAttributes().bool(Attribute.IS_DRUNK)) {
            MorphItems.INSTANCE.resetRunning(player);
            player.setBas(2769);
            player.updateAppearance();
        }

        if (player.getAttributes().bool(Attribute.IS_FLYING)) {
            player.setBas(1851);
            player.updateAppearance();
        }
        if (player.isRunning() && player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 20056) {
            player.setRunning(false);
            player.getPacketSender().sendRunStatus();
            player.getPacketSender().sendMessage("You can't run while holding the Ale of the gods.", 1000);
        }
        if (player.isRunning() && player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 7671 || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 7673
                || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 11705 || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 11706) {
            player.setRunning(false);
            player.getPacketSender().sendRunStatus();
            player.getPacketSender().sendMessage("You can't run while holding boxing gloves.", 1000);
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "", false, true))
            MorphItems.INSTANCE.resetRunning(player);
    }

    private boolean canMove(Player player, int opcode) {

        if (player.getHitpoints() <= 0)
            return false;

        if (player.isAccountFlagged()) {
            player.flagAccount();
            return false;
        }

        if (player.getGameMode().isOneLife() && player.fallenOneLifeGameMode()) {
            player.sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.");
            return false;
        }

        if (player.BLOCK_ALL_BUT_TALKING)
            return false;

        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) ) {
            RandomEvents.foodPuzzle(player);
            return false;
        }
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            RandomEvents.refreshmentsPuzzle(player);
            return false;
        }

        if(player.getForceMovement() != null)
            return false;

        if (player.isInTutorial())
            return false;

        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.getPacketSender().sendMessage("You can't move when you're on AFK mode, type ::back to return!", 1000);
            return false;
        }

        if (player.getTimerRepository().has(TimerKey.FREEZE)) {
        	player.getPacketSender().sendMessage("A magical spell has made you unable to move.", 1000);
        	player.getPacketSender().sendSound(Sounds.FROZEN_CANT_MOVE);
            return false;
        }

        // Duel, disabled movement?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MOVEMENT.ordinal()]) {
            if (opcode != PacketConstants.COMMAND_MOVEMENT_OPCODE) {
                DialogueManager.sendStatement(player, "Movement has been disabled in this duel!");
            }
            return false;
        }

        // Stun
        if (player.getTimerRepository().has(TimerKey.STUN)) {
            player.getPacketSender().sendMessage("You're stunned!", 1000);
            return false;
        }

        // rest from home teleport on movement
        if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }

        return !player.isTeleporting() && !player.hasPendingTeleportUpdate() && player.getMotion().canMove();
    }
}
