package com.grinder.game.entity.agent.player.bot;

import com.grinder.game.entity.agent.player.bot.script.BotScript;
import com.grinder.game.entity.agent.player.bot.script.BotScriptRepository;
import com.grinder.game.model.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BotManager {

	private final static Logger LOGGER = LogManager.getLogger(BotManager.class.getSimpleName());

	/**
	 * A hash collection of bot names.
	 */
	private static final Set<String> BOT_NAMES = new HashSet<>();

	/**
	 * Gets the bot names.
	 * 
	 * @return The bot names.
	 */
	public static Set<String> getBotNames() {
		return BOT_NAMES;
	}

	/**
	 * A hash collection of the currently active scripts.
	 */
	private static final Set<BotScript> ACTIVE_SCRIPTS = ConcurrentHashMap.newKeySet();

	public static void addBot(String script, String username, Position position) {
		final BotPlayer newBot = new BotPlayer(username, position);

		startScript(newBot, script.toLowerCase() + "script");

		BOT_NAMES.add(username);
	}

	/**
	 * Processes the active scripts.
	 */
	public static void process() {

		for (Iterator<BotScript> it = ACTIVE_SCRIPTS.iterator(); it.hasNext();) {

			final BotScript script = it.next();
			final BotPlayer botPlayer = script.getPlayer();

			try {

				if(botPlayer == null || !botPlayer.isRegistered())
					script.stop();

				if (script.stopped()) {
					script.onStop();
					it.remove();
					continue;
				}

				if (System.currentTimeMillis() - script.getLastAction() < script.getActionDelay())
					continue;

				script.execute();

			} catch (Exception e){
				LOGGER.error("Failed processing bot script '"+script.getClass().getSimpleName()+"' for bot ["+botPlayer+"]", e);
			}
		}
	}

	/**
	 * Executes the specified {@link BotScript} for the specified
	 * {@link BotPlayer}.
	 */
	public static void startScript(BotPlayer bot, String scriptIdentifier) {

		try {
			final BotScript requestedScript = BotScriptRepository.getScripts().get(scriptIdentifier);

			if(requestedScript == null){
				LOGGER.error("Could not start bot script '"+scriptIdentifier+"' for bot ["+bot+"]");
				return;
			}

			final BotScript scriptInstance = requestedScript.getClass().getDeclaredConstructor().newInstance();

			bot.setActiveScript(scriptInstance);

			scriptInstance.setPlayer(bot);
			scriptInstance.initialize();
			ACTIVE_SCRIPTS.add(scriptInstance);

		} catch (Exception e) {
			LOGGER.error("Could not start bot script '"+scriptIdentifier+"' for bot ["+bot+"]", e);
		}
	}

	/**
	 * Stops the specified {@link BotScript}.
	 * 
	 * @param script
	 *            The script.
	 */
	public static void stopScript(BotScript script) {
		ACTIVE_SCRIPTS.remove(script);
	}

}
