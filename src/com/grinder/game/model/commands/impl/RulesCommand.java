package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class RulesCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the server's rules page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendMessage("@dre@Opening Grinderscape rules link..");
        player.getPacketSender().sendURL("https://wiki.grinderscape.org/Main_page/Server_Rules_hub/Server_rules/In-game_rules");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
