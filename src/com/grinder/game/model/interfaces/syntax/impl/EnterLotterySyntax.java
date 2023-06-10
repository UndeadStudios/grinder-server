package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

/**
 * Entering the lottery
 * 
 * @author 2012
 *
 */
public class EnterLotterySyntax implements EnterSyntax {

	@Override
	public void handleSyntax(Player player, String input) {

	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(input < 1)
			return;

		Lottery.purchaseTickets(player, input);
	}
}