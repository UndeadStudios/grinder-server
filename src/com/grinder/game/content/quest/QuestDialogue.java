package com.grinder.game.content.quest;


import com.grinder.game.model.interfaces.dialogue.Dialogue;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

/**
 * 
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public abstract class QuestDialogue extends Dialogue {

	public abstract int getStage();

	@Override
	public DialogueType type() {
		return null;
	}

	@Override
	public DialogueExpression animation() {
		return null;
	}

	@Override
	public String[] dialogue() {
		return null;
	}
}
