package com.grinder.game.definition.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An abstract class which handles the loading
 * of some sort of definition-related file.
 *
 * @author Professor Oak
 */
public abstract class DefinitionLoader implements Runnable {

    private final Logger logger = LogManager.getLogger(getClass().getSimpleName());

    public abstract void load() throws Throwable;

    public abstract String file();

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            load();
            long elapsed = System.currentTimeMillis() - start;
            logger.info("Loaded "+file()+" in " + elapsed + " ms.");
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("Failed to load " + file(), e);
        }
    }
}
