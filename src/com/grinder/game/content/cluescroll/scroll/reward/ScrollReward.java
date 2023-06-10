package com.grinder.game.content.cluescroll.scroll.reward;


import com.grinder.util.Misc;

public class ScrollReward {

	private final int itemID;
	private final int minAmount;
	private final int maxAmount;

	public ScrollReward(int itemID, int minAmount, int maxAmount) {
		this.itemID = itemID;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	public ScrollReward(int itemID, int amount) {
		this(itemID, amount, amount);
	}

	public ScrollReward(int itemID) {
		this(itemID, 1, 1);
	}

	public int getItemID() {
		return itemID;
	}
	
	public int getAmount() {
		return minAmount + Misc.randomInt(maxAmount - minAmount);
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

}
