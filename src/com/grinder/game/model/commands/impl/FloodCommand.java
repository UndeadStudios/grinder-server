package com.grinder.game.model.commands.impl;

import com.grinder.Server;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class FloodCommand implements Command {

    @Override
    public String getSyntax() {
        return "[amount]";
    }

    @Override
    public String getDescription() {
        return "Floods server with x amount of logins.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amt = Integer.parseInt(parts[1]);
//        Server.getFlooder().login(amt);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
