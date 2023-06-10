package com.grinder.game.content.skill;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;

import java.util.concurrent.TimeUnit;

public class SkillStats {

    /**
     * Decrease boosted stats Increase lowered stats
     * @param player
     */
    public static void sequenceTemporaryStats(Player player) {

        if (player.getHitpoints() > 0) {
            if (player.increaseStats.finished() || player.decreaseStats.secondsElapsed() >= (PrayerHandler.isActivated(player, PrayerHandler.PRESERVE) ? 120 : 90)) {
                for (Skill skill : Skill.values()) {
                    int current = player.getSkillManager().getCurrentLevel(skill);
                    int max = player.getSkillManager().getMaxLevel(skill);

                    // Should lowered stats be increased?
                    if (current < max) {
                        if (player.increaseStats.finished()) {
                            int restoreRate = 1;

                            // Rapid restore effect - 2x restore rate for all
                            // stats except hp/prayer
                            // Rapid heal - 2x restore rate for hitpoints
                            if (skill != Skill.HITPOINTS && skill != Skill.PRAYER) {
                                if (PrayerHandler.isActivated(player, PrayerHandler.RAPID_RESTORE)) {
                                    restoreRate = 2;
                                }
                            } else if (skill == Skill.HITPOINTS) {
                                AchievementManager.processFor(AchievementType.QUICK_RECOVERY, player);
                                if (PrayerHandler.isActivated(player, PrayerHandler.RAPID_HEAL)) {
                                    restoreRate = 2;
                                }
                            }
                            if (skill != Skill.PRAYER) {
                                player.getSkillManager().increaseLevelTemporarily(skill, restoreRate, max);
                            }
                        }
                    } else if (current > max) {

                        // Should boosted stats be decreased?
                        if (player.decreaseStats.secondsElapsed() >= (PrayerHandler.isActivated(player, PrayerHandler.PRESERVE)
                                ? 120 : 90)) {

                            boolean shouldDecrease = true;
                            //Wait 5 minutes if divine potion..
                            if (player.divineAttack.secondsRemaining() == 1 && skill == Skill.ATTACK) { player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkills().getMaximumLevel(Skill.ATTACK), true); }
                            if (player.divineStrength.secondsRemaining() == 1 && skill == Skill.STRENGTH) { player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkills().getMaximumLevel(Skill.STRENGTH), true); }
                            if (player.divineDefence.secondsRemaining() == 1 && skill == Skill.DEFENCE) { player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkills().getMaximumLevel(Skill.DEFENCE), true); }
                            if (player.divineRange.secondsRemaining() == 1 && skill == Skill.RANGED) { player.getSkillManager().setCurrentLevel(Skill.RANGED, player.getSkills().getMaximumLevel(Skill.RANGED), true); }
                            if (player.divineMagic.secondsRemaining() == 1 && skill == Skill.MAGIC) { player.getSkillManager().setCurrentLevel(Skill.MAGIC, player.getSkills().getMaximumLevel(Skill.MAGIC), true); }
                            if (player.divineAttack.secondsRemaining() > 0 && skill == Skill.ATTACK) { shouldDecrease = false; }
                            if (player.divineStrength.secondsRemaining() > 0 && skill == Skill.STRENGTH) { shouldDecrease = false; }
                            if (player.divineDefence.secondsRemaining() > 0 && skill == Skill.DEFENCE) { shouldDecrease = false; }
                            if (player.divineRange.secondsRemaining() > 0 && skill == Skill.RANGED) { shouldDecrease = false; }
                            if (player.divineMagic.secondsRemaining() > 0 && skill == Skill.MAGIC) { shouldDecrease = false; }
                            // Never decrease Hitpoints / Prayer
                            if (skill != Skill.HITPOINTS && skill != Skill.PRAYER && shouldDecrease) {
                                player.getSkillManager().decreaseLevelTemporarily(skill, 1, 1);
                            }

                        }
                    }
                }

                // Reset timerRepository
                if (player.increaseStats.finished()) {
                    player.increaseStats.start(90);
                }
                if (player.decreaseStats
                        .secondsElapsed() >= (PrayerHandler.isActivated(player, PrayerHandler.PRESERVE) ? 120 : 90)) {
                    player.decreaseStats.start((PrayerHandler.isActivated(player, PrayerHandler.PRESERVE) ? 120 : 90));
                }
            }
        }
    }

    /**
     * Checking if player is maxed
     *
     * @param player the {@link Player} to check.
     */
    public static boolean isMaxed(Player player) {
        for (Skill skill : Skill.values()) {
            if (player.getSkillManager().getMaxLevel(skill) < 99 && skill != Skill.CONSTRUCTION && skill != Skill.HUNTER) {
                return false;
            }
        }
        return true;
    }
}
