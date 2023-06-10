package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class HumanCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Sets your account to the default mode.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
/*		if (player.getHitpoints() <= 0)
			return;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while in a trade!");
			return;
		}
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isOnTutorialMode()) {
        	return;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("You can't do that when you're busy.");
    		return;
    	}
    	if (!player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't use back command when your not AFK!");
    		return;
    	}*/
		player.performAnimation(new Animation(65535));
		player.resetAttributes();
		player.getPacketSender().sendMessage("Attributes reseted, and you're back to a human!");
    }

    @Override
    public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
    	return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("3lou 55"));
    }

}
