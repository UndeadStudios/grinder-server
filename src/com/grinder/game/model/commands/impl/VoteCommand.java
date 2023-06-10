package com.grinder.game.model.commands.impl;

import com.grinder.game.content.miscellaneous.voting.Voting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;

public class VoteCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the voting page link.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	player.getPacketSender().sendMessage("@dre@Opening the voting page URL. Type @blu@::redeemvote</col> once you're done.");
        player.getPacketSender().sendURL(Voting.VOTE_URL);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
