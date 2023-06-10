package com.grinder.game.model.passages;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.grinder.Server;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.util.oldgrinder.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class PassageManager {

    private static final Logger logger = LoggerFactory.getLogger(PassageManager.class.getName());

    private static final Path PATH = DataUtil.getDefinition("passages").toPath();

    private static final boolean PREDICT = false;
    public static final boolean WRITE_LOG = false;
    public static final int AUTO_REVERT_TIME = 100;
    public static final int STUCK_TIME = 80;
    private static final Gson GSON_WRITER = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
            .create();
    private static final Gson GSON_READER = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
            .create();

    private static final PassageMap passages = new PassageMap();

    public static void initialize() {
        try {
            load();
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("An error occurred while loading the passages from the data folder", e);
            System.exit(1);
        }
        //Defaults.load();
        PassageRequirements.init();
        //saveAll();
        Server.getLogger().info("Loaded passages.");
    }

    private static void load() throws IOException {
        final var type = new TypeToken<List<Passage>>() {
        }.getType();
        for (var path : Files.list(PATH).collect(Collectors.toList())) {
            try (var reader = Files.newBufferedReader(path)) {
                if (path.toString().endsWith(".json")) {
                    var list = GSON_READER.<List<Passage>>fromJson(reader, type);
                    for (var passage : list) {
                        passages.add(passage);
                    }
                }
            }
        }
    }

    public static void saveAll() {
        save(PassageCategory.DOOR);
        save(PassageCategory.GATE);
        save(PassageCategory.WOODEN_GATE);
        save(PassageCategory.CURTAIN);
        save(PassageCategory.TRAPDOOR);
    }

    public static void save(PassageCategory category) {
        var path = PATH.resolve(category.name().toLowerCase() + ".json");
        try (var fileWriter = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            var passages = PassageManager.passages
                    .list()
                    .stream()
                    .filter(passage -> passage.getCategory() == category)
                    .distinct()
                    .collect(Collectors.toList());
            /*if (category == PassageCategory.GATE) {
                var woodenGates = passages.stream().filter(passage -> PassageUtils.isWoodenGate(passage.getId()))
                        .collect(Collectors.toList());
                passages.removeAll(woodenGates);
                try (var fw = Files.newBufferedWriter(PATH.resolve("wooden_gates.json"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    GSON_WRITER.toJson(woodenGates, fw);
                }
            }*/
            GSON_WRITER.toJson(passages, fileWriter);
        } catch (IOException | JsonIOException e) {
            PassageManager.logger.error("Error writing " + category + ".");
            e.printStackTrace();
        }
    }

    public static boolean isForceDoorPassage(GameObject object) {
        Passage passage = passages.lookup(object.getPosition());
        return passage != null && passage.getMode() == PassageMode.FORCE && (passage.getCategory() == PassageCategory.DOOR || passage.getCategory() == PassageCategory.GATE);
    }

    public static boolean isForcePassage(GameObject object) {
        Passage passage = passages.lookup(object.getPosition());
        return passage != null && passage.getMode() == PassageMode.FORCE;
    }

    public static boolean handle(Player player, GameObject object) {
        return findOrPredict(object).map(value -> value.handle(player)).orElse(false);
    }

    public static boolean handle(Player player, GameObject object, int optionId) {
        return findOrPredict(object).map(value -> value.handle(player, optionId)).orElse(false);
    }

    public static boolean open(Player player, GameObject object) {
        return findOrPredict(object).map(value -> value.open(player)).orElse(false);
    }

    public static boolean close(Player player, GameObject object) {
        return findOrPredict(object).map(value -> value.close(player)).orElse(false);
    }

    public static Optional<Passage> find(GameObject object) {
        return Optional.ofNullable(passages.lookup(object.getPosition()));
    }

    public static Optional<Passage> findOrPredict(GameObject object) {
        if (!isPassage(object)) {
            return Optional.empty();
        }
        var passage = find(object);
//        if (passage.isEmpty()) {
//            if (PREDICT) {
//                var predictedPassage = PassageGenerator.generate(object);
//                if (predictedPassage != null) {
//                    add(predictedPassage);
//                }
//                return Optional.ofNullable(predictedPassage);
//            }
//        }
        return passage;
    }

    public static void add(Passage passage) {
        passages.add(passage);
    }

    public static Passage lookup(String name) {
        return passages.lookup(name);
    }

    public static boolean isPassage(GameObject object) {
        return determineType(object) != null;
    }

    public static boolean isDoor(GameObject object) {
        return determineType(object) == PassageCategory.DOOR;
    }

    public static boolean isGate(GameObject object) {
        return determineType(object) == PassageCategory.GATE;
    }

    public static boolean isWoodenGate(GameObject object) {
        return determineType(object) == PassageCategory.WOODEN_GATE;
    }

    public static boolean isCurtain(GameObject object) {
        return determineType(object) == PassageCategory.CURTAIN;
    }

    public static boolean isTrapdoor(GameObject object) {
        return determineType(object) == PassageCategory.TRAPDOOR;
    }

    public static PassageCategory determineType(GameObject object) {
        //this is being used for doors with  name null in them.
        Set<Integer> doorsWithNullNames = new HashSet<>(Arrays.asList(1537, 1538, 37321, 37322));
        if (doorsWithNullNames.contains(object.getId())) {
            return PassageCategory.DOOR;
        }
        if (object.getDefinition() == null || object.getDefinition().getName() == null) {
            return null;
        }
//        var category = Defaults.getCategory(object.getId());
//        if (category != null) {
//            return category;
//        }

        if (PassageUtils.isWoodenGate(object.getId())) {
            return PassageCategory.WOODEN_GATE;
        }

        var name = object.getDefinition().getName().toLowerCase();
        if (name.contains("trapdoor")) {
            return PassageCategory.TRAPDOOR;
        } else if (name.contains("door")) {
            return PassageCategory.DOOR;
        } else if (name.contains("gate")) {
            return PassageCategory.GATE;
        } else if (name.contains("curtain")) {
            return PassageCategory.CURTAIN;
        }
        return null;
    }

    public static void lookForDupes() {
        for (var originalPassage : PassageManager.getPassages().list()) {
            for (var comparePassage : PassageManager.getPassages().list()) {
                var closedPassagePosition = originalPassage.getPosition(PassageState.CLOSED);
                var openedPassagePosition = originalPassage.getPosition(PassageState.OPENED);
                if (originalPassage != comparePassage) {
                    assert closedPassagePosition != null;
                    if (closedPassagePosition.equals(comparePassage.getPosition(PassageState.CLOSED))) {
                        System.out.println("Passage Dupe at:" + closedPassagePosition);
                    }
                    assert openedPassagePosition != null;
                    if (openedPassagePosition.equals(comparePassage.getPosition(PassageState.OPENED))) {
                        System.out.println("Passage Dupe at:" + openedPassagePosition);
                        continue;
                    }

                    if (originalPassage.getAttachment() != null) {
                        if (comparePassage.getAttachment() != null) {
                            var closedAttachmentPosition = originalPassage.getAttachment().getPosition(PassageState.CLOSED);
                            var openedAttachmentPosition = originalPassage.getAttachment().getPosition(PassageState.OPENED);
                            assert closedAttachmentPosition != null;
                            if (closedAttachmentPosition.equals(comparePassage.getAttachment().getPosition(PassageState.CLOSED))) {
                                System.err.println("Passage Dupe at:" + closedAttachmentPosition);
                                continue;
                            }
                            assert openedAttachmentPosition != null;
                            if (openedAttachmentPosition.equals(comparePassage.getAttachment().getPosition(PassageState.OPENED))) {
                                System.err.println("Passage Dupe at:" + openedAttachmentPosition);
                            }
                        }
                    }
                }
            }
        }
    }

    public static PassageMap getPassages() {
        return passages;
    }
}
