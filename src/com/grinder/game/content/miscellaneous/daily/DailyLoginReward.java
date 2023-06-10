package com.grinder.game.content.miscellaneous.daily;

import com.google.gson.annotations.Expose;

/**
 * A class that handles the daily login rewards.
 * 
 * @author Blake
 * @author Stan van der Bend
 */
public class DailyLoginReward {

	@Expose final String ipAddress;
	@Expose final String macAddress;
	@Expose final long lastReward;
	
	/**
	 * Constructs a new {@link DailyLoginReward}.
	 * 
	 * @param ipAddress The ip address.
	 * @param macAddress The mac address.
	 * @param lastReward The time of last reward received.
	 */
	public DailyLoginReward(final String ipAddress, final String macAddress, final long lastReward) {
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.lastReward = lastReward;
	}

}
