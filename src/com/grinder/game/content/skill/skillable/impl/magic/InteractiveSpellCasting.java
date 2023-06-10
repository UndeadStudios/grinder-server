package com.grinder.game.content.skill.skillable.impl.magic;

import java.util.Optional;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.EffectTimer;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Misc;

/**
 * Handles spells with special effects.
 */
public class InteractiveSpellCasting {

    public static boolean handleSpell(Player player, int button) {

        Optional<InteractiveSpell> spell = InteractiveSpell.forSpellId(button);

        if (spell.isEmpty())
            return false;

        if (player.BLOCK_ALL_BUT_TALKING)
        	return false;

        if (player.isInTutorial()) {
            player.sendMessage("You cannot use this teleport while in a tutorial.");
            return false;
        }

        if (!PlayerExtKt.notAfkOrBusyOrInteracting(player))
            return false;

        if (!spell.get().getSpell().canCast(player, null, false))
            return true;

        if(spell.get() == InteractiveSpell.CHARGE)
            spell.get().getSpell().startCast(player, player);

        SkillUtil.stopSkillable(player);

        switch (spell.get()) {
        case TARGET_TELEPORT:
        case TARGET_TELEPORT_ANCIENT:
        case TARGET_TELEPORT_LUNAR:
            if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return false;
        	if (BountyHunterManager.canTeleportToTarget(player)) {
                BountyHunterManager.teleportToBountyTarget(player, spell.get());
            }
        	break;
        case OURANIA_TELEPORT:
                onTeleportSpell(player, spell, 3053, 1, 5579, 1);
        	break;
        case KOUREND_TELEPORT:
                onTeleportSpell(player, spell, 1632, 4, 3679, 2);
        	break;
        case APE_ATOLL_TELPEORT:
                onTeleportSpell(player, spell, 2757, 0, 2773, 2);
        	break;
        case VARROCK_TELEPORT:
                onTeleportSpell(player, spell, 3212, 2, 3422, 2);
            break;
        case LUMBRIDGE_TELEPORT:
                onTeleportSpell(player, spell, 3221, 3, 3218, 1);
            break;
        case HOME_TELEPORT:
                onTeleportSpell(player, spell, 3092, 4, 3484, 2);
            break;
        case FALADOR_TELEPORT:
                onTeleportSpell(player, spell, 2962, 5, 3386, 3);
            break;
        case CAMELOT_TELEPORT:
                onTeleportSpell(player, spell, 2756, 2, 3476, 2);
            break;
        case ARDOUGNE_TELEPORT:
                onTeleportSpell(player, spell, 2660, 5, 3302, 5);
            break;
        case WATCHTOWER_TELEPORT:
                onTeleportSpell(player, spell, 2545, 2, 3112, 1);
            break;
        case TROLLHEIM_TELEPORT:
                onTeleportSpell(player, spell, 2890, 3, 3678, 2);
            break;
        case PADDEWWA_TELEPORT:
                onTeleportSpell(player, spell, 3097, 3, 9883, 3);
            break;
        case SENNTISTEN_TELEPORT:
                onTeleportSpell(player, spell, 3321, 1, 3337, 1);
            break;
        case KHARYRLL_TELEPORT:
                onTeleportSpell(player, spell, 3493, 1, 3474, 1);
            break;
        case LASSAR_TELEPORT:
                onTeleportSpell(player, spell, 3006, 1, 3471, 3);
            break;
        case DAREEYAK_TELEPORT:
            if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return false;
            if (TeleportHandler.checkReqs(player, new Position(2968, 3695, 0), true, true, player.getSpellbook().getTeleportType())) {
                PlayerExtKt.resetInteractions(player, true, false);
                TeleportHandler.wildernessSpellTeleport(player, new Position(2968 + Misc.getRandomInclusive(3), 3695 + Misc.getRandomInclusive(2), 0), player.getSpellbook().getTeleportType(), true, true, button);
            }
            break;
        case CARRALLANGAR_TELEPORT:
              if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return false;
            if (TeleportHandler.checkReqs(player, new Position(3158, 3668, 0), true, true, player.getSpellbook().getTeleportType())) {
                PlayerExtKt.resetInteractions(player, true, false);
                TeleportHandler.wildernessSpellTeleport(player, new Position(3158 + Misc.getRandomInclusive(1), 3668 + Misc.getRandomInclusive(2), 0), player.getSpellbook().getTeleportType(), true, true, button);
            }
            break;
        case ANNAKARL_TELEPORT:
            if (TeleportHandler.checkReqs(player, new Position(3287, 3886, 0), true, true, player.getSpellbook().getTeleportType())) {
                PlayerExtKt.resetInteractions(player, true, false);
                TeleportHandler.wildernessSpellTeleport(player, new Position(3287 + Misc.getRandomInclusive(1), 3886 + Misc.getRandomInclusive(2), 0), player.getSpellbook().getTeleportType(), true, true, button);
            }
            break;
        case GHORROCK_TELEPORT:
              if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return false;
            if (TeleportHandler.checkReqs(player, new Position(2974, 3872, 0), true, true, player.getSpellbook().getTeleportType())) {
                PlayerExtKt.resetInteractions(player, true, false);
                TeleportHandler.wildernessSpellTeleport(player, new Position(2974 + Misc.getRandomInclusive(5), 3872 + Misc.getRandomInclusive(5), 0), player.getSpellbook().getTeleportType(), true, true, button);
            }
            break;
        case MOONCLAN_TELEPORT:
                onTeleportSpell(player, spell, 2113, 1, 3915, 2);
            break;
        case WATERBIRTH_TELEPORT:
                onTeleportSpell(player, spell, 2548, 3, 3755, 3);
            break;
        case BARBARIAN_TELEPORT:
                onTeleportSpell(player, spell, 2543, 2, 3567, 2);
            break;
        case KHAZARD_TELEPORT:
                onTeleportSpell(player, spell, 2635, 1, 3168, 1);
            break;
        case FISHING_GUILD_TELEPORT:
                onTeleportSpell(player, spell, 2611, 2, 3390, 2);
            break;
        case CATHERBY_TELEPORT:
                onTeleportSpell(player, spell, 2801, 3, 3447, 3);
            break;
        case ICE_PLATEAU_TELEPORT:
              if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return false;
            if (TeleportHandler.checkReqs(player, new Position(2975, 3939, 0), true, true, player.getSpellbook().getTeleportType())) {
                PlayerExtKt.resetInteractions(player, true, false);
                TeleportHandler.wildernessSpellTeleport(player, new Position(2975 + Misc.getRandomInclusive(1), 3939 + Misc.getRandomInclusive(1), 0), player.getSpellbook().getTeleportType(), true, true, button);
            }
            break;
        case LUMBRIDGE_GRAVEYARD_TELEPORT:
                onTeleportSpell(player, spell, 3244, 2, 3196, 3);
            break;
        case DRAYNOR_MANOR_TELEPORT:
                onTeleportSpell(player, spell, 3108, 1, 3350, 1);
            break;
        case BATTLEFRONT_TELEPORT:
                onTeleportSpell(player, spell, 1345, 2, 3741, 0);
            break;
        case MIND_ALTAR_TELEPORT:
                onTeleportSpell(player, spell, 2978, 1, 3508, 0);
            break;
            case RESPAWN_TELEPORT:
                onTeleportSpell(player, spell, 2970, 3, 3342, 3);
            break;
        case SALVE_GRAVEYARD_TELEPORT:
                onTeleportSpell(player, spell, 3432, 2, 3460, 2);
            break;
        case FENKENSTRAINS_CASTLE_TELEPORT:
                onTeleportSpell(player, spell, 3547, 3, 3527, 3);
            break;
        case WEST_ARDOUGNE_TELEPORT:
                onTeleportSpell(player, spell, 2500, 2, 3291, 1);
            break;
        case HARMONY_ISLAND_TELEPORT:
                onTeleportSpell(player, spell, 3797, 1, 2868, 1);
            break;
        case CEMETERY_TELEPORT:
                onTeleportSpell(player, spell, 2980, 0, 3763, 1);
            break;
        case BARROWS_TELEPORT:
                onTeleportSpell(player, spell, 3565, 2, 3315, 1);
            break;
        case APE_ATOLL_TELEPORT:
                onTeleportSpell(player, spell, 2765, 2, 9103, 1);
            break;
        case TELEPORT_HOUSE:
                onTeleportSpell(player, spell, 2956, 2, 3224, 2);
        	break;
            case BONES_TO_PEACHES:
            case BONES_TO_BANANAS:
                if (!player.getCombat().getSpellBlockTimer().finished()) {
                    player.getPacketSender().sendMessage("You must wait another " + player.getCombat().getSpellBlockTimer().secondsRemaining() + " " + ((player.getCombat().getSpellBlockTimer().secondsRemaining() == 1) ? "second" : "seconds") + " before you can cast that again.", 1000);
                    return true;
                }
                if (!player.getInventory().contains(526)) {
                    player.getPacketSender().sendMessage("You do not have any bones in your inventory.", 1000);
                    return true;
                }
                PlayerExtKt.resetInteractions(player, true, false);
                spell.get().getSpell().deleteItemsRequired(player);
                int i = 0;
                for (Item invItem : player.getInventory().getValidItems()) {
                    if (invItem.getId() == 526) {
                        player.getInventory().delete(526, 1).add(spell.get() == InteractiveSpell.BONES_TO_PEACHES ? 6883 : 1963, 1);
                        i++;
                    }
                }
                player.getCombat().getSpellBlockTimer().start(1);
                player.getAsPlayer().getPacketSender().sendSound(Sounds.BONES_TO_BANANA_SPELL);
                player.performGraphic(new Graphic(141, GraphicHeight.MIDDLE));
                player.performAnimation(new Animation(722));
                player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience() * i / 200);

                // Increase points
                player.getPoints().increase(AttributeManager.Points.BONES_SPELL_CASTS); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

                player.getClickDelay().reset();
                break;
            case VENGEANCE:
                if (player.getDueling().inDuel()) {
                    player.getPacketSender().sendMessage("You can't cast Vengeance during a duel!", 1000);
                    return true;
                }
                if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 40) {
                    player.getPacketSender().sendMessage("You need at least level 40 Defence to cast this spell.", 1000);
                    return true;
                }
                if (!player.getVengeanceDelayTimer().finished()) {
                    player.getPacketSender().sendMessage("You can only cast vengeance spells every 30 seconds.", 1000);
                    return true;
                }
                if (!player.getVengeanceEffect().finished()) {
                    player.getPacketSender().sendMessage("You already have the vengeance effect.", 1000);
                    return true;
                }
                spell.get().getSpell().deleteItemsRequired(player);
                PlayerExtKt.resetInteractions(player, true, false);

                //Send message and effect timer to client
                player.getPacketSender().sendAreaPlayerSound(Sounds.VENGENANCE_SPELL);
                player.getVengeanceEffect().start(30);
                player.getVengeanceDelayTimer().start(30);
                player.getPacketSender().sendEffectTimer(30, EffectTimer.VENGEANCE);

                AchievementManager.processFor(AchievementType.VENGEANCE, player);
                player.getPacketSender().sendMessage("You have the power of vengeance!", 1000);
                player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience() / 100);
                player.performAnimation(new Animation(4409));

                // Increase points
                player.getPoints().increase(AttributeManager.Points.VENG_CASTS); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

                player.performGraphic(new Graphic(726, GraphicHeight.HIGH));
                break;
        }
        return true;
    }

    private static void onTeleportSpell(Player player, Optional<InteractiveSpell> spell, int baseX, int randomX, int baseY, int randomY) {
        if (!TeleportHandler.checkReqsNoCombat(player, new Position(baseX, baseY, 0), true, false, player.getSpellbook().getTeleportType())) {
            return;
        }
        if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
            return;
        PlayerExtKt.resetInteractions(player, true, false);
        TeleportHandler.teleportNoCombatChecks(player,
                new Position(baseX + Misc.getRandomInclusive(randomX), baseY + Misc.getRandomInclusive(randomY), 0),
                player.getSpellbook().getTeleportType(), true, true);
        player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience());

        // Increase points
        player.getPoints().increase(AttributeManager.Points.SPELL_BOOK_TELEPORTS); // Increase points
        player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points
        spell.get().getSpell().deleteItemsRequired(player);
    }

}
