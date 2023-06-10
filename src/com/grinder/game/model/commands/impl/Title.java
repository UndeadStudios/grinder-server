package com.grinder.game.model.commands.impl;

import java.util.Arrays;
import java.util.List;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class Title implements Command {

    @Override
    public String getSyntax() {
        return "[title]";
    }

    @Override
    public String getDescription() {
        return "Sets a custom title.";
    }

    private static final List<String> INAPPROPRIATE_TITLES = Arrays.asList("nigger", "ass", "boobs");

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (INAPPROPRIATE_TITLES.stream().anyMatch(title -> parts[1].toLowerCase().contains(title))) {
            player.getPacketSender().sendMessage("You're not allowed to have that in your title.");
            return;
        }
        player.setTitle("<col=ff0000>" + parts[1] + "</col>");
        player.updateAppearance();
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
