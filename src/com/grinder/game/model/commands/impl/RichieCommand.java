package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class RichieCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Adds you Gucci richie bag.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
            player.getInventory().add(995, Integer.MAX_VALUE);
        }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("Mod Grinder"));
    }

}
