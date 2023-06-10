package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.miscellaneous.Notes;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class AddNote implements EnterSyntax {

	@Override
	public void handleSyntax(Player player, String input) {
		if (input.length() > 12) {
			input = input.substring(0, 11);
		}
		
		Notes.addNote(player, input);
		AchievementManager.processFor(AchievementType.IDEA_NOTED, player);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}

}
