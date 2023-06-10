package com.grinder.game.content.miscellaneous;

import java.util.ArrayList;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.syntax.impl.AddNote;

/**
 * Handles notes
 * 
 * @author 2012
 *
 */
public class Notes {

	static {
		ButtonActions.INSTANCE.onClick(32006, clickAction -> clearAll(clickAction.getPlayer()));
		ButtonActions.INSTANCE.onClick(32003, clickAction -> addNote(clickAction.getPlayer()));
	}

	/**
	 * The start interface id
	 */
	public static final int START_ID = 32010;

	/**
	 * The finish interface id
	 */
	public static final int FINISH_ID = 32110;

	/**
	 * The max strings
	 */
	private static final int MAX = 100;

	/**
	 * The notes
	 */
	private ArrayList<String> notes = new ArrayList<String>();

	/**
	 * Displaying the notes in the tab
	 * 
	 * @param player
	 *            the player
	 */
	public static void display(Player player) {
		int id = START_ID;
		player.getPacketSender().clearInterfaceText(START_ID, FINISH_ID);
		for (String s : player.getNotes().getNotes()) {
			player.getPacketSender().sendString(id, s);
			id++;
		}
	}

	/**
	 * The notes
	 * 
	 * @param player
	 *            the player
	 */
	public static void addNote(Player player) {
		player.setEnterSyntax(new AddNote());
		player.getPacketSender().sendEnterInputPrompt("Write a note to add..");
	}
	

	/**
	 * Adds the notes
	 * 
	 * @param player
	 *            the player
	 * @param string
	 *            the string
	 */
	public static void addNote(Player player, String string) {
		if (player.getNotes().getNotes().size() >= MAX) {
			player.getPacketSender().sendMessage(
					"You have reached the max " + MAX + " notes and can't add anymore. Remove some first.");
			return;
		}
		player.getPacketSender().sendMessage("Note added: @or2@" + string);
		player.getNotes().getNotes().add(string);
		display(player);
	}

	/**
	 * Clearing notes
	 * 
	 * @param player
	 *            the player
	 * @param id
	 *            the id
	 */
	public static void clearNote(Player player, int id) {
		int slot = id - START_ID;
		if (slot > player.getNotes().getNotes().size()) {
			return;
		}
		player.getNotes().getNotes().remove(slot);
		player.getNotes().getNotes().trimToSize();
		display(player);
	}

	public static boolean handleButton(final Player player, int buttonId){
		if (buttonId >= Notes.START_ID && buttonId <= Notes.FINISH_ID) {
			clearNote(player, buttonId);
			return true;
		}
		return false;
	}

	/**
	 * Clearing all notes
	 * 
	 * @param player
	 *            the player
	 */
	public static void clearAll(Player player) {
		if(player.getNotes().getNotes().size() == 0) {
			player.getPacketSender().sendMessage("Your note tab is clear already.");
			return;
		}
		
		DialogueManager.start(player, 2510);
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				if (option == 1) {
					player.getPacketSender().sendMessage("All of your notes have been deleted");
					player.getNotes().getNotes().clear();
					display(player);
				}
				player.getPacketSender().sendInterfaceRemoval();
			}
		});
	}

	/**
	 * Sets the notes
	 *
	 * @return the notes
	 */
	public ArrayList<String> getNotes() {
		return notes;
	}

	/**
	 * Sets the notes
	 * 
	 * @param notes
	 *            the notes
	 */
	public void setNotes(ArrayList<String> notes) {
		this.notes = notes;
	}
}