package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

public class ForceKickCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Forces a temporary kick of the account.";
	}
	
	@Override
	public void execute(Player player, String command, String[] parts) {
		Optional<Player> plr = World.findPlayerByName(command.substring(parts[0].length() + 1));

		if (!plr.isPresent()) {
			player.getPacketSender().sendMessage(command.substring(parts[0].length() + 1) + " appears to be offline.");
			return;
		}

		PunishmentManager.submit(player, plr.get().getUsername(), PunishmentType.KICK);
		Logging.log("kicks", player.getUsername() + " has kicked the account " + plr.get().getUsername());
		player.getPacketSender().sendMessage(plr.get().getUsername() + " has been successfully kicked.");
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
	}

}
