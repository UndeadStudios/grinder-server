package com.grinder.game.model.commands.impl;

import com.grinder.game.content.GameMode;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class IronManRankCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Sets your Iron Man rank icon.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
/*        if (player.getGameMode().getCrown() != -1) {
            player.setCrown(player.getGameMode().getCrown());
        }*/
        player.getPacketSender().sendRights();
        player.getPacketSender().sendMessage("<img=779> You have successfully changed your display rank.");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getGameMode().isAnyIronman();
    }
}
