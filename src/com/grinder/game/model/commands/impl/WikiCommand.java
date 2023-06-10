package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class WikiCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the server's wiki page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening Grinderscape Wiki's link..");
		player.getPacketSender().sendURL("https://wiki.grinderscape.org");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
