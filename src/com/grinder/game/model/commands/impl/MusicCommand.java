package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class MusicCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id]";
    }

    @Override
    public String getDescription() {
        return "Plays the selected music id.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendMusic(Integer.parseInt(parts[1]), 4, 0);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
