package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.commands.Command;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;

public class TournamentCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the tournament minigame.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		TeleportHandler.teleport(player, MinigameManager.EXIT_MINIGAME.clone(), player.getSpellbook().getTeleportType(),
				true, true);
	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}
}
