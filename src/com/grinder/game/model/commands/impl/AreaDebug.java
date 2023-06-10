package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class AreaDebug implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Prints out your current area name.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		if (player.getArea() != null) {
			//player.getPacketSender().sendMessage("");
			player.getPacketSender().sendMessage("Area: " + player.getArea().getClass().getName());
			// player.getPacketSender().sendMessage("Players in this area: " +
			// player.getArea().players.size() +", npcs in this area:
			// "+player.getArea().npcs.size());
		} else {
			player.getPacketSender().sendMessage("No area found for your coordinates.");
		}
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
	}

}
