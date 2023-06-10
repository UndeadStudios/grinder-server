package com.grinder.game;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grinder.Server;
import com.grinder.util.benchmark.SimpleBenchMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * The engine which processes the game.
 *
 * @author Professor Oak
 */
public final class GameEngine implements Runnable {

    private static final ThreadFactory THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setNameFormat("GameThread").build();
    private static final Logger LOGGER = LogManager.getLogger(GameEngine.class);
    private static final SimpleBenchMarker BENCH_MARKER = new SimpleBenchMarker();

    /**
     * Initializes this {@link GameEngine}.
     */
    public void init() {

        LOGGER.info("Starting game engine, cycling every "+GameConstants.WORLD_CYCLE_PERIOD +" ms");

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY_BUILDER);
        final ScheduledFuture<?> handle = executorService.scheduleAtFixedRate(this, 0,
                GameConstants.WORLD_CYCLE_PERIOD,
                TimeUnit.MILLISECONDS);

        final Thread exceptionHandlerThread = new Thread(() -> {
            try {
                handle.get();
            } catch (ExecutionException | InterruptedException e){
                LOGGER.error("Exception occurred during world processing -> "+e.getLocalizedMessage(), e);
            }
        });

        exceptionHandlerThread.start();
    }

    @Override
    public void run() {

        if(!Server.GAME_BUILDER.isFinished())
            return;

        BENCH_MARKER.start();

        World.INSTANCE.cycle(BENCH_MARKER);

        BENCH_MARKER.println("World cycle", 300, LOGGER);
    }
}