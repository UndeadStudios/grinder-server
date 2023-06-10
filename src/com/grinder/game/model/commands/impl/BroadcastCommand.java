package com.grinder.game.model.commands.impl;

import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;

public class BroadcastCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Opens the broadcast system panel.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

    	/*if (parts.length < 2) {
    		player.getPacketSender().sendMessage("@dre@Usage -> ::broadcast [min] [message]");
    		return;
    	}
    	
    	int min = Integer.parseInt(parts[1]);
    	
    	int sec = min * 60;
    	
    	int ticks = (int) (sec / 0.6);
    	
    	String message = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
    	
    	Broadcast.broadcast("<img=998>  " + Misc.capitalizeWords(message), ticks);
    	
    	Broadcast.broadcast(ticks, Misc.capitalize(message), link);
    	
    	player.sendMessage(String.format("<img=998>  Broad casting the message: %s for %d ticks.", message, ticks));

		Logging.log("broadcasts", player.getUsername() + ": " + message);*/

    	Broadcast.openInterface(player);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR
                || rights == PlayerRights.GLOBAL_MODERATOR || rights == PlayerRights.MODERATOR);
    }

}
