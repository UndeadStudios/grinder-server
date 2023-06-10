package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class HighscoreCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the highscore's page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.getPacketSender().sendMessage("@dre@Opening the Highscores page..");
		player.getPacketSender().sendURL("https://www.grinderscape.org/highscores/");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
