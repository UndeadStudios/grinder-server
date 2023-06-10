package com.grinder.game.content.points;

import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.entity.agent.player.Player;

public class PremiumPoints {

	public static void rewardPremiumPoints(Player player, int amount, String source) {

		final long newAmount = (long) player.getPoints().get(Points.PREMIUM_POINTS) + amount;

		if (newAmount <= Integer.MAX_VALUE) {
			player.getPoints().increase(Points.PREMIUM_POINTS, amount);
		} else
			System.err.println("Could not add "+amount+" premium points to "+player+" (new amount '"+newAmount+"' exceeds max integer value!)");

		logIncome(player, amount, source);
	}

	public static void removePremiumPoints(Player player, int itemID, int itemAmount, int value, String source) {
		player.getPoints().decrease(Points.PREMIUM_POINTS, value);
		if (player.getPoints().get(Points.PREMIUM_POINTS) < 0) {
			player.getPoints().decrease(Points.PREMIUM_POINTS, 0);
		}
		logOutcome(player, itemID, itemAmount, value, source);
	}

	public static void removePremiumPoints(Player player, int amount, String outcome) {
		player.getPoints().decrease(Points.PREMIUM_POINTS, amount);
		if (player.getPoints().get(Points.PREMIUM_POINTS) < 0) {
			player.getPoints().decrease(Points.PREMIUM_POINTS, 0);
		}
		logOutcome(player, -1, 1, amount, outcome);
	}

	public static void logOutcome(Player player, int itemID, int itemAmount, int value, String incomeSource) {
		if (player != null) {
//			PassiveDatabaseWorker.addRequest(new PremiumExtractDAO(player, ExtractType.OUTCOME, incomeSource, value, itemID, itemAmount));
		}
	}

	public static void logIncome(Player player, int value, String incomeSource) {
		if (player != null) {
//			PassiveDatabaseWorker.addRequest(new PremiumExtractDAO(player, ExtractType.INCOME, incomeSource, value, 0, 0));
		}
	}

}
