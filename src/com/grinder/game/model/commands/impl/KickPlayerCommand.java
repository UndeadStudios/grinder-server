package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.Logging;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class KickPlayerCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Temporary kicks the player's account.";
	}

	private static final String FORCE_KICK_ATTR = "FORCE_KICK";
	
	@Override
	public void execute(Player player, String command, String[] parts) {

		final String targetName = command.substring(parts[0].length() + 1);



		Optional<Player> plr = World.findPlayerByName(targetName);

		if (!plr.isPresent()) {
			player.getPacketSender().sendMessage(command.substring(parts[0].length() + 1) + " appears to be offline.");
			return;
		}
		
		if (plr.get().getStatus() == PlayerStatus.TRADING
				|| plr.get().getStatus() == PlayerStatus.DUELING
				|| plr.get().getStatus() == PlayerStatus.DICING) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
			return;
		}
		if (plr.get().getDueling().inDuel()) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
			return;
		}

		if (plr.get().getTrading().getInteract() != null) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
			return;
		}

		if (plr.get().isBlockingDisconnect()) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
			return;
		}

		if (plr.get().getCombat().isInCombat()) {
			player.getPacketSender().sendMessage(plr.get().getUsername() + " is in combat and can't be kicked right now!");
			return;
		}
		
		/*if (!AreaManager.inWilderness(plr.get())) {
			player.getPacketSender().sendMessage(plr.get().getUsername() + " is in Wilderness and can't be kicked right now!");
			return;
		}*/

		PunishmentManager.submit(player, targetName, PunishmentType.KICK);
		Logging.log("kicks", player.getUsername() + " has kicked the account " + plr.get().getUsername());
	}

	@Override
	public boolean canUse(Player player) {
		return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
