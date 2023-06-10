package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class ExitClientCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Exits the player's client.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage("Player " + player2 + " is not online.");
            return;
        }
        if (plr.get().getCombat().isInCombat()) {
            player.getPacketSender().sendMessage("Player " + player2 + " is in combat!");
            return;
        }
        plr.get().getPacketSender().sendExit();
        player.getPacketSender().sendMessage("Closed other player's client.");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
