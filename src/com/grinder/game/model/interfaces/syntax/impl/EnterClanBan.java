package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.clan.ClanChat;
import com.grinder.game.content.clan.ClanMember;
import com.grinder.game.content.clan.ClanMemberAction;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.Misc;

import java.util.Optional;

public class EnterClanBan implements EnterSyntax {

	@Override
	public void handleSyntax(Player player, String input) {
		if (input.length() > 12) {
			input = input.substring(0, 11);
		}

		if (!Misc.isValidName(input)) {
			player.getPacketSender().sendMessage("Invalid syntax entered. Please set a valid name.");
			return;
		}

		final ClanChat clanChat = player.getCurrentClanChat();

		if(clanChat != null){

			final Optional<ClanMember> optionalTarget = clanChat.findByName(input);

			optionalTarget.ifPresent(clanMember -> ClanMemberAction.BAN.execute(player, clanChat, clanMember));
		}
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
