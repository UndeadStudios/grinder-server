package com.grinder.game.content.minigame.chamberoxeric.party.syntax;

import com.grinder.game.content.minigame.chamberoxeric.COXInterface;
import com.grinder.game.content.minigame.chamberoxeric.party.COXParty;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

/**
 * Setting raid party preferred size
 *
 * @author 2012
 *
 */
public class SetPartySizeEnterSyntax implements EnterSyntax {

	@Override
	public void handleSyntax(final Player player, final String input) {

	}

	@Override
	public void handleSyntax(final Player player, int input) {
		if (player.getCOX().getParty() == null) {
			return;
		}
		if (input < 0) {
			input = 1;
		}
		if (input > COXParty.MAX_SIZE) {
			input = COXParty.MAX_SIZE;
		}
		player.getCOX().getParty().preferSize = input;
		player.getPacketSender().sendString(COXInterface.PARTY_PREFER_SIZE_STRING,
				"Preferred party size: @whi@" + input);
	}
}
