package com.grinder.game.model.commands;

import com.grinder.game.entity.agent.player.Player;

public interface Command {

    String getSyntax();

    String getDescription();

    void execute(Player player, String command, String[] parts);

    boolean canUse(Player player);

}
