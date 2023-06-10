package com.grinder.game.model.interfaces.dialogue.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.model.interfaces.dialogue.Dialogue;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

import java.util.stream.Stream;

/**
 * Represents a dialogue of possible options.
 * 
 * @author Pb600
 * 
 */
public class DialogueChat extends Dialogue {

	private String title;
	private int totalLines;
	private String firstLine;
	private String secondLine;
	private String thirdLine;
	private String fourthLine;
	private String fifthLine;
	private DialogueExpression animation;
	private int mediaID;
	private int zoom = 200;
	private boolean npcChat;
	private DialogueType dialogueType;
	private boolean overlayChat;

	public DialogueChat() {
		setTitle("");
		setTotalLines(1);
		setFirstLine("");
		setSecondLine("");
		setThirdLine("");
		setFourthLine("");
		setFifthLine("");
		setAnimation(DialogueExpression.HAPPY);

		setDialogueType(DialogueType.PLAYER_STATEMENT);
		setMediaID(-1);
	}

	public DialogueChat(String... lines) {
		this();
		int totalLines = lines.length > 5 ? 5 : lines.length;
		setTotalLines(totalLines);
		for (int line = 0; line < lines.length; line++) {
			switch (line) {
				case 0 :
					setFirstLine(lines[line]);
					break;
				case 1 :
					setSecondLine(lines[line]);
					break;
				case 2 :
					setThirdLine(lines[line]);
					break;
				case 3 :
					setFourthLine(lines[line]);
					break;
				case 4 :
					setFifthLine(lines[line]);
					break;
			}
		}
	}

	public DialogueChat(String firstLine) {
		setTitle("");
		setTotalLines(1);
		setFirstLine(firstLine);
		setSecondLine("");
		setThirdLine("");
		setFourthLine("");
		setFifthLine("");
		setAnimation(DialogueExpression.DEFAULT);
		setDialogueType(DialogueType.PLAYER_STATEMENT);
	}

	public DialogueChat(String firstLine, String secondLine) {
		setTitle("");
		setTotalLines(2);
		setFirstLine(firstLine);
		setSecondLine(secondLine);
		setThirdLine("");
		setFourthLine("");
		setFifthLine("");
		setAnimation(DialogueExpression.DEFAULT);
		setDialogueType(DialogueType.PLAYER_STATEMENT);
	}

	public DialogueChat(String firstLine, String secondLine, String thirthLine) {
		setTitle("");
		setTotalLines(3);
		setFirstLine(firstLine);
		setSecondLine(secondLine);
		setThirdLine(thirthLine);
		setFourthLine("");
		setFifthLine("");
		setAnimation(DialogueExpression.DEFAULT);
		setDialogueType(DialogueType.PLAYER_STATEMENT);
	}

	public DialogueChat(String firstLine, String secondLine, String thirthLine, String fourthLine) {
		setTitle("");
		setTotalLines(4);
		setFirstLine(firstLine);
		setSecondLine(secondLine);
		setThirdLine(thirthLine);
		setFourthLine(fourthLine);
		setFifthLine("");
		setAnimation(DialogueExpression.DEFAULT);
		setDialogueType(DialogueType.PLAYER_STATEMENT);
	}

	public DialogueChat(String firstLine, String secondLine, String thirthLine, String fourthLine, String fifthLine) {
		setTitle("");
		setTotalLines(4);
		setFirstLine(firstLine);
		setSecondLine(secondLine);
		setThirdLine(thirthLine);
		setFourthLine(fourthLine);
		setFifthLine(fifthLine);
		setAnimation(DialogueExpression.DEFAULT);
		setDialogueType(DialogueType.PLAYER_STATEMENT);
	}

	public String getFourthLine() {
		return fourthLine;
	}

	public DialogueChat setFourthLine(String fourthOption) {
		this.fourthLine = fourthOption;
		if (!fourthOption.isEmpty() && totalLines < 4) {
			setTotalLines(4);
		}
		return this;
	}

	public String getThirdLine() {
		return thirdLine;
	}

	public DialogueChat setThirdLine(String thirdOption) {
		this.thirdLine = thirdOption;
		if (!thirdOption.isEmpty() && totalLines < 3) {
			setTotalLines(3);
		}
		return this;
	}

	public String getSecondLine() {
		return secondLine;
	}

	public DialogueChat setSecondLine(String secondOption) {
		this.secondLine = secondOption;
		if (!secondLine.isEmpty() && totalLines < 2) {
			setTotalLines(2);
		}
		return this;
	}

	public String getFirstLine() {
		return firstLine;
	}

	public DialogueChat setFirstLine(String firstOption) {
		this.firstLine = firstOption;
		return this;
	}

	public int getTotalLines() {
		return totalLines;
	}

	private void setTotalLines(int totalOptions) {
		this.totalLines = totalOptions;
	}

	public String getTitle() {
		return title;
	}

	public DialogueChat setTitle(String title) {
		this.title = title;
		return this;
	}

	public DialogueExpression getAnimation() {
		return animation;
	}

	public DialogueChat setAnimation(DialogueExpression animation) {
		this.animation = animation;
		return this;
	}

	public DialogueChat setMedia(int mediaID, DialogueExpression animation, int zoom) {
		this.mediaID = mediaID;
		this.animation = animation;
		this.zoom = zoom;
		this.dialogueType = DialogueType.ITEM_STATEMENT;
		return this;
	}

	public DialogueChat setItem(int itemID, DialogueExpression animation, int zoom) {
		this.mediaID = itemID;
		this.animation = animation;
		this.zoom = zoom;
		setItemDialogue();
		return this;
	}

	public DialogueChat setNPC(int headID, String npcName) {
		setMediaID(headID);
		setTitle(npcName);
		setNPCDialogue();
		setHeadID(headID);
		setZoom(200);
		return this;
	}

	public DialogueChat setHeadID(int headID) {
		setMediaID(headID);
		if (headID > 0) {
			setNpcChat(true);
		} else {
			setNpcChat(false);
		}
		return this;
	}

	public boolean isNpcChat() {
		return npcChat;
	}

	public DialogueChat setNpcChat(boolean npcChat) {
		this.npcChat = npcChat;
		return this;
	}

	public DialogueType getDialogueType() {
		return dialogueType;
	}

	public DialogueChat setDialogueType(DialogueType dialogueType) {
		this.dialogueType = dialogueType;
		return this;
	}

	public DialogueChat setPlayerDialogue() {
		this.dialogueType = DialogueType.PLAYER_STATEMENT;
		return this;
	}

	public DialogueChat setNPCDialogue() {
		this.dialogueType = DialogueType.NPC_STATEMENT;
		return this;
	}

	public DialogueChat setItemDialogue() {
		this.dialogueType = DialogueType.ITEM_STATEMENT;
		return this;
	}

	public DialogueChat setStatementDialogue() {
		this.dialogueType = DialogueType.STATEMENT;
		return this;
	}

	public String getFifthLine() {
		return fifthLine;
	}

	public DialogueChat setFifthLine(String fifthLine) {
		this.fifthLine = fifthLine;
		if (!fifthLine.isEmpty() && totalLines < 5) {
			setTotalLines(5);
		}
		return this;
	}

	public boolean isOverlayChat() {
		return overlayChat;
	}

	public DialogueChat setOverlayChat(boolean overlayChat) {
		this.overlayChat = overlayChat;
		return this;
	}

	public DialogueChat setZoom(int zoom) {
		this.zoom = zoom;
		return this;
	}

	public int getZoom() {
		return zoom;
	}

	public int getMediaID() {
		return mediaID;
	}

	public DialogueChat setMediaID(int mediaID) {
		this.mediaID = mediaID;
		return this;
	}

	@Override
	public DialogueType type() {
		return dialogueType;
	}

	@Override
	public DialogueExpression animation() {
		return animation;
	}

	@Override
	public String[] dialogue() {
		return Stream.of(firstLine, secondLine, thirdLine, fourthLine, fifthLine)
				.filter(string -> !string.isEmpty())
				.toArray(String[]::new);
	}

	@Override
	public int npcId() {
		return !npcChat ? -1 : mediaID;
	}

	@Override
	public String[] item() {

		if(dialogueType != DialogueType.ITEM_STATEMENT)
			return null;

		if(mediaID <= 0)
			return null;

		final ItemDefinition definition = ItemDefinition.forId(mediaID);
		final String name = definition == null ? "" : definition.getName();

		return dialogueType != DialogueType.ITEM_STATEMENT ? null : new String[]{String.valueOf(mediaID), String.valueOf(zoom), name};
	}
}