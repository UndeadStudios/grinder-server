package com.grinder.game.model.interfaces.dialogue.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.Dialogue;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

public abstract class ConfirmDialogue extends Dialogue {

	private String[] confirm;
	
	private String[] option;

	public ConfirmDialogue(Player player, String option[]) {
		this(player, null, option);
	}

	public ConfirmDialogue(Player player, String confirm[], String[] option) {
		this.confirm = confirm;
		this.option = option;
		
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				player.getPacketSender().sendInterfaceRemoval();
				switch (option) {
				case 1:
					onConfirm();
					break;
				}
			}
		});
	}

	@Override
	public DialogueType type() {
		return confirm == null ? DialogueType.OPTION : DialogueType.STATEMENT;
	}

	@Override
	public Dialogue nextDialogue() {
		if (confirm == null) {
			return null;
		}
		
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return option;
			}
		};
	}

	@Override
	public DialogueExpression animation() {
		return null;
	}

	@Override
	public String[] dialogue() {
		return confirm == null ? option : confirm;
	}

	public abstract void onConfirm();

}
