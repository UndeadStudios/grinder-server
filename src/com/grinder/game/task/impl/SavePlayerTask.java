package com.grinder.game.task.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSaving;
import com.grinder.game.task.Task;

/**
 * Saves character file
 * 
 * @author 2012
 *
 */
public class SavePlayerTask extends Task {

	/**
	 * The minute saves
	 */
	private static final int MINUTES = 10;

	/**
	 * The player
	 */
	private Player player;

	/**
	 * Saving task
	 * 
	 * @param player
	 */
	public SavePlayerTask(Player player) {
		super(100 * MINUTES);
		this.player = player;
	}

	@Override
	protected void execute() {
		if(!World.getPlayers().contains(player)) {
			stop();
			return;
		}
		if (player.isInTutorial()) {
			return;
		}
		PlayerSaving.save(player);
	}
}
