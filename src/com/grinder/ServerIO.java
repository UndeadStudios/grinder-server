package com.grinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.model.commands.impl.UpdateServerCommand;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.task.Task;
import com.grinder.util.Misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handling server settings
 * 
 * @author 2012
 *
 */
public class ServerIO {

	/**
	 * The server time
	 */
	public static final Date SERVER_TIME = new Date();

	/**
	 * The file location
	 */
	private static final String LOCATION = "./data/GrinderScape.json";

	/**
	 * The max players online
	 */
	public static int MAX_PLAYERS_ONLINE = 0;

	/**
	 * The server start time
	 */
	static long ONLINE_TIME;

	/**
	 * Loading
	 */
	public static void load() {

		PunishmentManager.load();

		Path path = Paths.get(LOCATION);
		File file = path.toFile();

		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if (!file.exists()) {
			return;
		}

		// Now read the properties from the json parser.
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);

			if(reader.has("enable_debug_messages")){
				Config.enable_debug_messages = reader.get("enable_debug_messages").getAsBoolean();
			}

			if (reader.has("max-players")) {
				MAX_PLAYERS_ONLINE = reader.get("max-players").getAsInt();
				Server.getLogger().info("MAX_PLAYERS_ONLINE: "+MAX_PLAYERS_ONLINE);
			}

			Lottery.loadTickets(reader);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saving
	 */
	public static void save() {
		Path path = Paths.get(LOCATION);
		File file = path.toFile();
		file.getParentFile().setWritable(true);

		if (!file.getParentFile().exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		final Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try {
			final FileWriter writer = new FileWriter(file);
			JsonObject object = new JsonObject();
			object.addProperty("max-players", MAX_PLAYERS_ONLINE);
			object.addProperty("enable_debug_messages", Config.enable_debug_messages);

			Lottery.saveTickets(object);

			gson.toJson(object, writer);

			writer.flush();
			writer.close();

		} catch (IOException e) {
			Server.getLogger().warn("Could not save ServerIO state", e);
		}
	}

	/**
	 * Gets the server online time
	 *
	 * @return the time
	 */
	public static String getServerOnlineTime() {
		return Misc.getTimeElapsed(ONLINE_TIME, true);
	}
}
