package com.grinder.game.content.gambling.lottery;

/**
 * The lottery tickets
 *
 * @author 2012
 */
public class LotteryTicket {

	private String username;

	private long amount;

	public LotteryTicket(String username, long amount) {
		this.setUsername(username);
		this.setAmount(amount);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

}
