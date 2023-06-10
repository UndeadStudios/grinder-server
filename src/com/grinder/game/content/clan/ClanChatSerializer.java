package com.grinder.game.content.clan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.json.SecondsTimerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class represents a serializer for {@link ClanChat} instances.
 *
 * @author Stan van der Bend
 * @since 20-3-19
 */
public class ClanChatSerializer {

    private final static Logger LOGGER = LogManager.getLogger(ClanChatSerializer.class.getSimpleName());
    private final static Path savePath = Paths.get("data", "saves", "clans");
    private final static Type type = new TypeToken<ClanChat>(){}.getType();
    private final static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(SecondsTimer.class, new SecondsTimerAdapter())
            .setPrettyPrinting()
            .create();

    public static void save(ClanChat chat) {

        final File file = savePath.resolve(chat.getName() + ".json").toFile();

        try {

            if (file.createNewFile())
                LOGGER.info("Created a new file for " + chat + "");

            final FileWriter writer = new FileWriter(file);

            gson.toJson(chat, type, writer);

            writer.flush();
            writer.close();

            LOGGER.debug("Successfully saved " + chat + " to file " + file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stream<ClanChat> loadClans() throws IOException {
        return Files.list(savePath).map(filePath -> {
            try {
                return new FileReader(filePath.toFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).map(reader -> gson.fromJson(reader, ClanChat.class));
    }

}
