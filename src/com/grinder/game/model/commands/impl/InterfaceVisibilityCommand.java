package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class InterfaceVisibilityCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id] [hidden]";
    }

    @Override
    public String getDescription() {
        return "Toggles the visibility of the selected interface id.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendInterfaceDisplayState(Integer.parseInt(parts[1]), Boolean.parseBoolean(parts[2]));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
