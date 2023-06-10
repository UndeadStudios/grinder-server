package com.grinder.game.content.skill;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Manages skill related attributes for a {@link Player}.
 *
 * @author relex lawl
 * @author Professor Oak
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 2.0
 */
public final class SkillManager {

	private final Player player;
    private Skills skills;

	public static final int[] SKILL_LEVEL_UP_MUSIC_EFFECTS = { 30, 38, 65, 48,
			58, 56, 52, 34, 70, 44, 42, 39, 36, 64, 54, 46, 28, 68, 62, 63, 60,
			50, 32 };

	/*
	 * Messages that are sent to the player while training comabt skills
	 */
	private static final String[][] RANDOM_SKILL_TRAIN_MESSAGE = {
			{ "Info: You can train Strength in the Warriors guild 2nd Floor!" },
			{ "Info: Voting will reward you with 1 hour of 25% bonus experience!" },
			{ "Info: Leveling up your combat stats rewards you with cash." },
			{ "Info: It's best to train on a Slayer task for extra damage and experience." },
			{ "Info: Training with Dharok's gear while using protection prayers is effective." },
			{ "Info: Chinchompas, Cannon, and Guthan's gear are very effective in multi training." },
			{ "Info: Double XP on every weekend is very effective for free bonus experience." },
			{ "Info: Some whips have unique special attack effect such as the Dragon whip." },
			{ "Info: It's advisable to slay bosses for quick money making." },
			{ "Info: Skeleton hellhounds drop the best bones in-game." },
			{ "Info: Not sure where to get an item? Check the Item Drop Finder in Quest tab!" },
			{ "Info: Click on a skill to check what is the best equipment you can equip next." },
			{ "Info: You can Barrage multiple NPC's when training Magic for great experience." },
			{ "Info: Completing clue scrolls will reward you with unique untradeable items." },
			{ "Info: Demon Agony spell hits very high against demon type monsters." },
			{ "Info: Skeleton bash spell is effective against shades, zombies, and skeletons." },
			{ "Info: Equipping a double XP ring can be effective in some circumstances." },
			{ "Info: Boost your drop rates when having a ring of wealth equipped." },
	};

    /**
     * Create a new {@link SkillManager}.
     *
     * @param player the {@link Player} whose skills are managed.
     */
    public SkillManager(Player player) {
        this.player = player;
        this.skills = new Skills();
        for(Skill skill : Skill.values())
			setDefaults(skill, false);
    }

	public String getGameModeRankStringCrown(Player player) {
		if (player.getGameMode().isOneLife()) {
			return "<img=1235>";
		} else if (player.getGameMode().isRealism()) {
			return "<img=1236>";
		} else if (player.getGameMode().isClassic()) {
			return "<img=77>";
		} else if (player.getGameMode().isPure()) {
			return "<img=1238>";
		} else if (player.getGameMode().isMaster()) {
			return "<img=1237>";
		} else if (player.getGameMode().isSpawn()) {
			return "<img=1242>";
		} else if (player.getGameMode().isIronman()) {
			return "<img=807>";
		} else if (player.getGameMode().isHardcore()) {
			return "<img=78>";
		} else if (player.getGameMode().isUltimate()) {
			return "<img=808>";
		}
		return "";
	}

	/**
	 * Adds experience to {@code skill} by the {@code experience} amount.
	 *
	 * @param skill      The skill to add experience to.
	 * @param experience The amount of experience to add to the skill.
	 * @return The Skills instance.
	 */
	public SkillManager addExperience(Skill skill, int experience, boolean multipliers) {

		boolean slayerBossRewardMessage = false;

		// Exact number when completing a boss task
		if (skill == Skill.SLAYER && experience == 10_000) {
			slayerBossRewardMessage = true;
		}


		//experience = SkillUtil.applyExperienceGameModeModifiers(player.getGameMode(), experience);

		int originalExp = experience;
		if (multipliers) // Skill bonus multipliers
			experience = SkillUtil.applyExperienceCustomaryMultipliers(player, skill, experience);

		// Don't add the experience if it has been locked..
		if (SkillUtil.isCombatSkill(skill) && player.getAttributes().bool(Attribute.EXPERIENCED_LOCKED))
			return this;

		// If we already have max exp, don't add any more.
		if (skills.experience[skill.ordinal()] >= SkillConstants.MAX_EXPERIENCE) {
			skills.experience[skill.ordinal()] = SkillConstants.MAX_EXPERIENCE;
			return this;
		}

		boolean sendOriginalExp = SkillUtil.isCombatSkill(skill) && !player.getAttributes().bool(Attribute.MULTIPLY_XP_DROPS);
		player.getPacketSender().sendExpDrop(skill, sendOriginalExp ? originalExp : experience);

		if (player.getPosition().inside(3174, 3924, 3196, 3944))
			AchievementManager.processFor(AchievementType.THE_ROAD_TO_HELL, experience, player);

		if (skill == Skill.SLAYER && slayerBossRewardMessage) {

			PlayerUtil.broadcastMessage("[<img=91> ] " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has received " + Misc.format(experience) +" Slayer experience for completing a boss slayer task.");
			slayerBossRewardMessage = false;
		}

		final int startingLevel = skills.maxLevel[skill.ordinal()];
		final int preExperienceAmount = skills.experience[skill.ordinal()];
		final int postExperienceAmount = (int) Math.min(((long) preExperienceAmount + experience), SkillConstants.MAX_EXPERIENCE);

		skills.experience[skill.ordinal()] = postExperienceAmount;
		SkillUtil.processExperienceGainAchievementsAndRewards(player, skill, preExperienceAmount, postExperienceAmount);

		// Get the skill's new level after experience has been added..
		int newLevel = SkillUtil.calculateLevelForExperience(skills.experience[skill.ordinal()]);

/*		// Cap combat levels at 120
		if (Skill.ATTACK.equals(skill) || Skill.STRENGTH.equals(skill) || Skill.DEFENCE.equals(skill) || Skill.RANGED.equals(skill) || Skill.MAGIC.equals(skill)) {
			newLevel = Math.min(newLevel, 120);
		}*/

		// Handle level up..
		if (newLevel > startingLevel) {

			final int level = newLevel - startingLevel;
			final String skillName = Misc.formatText(skill.toString().toLowerCase());

			skills.maxLevel[skill.ordinal()] += level;

			if (skill != Skill.HITPOINTS) {
				if (skills.getLevels()[skill.ordinal()] < skills.maxLevel[skill.ordinal()])
					setCurrentLevel(skill, skills.maxLevel[skill.ordinal()], true);

			}
			// Reward participation points
			if (!player.getGameMode().isSpawn() && !player.getGameMode().isPure() && !player.getGameMode().isMaster()) {
				if (this.skills.maxLevel[skill.ordinal()] >= 92)
					ParticipationPoints.addPoints(player, 1, "@dre@from leveling</col>.");
			}
			if (player.getGameMode().isRealism()) {
				if (this.skills.maxLevel[skill.ordinal()] >= 70)
					ParticipationPoints.addPoints(player, 1, "@dre@from leveling</col>.");
			}

			if (EntityExtKt.passedTime(player, Attribute.LAST_INFO_BROADCAST, 60, TimeUnit.MILLISECONDS, false, true)) {
				if (SkillUtil.isCombatSkill(skill) && newLevel < SkillUtil.maximumAchievableLevel() && Misc.random(3) == Misc.random(3)) {
					// Send broadcast
					Broadcast.broadcastSingle(
							player,
							30,
							RANDOM_SKILL_TRAIN_MESSAGE[Misc.getRandomInclusive(RANDOM_SKILL_TRAIN_MESSAGE.length - 1)][0],
							""
					);
				}
			}

			//player.getPacketSender().sendInterfaceRemoval();
			//if (System.currentTimeMillis() - player.lastLevelUp >= 15000) {
				player.getPacketSender().sendString(4268,
						"Congratulations, you just advanced a " + skillName + " level!");
				player.getPacketSender().sendString(4269, "Your " + skillName +" level is now " + newLevel + ".");
				player.getPacketSender().sendString(358, "Click here to continue.");
				player.getPacketSender().sendChatboxInterface(skill.getChatboxInterface());
				player.sendMessage("Congratulations, you've just advanced your " + skillName + " level. You are now level " + newLevel + ".");
				//player.getPacketSender().sendMusic(MusicSongs.getLevelUpSong(skill.ordinal()), 4, 350);
			//}

			// Send graphic
			player.performGraphic(new Graphic(199, GraphicHeight.HIGH));


			if (SkillUtil.isCombatSkill(skill) && newLevel == 25 | newLevel == 50 || newLevel == 75 || newLevel == 99) {
				if (!player.getGameMode().isSpawn() && !player.getGameMode().isPure() && !player.getGameMode().isMaster()) {
					SkillUtil.processLevelUpAchievementsAndRewards(player, newLevel);
				}
				player.getPacketSender().sendJinglebitMusic(95, 0); // Unique cases for achievement
			} else {
				// LEVEL UP JINGLEBITS
				if (EntityExtKt.passedTime(player, Attribute.LAST_LEVEL_UP, 30_000, TimeUnit.MILLISECONDS, false, true)) {
					player.getPacketSender().sendJinglebitMusic(SKILL_LEVEL_UP_MUSIC_EFFECTS[skill.ordinal()], 0);
				}
			}

			// Process achievement rewards and messages
			SkillUtil.processAchievements(player, skill, newLevel);

			// Max level
			if (skills.maxLevel[skill.ordinal()] == 99) {
				if (!player.getGameMode().isSpawn() && !player.getGameMode().isPure() && !player.getGameMode().isMaster()) {
					player.sendMessage("Well done! You've achieved a level of " + newLevel + " in the " + skillName + " skill!");
					//player.sendMessage("Well done! You've achieved the highest possible level in the " + skillName + " skill!");
					PlayerUtil.broadcastMessage("[<img=753> ]<shad=15536940> " + PlayerUtil.getImages(player) + "" + player.getUsername() + ""
							+ " has just reached a level of " + newLevel + " in " + skillName + " !");
				}
			} else if (skills.maxLevel[skill.ordinal()] >= SkillUtil.maximumAchievableLevel()) {
				player.sendMessage("Well done! You've achieved a level of " + newLevel + " in the " + skillName + " skill!");
				//player.sendMessage("Well done! You've achieved the highest possible level in the " + skillName + " skill!");
				PlayerUtil.broadcastMessage("[<img=753> ]<shad=15536940> " + PlayerUtil.getImages(player) + "" + player.getUsername() + ""
						+ " has just reached a level of " + newLevel + " in " + skillName + " !");
			}
			player.updateAppearance();
		}
		updateSkill(skill);
		return this;
	}

	/**
	 * Adds fixed experience to {@code skill} by the {@code experience} amount.
	 * This is used for EXP Tomes / Brews / and other bonuses so it gives precise XP.
	 * @param skill      The skill to add experience to.
	 * @param experience The amount of experience to add to the skill.
	 * @return The Skills instance.
	 */
	public SkillManager addFixedExperience(Skill skill, int experience) {
		int originalExp = experience;
		experience = SkillUtil.applyExperienceGameModeModifiers(player.getGameMode(), experience);
		// Send exp drop..

		// If we already have max exp, don't add any more.
		if (skills.experience[skill.ordinal()] >= SkillConstants.MAX_EXPERIENCE) {
			skills.experience[skill.ordinal()] = SkillConstants.MAX_EXPERIENCE;
			return this;
		}

		boolean sendOriginalExp = SkillUtil.isCombatSkill(skill) && !player.getAttributes().bool(Attribute.MULTIPLY_XP_DROPS);
		player.getPacketSender().sendExpDrop(skill, sendOriginalExp ? originalExp : experience);

		final int startingLevel = skills.maxLevel[skill.ordinal()];
		final int preExperienceAmount = skills.experience[skill.ordinal()];
		final int postExperienceAmount = (int) Math.min(((long) preExperienceAmount + experience), SkillConstants.MAX_EXPERIENCE);

		skills.experience[skill.ordinal()] = postExperienceAmount;
		SkillUtil.processExperienceGainAchievementsAndRewards(player, skill, preExperienceAmount, postExperienceAmount);

		// Get the skill's new level after experience has been added..
		int newLevel = SkillUtil.calculateLevelForExperience(skills.experience[skill.ordinal()]);

/*		// Cap combat levels at 120
		if (Skill.ATTACK.equals(skill) || Skill.STRENGTH.equals(skill) || Skill.DEFENCE.equals(skill) || Skill.RANGED.equals(skill) || Skill.MAGIC.equals(skill)) {
			newLevel = Math.min(newLevel, 120);
		}*/

		// Handle level up..
		if (newLevel > startingLevel) {

			final int level = newLevel - startingLevel;
			final String skillName = Misc.formatText(skill.toString().toLowerCase());

			skills.maxLevel[skill.ordinal()] += level;

			if (skill != Skill.HITPOINTS) {
				if (skills.getLevels()[skill.ordinal()] < skills.maxLevel[skill.ordinal()])
					setCurrentLevel(skill, skills.maxLevel[skill.ordinal()], true);

			}
			// Reward participation points
			if (this.skills.maxLevel[skill.ordinal()] >= 92)
				ParticipationPoints.addPoints(player, 1, "@dre@from leveling</col>.");

			//player.getPacketSender().sendInterfaceRemoval();
			//if (System.currentTimeMillis() - player.lastLevelUp >= 15000) {

			player.getPacketSender().sendString(4268,
					"Congratulations, you just advanced a " + skillName + " level!");
			player.getPacketSender().sendString(4269, "Your " + skillName +" level is now " + newLevel + ".");
			player.getPacketSender().sendString(358, "Click here to continue.");
			player.getPacketSender().sendChatboxInterface(skill.getChatboxInterface());
			player.sendMessage("Congratulations, you've just advanced your " + skillName + " level. You are now level " + newLevel +".");
			//player.getPacketSender().sendMusic(MusicSongs.getLevelUpSong(skill.ordinal()), 4, 350);
			//}
			player.performGraphic(new Graphic(199));

			if (SkillUtil.isCombatSkill(skill) && newLevel == 25 | newLevel == 50 || newLevel == 75 || newLevel == 99) {
				if (!player.getGameMode().isSpawn() && !player.getGameMode().isPure() && !player.getGameMode().isMaster()) {
					SkillUtil.processLevelUpAchievementsAndRewards(player, newLevel);
				}
				player.getPacketSender().sendJinglebitMusic(95, 0); // Unique cases for achievement
			} else {
				// LEVEL UP JINGLEBITS
				if (EntityExtKt.passedTime(player, Attribute.LAST_LEVEL_UP, 30_000, TimeUnit.MILLISECONDS, false, true)) {
					player.getPacketSender().sendJinglebitMusic(SKILL_LEVEL_UP_MUSIC_EFFECTS[skill.ordinal()], 0);
				}
			}

			SkillUtil.processAchievements(player, skill, newLevel);

/*			if (skills.maxLevel[skill.ordinal()] == SkillUtil.maximumAchievableLevel()) {
				player.sendMessage("Well done! You've achieved the highest possible level in the " + skillName +" skill!");
				PlayerUtil.broadcastMessage("[<img=753> ]<shad=15536940> " + PlayerUtil.getImages(player) + "" + player.getUsername() +""
						+ " has just reached a level of 99 in " + skillName + " !");
			}*/

			if (skills.maxLevel[skill.ordinal()] >= SkillUtil.maximumAchievableLevel()) {
			player.sendMessage("Well done! You've achieved a level of " + newLevel + " in the " + skillName + " skill!");
			//player.sendMessage("Well done! You've achieved the highest possible level in the " + skillName + " skill!");
			PlayerUtil.broadcastMessage("[<img=753> ]<shad=15536940> " + PlayerUtil.getImages(player) + "" + player.getUsername() + ""
					+ " has just reached a level of " + newLevel + " in " + skillName + " !");
		}
			player.updateAppearance();
		}
		updateSkill(skill);
		return this;
	}

	/**
	 * Adds fixed experience to {@code skill} by the {@code experience} amount.
	 * This is used for Quests to give the experience after the interface is closed rather than instantly
	 * @param skill      The skill to add experience to.
	 * @param experience The amount of experience to add to the skill.
	 * @return The Skills instance.
	 */
	public SkillManager addFixedDelayedExperience(Skill skill, int experience) {
		int originalExp = experience;
		experience = SkillUtil.applyExperienceGameModeModifiers(player.getGameMode(), experience);
		// Send exp drop..

		// If we already have max exp, don't add any more.
		if (skills.experience[skill.ordinal()] >= SkillConstants.MAX_EXPERIENCE) {
			skills.experience[skill.ordinal()] = SkillConstants.MAX_EXPERIENCE;
			return this;
		}

		boolean sendOriginalExp = SkillUtil.isCombatSkill(skill) && !player.getAttributes().bool(Attribute.MULTIPLY_XP_DROPS);
		player.getPacketSender().sendExpDrop(skill, sendOriginalExp ? originalExp : experience);

		final int startingLevel = skills.maxLevel[skill.ordinal()];
		final int preExperienceAmount = skills.experience[skill.ordinal()];
		final int postExperienceAmount = (int) Math.min(((long) preExperienceAmount + experience), SkillConstants.MAX_EXPERIENCE);

		skills.experience[skill.ordinal()] = postExperienceAmount;
		SkillUtil.processExperienceGainAchievementsAndRewards(player, skill, preExperienceAmount, postExperienceAmount);

		// Get the skill's new level after experience has been added..
		int newLevel = SkillUtil.calculateLevelForExperience(skills.experience[skill.ordinal()]);

		// Handle level up..
		if (newLevel > startingLevel) {

			final int level = newLevel - startingLevel;
			final String skillName = Misc.formatText(skill.toString().toLowerCase());

			skills.maxLevel[skill.ordinal()] += level;

			if (skill != Skill.HITPOINTS) {
				if (skills.getLevels()[skill.ordinal()] < skills.maxLevel[skill.ordinal()])
					setCurrentLevel(skill, skills.maxLevel[skill.ordinal()], true);

			}
			// Reward participation points
			if (this.skills.maxLevel[skill.ordinal()] >= 92)
				ParticipationPoints.addPoints(player, 1, "@dre@from leveling</col>.");


			if (SkillUtil.isCombatSkill(skill) && newLevel == 25 | newLevel == 50 || newLevel == 75 || newLevel == 99) {
				if (!player.getGameMode().isSpawn() && !player.getGameMode().isPure() && !player.getGameMode().isMaster()) {
					SkillUtil.processLevelUpAchievementsAndRewards(player, newLevel);
				}
			}

			SkillUtil.processAchievements(player, skill, newLevel);
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					if (!EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_EXPERIENCE_DELAY, false)) {
						stop();
						player.getPacketSender().sendString(4268,
								"Congratulations, you just advanced a " + skillName + " level!");
						player.getPacketSender().sendString(4269, "Your " + skillName +" level is now " + newLevel + ".");
						player.getPacketSender().sendString(358, "Click here to continue.");
						player.getPacketSender().sendChatboxInterface(skill.getChatboxInterface());
						player.sendMessage("Congratulations, you've just advanced your " + skillName + " level. You are now level " + newLevel +".");
						//player.getPacketSender().sendMusic(MusicSongs.getLevelUpSong(skill.ordinal()), 4, 350);
						//}
						player.getPacketSender().sendJinglebitMusic(SKILL_LEVEL_UP_MUSIC_EFFECTS[skill.ordinal()], 0);
						player.performGraphic(new Graphic(199));

						if (skills.maxLevel[skill.ordinal()] >= SkillUtil.maximumAchievableLevel()) {
							player.sendMessage("Well done! You've achieved a level of " + newLevel + " in the " + skillName + " skill!");
							//player.sendMessage("Well done! You've achieved the highest possible level in the " + skillName + " skill!");
							PlayerUtil.broadcastMessage("[<img=753> ]<shad=15536940> " + PlayerUtil.getImages(player) + "" + player.getUsername() + ""
									+ " has just reached a level of " + newLevel + " in " + skillName + " !");
						}
						player.updateAppearance();
					}
				}
			});


		}
		updateSkill(skill);
		return this;
	}

	public SkillManager addExperience(Skill skill, double experience) {
		return addExperience(skill, (int) experience, true);
	}

	/**
	 * Add experience to the specified {@link Skill}.
	 *
	 * @param skill the {@link Skill} to add experience for.
	 * @param amount the amount of experience to add.
	 * @return this {@link SkillManager} instance.
	 */
    public SkillManager addExperience(Skill skill, int amount) {
        return addExperience(skill, amount, true);
    }

	/**
	 * Sets the effective level of the specified {@link Skill}.
	 *
	 * @param skill the {@link Skill} to set the effective level of.
	 * @param level the effective level to set.
	 * @param refresh {@code true} if the client should be notified.
	 * @return this {@link SkillManager} instance.
	 */
	public SkillManager setCurrentLevel(Skill skill, int level, boolean refresh) {
		this.skills.level[skill.ordinal()] = Math.max(level, 0);
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the max (actual) level of the specified {@link Skill}.
	 *
	 * @param skill the {@link Skill} to set the effective level of.
	 * @param level the max level to set.
	 * @param refresh {@code true} if the client should be notified.
	 * @return this {@link SkillManager} instance.
	 */
	public SkillManager setMaxLevel(Skill skill, int level, boolean refresh) {
		skills.maxLevel[skill.ordinal()] = level;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	public Skills getSkills() {
		return skills;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	/**
	 * Update the client-sided skill(level)-indicators.
	 *
	 * @param skill the {@link Skill} requiring an update.
	 */
	public void updateSkill(Skill skill) {

		final PacketSender packetSender = player.getPacketSender();
		final String levels = getCurrentLevel(skill) + "/" + getMaxLevel(skill);
		final String totalLevel = String.valueOf(countTotalLevel());
		final String combatLevel = "Combat level: " + calculateCombatLevel();

		if (skill == Skill.PRAYER)
			packetSender.sendString(SkillConstants.PRAYER_ORB_CHILD_ID, levels, true);

		packetSender.sendString(SkillConstants.TOTAL_LEVEL_CHILD_ID, totalLevel, true);
		packetSender.sendString(SkillConstants.COMBAT_LEVEL_CHILD_ID_1, combatLevel, true);
		packetSender.sendString(SkillConstants.COMBAT_LEVEL_CHILD_ID_2, combatLevel, true);
		packetSender.sendSkill(skill);
	}

    /**
	 * Set the effective level, max level and experience back
	 * to their default values for the specified {@link Skill}.
	 *
	 * @param skill the {@link Skill} to reset.
	 * @param update {@code true} if the client should be notified.
	 */
	public void setDefaults(Skill skill, boolean update) {
		if (skill.equals(Skill.HITPOINTS)) {
			setCurrentLevel(skill, 10, false);
			setMaxLevel(skill, 10, false);
			setExperience(skill, 1184, false);
		}else {
			setCurrentLevel(skill, 1, false);
			setMaxLevel(skill, 1, false);
			setExperience(skill, 0, false);
		}
		if(update)
			updateSkill(skill);
	}

	/**
	 * Set the experience of a {@link Skill}.
	 *
	 * @param skill the {@link Skill} to set the experience of.
	 * @param amount the amount of experience to set.
	 * @param update {@code true} if the client should be notified.
	 */
	private void setExperience(Skill skill, int amount, boolean update) {
		this.skills.experience[skill.ordinal()] = Math.max(amount, 0);
		if (update)
			updateSkill(skill);
	}

	/**
	 * Set the experience of a {@link Skill}.
	 *
	 * @param skill the {@link Skill} to set the experience of.
	 * @param amount the amount of experience to set.
	 */
	public void setExperienceIfMoreThanCurrent(Skill skill, int amount) {
		if (getExperience(skill) > amount)
			return;
		setExperience(skill, amount, true);
	}
	public void setExperience(Skill skill, int amount) {
		setExperience(skill, amount, true);
	}

	/**
	 * Increase the effective level of this skill by the specified amount.
	 *
	 * @param skill the {@link Skill} of which to increase the level.
	 * @param amount the amount to increase the skill level by.
	 * @param upperLevelLimit the maximum level of the skill.
	 */
	public void increaseLevelTemporarily(Skill skill, int amount, int upperLevelLimit) {
		final int temporaryLevel = getCurrentLevel(skill) + amount;
		setCurrentLevel(skill, Math.min(temporaryLevel, upperLevelLimit), true);
	}

	/**
	 * Increase the effective level of this skill by the specified amount.
	 *
	 * @param skill the {@link Skill} of which to increase the level.
	 * @param amount the amount to increase the skill level by.
	 */
	public void increaseLevelTemporarily(Skill skill, int amount) {
		increaseLevelTemporarily(skill, amount, getMaxLevel(skill) + amount);
	}

	/**
	 * Decrements this level by {@code amount} to a minimum of
	 * {@code realLevel - amount}.
	 *
	 * @param amount the amount to decrease this level by.
	 */
	public void decreaseLevelTemporarily(Skill skill, int amount) {
		decreaseLevelTemporarily(skill, amount, getMaxLevel(skill) - amount);
	}

	/**
	 * Decrease the effective level of this skill by the specified amount.
	 *
	 * @param skill the {@link Skill} of which to decrease the level.
	 * @param amount the amount to decrease the skill level by.
	 * @param lowerLevelLimit the minimum level of the skill.
	 */
	public void decreaseLevelTemporarily(Skill skill, int amount, int lowerLevelLimit) {
		final int temporaryLevel = getCurrentLevel(skill) - amount;
		setCurrentLevel(skill,  Math.max(temporaryLevel, lowerLevelLimit), true);
	}

	/**
	 * Gets the current level for said skill.
	 *
	 * @param skill The skill to get current/temporary level for.
	 * @return The skill's level.
	 */
	public int getCurrentLevel(Skill skill) {
		return skills.level[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 *
	 * @param skill The skill to get max level for.
	 * @return The skill's maximum level.
	 */
	public int getMaxLevel(Skill skill) {
		return skills.maxLevel[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 *
	 * @param skill The skill to get max level for.
	 * @return The skill's maximum level.
	 */
	public int getMaxLevel(int skill) {
		return skills.maxLevel[skill];
	}

	/**
	 * Gets the experience for said skill.
	 *
	 * @param skill The skill to get experience for.
	 * @return The experience in said skill.
	 */
	public int getExperience(int skill) {
		return skills.experience[skill];
	}

	/**
	 * Gets the experience for said skill.
	 *
	 * @param skill The skill to get experience for.
	 * @return The experience in said skill.
	 */
	public int getExperience(Skill skill) {
		return skills.experience[skill.ordinal()];
	}

	/**
	 * Calculates the player's combat level.
	 *
	 * @return The average of the player's combat skills.
	 */
	public int calculateCombatLevel() {
		final int attack = getMaxLevel(Skill.ATTACK);
		final int defence = getMaxLevel(Skill.DEFENCE);
		final int strength = getMaxLevel(Skill.STRENGTH);
		final int hp = getMaxLevel(Skill.HITPOINTS);
		final int prayer = getMaxLevel(Skill.PRAYER);
		final int ranged = getMaxLevel(Skill.RANGED);
		final int magic =  getMaxLevel(Skill.MAGIC);

		int combatLevel = (int) ((defence + hp + Math.floor(prayer / 2.0)) * 0.2535) + 1;

		final double warriorFactor = (attack + strength) * 0.325;
		final double archeryFactor = Math.floor(ranged * 1.5) * 0.325;
		final double magicianFactor = Math.floor(magic * 1.5) * 0.325;

		if (warriorFactor >= archeryFactor && warriorFactor >= magicianFactor)
			combatLevel += warriorFactor;
		else if (archeryFactor >= warriorFactor && archeryFactor >= magicianFactor)
			combatLevel += archeryFactor;
		else if (magicianFactor >= warriorFactor && magicianFactor >= archeryFactor)
			combatLevel += magicianFactor;

		combatLevel = Math.min(combatLevel, SkillConstants.MAXIMUM_COMBAT_LEVEL);
		combatLevel = Math.max(combatLevel, SkillConstants.MINIMUM_COMBAT_LEVEL);

		return combatLevel;
	}

	public int calculateCombatLevelCapped_126() {
		final int attack = getMaxLevel(Skill.ATTACK);
		final int defence = getMaxLevel(Skill.DEFENCE);
		final int strength = getMaxLevel(Skill.STRENGTH);
		final int hp = getMaxLevel(Skill.HITPOINTS);
		final int prayer = getMaxLevel(Skill.PRAYER);
		final int ranged = getMaxLevel(Skill.RANGED);
		final int magic =  getMaxLevel(Skill.MAGIC);

		int combatLevel = (int) ((defence + hp + Math.floor(prayer / 2.0)) * 0.2535) + 1;

		final double warriorFactor = (attack + strength) * 0.325;
		final double archeryFactor = Math.floor(ranged * 1.5) * 0.325;
		final double magicianFactor = Math.floor(magic * 1.5) * 0.325;

		if (warriorFactor >= archeryFactor && warriorFactor >= magicianFactor)
			combatLevel += warriorFactor;
		else if (archeryFactor >= warriorFactor && archeryFactor >= magicianFactor)
			combatLevel += archeryFactor;
		else if (magicianFactor >= warriorFactor && magicianFactor >= archeryFactor)
			combatLevel += magicianFactor;

		combatLevel = Math.min(combatLevel, 126);
		combatLevel = Math.max(combatLevel, SkillConstants.MINIMUM_COMBAT_LEVEL);

		return combatLevel;
	}

	/**
	 * Count the number of skills with an experience equals to or less than the threshold.
	 *
	 * @param thresholdAmount the minimum amount of experience required to be taken into account.
	 * @return the amount of skills with an experience equals to or less than the threshold.
	 */
	public int countSkillsWithExperienceAbove(int thresholdAmount) {
		return (int) Arrays.stream(Skill.values()).filter(skill -> getExperience(skill) >= thresholdAmount).count();
	}

	/**
	 * Count the number of skills mastered by the {@link #player}.
	 *
	 * @return the amount of skills with a max level >= {@link SkillConstants#MAX_LEVEL}.
	 */
	public int countSkillsMastered() {
		return (int) Arrays.stream(Skill.values()).filter(skill -> getMaxLevel(skill) >= SkillConstants.MAX_LEVEL).count();
	}

	/**
	 * Count the cumulative level of all skills.
	 *
	 * @return the sum of all skill levels.
	 */
	public int countTotalLevel() {
		return Arrays.stream(Skill.values()).mapToInt(this::getMaxLevel).sum();
	}

	/**
	 * Count the cumulative experience of all skills.
	 *
	 * @return the sum of all skill experience.
	 */
	public long countTotalExperience() {
		return Arrays.stream(Skill.values()).mapToLong(this::getExperience).sum();
	}

}