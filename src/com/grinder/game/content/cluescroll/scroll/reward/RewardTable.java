package com.grinder.game.content.cluescroll.scroll.reward;

import java.util.Arrays;
import java.util.List;

/**
 * Table with a set of items that could be rewarded based on table probability
 * 
 * @author Pb600
 */
public class RewardTable {

	private final float probability;
	private final ScrollReward[] rewards;

	public RewardTable(float probability, ScrollReward... rewards) {
		this.probability = probability;
		this.rewards = rewards;
	}

	public float getProbability() {
		return probability;
	}

	public ScrollReward[] getRewards() {
		return rewards;
	}

	public boolean hasUniqueRewards(List<ScrollReward> alreadyPresent){
		return Arrays.stream(rewards).anyMatch(scrollReward -> alreadyPresent.stream().noneMatch(scrollReward1 -> scrollReward1 == scrollReward));
	}

}
