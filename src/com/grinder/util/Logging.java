package com.grinder.util;

import com.grinder.game.World;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.service.logging.LoggingEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Used for logging various in-game data.
 *
 * @author Blake
 */
public class Logging {
	
	/**
	 * The time format.
	 */
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * The date format.
	 */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * The log file path.
	 */
	public static String PATH = "./data/logs/";

	public static String SUFFIX = ".log";

	/**
	 * Logs the data into a file.
	 * 
	 * @param folder
	 *            The folder of the log file.
	 * @param data
	 *            The attached data.
	 */
	public static void log(String folder, String data) {
		try {
			ServiceManager.INSTANCE
					.getLoggingService()
					.addLogEntry(new LoggingEntry(folder, data));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
