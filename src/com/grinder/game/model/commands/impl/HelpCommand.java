package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class HelpCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the Discord support link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	player.getPacketSender().sendMessage("@dre@You can post here and our staff will reply to you as soon as possible.");
        player.getPacketSender().sendURL("https://discord.com/channels/358664434324865024/992202052434403378");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
