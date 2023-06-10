package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.game.content.minigame.MinigameManager;

public class MinigameMinuteCommand implements Command {

	@Override
	public String getSyntax() {
		return "[id]";
	}

	@Override
	public String getDescription() {
		return "Changes the time left for a Minigame to start.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		int id = Integer.parseInt(parts[1]);
		MinigameManager.minutesSinceLastPublicGameEnded = id;
		player.getPacketSender().sendMessage("Minigame minute set to: "+id);
	}

	@Override
	public boolean canUse(Player player) {
		return PlayerUtil.isStaff(player);
	}

}
