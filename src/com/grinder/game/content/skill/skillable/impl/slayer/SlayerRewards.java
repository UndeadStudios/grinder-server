package com.grinder.game.content.skill.skillable.impl.slayer;

import java.util.Arrays;
import java.util.Optional;

import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager.ConfirmType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Misc;

/**
 * Handles slayer rewards
 * 
 * @author 2012
 * @author Luka Furlan
 */
public class SlayerRewards {

	private static final int UNLOCK_BUTTON_START_ID = 62110;
	private static final int EXTEND_BUTTON_START_ID = 62244;

	private static final int BUTTON_ID_INCREMENT = 7;

	private static final int EXTEND_REWARD_START_OFFSET = Rewards.NEED_MORE_DARKNESS.ordinal();

	private enum RewardType {
		UNLOCK,
		EXTEND,
	}

	/**
	 * The rewards
	 */
	public enum Rewards {

		// todo: make them all work properly.

		GARGOYLE_SMASHER(RewardType.UNLOCK, 120),

		SLUG_SALTER(RewardType.UNLOCK, 80),

		REPTILE_FREEZER(RewardType.UNLOCK, 90),

		SHROOM_SPAYER(RewardType.UNLOCK, 110),

		BROADER_FLETCHING(RewardType.UNLOCK, 300),

		MALEVOLENT_MASQUERADE(RewardType.UNLOCK, 400),

		RING_BLING(RewardType.UNLOCK, 300),

		SEEING_RED(RewardType.UNLOCK, 50),

		I_HOPE_YOU_MITH_ME(RewardType.UNLOCK, 80),

		WATCH_THE_BIRDIE(RewardType.UNLOCK, 80),

		HOT_STUFF(RewardType.UNLOCK, 100),

		REPTILE_GOT_RIPPED(RewardType.UNLOCK, 75),

		LIKE_A_BOSS(RewardType.UNLOCK, 200),

		KING_BLACK_BONNET(RewardType.UNLOCK, 1000),

		KALPHITE_KAT(RewardType.UNLOCK, 1000),

		UNHOLY_HELMET(RewardType.UNLOCK, 1000),

		BIGGER_AND_BADDER(RewardType.UNLOCK, 150),

		UNDEAD_HEAD(RewardType.UNLOCK, 1000),

		TWISTED_VISION(RewardType.UNLOCK, 1000),

		USE_MORE_HEAD(RewardType.UNLOCK, 1000),

		TZTOK_JAD(RewardType.UNLOCK, 2000),

		VERZIK_VITUR(RewardType.UNLOCK, 3000),

		TZKAL_ZUK(RewardType.UNLOCK, 3500),
		
		NEED_MORE_DARKNESS(RewardType.EXTEND, 100),
		
		ANKOU_VERY_MUCH(RewardType.EXTEND, 100),
		
		SUQ_A_NOTHER_ONE(RewardType.EXTEND, 100),
		
		FIRE_AND_DARKNESS(RewardType.EXTEND, 50),
		
		PEDAL_TO_THE_METALS(RewardType.EXTEND, 100),
		
		I_REALLY_MITH_YOU(RewardType.EXTEND, 120),

		SPIRITUAL_FERVOUR(RewardType.EXTEND, 100),
		
		BIRDS_OF_A_FEATHER(RewardType.EXTEND, 100),
		
		GREATER_CHALLENGE(RewardType.EXTEND, 100),
		
		ITS_DARK_IN_HERE(RewardType.EXTEND, 100),
		
		BLEED_ME_DRY(RewardType.EXTEND, 75),
		
		SMELL_YA_LATER(RewardType.EXTEND, 100),

		HORRORIFIC(RewardType.EXTEND, 100),
		
		TO_DUST_YOU_SHALL_RETURN(RewardType.EXTEND, 100),
		
		WYVER_NOTHER_ONE(RewardType.EXTEND, 100),
		
		GET_SMASHED(RewardType.EXTEND, 100),
		
		NECH_PLEASE(RewardType.EXTEND, 100),
		
		AUGMENT_MY_ABBIES(RewardType.EXTEND, 100),
		
		KRACK_ON(RewardType.EXTEND, 100);

		/**
		 * The type of reward
		 */
		private RewardType rewardType;

		/**
		 * The cost
		 */
		private int cost;

		/**
		 * Represents a new reward
		 * 
		 * @param rewardType {@link RewardType}
		 *            the button
		 * @param cost {@link Integer}
		 *            the cost
		 */
		Rewards(RewardType rewardType, int cost) {
			this.setRewardType(rewardType);
			this.setCost(cost);
		}

		/**
		 * @return {@link RewardType}
		 */
		public RewardType getRewardType() {
			return rewardType;
		}

		/**
		 *
		 * @param rewardType {@link RewardType}
		 */
		public void setRewardType(RewardType rewardType) {
			this.rewardType = rewardType;
		}

		/**
		 * Sets the cost
		 *
		 * @return the cost
		 */
		public int getCost() {
			return cost;
		}

		/**
		 * Sets the cost
		 * 
		 * @param cost
		 *            the cost
		 */
		public void setCost(int cost) {
			this.cost = cost;
		}

		/**
		 * Gets for button
		 * 
		 * @param button
		 *            the button
		 * @return
		 */
		public static Optional<Rewards> forButton(int button) {
			return Arrays.stream(values()).filter(reward -> {
				int rewardButtonId;

				if (reward.getRewardType() == RewardType.UNLOCK) {
					rewardButtonId = UNLOCK_BUTTON_START_ID + reward.getRewardIndex() * BUTTON_ID_INCREMENT;
				} else {
					rewardButtonId = EXTEND_BUTTON_START_ID + reward.getRewardIndex() * BUTTON_ID_INCREMENT;
				}

				return rewardButtonId == button;
			}).findFirst();
		}

		public int getRewardIndex () {
			if (this.getRewardType() == RewardType.UNLOCK) {
				return this.ordinal();
			}

			return this.ordinal() - EXTEND_REWARD_START_OFFSET;
		}
	}

	public static boolean unlocked(Player player, Rewards reward){
		return player.getSlayer().getExtended()[reward.getRewardIndex()];
	}
	/**
	 * Purchasing rewards
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 */
	public static boolean purchase(Player player, int button) {
		/*
		 * The reward
		 */
		Optional<Rewards> optionalReward = Rewards.forButton(button);

		/*
		 * No reward
		 */
		if (optionalReward.isEmpty()) {
			return false;
		}
		if (player.getInterfaceId() != SlayerManager.UNLOCK) {
			return false;
		}

		Rewards reward = optionalReward.get();

		/*
		 * The reward index
		 */
		int rewardIndex = reward.getRewardIndex();

		/*
		 * Unlockables
		 */
		if (reward.getRewardType() == RewardType.UNLOCK) {
			/*
			 * Already unlocked
			 */
			if (player.getSlayer().getUnlocked()[rewardIndex]) {
				player.getPacketSender().sendMessage("This reward is already unlocked.", 1000);
				return true;
			}
			/*
			 * Not enough points
			 */
			if (player.getPoints().get(Points.SLAYER_POINTS) < reward.getCost()) {
				player.getPacketSender().sendMessage("You don't have enough Slayer points to unlock this reward.", 1000);
				return true;
			}
			player.getSlayer().setType(ConfirmType.UNLOCK);
			player.getSlayer().setReward(reward);
		} else {
			/*
			 * Already unlocked
			 */
			if (player.getSlayer().getExtended()[rewardIndex]) {
				player.getPacketSender().sendMessage("This reward is already unlocked.", 1000);
				return true;
			}
			/*
			 * Not enough points
			 */
			if (player.getPoints().get(Points.SLAYER_POINTS) < reward.getCost()) {
				player.getPacketSender().sendMessage("You don't have enough Slayer points to unlock this reward.", 1000);
				return true;
			}
			player.getSlayer().setType(ConfirmType.EXTEND);
			player.getSlayer().setReward(reward);
		}


		player.getPacketSender().sendString(60107, Misc.formatName(reward.name().toLowerCase()));
		player.getPacketSender().sendString(60108,
				"You are about to purchase a Slayer\\nreward. Please be sure you know what you\\nare purchashing before confirming.");
		player.getPacketSender().sendString(60109, "@red@Cost: " + reward.getCost() + " points");
		player.getPacketSender().sendString(60110, "There are no refunds available");
		player.getPacketSender().sendInterface(SlayerManager.CONFIRM);
		return true;
	}
}
