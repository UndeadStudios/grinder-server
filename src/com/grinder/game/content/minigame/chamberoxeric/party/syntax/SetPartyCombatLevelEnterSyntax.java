package com.grinder.game.content.minigame.chamberoxeric.party.syntax;

import com.grinder.game.content.minigame.chamberoxeric.COXInterface;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class SetPartyCombatLevelEnterSyntax implements EnterSyntax {

	@Override
	public void handleSyntax(final Player player, final String input) {

	}

	@Override
	public void handleSyntax(final Player player, int input) {
		if (player.getCOX().getParty() == null) {
			return;
		}
		if (input < 0) {
			input = 3;
		}
		if (input > 255) {
			input = 255;
		}
		player.getCOX().getParty().preferCombatlevel = input;
		player.getPacketSender().sendString(COXInterface.PARTY_PREFER_COMBAT_LEVEL_STRING,
				"Preferred combat level: @whi@" + input);
	}
}
