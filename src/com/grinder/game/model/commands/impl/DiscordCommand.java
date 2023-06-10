package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class DiscordCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the discord's channel link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening the discord's link..");
		player.getPacketSender().sendURL("https://discord.gg/b46xx5u");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
