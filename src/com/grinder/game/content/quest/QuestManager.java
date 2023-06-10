package com.grinder.game.content.quest;

import com.grinder.game.World;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.content.quest.impl.DruidicRitual;
import com.grinder.game.content.quest.impl.PiratesTreasure;
import com.grinder.game.content.quest.impl.WaterfallQuest;
import com.grinder.game.content.quest.impl.WitchsPotion;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.progresstracker.ProgressTracker;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class QuestManager extends PacketInteraction {

    public static final int QUEST_LINE = 65_211;

    private static final int SPECIAL_LINE = 65_233;

    public static final HashMap<String, Quest> QUESTS = new HashMap<>();

    public static final WitchsPotion WITCHS_POTION = new WitchsPotion();

    public static final DruidicRitual DRUIDIC_RITUAL = new DruidicRitual();

    public static final WaterfallQuest WATERFALL_QUEST = new WaterfallQuest();

    public static final PiratesTreasure PIRATES_TREASURE = new PiratesTreasure();

    public ArrayList<Position> fixedRailings = new ArrayList<>();

    public ArrayList<NPC> spawnedNpcs = new ArrayList<>();

    public boolean spawnedTreeSpirit;

    public int rohakDrunkness;

    public ProgressTracker tracker;

    public Quest dialogue;

    private static final int[] STRING_IDS = {8145, 8147, 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157,
            8158, 8159, 8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175,
            8176, 8177, 8178, 8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193,
            8194, 8195, 12174, 12175, 12176, 12177, 12178, 12179, 12180, 12181, 12182, 12183, 12184, 12185, 12186,
            12187, 12188, 12189, 12190, 12191, 12192, 12193, 12194, 12195, 12196, 12197, 12198, 12199, 12200, 12201,
            12202, 12203, 12204, 12205, 12206, 12207, 12208, 12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216,
            12217, 12218, 12219, 12220, 12221, 12222, 12223};

    public static final int[] JINGEBIT_MUSIC_IDS = {153, 154, 35, 166, 3, 204, 205};

    public QuestManager() {

    }

    public QuestManager(Player player) {
        this.tracker = new ProgressTracker() {
            public void update() {

                int free = QUEST_LINE;

                for (Quest quest : QuestDialogueLoader.FOR_TYPE.get(QuestType.FREE)) {
                    sendQuestColour(player, quest, free);
                    free++;
                }

                int special = SPECIAL_LINE;

                for (Quest quest : QuestDialogueLoader.FOR_TYPE.get(QuestType.SPECIAL)) {
                    sendQuestColour(player, quest, special);
                    special++;
                }
            }

            public HashMap<String, Integer> getRequirement() {
                return QuestDialogueLoader.REQUIREMENTS;
            }
        };
    }

    public static void sendTab(Player player) {
        player.getQuest().tracker.update();
    }

    public static boolean display(final Player player, final int button) {
        QuestType type = QuestType.FREE;

        if (button >= SPECIAL_LINE) {
            type = QuestType.SPECIAL;
        }

        ArrayList<Quest> quests = QuestDialogueLoader.FOR_TYPE.get(type);

        int line = button >= SPECIAL_LINE ? SPECIAL_LINE : QUEST_LINE;

        int index = button - line;

        if (index >= quests.size() || index < 0) {
            return false;
        }

        Quest quest = quests.get(index);

        sendQuestDisplay(player, quest);
        return true;
    }

    public static void sendQuestDisplay(Player player, Quest quest) {

        if (quest == null) {
            return;
        }

        for (int i = 0; i < STRING_IDS.length; i++) {
            player.getPacketSender().sendString(STRING_IDS[i], "");
        }

        int stage = getStage(player, quest.name);

        if (stage > quest.getDescription(player).length) {
            stage = -1;
        }

        int line = 0;

        if (quest.getDescription(player) != null) {
            for (int i = 0; i <= stage; i++) {
                for (int l = 0; l < quest.getDescription(player)[i].length; l++) {
                    if (i == stage) {
                        player.getPacketSender().sendString(STRING_IDS[line], quest.getDescription(player)[i][l]);
                        line++;
                    } else if (i < stage) {
                        player.getPacketSender().sendString(STRING_IDS[line],
                                "<str=0>" + quest.getDescription(player)[i][l] + "</str>");
                        line++;
                    }
                }
            }
        }

        if (quest.isCompleted(player)) {
            line++;
            player.getPacketSender().sendString(STRING_IDS[line], "<col=344>QUEST COMPLETED!");
        }

        String extra = "";

        if (player.getRights() == PlayerRights.DEVELOPER) {
            extra = "stage: " + getStage(player, quest.name) + "/" + quest.finalStage;
        }

        for (int i = 0; i < 100; i++) {
            player.getPacketSender().sendInterfaceScrollReset(8135 + i);
        }

        player.getPacketSender()
                .sendString(8144,
                        "@blu@" + quest.name + " (" + player.getQuest().tracker.getPercentage(quest.name) + "%) " + extra)
                .sendInterface(8134);
    }

    public static void complete(final Player player, final Quest quest, String[] reward, int itemId) {
        EntityExtKt.setBoolean(player, Attribute.HAS_PENDING_EXPERIENCE_DELAY, true, false);
         player.BLOCK_ALL_BUT_TALKING = true;
        TaskManager.submit(new Task(1) {
            @Override
            public void execute() {
                stop();
                player.getPacketSender().sendString(12144, "Congratulations! Quest complete!");
                player.getPacketSender().sendString(12150,
                        quest.questPoints + " quest point" + (quest.questPoints > 1 ? "s" : "") + "! ");
                for (int i = 0; i < 5; i++) {
                    if (i < reward.length) {
                        player.getPacketSender().sendString(12151 + i, reward[i]);
                    } else {
                        player.getPacketSender().sendString(12151 + i, "");
                    }
                }

                // player.BLOCK_ALL_BUT_TALKING = false;

                player.getPoints().increase(AttributeManager.Points.QUEST_POINTS, quest.questPoints);

                int questPoints = player.getPoints().get(AttributeManager.Points.QUEST_POINTS);

                player.getPacketSender().sendString(12147, "" + questPoints);
                player.getPacketSender().sendMessage("Congratulations! You have completed the quest '" + quest.name + "'.");
                // Send jinglebit
                if (EntityExtKt.passedTime(player, Attribute.LAST_ACHIEVMENT_COMPLETION, 5000, TimeUnit.MILLISECONDS, false, true)
                        && EntityExtKt.passedTime(player, Attribute.LAST_LEVEL_UP, 30_000, TimeUnit.MILLISECONDS, false, true)) {
                    player.getPacketSender().sendJinglebitMusic(Misc.randomElement(JINGEBIT_MUSIC_IDS), 0);
                }
                ParticipationPoints.addPoints(player, 1 + Misc.getRandomInclusive(2) * quest.questPoints,
                        "@dre@from completing quests</col>.");
                player.getPacketSender().sendInterfaceModel(12145, itemId, 240).sendInterface(12140);
                player.performGraphic(new Graphic(199));
            }
        });
    }

    public static void sendQuestColour(Player player, Quest quest, int lineId) {

        String percentage = " (" + player.getQuest().tracker.getPercentage(quest.name) + "%)";

        if (quest.isCompleted(player)) {
            player.getPacketSender().sendStringColour(lineId, Misc.GREEN);
        } else if (!quest.isCompleted(player) && getStage(player, quest.name) > 0) {
            player.getPacketSender().sendStringColour(lineId, Misc.YELLOW);
        } else {
            player.getPacketSender().sendStringColour(lineId, Misc.RED);
        }

        int line = quest.type == QuestType.SPECIAL ? SPECIAL_LINE : QUEST_LINE;

        int slot = 1 + lineId - line;

        player.getPacketSender().sendString(lineId, slot + ". " + quest.name + percentage);
    }

    private static int getCompletionQuestPoints() {
        int points = 0;
        for (Quest q : QUESTS.values()) {
            points += q.questPoints;
        }
        return points;
    }

    public static int getSpecialQuestPoints() {
        int points = 0;
        for (Quest q : QUESTS.values()) {
            if (q.type == QuestType.SPECIAL) {
                points += q.questPoints;
            }
        }
        return points;
    }

    public static int getSpecialQuestsCompleted(Player p) {
        int amount = 0;
        for (Quest q : QUESTS.values()) {
            if (q.type == QuestType.SPECIAL) {
                if (hasCompletedQuest(p, q.name)) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public static ArrayList<Quest> getQuestForNpcs(int npcId) {
        return QuestDialogueLoader.FOR_NPC.get(npcId);
    }

    public static String hasItem(final Player player, Item item, final String s) {
        final boolean str = player.getInventory().getAmount(item.getId()) >= item.getAmount();
        return (str ? "<str=0>" : "") + s;
    }

    public static String hasQuestPoints(Player p, int amount, String s) {
        boolean str = p.getPoints().get(AttributeManager.Points.QUEST_POINTS) >= amount;

        return (str ? "<str=0>" : "") + s;
    }

    public static String hasLevel(final Player player, Skill skill, final int level) {
        boolean str = player.getSkillManager().getCurrentLevel(skill) >= level;

        return (str ? "<str=0>" : "") + "level " + level + " " + skill.getName();
    }

    public static int getStage(Player player, String name) {
        return player.getQuest().tracker.getProgress(name);
    }

    public static void increaseStage(Player player, Quest quest) {
        setStage(player, quest, getStage(player, quest.name) + 1);
    }

    public static void increaseStage(Player player, String name) {
        int current = player.getQuest().tracker.getProgress(name);

        player.getQuest().tracker.setProgress(name, current + 1);

        player.getQuest().tracker.update();
    }

    public static void setStage(Player player, Quest quest, int amount) {
        player.getQuest().tracker.setProgress(quest.name, amount);

        player.getQuest().tracker.update();
    }

    public static boolean hasCompletedQuest(Player p, String name) {
        if (QUESTS.get(name) == null) {
            return false;
        }
        Quest quest = QUESTS.get(name);
        return getStage(p, name) == quest.finalStage;
    }

    public static Optional<NPC> getNpcById(final int id) {
        for (NPC npc : World.getNpcs()) {
            if (npc == null) {
                continue;
            }
            if (npc.getId() == id) {
                return Optional.of(npc);
            }
        }
        return Optional.empty();
    }

    public static void despawnNpcs(Player p) {
        if(p.getQuest().spawnedNpcs.size() == 0) {
            return;
        }

        for(NPC n : p.getQuest().spawnedNpcs) {
            World.getNpcRemoveQueue().add(n);
        }

        p.getQuest().spawnedNpcs.clear();
    }

    @Override
    public boolean handleEquipItemInteraction(Player player, Item item, int slot) {
        switch (item.getId()) {
            case ItemID.QUEST_POINT_CAPE:
            case ItemID.QUEST_POINT_CAPE_T_:
            case ItemID.QUEST_POINT_HOOD:
                int questPoints = player.getPoints().get(AttributeManager.Points.QUEST_POINTS);
                if (questPoints != getCompletionQuestPoints()) {
                    player.getPacketSender().sendMessage("You must have all the quests completed to be able to equip this.");
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean handleCommand(Player player, String command, String[] args) {
        if (player.getRights() == PlayerRights.DEVELOPER) {
            if (command.startsWith("max-quests")) {
                for (Quest q : QUESTS.values()) {
                    complete(player, q, new String[0], 4151);
                    player.getQuest().tracker.setProgress(q.name, q.finalStage);
                }
                player.getQuest().tracker.update();
                return true;
            } else if(command.startsWith("set-quest")) {
                String name = args[1].replaceAll("_", " ");
                int stage = Integer.parseInt(args[2]);

                player.getQuest().tracker.setProgress(name, stage);
                player.getQuest().tracker.update();
                return true;
            } else if(command.startsWith("reset-all-quests")) {
                player.getQuest().tracker.reset();
                player.getQuest().tracker.update();
                return true;
            }
        }
        return false;
    }

    public Quest getDialogue() {
        return dialogue;
    }

    public void setDialogue(Quest dialogue) {
        this.dialogue = dialogue;
    }
}
