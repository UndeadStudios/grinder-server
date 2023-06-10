package com.grinder.game.content.quest;

import com.google.gson.*;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class QuestDialogueLoader extends DefinitionLoader {

    public static final HashMap<Integer, ArrayList<Quest>> FOR_NPC = new HashMap<>();

    public static final HashMap<String, Integer> REQUIREMENTS = new HashMap<String, Integer>();

    public static final HashMap<QuestType, ArrayList<Quest>> FOR_TYPE = new HashMap<>();

    @Override
    public void load() throws Throwable {
        loadAll();
    }

    @SuppressWarnings("rawtypes")
    private void loadAll() throws InstantiationException, IllegalAccessException {
        try {
            Class[] classes = getClasses("com.grinder.game.content.quest.impl");

            long time = System.currentTimeMillis();

            for (Class c : classes) {
                if (c.isAnonymousClass()) {
                    continue;
                }
                if(c.isEnum()) {
                    continue;
                }

                Object o = c.newInstance();

                if (!(o instanceof Quest)) {
                    continue;
                }

                Quest quest = (Quest) o;

                REQUIREMENTS.put(quest.name, quest.finalStage);

                if (quest.getQuestNpcs() != null) {
                    for (int id : quest.getQuestNpcs()) {
                        FOR_NPC.computeIfAbsent(id, k -> new ArrayList<>());
                        FOR_NPC.get(id).add(quest);
                    }
                }

                QuestManager.QUESTS.put(quest.name, quest);

                QuestType type = quest.type;

                FOR_TYPE.computeIfAbsent(type, k -> new ArrayList<>());

                FOR_TYPE.get(type).add(quest);

                read((Quest) o, GameConstants.DEFINITIONS_DIRECTORY + "quest/" + quest.dialogueName + ".json");
            }

            long diff = System.currentTimeMillis() - time;

            int specialQuests = FOR_TYPE.getOrDefault(QuestType.SPECIAL, new ArrayList<>()).size();

            System.out.println("Loaded: " + QuestManager.QUESTS.size() + " quests and " + specialQuests + " special quests in " + diff + " ms.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(Quest quest, String file) throws IOException {
        File f = new File(file);
        if (!f.exists()) {
            return;
        }
        FileReader fileReader = new FileReader(file);
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(fileReader);
        Gson builder = new GsonBuilder().create();
        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);
            parse(quest, reader, builder);
        }
        fileReader.close();
    }

    private void parse(Quest quest, JsonObject reader, Gson builder) {
        final int id = reader.get("id").getAsInt();

        final int stage = reader.has("stage") ? reader.get("stage").getAsInt() : -1;
        final DialogueType type = DialogueType.valueOf(reader.get("type").getAsString());
        final DialogueExpression anim = reader.has("anim")
                ? DialogueExpression.valueOf(reader.get("anim").getAsString())
                : null;
        final int lines = reader.has("lines") ? reader.get("lines").getAsInt() : 0;
        String[] dialogueLines = new String[lines];
        if (lines > 0) {
            for (int i = 0; i < lines; i++) {
                dialogueLines[i] = reader.get("line" + (i + 1)).getAsString();
            }
        }
        final int next = reader.get("next").getAsInt();
        final int npcId = reader.has("npcId") ? reader.get("npcId").getAsInt() : -1;
        QuestDialogue dialogue = new QuestDialogue() {

            @Override
            public int getStage() {
                return stage;
            }

            @Override
            public int id() {
                return id;
            }

            @Override
            public DialogueType type() {
                return type;
            }

            @Override
            public DialogueExpression animation() {
                return anim;
            }

            @Override
            public String[] dialogue() {
                return dialogueLines;
            }

            @Override
            public int nextDialogueId() {
                return next;
            }

            @Override
            public int npcId() {
                return npcId;
            }

            @Override
            public String[] item() {
                return null;
            }
        };
        quest.dialogue.add(dialogue);
    }

    @Override
    public String file() {
        int specialQuests = FOR_TYPE.getOrDefault(QuestType.SPECIAL, new ArrayList<>()).size();
        return QuestManager.QUESTS.size() + " quests dialogues and " + specialQuests + " special quests";
    }

    public static void sendDialogue(Player player, Quest quest, int id) {
        if (id >= quest.dialogue.size()) {
            System.out.println("Invalid dialogue: " + quest.name + ", dialogue id: " + id + ". Dialogue size: " + quest.dialogue.size());
            return;
        }

        QuestDialogue dialogue = quest.dialogue.get(id);

        if (dialogue == null) {
            return;
        }

        player.getQuest().dialogue = quest;

        DialogueManager.start(player, dialogue);

        player.setDialogueOptions(quest.getDialogueOptions(player));
    }

    public static QuestDialogue getDialogueForId(Quest quest, int id) {
        for (QuestDialogue dialogue : quest.dialogue) {
            if (dialogue == null) {
                continue;
            }
            if (dialogue.id() == id) {
                return dialogue;
            }
        }
        return null;
    }

    public static boolean sendQuestDialogue(Player player, int npcId) {
        ArrayList<Quest> quests = QuestManager.getQuestForNpcs(npcId);

        if (quests == null) {
            return false;
        }

        for (Quest quest : quests) {
            for (QuestDialogue dialogue : quest.dialogue) {
                if (dialogue.npcId() == npcId) {
                    if(!quest.hasRequirements(player)) {
                        continue;
                    }
                    if (player.getQuest().tracker.getProgress(quest.name) == dialogue.getStage()) {
                        if (!quest.hasStartDialogue(player, npcId)) {
                            DialogueManager.start(player, dialogue);
                        }
                        player.setDialogueOptions(quest.getDialogueOptions(player));
                        player.getQuest().dialogue = quest;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> findClasses(File directory, String packageName) {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class
                            .forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (Throwable e) {

                }
            }
        }
        return classes;
    }
}