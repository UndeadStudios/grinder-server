package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;

public class BackCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Get out of the AFK game mode.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		if (player.getHitpoints() <= 0)
			return;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.sendMessage("You can't use this command while in a trade!");
			return;
		}
		if (player.getStatus() != PlayerStatus.AWAY_FROM_KEYBOARD) {
			player.sendMessage("You can't use back command when your not AFK!");
			return;
		}
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isInTutorial()) {
        	return;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
    	if (player.busy()) {
    		player.sendMessage("You can't do that when you're busy.");
    		return;
    	}
		player.performAnimation(new Animation(65535));
		player.setStatus(PlayerStatus.NONE);
		player.say("I'm back, show me some love :love:");
		player.updateAppearance();
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
