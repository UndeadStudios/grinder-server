package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class KickBotsCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Disconnects all active bots.";
	}
	
	private int count;

	@Override
	public void execute(Player player, String command, String[] parts) {
		World.getPlayers().forEach(p -> {
			if (p instanceof BotPlayer) {
				World.getPlayerRemovalQueue().add(p);
				count++;
			}
		});
		
		if (count > 0) {
			player.sendMessage("Successfully kicked @dre@" + count + "@bla@ bots.");
		} else {
			player.sendMessage("There are currently no bots online.");
		}
		
		count = 0;
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || player.getUsername().equals("3lou 55")
				|| player.getUsername().equals("Stan")
				|| player.getUsername().equals("Mod Grinder"));
	}

}
