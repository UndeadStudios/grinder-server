package com.grinder.game.content.object;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

/**
 * BrimstoneChest.java
 * Handles Brimstone chest rewards
 *	@author Lou
 */

public class BrimstoneChest {
	
	private static final Animation EMOTE = new Animation(832, 5);
	
	/**
	 * ENUM of item rewards
	 * FORMAT: {itemId, minimumAmount, maximumAmount}
	 */
	public enum BrimstoneRewards {
		RAW_SWORDFISH(372, 60, 105),
		RAW_MONKFISH(7945, 60, 72),
		RAW_KARAMBWAN(3143, 60, 85),
		RAW_SHARK(384, 78, 102),
		RAW_MANTARAY(390, 70, 105),
		RAW_ANGLER(13440, 55, 63),
		RAW_SALMON(332, 110, 150),
		RAW_SEATURTLE(396, 60, 85),
		RAW_LOBSTER(378, 90, 140),
		BOW_STRING(1778, 220, 300),
		MAPLE_SHORT_U(65, 60, 90),
		YEW_SHORT_U(69, 60, 92),
		GRIMY_TORSOL(220, 60, 85),
		GRIMY_TOADFLAX(3050, 60, 90),
		GRIMY_SNAPDRAGON(3052, 72, 85),
		GRIMY_KWAURM(214, 70, 92),
		IRON_ORE(441, 80, 160),
		COAL(454, 90, 140),
		MITHRIL_ORE(448, 80, 120),
		ADAMANT_ORE(450, 70, 110),
		RUNE_ORE(452, 70, 100),
		IRON_BAR(2352, 120, 160),
		STEEL_BAR(2354, 110, 150),
		GOLD_BAR(2358, 50, 61),
		MITHRIL_BAR(2360, 90, 100),
		ADAMANT_BAR(2362, 85, 95),
		RUNE_BAR(2364, 80, 90),
		PURE_ESSENCE(7937, 150, 300),
		UNCUT_SHAPPHIRE(1624, 70, 93),
		UNCUT_RUBY(1620, 67, 97),
		UNCUT_DIAMOND(1618, 55, 85),
		UNCUT_DRAGONSTONE(1632, 20, 35),
		WILLOW_LOGS(1520, 120, 240),
		YEW_LOGS(1516, 70, 140),
		MAGIC_LOGS(1514, 95, 120),
		COINS(995, 15000000, 30000000),
		RUNE_PLATEBODY(1128, 1, 1),
		RUNE_PLATELEGS(1080, 1, 1),
		RUNE_FULLHELM(1164, 1, 1);

		private final int itemId, minimumAmount, maximumAmount;

		private BrimstoneRewards(int itemId, int minimumAmount, int maximumAmount) {
			this.itemId = itemId;
			this.minimumAmount = minimumAmount;
			this.maximumAmount = maximumAmount;
		}

		public int getItemId() {
			return itemId;
		}

		public int getMinimumAmount() {
			return minimumAmount;
		}

		public int getMaximumAmount() {
			return maximumAmount;
		}
		
        /**
         * Pick a random value of the BaseColor enum.
         * @return a random ItemId.
         */
        public static BrimstoneRewards getRandomItem() {
            Random random = new Random();
            return values()[random.nextInt(BrimstoneRewards.values().length)];
        }
	}

	
	/**
	 * Opens the BrimstoneChest
	 * @param	player	[The player instance]
	 */
	public static void openBrimstoneChest(final Player player) {
		
        if (!player.getInventory().contains(23083)) {
            player.getPacketSender().sendMessage("You need a brimstone key to search this chest.", 1000);
            player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
            return;
        }
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, true)) {
            return;
        }
        BrimstoneRewards reward = BrimstoneRewards.getRandomItem();
        int amount = Misc.randomInclusive(reward.getMinimumAmount(), reward.getMaximumAmount());
        // Logging
		if (!player.getGameMode().isSpawn())
		Logging.log("BrimstoneChest", "[BrimstoneChest]: " + player.getUsername() + " opened a brimstone chest.");
        player.getInventory().delete(23083, 1);
        player.performAnimation(EMOTE);
        player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
        player.getPacketSender().sendMessage("You unlock the chest with your key.", 1000);
        player.BLOCK_ALL_BUT_TALKING = true;
        TaskManager.submit(1, () -> {
			if (player.getInventory().countFreeSlots() > 1) {
				player.getInventory().add(reward.getItemId(), amount);
			} else {
				ItemContainerUtil.dropUnder(player,reward.getItemId(), amount);
			}
			player.getPacketSender().sendMessage("You find some treasure in the chest.", 1000);

			// Increase points & send message
			player.getPoints().increase(AttributeManager.Points.BRIMSTONE_CHESTS_OPENED, 1); // Increase points
			player.sendMessage("You have opened the brimstone chest " + player.getPoints().get(AttributeManager.Points.BRIMSTONE_CHESTS_OPENED) +" times.");

			player.BLOCK_ALL_BUT_TALKING = false;
		});
	}
}