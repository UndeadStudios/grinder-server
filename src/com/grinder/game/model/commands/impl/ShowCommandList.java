package com.grinder.game.model.commands.impl;

import com.grinder.game.content.miscellaneous.MyCommandsInterface;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-18
 */
public class ShowCommandList implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Displays a list of usuable commands.";
    }

    @Override
    public void execute(Player player, String input, String[] parts) {
        MyCommandsInterface.sendInterface(player);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
