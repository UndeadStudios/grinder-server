package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class PricesCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the item's prices page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendMessage("@dre@Opening the Item Prices page..");
		player.getPacketSender().sendURL("https://wiki.grinderscape.org/Main_page/Prices");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
