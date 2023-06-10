package com.grinder;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.common.base.Stopwatch;
import com.grinder.game.GameBuilder;
import com.grinder.game.GameConstants;
import com.grinder.game.content.miscellaneous.PlayerTitles;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMasterTask;
import com.grinder.game.model.commands.impl.UpdateServerCommand;
import com.grinder.game.task.Task;
import com.grinder.net.NetworkBuilder;
import com.grinder.net.NetworkConstants;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.util.DiscordBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The starting point of the application. Initializes the bootstrap.
 *
 * @author Professor Oak
 * @author Lare96
 * @author Stan van der Bend
 */
public final class Server {

	/**
	 * The logger that will print important information.
	 */
	private static final Logger logger = LogManager.getLogger(Server.class.getSimpleName());

	public static final GameBuilder GAME_BUILDER = new GameBuilder();

	/**
	 * checks if the server is loaded
	 */
	public static AtomicBoolean loaded = new AtomicBoolean(false);

	/**
	 * The flag that determines if the server is currently being updated or not.
	 */
	public static AtomicBoolean updating = new AtomicBoolean(false);

	/**
	 * The flag that determines if the server {@link UpdateServerCommand}
	 * submitted {@link Task update task} has finished.
	 */
	public static AtomicBoolean updatingCompleted = new AtomicBoolean(false);

	/**
	 * The main method that will put the server online.
	 */
	public static void main(String[] args) {

		//PropertyConfigurator.configure("log4j.properties");

		final Stopwatch stopwatch = Stopwatch.createStarted();

		try {

			if (args.length == 1) {
				Config.PRODUCTION = Integer.parseInt(args[0]) == 1;
			}

			ServerIO.ONLINE_TIME = System.currentTimeMillis();

			logger.info("Initializing " + GameConstants.NAME + " in " + (Config.PRODUCTION ? "production" : "non-production") + " mode..");

			GAME_BUILDER.initialize();

			final NetworkBuilder networkBuilder = new NetworkBuilder();

			networkBuilder.initialize();

			final SocketAddress game = new InetSocketAddress(NetworkConstants.GAME_PORT);
			final SocketAddress web = new InetSocketAddress(NetworkConstants.WEB_PORT);

			networkBuilder.bind(game, web);

			SlayerMasterTask.initializeTasks();
			PlayerTitles.PlayerTitle.load();

			logger.info("Loading server settings..");

			ServerIO.load();

			logger.info("Connecting to database..");
			
			if(GameConstants.MYSQL_ENABLED)
				SQLManager.Companion.getINSTANCE().init();

			logger.info("Connecting to discord bot..");
			DiscordBot.init();

			logger.info(GameConstants.NAME + " is now online!");

			loaded.set(true);
		} catch (Exception e) {
			logger.error( "An error occurred while binding the Bootstrap!", e);
			System.exit(1);
		}

		logger.info("Starting "+GameConstants.NAME+" took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
	}

	public static Logger getLogger() {
		return logger;
	}

	public static boolean isUpdating() {
		return updating.get();
	}

	public static void setUpdating(boolean isUpdating) {
		updating.set(isUpdating);
	}

	public static volatile ChatterBotFactory factory;

	public static ChatterBotFactory getFactory() {
		if (factory == null)
			factory = new ChatterBotFactory();
		return factory;
	}

	public static ChatterBotSession botSession;
}