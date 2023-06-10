package com.grinder.game.content.miscellaneous;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Logging;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class that handles the broadcast messages above the chatbox.
 * 
 * @author Blake
 *
 */
public class Broadcast {
	
	/**
	 * The list of broadcast messages.
	 */
	private static final Map<String, String> messages = new LinkedHashMap<>();

	/**
	 * Map of messages and their expiry times.
	 */
	private static final Map<String, DateTime> messageEndTimes = new LinkedHashMap<>();
	
	/**
	 * The interface id.
	 */
	public static final int INTERFACE_ID = 35_000;
	
	/**
	 * The active broadcast string id.
	 */
	public static final int ACTIVE_BROADCAST_STRING = INTERFACE_ID + 17;
	
	/**
	 * Broadcasts a new message.
	 * 
	 * @param duration
	 *            The duration.
	 * @param msg
	 *            The message.
	 * @param link
	 *            The link.
	 */
	public static void broadcast(final Player player, final int duration, final String msg, final String link) {
		if (messages.size() > 4 || messages.containsKey(msg)) {
			return;
		}

		World.playerStream().forEach(p -> addBroadcast(p, msg, link));

		messageEndTimes.putIfAbsent(msg, DateTime.now().plusSeconds(duration));

		if(player != null) {
			Logging.log("broadcasts", player.getUsername() + ": added a broadcast with the message: " + msg);
		}

		TaskManager.submit(duration, () -> World.playerStream().forEach(p -> removeBroadcast(p, msg)));
	}

	/**
	 * Broadcasts a new message ONLY to the player himself, not a global broadcast.
	 *
	 * @param duration
	 *            The duration.
	 * @param msg
	 *            The message.
	 * @param link
	 *            The link.
	 */
	public static void broadcastSingle(final Player player, final int duration, final String msg, final String link) {
		if (messages.size() > 4 || messages.containsKey(msg)) {
			return;
		}

		player.getPacketSender().sendBroadcastMessage(msg, link);

		//player.getPacketSender().sendMessage("<col=3e541c>Server Message:</col> " + (link.isEmpty() ? "" : "<img=998>  ") + msg);

		messageEndTimes.putIfAbsent(msg, DateTime.now().plusSeconds(duration));

		TaskManager.submit(duration, () -> removeBroadcast(player, msg));
	}
	
	/**
	 * Opens the broadcast interface.
	 * 
	 * @param player
	 *            The player.
	 */
	public static void openInterface(final Player player) {
		updateInterface(player);

		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	/**
	 * Handles the interface's buttons.
	 * 
	 * @param player
	 *            The player.
	 * @param buttonId
	 *            The button.
	 * @return <code>true</code> if handled.
	 */
	public static boolean handleButton(Player player, int buttonId) {
		if (player.getInterfaceId() != INTERFACE_ID) {
			return false;
		}
		
		switch (buttonId) {
		case INTERFACE_ID + 23:
		case INTERFACE_ID + 26:
		case INTERFACE_ID + 29:
		case INTERFACE_ID + 32:
		case INTERFACE_ID + 35:
			int index = (buttonId - (INTERFACE_ID + 23)) / 3;

			String[] keys = messages.keySet().toArray(new String[0]);
			
			if (index >= keys.length) {
				return true;
			}
			
			World.playerStream().forEach(p -> removeBroadcast(p, keys[index]));
			return true;
		}
		
		return false;
	}

	/**
	 * Sends the broadcast messages when the player logs in.
	 * 
	 * @param player
	 *            The player.
	 */
	public static void onLogin(final Player player) {
		if (messages.isEmpty()) {
			return;
		}

		messages.forEach((key, value) -> {
			addBroadcast(player, key, value);
			DateTime end = messageEndTimes.getOrDefault(key, DateTime.now().plusMinutes(2));
			Seconds duration = new Duration(DateTime.now(), end).toStandardSeconds();
			int secs = Math.max(duration.getSeconds(), 300);
			TaskManager.submit(secs, () -> World.playerStream().forEach(p -> removeBroadcast(p, key)));
		});
	}

	/**
	 * Sends the broadcast message to the player.
	 * 
	 * @param player
	 *            The player.
	 * @param message
	 *            The message.
	 * @param link
	 *            The link.
	 */
	private static void addBroadcast(final Player player, final String message, final String link) {
		player.getPacketSender().sendBroadcastMessage(message, link);

		player.getPacketSender().sendMessage("<col=3e541c>Broadcast:</col> " + (link.isEmpty() ? "" : "<img=998>  ") + message);
		
		messages.put(message, link);

		//Logging.log("broadcasts", player.getUsername() + ": " + message);
		
		updateInterface(player);
	}
	
	private static void updateInterface(final Player player) {
		int stringId = ACTIVE_BROADCAST_STRING;

		for (Entry<String, String> e : messages.entrySet()) {
			player.getPacketSender().sendString(stringId++, e.getKey());
		}

		if (stringId < ACTIVE_BROADCAST_STRING + 5) {
			player.getPacketSender().clearInterfaceText(stringId, ACTIVE_BROADCAST_STRING + 5);
		}
	}

	/**
	 * Removes a broadcast message.
	 * 
	 * @param player
	 *            The player.
	 * @param message
	 *            The message.
	 */
	public static void removeBroadcast(final Player player, final String message) {
		player.getPacketSender().removeBroadcastMessage(message);

		messages.remove(message);
		messageEndTimes.remove(message);

		updateInterface(player);
	}

	/**
	 * Removes a global broadcast message.
	 *
	 * @param player
	 *            The player.
	 * @param message
	 *            The message.
	 */
	public static void removeGlobalBroadcast(final Player player, final String message) {

		World.playerStream().forEach(p -> removeBroadcast(p, message));
	}
	
}
