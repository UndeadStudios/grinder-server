package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class DropsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the NPC drops table link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening Grinderscape NPC drops link..");
		player.getPacketSender().sendURL("https://wiki.grinderscape.org/Main_page/In-Game_Section/Bestiary");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
