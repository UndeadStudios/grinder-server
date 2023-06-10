package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSaving;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

public class DemoteCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Demote account from staff's rank.";
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
		if (plr.get().getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a trade!");
			return;
		}
/*		if (plr.get().getGameMode().isIronman() || plr.get().getGameMode().isHardcore() || plr.get().getGameMode().isUltimate()) {
			player.getPacketSender().sendMessage("You can't demote a player with an Iron Man rank.");
			return;
		}*/
        if (plr.get().BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (plr.get().isInTutorial()) {
        	return;
        }
		if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("<img=779> You can't do that when you're busy.");
    		return;
    	}
    	if (plr.get().getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.sendMessage("<img=779> " + player2 +" can't be promoted while AFK!");
    		return;
    	}
    	if (plr.get().getRights().isStaff()) {
				if (plr.get().getRights().equals(PlayerRights.DEVELOPER) || plr.get().getRights().equals(PlayerRights.CO_OWNER)
						|| plr.get().getRights().equals(PlayerRights.OWNER)) {
					player.getPacketSender().sendMessage("<img=742> " + player2 +" can't be demoted. You can only demote ranks below you.");
					return;
				}
		plr.get().setTitle("");
		plr.get().updateAppearance();
        plr.get().setRights(PlayerRights.NONE);
        plr.get().setCrown(0);
		plr.get().setTitle("");
		plr.get().updateAppearance();
        plr.get().getPacketSender().sendRights();
        //plr.get().requestLogout();
        player.getPacketSender().sendMessage("<img=740> " + player2 + " has been demoted!");
        plr.get().getPacketSender().sendMessage("<img=740> You have been demoted by " + player.getUsername() + "!");
        Logging.log("demotes", "" + player.getUsername() + " has demoted the player: " + plr.get().getUsername() + "");
    } else {
    		player.getPacketSender().sendMessage("<img=742> " + player2 +" is not a ranked staff member to be demoted.");
    		return;
    	}
    }
    	

    @Override
    public boolean canUse(Player player) {
    	return player.getUsername().equals("3lou 55") || player.getRights().equals(PlayerRights.DEVELOPER) || player.getRights().equals(PlayerRights.ADMINISTRATOR)
    			|| player.getRights().equals(PlayerRights.OWNER) ||player.getRights().equals(PlayerRights.CO_OWNER);
    }

}
