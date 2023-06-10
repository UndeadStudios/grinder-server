package com.grinder.game.content.pvp.bountyhunter;

import java.util.Arrays;

import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;

/**
 * Handles unlocking through pvp
 * 
 * @author 2012
 *
 */
public class PlayerKillItemUnlockManager {

	/**
	 * The unlockables
	 */
	public enum UnlockableItems {

		;

		/**
		 * The item id
		 */
		private int id;

		/**
		 * The killcount
		 */
		private int killCount;

		/**
		 * The kill streak
		 */
		private int killStreak;

		/**
		 * The target kills
		 */
		private int targetKills;

		/**
		 * Represents an item to be unlocked
		 * 
		 * @param id
		 *            the id
		 * @param killCount
		 *            the killcount
		 * @param killStreak
		 *            the killstreak
		 * @param targetKills
		 *            the target kills
		 */
		UnlockableItems(int id, int killCount, int killStreak, int targetKills) {
			this.setId(id);
			this.setKillCount(killCount);
			this.setKillStreak(killStreak);
			this.setTargetKills(targetKills);
		}

		/**
		 * Sets the id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the id
		 * 
		 * @param id
		 *            the id
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * Sets the killCount
		 *
		 * @return the killCount
		 */
		public int getKillCount() {
			return killCount;
		}

		/**
		 * Sets the killCount
		 * 
		 * @param killCount
		 *            the killCount
		 */
		public void setKillCount(int killCount) {
			this.killCount = killCount;
		}

		/**
		 * Sets the killStreak
		 *
		 * @return the killStreak
		 */
		public int getKillStreak() {
			return killStreak;
		}

		/**
		 * Sets the killStreak
		 * 
		 * @param killStreak
		 *            the killStreak
		 */
		public void setKillStreak(int killStreak) {
			this.killStreak = killStreak;
		}

		/**
		 * Sets the targetKills
		 *
		 * @return the targetKills
		 */
		public int getTargetKills() {
			return targetKills;
		}

		/**
		 * Sets the targetKills
		 * 
		 * @param targetKills
		 *            the targetKills
		 */
		public void setTargetKills(int targetKills) {
			this.targetKills = targetKills;
		}

		/**
		 * Finds an item
		 * 
		 * @param item
		 *            the item
		 * @return the data
		 */
		public static UnlockableItems forId(Item item) {
			return Arrays.stream(values()).filter(c -> c.getId() == item.getId()).findFirst().orElse(null);
		}
	}

	/**
	 * Equipping item
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 * @return equip
	 */
	public static boolean equipItem(Player player, Item item) {
		/*
		 * Unlock data
		 */
		UnlockableItems unlock = UnlockableItems.forId(item);
		/*
		 * No item
		 */
		if (unlock == null) {
			return true;
		}
		/*
		 * Checks kill count
		 */
		if (player.getPoints().get(Points.KILLS) < unlock.getKillCount()) {
			player.getPacketSender()
					.sendMessage("You havent killed enough players to equip " + item.getDefinition().getName()
							+ ". You need " + (unlock.getKillCount() - player.getPoints().get(Points.KILLS)) + " more kills.");
			return false;
		}
		/*
		 * Checks killstreak
		 */
		if (player.getPoints().get(Points.HIGHEST_KILLSTREAK) < unlock.getKillStreak()) {
			player.getPacketSender().sendMessage("You havent killed reached high enough killstreak to equip "
					+ item.getDefinition().getName() + ". You need to reach a killstreak of " + unlock.getKillStreak());
			return false;
		}
		return true;
	}
}
