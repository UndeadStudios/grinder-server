package com.grinder.game.model.interfaces;

import java.util.Map;

import com.grinder.game.entity.agent.player.Player;

/**
 * Represents an in-game interface which mostly consists of text.
 * 
 * @author Blake
 */
public abstract class TextInterface {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Constructs a new {@link TextInterface}.
	 * 
	 * @param player
	 *            the player
	 */
	public TextInterface(Player player) {
		this.player = player;
	}

	/**
	 * Gets the interface's text.
	 * 
	 * @return the text
	 */
	protected abstract Map<Integer, String> getText();

	/**
	 * Sends the text on an interface.
	 * 
	 * @param tInterface
	 *            the {@link TextInterface}
	 */
	public static void send(TextInterface tInterface) {
		tInterface.getText().keySet().forEach(id -> tInterface.player.getPacketSender().sendString(id, tInterface.getText().get(id)));
	}

	/**
	 * Sends the specified text on an interface.
	 * 
	 * @param tInterface
	 *            the {@link TextInterface}
	 * @param ids
	 *            the ids
	 */
	public static void send(TextInterface tInterface, int... ids) {
		for (int i : ids) {
			tInterface.player.getPacketSender().sendString(i, tInterface.getText().get(i));
		}
	}

}
