package com.grinder.game.model.commands.impl;

import com.grinder.game.content.item.VotingTicket;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class AddPointsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Gives 1,000 voting tickets.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
		VotingTicket.addVotingTickets(player, 1000);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
