package com.grinder.game.content.cluescroll.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.cluescroll.ClueGuide;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.ClueScrollManager;
import com.grinder.game.content.cluescroll.scroll.ScrollConstants;
import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty;
import com.grinder.game.content.cluescroll.scroll.reward.RewardTable;
import com.grinder.game.content.cluescroll.scroll.reward.ScrollReward;
import com.grinder.game.content.cluescroll.task.impl.*;
import com.grinder.game.content.miscellaneous.Emotes.EmoteData;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.Coordinate;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.IntefaceID;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.SlotItem;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.oldgrinder.Area;
import com.grinder.util.oldgrinder.EquipSlot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Handle Clue Scrolls assigning, saving, loading, storing, mapping.
 *
 * @author Pb600
 * @version 1.0 07/05/2016
 */
public class ClueTaskFactory {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final transient Logger logger = LogManager.getLogger(ClueTaskFactory.class.getSimpleName());

    private static ClueTaskFactory instance;

    private final ArrayList<ClueTask> easyScrolls = new ArrayList<>();
    private final ArrayList<ClueTask> mediumScrolls = new ArrayList<>();
    private final ArrayList<ClueTask> hardScrolls = new ArrayList<>();
    private final ArrayList<ClueTask> eliteScrolls = new ArrayList<>();
    private final Map<Integer, ClueTask> taskMap = new TreeMap<>();

    private ClueTaskFactory() {
    }

    public static ClueTaskFactory getInstance() {
        if (instance == null) {
            instance = new ClueTaskFactory();
        }
        return instance;
    }

    /**
     * Attempt to execute a task operation.
     * <p>
     * Filter scrolls that are present and attempt to perform the argued
     * operation on the tasks.
     *
     * @param player    Client attempting to perform a task operation.
     * @param taskType  Type of task operation.
     * @param arguments Arguments given to validate operation.
     */
    public static boolean executeOperation(final Player player, final ClueType taskType, final Object... arguments) {
        final ClueScrollManager manager = player.getClueScrollManager();

        manager.scrollSuccess = false;

        Stream.of(manager.easyScroll, manager.mediumScroll, manager.hardScroll, manager.eliteScroll)
                .filter(Objects::nonNull)
                .forEach(task -> {
                    if (task.performOperation(player, taskType, arguments))
                        manager.scrollSuccess = true;
                });
        return manager.scrollSuccess;
    }

    /**
     * Increment the amount of a task difficulty level completed.
     *
     * @param player     Player that is incrementing task count.
     * @param difficulty Difficulty of the task count type.
     */
    static int incrementTaskCount(final Player player, final ScrollDifficulty difficulty) {
        final ClueScrollManager manager = player.getClueScrollManager();
        switch (difficulty) {
            case EASY:
                return ++manager.easyScrollCount;
            case MEDIUM:
                return ++manager.mediumScrollCount;
            case HARD:
                return ++manager.hardScrollCount;
            case ELITE:
                return ++manager.eliteScrollCount;
        }
        return 0;
    }

    public Map<Integer, ClueTask> getTaskMap() {
        return taskMap;
    }

    public void initialize() {

        easyScrolls.clear();
        mediumScrolls.clear();
        hardScrolls.clear();
        eliteScrolls.clear();
        taskMap.clear();

        addTask(new SearchDigSpotClueTask(1, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_VARROCK_GUILD)),
                new Position(3166, 3360, 0), 1));
        addTask(new SearchDigSpotClueTask(2, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_VARROCK_MINE)),
                new Position(3290, 3372, 0), 1));
        addTask(new SearchDigSpotClueTask(3, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_WEST_CRAFTING_GUILD)),
                new Position(2906, 3294, 0), 1));
        addTask(new SearchDigSpotClueTask(4, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_DRAYNOR_BANK)),
                new Position(3093, 3227, 0), 1));
        addTask(new SearchObjectClueTask(5, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_GRAVEYARD_CRATER)), 1, false,
                new Coordinate(3309, 3503, 0)));
        addTask(new SearchDigSpotClueTask(6, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_FALADOR_NORTH)),
                new Position(3043, 3399, 0), 1));
        addTask(new SearchDigSpotClueTask(7, new ClueScroll(ScrollDifficulty.EASY, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_YANILLE_ANVIL_SOUTH)),
                new Position(2616, 3077, 0), 1));

        /* Simple tasks */
        addTask(new SearchObjectClueTask(8, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search for a crate", "in a building in Hemenster.")), 1, false,
                new Coordinate(2636, 3453, 0)));
        addTask(new SearchObjectClueTask(9, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search for a crate", "in Varrock Palace.")), 1,false,
                new Coordinate(3224, 3492, 0)));
        addTask(new SearchObjectClueTask(10, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search for a crate", "on the ground floor", "of a house in Seers' Village.")), 1,false,
                new Coordinate(2699, 3470, 0)));
        addTask(new SearchObjectClueTask(11, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the boxes", "in a shop in Taverley.")), 1,false,
                new Coordinate(2886, 3449, 0), new Coordinate(2886, 3448, 0)));
        addTask(new SearchObjectClueTask(12, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the boxes", "in the goblin house near Lumbridge.")), 1,false,
                new Coordinate(3245, 3245, 0)));
        addTask(new SearchObjectClueTask(13, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the boxes", "in the house near the south entrance of Varrock.")), 1,false,
                new Coordinate(3203, 3384, 0),
                new Coordinate(3202, 3385, 0),
                new Coordinate(3201, 3386, 0)));
        addTask(new SearchObjectClueTask(14, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the chest", " in the Duke of Lumbridge's bedroom.")), 1,false,
                new Coordinate(3209, 3217, 1),
                new Coordinate(3209, 3218, 1)));
        addTask(new SearchObjectClueTask(15, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the chests", "in Al Kharid palace.")), 1,false,
                new Coordinate(3301, 3167, 0)));
        addTask(new SearchObjectClueTask(16, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crate in the left-hand", "tower of Lumbridge castle.")), 1, false,
                new Coordinate(3228, 3212, 1)));
        addTask(new SearchObjectClueTask(17, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in a house", "in Yanille that has a piano.")), 1, false,
                new Coordinate(2598, 3105, 0)));
        addTask(new SearchObjectClueTask(18, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in Canifis.")), 1, false,
                new Coordinate(3509, 3496, 0),
                new Coordinate(3510, 3496, 0),
                new Coordinate(3509, 3497, 0)));
        addTask(new SearchObjectClueTask(19, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in", "East Ardougne's general store.")), 1, false,
                new Coordinate(2615, 3291, 0)));
        addTask(new SearchObjectClueTask(20, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in", "Horvik's armoury.")), 1, false,
                new Coordinate(3228, 3433, 0)));
        addTask(
                new SearchObjectClueTask(21,
                        new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                                new ClueGuide(
                                        "Search the crates in the guardhouse",
                                        "of the northern gate of East Ardougne.")
                        ),
                        1, false,
                        new Coordinate(2645, 3338, 0),
                        new Coordinate(2646, 3338, 0),
                        new Coordinate(2647, 3339, 0)));
        addTask(new SearchObjectClueTask(22, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in the", "northern most house in Al Kharid.")), 1,false,
                new Coordinate(3289, 3202, 0)));
        addTask(new SearchObjectClueTask(23, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in the", "Port Sarim fishing shop.")), 1, false,
                new Coordinate(3012, 3222, 0),
                new Coordinate(3012, 3221, 0)));
        addTask(new SearchObjectClueTask(24, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates in the", "shed just north of east Ardougne.")), 1, false,
                new Coordinate(2617, 3347, 0),
                new Coordinate(2618, 3347, 0)));
        addTask(new SearchObjectClueTask(25, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates", "near a cart in Varrock.")), 1, false,
                new Coordinate(3226, 3452, 0)));
        addTask(new SearchObjectClueTask(26, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the crates just outside", "the armour shop in East Ardougne.")), 1, false,
                new Coordinate(2654, 3299, 0)));
        addTask(new SearchObjectClueTask(27, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "above Varrock's shops.")), 1, false,
                new Coordinate(3206, 3419, 1)));
        addTask(new SearchObjectClueTask(28, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in a house in Draynor Village.")), 1, false,
                new Coordinate(3097, 3277, 0)));
        addTask(new SearchObjectClueTask(29, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in Catherby's archery shop.")), 1, false,
                new Coordinate(2825, 3442, 0)));
        addTask(new SearchObjectClueTask(30, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in Falador's chainmail shop.")), 1, false,
                new Coordinate(2969, 3311, 0), new Coordinate(2969, 3312, 0)));
        addTask(new SearchObjectClueTask(31, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in one of Gertrude's bedrooms.")), 1, false,
                new Coordinate(3156, 3406, 0)));
        addTask(new SearchObjectClueTask(32, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in the ground floor of a shop in Yanille.")), 1, false,
                new Coordinate(2570, 3085, 0)));
        addTask(new SearchObjectClueTask(33, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers", "in the upstairs of a house in Catherby.")), 1, false,
                new Coordinate(2809, 3451, 1), new Coordinate(2809, 3449, 1)));
        addTask(new SearchObjectClueTask(34, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers upstairs in", "Falador's shield shop.")), 1, false,
                new Coordinate(2971, 3386, 1)));
        addTask(new SearchObjectClueTask(35, new ClueScroll(ScrollDifficulty.EASY, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers upstairs in", "the bank to the East of Varrock.")), 1, false,
                new Coordinate(3250, 3420, 1)));

        /* Cryptics */

        addTask(new SearchDigSpotClueTask(100, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_SEERS_VILLAGE)),
                new Position(2612, 3481, 0), 1));
        addTask(new SearchDigSpotClueTask(102, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_ARDOUGNE_BROKEN_HOUSES)),
                new Position(2489, 3306, 0), 1));
        addTask(new SearchDigSpotClueTask(104, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_NORTH_NECROMANCER_TOWER)),
                new Position(2653, 3233, 0), 1));
        addTask(new SearchDigSpotClueTask(106, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_CHEMIST_HOUSE)),
                new Position(2924, 3208, 0), 1));
        addTask(new SearchDigSpotClueTask(108, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_FALADOR_STATUE)),
                new Position(2970, 3414, 0), 1));
        addTask(new SearchDigSpotClueTask(109, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_WILD_VULCANS)),
                new Position(3020, 3912, 0), 2)
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_ZAMORAK_WIZARD, -1)));
        addTask(new SearchDigSpotClueTask(110, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_LEGENDS_GUILD_SOUTH)),
                new Position(2723, 3339, 0), 1));
        addTask(new SearchDigSpotClueTask(111, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_MISCELANIA)),
                new Position(2535, 3865, 0), 1));
        addTask(new SearchDigSpotClueTask(112, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_MORTON)),
                new Position(3434, 3265, 0), 1));
        addTask(new SearchDigSpotClueTask(113, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_CHAOS_ALTAR)),
                new Position(2454, 3230, 0), 1));
        addTask(new SearchDigSpotClueTask(114, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DIG,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_BARBARIAN_OUTPOST_NORTH)),
                new Position(2578, 3597, 0), 1));

        addTask(new SearchObjectClueTask(116, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_SEERS_VILLAGE_CRATER)), 1, false,
                new Coordinate(2658, 3488, 0)));
        addTask(new SearchObjectClueTask(117, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_WILD_CASTLE_FORTRESS)), 1, false,
                new Coordinate(3026, 3628, 0)));
        addTask(new SearchObjectClueTask(118, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide(ScrollConstants.INTERFACE_MAP_CLOCK_TOWER)), 1, false,
                new Coordinate(2565, 3248, 0)));
        // Medium
        addTask(new SearchObjectClueTask(120, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the drawers of houses", "in Burthorpe.")), 1, false,
                new Coordinate(2929, 3570, 0),
                new Coordinate(2921, 3577, 0)));

        // Dance type
        addTask(new PerformEmoteClueTask(121, 0, 2,
                new Area(3311, 3240, 3316, 3244),
                new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.DANCE,
                        new ClueGuide("Bow or curtsy in the ticket office", "of the Duel Arena.")))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        addTask(new SearchNpcClueTask(122, 4280, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Ned in Draynor Village."))));
        addTask(new SearchNpcClueTask(123, 6527, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Roavar."))));
        addTask(new SearchNpcClueTask(124, 3521, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Sir Kay in Camelot Castle."))));
        addTask(new SearchNpcClueTask(125, 4737, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Talk to the Squire in the ", "White Knights' castle in Falador."))));
        addTask(new SearchNpcClueTask(126, 2875, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Talk to Zeke in Al Kharid."))));

        addTask(new SearchObjectClueTask(127, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search the tents in the imperial", "guard camp in Burthorpe for some boxes.")), 1, false,
                new Coordinate(2894, 3527, 0),
                new Coordinate(2894, 3528, 0),
                new Coordinate(2894, 3529, 0),
                new Coordinate(2899, 3540, 0),
                new Coordinate(2900, 3540, 0),
                new Coordinate(2901, 3538, 0),
                new Coordinate(2885, 3540, 0)));
        addTask(new SearchObjectClueTask(128, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.OBJECT_CLICK,
                        new ClueGuide("Search through chests found in", "the upstairs of houses in eastern Falador.")), 1, false,
                new Coordinate(3041, 3364, 1),
                new Coordinate(3041, 3361, 1)));

        addTask(new SearchNpcClueTask(129, 3200, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Arhein in Catherby."))));
        addTask(new SearchNpcClueTask(130, 3893, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Doric, who lives", "north of Falador."))));
        addTask(new SearchNpcClueTask(131, 3231, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Ellis in Al Kharid."))));
        addTask(new SearchNpcClueTask(132, 3105, 1, new ClueScroll(ScrollDifficulty.MEDIUM, ClueType.NPC_CLICK,
                        new ClueGuide("Speak to Hans to solve the clue."))));

        /*
         * Hard Tasks
         */

      //  addTask(new SolvePuzzleClueTask(200, new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
      //          new ClueGuide("King Roald is requesting your", "presence at the Castle.<l>", "BL EPOITE")), ScrollConstants.NPC_KING_ROALD, 1, ScrollConstants.ITEM_CASTLE_PUZZLE_BOX)
      //          .setRequirements((c) -> c.getClueScrollManager().getScrollManager().hasBown(30_000)));

       // addTask(new SolvePuzzleClueTask(201, new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
        //        new ClueGuide("This anagram reveals", "who to speak to next:", "ACE MATCH ELM")), 5952, 1, ScrollConstants.ITEM_PLANE_PUZZLE_BOX).setMessages(new DialogueChat("You need some inventory space.").setStatementDialogue(), new DialogueChat("*makes camel noises*").setNPC(2812, "Cam the Camel"), new DialogueChat("*makes happier camel noises*").setNPC(2812, "Cam the Camel"), new DialogueChat("The camel is not impressed.").setStatementDialogue()));

        addTask(new PerformEmoteClueTask(202, 0, EmoteData.CHEER,
                new Area(2550, 3554, 2553, 3558),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Cheer in the Barbarian Agility Arena. ", "Headbang before you talk to me. ", "Equip a steel platebody, maple shortbow ", "and bronze boots.")))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.HEADBANG))
                .setRequiredEquipments(
                        new SlotItem(853, 1, EquipmentConstants.WEAPON_SLOT),
                        new SlotItem(1119, 1, EquipmentConstants.BODY_SLOT),
                        new SlotItem(4119, 1, EquipmentConstants.FEET_SLOT))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_1_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Anagram: OK CO
        addTask(new AnswerQuestionByNpcClueTask("9", 204, 4626,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "OK CO")),
                "How many cannons does Lumbridge castle have?"));

        // Anagram: Snah
        addTask(new SearchNpcClueTask(210, 3105, 1,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("Snah? I feel all confused.", "like one of those cakes."))));

        // Anagram: Unleash Night Mist
        addTask(new AnswerQuestionByNpcClueTask("302", 211, 3924,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "UNLEASH NIGHT MIST")), "What is the combined slayer requirement of every monster in the Slayer Cave?"));

        /// Anagram: Are Col
        addTask(new AnswerQuestionByNpcClueTask("48", 212, 821,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "ARE COL")),
                "If x is 15 and y is 3", "What is 3x + y?"));

        // Anagram: T RUN B
        addTask(new AnswerQuestionByNpcClueTask("4", 213, 9263,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "DT RUN B")),
                "How many people are waiting for the", "next Bard to perform?"));

        // Anagram: Err Cure It
        addTask(new AnswerQuestionByNpcClueTask("20", 214, 7734,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "ERR CURE IT")),
                "How many houses have a cross on the door?"));

        // Dance type
        addTask(new PerformEmoteClueTask(215, 0, 21,
                new Area(2910, 3157, 2928, 3167),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Salute in the banana plantation. ",
                                "Beware of double agents! ",
                                "Equip a diamond ring, amulet of power, ",
                                "and nothing on your chest and legs.")))
                .setRequiredEquipments(new SlotItem(1643, 1, EquipSlot.RING), new SlotItem(1731, 1, EquipSlot.AMULET), new SlotItem(-1, 1, EquipSlot.CHEST), new SlotItem(-1, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(216, 0, 5,
                new Area(3305, 3491, 3310, 3493),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Wave along the south fence ", "of the Lumberyard. ", "Equip a leather body, leather chaps ", "and a bronze hatchet.")))
                .setRequiredEquipments(new SlotItem(1129, 1, EquipSlot.CHEST), new SlotItem(1095, 1, EquipSlot.LEGS), new SlotItem(1351, 1, EquipSlot.WEAPON))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(217, 0, 5,
                new Area(2989, 3111, 3001, 3125),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Wave on Mudskipper Point. ", "Equip a gold ring, leather chaps ", "and a steel mace.")))
                .setRequiredEquipments(new SlotItem(1095, 1, EquipSlot.LEGS), new SlotItem(1635, 1, EquipSlot.RING), new SlotItem(1424, 1, EquipSlot.WEAPON))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(218, 0, EmoteData.SPIN,
                new Area(3104, 3420, 3106, 3421),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Spin on the bridge by Gunnarsgrunn ", "(or Barbarian Village). ", "Salute before you talk to me. ", "Equip an iron hatchet, steel kiteshield ", "and mithril full helm.")))
                .setRequiredEquipments(new SlotItem(1349, 1, EquipSlot.WEAPON), new SlotItem(1193, 1, EquipSlot.SHIELD), new SlotItem(1159, 1, EquipSlot.HAT))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.SALUTE))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(219, 0, 14,
                new Area(3086, 3333, 3091, 3338),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Spin in Draynor Manor by the fountain. ",
                                "Equip an iron platebody,",
                                " studded leather chaps ",
                                "and a bronze full helmet.")))
                .setRequiredEquipments(
                        new SlotItem(1155, 1, EquipSlot.HAT),
                        new SlotItem(1115, 1, EquipSlot.CHEST),
                        new SlotItem(1097, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(220, 0, 14,
                new Area(2953, 3240, 2956, 3243),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Spin at the crossroads",
                                "north of Rimmington.", "Equip a Sapphire ring,",
                                " adamant mace and leather chaps.")))
                .setRequiredEquipments(
                        new SlotItem(1637, 1, EquipSlot.RING),
                        new SlotItem(1430, 1, EquipSlot.WEAPON),
                        new SlotItem(1095, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(221, 0, 4,
                new Area(3156, 3296, 3160, 3301),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Think in the middle ", "of the wheat field", "by the Lumbridge mill. ", "Equip a sapphire necklace,", "cape of legends and an oak shortbow.")))
                .setRequiredEquipments(new SlotItem(843, 1, EquipSlot.WEAPON), new SlotItem(1052, 1, EquipSlot.CAPE))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(222, 0, 12,
                new Area(2833, 2579, 2862, 2608),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Dance in the Medallion Casino. ", "Equip a steel full helm,", "steel platebody and ", "iron plateskirt.")))
                .setRequiredEquipments(new SlotItem(1157, 1, EquipSlot.HAT), new SlotItem(1119, 1, EquipSlot.CHEST), new SlotItem(1081, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(223, 0, 12,
                new Area(3202, 3167, 3205, 3170),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Dance in the shack",
                                "in Lumbridge Swamp. ",
                                "Equip a bronze dagger,",
                                " iron full helm and a gold ring.")))
                .setRequiredEquipments(
                        new SlotItem(1153, 1, EquipSlot.HAT),
                        new SlotItem(1635, 1, EquipSlot.RING),
                        new SlotItem(1205, 1, EquipSlot.WEAPON))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(224, 0, 10,
                new Area(2753, 3439, 2765, 3449),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Jump for joy at the beehives. ",
                                "Equip iron boots,",
                                " a gold amulet and a steel hatchet.")))
                .setRequiredEquipments(
                        new SlotItem(1353, 1, EquipSlot.WEAPON),
                        new SlotItem(4121, 1, EquipSlot.FEET),
                        new SlotItem(1692, 1, EquipSlot.AMULET))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(225, 0, EmoteData.JUMP_FOR_JOY,
                new Area(2609, 3088, 2614, 3097),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Jump for joy in Yanille bank. ",
                                "Dance a jig before you talk to me.",
                                "Equip an iron crossbow,",
                                " adamant medium helmet and snakeskin chaps.")))
                .setRequiredEquipments(
                        new SlotItem(9177, 1, EquipSlot.WEAPON),
                        new SlotItem(1145, 1, EquipSlot.HAT),
                        new SlotItem(6324, 1, EquipSlot.LEGS))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.JIG))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(226, 0, 9,
                new Area(2740, 3534, 2742, 3538),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Laugh at the crossroads",
                                "south of Sinclair Mansion. ",
                                "Equip a cowl,",
                                " strength amulet and iron scimitar.")))
                .setRequiredEquipments(
                        new SlotItem(1323, 1, EquipSlot.WEAPON),
                        new SlotItem(1167, 1, EquipSlot.HAT),
                        new SlotItem(1725, 1, EquipSlot.AMULET))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(227, 0, EmoteData.YAWN,
                new Area(2441, 3082, 2444, 3097),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Yawn in the Castle Wars lobby. ",
                                "Shrug before you talk to me. ",
                                "Equip a ruby amulet,",
                                " mithril scimitar and iron square shield.")))
                .setRequiredEquipments(
                        new SlotItem(1698, 1, EquipSlot.AMULET),
                        new SlotItem(1329, 1, EquipSlot.WEAPON),
                        new SlotItem(1175, 1, EquipSlot.SHIELD))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.SHRUG))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(228, 0, 11,
                new Area(3077, 3248, 3081, 3252),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Yawn in Draynor Marketplace. ",
                                "Equip an iron kiteshield,",
                                " steel longsword and studded leather chaps.")))
                .setRequiredEquipments(
                        new SlotItem(1191, 1, EquipSlot.SHIELD),
                        new SlotItem(1295, 1, EquipSlot.WEAPON),
                        new SlotItem(1097, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(229, 0, 6,
                new Area(2976, 3237, 2982, 3243),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Shrug in the mine near Rimmington,",
                                " Equip a gold necklace,",
                                " a gold ring and a bronze spear.")))
                .setRequiredEquipments(
                        new SlotItem(1237, 1, EquipSlot.WEAPON),
                        new SlotItem(1654, 1, EquipSlot.AMULET),
                        new SlotItem(1635, 1, EquipSlot.RING))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.SHRUG))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(230, 0, EmoteData.BECKON,
                new Area(3367, 3423, 3372, 3430),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Show your angry in the Digsite,",
                                " near the eastern winch. ",
                                "Bow before you talk to me. ",
                                "Equip a steel med helm,snakeskin boots,",
                                " and an iron pickaxe.")))
                .setRequiredEquipments(
                        new SlotItem(1267, 1, EquipSlot.WEAPON),
                        new SlotItem(6328, 1, EquipSlot.FEET),
                        new SlotItem(1141, 1, EquipSlot.HAT))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.BOW))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(231, 0, 20,
                new Area(3112, 3174, 3115, 3206),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Clap on the causeway to the Wizards' Tower. ",
                                "Equip an iron med helm,",
                                " emerald ring and leather gloves.")))
                .setRequiredEquipments(
                        new SlotItem(1137, 1, EquipSlot.HAT),
                        new SlotItem(1059, 1, EquipSlot.HANDS),
                        new SlotItem(1639, 1, EquipSlot.RING))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(232, 2, 20,
                new Area(2630, 3383, 2635, 3388),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Clap on the top level of the mill,",
                                " north of east Ardougne. ",
                                "Equip an Emerald ring,",
                                " leather body and unenchanted Tiara.")))
                .setRequiredEquipments(
                        new SlotItem(5525, 1, EquipSlot.HAT),
                        new SlotItem(1129, 1, EquipSlot.CHEST),
                        new SlotItem(1639, 1, EquipSlot.RING))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(233, 0, EmoteData.CRY,
                new Area(2821, 3441, 2825, 3445),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Cry in Catherby archery shop. ",
                                "Bow before you talk to me. ", "Equip a Mind tiara,",
                                " a leather body and a silver sickle.")))
                .setRequiredEquipments(
                        new SlotItem(5529, 1, EquipSlot.HAT),
                        new SlotItem(2961, 1, EquipSlot.WEAPON),
                        new SlotItem(1129, 1, EquipSlot.CHEST))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.BOW))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(234, 0, 13,
                new Area(2606, 3384, 2615, 3393),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Dance a jig ",
                                "by the entrance to the Fishing Guild. ",
                                "Equip an emerald ring,",
                                " sapphire amulet and bronze chainbody.")))
                .setRequiredEquipments(
                        new SlotItem(1639, 1, EquipSlot.RING),
                        new SlotItem(1694, 1, EquipSlot.AMULET),
                        new SlotItem(1103, 1, EquipSlot.CHEST))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(235, 0, EmoteData.JIG,
                new Area(3302, 3122, 3305, 3125),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Dance a jig under Shantay's Awning. ",
                                "Bow before you talk to me. ",
                                "Equip a diamond ring and an air staff.")))
                .setRequiredEquipments(new SlotItem(1643, 1, EquipSlot.RING), new SlotItem(1381, 1, EquipSlot.WEAPON))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.BOW))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(236, 0, 12,
                new Area(3108, 3293, 3111, 3296),
                new ClueScroll(ScrollDifficulty.HARD, ClueType.DANCE,
                        new ClueGuide("Dance at the crossroads north of Draynor. ",
                                "Equip an iron chainbody,",
                                " sapphire ring and bronze kiteshield.")))
                .setRequiredEquipments(
                        new SlotItem(1637, 1, EquipSlot.RING),
                        new SlotItem(1189, 1, EquipSlot.SHIELD),
                        new SlotItem(1101, 1, EquipSlot.CHEST))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        /* Anagrams **/
        addTask(new SearchNpcClueTask(237, 3344, 1,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "AHA JAR"))));

        /* Talk to Lowe. ","**/
        addTask(new SearchNpcClueTask(238, NpcID.LOWE_8683, 1,
                new ClueScroll(ScrollDifficulty.HARD, ClueType.NPC_CLICK,
                        new ClueGuide("This anagram reveals", "who to speak to next:", "EL OW"))));

        /*
          Elite Scrolls
         */

        /* Talk to Abbot Langley in the Monastery near Edgeville. **/
        addTask(new SearchNpcClueTask(300, 2577, 1,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.NPC_CLICK,
                        new ClueGuide("'A bag belt only?',", " he asked his balding brothers."))));

        /*
          Upstairs in the church of East Ardougne, check the crate in the
          corner.
         */
        addTask(new SearchObjectClueTask(301,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("A crate found in the tower of a church ",
                                "is your next location.")),
                1, false,
                new Coordinate(2612, 3304, 1),
                new Coordinate(2612, 3306, 1)));

        /* Search the crates in the clothes shop in Canifis. **/
        addTask(new SearchObjectClueTask(303,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("A town with a different",
                                "sort of night-life ", "is your destination. ",
                                "Search for some crates ",
                                "in one of the houses.")),
                1, false,
                new Coordinate(3498, 3507, 0),
                new Coordinate(3497, 3507, 0)));

        /*
          Dig under the southern window in Aggie's house at Draynor Village.
         */
        addTask(new SearchDigSpotClueTask(304,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("Aggie I see. ",
                                "Lonely and southern I feel. ",
                                "I am neither inside nor outside the house,",
                                " yet no house would be complete without me. ",
                                "The treasure lies beneath me!")),
                new Position(3085, 3255, 0)));

        /*
          On Etceteria, dig next to the Evergreen in front of the castle walls.
         */
        addTask(new SearchDigSpotClueTask(305,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("And so on, and so on, and so on. ",
                                "Walking from the land of many unimportant things",
                                " leads to a choice of paths.")),
                new Position(2591, 3878, 0), 1));

        /*
          Search the crates north of the north-eastern most building in the
          battlefield south of Ardougne.
         */
        addTask(new SearchObjectClueTask(306,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("Being this far north has meant",
                                "that these crates have escaped ",
                                "being battled over.")),
                1, false,
                new Coordinate(2509, 3260, 0),
                new Coordinate(2518, 3258, 0),
                new Coordinate(2519, 3259, 0),
                new Coordinate(2520, 3258, 0),
                new Coordinate(2515, 3254, 0),
                new Coordinate(2517, 3254, 0),
                new Coordinate(2514, 3253, 0),
                new Coordinate(2520, 3249, 0)));

        /* Dig next to the well in Pollnivneach. **/
        addTask(new SearchDigSpotClueTask(307,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("Dig here if you're not feeling too well",
                                "after travelling through the desert. ",
                                "Ali heartily recommends it.")),
                new Position(3360, 2972, 0), 1));

        /* In the Monastery south of Ardougne, south wall of the east room. **/
        addTask(new SearchObjectClueTask(308,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("Find a crate close to the monks ", "that like to paaarty!")),
                1, false,
                new Coordinate(2614, 3204, 0)));

        /* Lumbridge windmill, search the crates on the top floor. **/
        addTask(new SearchObjectClueTask(309,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("Four blades I have, yet draw no blood;",
                                " Still I turn my prey to powder. ",
                                "If you're brave, come search my roof;",
                                " It is there my blades are louder.")),
                1,false,
                new Coordinate(3166, 3309, 2)));

        // Talk to General bentnose in goblin village. *
        addTask(new SearchNpcClueTask(310, 669, 1,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.NPC_CLICK,
                        new ClueGuide("Generally speaking, his nose was very bent"))));

        /*
         Search the hay bales in the circular room south of the Baxtorian
         Falls, near Rasolo.
        */
        addTask(new SearchObjectClueTask(311,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.OBJECT_CLICK,
                        new ClueGuide("Hay! Stop for a bit and admire the scenery,",
                                " just like the tourism promoter says.")),
                1, false,
                new Coordinate(2525, 3436, 0)));

        // Dig next to the gate, under the west bank of Varrock
        addTask(new SearchDigSpotClueTask(312,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("I am a token of the greatest love. ",
                                "I have no beginning or end. ",
                                "My eye is red, I can fit like a glove. ",
                                "Go to the place where it's money they lend,",
                                " And dig by the gate to be my friend.")),
                new Position(3192, 9825, 0), 1));

        /* Talk to Hamid, the monk inside the lobby of the Duel Arena. **/
        addTask(new SearchNpcClueTask(313, 3352, 1,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.NPC_CLICK,
                        new ClueGuide("Identify the back of this over-acting brother. ",
                                "(He's a long way from home.)"))));

        /* Talk to Gerrant in the fish shop in Port Sarim. **/
        addTask(new SearchNpcClueTask(314, 2891, 1,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.NPC_CLICK,
                        new ClueGuide("If a man carried my burden,",
                                " he would break his back. ",
                                "I am not rich,",
                                " but leave silver in my track. ",
                                "Speak to the keeper of my trail."))));

        // Dance type
        addTask(new PerformEmoteClueTask(315, 0, 17,
                new Area(2852, 2952, 2853, 2954),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Blow a kiss between the tables ",
                                "in Shilo Village bank. ",
                                "Beware of double agents! ",
                                "Equip a blue mystic hat,",
                                " iron dagger and rune platebody.")))
                .setRequiredEquipments(
                    new SlotItem(1203, 1, EquipSlot.WEAPON),
                    new SlotItem(4089, 1, EquipSlot.HAT),
                    new SlotItem(1127, 1, EquipSlot.CHEST))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(316, 0, 19,
                new Area(2596, 3278, 2599, 3281),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Blow a raspberry at the monkey cage ",
                                "in Ardougne Zoo. ",
                                "Equip a studded leather body,",
                                " bronze platelegs and a willow longbow.")))
                .setRequiredEquipments(
                    new SlotItem(847, 1, EquipSlot.WEAPON),
                    new SlotItem(1133, 1, EquipSlot.CHEST),
                    new SlotItem(1075, 1, EquipSlot.LEGS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(317, 0, 2,
                new Area(2726, 3346, 2731, 3349),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Bow or curtsy outside the entrance ",
                                "to the Legends' Guild. ",
                                "Equip iron platelegs,",
                                " an emerald amulet and an oak shortbow.")))
                .setRequiredEquipments(
                        new SlotItem(1067, 1, EquipSlot.LEGS),
                        new SlotItem(843, 1, EquipSlot.WEAPON),
                        new SlotItem(1696, 1, EquipSlot.AMULET))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(318, 0, 7,
                new Area(2923, 3480, 2929, 3487),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Cheer at the Druids' Circle. ",
                                "Equip an air tiara,",
                                " bronze two-handed sword and gold amulet.")))
                .setRequiredEquipments(
                        new SlotItem(5527, 1, EquipSlot.HAT),
                        new SlotItem(1307, 1, EquipSlot.WEAPON),
                        new SlotItem(1692, 1, EquipSlot.AMULET))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(319, 0, 7,
                new Area(3045, 3234, 3050, 3237),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide(
                                "Cheer for the monks at Port Sarim. ",
                                "Equip a coif,",
                                " steel plateskirt and a sapphire amulet.")
                ))
                .setRequiredEquipments(
                    new SlotItem(1169, 1, EquipSlot.HAT),
                    new SlotItem(1083, 1, EquipSlot.LEGS),
                    new SlotItem(1694, 1, EquipSlot.AMULET))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)
        ));

        // Dance type
        addTask(new PerformEmoteClueTask(320, 0, EmoteData.CHEER,
                new Area(2523, 3373, 2533, 3377),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Cheer in the Ogre Pen in the Training Camp. ",
                                "Beckon before you talk to me. ",
                                "Equip a green dragonhide body and chaps,",
                                " and a steel squareshield.")
                ))
                .setRequiredEquipments(
                    new SlotItem(1135, 1, EquipSlot.CHEST),
                    new SlotItem(1099, 1, EquipSlot.LEGS),
                    new SlotItem(1177, 1, EquipSlot.SHIELD))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.ANGRY))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(321, 0, 20,
                new Area(3357, 3332, 3367, 3348),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Clap in the main exam room in the Exam Centre. ",
                                "Equip a ruby amulet, dragon battleaxe and leather gloves.")))
                .setRequiredEquipments(
                        new SlotItem(1698, 1, EquipSlot.AMULET),
                        new SlotItem(1377, 1, EquipSlot.WEAPON),
                        new SlotItem(1059, 1, EquipSlot.HANDS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(322, 0, EmoteData.DANCE,
                new Area(3489, 3485, 3501, 3494),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Dance in the centre of Canifis. ",
                                "Bow before you talk to me. ",
                                "Equip a spiny helmet,",
                                " mithril platelegs and ", "an iron two-handed sword.")))
                .setRequiredEquipments(
                        new SlotItem(4551, 1, EquipSlot.HAT),
                        new SlotItem(1309, 1, EquipSlot.WEAPON),
                        new SlotItem(1071, 1, EquipSlot.LEGS))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.BOW))
                .setAgent(new ClueTaskAgent(-1, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(323, 0, EmoteData.YAWN,
                new Area(3207, 3490, 3214, 3497),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Yawn in Varrock Palace library. ",
                                "Equip a holy symbol,",
                                " leather vambraces and an iron warhammer.")))
                .setRequiredEquipments(
                        new SlotItem(1718, 1, EquipSlot.AMULET),
                        new SlotItem(1335, 1, EquipSlot.WEAPON),
                        new SlotItem(1063, 1, EquipSlot.HANDS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(324, 0, EmoteData.SHRUG,
                new Area(3239, 3609, 3240, 3610),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Shrug in the Zamorak temple ",
                                "found in the Eastern Wilderness. ",
                                "Beware of double agents!",
                                " Equip bronze platelegs, an iron platebody,",
                                " and blue d'hide vambraces.")))
                .setRequiredEquipments(
                        new SlotItem(1075, 1, EquipSlot.LEGS),
                        new SlotItem(1115, 1, EquipSlot.CHEST),
                        new SlotItem(2487, 1, EquipSlot.HANDS))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(325, 0, EmoteData.PANIC,
                new Area(3504, 3314, 3508, 3318),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Panic in the Mort'ton temple. ",
                                "Wave before you speak to me. ",
                                "Equip a mithril plateskirt, glowing dagger,",
                                " elemental shield and no boots.")))
                .setRequiredEquipments(
                        new SlotItem(747, 1, EquipSlot.WEAPON),
                        new SlotItem(2890, 1, EquipSlot.SHIELD),
                        new SlotItem(-1, 1, EquipSlot.FEET))
                .setFinishCondition(createEmotePerformFinishCondition(EmoteData.WAVE))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(326, 0, 18,
                new Area(2676, 3166, 2676, 3170),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Panic on the pier where you catch the Fishing Trawler. ", "Have nothing equipped at all when you do.")))
                .setRequiredEquipments(
                        new SlotItem(-1, 1, EquipSlot.HAT),
                        new SlotItem(-1, 1, EquipSlot.CAPE),
                        new SlotItem(-1, 1, EquipSlot.AMULET),
                        new SlotItem(-1, 1, EquipSlot.ARROWS),
                        new SlotItem(-1, 1, EquipSlot.WEAPON),
                        new SlotItem(-1, 1, EquipSlot.CHEST),
                        new SlotItem(-1, 1, EquipSlot.SHIELD),
                        new SlotItem(-1, 1, EquipSlot.LEGS),
                        new SlotItem(-1, 1, EquipSlot.HANDS),
                        new SlotItem(-1, 1, EquipSlot.FEET),
                        new SlotItem(-1, 1, EquipSlot.RING))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(327, 0, 18,
                new Area(2848, 3496, 2850, 3498),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Panic by the pilot on White Wolf Mountain. ",
                                "Beware of double agents!",
                                " Equip mithril platelegs, a ring of life ",
                                "and a rune hatchet.")))
                .setRequiredEquipments(
                        new SlotItem(1359, 1, EquipSlot.WEAPON),
                        new SlotItem(1071, 1, EquipSlot.LEGS),
                        new SlotItem(2570, 1, EquipSlot.RING))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type
        addTask(new PerformEmoteClueTask(328, 0, 18,
                new Area(3369, 3497, 3373, 3501),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Panic in the limestone mine. ",
                                "Equip bronze platelegs,",
                                " a steel pickaxe and a steel med helmet.")))
                .setRequiredEquipments(
                        new SlotItem(1075, 1, EquipSlot.LEGS),
                        new SlotItem(1269, 1, EquipSlot.WEAPON),
                        new SlotItem(1141, 1, EquipSlot.HAT))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        // Dance type - Elite
        addTask(new PerformEmoteClueTask(329, 0, 9,
                new Area(2811, 3680, 2813, 3681),
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DANCE,
                        new ClueGuide("Laugh in the Jokul's tent in the Mountain Camp. ", "Beware of double agents!", " Equip a rune full helmet,", " blue dragonhide chaps and a staff of fire.")))
                .setRequiredEquipments(
                        new SlotItem(1163, 1, EquipSlot.HAT),
                        new SlotItem(2493, 1, EquipSlot.LEGS),
                        new SlotItem(1387, 1, EquipSlot.WEAPON))
                .setAgent(new ClueTaskAgent(ScrollConstants.NPC_DOUBLE_AGENT_2_ID, ScrollConstants.NPC_URI_AGENT_ID)));

        addTask(new ScanDigSpotClueTask(330, 22,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in the East Ardougne.", "Orb scan range: 22 spaces.")),
                new Position(2625, 3293, 0),
                new Position(2635, 3313, 0),
                new Position(2623, 3311, 0),
                new Position(2662, 3305, 0),
                new Position(2662, 3338, 0),
                new Position(2613, 3339, 0),
                new Position(2589, 3331, 0),
                new Position(2589, 3320, 0),
                new Position(2569, 3340, 0),
                new Position(2583, 3265, 0)));

        addTask(new ScanDigSpotClueTask(331, 22,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in the West Ardougne.", "Orb scan range: 22 spaces.")),
                new Position(2540, 3331, 0),
                new Position(2529, 3306, 0),
                new Position(2512, 3267, 0),
                new Position(2517, 3281, 0),
                new Position(2500, 3290, 0),
                new Position(2496, 3282, 0),
                new Position(2509, 3330, 0),
                new Position(2475, 3331, 0),
                new Position(2467, 3319, 0),
                new Position(2483, 3313, 0),
                new Position(2462, 3282, 0),
                new Position(2448, 3315, 0),
                new Position(2520, 3318, 0)));

        addTask(new ScanDigSpotClueTask(332, 22,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Falador.", "Orb scan range: 22 spaces.")),
                new Position(2959, 3376, 0),
                new Position(2972, 3342, 0),
                new Position(2942, 3388, 0),
                new Position(2939, 3355, 0),
                new Position(2945, 3340, 0),
                new Position(2938, 3322, 0),
                new Position(2948, 3317, 0),
                new Position(2976, 3316, 0),
                new Position(3005, 3326, 0),
                new Position(3015, 3339, 0),
                new Position(3059, 3384, 0),
                new Position(3031, 3379, 0),
                new Position(3027, 3365, 0),
                new Position(3011, 3382, 0),
                new Position(3025, 3379, 0)));

        addTask(new ScanDigSpotClueTask(333, 16,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Neitiznot.", "Orb scan range: 16 spaces.")),
                new Position(2322, 3789, 0),
                new Position(2311, 3801, 0),
                new Position(2324, 3808, 0),
                new Position(2354, 3790, 0),
                new Position(2315, 3833, 0),
                new Position(2329, 3829, 0),
                new Position(2373, 3834, 0),
                new Position(2377, 3851, 0),
                new Position(2353, 3856, 0),
                new Position(2313, 3851, 0),
                new Position(2326, 3850, 0),
                new Position(2352, 3892, 0),
                new Position(2352, 3892, 0),
                new Position(2312, 3894, 0),
                new Position(2389, 3899, 0),
                new Position(2417, 3893, 0),
                new Position(2418, 3870, 0),
                new Position(2414, 3848, 0),
                new Position(2342, 3809, 0),
                new Position(2340, 3803, 0)));

        addTask(new ScanDigSpotClueTask(334, 16,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Fremennik Slayer Dungeon.", "Orb scan range: 16 spaces.")),
                new Position(2807, 10003, 0),
                new Position(2808, 10017, 0),
                new Position(2789, 10039, 0),
                new Position(2772, 10030, 0),
                new Position(2757, 10030, 0),
                new Position(2736, 10013, 0),
                new Position(2767, 10002, 0),
                new Position(2751, 9995, 0),
                new Position(2722, 10025, 0),
                new Position(2718, 10000, 0),
                new Position(2731, 9999, 0),
                new Position(2715, 9990, 0),
                new Position(2700, 9981, 0),
                new Position(2719, 9969, 0),
                new Position(2716, 9975, 0),
                new Position(2750, 9993, 0),
                new Position(2750, 9994, 0)));

        addTask(new ScanDigSpotClueTask(335, 30,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Haunted Woods.", "Orb scan range: 30 spaces.")),
                new Position(3616, 3512, 0),
                new Position(3624, 3508, 0),
                new Position(3637, 3486, 0),
                new Position(3623, 3476, 0),
                new Position(3604, 3506, 0),
                new Position(3596, 3501, 0),
                new Position(3523, 3460, 0),
                new Position(3529, 3501, 0),
                new Position(3551, 3514, 0),
                new Position(3562, 3509, 0),
                new Position(3544, 3465, 0)));

        addTask(new ScanDigSpotClueTask(336, 16,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Varrock.", "Orb scan range: 16 spaces.")),
                new Position(3143, 3488, 0),
                new Position(3185, 3472, 0),
                new Position(3178, 3508, 0),
                new Position(3188, 3486, 0),
                new Position(3230, 3495, 0),
                new Position(3214, 3462, 0),
                new Position(3197, 3423, 0),
                new Position(3196, 3415, 0),
                new Position(3204, 3409, 0),
                new Position(3175, 3403, 0),
                new Position(3197, 3383, 0),
                new Position(3211, 3385, 0),
                new Position(3228, 3383, 0),
                new Position(3240, 3383, 0),
                new Position(3253, 3393, 0),
                new Position(3228, 3409, 0),
                new Position(3220, 3407, 0),
                new Position(3231, 3439, 0),
                new Position(3241, 3480, 0),
                new Position(3248, 3453, 0)));

        addTask(new ScanDigSpotClueTask(337, 25,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work the Wilderness deep.", "Orb scan range: 25 spaces.")),
                new Position(3282, 3940, 0),
                new Position(3028, 3919, 0),
                new Position(3046, 3956, 0),
                new Position(3190, 3956, 0),
                new Position(2998, 3914, 0),
                new Position(3020, 3959, 0),
                new Position(2947, 3926, 0),
                new Position(3217, 3944, 0)));

        addTask(new ScanDigSpotClueTask(338, 16,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Edgeville.", "Orb scan range: 16 spaces.")),
                new Position(3132, 3482, 0),
                new Position(3069, 3516, 0),
                new Position(3067, 3518, 0),
                new Position(3083, 3479, 0),
                new Position(3043, 3482, 0),
                new Position(3057, 3475, 0),
                new Position(3072, 3453, 0),
                new Position(3094, 3489, 0)));

        addTask(new ScanDigSpotClueTask(339, 14,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Al Kharid.", "Orb scan range: 14 spaces.")),
                new Position(3259, 3174, 0),
                new Position(3269, 3161, 0),
                new Position(3292, 3177, 0),
                new Position(3324, 3142, 0),
                new Position(3300, 3126, 0),
                new Position(3286, 3174, 0),
                new Position(3317, 3180, 0),
                new Position(3317, 3205, 0),
                new Position(3319, 3243, 0),
                new Position(3390, 3265, 0),
                new Position(3324, 3216, 0),
                new Position(3299, 3281, 0)));

        addTask(new ScanDigSpotClueTask(340, 15,
                new ClueScroll(ScrollDifficulty.ELITE, ClueType.DIG,
                        new ClueGuide("This scroll will work in Draynor.", "Orb scan range: 15 spaces.")),
                new Position(3087, 3246, 0),
                new Position(3089, 3264, 0),
                new Position(3072, 3278, 0),
                new Position(3104, 3249, 0),
                new Position(3113, 3255, 0),
                new Position(3120, 3244, 0),
                new Position(3109, 3295, 0),
                new Position(3089, 3280, 0),
                new Position(3093, 3271, 0),
                new Position(3102, 3214, 0),
                new Position(3119, 3209, 0),
                new Position(3119, 3236, 0)));

        int totalTasks = Stream.of(easyScrolls, mediumScrolls, hardScrolls, eliteScrolls).mapToInt(ArrayList::size).sum();
        logger.info("Loaded " + totalTasks + " tasks.");
    }

    private void addTask(@NotNull ClueTask clueTask) {

        switch (clueTask.getDifficulty()) {
            case EASY:
                easyScrolls.add(clueTask);
                break;
            case MEDIUM:
                mediumScrolls.add(clueTask);
                break;
            case HARD:
                hardScrolls.add(clueTask);
                break;
            case ELITE:
                eliteScrolls.add(clueTask);
                break;
        }

        final int taskID = clueTask.getTaskID();

        if (taskMap.containsKey(clueTask.getTaskID()))
            throw new IllegalArgumentException("Duplicate Clue Task ID: (" + taskID + ")");

        taskMap.put(clueTask.getTaskID(), clueTask);
    }

    private static Predicate<Player> createEmotePerformFinishCondition(EmoteData emoteData) {
        return player -> player.getClueScrollManager().getScrollManager().hasPerformedEmote(emoteData.ordinal(), 15_000);
    }

    void removeTask(final Player player, final ScrollDifficulty difficulty) {

        final ClueScrollManager manager = player.getClueScrollManager();

        switch (difficulty) {
            case EASY:
                manager.easyScroll = null;
                break;
            case MEDIUM:
                manager.mediumScroll = null;
                break;
            case HARD:
                manager.hardScroll = null;
                break;
            case ELITE:
                manager.eliteScroll = null;
                break;
            default:
                break;
        }
    }

    /**
     * Reward a casket to player as Clue Scroll completion. The reward will be
     * added to the Inventory, whenever not possible it will attempt to deposit
     * at player Bank, as last resort it will be placed under player on the
     * floor.
     *
     * @param player          Player to receive the casket reward
     * @param difficulty Difficulty of the Clue Scroll completed.
     */
    public int rewardCasket(Player player, ScrollDifficulty difficulty) {
        int casketID = difficulty.getCasketID();
        if (!player.getGameMode().isUltimate()) {
            if (player.getInventory().canHold(new Item(casketID, 1))) {
                player.getInventory().add(new Item(casketID, 1));
            } else {
                if (player.getBank(player.getCurrentBankTab()).canHold(casketID, 1)) {
                    player.getBank(player.getCurrentBankTab()).add(casketID, 1);
                    player.sendMessage("Your reward has been sent to the bank.");
                } else {
                    if (!player.getGameMode().isSpawn()) {
                        ItemContainerUtil.dropUnder(player, casketID, 1);
                        player.sendMessage("Your reward has been placed on the floor.");
                    }
                }
            }
        } else {
            ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(casketID, 1));
        }
        return casketID;
    }

    /**
     * Load a task progress from a JSON document format.
     * <p>
     * The task ID is firstly loaded, then the task is gathered from the map,
     * then it will proceed the un-serialization of the task properties.
     * <p>
     * After the super class finish un-serializing the parent will implement the
     * serialization|un-serialization methods in order to store and gather
     * custom data.
     *
     * @param element JSON document containing task progress.
     * @return A scroll task with given progress.
     */
    public Optional<ClueTask> deserialize(final JsonElement element) {

        final JsonObject jsonDocument = element.getAsJsonObject();
        final int taskID = jsonDocument.get("taskID").getAsInt();

        final Optional<ClueTask> optionalClueTask = getTask(taskID);

        optionalClueTask.ifPresent(task -> task.fromJson(GSON, jsonDocument));

        return optionalClueTask;
    }

    /**
     * Get a <b>Cloned</b> task version of referred task index.
     *
     * @param taskID Index of the mapped task
     * @return a cloned version of mapped task based on the index value
     */
    private Optional<ClueTask> getTask(final int taskID) {
        return Optional.ofNullable(taskMap.get(taskID)).map(ClueTask::clone);
    }

    private List<ScrollReward> getScrollTaskReward(final ScrollDifficulty difficulty) {

        if (difficulty == null)
            return null;

        final int totalRewards = difficulty.getRewardAmount().getRandomAmount();

        final RewardTable[] rewardTables = difficulty.getRewards();
        final ArrayList<ScrollReward> itemList = new ArrayList<>();

        reward:
        for (int i = 0; i < totalRewards; i++) {

            for (RewardTable rewardTable : rewardTables) {

                final float probability = rewardTable.getProbability();

                if (Misc.randomChance(probability)) {

                    ScrollReward reward = Misc.random(rewardTable.getRewards());

                    while (itemList.contains(reward)) {

                        if (!rewardTable.hasUniqueRewards(itemList)) {
                            System.err.println("[ClueTaskFactory] could not generate enough reward for scroll " + difficulty + ", needed " + totalRewards + "!");
                            continue reward;
                        }

                        reward = Misc.random(rewardTable.getRewards());

                    }

                    itemList.add(reward);
                    continue reward;
                }
            }
        }
        if (itemList.size() == 0)
            return getScrollTaskReward(difficulty);

        return itemList;
    }

    public void openCasket(final Player player, final int casketID, final int itemSlot) {

        if (player.getInventory().countFreeSlots() <= 4) {
            player.sendMessage("You need at least 4 free inventory slots to open this casket.");
            return;
        }

        if (player.getInterfaceId() == IntefaceID.SCROLL_REWARD_INTERFACE)
            player.dispatchInterfaceClose();

        final ScrollDifficulty difficulty = ScrollDifficulty.forCasket(casketID);
        final Item item = player.getInventory().get(itemSlot);

        if (item != null && item.getId() == casketID) {
            String type = null;
            if(difficulty.equals(ScrollDifficulty.EASY)) {
                type = "Easy Treasure Trails";
            } else if(difficulty.equals(ScrollDifficulty.MEDIUM)) {
                type = "Medium Treasure Trails";
            } else if(difficulty.equals(ScrollDifficulty.HARD)) {
                type = "Hard Treasure Trails";
            } else if(difficulty.equals(ScrollDifficulty.ELITE)) {
                type = "Elite Treasure Trails";
            }
            //c.BLOCK_ALL_BUT_TALKING = true;
            player.getInventory().delete(new Item(item.getId(), 1));
            player.getInventory().refreshItems();
            final Item[] rewardItems = rollReward(player, difficulty);
            player.getClueScrollManager().getScrollManager().setRewards(rewardItems, difficulty);
            displayRewards(player, rewardItems);
            //c.setInterfaceCloseListener(() -> c.getClueScrollManager().getScrollManager().processReward());
            player.getClueScrollManager().getScrollManager().processReward();
            final Item[] displayItems = new Item[rewardItems.length];
            long totalValue = 0;

            for (int i = 0; i < displayItems.length; i++) {

                final Item clueReward = rewardItems[i];
                final ItemDefinition itemDefinition = clueReward.getDefinition();
                final boolean isNoted = itemDefinition != null && itemDefinition.isNoted();

                int itemID = clueReward.getId();


                if (isNoted)
                    itemID--;

                displayItems[i] = new Item(itemID, rewardItems[i].getAmount());
                totalValue += rewardItems[i].getValue(ItemValueType.PRICE_CHECKER) * rewardItems[i].getAmount();

                if(type != null) {
                    player.getCollectionLog().createOrUpdateEntry(player,  type, clueReward);
                    player.getCollectionLog().createOrUpdateEntry(player,  "Global Rewards", clueReward);
                }
            }

            // Reward participation points
            ClueTaskFactory.getInstance().rewardParticipationPoints(player, difficulty);

            // Process achievements
            AchievementManager.processFor(AchievementType.TREASURE_MASTER, player);
            AchievementManager.processFor(AchievementType.TREASURE_HOLDER, player);
            AchievementManager.processFor(AchievementType.TREASURE_FOUNDER, player);
            AchievementManager.processFor(AchievementType.TREASURE_HUNTER, player);



            // Send Total Completed
            player.getPoints().increase(AttributeManager.Points.FINISHED_CLUE_SCROLLS, 1); // Increase points
            PlayerTaskManager.progressTask(player, DailyTask.CLUE_SCROLLS);
            PlayerTaskManager.progressTask(player, WeeklyTask.CLUE_SCROLLS);
            int completedCount = ClueTaskFactory.incrementTaskCount(player, difficulty);
            player.sendMessage("<img=776> @blu@You have completed " + completedCount + " " + difficulty.name().toLowerCase() + " Treasure Trials.");
            player.sendMessage("<img=776> @blu@Your treasure is worth around " + Misc.formatWithAbbreviationCustomPrefix((totalValue), null) + "@blu@ coins!");
            player.sendMessage("<img=776> @blu@Total rewards: " + rewardItems.length + ""); // Total Rewards: " + rewardItems.length);
            //player.sendMessage("<img=776> @blu@Total completed clues: " + player.getPoints().get(AttributeManager.Points.FINISHED_CLUE_SCROLLS) +""); // Total Rewards: " + rewardItems.length);

            // Jinglebit
            if (Misc.random(2) == 1) {
                player.getPacketSender().sendJinglebitMusic(152, 0);
            } else {
                player.getPacketSender().sendJinglebitMusic(153, 0);
            }

        }

    }

    public Item[] rollReward(final Player player, final ScrollDifficulty scrollDifficulty) {
        float probabilityBoost = PlayerUtil.isMember(player) ? 0.03F : 0.01F;//Totem.LUCK.isActive(player) && player.isExtremeMember() ? 0.03F : 0F;
        if (PlayerUtil.isTopazMember(player) || PlayerUtil.isAmethystMember(player)) {
            probabilityBoost += 0.02F;
        }
        final List<ScrollReward> itemList = getScrollTaskReward(scrollDifficulty);
        final Item[] items = new Item[itemList.size()];
        for (int i = 0; i < items.length; i++) {
            final ScrollReward reward = itemList.get(i);
            items[i] = new Item(reward.getItemID(), reward.getAmount());
        }
        return items;
    }

    public void displayRewards(final Player player, final Item[] rewardItems) {
        final Item[] displayItems = new Item[rewardItems.length];
        for (int i = 0; i < displayItems.length; i++) {

            final Item item = rewardItems[i];
            final ItemDefinition itemDefinition = item.getDefinition();
            final boolean isNoted = itemDefinition != null && itemDefinition.isNoted();

            int itemID = item.getId();

            if (isNoted)
                itemID--;

            displayItems[i] = new Item(itemID, rewardItems[i].getAmount());
        }
        player.getPacketSender().sendInterfaceItems(IntefaceID.SCROLL_REWARD_INVENTORY, Arrays.asList(displayItems));
        player.getPacketSender().sendInterface(IntefaceID.SCROLL_REWARD_INTERFACE);
    }

    public void openScroll(final Player player, final int itemId) {

        final ScrollDifficulty scrollDifficulty = ScrollDifficulty.forScrollID(itemId);
        Optional<ClueTask> task = getCurrentTask(player, scrollDifficulty);
        if (task.isEmpty()) {
            task = Optional.ofNullable(startTask(player, scrollDifficulty, 0, null));
        }
        if (task.isPresent()) {
            ClueScroll clueScroll = task.get().clueScroll;
            ClueGuide clueGuide = clueScroll.getClueGuide();
            clueGuide.display(player);
        }
    }

    /**
     * Get current player task based on the given difficulty
     *
     * @param player     Player to get current task
     * @param difficulty Difficulty of task being searched
     * @return current task if available.
     */
    private Optional<ClueTask> getCurrentTask(final Player player, final ScrollDifficulty difficulty) {

        final ClueScrollManager manager = player.getClueScrollManager();

        switch (difficulty) {
            case EASY:
                return Optional.ofNullable(manager.easyScroll);
            case MEDIUM:
                return Optional.ofNullable(manager.mediumScroll);
            case HARD:
                return Optional.ofNullable(manager.hardScroll);
            case ELITE:
                return Optional.ofNullable(manager.eliteScroll);
        }
        return Optional.empty();
    }

    /**
     * Start a new task with given progress.
     *
     * @param player         Player getting a new task set.
     * @param difficulty     Difficulty of the tasks.
     * @param taskCount      Count of completed tasks.
     * @param scrollProgress Progress log of previous tasks.
     */
    ClueTask startTask(final Player player, final ScrollDifficulty difficulty, final int taskCount, final List<Integer> scrollProgress) {

        ClueTask task = getTask(difficulty);

        /* Run loop until find a task that wasn't completed yet. **/
        if (scrollProgress != null) {
            while (task == null || scrollProgress.contains(task.getTaskID())) {
                task = getTask(difficulty);
            }
        }

        if (task == null)
            return null;

        /* Set current player progress on the Clue Scroll **/
        task.setCount(taskCount);
        task.setScrollProgress(scrollProgress);
        setTask(player, difficulty, task);
        return task;
    }

    /**
     * Get a <b>Cloned</b> version of a Clue Scroll task.
     *
     * @param difficulty The difficulty of task to be gathered.
     * @return a cloned version of original clue scroll task from the referred
     * difficulty.
     */
    public ClueTask getTask(final ScrollDifficulty difficulty) {
        return Misc.random(getTaskList(difficulty)).clone();
    }

    public void setTask(final Player player, final ScrollDifficulty difficulty, final ClueTask task) {

        final ClueScrollManager manager = player.getClueScrollManager();

        switch (difficulty) {
            case EASY:
                manager.easyScroll = task;
                break;
            case MEDIUM:
                manager.mediumScroll = task;
                break;
            case HARD:
                manager.hardScroll = task;
                break;
            case ELITE:
                manager.eliteScroll = task;
                break;
        }
    }

    /**
     * Get a list of tasks based on the difficulty.
     *
     * @param difficulty Difficulty of the clue scroll
     * @return A task list based on clue scroll difficulty
     */
    private ArrayList<ClueTask> getTaskList(final ScrollDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return easyScrolls;
            case MEDIUM:
                return mediumScrolls;
            case HARD:
                return hardScrolls;
            case ELITE:
                return eliteScrolls;
            default:
                return easyScrolls;
        }
    }

    public void findScroll(final Player player, final int itemSlot, final int itemID) {

        final Item item = player.getInventory().get(itemSlot);

        if (item != null && item.getId() == itemID) {

            final int scrollID = getScrollFromCasket(itemID);

            if (scrollID != -1)
                sendNewClue(player, itemSlot, scrollID);
        }
    }

    private int getScrollFromCasket(final int casketID) {
        switch (casketID) {
            case ScrollConstants.ITEM_EASY_SCROLL_REWARD_CASKET:
                return ScrollConstants.ITEM_EASY_SCROLL;
            case ScrollConstants.ITEM_MEDIUM_SCROLL_REWARD_CASKET:
                return ScrollConstants.ITEM_MEDIUM_SCROLL;
            case ScrollConstants.ITEM_HARD_SCROLL_REWARD_CASKET:
                return ScrollConstants.ITEM_HARD_SCROLL;
            case ScrollConstants.ITEM_ELITE_SCROLL_REWARD_CASKET:
                return ScrollConstants.ITEM_ELITE_SCROLL;
        }
        return -1;
    }

    public static void sendNewClue(final Player player, final int itemSlot, final int scrollID) {
        player.getInventory().set(itemSlot, new Item(scrollID, 1));
        player.getInventory().refreshItems();
        new DialogueBuilder(DialogueType.ITEM_STATEMENT)
                .setItem(scrollID, 200)
                .setText("You've found another clue!")
                .start(player);
    }

    public int getScrollRewardCasket(ScrollDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return ScrollConstants.ITEM_EASY_SCROLL_REWARD_CASKET;
            case ELITE:
                return ScrollConstants.ITEM_ELITE_SCROLL_REWARD_CASKET;
            case HARD:
                return ScrollConstants.ITEM_HARD_SCROLL_REWARD_CASKET;
            case MEDIUM:
                return ScrollConstants.ITEM_MEDIUM_SCROLL_REWARD_CASKET;
        }
        return -1;
    }

    int getScrollType(final ScrollDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return ScrollConstants.ITEM_EASY_SCROLL;
            case MEDIUM:
                return ScrollConstants.ITEM_MEDIUM_SCROLL;
            case HARD:
                return ScrollConstants.ITEM_HARD_SCROLL;
            case ELITE:
                return ScrollConstants.ITEM_ELITE_SCROLL;
        }
        return -1;
    }

    public ClueTask getTaskForID(final int taskID) {
        final Stream<ClueTask> scrolls = Stream.concat(Stream.concat(easyScrolls.stream(), mediumScrolls.stream()), Stream.concat(hardScrolls.stream(), eliteScrolls.stream()));
        final Optional<ClueTask> task = scrolls.filter(t -> t.getTaskID() == taskID).findFirst();
        return task.map(ClueTask::clone).orElse(null);
    }

    void rewardParticipationPoints(final Player player, final ScrollDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                ParticipationPoints.addPoints(player, 10, "@dre@from Treasure trial completion</col>");
                break;
            case MEDIUM:
                ParticipationPoints.addPoints(player, 15, "@dre@from Treasure trial completion</col>");
                break;
            case HARD:
                ParticipationPoints.addPoints(player, 20, "@dre@from Treasure trial completion</col>");
                break;
            case ELITE:
                ParticipationPoints.addPoints(player, 30, "@dre@from Treasure trial completion</col>");
                break;
        }
    }
}
