package com.grinder.util;

import com.grinder.game.GameConstants;
import com.grinder.util.io.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * TODO: redo this
 */
public class PlayerFlagsLogger {

    private static final Logger LOGGER = LogManager.getLogger(PlayerFlagsLogger.class.getSimpleName());
    private static final Path FLAGGED_ACCOUNTS_PATH = Paths.get(GameConstants.SAVES_DIRECTORY, "Flagged.txt");
    private static final ArrayList<String> FLAGGED_ACCOUNT_LIST = new ArrayList<>();

    public static void load() {
        FLAGGED_ACCOUNT_LIST.clear();

        try {
            initializeList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeList() throws IOException {

        final File file = FLAGGED_ACCOUNTS_PATH.toFile();

        if(!file.exists()){
            if(file.createNewFile()){
                LOGGER.info("Created a new file at '"+FLAGGED_ACCOUNTS_PATH+"'");
            }
        }

        final BufferedReader reader = new BufferedReader(new FileReader(file));

        String data;

        while ((data = reader.readLine()) != null) {
            PlayerFlagsLogger.FLAGGED_ACCOUNT_LIST.add(data);
        }

        reader.close();
    }
    
    public static void flag(String playerName) {

        playerName = Misc.formatPlayerName(playerName.toLowerCase());

        if (!FLAGGED_ACCOUNT_LIST.contains(playerName))
            FileUtil.appendToFile(FLAGGED_ACCOUNTS_PATH.toString(), playerName, true);

        FLAGGED_ACCOUNT_LIST.add(playerName);

        load();
    }
}
