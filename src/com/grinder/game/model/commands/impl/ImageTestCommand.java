package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class ImageTestCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id]";
    }

    @Override
    public String getDescription() {
        return "Display's the image id in the chatbox.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        	int imageId = Integer.parseInt(parts[1]);
			player.getPacketSender().sendMessage("<img=" + imageId +"> Loaded image : " + imageId);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
