package com.grinder.game.content.skill;

import com.grinder.game.GameConstants;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.FrogPrinceEvent;
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager;
import com.grinder.game.content.skill.skillable.SkillActionTask;
import com.grinder.game.content.skill.skillable.Skillable;
import com.grinder.game.content.skill.skillable.impl.Mining;
import com.grinder.game.content.skill.skillable.impl.Woodcutting;
import com.grinder.game.content.skill.skillable.impl.mining.RockType;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.woodcutting.TreeType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Objects;
import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author relex lawl
 * @author Professor Oak
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/10/2019
 */
public class SkillUtil {

    /**
	 * Initiates a {@link SkillActionTask}.
	 *
	 * @param player the {@link Player} subject of the task.
	 * @param task   the {@link SkillActionTask} instance.
	 */
	public static void startActionTask(Player player, SkillActionTask task) {
		if (task == null)
			return;

		final int delay = (int) (task.getDelay() * 0.6);

		if (!task.isImmediate() && !player.getClickDelay().elapsed(delay * 1000))
			return;

		if (!task.precheck(player))
			return;

		player.getPacketSender().sendInterfaceRemoval();
		player.getClickDelay().reset();
		player.setSkillAction(task);
		TaskManager.submit(task.action(player));
	}

    /**
     * Starts the {@link Skillable} task if the requirements are met by the player.
     *
     * @param player the {@link Player} subject to the task.
     * @param skill the {@link Skillable} task.
     */
    public static void startSkillable(Player player, Skillable skill) {

        stopSkillable(player);
        player.getPacketSender().sendInterfaceRemoval();

        if (!skill.hasRequirements(player))
            return;
        //player.sendMessage("starting skillable");
        player.setSkill(Optional.of(skill));
        skill.start(player);
    }

    /**
     * Stops any {@link Skillable} task if present.
     *
     * @param player the {@link Player} subject to the task if present.
     */
    public static void stopSkillable(Player player) {
        player.getSkill().ifPresent(e -> e.cancel(player));
        player.setSkill(Optional.empty());
        player.setCreationMenu(Optional.empty());
        if (player.hasAutoTalkerMessageActive()) {
            player.sendMessage("@red@The message auto-typer has been interrupted. Type ::repeat to repeat the last message.");
            player.getAutoChatBreakTimer().start(10);
            player.setHasAutoTalkerMessageActive(false);
        }
        if (!Objects.equals(player.getMessageToAutoTalk(), "")) {
            player.setMessageToAutoTalk("");
        }
        // Logout timer in the Wilderness
        if (player.hasLogoutTimer()) {
            //player.getPacketSender().sendInterfaceRemoval();
            player.sendMessage("@red@Your logout request has been interrupted.");
            player.setHasLogoutTimer(false);
        }
    }

    /**
     * Gets the minimum experience in said level.
     *
     * @param level The level to get minimum experience for.
     * @return The least amount of experience needed to achieve said level.
     */
    public static int calculateExperienceForLevel(int level) {
        return SkillConstants.EXP_ARRAY[Math.min(level - 1, 199)];
    }

    /**
     * Gets the level from said experience.
     *
     * @param experience The experience to get level for.
     * @return The level you obtain when you have specified experience.
     */
    public static int calculateLevelForExperience(int experience) {
        //if (experience <= SkillConstants.EXPERIENCE_FOR_200) {
            for (int level = 0; level <= 199; level++) {
                if (experience < SkillConstants.EXP_ARRAY[level]) {
                    return level;
                }
            }
       // }
        return 200;
    }

    public static int applyExperienceGameModeModifiers(GameMode gameMode, int experienceAmount) { // Used in tomes, xp lamps and other stuff to reduce the fixed experience gain.
        if (gameMode.isClassic()) {
            return (int) (experienceAmount * 0.40); // 60% reduction
        } else if (gameMode.isAnyIronman()) {
            return (int) (experienceAmount * 0.60); // 40% reduction
        } else if (gameMode.isRealism()) {
            return (int) (experienceAmount * 0.10); // 90% reduction
        } else if (gameMode.isSpawn()) {
            return 1; // just in case
        }
        // Just in case
//        if (experienceAmount <= 1)
//            experienceAmount = 1;

        return experienceAmount;
    }

    public static double getXPForGameMode(Skill skill, GameMode gameMode, double experienceAmount) {
        if (isCombatSkill(skill)) {
            // Combat skills
            if (gameMode.isNormal() || gameMode.isOneLife() || gameMode.isSpawn()) {
                experienceAmount *= GameConstants.REGULAR_COMBAT_EXP_MULTIPLIER;
            } else if (gameMode.isRealism()) {
                experienceAmount *= GameConstants.REALISM_COMBAT_EXP_MULTIPLIER;
            } else if (gameMode.isClassic()) {
                experienceAmount *= GameConstants.CLASSIC_COMBAT_EXP_MULTIPLIER;
            } else if (gameMode.isPure() || gameMode.isMaster()) {
                experienceAmount *= GameConstants.PURE_MASTER_COMBAT_EXP_MULTIPLIER;
            } else if (gameMode.isAnyIronman()) {
                experienceAmount *= GameConstants.IRONMAN_COMBAT_EXP_MULTIPLIER;
            } else { // Safety
                experienceAmount *= GameConstants.REGULAR_COMBAT_EXP_MULTIPLIER;
            }
//            if (experienceAmount <= 1)
//                experienceAmount = 1;
            return experienceAmount;
        } else {
            // Non-combat skills
            if (gameMode.isNormal() || gameMode.isOneLife() || gameMode.isSpawn()) {
                experienceAmount *= GameConstants.REGULAR_SKILLS_EXP_MULTIPLIER;
            } else if (gameMode.isRealism()) {
                experienceAmount *= GameConstants.REALISM_SKILLS_EXP_MULTIPLIER;
            } else if (gameMode.isClassic()) {
                experienceAmount *= GameConstants.CLASSIC_SKILLS_EXP_MULTIPLIER;
            } else if (gameMode.isPure() || gameMode.isMaster()) {
                experienceAmount *= GameConstants.PURE_MASTER_SKILLS_EXP_MULTIPLIER;
            } else if (gameMode.isAnyIronman()) {
                experienceAmount *= GameConstants.IRONMAN_SKILLS_EXP_MULTIPLIER;
            } else { // Safety
                experienceAmount *= GameConstants.REGULAR_SKILLS_EXP_MULTIPLIER;
            }

        }
        // Just in case
//        if (experienceAmount <= 1)
//            experienceAmount = 1;
        return experienceAmount;
    }

    public static double getXPForGameMode_Post_120(Skill skill, GameMode gameMode, double experienceAmount) {

            if (gameMode.isNormal() || gameMode.isOneLife() || gameMode.isSpawn()) {
                experienceAmount *= GameConstants.REGULAR_POST_120_EXP_MULTIPLIER;
            } else if (gameMode.isRealism()) {
                experienceAmount *= GameConstants.POST_120_REALISM_EXP_MULTIPLIER;
            } else if (gameMode.isClassic()) {
                experienceAmount *= GameConstants.POST_99_CLASSIC_EXP_MULTIPLIER;
            } else if (gameMode.isPure() || gameMode.isMaster()) {
                experienceAmount *= GameConstants.POST_99_PURE_MASTER_EXP_MULTIPLIER;
            } else if (gameMode.isAnyIronman()) {
                experienceAmount *= GameConstants.POST_99_IRONMAN_EXP_MULTIPLIER;
            } else { // Safety
                experienceAmount *= GameConstants.REGULAR_POST_120_EXP_MULTIPLIER;
            }
        // Just in case
/*        if (experienceAmount <= 1)
            experienceAmount = 1;*/
        return experienceAmount;
    }

    static int applyExperienceCustomaryMultipliers(Player player, Skill skill, int experienceAmount) {

        final SkillManager skillManager = player.getSkillManager();
        final int maxLevel = skillManager.getMaxLevel(skill);


        if (isCombatSkill(skill)) {
            // XP changes after level 120
//            if (maxLevel >= 120) {
//                experienceAmount = (int) getXPForGameMode_Post_120(skill, player.getGameMode(), experienceAmount);
//            } else {
                // Game mode XP modifier (Combat skills)
            experienceAmount = (int) getXPForGameMode(skill, player.getGameMode(), experienceAmount);
            //}
            // 25% bonus experience for weekends
            if (GameConstants.BONUS_DOUBLE_EXP_WEEKEND)
                experienceAmount *= 1.25;

            if (player.getMinigame() != null || player.inPestControl() || player.inCastleWars()) {
                experienceAmount *= 3;
            }

            if (player.getWildernessLevel() > 0) {
                experienceAmount *= 2;
            }

            // Double experience for combat skills
            if(player.getEquipment().contains(ItemID.RING_OF_CHAROS))
                experienceAmount *= 2;

            // Skill capes bonus experience
            experienceAmount *= SkillExperienceBonus.getSkillBonus(player, skill);

            // Voting bonus xp
            if (!player.getVotingBonusTimer().finished()) // 25% bonus experience from voting
                experienceAmount *= GameConstants.VOTING_BONUS_XP_MULTIPLIER;

            // Member ranks benefits
            experienceAmount *= SkillExperienceBonus.getMembersRankBonusExperience(player);

        } else { // Non combat skills

            // XP changes after level 120
//            if (maxLevel >= 120) {
//                experienceAmount = (int) getXPForGameMode_Post_120(skill, player.getGameMode(), experienceAmount);
//            } else {
                // Game mode XP modifier (Non combat skills)
            experienceAmount = (int) getXPForGameMode(skill, player.getGameMode(), experienceAmount);
            //}

            // Skilling equipment bonus experience
            experienceAmount *= SkillExperienceBonus.getSkillBonus(player, skill);

            // Hourly skill bonus
            if (GameConstants.BONUS_SKILL_ENABLED) {
                if (skill.equals(Skill.getBonusSkill()))
                    experienceAmount *= GameConstants.BONUS_SKILL_EXP_MULTIPLIER;
            }

            // Double experience gain when training in the wilderness
            if (player.getWildernessLevel() > 0) {
                experienceAmount *= 2;
            }
            // Voting bonus xp
            if (!player.getVotingBonusTimer().finished()) // 25% bonus experience from voting
                experienceAmount *= GameConstants.VOTING_BONUS_XP_MULTIPLIER;

            // 25% Boosted XP in resource area wilderness
            if (/*player.getArea() != null && */!skill.equals(Skill.PRAYER)) {
                //if (player.getArea() instanceof ResourceArea) {
                if (player.getPosition().inside(3174, 3924, 3196, 3944)) {
                    experienceAmount *= 1.25; // 25% bonus experience in the resource area
                }
            }

            // Member ranks benefits
            experienceAmount *= SkillExperienceBonus.getMembersRankBonusExperience(player);

            // Skilling points system
            SkillingPoints.addExperience(player, skill, experienceAmount);

        }
        return experienceAmount;
    }
    /**
     * @return the maximum achievable level in a {@link Skill}
     */
    public static int maximumAchievableLevel() {
        return 99;
    }

    /**
     * Sets the effective and max level of a {@link Skill}.
     *
     * @param player the {@link Player} having the {@link Skill}.
     * @param skill the {@link Skill} to set the level of.
     * @param level the level to set.
     */
    public static void setLevel(Player player, Skill skill, int level) {

        final boolean developerRights = player.getRights().anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER);

        if (!developerRights) {

            if (AreaManager.inWilderness(player)) {
                player.sendMessage("You can't do this in the Wilderness!");
                return;
            }
            if (player.busy()) {
                player.sendMessage("You can't do this right now.");
                return;
            }
            if (!player.getEquipment().isEmpty()) {
                player.sendMessage("Please un-equip your items before doing that.");
                return;
            }
            if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
                player.sendMessage("You can't do that while in combat.");
                return;
            }
            if (skill == Skill.HITPOINTS) {
                if (level < 10) {
                    player.sendMessage("Hitpoints must be set to at least level 10.");
                    return;
                }
            }
        }
        if (!skill.canSetLevel()) {
            if (!developerRights) {
                player.sendMessage("You can only edit your combat stats excluding prayer!");
                return;
            }
        }

        final PacketSender packetSender = player.getPacketSender();
        final SkillManager skillManager = player.getSkillManager();
        skillManager.setCurrentLevel(skill, level, false);
        skillManager.setMaxLevel(skill, level, false);
        if (player.getGameMode().isSpawn()) {
            skillManager.setExperience(skill, calculateExperienceForLevel(level));
        } else {
        skillManager.setExperienceIfMoreThanCurrent(skill, calculateExperienceForLevel(level));
        }
        skillManager.updateSkill(skill);

        if (skill == Skill.PRAYER) {
            packetSender.sendConfig(709, PrayerHandler.canUse(player, PrayerHandler.PrayerType.PRESERVE, false) ? 1 : 0);
            packetSender.sendConfig(711, PrayerHandler.canUse(player, PrayerHandler.PrayerType.RIGOUR, false) ? 1 : 0);
            packetSender.sendConfig(713, PrayerHandler.canUse(player, PrayerHandler.PrayerType.AUGURY, false) ? 1 : 0);
        }

        player.getVengeanceEffect().stop();
        EquipmentBonuses.update(player);
        WeaponInterfaces.INSTANCE.assign(player);
        //PrayerHandler.deactivatePrayers(player);
        BountyHunterManager.disassemblePairIfPresent(player);
        player.updateAppearance();
    }

    static void processLevelUpAchievementsAndRewards(Player player, int newLevel) {


/*        if (!player.getGameMode().isNormal()) {
            return;
        }*/
        if (player.getGameMode().isMaster() || player.getGameMode().isPure()
                || player.getGameMode().isSpawn()
                || player.getGameMode().isAnyIronman()) {
            return;
        }
        if (newLevel == 25)
            addCashReward(player, 250_000, SkillConstants.CASH_REWARD_FOR_LEVELING_UP_REASON);
        else if (newLevel == 50)
            addCashReward(player, 500_000, SkillConstants.CASH_REWARD_FOR_LEVELING_UP_REASON);
        else if (newLevel == 75)
            addCashReward(player, 750_000, SkillConstants.CASH_REWARD_FOR_LEVELING_UP_REASON);
        else if (newLevel == 99) {
            addCashReward(player, 1_000_000, "as a reward for reaching level 99!");

            final Item bloodMoneyReward = new Item(ItemID.BLOOD_MONEY, 15_000);
            final Inventory inventory = player.getInventory();

            player.sendMessage("<img=753> @red@You have received 15,000 blood money as a reward for reaching level 99!");
            player.getPacketSender().sendJinglebitMusic(6, 0);
            if (!player.getGameMode().isUltimate()) {
                if (inventory.canHold(bloodMoneyReward)) {
                    inventory.add(bloodMoneyReward);
                } else {
                    player.sendMessage("Your reward has been sent to the bank.");
                    BankUtil.addToBank(player, bloodMoneyReward);
                }
            } else {
                ItemContainerUtil.addOrDrop(player.getInventory(), player, bloodMoneyReward);
            }
            if (player.getSkillManager().countSkillsMastered() >= 22) {
                player.sendMessage("@red@You can claim your max cape by speaking with Leon d'Cour.");
            }
        }

        // Trigger random to prevent botters making 100 accounts and training at the same time.
        int randomEvent = Misc.random(5);
        if (randomEvent == 1) {
            FrogPrinceEvent.INSTANCE.trigger(player);
        } else if (randomEvent == 2) {
            PlayerExtKt.tryRandomEventTrigger(player, 75F);
        }

    }

    static void processExperienceGainAchievementsAndRewards(Player player, Skill skill, int preAmount, int postAmount) {

        if (player.getGameMode().isMaster() || player.getGameMode().isPure()
        || player.getGameMode().isSpawn()) {
            return;
        }
        final String playerName = player.getUsername();
        final String formattedSkillName = Misc.formatName(skill.name().toLowerCase());
        final String preMessage = "[<img=753> ]<shad=15536940> "+PlayerUtil.getImages(player)+""+playerName+" has just achieved ";
        final String postMessage = " million experience in "+formattedSkillName+"!";

        if (preAmount < SkillConstants.MAX_EXPERIENCE && postAmount >= SkillConstants.MAX_EXPERIENCE) {

            PlayerUtil.broadcastMessage(preMessage + 2000 + postMessage);
            addCashReward(player, 2_000_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_750_000_000 && postAmount >= 1_750_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1750 + postMessage);
            addCashReward(player, 1_750_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_500_000_000 && postAmount >= 1_500_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1500 + postMessage);
            addCashReward(player, 1_500_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_250_000_000 && postAmount >= 1_250_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1250 + postMessage);
            addCashReward(player, 1_250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_200_000_000 && postAmount >= 1_200_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1200 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_100_000_000 && postAmount >= 1_100_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1100 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 1_000_000_000 && postAmount >= 1_000_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 1000 + postMessage);
            addCashReward(player, 1_000_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 950_000_000 && postAmount >= 950_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 950 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 900_000_000 && postAmount >= 900_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 900 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 850_000_000 && postAmount >= 850_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 850 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 800_000_000 && postAmount >= 800_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 800 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 750_000_000 && postAmount >= 750_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 750 + postMessage);
            addCashReward(player, 750_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 700_000_000 && postAmount >= 700_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 700 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 650_000_000 && postAmount >= 650_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 650 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 600_000_000 && postAmount >= 600_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 600 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 550_000_000 && postAmount >= 550_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 550 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 500_000_000 && postAmount >= 500_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 500 + postMessage);
            addCashReward(player, 500_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 450_000_000 && postAmount >= 450_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 450 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 400_000_000 && postAmount >= 400_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 400 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 350_000_000 && postAmount >= 350_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 350 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 300_000_000 && postAmount >= 300_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 300 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 250_000_000 && postAmount >= 250_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 250 + postMessage);
            addCashReward(player, 250_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 200_000_000 && postAmount >= 200_000_000) {
            PlayerUtil.broadcastMessage(preMessage + 200 + postMessage);
            addCashReward(player, 200_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 150_000_000 && postAmount >= 150_000_000) {

            PlayerUtil.broadcastMessage(preMessage + 150 + postMessage);
            addCashReward(player, 150_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);
        } else if (preAmount < 100_000_000 && postAmount >= 100_000_000) {

            process100MillAchievements(player, skill);
            PlayerUtil.broadcastMessage(preMessage + 100 + postMessage);
            addCashReward(player, 100_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);
            player.getPacketSender().sendJinglebitMusic(94, 0);

        } else if (preAmount < 50_000_000 && postAmount >= 50_000_000) {

            process200MillAchievements(player, skill);
            PlayerUtil.broadcastMessage(preMessage + 50 + postMessage);
            addCashReward(player, 50_000_000, SkillConstants.EXPERIENCE_GAIN_CASH_BONUS_REASON);

            player.getPacketSender().sendJinglebitMusic(94, 0);
        }
    }

    private static void addCashReward(Player player, int amount, String reason) {

        final Item cashReward = new Item(ItemID.COINS, amount);
        final Inventory inventory = player.getInventory();
        final String formattedAmount = Misc.formatWithAbbreviation(cashReward.getAmount());

        player.sendMessage("<img=753> You have received "+formattedAmount+" coins "+reason);

        // Handle separately while in a minigame
        if (player.getMinigame() != null && !player.getGameMode().isUltimate()) {
            player.sendMessage("Your reward has been sent to the bank.");
            BankUtil.addToBank(player, cashReward);
            return;
        }


        if (!player.getGameMode().isUltimate()) {
            if (inventory.canHold(cashReward)) {
                inventory.add(cashReward);
            } else {
                player.sendMessage("Your reward has been sent to the bank.");
                BankUtil.addToBank(player, cashReward);
            }
        } else {
            ItemContainerUtil.addOrDrop(player.getInventory(), player, cashReward);
        }
    }

    private static void process100MillAchievements(Player player, Skill skill) {
        switch (skill) {
            case ATTACK: AchievementManager.processFor(AchievementType.MASTER_PRECISION, player);break;
            case STRENGTH: AchievementManager.processFor(AchievementType.MASTER_OF_WAR, player);break;
            case DEFENCE: AchievementManager.processFor(AchievementType.UNBREAKABLE_ARMOR, player);break;
            case RANGED: AchievementManager.processFor(AchievementType.MASTER_MARKSMAN, player);break;
            case MAGIC: AchievementManager.processFor(AchievementType.MAGIC_MASTER, player);break;
            case PRAYER: AchievementManager.processFor(AchievementType.ENCHANTED_SPIRIT, player);break;
            case HITPOINTS: AchievementManager.processFor(AchievementType.IMMORTALITY, player);break;
            case WOODCUTTING: AchievementManager.processFor(AchievementType.MASTER_CUTTER, player);break;
            case COOKING: AchievementManager.processFor(AchievementType.MASTER_CHEF, player);break;
            case FISHING: AchievementManager.processFor(AchievementType.MARIANA_TRENCH, player);break;
            case HERBLORE: AchievementManager.processFor(AchievementType.MASTER_HERBALISM, player);break;
            case SLAYER: AchievementManager.processFor(AchievementType.MASTER_DIPLOMACY, player);break;
            case AGILITY: AchievementManager.processFor(AchievementType.MASTER_NINJA, player);break;
            case CRAFTING: AchievementManager.processFor(AchievementType.MASTER_CRAFTSMANSHIP, player);break;
            case FLETCHING: AchievementManager.processFor(AchievementType.MASTER_FLETCHER, player);break;
            case MINING: AchievementManager.processFor(AchievementType.NEVER_GIVING_UP, player);break;
            case SMITHING: AchievementManager.processFor(AchievementType.HEAT_ROUTINE, player);break;
            case RUNECRAFTING: AchievementManager.processFor(AchievementType.MASTER_ARTIST, player);break;
            case FARMING: AchievementManager.processFor(AchievementType.FARM_MASTER, player);break;
            default: break;
        }
    }

    private static void process200MillAchievements(Player player, Skill skill) {
        switch (skill) {
            case ATTACK: AchievementManager.processFor(AchievementType.ELITE_PRECISION, player);break;
            case STRENGTH: AchievementManager.processFor(AchievementType.ELITE_WAR, player);break;
            case DEFENCE: AchievementManager.processFor(AchievementType.ELITE_ARMOR, player);break;
            case RANGED: AchievementManager.processFor(AchievementType.ELITE_MARKSMAN, player);break;
            case MAGIC: AchievementManager.processFor(AchievementType.ELITE_MAGE, player);break;
            case PRAYER: AchievementManager.processFor(AchievementType.ELITE_SPIRIT, player);break;
            case HITPOINTS: AchievementManager.processFor(AchievementType.ELITE_AGE, player);break;
            case WOODCUTTING: AchievementManager.processFor(AchievementType.ELITE_CUTTING, player);break;
            case COOKING: AchievementManager.processFor(AchievementType.ELITE_CHEF, player);break;
            case FISHING: AchievementManager.processFor(AchievementType.DEEP_FISHING, player);break;
            case HERBLORE: AchievementManager.processFor(AchievementType.ELITE_HERBALISM, player);break;
            case SLAYER: AchievementManager.processFor(AchievementType.ELITE_DIPLOMACY, player);break;
            case AGILITY: AchievementManager.processFor(AchievementType.ELITE_AGILIY, player);break;
            case CRAFTING: AchievementManager.processFor(AchievementType.ELITE_CRAFTSMANSHIP, player);break;
            case FLETCHING: AchievementManager.processFor(AchievementType.SHAFTS_ARTISAN, player);break;
            case MINING: AchievementManager.processFor(AchievementType.NOT_GIVING_UP, player);break;
            case SMITHING: AchievementManager.processFor(AchievementType.SURVIVING_THE_HEAT, player);break;
            case RUNECRAFTING: AchievementManager.processFor(AchievementType.ELITE_ARTIST, player);break;
            case FARMING: AchievementManager.processFor(AchievementType.EVER_GREEN_AGAIN, player);break;
            default: break;
        }
    }

    static void processAchievements(Player player, Skill skill, int newLevel) {

        final SkillManager skillManager = player.getSkillManager();
        final int totalLevel = skillManager.countTotalLevel();
        final long totalExperience = skillManager.countTotalExperience();

        if (totalLevel >= 2179)
            AchievementManager.processFor(AchievementType.END_OF_JOURNEY, player);
        else if (totalLevel >= 1500)
            AchievementManager.processFor(AchievementType.ULTIMATE_MAX, player);

        if (totalExperience >= 500_000_000)
            AchievementManager.processFor(AchievementType.GRINDERSCAPE_IDOL, player);

        if (newLevel == 99) {
            switch (skill) {
                case ATTACK: AchievementManager.processFor(AchievementType.PRECISE_DETAILS, player);break;
                case STRENGTH: AchievementManager.processFor(AchievementType.GOD_OF_WAR, player);break;
                case DEFENCE: AchievementManager.processFor(AchievementType.BRUTAL_ARMOR, player);break;
                case RANGED: AchievementManager.processFor(AchievementType.EXPERT_MARKSMAN, player);break;
                case MAGIC: AchievementManager.processFor(AchievementType.MAGIC_ARTIST, player);break;
                case PRAYER: AchievementManager.processFor(AchievementType.BLESSED_SPIRIT, player);break;
                case HITPOINTS: AchievementManager.processFor(AchievementType.SEVEN_LIVES, player);break;
                case WOODCUTTING: AchievementManager.processFor(AchievementType.MAKE_THE_CUT, player);break;
                case COOKING: AchievementManager.processFor(AchievementType.STAR_CHEF, player);break;
                case FISHING: AchievementManager.processFor(AchievementType.SURF_N_TURF, player);break;
                case HERBLORE: AchievementManager.processFor(AchievementType.HERBALISM, player);break;
                case SLAYER: AchievementManager.processFor(AchievementType.NIGHT_DIPLOMACY, player);break;
                case AGILITY: AchievementManager.processFor(AchievementType.AGILE_TASKS, player);break;
                case CRAFTING: AchievementManager.processFor(AchievementType.CRAFTSMANSHIP, player);break;
                case FLETCHING: AchievementManager.processFor(AchievementType.FEATHER_ARTISAN, player);break;
                case MINING: AchievementManager.processFor(AchievementType.NEVER_GIVE_UP, player);break;
                case SMITHING: AchievementManager.processFor(AchievementType.STAND_THE_HEAT, player);break;
                case RUNECRAFTING: AchievementManager.processFor(AchievementType.ESSENCE_ARTIST, player);break;
                case FARMING: AchievementManager.processFor(AchievementType.NATURE_PRESERVED, player);break;
                default: break;
            }
        }
    }

    public static boolean isCombatSkill(Skill skill) {
        return skill == Skill.ATTACK
                || skill == Skill.DEFENCE
                || skill == Skill.STRENGTH
                || skill == Skill.HITPOINTS
                || skill == Skill.RANGED
                || skill == Skill.MAGIC;
    }

    public static boolean startSkillable(final Player player, GameObject object, int action) {

        // Check woodcutting..
        Optional<TreeType> tree = TreeType.forObjectId(object.getId());

        if (tree.isPresent()) {
            startSkillable(player, new Woodcutting(object, tree.get()));
            return true;
        }

        // Check mining..
        Optional<RockType> rock = RockType.forObjectId(object.getId());
        if (rock.isPresent()) {
            if (action == 1) {
                startSkillable(player, new Mining(object, rock.get()));
            } else {
                player.BLOCK_ALL_BUT_TALKING = true;
                player.sendMessage("You examine the rock for ores...");
                player.getPacketSender().sendSound(Sounds.PROSPECT_ORE);
                TaskManager.submit(2, () -> {
                      if (!rock.get().equals(RockType.NO_ORES)) {
                          player.sendMessage("This rock contains " + rock.get().name().toLowerCase().replace("_", " ") + ".");
                      } else {
                          player.sendMessage("This rock contains no ores.");
                      }
                      player.BLOCK_ALL_BUT_TALKING = false;
                  });
            }
            return true;
        }

        return false;
    }

}
