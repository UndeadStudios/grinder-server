package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.Misc;

public class ChangeClanChatName implements EnterSyntax {

	@Override
	public void handleSyntax(Player player, String input) {
		if (input.length() > 12) {
			input = input.substring(0, 11);
		}
		if (!Misc.isValidName(input)) {
			player.getPacketSender().sendMessage("Invalid syntax entered. Please set a valid name.");
			return;
		}
		GlobalClanChatManager.setName(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
