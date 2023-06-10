package com.grinder.game.content.minigame.chamberoxeric.party.syntax;


import com.grinder.game.content.minigame.chamberoxeric.COXInterface;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

/**
 * Setting raid party preferred size
 *
 * @author 2012
 *
 */
public class SetPartySkillTotalEnterSyntax implements EnterSyntax {

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
		if (input > 2_227) {
			input = 2_227;
		}
		player.getCOX().getParty().preferSkillTotal = input;
		player.getPacketSender().sendString(COXInterface.PARTY_PREFER_SKILL_TOTAL_STRING,
				"Preferred skill total: @whi@" + input);
	}
}
