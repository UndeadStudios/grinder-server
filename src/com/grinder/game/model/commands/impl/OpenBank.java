package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

public class OpenBank implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Opens the target player bank.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not currently online.");
            return;
        }
        plr.get().getBankpin().openBank();
        plr.get().getPacketSender().sendMessage("<img=742> @dre@" + player.getUsername() + "</col> has given you access to your bank.");
		player.sendMessage("<img=742> @dre@Opened up " + plr.get().getUsername() + "'s</col> bank!");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
