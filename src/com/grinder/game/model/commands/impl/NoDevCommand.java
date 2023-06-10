package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class NoDevCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Removes developer's rank.";
    }

    /**
     * Promotes your account to developer / none developer based on the current rank.
     */
    @Override
    public void execute(Player player, String command, String[] parts) {

        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.setRights(PlayerRights.NONE);
        } else {
            player.setRights(PlayerRights.DEVELOPER);
        }
        player.getPacketSender().sendRights();
        player.getPacketSender().sendMessage("You have changed your rights to " + player.getRights().toString()+"!");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights() == PlayerRights.DEVELOPER || player.getUsername().equals("Lou") || player.getUsername().equals("3lou 55") || player.getUsername().equals("Mod Grinder") || player.getUsername().equals("3lou 55gg");
    }

}
