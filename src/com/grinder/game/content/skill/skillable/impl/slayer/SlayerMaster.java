package com.grinder.game.content.skill.skillable.impl.slayer;

import java.util.ArrayList;
import java.util.List;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.util.Misc;

/**
 * Handles the slayer masters
 * 
 * @author 2012
 *
 */
public enum SlayerMaster {

	TURAEL("Turael", 401, 1, 1, 1, 2, 5, 15, 0), // Taverly
	MAZCHNA("Mazchna", 402, 2, 20, 20, 5, 10, 20, 0), // Canifis
	VANNAKA("Vannaka", 403, 3, 40, 40, 10, 20, 50, 0), // Edgeville dungeon
	CHAELDAR("Chaeldar", 404, 4, 70, 60, 20, 50, 125, 0), // Zanaris
	NIEVE("Nieve", 6797, 5, 85, 1, 15, 75, 225, 0), // 	Tree Gnome Stronghold
	KONAR_QUO_MATEN("Konar quo Maten", 8623, 5, 75, 1, 20, 100, 300, 0), // 	Tree Gnome Stronghold
	DURADEL("Duradel", 405, 6, 100, 70, 25, 75, 185, 0), // Shilo Village
	KRYSTILIA("Krystilia", 7663, 7, 50, 1, 25, 125, 375, 0), // Edgeville
	;

	/**
	 * The master name
	 */
	private final String masterName;

	/**
	 * The master id
	 */
	private final int masterID;

	/**
	 * The difficulty
	 */
	public final int difficulty;

	/**
	 * The required combat
	 */
	private final int requiredCombat;

	/**
	 * The required slayer level
	 */
	private final int requiredSlayer;

	/**
	 * The reward points
	 */
	private final int rewardPoints;

	/**
	 * Ten task bonus
	 */
	private final int tenTaskBonus;

	/**
	 * Fifty task bonus
	 */
	private final int fiftyTaskBonus;

	/**
	 * The required member rank
	 */
	private final int requiredMemberRank;

	/**
	 * The slayer tasks
	 */
	private ArrayList<SlayerMasterTask> tasks;

	/**
	 * Represents a slayer master
	 * 
	 * @param masterName
	 *            the name
	 * @param masterID
	 *            the id
	 * @param difficulty
	 *            the difficulty
	 * @param requiredCombat
	 *            the required combat
	 * @param requiredSlayer
	 *            the required slayer level
	 * @param rewardPoints
	 *            the reward points
	 * @param tenTaskBonus
	 *            ten task bonus
	 * @param fiftyTaskBonus
	 *            fifty task bonus
	 * @param requiredMemberRank
	 *            required member rank
	 */
	private SlayerMaster(String masterName, int masterID, int difficulty, int requiredCombat, int requiredSlayer,
			int rewardPoints, int tenTaskBonus, int fiftyTaskBonus, int requiredMemberRank) {
		this.masterName = masterName;
		this.masterID = masterID;
		this.difficulty = difficulty;
		this.requiredCombat = requiredCombat;
		this.requiredSlayer = requiredSlayer;
		this.rewardPoints = rewardPoints;
		this.tenTaskBonus = tenTaskBonus;
		this.fiftyTaskBonus = fiftyTaskBonus;
		this.requiredMemberRank = requiredMemberRank;
		tasks = new ArrayList<SlayerMasterTask>();
	}

	/**
	 * Gets the master by id
	 * 
	 * @param masterID
	 *            the id
	 * @return the master
	 */
	public static SlayerMaster forMasterID(int masterID) {
		for (SlayerMaster master : values()) {
			if (master.getMasterID() == masterID) {
				return master;
			}
		}
		return null;
	}

	/**
	 * Adds a task to master
	 * 
	 * @param task
	 *            the task
	 */
	public void addTask(SlayerMasterTask task) {
		if (task != null) {
			if (!tasks.contains(task))
				tasks.add(task);
		}
	}

	private static boolean unlocked(Player player, SlayerRewards.Rewards rewards){
		return player.getSlayer().getUnlocked()[rewards.getRewardIndex()];
	}

	public static boolean canGetTaskOf(Player player, SlayerMonsterType slayerMonsterType) {

		if (slayerMonsterType.getRequiredSlayerLevel() > PlayerExtKt.getLevel(player, Skill.SLAYER))
			return false;

		final SlayerManager slayerManager = player.getSlayer();

		final List<String> blockedMonsters = slayerManager.getBlockedMonsters();
		if (blockedMonsters != null && blockedMonsters.contains(slayerMonsterType.getName()))
			return false;

		final SlayerMasterTask lastCancelledTask = slayerManager.getLastCancelledTask();
		if (lastCancelledTask != null && lastCancelledTask.monsterType.getName().equals(slayerMonsterType.getName())) {
			DialogueManager.sendStatement(player,"cannot select "+slayerMonsterType.getName()+"");
			return false;
		}

		if (slayerMonsterType == SlayerMonsterType.MITHRIL_DRAGON)
			return unlocked(player, SlayerRewards.Rewards.I_HOPE_YOU_MITH_ME);
		if (slayerMonsterType == SlayerMonsterType.RED_DRAGON)
			return unlocked(player, SlayerRewards.Rewards.SEEING_RED);
		if (slayerMonsterType == SlayerMonsterType.TZHAAR || slayerMonsterType == SlayerMonsterType.TZTOK_JAD)
			return unlocked(player, SlayerRewards.Rewards.HOT_STUFF);
		if (slayerMonsterType.isInWilderness() && slayerMonsterType.isBoss()) // Only players with combat above 95 will get boss tasks in wild from Krystilia
			return player.getSkillManager().calculateCombatLevel() >= 95;
		if (!slayerMonsterType.isInWilderness() && slayerMonsterType.isBoss()) // Nieve/Duradel bosses that are not in the wilderness require a reward unlocking
			return unlocked(player, SlayerRewards.Rewards.LIKE_A_BOSS);
		return true;
	}
	/**
	 * Gets an assignment from master
	 * 
	 * @param player
	 *            the player
	 * @return the assignment
	 */
	public SlayerTask getTask(Player player) {
		/*
		 * Load tasks
		 */
		if (tasks.size() <= 0) {
			SlayerMasterTask.initializeTasks();
		}
		/*
		 * No tasks
		 */
		if (tasks.size() <= 0) {
			return null;
		}
		/*
		 * The slayer task
		 */
		SlayerMasterTask selectedTask = null;
		/*
		 * The total weight
		 */
		int totalWeight = 0;
		/*
		 * Loops through the tasks
		 */
		for (SlayerMasterTask task : tasks) {
			// tasks that need to be unlocked.
			if (!canGetTaskOf(player, task.monsterType))
				continue;
			/*
			 * The weight
			 */
			int weight = task.getWeight();
			/*
			 * The random
			 */
			int random = Misc.getRandomInclusive(totalWeight + weight);
			/*
			 * Random chance
			 */
			if (random >= totalWeight || selectedTask == null) {
				selectedTask = task;
			}
			/*
			 * Total weight
			 */
			totalWeight += weight;

		}
		/*
		 * Required kill
		 */
		if (selectedTask == null)
			return null;
		int requiredKills = selectedTask.minAmount + Misc.getRandomInclusive(selectedTask.maxAmount - selectedTask.minAmount);

		if (PlayerUtil.isAmethystMember(player) || PlayerUtil.isLegendaryMember(player) || PlayerUtil.isPlatinumMember(player)) {
			requiredKills *= 0.75;
		} else if (PlayerUtil.isTopazMember(player)) {
			requiredKills *= 0.80;
		} else if (PlayerUtil.isMember(player)) {
			requiredKills *= 0.90;
		}
		return new SlayerTask(selectedTask.monsterType.getName(),
				this, selectedTask.monsterType, requiredKills, requiredKills);
	}

	public int getMasterID() {
		return masterID;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getRequiredCombat() {
		return requiredCombat;
	}

	public int getRequiredSlayer() {
		return requiredSlayer;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}

	public int getFiftyTaskBonus() {
		return fiftyTaskBonus;
	}

	public int getTenTaskBonus() {
		return tenTaskBonus;
	}

	public String getMasterName() {
		return masterName;
	}

	@Override
	public String toString() {
		return masterName;
	}

	public boolean isMembersOnly() {
		return false;
	}

	public int getRequiredMemberRank() {
		return requiredMemberRank;
	}

}
