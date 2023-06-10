package com.grinder.game.model.commands.impl;

import java.util.Arrays;
import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.bot.BotManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

public class MassBot implements Command {

	@Override
	public String getSyntax() {
		return "[amount]";
	}

	@Override
	public String getDescription() {
		return "Mass add 600 bot's within your area.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		String script = parts[1];
		
		String username = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));

		Optional<Player> plr = World.findPlayerByName(username);

		if (plr.isPresent()) {
			player.sendMessage("A player with that username is already online.");
			return;
		}
		int number = 0;
		for (int i = 0; i <= 300; i++) {
			number += 1;
		String username2 = Integer.toString(number);

		//System.out.println(username2);
		BotManager.addBot(script, username2, new Position(player.getPosition().getX() + Misc.getRandomInclusive(64), player.getPosition().getY() + Misc.getRandomInclusive(32), 0));
		BotManager.addBot(script, username2, new Position(player.getPosition().getX() - Misc.getRandomInclusive(64), player.getPosition().getY() - Misc.getRandomInclusive(32), 0));
	}
			}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || player.getUsername().equals("3lou 55")
				|| player.getUsername().equals("Stan")
				|| player.getUsername().equals("Mod Grinder"));
	}

}
