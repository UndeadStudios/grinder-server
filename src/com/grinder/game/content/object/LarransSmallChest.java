package com.grinder.game.content.object;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * LarransChest.java
 * Handles Larran's chest rewards
 *	@author Lou
 */
public class LarransSmallChest {
	
	private static final Animation EMOTE = new Animation(832, 5);
	
	/**
	 * ENUM of item rewards
	 * FORMAT: {itemId, minimumAmount, maximumAmount}
	 */
	public enum LarransRewards {
		UNCUT_DIAMOND(ItemID.UNCUT_DIAMOND_2, 35, 45),
		UNCUT_RUBY(ItemID.UNCUT_RUBY_2, 35, 45),
		COAL(ItemID.COAL_2, 450, 650),
		GOLD_ORE(ItemID.GOLD_ORE_2, 150, 250),
		DRAGON_ARROWTIPS(ItemID.DRAGON_ARROWTIPS, 100, 250),
		COINS(ItemID.COINS, 10_500_000, 37_500_000),
		COINS2(ItemID.COINS, 15_500_000, 21_500_000),
		BLOOD_MONEY(ItemID.BLOOD_MONEY, 20_000, 35_000),
		BLOOD_MONEY2(ItemID.BLOOD_MONEY, 10_000, 35_000),
		IRON_ORE(ItemID.IRON_ORE_2, 500, 650),
		RUNE_FULLHELM(ItemID.RUNE_FULL_HELM_2, 3, 5),
		RUNE_PLATEBODY(ItemID.RUNE_PLATEBODY_2, 2, 3),
		RUNE_PLATELEGS(ItemID.RUNE_PLATELEGS_2, 2, 3),
		PURE_ESSENCE(ItemID.PURE_ESSENCE_2, 4500, 7500),
		RAW_TUNA(ItemID.RAW_TUNA_2, 150, 525),
		RAW_LOBSTER(ItemID.RAW_LOBSTER_2, 150, 525),
		RAW_SWORDFISH(ItemID.RAW_SWORDFISH_2, 150, 450),
		RAW_MONKFISH(ItemID.RAW_MONKFISH_2, 150, 450),
		RAW_SHARK(ItemID.RAW_SHARK_2, 150, 375),
		RAW_SEATURTLE(ItemID.RAW_SEA_TURTLE_2, 120, 300),
		RAW_MANTARAY(ItemID.RAW_MANTA_RAY_2, 120, 240),
		RUNITE_ORE(ItemID.RUNITE_ORE_2, 120, 150),
		STEEL_BAR(ItemID.STEEL_BAR_2, 350, 550),
		MAGIC_LOGS(ItemID.MAGIC_LOGS_2, 180, 440),
		DRAGON_DART_TIP(ItemID.DRAGON_DART_TIP, 80, 200),
		PALM_TREE_SEED(ItemID.PALM_TREE_SEED, 3, 5),
		MAGIC_SEED(ItemID.MAGIC_SEED, 3, 4),
		CELASTRUS_SEED(22869, 3, 5),
		DRAGONFRUIT_TREE_SEED(22877, 3, 5),
		REDWOOD_TREE_SEED(22871, 1, 1),
		TORSOL_SEED(ItemID.TORSTOL_SEED, 4, 6),
		SNAPDRAGON_SEED(ItemID.SNAPDRAGON_SEED, 4, 6),
		RANARR_SEED(ItemID.RANARR_SEED, 4, 6),
		ANGLERFISH(ItemID.ANGLERFISH_2, 50, 150)
		;

		private final int itemId, minimumAmount, maximumAmount;

		private LarransRewards(int itemId, int minimumAmount, int maximumAmount) {
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
        public static LarransRewards getRandomItem() {
            Random random = new Random();
            return values()[random.nextInt(LarransRewards.values().length)];
        }
	}

	
	/**
	 * Opens the LarransChest
	 * @param	player	[The player instance]
	 */
	public static void openLarransSmallChest(final Player player) {
		
        if (!player.getInventory().contains(23490)) {
            player.getPacketSender().sendMessage("You need a Larran's key to unlock this chest.", 1000);
            player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
            return;
        }
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
            return;
        }
		LarransRewards reward = LarransRewards.getRandomItem();
        int amount = Misc.randomInclusive(reward.getMinimumAmount(), reward.getMaximumAmount());
        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
        // Logging
		if (!player.getGameMode().isSpawn())
		Logging.log("Larranschest", "[Larranschest]: " + player.getUsername() + " unlocked a Larran's chest.");
        player.getInventory().delete(23490, 1);
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
			player.getPoints().increase(AttributeManager.Points.LARRAN_CHESTS_OPENED, 1); // Increase points
			player.sendMessage("You have unlocked the Larran's chest " + player.getPoints().get(AttributeManager.Points.LARRAN_CHESTS_OPENED) +" times.");

			player.BLOCK_ALL_BUT_TALKING = false;
		});
	}
}