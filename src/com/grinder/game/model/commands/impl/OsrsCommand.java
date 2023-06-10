package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class OsrsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the OSRS trading page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening the OSRS trading page link..");
		player.getPacketSender().sendURL("https://forum.grinderscape.org/index.php?/forum/121-trade-requests/");
    }

    @Override
    public boolean canUse(Player player) {
        return false;
    }
}
