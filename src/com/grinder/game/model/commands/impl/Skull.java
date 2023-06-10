package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.commands.Command;

public class Skull implements Command {

	@Override
	public String getSyntax() {
		return "[red]";
	}

	@Override
	public String getDescription() {
		return "Skulls your account to white or red skull.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		if (player.getCombat().isInCombat()) {
			player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat before using this command!");
			return;
		}
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("You can't do that when you're busy.");
    		return;
    	}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't use this command while trading!");
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't use this command while dueling!");
			return;
		}
		if (parts[0].contains("red")) {
			player.getCombat().skull(SkullType.RED_SKULL, (60 * 30));
		} else {
			player.getCombat().skull(SkullType.WHITE_SKULL, 3600);
		}
	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}
}
