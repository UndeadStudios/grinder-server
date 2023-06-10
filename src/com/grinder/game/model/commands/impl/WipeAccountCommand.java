package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.util.DiscordBot;

/**
 * The wipe account command
 * 
 * @author 2012
 *
 */
public class WipeAccountCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Wipes the player's bank and inventory.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		Optional<Player> plr = World.findPlayerByName(command.substring(parts[0].length() + 1));
		if (plr.isPresent()) {
			plr.get().getInventory().resetItems().refreshItems();
			plr.get().getEquipment().resetItems().refreshItems();
			plr.get().getSafeDeposit().resetItems().refreshItems();
			Banking.wipe(plr.get());
			plr.get().updateAppearance();
			player.getPacketSender().sendMessage("<img=742> The account @dre@" + plr.get().getUsername() +"</col> has been successfully wiped.");
			plr.get().getPacketSender()
					.sendMessage("<img=742> @red@Your account has been completed cleaned of items. Consider following the rules.");
			if(DiscordBot.ENABLED)
				DiscordBot.INSTANCE.sendModMessage(plr.get().getUsername() + " account has been wiped by " + player.getUsername() +" for breaking the rules.");
		} else {
			player.getPacketSender().sendMessage("The player doesn't seem to be online to execute this action.");
		}
	}

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER);
    }

}
