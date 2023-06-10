package com.grinder.game.content.pvp.bountyhunter.kill;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;

/**
 * clears player recent kills in the Wilderness
 */
public class KillClearanceTask extends Task {

	/**
	 * The minute saves
	 */
	private static final int MINUTES = 30;

	/**
	 * The player
	 */
	private Player player;

	/**
	 * Saving task
	 * 
	 * @param player
	 */
	public KillClearanceTask(Player player) {
		super(100 * MINUTES);
		this.player = player;
	}

	@Override
	protected void execute() {

		if(!player.isRegistered()) {
			stop();
			return;
		}

		if (player.isInTutorial())
			return;

		player.getCombat().getBountyHuntController().clearRecentKills();
	}
}
