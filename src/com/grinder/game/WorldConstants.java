package com.grinder.game;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds world-related constants.
 *
 * @author Graham
 */
public final class WorldConstants {

    public static final long REVISION = 180;

    /**
     * The maximum number of npcs.
     */
    public static final int MAXIMUM_NPCS = 32766;

    /**
     * The maximum number of players.
     */
    public static final int MAXIMUM_PLAYERS = 350;

    /**
     * The {@link Path} to the oldschool cache files.
     */
    public static final Path OLDSCHOOL_STORE_PATH = Paths.get("./data", "oldschool");

    /**
     * Default private constructor to prevent instantiation by other classes.
     */
    private WorldConstants() {

    }

}