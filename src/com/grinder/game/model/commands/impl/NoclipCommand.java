package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class NoclipCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Enables noclip on your account.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendEnableNoclip();
        player.getPacketSender().sendConsoleMessage("Noclip enabled.");
        player.getPacketSender().sendMessage("Noclip enabled.");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
