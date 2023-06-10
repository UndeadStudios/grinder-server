package com.grinder.game;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.item.StarterPack;
import com.grinder.game.content.miscellaneous.daily.DailyLoginRewardManager;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.definition.loader.NpcStatsDefinitionLoader;
import com.grinder.game.definition.loader.impl.*;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.content.cluescroll.task.ClueTaskFactory;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.model.passages.PassageManager;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.AnnouncementTask;
import com.grinder.game.task.impl.ItemOnGroundSequenceTask;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.BackgroundLoader;
import com.grinder.util.PlayerFlagsLogger;
import com.grinder.util.ServerClassPreLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Loads all required necessities and starts processes required for the game to
 * work.
 *
 * @author Lare96
 */
public final class GameBuilder {

	private final Logger logger = LogManager.getLogger(getClass().getSimpleName());

	/**
	 * The background loader that will load various utilities in the background
	 * while the bootstrap is preparing the server.
	 */
	private final BackgroundLoader backgroundLoader = new BackgroundLoader();
	private boolean finished = false;

	/**
	 * Initializes this game builder effectively preparing the background
	 * startup tasks and game processing.
	 *
	 * @throws Exception
	 *             if any issues occur while starting the network.
	 */
	public void initialize() throws Exception {

		ServiceManager.INSTANCE.init();

		// Start game engine..
		new GameEngine().init();

		World.INSTANCE.initFileStore();

		// Start immediate tasks..
		CollisionManager.init();

		if(Paths.get(GameConstants.SAVES_DIRECTORY).toFile().mkdir())
			logger.info("Created saves directory at '"+GameConstants.SAVES_DIRECTORY+"'");

		// Start background tasks..
		backgroundLoader.init(createBackgroundTasks());

		// Start global tasks..
		TaskManager.submit(new ItemOnGroundSequenceTask());
		TaskManager.submit(new AnnouncementTask());
		MinigameManager.init();

		// Make sure the background tasks loaded properly..
		if (!backgroundLoader.awaitCompletion())
			throw new IllegalStateException("Background load did not complete normally!");

		postLoad();
		ServerClassPreLoader.INSTANCE.preloadClasses();
		ServiceManager.INSTANCE.postLoad();

		finished = true;
	}

	private void postLoad() {
		try {
			AreaManager.load();
			new NpcSpawnDefinitionLoader().run();
			new ItemOnGroundDefinitionLoader().run();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * Returns a queue containing all of the background tasks that will be
	 * executed by the background loader. Please note that the loader may use
	 * multiple threads to load the utilities concurrently, so utilities that
	 * depend on each other <b>must</b> be executed in the same task to ensure
	 * thread safety.
	 *
	 * @return the queue of background tasks.
	 */
	private Queue<Runnable> createBackgroundTasks() {

		final Queue<Runnable> tasks = new ArrayDeque<>();

		tasks.add(GlobalClanChatManager::init);
		tasks.add(PoisonEffect::init);
		tasks.add(PlayerFlagsLogger::load);
		tasks.add(StarterPack::init);
		tasks.add(DailyLoginRewardManager::init);
		tasks.add(ClueTaskFactory.getInstance()::initialize);

		// Load definitions..
		tasks.add(new ObjectSpawnDefinitionLoader());
		tasks.add(new ItemDefinitionLoader());
		tasks.add(new ItemValueDefinitionLoader());
		tasks.add(new ShopDefinitionLoader());
		tasks.add(new DialogueDefinitionLoader());
		tasks.add(new NpcDefinitionLoader());
		tasks.add(new NpcStatsDefinitionLoader());
		tasks.add(new NpcDropDefinitionLoader());
		tasks.add(new PresetDefinitionLoader());
		tasks.add(new QuestDialogueLoader());
		tasks.add(PacketInteractionManager::init);
		tasks.add(PassageManager::initialize);
		return tasks;
	}

	public boolean isFinished() {
		return finished;
	}
}
