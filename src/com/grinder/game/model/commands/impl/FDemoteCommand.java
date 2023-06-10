package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

public class FDemoteCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Forces to demote a player from staff's rank.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	if (command.length() <= 6) {
    		return;
    	}
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not a valid online player.");
            return;
        }
        plr.get().setRights(PlayerRights.NONE);
        plr.get().setCrown(0);
        plr.get().getPacketSender().sendRights();
        //plr.get().requestLogout();
        player.getPacketSender().sendMessage("<img=740> " + player2 + " has been demoted!");
        plr.get().getPacketSender().sendMessage("<img=740> You have been demoted by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
        Logging.log("demotes", "" + player.getUsername() + " has demoted the player: " + plr.get().getUsername() + "");
    }
    	

    @Override
    public boolean canUse(Player player) {
    	return player.getUsername().equals("3lou 55") || player.getUsername().equals("Mod Grinder");
    }

}
