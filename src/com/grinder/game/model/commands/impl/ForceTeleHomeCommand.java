package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

/**
 * Teleporting a player home
 * 
 * @author 2012
 *
 */
public class ForceTeleHomeCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Forces the player to be teleported to home.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
        Optional<Player> plr = World.findPlayerByName(command.substring(parts[0].length() + 1));
        if (plr.isPresent()) {
			if (plr.get().getStatus() == PlayerStatus.TRADING) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
			if (plr.get().getStatus() == PlayerStatus.BANKING) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
			if (plr.get().getStatus() == PlayerStatus.DICING) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
			if (plr.get().getStatus() == PlayerStatus.SHOPPING) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
			if (plr.get().getStatus() == PlayerStatus.DUELING) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
			if (!PlayerUtil.isDeveloper(player) && plr.get().busy()) {
				player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
				return;
			}
            plr.get().moveTo(GameConstants.DEFAULT_POSITION);
            plr.get().getPacketSender().sendMessage("<img=742> You have been teleported to home by: @dre@" + player.getUsername() +"</col>!");
            player.sendMessage("<img=742> You have sucessfully teleported @dre@" + plr.get().getUsername() + "</col> to home!");
        } else {
        	player.sendMessage("The player doesn't seem to be online to execute this action.");
        }
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.ADMINISTRATOR);
	}

}
