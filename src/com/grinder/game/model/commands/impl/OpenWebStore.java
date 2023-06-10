package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class OpenWebStore implements Command {

    final String STORE_URL = "https://www.grinderscape.org/store";

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the server's store page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening the store's link..");
        player.getPacketSender().sendURL(STORE_URL);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
