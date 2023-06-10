package com.grinder.util.oldgrinder;

import com.grinder.util.Misc;

public class ValueRange {

	private final int min;
	private final int max;

	public ValueRange(int minTasks, int maxTasks) {
		this.min = minTasks;
		this.max = maxTasks;
	}

	public int getRandomAmount() {
		return min + Misc.getRandomInclusive(max - min);
	}
}
