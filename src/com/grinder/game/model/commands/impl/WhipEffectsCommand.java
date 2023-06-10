package com.grinder.game.model.commands.impl;

import com.grinder.game.content.miscellaneous.voting.Voting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class WhipEffectsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the whip effect's link.";
    }

    final String WHIP_EFFECTS_URL = "https://wiki.grinderscape.org/Main_page/Prices/Whips";

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendMessage("@dre@Opening the whip effect's URL.");
        player.getPacketSender().sendURL(WHIP_EFFECTS_URL);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
