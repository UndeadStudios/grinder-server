package com.grinder.game.content.skill.skillable.impl.agility;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.minigame.impl.agility.AgilityPyramidManager;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.SkillRequirement;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.SkillActionTask;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.AgilityObstacle;
import com.grinder.game.content.skill.skillable.impl.agility.obstacle.impl.*;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ObjectID.OBSTACLE_PIPE_12;

public class Agility {

    // 3355 2847 1

    // Walking on log = 762
    // Going through a pipe = 844
    // Climbing up something = 828
    // Climbing down something = 827
    // Climbing up (as a monkey) = 3487
    // Werewolf Hurdle Emote = 2750
    // Werewolf Sling = 744
    // Swing Rope? = 3067
    // Stepping Stone = 3067
    // More Stepping stones?: = 1604 or 2588 not sure which
    // Climbing up Wildy rocks = 1148
    // Werewolf course object (the skulls?) = 1148
    // Ledge Agility = 756
    // Climbing over a wall = 840
    // Stepping Stones (as a monkey) = 3480
    // Ape Atoll Monkeybars (as a monkey) = 3483
    // Ape Atoll Climb Slope (as a monkey) = 3485
    // Ape Atoll going down the tree? (as a monkey) = 3494
    // Ape Atoll Swing Rope (as a monkey) = 3482
    //
    // Objects
    // Log Object you walk on = 2295
    // Net Object you climb up= 2285
    // Tree Object = 2313
    // Rope Object = 2312
    // Tree branch object = 2314
    // More nets = 2286
    // Pipe object you go through = 154
    // Another pip object you go through = 4058
    // Wildy Pipe object? = 2288
    // Wildy Swing Rope? = 2283
    // Wildy Stepping stone? = 2311
    // Wildy log balance? = 2297
    // Wildy Rocks? = 2328
    // Barb Object 1= 2282
    // Barb Object 2 = 2294
    // Barb Net = 2284
    // Barb Ledge = 2302
    // Barb Ladder = 3205
    // Barbarian Wall = 1948
    // Pyramid Stairs (there are several of these so you may have to do some
    // testing)= 10857
    // Pyramid Wall = 10865
    // Pyramid Log/Plank= 10868
    // Pyramid Gap Jump? = 10863
    // Werewolf Stepping Stone = 5138
    // Werewolf Hurdle = 5133
    // Another Werewolf Hurdle = 5134
    // A final Werewolf Hurdle = 5135
    // Werewolf Pipes? = 5152
    // Werewolf SKulls = 5136
    // Werewolf Sling = 5141

    /**
     * Climbing up animation
     */
    public static final Animation CLIMB_UP = new Animation(828);

    /**
     * Climbing down animation
     */
    public static final Animation CLIMB_DOWN = new Animation(827);

    /**
     * Log walk animation
     */
    private static final Animation LOG_WALK = new Animation(7134);

    /**
     * Monkey bars climbing
     */
    private static final Animation BAR_CLIMBING = new Animation(743);

    /**
     * Rope swing animation
     */
    private static final Animation ROPE_SWING = new Animation(751);

    /**
     * Pulling up animation
     */
    public static final Animation PULL_UP = new Animation(2585);

    /**
     * Jump down animation
     */
    public static final Animation JUMP_DOWN = new Animation(2586);

    /**
     * Land down animation
     */
    public static final Animation LAND_DOWN = new Animation(2588);

    /**
     * Prepare zip line animation
     */
    private static final Animation PREPARE_ZIP = new Animation(1601);

    /**
     * Zip lining animation
     */
    private static final Animation ZIP_LINE = new Animation(1602);

    /**
     * Climbing a wall
     */
    private static final Animation CLIMB_WALL = new Animation(840);

    /**
     * Crossing a ledge
     */
    public static final Animation CROSS_LEDGE = new Animation(756);

    /**
     * Jumping
     */
    private static final Animation JUMP = new Animation(3067);

    /**
     * The stepping stone animation
     */
    private static final Animation STEPPING_STONE = new Animation(1604);

    /**
     * Reset Character Animation
     */
    private static final Animation RESET_ANIMATION = new Animation(65535);

    /*
     * Messages that are sent to the player while training Agility skill
     */

    private static final String[][] AGILITY_MESSAGES = {
            { "@whi@The Brimhaven Agility Course rewards you with Marks of Grace upon tagging the pillars." },
            { "@whi@The best Agility course is the Wilderness Agility Course!" },
            { "@whi@The best Agility Rooftop Course is the one in West Ardougne." },
            { "@whi@Every equipped Graceful gear piece increases your experience gain in Agility skill!" },
            { "@whi@Grab your Graceful gear from Grace located in the Barbarian Outpost!" },
            { "@whi@Doing Agility with the skillcape equipped will give you 20% bonus experience gain!" },
    };

    public static String currentMessage;

    public static void sendSkillRandomMessages(Player player) {
        currentMessage = AGILITY_MESSAGES[Misc.getRandomInclusive(AGILITY_MESSAGES.length - 1)][0];
        player.getPacketSender().sendMessage("<img=779> " + currentMessage);
    }

    /**
     * The agility courses
     */
    public enum Course {
        DRAYNOR_ROOFTOP(7, 120),

        AL_KHARID_ROOFTOP(8, 180),

        VARROCK_ROOFTOP(9, 125),

        CANIFIS_ROOFTOP(8, 240),

        SEERS_ROOFTOP(6, 570),

        FALADOR_ROOFTOP(13, 440),

        POLLNIVNEACH_ROOFTOP(9, 890),

        RELLEKKA_ROOFTOP(7, 475),

        ARDOUNGE_ROOFTOP(7, 529),

        GNOME(7, 80),

        BARBARIAN(7, 250),

        WILDERNESS(5, 900),

        PYRAMID(25, 700),

        OBSTACLES(0, 0),

        SKIP_LOOP(0, 0),

        ;

        /**
         * The amount of courses
         */
        private int courses;

        /**
         * The experience for completing the whole course
         */
        private int experience;

        /**
         * The course
         *
         * @param courses    the courses
         * @param experience the experience
         */
        Course(int courses, int experience) {
            this.setCourses(courses);
            this.setExperience(experience);
        }

        /**
         * Sets the courses
         *
         * @return the courses
         */
        public int getCourses() {
            return courses;
        }

        /**
         * Sets the courses
         *
         * @param courses the courses
         */
        public void setCourses(int courses) {
            this.courses = courses;
        }

        /**
         * Sets the experience
         *
         * @return the experience
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Sets the experience
         *
         * @param experience the experience
         */
        public void setExperience(int experience) {
            this.experience = experience;
        }
    }

    /**
     * The completed obstacle
     */
    private boolean[] completed = new boolean[Obstacles.values().length];

    /**
     * The obstacle
     */
    private Task obstacle;

    /**
     * The agility obstacles
     */
    public enum Obstacles {

        /**
         * Misc Obstacles
         */
        TAVERLY_PIPE_WEST(new SkillRequirement(16509, 70, 10),
                new CrossAgilityObstacle(new Position(2886, 9799, 0), new Animation(749), new Position(6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                6, new Position(2887, 9799, 0), Course.OBSTACLES),

        TAVERLY_PIPE_EAST(new SkillRequirement(16509, 70, 10),
                new CrossAgilityObstacle(new Position(2892, 9799, 0), new Animation(749), new Position(-6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                6, new Position(2890, 9799, 0), Course.OBSTACLES),

        VARROCK_SEWERS_WEST(new SkillRequirement(16511, 51, 10),
                new CrossAgilityObstacle(new Position(3149, 9906, 0), new Animation(749), new Position(6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                6, new Position(3150, 9906, 0), Course.OBSTACLES),

        VARROCK_SEWERS_EAST(new SkillRequirement(16511, 51, 10),
                new CrossAgilityObstacle(new Position(3155, 9906, 0), new Animation(749), new Position(-6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                6, new Position(3153, 9906, 0), Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_NORTH(new SkillRequirement(21728, 22, 8.5f),
                new CrossAgilityObstacle(new Position(2655, 9573, 0), new Animation(749), new Position(0, -7), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                7, new Position(2655, 9571, 0), Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_SOUTH(new SkillRequirement(21728, 22, 8.5f),
                new CrossAgilityObstacle(new Position(2655, 9566, 0), new Animation(749), new Position(0, 7), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                7, new Position(2655, 9567, 0), Course.OBSTACLES),

        /*
         * Al kharid roof top
         */
        AL_KHARID_ROUGH_WALL(new SkillRequirement(11633, 20, 10),
                new ClimbAgilityObstacle(new Position(3273, 3192, 3), true, CLIMB_UP, null), 2,
                Course.AL_KHARID_ROOFTOP, new Position(3269, 3169, 3), 175),

        AL_KHARID_TIGHTROPE(new SkillRequirement(14398, 20, 30), new CrossAgilityObstacle(new Position(3272, 3182, 3),
                null, new Position(0, -10), LOG_WALK, null, 200, 4, 2, new Sound(2495), true), 10, Course.AL_KHARID_ROOFTOP),

        AL_KHARID_CABLE(new SkillRequirement(14402, 20, 40), new ForceMovementAgilityObstacle(new Position(3268, 3166, 3), null,
                new Position(15, 0), ROPE_SWING, null, 50, 4, 1, new Animation(497), new Position(3269, 3166, 1), "You begin an almighty run-up...", "...You gained enough momentum to swing to the other side!"), 5, Course.AL_KHARID_ROOFTOP,
                new Position(3294, 3165, 3), 200),

        AL_KHARID_ZIP_LINE(new SkillRequirement(14403, 20, 40), new CrossAgilityObstacle(new Position(3302, 3163, 3),
                PREPARE_ZIP, new Position(14, 0, -2), ZIP_LINE, null, 4200, 5, 1, false, true, new Sound(1934), false), 8, Course.AL_KHARID_ROOFTOP,
                new Position(3315, 3175, 2), 175),

        AL_KHARID_TREE(
                new SkillRequirement(14404, 20, 10),
                new JumpAgilityObstacle(new Position(3318, 3167, 1),
                                new Position(3317, 3169, 1), new Position(3317, 3174, 2), new Animation(1122)),
                6, Course.AL_KHARID_ROOFTOP),

        AL_KHARID_ROOF_BEAMS(new SkillRequirement(11634, 20, 5),
                new ClimbAgilityObstacle(new Position(3316, 3180, 3), true, CLIMB_UP, null), 3,
                Course.AL_KHARID_ROOFTOP,
                new Position(3318, 3183, 3), 175),

        AL_KHARID_TIGHTROPE_2(new SkillRequirement(14409, 20, 15), new CrossAgilityObstacle(new Position(3314, 3186, 3),
                null, new Position(-12, 0), LOG_WALK, null, 200, 4, 3, new Sound(2495), true), 12, Course.AL_KHARID_ROOFTOP),

        AL_KHARID_GAP(new SkillRequirement(14399, 20, 30),
                new ClimbAgilityObstacle(new Position(3299, 3194, 0), false, JUMP_DOWN, LAND_DOWN, new Sound(2462, 15)), 3,
                Course.AL_KHARID_ROOFTOP),

        /*
         * Varrock Rooftop
         */

        VARROCK_CLIMB(new SkillRequirement(14412, 30, 12), new ClimbAgilityObstacle(new Position(3220, 3414, 2), true, CLIMB_UP, null,
                new ClimbAgilityObstacle(new Position(3219, 3414, 3), true, PULL_UP, null, new Sound(2468)))
                , 5, Course.VARROCK_ROOFTOP),

        VARROCK_CLOTHES_LINE(new SkillRequirement(14413, 30, 21), new JumpAgilityObstacle(new Position(3212, 3414, 2),
                new Position(3210, 3414, 2), new Position(3208, 3414, 3), STEPPING_STONE, new Sound(2461)), 5, Course.VARROCK_ROOFTOP),

        VARROCK_LEAP_GAP(new SkillRequirement(14414, 30, 17),
                new ClimbAgilityObstacle(new Position(3197, 3416, 1), true, JUMP_DOWN, null, new Sound(2462, 15)),
                5, Course.VARROCK_ROOFTOP, new Position(3196, 3416, 1), 175),

        VARROCK_BALANCE_WALL(new SkillRequirement(14832, 30, 25),
                new MultipleAgilityObstacles(new SkillRequirement(14832, 30, 0), new AgilityObstacle[]{
                        new ClimbAgilityObstacle(new Position(3190, 3414, 1), true, JUMP, new Animation(1120), new Sound(2461)),
                        new ForceMovementAgilityObstacle(new Position(3190, 3414, 1), null, new Position(0, -1), new Animation(1121), null, 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3190, 3413, 1), null, new Position(0, -1), new Animation(1121), null, 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3190, 3412, 1), null, new Position(0, -1), new Animation(1121), null, 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3190, 3411, 1), null, new Position(0, -1), new Animation(1121), null, 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3190, 3410, 1), null, new Position(0, -1), new Animation(1120), null, 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3190, 3409, 1), null, new Position(0, -3, 0), CROSS_LEDGE, null, 120, 5, 2, new Sound(2451), true),
                        new ClimbAgilityObstacle(new Position(3192, 3406, 3), true, JUMP, null, new Sound(2468), new Position(3192, 3406, 3)),
                }, new int[]{3, 3, 3, 3, 3, 3, 6, 3}), 29, Course.VARROCK_ROOFTOP, new Position(3197, 3403, 3), 175),

        VARROCK_LEAP_GAP_2(new SkillRequirement(14833, 30, 9), new ClimbAgilityObstacle(new Position(3193, 3399, 2), true, JUMP, null,
                new ClimbAgilityObstacle(new Position(3193, 3398, 3), true, PULL_UP, null, new Sound(2468), new Position(3193, 3398, 3)))
                , 5, Course.VARROCK_ROOFTOP, new Position(3190, 3396, 3), 175),

        VARROCK_LEAP_GAP_3(new SkillRequirement(14834, 30, 22), new ClimbAgilityObstacle(new Position(3215, 3399, 3), true, JUMP, null, new Sound(2461),
                new ClimbAgilityObstacle(new Position(3218, 3399, 3), true, JUMP, null, new Sound(2468)))
                , 5, Course.VARROCK_ROOFTOP),

        VARROCK_LEAP_GAP_4(new SkillRequirement(14835, 30, 4),
                new ClimbAgilityObstacle(new Position(3236, 3403, 3), true, JUMP, null, new Sound(2462, 15))
                , 3, Course.VARROCK_ROOFTOP),

        VARROCK_LEAP_GAP_5(new SkillRequirement(14836, 30, 3),

                new ClimbAgilityObstacle(new Position(3237, 3410, 3), true, CLIMB_UP, null, new Sound(1936), new Position(3237, 3410, 3))
                , 3, Course.VARROCK_ROOFTOP),

        VARROCK_JUMP_OFF(new SkillRequirement(14841, 30, 0),
                new ClimbAgilityObstacle(new Position(3236, 3416, 2), true, JUMP_DOWN, null, new Sound(2461), new ClimbAgilityObstacle(new Position(3236, 3417, 0), true, JUMP_DOWN, null, new Sound(2462, 15)))
                , 5, Course.VARROCK_ROOFTOP),

        CANIFIS_CLIMB_TREE(new SkillRequirement(14843, 40, 10), new MultipleAgilityObstacles(
                new SkillRequirement(14843, 40, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(3507, 3488, 0), null, new Position(0, 1), new Animation(819), null, 4, 5, 0),

                new ForceMovementAgilityObstacle(new Position(3507, 3489, 0), null, new Position(-3, 0), new Animation(1765), null, 65, 2, 1, new Sound(1705), false),
                new ClimbAgilityObstacle(new Position(3506, 3492, 2), true, null, RESET_ANIMATION)
        }, new int[]{3, 4, 3}
        ), 10, Course.CANIFIS_ROOFTOP, new Position(3508, 3495, 2), 200),

        CANIFIS_JUMP_GAP_1(new SkillRequirement(14844, 40, 8),
                new ClimbAgilityObstacle(new Position(3502, 3504, 2), false, new Animation(2586), new Animation(2588), new Sound(2462, 15)), 3, Course.CANIFIS_ROOFTOP,
                new Position(3500, 3506, 2), 200),

        CANIFIS_JUMP_GAP_2(new SkillRequirement(14845, 40, 8),
                new ClimbAgilityObstacle(new Position(3493, 3504, 2), false, new Animation(2586), new Animation(2588), new Sound(2462, 15)), 3, Course.CANIFIS_ROOFTOP,
                new Position(3490, 3503, 2), 200),

        CANIFIS_JUMP_GAP_3(new SkillRequirement(14848, 40, 10),
                new MultipleAgilityObstacles(new SkillRequirement(14848, 40, 0), new AgilityObstacle[]{
                        new ClimbAgilityObstacle(new Position(3481, 3499, 2), true, null, null, new Sound(2468)),
                        new ClimbAgilityObstacle(new Position(3479, 3499, 3), true, PULL_UP, null),
                }, new int[]{1, 2}),
                4, Course.CANIFIS_ROOFTOP, new Position(3476, 3496, 3), 200),

        CANIFIS_JUMP_GAP_4(new SkillRequirement(14846, 40, 8),
                new ClimbAgilityObstacle(new Position(3478, 3486, 2), true, new Animation(2586), new Animation(2588), new Sound(2462, 15)), 3, Course.CANIFIS_ROOFTOP,
                new Position(3478, 3485, 2), 200),

        CANIFIS_POLE_VAULT(new SkillRequirement(14894, 40, 10),
                new MultipleAgilityObstacles(new SkillRequirement(14894, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3478, 3485, 2), null, new Position(2, -2), new Animation(1995), null, 4, 2, 4),
                        new ForceMovementAgilityObstacle(new Position(3481, 3481, 2), null, new Position(8, -5), new Animation(7132), null, 35, 2, 4),
                        new ClimbAgilityObstacle(new Position(3489, 3476, 3), true, RESET_ANIMATION, new Animation(2588)),
                },
                        new int[]{3, 3, 8}),
                11, Course.CANIFIS_ROOFTOP),

        CANIFIS_JUMP_GAP_5(new SkillRequirement(14847, 40, 11),
                new ClimbAgilityObstacle(new Position(3510, 3476, 2), false, new Animation(2586), new Animation(2588), new Sound(2462, 15)), 3, Course.CANIFIS_ROOFTOP),

        CANIFIS_JUMP_GAP_6(new SkillRequirement(14897, 40, 0),
                new ClimbAgilityObstacle(new Position(3510, 3485, 0), false, new Animation(2586), new Animation(2588), new Sound(2462, 15)), 3, Course.CANIFIS_ROOFTOP),


        /*
         * Falador Rooftop
         */

        FALADOR_CLIMB_UP(new SkillRequirement(14898, 50, 8),
                new ClimbAgilityObstacle(new Position(3036, 3342, 3), true, CLIMB_UP, null),
                2, Course.FALADOR_ROOFTOP, new Position(3046, 3346, 3), 185),

        FALADOR_CROSS_ROPE(new SkillRequirement(14899, 50, 17),
                new CrossAgilityObstacle(new Position(3039, 3343, 3), null, new Position(8, 0), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),
                8, Course.FALADOR_ROOFTOP),

        FALADOR_HAND_HOLD(new SkillRequirement(14901, 50, 45),
                new MultipleAgilityObstacles(
                        new SkillRequirement(14901, 50, 0), new AgilityObstacle[]{

                        new ClimbAgilityObstacle(new Position(3050, 3351, 2), true, null, new Animation(1118)),
                        new ClimbAgilityObstacle(new Position(3051, 3351, 2), true, null, new Animation(1118), new Sound(2459, 35)),
                        new ForceMovementAgilityObstacle(new Position(3051, 3352, 2), null, new Position(1, 0), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3051, 3353, 2), null, new Position(0, 1), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new FailAgilityObstacle(new Position(3051, 3354, 2), null, new Animation(1119), new Animation(2588), 85, 1, 4, new Position(3051, 3352, 0)),
                        new ForceMovementAgilityObstacle(new Position(3051, 3354, 2), null, new Position(0, 1), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3051, 3355, 2), null, new Position(0, 1), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new FailAgilityObstacle(new Position(3051, 3356, 2), null, new Animation(1119), new Animation(2588), 85, 1, 4, new Position(3051, 3355, 0)),
                        new ForceMovementAgilityObstacle(new Position(3051, 3356, 2), null, new Position(0, 1), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3051, 3356, 2), null, new Position(0, 1), new Animation(1118), new Animation(1118), 50, 2, 3, new Sound(2459, 35), false),
                        new ClimbAgilityObstacle(new Position(3050, 3357, 3), true, new Animation(1118), RESET_ANIMATION),
                }, new int[]{1, 1, 2, 3, 1, 3, 1, 1, 4, 4, 1}
                ), 22, Course.FALADOR_ROOFTOP, new Position(3048, 3363, 3), 185),

        FALADOR_JUMP_GAP_1(new SkillRequirement(14903, 50, 20),
                new ForceMovementAgilityObstacle(new Position(3048, 3358, 3), null, new Position(-1, 3), new Animation(741), null, 25, 4, 0, new Sound(2461), false), 5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_2(new SkillRequirement(14904, 50, 20),
                new ForceMovementAgilityObstacle(new Position(3045, 3361, 3), null, new Position(-4, 0), new Animation(741), null, 25, 4, 3, new Sound(2461), false), 5, Course.FALADOR_ROOFTOP,
                new Position(3028, 3353, 3), 175),

        FALADOR_TIGHT_ROPE_2(new SkillRequirement(14905, 50, 45), new MultipleAgilityObstacles(
                new SkillRequirement(14905, 50, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(3035, 3362, 3), null, new Position(-1, 0), new Animation(819), null, 4, 2, 5),
                new CrossAgilityObstacle(new Position(3034, 3362, 3), null, new Position(-7, -7), LOG_WALK, null, 4, 2, 5, new Sound(2495), true),
        }, new int[]{1, 8}
        ), 9, Course.FALADOR_ROOFTOP, new Position(3019, 3344, 3), 185),

        FALADOR_TIGHT_ROPE_3(new SkillRequirement(14911, 50, 40),
                new CrossAgilityObstacle(new Position(3026, 3353, 3), null, new Position(-6, 0), LOG_WALK, null, 4, 2, 3, new Sound(2495), true),
                6, Course.FALADOR_ROOFTOP, new Position(3022, 3334, 3), 185),

        FALADOR_JUMP_GAP_3(new SkillRequirement(14919, 50, 25),
                new ForceMovementAgilityObstacle(new Position(3018, 3353, 3), null, new Position(0, -5), new Animation(1603), null, 20, 4, 2, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_4(new SkillRequirement(14920, 50, 10), new ForceMovementAgilityObstacle(new Position(3016, 3345, 3), null, new Position(-2, 0), new Animation(1603), null, 25, 4, 3, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_5(new SkillRequirement(14921, 50, 10), new ForceMovementAgilityObstacle(new Position(3013, 3344, 3), null, new Position(0, -2), new Animation(1603), null, 25, 4, 2, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_6(new SkillRequirement(14922, 50, 10), new ForceMovementAgilityObstacle(new Position(3013, 3335, 3), null, new Position(0, -2), new Animation(1603), null, 25, 4, 2, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_7(new SkillRequirement(14924, 50, 10), new ForceMovementAgilityObstacle(new Position(3017, 3333, 3), null, new Position(2, 0), new Animation(1603), null, 25, 4, 1, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_8(new SkillRequirement(14925, 50, 0),
                new ClimbAgilityObstacle(new Position(3027, 3334, 3), false, new Animation(1603), new Animation(2586), new Sound(2461), new ClimbAgilityObstacle(new Position(3029, 3334, 0), false, null, new Animation(2588), new Sound(2462, 15))),
                5, Course.FALADOR_ROOFTOP),

        FALADOR_JUMP_GAP_9(new SkillRequirement(14923, 50, 0), new ForceMovementAgilityObstacle(new Position(3013, 3335, 3), null, new Position(0, -2), new Animation(1603), null, 25, 4, 1, new Sound(2461), false),
                5, Course.FALADOR_ROOFTOP),

        /**
         * Pollnivneach Agility Rooftop
         */
        POLLNIVEACH_BASKET_JUMP(new SkillRequirement(14935, 70, 10),
                new ClimbAgilityObstacle(new Position(3351, 2962, 1), true, JUMP, null, new ClimbAgilityObstacle(new Position(3351, 2964, 1), true, null, null)),
                3, Course.POLLNIVNEACH_ROOFTOP, new Position(3346, 2965, 1), 185),

        POLLNIVEACH_MARKET_STALL(new SkillRequirement(14936, 70, 45),
                new MultipleAgilityObstacles(new SkillRequirement(14936, 70, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3349, 2968, 1), null, new Position(1, 0), new Animation(819), null, 1, 2, 0),
                        new ForceMovementAgilityObstacle(new Position(3350, 2968, 1), null, new Position(0, 3), JUMP, null, 25, 2, 0, new Sound(2465), false),
                        new ForceMovementAgilityObstacle(new Position(3350, 2971, 1), null, new Position(2, 2), JUMP, null, 25, 2, 0, new Sound(2465), false),
                },
                        new int[]{1, 5, 4}),
                12, Course.POLLNIVNEACH_ROOFTOP, new Position(3362, 2979, 1), 185),

        POLLNIVEACH_BANNER(new SkillRequirement(14937, 70, 65),
                new MultipleAgilityObstacles(new SkillRequirement(14937, 70, 0), new AgilityObstacle[]{
                        new ClimbAgilityObstacle(new Position(3355, 2976, 2), true, null, null),
                        new ForceMovementAgilityObstacle(new Position(3355, 2976, 2), null, new Position(2, 1), JUMP, null, 30, 2, 0),
                        new ForceMovementAgilityObstacle(new Position(3357, 2977, 2), new Animation(1121), new Position(3, 0), new Animation(1121), RESET_ANIMATION, 40, 2, 0),
                        new ClimbAgilityObstacle(new Position(3360, 2977, 1), true, null, RESET_ANIMATION),
                }, new int[]{3, 4, 3, 1}),
                12, Course.POLLNIVNEACH_ROOFTOP, new Position(3366, 2986, 1), 185),

        POLLNIVEACH_JUMP_GAP(new SkillRequirement(14938, 70, 35),
                new MultipleAgilityObstacles(new SkillRequirement(14938, 70, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3362, 2977, 1), null, new Position(3, -1), JUMP, null, 25, 3, 1, new Sound(2465), false),
                        new ClimbAgilityObstacle(new Position(3366, 2976, 1), true, PULL_UP, null)},
                        new int[]{4, 4}),
                8, Course.POLLNIVNEACH_ROOFTOP),

        POLLNIVEACH_TREE_1(
                new SkillRequirement(14939, 70, 75),
                new MultipleAgilityObstacles(new SkillRequirement(14939, 70, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3366, 2976, 1), null, new Position(1, 0), new Animation(819), null, 3, 2, 0),
                        new ForceMovementAgilityObstacle(new Position(3367, 2977, 1), null, new Position(0, 3), JUMP, null, 30, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3368, 2979, 1), new Animation(1121), new Position(0, 3), new Animation(1121), RESET_ANIMATION, 30, 2, 0, new Sound(2459, 35), false)
                },
                        new int[]{2, 4, 4}),
                12, Course.POLLNIVNEACH_ROOFTOP),

        POLLNIVEACH_ROUGH_WALL(new SkillRequirement(14940, 70, 5), new ClimbAgilityObstacle(new Position(3365, 2983, 2), true, CLIMB_UP, null), 4, Course.POLLNIVNEACH_ROOFTOP,
                new Position(3363, 2995, 2), 185),

        POLLNIVEACH_MONKEY_BARS(new SkillRequirement(14941, 70, 55),
                new CrossAgilityObstacle(new Position(3358, 2984, 2), new Animation(742), new Position(0, 8), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                8, Course.POLLNIVNEACH_ROOFTOP, new Position(3358, 3004, 2), 185),

        POLLNIVEACH_TREE_2(new SkillRequirement(14944, 70, 60),
                new MultipleAgilityObstacles(new SkillRequirement(14944, 70, 0), new AgilityObstacle[]{
                        //new CrossAgilityObstacle(new Position(3359, 2994, 2), null, new Position(0, 0), new Animation(819), null, 3, 2, 0),
                        new ForceMovementAgilityObstacle(new Position(3359, 2995, 2), null, new Position(1, 2), JUMP, null, 30, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(3360, 2998, 2), null, new Position(-1, 3), JUMP, null, 30, 2, 0, new Sound(2459, 35), false),
                },
                        new int[]{5, 5}),
                11, Course.POLLNIVNEACH_ROOFTOP),

        POLLNIVEACH_CLOTHES_LINE(new SkillRequirement(14945, 70, 0),
                new MultipleAgilityObstacles(new SkillRequirement(14945, 70, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3362, 3002, 2), null, new Position(1, -2), JUMP, null, 30, 2, 2),
                        new ClimbAgilityObstacle(new Position(3363, 2998, 0), false, new Animation(2586), new Animation(2588))
                }, new int[]{4, 2}), 7, Course.POLLNIVNEACH_ROOFTOP),

        RELLEKKA_ROUGH_WALL(new SkillRequirement(14946, 80, 20),
                new ClimbAgilityObstacle(new Position(2625, 3676, 3), true, CLIMB_UP, null), 2, Course.RELLEKKA_ROOFTOP,
                new Position(2621, 3664, 3), 175),

        RELLEKKA_LEAP_GAP_1(new SkillRequirement(14947, 80, 30),
                new ForceMovementAgilityObstacle(new Position(2622, 3672, 3), null, new Position(0, -4), JUMP, null, 30, 2, 2, new Sound(2465), false),
                5, Course.RELLEKKA_ROOFTOP, new Position(2627, 3652, 3), 175),

        RELLEKKA_TIGHT_ROPE_1(new SkillRequirement(14987, 80, 40),
                new CrossAgilityObstacle(new Position(2622, 3658, 3), null, new Position(4, -4), LOG_WALK, null, 3, 2, 2, new Sound(2495), true),
                5, Course.RELLEKKA_ROOFTOP, new Position(2624, 3657, 0), 110, 3, new Animation(770), 3, new Position(2625, 3656, 3), 175),

        RELLEKKA_LEAP_GAP2(new SkillRequirement(14990, 80, 85), new MultipleAgilityObstacles(new SkillRequirement(14990, 80, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2629, 3655, 3), null, new Position(0, 4), JUMP, null, 20, 2, 0, new Sound(2465), false),
                new ClimbAgilityObstacle(new Position(2629, 3659, 2), true, null, null),
                new CrossAgilityObstacle(new Position(2629, 3659, 2), null, new Position(6, 0), CROSS_LEDGE, null, 3, 4, 2),
                new CrossAgilityObstacle(new Position(2635, 3659, 2), null, new Position(0, -1), new Animation(819), null, 3, 2, 2),
                new ClimbAgilityObstacle(new Position(2635, 3658, 3), true, null, null),
                new CrossAgilityObstacle(new Position(2635, 3658, 3), null, new Position(5, -5), LOG_WALK, null, 3, 5, 3)
        }, new int[]{3, 5, 6, 1, 2, 2}), 25, Course.RELLEKKA_ROOFTOP, new Position(2657, 3674, 3), 175),

        RELLEKKA_HURDLE_GAP(new SkillRequirement(14991, 80, 25),
                new ForceMovementAgilityObstacle(new Position(2643, 3653, 3), null, new Position(0, 3), JUMP, null, 20, 2, 0, new Sound(2465), false),
                6, Course.RELLEKKA_ROOFTOP),

        RELLEKKA_TIGHT_ROPE_2(new SkillRequirement(14992, 80, 105),
                new CrossAgilityObstacle(new Position(2647, 3663, 3), null, new Position(7, 7), LOG_WALK, null, 3, 2, 6, new Sound(2495), true),
                9, Course.RELLEKKA_ROOFTOP),

        RELLEKKA_PILE_OF_FISH(new SkillRequirement(14994, 80, 0), new MultipleAgilityObstacles(new SkillRequirement(14994, 80, 0), new AgilityObstacle[]{
                new ClimbAgilityObstacle(new Position(2653, 3676, 0), false, JUMP_DOWN, new Animation(2588), new Sound(2465)),
                new CrossAgilityObstacle(new Position(2653, 3676, 0), null, new Position(-1, 0), new Animation(819), null, 3, 2, 3)
        }, new int[]{5, 2}), 7, Course.RELLEKKA_ROOFTOP),

        /**
         * Ardounge Rooftop
         */

        ARDOUGNE_WOODEN_BEAMS(new SkillRequirement(15608, 90, 43),
                new MultipleAgilityObstacles(new SkillRequirement(15608, 90, 0), new AgilityObstacle[]{
                        new ClimbAgilityObstacle(new Position(2673, 3298, 1), true, new Animation(737), null, new Sound(2470)),
                        new ClimbAgilityObstacle(new Position(2673, 3298, 2), true, new Animation(737), null, new Sound(2470)),
                        new ClimbAgilityObstacle(new Position(2671, 3299, 3), true, new Animation(737), new Animation(2588), new Sound(2470)),
                }, new int[]{1, 1, 1}),
                4, Course.ARDOUNGE_ROOFTOP, new Position(2671, 3306, 3), 175),

        ARDOUGNE_JUMP_GAP_1(new SkillRequirement(15609, 90, 65), new MultipleAgilityObstacles(new SkillRequirement(15609, 90, 0), new AgilityObstacle[]{
                new ClimbAgilityObstacle(new Position(2667, 3311, 1), true, new Animation(2586), new Animation(2588)),
                new ClimbAgilityObstacle(new Position(2665, 3315, 1), true, new Animation(2586), new Animation(2588)),
                new ClimbAgilityObstacle(new Position(2665, 3318, 3), true, new Animation(2586), new Animation(2588), new Position(2665, 3320, 1)),
        }, new int[]{3, 3, 3}),
                9, Course.ARDOUNGE_ROOFTOP, new Position(2653, 3312, 3), 175),

        ARDOUGNE_WALK_PLANK(new SkillRequirement(26635, 90, 50),
                new CrossAgilityObstacle(new Position(2662, 3318, 3), null, new Position(-6, 0), LOG_WALK, null, 4, 4, 3, new Sound(2470), true),
                6, Course.ARDOUNGE_ROOFTOP, new Position(2660, 3317, 0), 100, 3, new Animation(770), 4, new Position(2653, 3301, 3), 175),

        ARDOUNGE_JUMP_GAP_2(new SkillRequirement(15610, 90, 21),
                new ClimbAgilityObstacle(new Position(2653, 3314, 3), true, new Animation(2586), new Animation(2588), new Position(2653, 3314, 3), new Sound(2465)), 4, Course.ARDOUNGE_ROOFTOP),

        ARDOUGNE_JUMP_GAP_3(new SkillRequirement(15611, 90, 28),
                new ClimbAgilityObstacle(new Position(2651, 3308, 3), false, new Animation(2586), new Animation(2588), new Position(2649, 3308, 3), new Sound(2465)), 4, Course.ARDOUNGE_ROOFTOP),

        ARDOUNGE_STEEP_ROOF(new SkillRequirement(28912, 90, 57),
                new CrossAgilityObstacle(new Position(2653, 3300, 3), null, new Position(3, -3), CROSS_LEDGE, null, 4, 2, 4, new Sound(2470), true),
                4, Course.ARDOUNGE_ROOFTOP),

        ARDOUNGE_JUMP_GAP_4(new SkillRequirement(15612, 90, 0), new MultipleAgilityObstacles(new SkillRequirement(15609, 90, 0), new AgilityObstacle[]{
                new ClimbAgilityObstacle(new Position(2658, 3298, 1), true, new Animation(2586), new Animation(2588), new Position(2669, 3298, 3), new Sound(2462, 15)),
                new CrossAgilityObstacle(new Position(2658, 3298, 1), null, new Position(3, 0), new Animation(819), null, 2, 2, 1),
                new ClimbAgilityObstacle(new Position(2663, 3297, 1), true, new Animation(2586), new Animation(2588), new Position(2663, 3297, 1), new Sound(2462, 15)),
                new CrossAgilityObstacle(new Position(2663, 3297, 1), null, new Position(3, 0), new Animation(819), null, 2, 2, 1),
                new ClimbAgilityObstacle(new Position(2667, 3297, 1), true, new Animation(2586), new Animation(2588), new ClimbAgilityObstacle(new Position(2668, 3297, 0), false, new Animation(2468), new Animation(2588), new Sound(2462, 15))),
        }, new int[]{3, 5, 3, 5, 3, 2}),
                21, Course.ARDOUNGE_ROOFTOP),
        /*
         * Gnome agility XP per lap: 3600
         */

        GNOME_LOG_BALANCE(new SkillRequirement(23145, 1, 60), new CrossAgilityObstacle(new Position(2474, 3436, 0),
                null, new Position(0, -7), LOG_WALK, null, 4, 2, 2, new Sound(2470), true, "You walk carefully across the slippery log...", "...You make it safely to the other side."), 7, Course.GNOME),

        GNOME_NET_UP(new SkillRequirement(23134, 1, 30),
                new ClimbAgilityObstacle(new Position(2473, 3424, 1), true, CLIMB_UP, null, "You climb the netting...", ""), 3, Course.GNOME),

        GNOME_BRANCH_UP(new SkillRequirement(23559, 1, 30),
                new ClimbAgilityObstacle(new Position(2473, 3420, 2), true, CLIMB_UP, null, "You climb the tree...", "...To the platform above."), 3, Course.GNOME),

        GNOME_ROPE_BALANCE_W_E(new SkillRequirement(23557, 1, 50), new CrossAgilityObstacle(new Position(2477, 3420, 2),
                null, new Position(6, 0), LOG_WALK, null, 200, 4, 1, new Sound(2495), true, "You carefully cross the tightrope.", ""), 6, Course.GNOME),

		/*GNOME_ROPE_BALANCE_E_W(new SkillRequirement(23558, 1, 50), new CrossAgilityObstacle(new Position(2483, 3420, 2),
				null, new Position(-6, 0), LOG_WALK, null, 200, 4, 3, new Sound(2495), true), 5, Course.GNOME),*/

        GNOME_BRANCH_DOWN(new SkillRequirement(23560, 1, 35),
                new ClimbAgilityObstacle(new Position(2486, 3419, 0), true, CLIMB_DOWN, null, "You climb down the tree...", "You land on the ground."), 3, Course.GNOME),

        GNOME_NET_OVER_1(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2483, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2483, 3425, 0), 3, Course.GNOME),

        GNOME_NET_OVER_2(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2484, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2484, 3425, 0), 3, Course.GNOME),

        GNOME_NET_OVER_3(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2485, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2485, 3425, 0), 3, Course.GNOME),

        GNOME_NET_OVER_4(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2486, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2486, 3425, 0), 3, Course.GNOME),

        GNOME_NET_OVER_5(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2487, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2487, 3425, 0), 3, Course.GNOME),

        GNOME_NET_OVER_6(new SkillRequirement(23135, 1, 30),
                new ClimbAgilityObstacle(new Position(2488, 3427, 0), true, CLIMB_UP, null, "You climb the netting...", ""), new Position(2488, 3425, 0), 3, Course.GNOME),

        GNOME_PIPE1(new SkillRequirement(23138, 1, 70), new MultipleAgilityObstacles(new SkillRequirement(23138, 1, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(2484, 3430, 0), null, new Position(0, 1), new Animation(749), null, 4, 2, 2, new Sound(2489), false),
                new CrossAgilityObstacle(new Position(2484, 3431, 0), null, new Position(0, 2), new Animation(2590), null, 4, 2, 2),
                new CrossAgilityObstacle(new Position(2484, 3433, 0), null, new Position(0, 1), new Animation(2590), null, 4, 2, 2),
                new CrossAgilityObstacle(new Position(2484, 3435, 0), null, new Position(0, 2), new Animation(749), null, 4, 2, 2),
        }, new int[] {1, 4, 1, 2}),
                10, new Position(2484, 3431, 0), Course.GNOME),

        GNOME_PIPE2(new SkillRequirement(23138, 1, 70), new MultipleAgilityObstacles(new SkillRequirement(23138, 1, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(2487, 3430, 0), null, new Position(0, 1), new Animation(749), null, 4, 2, 2, new Sound(2489), false),
                new CrossAgilityObstacle(new Position(2487, 3431, 0), null, new Position(0, 2), new Animation(2590), null, 4, 2, 2),
                new CrossAgilityObstacle(new Position(2487, 3433, 0), null, new Position(0, 1), new Animation(2590), null, 4, 2, 2),
                new CrossAgilityObstacle(new Position(2487, 3435, 0), null, new Position(0, 2), new Animation(749), null, 4, 2, 2),
        }, new int[] {1, 4, 1, 2}),
                10, new Position(2487, 3431, 0), Course.GNOME),

        /*
         * Barbarian course XP per lap: 6000
         */


        BARBARIAN_ENTER_PIPE(new SkillRequirement(20210, 35, 0),
                new CrossAgilityObstacle(new Position(2552, 3561,0), 1,null, new Position(0, -3), new Animation(749), null, 50, 5, 2), new Position(2552, 3561, 0), 5, Course.OBSTACLES),

        BARBARIAN_LEAVE_PIPE(new SkillRequirement(20210, 35, 0),
                new CrossAgilityObstacle(new Position(2552, 3558,0), 1,null, new Position(0, 3), new Animation(749), null, 50, 5, 2), new Position(2552, 3558, 0),5, Course.OBSTACLES),

        BARBARIAN_ROPE(new SkillRequirement(23131, 20, 80), new MultipleAgilityObstacles(new SkillRequirement(23131, 20, 0), new AgilityObstacle[] {
            new CrossAgilityObstacle(new Position(2550, 3554, 0), null, new Position(1, 0), new Animation(819), null, 200, 2, 1),
                new ForceMovementAgilityObstacle(new Position(2551, 3554, 0), null, new Position(0, -5), ROPE_SWING, null, 30, 2, 2, new Sound(2494), false, new Animation(497), new Position(2551, 3550, 0), "You skillfully swing across."),
        }, new int[] {1, 5})
                , new Position(2551, 3554, 0),6, Course.BARBARIAN),

        BARBARIAN_LOG_BALANCE(new SkillRequirement(23144, 35, 90), new MultipleAgilityObstacles(new SkillRequirement(23144, 35, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(2551, 3546, 0), null, new Position(-5, 0), LOG_WALK, null, 200, 2, 3, new Sound(2470), true, "You walk carefully across the slippery log...", ""),
                new FailAgilityObstacle(new Position(2546, 3546, 0), null, 35, 1, 6,
                        new MultipleAgilityObstacles(new SkillRequirement(0, 0, 0), new AgilityObstacle[] {
                                //new ForceMovementAgilityObstacle(new Position(2546, 3546, 0), new Animation(764), new Position(-1, -1), new Animation(764), null, 10, 1, 2, new Sound(2451), true, true),
                                new ForceMovementAgilityObstacle(new Position(2544, 3544, 0), new Animation(764), new Position(-1, -4), new Animation(772), null, 50, 5, 2, new Sound(2451), true, true, "...You lose your footing and fall into the water","Something in the water bites you.")
                        }, new int[] {6}, true), new Graphic(68, 0, 0)),
                //new FailAgilityObstacle(new Position(2546, 3546, 0), null, new Animation(770), new Animation(765), 80, 1, 4, new Position(2546, 3545, 0), new Position(2543, 3543, 0), new Position(-1, 0)),
                new CrossAgilityObstacle(new Position(2546, 3546, 0), null, new Position(-5, 0), LOG_WALK, null, 200, 2, 3, new Sound(2470), true, "", "...You make it safely to the other side."),
        }, new int[]{5, 2, 5}),
                13, Course.BARBARIAN),

        BARBARIAN_NET_OVER(new SkillRequirement(20211, 35, 90),
                new ClimbAgilityObstacle(new Position(2537, 3546, 1), true, CLIMB_UP, null, "You climb the netting...", ""), 3, Course.BARBARIAN),

        BARBARIAN_LEDGE(new SkillRequirement(23547, 35, 90), new MultipleAgilityObstacles(new SkillRequirement(23547, 35, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(2536, 3547, 1), null, new Position(-2, 0, 0), CROSS_LEDGE, null, 150, 4, 3, new Sound(2470), true, "You put your foot on the ledge and try to edge across...", ""),
                new FailAgilityObstacle(new Position(2534, 3547, 1), new Animation(757), new Animation(761), null, 92, 1, 3, new Position(2535, 3546, 0), "You slip and fall to the pit below.", new Sound(Sounds.AGILITY_FALL_SOUND, 30, 3)),
                new CrossAgilityObstacle(new Position(2534, 3547, 1), null, new Position(-2, 0, 0), CROSS_LEDGE, null, 150, 4, 3, new Sound(2470), true, "", "You skillfully edge across the gap."),
        }, new int[] {2, 1, 2}),
                6, Course.BARBARIAN),

        BARBARIAN_LOW_WALL1(
                new SkillRequirement(1948, 35, 50), new CrossAgilityObstacle(new Position(2535, 3553, 0), null,
                new Position(2, 0), CLIMB_WALL, null, 50, 2, 1, new Sound(2453), false, "You climb the low wall...", ""),
                3, new Position(2536, 3553, 0), Course.BARBARIAN),

        BARBARIAN_LOW_WALL2(
                new SkillRequirement(1948, 35, 50), new CrossAgilityObstacle(new Position(2538, 3553, 0), null,
                new Position(2, 0), CLIMB_WALL, null, 50, 2, 1, new Sound(2453), false, "You climb the low wall...", ""),
                3, new Position(2539, 3553, 0), Course.BARBARIAN),

        BARBARIAN_LOW_WALL3(new SkillRequirement(1948, 35, 50),
                new CrossAgilityObstacle(new Position(2541, 3553, 0), null, new Position(2, 0), CLIMB_WALL, null, 50,
                        2, 1, new Sound(2453), false, "You climb the low wall...", ""),
                3, new Position(2542, 3553, 0), Course.BARBARIAN),

        /*
         * Wilderness course XP per lap: 40,000
         */

        WILDERNESS_ROPE(new SkillRequirement(23132, 52, 240), new MultipleAgilityObstacles(new SkillRequirement(23132, 52, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(3004, 3953, 0), null, new Position(1, 0), new Animation(819), null, 20, 2, 1),
                new ForceMovementAgilityObstacle(new Position(3005, 3953, 0), null, new Position(0, 5), ROPE_SWING, null, 25, 2, 0, new Sound(3424), false, new Animation(497), new Position(3005, 3952, 2)),
        }, new int[] {1,5}),
                new Position(3004, 3953, 0), 7, Course.WILDERNESS),

        WILDNERSS_PIPE(new SkillRequirement(23137, 52, 260), new MultipleAgilityObstacles(new SkillRequirement(23137, 52, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(3004, 3937, 0), null, new Position(0, 1), new Animation(749), null, 4, 2, 2, new Sound(2489), false),
                new CrossAgilityObstacle(new Position(3004, 3938, 0), null, new Position(0, 10), new Animation(2590), null, 4, 2, 2),
                new CrossAgilityObstacle(new Position(3004, 3948, 0), null, new Position(0, 2), new Animation(749), null, 4, 2, 2, new Sound(2489), false),
        }, new int[] {1, 10, 2}),
                 14, new Position(3004, 3938, 0), Course.WILDERNESS),

        WILDERNESS_STEPPING_STONE(new SkillRequirement(23556, 52, 200),
                new MultipleAgilityObstacles(new SkillRequirement(23556, 52, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3002, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(3001, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new FailAgilityObstacle(new Position(2794, 9564, 3), null, new Animation(770), null, 80, 1, 4, new Position(2997, 10348, 0)),
                        new ForceMovementAgilityObstacle(new Position(3000, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2999, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new FailAgilityObstacle(new Position(2794, 9564, 3), null, new Animation(770), null, 80, 1, 4, new Position(2997, 10348, 0)),
                        new ForceMovementAgilityObstacle(new Position(2998, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2997, 3960, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                }, new int[]{3, 2, 1, 3, 2, 1, 3, 3}),
                21, Course.WILDERNESS, new Position(2999, 10348, 0), 75, 5, new Animation(770), 12),

        WILDERNESS_LOG_BALANCE(new SkillRequirement(23542, 52, 210),
                new CrossAgilityObstacle(new Position(3002, 3945, 0), null, new Position(-8, 0), LOG_WALK, null, 1, 2,
                        3, new Sound(2470), true),
                9, Course.WILDERNESS),

        WILDERNESS_ROCKS(new SkillRequirement(23640, 52, 230),
                new CrossAgilityObstacle(new Position(2995, 3937, 0), null, new Position(0, -4), new Animation(740), null, 100,
                        4, 2, new Sound(2454), true),
                6, Course.WILDERNESS),

        /*
         * Agility pyramid
         */
        PYRAMID_ROCKS_DOWN_1(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3334, 2829, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3335, 2829, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3335, 2829, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_1(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3338, 2829, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3335, 2829, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3337, 2829, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_2(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3334, 2828, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3335, 2828, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3335, 2828, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_2(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3338, 2828, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3335, 2828, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3337, 2828, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_3(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3334, 2827, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3335, 2827, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3335, 2827, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_3(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3338, 2827, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3335, 2827, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3337, 2827, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_4(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3334, 2826, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3335, 2826, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3335, 2826, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_4(new SkillRequirement(11948, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11948, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3338, 2826, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3335, 2826, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3337, 2826, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_5(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3348, 2829, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3349, 2829, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3349, 2829, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_5(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3352, 2829, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3349, 2829, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3351, 2829, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_6(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3348, 2828, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3349, 2828, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3349, 2828, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_6(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3352, 2828, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3349, 2828, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3351, 2828, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_DOWN_7(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3348, 2827, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3349, 2827, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                }, new int[]{1, 5}),
                7, new Position(3349, 2827, 0), Course.OBSTACLES),

        PYRAMID_ROCKS_UP_7(new SkillRequirement(11949, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(11949, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3352, 2827, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 3, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3349, 2827, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0)
                }, new int[]{5, 1}),
                7, new Position(3351, 2827, 0), Course.OBSTACLES),

        PYRAMID_STAIRS1(new SkillRequirement(10857, 30, 0),
                new ClimbAgilityObstacle(new Position(3355, 2833, 1), true, CLIMB_UP, null), 3,
                new Position(3354, 2831, 0), Course.PYRAMID),


        PYRAMID_LOW_WALL1(new SkillRequirement(10865, 30, 25), new CrossAgilityObstacle(new Position(3355, 2848, 1), null,
                new Position(0, 2), CLIMB_WALL, null, 100, 3, 0, new Sound(2453), false, "You climb the low wall...", "... and make it over."), new Position(3355, 2849, 1), 3, Course.PYRAMID),

        PYRAMID_LEDGE1(new SkillRequirement(10860, 30, 25), new CrossAgilityObstacle(new Position(3363, 2851, 1), null,
                new Position(5, 0), CROSS_LEDGE, null, 150, 10, 1, new Sound(2451), true, "You put your foot on the ledge and try to edge across", "You skillfully edge across the gap."), new Position(3363, 2851, 1), 6, Course.PYRAMID,
                new Position(3364, 2853, 0), 69, 2, new Animation(761), 4),

        PYRAMID_PLANK1(new SkillRequirement(10868, 30, 55), new CrossAgilityObstacle(new Position(3375, 2845, 1), null,
                new Position(0, -5), LOG_WALK, null, 150, 10, 2, new Sound(2470), true, "You walk carefully across the slippery plank...", ""), new Position(3375, 2845, 1), 6, Course.PYRAMID,
                new Position(3376, 2843, 0), 69, 2, new Animation(770), 10),

        PYRAMID_GAP1(new SkillRequirement(10882, 30, 55),
                new CrossAgilityObstacle(new Position(3372, 2832, 1), new Animation(3057), new Position(-5, -0), new Animation(3060), new Animation(3058), 50, 5, 3, new Sound(2450), false),
                new Position(3372, 2832, 1), 8, Course.PYRAMID,
                new Position(3370, 2830, 0), 69, 2, new Animation(770), 8),

        PYRAMID_GAP2(new SkillRequirement(10863, 30, 55),
                new CrossAgilityObstacle(new Position(3372, 2832, 1), new Animation(3057), new Position(-5, -0), new Animation(3060), new Animation(3058), 50, 5, 3, new Sound(2450), false),
                new Position(3372, 2832, 1), 8, Course.PYRAMID,
                new Position(3370, 2830, 0), 69, 2, new Animation(770), 8),

        PYRAMID_GAP3(new SkillRequirement(10886, 30, 55),
                new CrossAgilityObstacle(new Position(3364, 2832, 1), null, new Position(-5, -0), new Animation(756), null, 50, 5, 3, new Sound(2451), true),
                new Position(3364, 2832, 1), 8, Course.PYRAMID,
                new Position(3362, 2830, 0), 69, 2, new Animation(761), 10),

        PYRAMID_STAIRS2(new SkillRequirement(10857, 30, 0),
                new ClimbAgilityObstacle(new Position(3357, 2835, 2), true, CLIMB_UP, null), 3,
                new Position(3356, 2833, 1), Course.PYRAMID),

        PYRAMID_FLOOR_2_GAP1(new SkillRequirement(10884, 30, 55),
                new CrossAgilityObstacle(new Position(3357, 2836, 2), new Animation(3057), new Position(0, 5), new Animation(3060), new Animation(3058), 50, 5, 3, new Sound(2450), false),
                new Position(3357, 2836, 2), 6, Course.PYRAMID,
                new Position(3355, 2838, 1), 69, 2, new Animation(770), 8),

        PYRAMID_FLOOR_2_GAP2(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3357, 2846, 2), null, new Position(0, 3), JUMP, null, 20, 2, 0, new Sound(2465), false, "You jump the gap..."),
                new Position(3357, 2846, 2), 6, Course.PYRAMID,
                new Position(3355, 2847, 1), 74, 1, new Animation(3068), 8),

        PYRAMID_FLOOR_2_GAP3(new SkillRequirement(10861, 30, 55),
                new CrossAgilityObstacle(new Position(3359, 2849, 2), new Animation(3057), new Position(5, 0), new Animation(3060), new Animation(3058), 50, 3, 3, new Sound(2450), false),
                new Position(3359, 2849, 2), 8, Course.PYRAMID,
                new Position(3361, 2851, 1), 69, 2, new Animation(770), 8),

        PYRAMID_FLOOR_2_GAP4(new SkillRequirement(10863, 30, 55),
                new CrossAgilityObstacle(new Position(3359, 2849, 2), new Animation(3057), new Position(5, 0), new Animation(3060), new Animation(3058), 50, 5, 3, new Sound(2450), false),
                new Position(3359, 2849, 2), 8, Course.PYRAMID,
                new Position(3361, 2851, 1), 69, 2, new Animation(770), 8),

        PYRAMID_FLOOR_2_LEDGE1(new SkillRequirement(10860, 30, 25),
                new CrossAgilityObstacle(new Position(3372, 2841, 2), null,
                        new Position(0, -5), CROSS_LEDGE, null, 150, 10, 1, new Sound(2451), true, "You put your foot on the ledge and try to edge across", "You skillfully edge across the gap."), new Position(3372, 2841, 2), 6, Course.PYRAMID,
                new Position(3374, 2839, 1), 69, 2, new Animation(760), 10),

        PYRAMID_FLOOR_2_LOW_WALL1(new SkillRequirement(10865, 30, 25), new CrossAgilityObstacle(new Position(3371, 2834, 2), null,
                new Position(-2, 0), CLIMB_WALL, null, 100, 5, 0, new Sound(2451), false, "You climb the low wall...", "... and make it over."), new Position(3371, 2834, 2), 3, Course.PYRAMID),

        PYRAMID_FLOOR_2_GAP5(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3366, 2834, 2), null, new Position(-3, 0), JUMP, null, 20, 2, 3, new Sound(2465), false, "You jump the gap..."),
                new Position(3366, 2834, 2), 6, Course.PYRAMID,
                new Position(3365, 2832, 1), 74, 1, new Animation(3068), 8),

        PYRAMID_FLOOR_2_STAIRS(new SkillRequirement(10857, 30, 0),
                new ClimbAgilityObstacle(new Position(3359, 2837, 3), true, CLIMB_UP, null), 3,
                new Position(3358, 2835, 2), Course.PYRAMID),

        PYRAMID_FLOOR_3_LOW_WALL1(new SkillRequirement(10865, 30, 25), new CrossAgilityObstacle(new Position(3359, 2838, 3), null,
                new Position(0, 2), CLIMB_WALL, null, 100, 5, 0, new Sound(2453), false, "You climb the low wall...", "... and make it over."), new Position(3359, 2838, 3), 3, Course.PYRAMID),

        PYRAMID_FLOOR_3_LEDGE1(new SkillRequirement(10888, 30, 25),
                new CrossAgilityObstacle(new Position(3359, 2842, 3), null,
                        new Position(0, 5), CROSS_LEDGE, null, 150, 10, 1, new Sound(2451), true, "You put your foot on the ledge and try to edge across", "You skillfully edge across the gap."), new Position(3359, 2842, 3), 6, Course.PYRAMID,
                new Position(3357, 2843, 2), 69, 2, new Animation(761), 10),

        PYRAMID_FLOOR_3_GAP1(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3370, 2843, 3), null, new Position(0, -3), JUMP, null, 20, 2, 2, new Sound(2465), false, "You jump the gap..."),
                new Position(3370, 2843, 3), 4, Course.PYRAMID,
                new Position(3372, 2842, 2), 74, 1, new Animation(3068), 8),

        PYRAMID_FLOOR_3_PLANK1(new SkillRequirement(10868, 30, 55), new CrossAgilityObstacle(new Position(3370, 2835, 3), null,
                new Position(-5, 0), LOG_WALK, null, 150, 10, 2, new Sound(2470), true, "You walk carefully across the slippery plank...", ""), new Position(3370, 2835, 3), 7, Course.PYRAMID,
                new Position(3368, 2834, 2), 69, 2, new Animation(770), 10),

        PYRAMID_FLOOR_3_STAIRS(new SkillRequirement(10857, 30, 0),
                new ClimbAgilityObstacle(new Position(3041, 4695, 2), true, CLIMB_UP, null), 3,
                new Position(3360, 2837, 3), Course.PYRAMID),

        PYRAMID_FLOOR_4_GAP1(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3041, 4696, 2), null, new Position(0, 3), JUMP, null, 20, 2, 0, new Sound(2465), false, "You jump the gap..."),
                new Position(3041, 4696, 2), 6, Course.PYRAMID,
                new Position(3359, 2837, 3), 74, 1, new Animation(3068), 8),

        PYRAMID_FLOOR_4_LOW_WALL1(new SkillRequirement(10865, 30, 25), new CrossAgilityObstacle(new Position(3041, 4701, 2), null,
                new Position(2, 0), CLIMB_WALL, null, 100, 5, 0, new Sound(2453), false, "You climb the low wall...", "... and make it over."), new Position(3041, 4701, 2), 3, Course.PYRAMID),

        PYRAMID_FLOOR_4_GAP2(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3048, 4697, 2), null, new Position(0, -3), JUMP, null, 20, 2, 2, new Sound(2465), false, "You jump the gap..."),
                new Position(3048, 4697, 2), 6, Course.PYRAMID,
                new Position(3370, 2840, 3), 74, 1, new Animation(3068), 8),

        PYRAMID_FLOOR_4_LOW_WALL2(new SkillRequirement(10865, 30, 25), new CrossAgilityObstacle(new Position(3048, 4694, 2), null,
                new Position(-2, 0), CLIMB_WALL, null, 100, 5, 0, new Sound(2453), false), new Position(3048, 4694, 2), 3, Course.PYRAMID),

        PYRAMID_FLOOR_5_GAP1(new SkillRequirement(10859, 30, 55),
                new ForceMovementAgilityObstacle(new Position(3046, 4699, 3), null, new Position(0, -3), JUMP, null, 20, 2, 2, new Sound(2465), false, "You jump the gap..."),
                new Position(3046, 4699, 3), 6, Course.PYRAMID,
                new Position(3048, 4698, 2), 74, 1, new Animation(3068), 8),

        /**
         * Seers Rooftop
         */

        SEERS_ROOFTOP_WALL(new SkillRequirement(11373, 60, 45),
                new ClimbAgilityObstacle(new Position(2729, 3488, 1), true, new Animation(737), null, new ClimbAgilityObstacle(new Position(2729, 3490, 3), true, new Animation(1117), new Animation(65535)), "You climb up the wall...", "...jump, and grab hold of the sign!"), 3,
                new Position(2729, 3489), Course.SEERS_ROOFTOP),

        SEERS_ROOFTOP_GAP1(new SkillRequirement(14928, 60, 20), new MultipleAgilityObstacles(new SkillRequirement(14928, 60, 0), new AgilityObstacle[]{
                new ClimbAgilityObstacle(new Position(2719, 3495, 2), false, JUMP, null, new Sound(2462, 15), new Position(2710, 3495, 2)),
                new ClimbAgilityObstacle(new Position(2713, 3493, 2), true, JUMP, null, new Sound(2462, 15), new Position(2710, 3495, 2))}, new int[]{4, 4}),
                8, Course.SEERS_ROOFTOP, new Position(2707, 3490, 2), 200),

        SEERS_ROOFTOP_TIGHTROPE(new SkillRequirement(14932, 60, 20), new CrossAgilityObstacle(new Position(2710, 3490, 2),
                null, new Position(0, -10), LOG_WALK, null, 200, 4, 2, new Sound(2495), true), 10, Course.SEERS_ROOFTOP),

        SEERS_ROOFTOP_GAP2(new SkillRequirement(14929, 60, 35),
                new MultipleAgilityObstacles(new SkillRequirement(14929, 60, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2710, 3477, 2), null, new Position(0, -3), JUMP_DOWN, null, 20, 2, 2, new Sound(2468), false),
                        new ClimbAgilityObstacle(new Position(2710, 3474, 2), true, PULL_UP, null, new Position(2710, 3470, 2), new ClimbAgilityObstacle(new Position(2710, 3472, 3), true, PULL_UP, null))
                }, new int[]{3, 8}),
                9, Course.SEERS_ROOFTOP, new Position(2713, 3471, 3), 200),

        SEERS_ROOFTOP_GAP3(new SkillRequirement(14930, 60, 15),
                new ClimbAgilityObstacle(new Position(2702, 3465, 2), false, JUMP, null, new Sound(2462, 15), new Position(2702, 3465, 3)),
                4, Course.SEERS_ROOFTOP, new Position(2700, 3463, 2), 200),

        SEERS_ROOFTOP_EDGE(new SkillRequirement(14931, 60, 0),
                new ClimbAgilityObstacle(new Position(2704, 3464, 0), false, JUMP, null, new Sound(2462, 15), new Position(2704, 3464, 2)),
                4, Course.SEERS_ROOFTOP),

        /**
         * Draynor Rooftop
         */
        DRAYNOR_CLIMB_TO_ROOF(new SkillRequirement(11404, 10, 5),
                new ClimbAgilityObstacle(new Position(3102, 3279, 3), true, CLIMB_UP, null), 2, Course.DRAYNOR_ROOFTOP,
                new Position(3100, 3280, 3), 200),

        DRAYNOR_TIGHTROPE_1(new SkillRequirement(11405, 10, 8),
                new CrossAgilityObstacle(new Position(3099, 3277, 3), null, new Position(-9, 0), LOG_WALK, null, 10, 4, 0, new Sound(2495), true)
                , 9, Course.DRAYNOR_ROOFTOP),

        DRAYNOR_TIGHTROPE_2(new SkillRequirement(11406, 10, 7),
                new MultipleAgilityObstacles(new SkillRequirement(11406, 10, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3091, 3276, 3), null, new Position(1, 0), new Animation(819), null, 10, 2, 1),
                        new CrossAgilityObstacle(new Position(3092, 3276, 3), null, new Position(0, -10), LOG_WALK, null, 10, 4, 0, new Sound(2495), true)},
                        new int[]{1, 3}), 11, Course.DRAYNOR_ROOFTOP),

        DRAYNOR_BALANCE_WALL(new SkillRequirement(11430, 10, 7),
                new MultipleAgilityObstacles(new SkillRequirement(11430, 10, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3089, 3265, 3), null, new Position(0, -4), CROSS_LEDGE, null, 10, 3, 2, new Sound(2451), true),
                        new CrossAgilityObstacle(new Position(3089, 3261, 3), null, new Position(-1, 0), CROSS_LEDGE, null, 10, 2, 3, new Sound(2451), true)
                }, new int[]{4, 4}), 5, Course.DRAYNOR_ROOFTOP),

        DRAYNOR_JUMP_UP_WALL(new SkillRequirement(11630, 10, 10),
                new ClimbAgilityObstacle(new Position(3088, 3256, 2), true, new Animation(2376), null, new ClimbAgilityObstacle(new Position(3088, 3255, 3), true, PULL_UP, null, new Sound(2468))), 2, Course.DRAYNOR_ROOFTOP),

        DRAYNOR_JUMP_BUILDING(new SkillRequirement(11631, 10, 4),
                new ClimbAgilityObstacle(new Position(3096, 3256, 3), true, JUMP, null, new Sound(2462, 15)),
                3, Course.DRAYNOR_ROOFTOP),

        DRAYNOR_JUMP_DOWN(new SkillRequirement(11632, 10, 0),
                new ClimbAgilityObstacle(new Position(3102, 3261, 1), true, JUMP, null, new Sound(2462, 15), new ClimbAgilityObstacle(new Position(3103, 3261, 0), true, JUMP, null, new Sound(2462, 15))),
                4, Course.DRAYNOR_ROOFTOP),

        /**
         * Agility Arena
         */
        BRIMHAVEN_LOW_WALL_DOWN_1(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2805, 9564, 3), null,
                        new Position(0, -3), new Animation(1252), null, 50, 2, 1, new Sound(2453), false),
                new Position(2805, 9564, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_LOW_WALL_UP_1(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2805, 9561, 3), null,
                        new Position(0, 3), new Animation(1252), null, 50, 2, 3, new Sound(2453), false),
                new Position(2805, 9561, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_LOW_WALL_DOWN_2(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2783, 9564, 3), null,
                        new Position(0, -3), new Animation(1252), null, 50, 2, 1, new Sound(2453), false),
                new Position(2783, 9564, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_LOW_WALL_UP_2(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2783, 9561, 3), null,
                        new Position(0, 3), new Animation(1252), null, 50, 2, 3, new Sound(2453), false),
                new Position(2783, 9561, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_LOW_WALL_LEFT_1(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2779, 9590, 3), null,
                        new Position(-3, 0), new Animation(1252), null, 50, 2, 1, new Sound(2453), false),
                new Position(2779, 9590, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_LOW_WALL_RIGHT_1(new SkillRequirement(3565, 1, 8),
                new CrossAgilityObstacle(new Position(2776, 9590, 3), null,
                        new Position(3, 0), new Animation(1252), null, 50, 2, 3, new Sound(2453), false),
                new Position(2776, 9590, 3), 3, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_DOWN_1(new SkillRequirement(3564, 1, 14),
                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9567, 3), new Animation(742), new Position(0, -3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2794, 9564, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2794, 9565, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2794, 9564, 3), null, new Position(0, -3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2794, 9561, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2794, 9560, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2794, 9561, 3), new Animation(745), new Position(0, -3), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9567, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_UP_1(new SkillRequirement(3564, 1, 14),
                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9558, 3), new Animation(742), new Position(0, 3), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),

                            new FailAgilityObstacle(new Position(2794, 9561, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2794, 9560, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2794, 9561, 3), null, new Position(0, 3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2794, 9564, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2794, 9565, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2794, 9564, 3), new Animation(745), new Position(0, 3), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9558, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_DOWN_2(new SkillRequirement(3564, 1, 14),

                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2772, 9578, 3), new Animation(742), new Position(0, -3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2772, 9575, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2772, 9577, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2772, 9575, 3), null, new Position(0, -3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2772, 9572, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2772, 9571, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2772, 9572, 3), new Animation(745), new Position(0, -3), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2772, 9578, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_UP_2(new SkillRequirement(3564, 1, 14),

                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2772, 9569, 3), new Animation(742), new Position(0, 3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2772, 9572, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2772, 9577, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2772, 9572, 3), null, new Position(0, 3), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2772, 9575, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2772, 9571, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2772, 9575, 3), new Animation(745), new Position(0, 3), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2772, 9569, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_LEFT_1(new SkillRequirement(3564, 1, 14),

                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2782, 9546, 3), new Animation(742), new Position(-3, 0), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2779, 9546, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2780, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2779, 9546, 3), null, new Position(-3, 0), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2776, 9546, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2772, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2776, 9546, 3), new Animation(745), new Position(-3, 0), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2782, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_MONKEY_BARS_RIGHT_1(new SkillRequirement(3564, 1, 14),

                new MultipleAgilityObstacles(new SkillRequirement(3564, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2773, 9546, 3), new Animation(742), new Position(3, 0), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2776, 9546, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2775, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2776, 9546, 3), null, new Position(3, 0), new Animation(744), new Animation(745), 4, 2, 0, new Sound(2466), true),

                        new FailAgilityObstacle(new Position(2779, 9546, 3), new Animation(745), new Animation(768), null, 100, 1, 4, new Position(2780, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 10)),
                        new CrossAgilityObstacle(new Position(2779, 9546, 3), new Animation(745), new Position(3, 0), new Animation(744), new Animation(743), 4, 2, 0, new Sound(2466), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2773, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_RIGHT_1(new SkillRequirement(3559, 40, 16),
                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2796, 9546, 3), null, new Position(2, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2798, 9546, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2797, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 30, 3)),
                        new CrossAgilityObstacle(new Position(2798, 9546, 3), null, new Position(3, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2801, 9546, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2802, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 30, 3)),
                        new CrossAgilityObstacle(new Position(2801, 9546, 3), null, new Position(2, 0), new Animation(754), null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2796, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_LEFT_1(new SkillRequirement(3561, 40, 16),

                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2803, 9546, 3), null, new Position(-2, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2801, 9546, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2802, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2801, 9546, 3), null, new Position(-3, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2798, 9546, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2797, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2798, 9546, 3), null, new Position(-2, 0), CROSS_LEDGE, null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2803, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_RIGHT_2(new SkillRequirement(3559, 40, 16),

                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2763, 9546, 3), null, new Position(2, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2765, 9546, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2764, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9546, 3), new Animation(755), new Position(3, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2768, 9546, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2769, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9546, 3), new Animation(755), new Position(2, 0), new Animation(754), null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2763, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_LEFT_2(new SkillRequirement(3561, 40, 16),

                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2770, 9546, 3), null, new Position(-2, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2768, 9546, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2769, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9546, 3), new Animation(757), new Position(-3, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2765, 9546, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2764, 9546, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9546, 3), new Animation(757), new Position(-2, 0), CROSS_LEDGE, null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2770, 9546, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_RIGHT_3(new SkillRequirement(3559, 40, 16),

                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2763, 9590, 3), null, new Position(2, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2765, 9590, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2764, 9590, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9590, 3), new Animation(755), new Position(3, 0), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2768, 9590, 3), new Animation(755), new Animation(760), null, 100, 1, 4, new Position(2769, 9590, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9590, 3), new Animation(755), new Position(2, 0), new Animation(754), null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2763, 9590, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_BALANCING_LEDGE_LEFT_3(new SkillRequirement(3561, 40, 16),
                new MultipleAgilityObstacles(new SkillRequirement(3559, 40, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2770, 9590, 3), null, new Position(-2, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2768, 9590, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2769, 9590, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9590, 3), new Animation(757), new Position(-3, 0), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),

                        new FailAgilityObstacle(new Position(2765, 9590, 3), new Animation(757), new Animation(761), null, 100, 1, 4, new Position(2764, 9590, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9590, 3), new Animation(757), new Position(-2, 0), CROSS_LEDGE, null, 4, 2, 2, new Sound(2451), true),

                }, new int[]{2, 1, 3, 1, 2}),
                new Position(2770, 9590, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_DOWN_1(new SkillRequirement(3551, 1, 10),
                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9555, 3), null, new Position(0, -2), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2794, 9553, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2795, 9554, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9553, 3), null, new Position(0, -3), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2794, 9550, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2793, 9549, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9550, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 0, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9556, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_UP_1(new SkillRequirement(3551, 1, 10),

                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9548, 3), null, new Position(0, 2), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2794, 9550, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2793, 9549, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9550, 3), null, new Position(0, 3), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2794, 9553, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2795, 9554, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9553, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9547, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_DOWN_2(new SkillRequirement(3551, 1, 10),
                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2783, 9588, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2783, 9586, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2784, 9587, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2783, 9586, 3), null, new Position(0, -3), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2783, 9583, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2782, 9582, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2783, 9583, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2783, 9589, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_UP_2(new SkillRequirement(3551, 1, 10),
                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2783, 9581, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2783, 9583, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2782, 9582, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2783, 9583, 3), null, new Position(0, 3), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2783, 9586, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2784, 9587, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2783, 9586, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2783, 9580, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_DOWN_3(new SkillRequirement(3551, 1, 10),
                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2772, 9566, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2772, 9564, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2771, 9565, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2772, 9564, 3), null, new Position(0, -3), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2772, 9561, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2771, 9560, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2772, 9561, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2772, 9567, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_TIGHT_ROPE_UP_3(new SkillRequirement(3551, 1, 10),
                new MultipleAgilityObstacles(new SkillRequirement(3551, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2772, 9559, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2772, 9561, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2771, 9560, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2772, 9561, 3), null, new Position(0, 3), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2495), true),

                        new FailAgilityObstacle(new Position(2772, 9564, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2771, 9565, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2772, 9564, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2495), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2772, 9558, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_UP_1(new SkillRequirement(3557, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3557, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2805, 9548, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2805, 9550, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2804, 9549, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2805, 9550, 3), null, new Position(0, 3), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2805, 9553, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2806, 9554, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2805, 9553, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2470), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2805, 9548, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_DOWN_1(new SkillRequirement(3553, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3553, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2805, 9555, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 0, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2805, 9553, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2806, 9554, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2805, 9553, 3), null, new Position(0, -3), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2805, 9550, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2804, 9549, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2805, 9550, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2470), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2805, 9555, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_UP_2(new SkillRequirement(3557, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3557, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9581, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2794, 9583, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2793, 9583, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9583, 3), null, new Position(0, 3), LOG_WALK, new Animation(763), 4, 2, 0, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2794, 9586, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2795, 9586, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9586, 3), null, new Position(0, 2), LOG_WALK, null, 4, 2, 0, new Sound(2470), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9581, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_DOWN_2(new SkillRequirement(3553, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3553, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2794, 9588, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2794, 9586, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2795, 9586, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9586, 3), null, new Position(0, -3), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2470), true),

                        new FailAgilityObstacle(new Position(2794, 9583, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2793, 9583, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2794, 9583, 3), null, new Position(0, -2), LOG_WALK, null, 4, 2, 2, new Sound(2470), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2794, 9588, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_LEFT_1(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2803, 9590, 3), null, new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                7, new Position(2802, 9590, 3), Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_RIGHT_1(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2796, 9590, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                7, new Position(2797, 9590, 3), Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_LEFT_2(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2770, 9557, 3), null,
                        new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                7, new Position(2769, 9557, 3), Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_RIGHT_2(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2763, 9557, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                7, new Position(2764, 9557, 3), Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_LEFT_3(new SkillRequirement(3557, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3557, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2770, 9579, 3), null, new Position(-2, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),

                        new FailAgilityObstacle(new Position(2768, 9579, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2769, 9578, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9579, 3), null, new Position(-3, 0), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2480), true),

                        new FailAgilityObstacle(new Position(2765, 9579, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2764, 9580, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9579, 3), null, new Position(-2, 0), LOG_WALK, null, 4, 2, 2, new Sound(2480), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2770, 9579, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_RIGHT_3(new SkillRequirement(3553, 1, 12),
                new MultipleAgilityObstacles(new SkillRequirement(3553, 1, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(2763, 9579, 3), null, new Position(2, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),

                        new FailAgilityObstacle(new Position(2765, 9579, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2769, 9578, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2765, 9579, 3), null, new Position(3, 0), LOG_WALK, new Animation(763), 4, 2, 2, new Sound(2480), true),

                        new FailAgilityObstacle(new Position(2768, 9579, 3), new Animation(763), new Animation(770), null, 100, 1, 4, new Position(2764, 9580, 0), "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),
                        new CrossAgilityObstacle(new Position(2768, 9579, 3), null, new Position(2, 0), LOG_WALK, null, 4, 2, 2, new Sound(2480), true),
                }, new int[]{3, 1, 3, 1, 3}),
                new Position(2763, 9579, 3), 12, Course.OBSTACLES),

        BRIMHAVEN_LOG_BALANCE_LEFT_4(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2803, 9589, 3), null,
                        new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2802, 9589, 3), Course.OBSTACLES, new Position(2801, 9588, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_RIGHT_4(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2796, 9589, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2797, 9589, 3), Course.OBSTACLES, new Position(2798, 9588, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_LEFT_5(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2803, 9591, 3), null,
                        new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2802, 9591, 3), Course.OBSTACLES, new Position(2801, 9592, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_RIGHT_5(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2796, 9591, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2797, 9591, 3), Course.OBSTACLES, new Position(2798, 9592, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_LEFT_6(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2770, 9556, 3), null,
                        new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2769, 9556, 3), Course.OBSTACLES, new Position(2769, 9555, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_RIGHT_6(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2763, 9556, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2764, 9556, 3), Course.OBSTACLES, new Position(2764, 9555, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_LEFT_7(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2770, 9558, 3), null,
                        new Position(-7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2769, 9558, 3), Course.OBSTACLES, new Position(2769, 9559, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_LOG_BALANCE_RIGHT_7(new SkillRequirement(3572, 1, 12),
                new CrossAgilityObstacle(new Position(2763, 9558, 3), null,
                        new Position(7, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                6, new Position(2764, 9558, 3), Course.OBSTACLES, new Position(2764, 9559, 0), 100, 2, new Animation(770), 4, "You stepped on a broken piece of plank!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_ROPE_SWING_RIGHT_1(new SkillRequirement(3566, 1, 20),
                new ForceMovementAgilityObstacle(new Position(2764, 9569, 3), null,
                        new Position(5, 0), ROPE_SWING, null, 25, 2, 1,  new Sound(2494), false, new Animation(497), new Position(2766,9569, 3)),
                new Position(2764, 9569, 3), 5, Course.OBSTACLES, new Position(2765, 9569, 0), 100, 1, new Animation(1105), 4, "You missed the rope!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_ROPE_SWING_LEFT_1(new SkillRequirement(3566, 1, 20),
                new ForceMovementAgilityObstacle(new Position(2769, 9567, 3), null,
                        new Position(-5, 0), ROPE_SWING, null, 25, 2, 3, new Sound(2494), false, new Animation(497), new Position(2767,9567, 1)),
                new Position(2769, 9567, 3), 5, Course.OBSTACLES, new Position(2768, 9567, 0), 100, 1, new Animation(1105), 4, "You missed the rope!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_ROPE_SWING_UP_1(new SkillRequirement(3566, 1, 20),
                new ForceMovementAgilityObstacle(new Position(2804, 9582, 3), null,
                        new Position(0, 5), ROPE_SWING, null, 25, 2, 0, new Sound(2494), false, new Animation(497), new Position(2804,9584, 2)),
                new Position(2804, 9582, 3), 5, Course.OBSTACLES, new Position(2806, 9583, 0), 100, 1, new Animation(1105), 4, "You missed the rope!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_ROPE_SWING_DOWN_1(new SkillRequirement(3566, 1, 20),
                new ForceMovementAgilityObstacle(new Position(2806, 9587, 3), null,
                        new Position(0, -5), ROPE_SWING, null, 25, 2, 2, new Sound(2494), false, new Animation(497), new Position(2806,9585, 0)),
                new Position(2806, 9587, 3), 5, Course.OBSTACLES, new Position(2806, 9587, 0), 100, 1, new Animation(1105), 4, "You missed the rope!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_DOWN_1(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2805, 9577, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9576, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9575, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9574, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9573, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9572, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9571, 3), null, new Position(0, -1), new Animation(741), null, 25, 1, 2, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2805, 9577, 3), 23, Course.OBSTACLES, new Position(2804, 9576, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_UP_1(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2805, 9570, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9571, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9572, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9573, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9574, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9575, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2805, 9576, 3), null, new Position(0, 1), new Animation(741), null, 25, 2, 0, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2805, 9570, 3), 23, Course.OBSTACLES, new Position(2804, 9571, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_DOWN_2(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2761, 9555, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9554, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9553, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9552, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9551, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9550, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9549, 3), null, new Position(0, -1), new Animation(741), null, 20, 2, 2, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2761, 9555, 3), 23, Course.OBSTACLES, new Position(2762, 9554, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_UP_2(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2761, 9548, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9549, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9550, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9551, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9552, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9553, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2761, 9554, 3), null, new Position(0, 1), new Animation(741), null, 20, 2, 0, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2761, 9548, 3), 23, Course.OBSTACLES, new Position(2760, 9549, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_LEFT_1(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2792, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2790, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2787, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9568, 3), null, new Position(-1, 0), new Animation(741), null, 20, 2, 3, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2792, 9568, 3), 23, Course.OBSTACLES, new Position(2791, 9567, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_JUMP_ON_PILLAR_RIGHT_1(new SkillRequirement(3578, 1, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2785, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2787, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2790, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9568, 3), null, new Position(1, 0), new Animation(741), null, 20, 2, 1, new Sound(2462, 15), false)
                }, new int[]{3, 3, 3, 3, 3, 3, 3}), new Position(2785, 9568, 3), 23, Course.OBSTACLES, new Position(2786, 9569, 0), 100, 5, new Animation(770), 4, "You have lost your balance!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

        BRIMHAVEN_HAND_HOLD_UP_1(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2759, 9559, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9560, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2759, 9561, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2759, 9561, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2759, 9561, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9562, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9563, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2759, 9564, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2759, 9564, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2759, 9564, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9565, 3), null, new Position(0, 1), new Animation(1121), null, 20, 2, 3, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2757, 9566, 3), false, true), new Position(2759, 9559, 3), 28, Course.OBSTACLES),

        BRIMHAVEN_HAND_HOLD_DOWN_1(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2759, 9566, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9565, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2759, 9564, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2759, 9564, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2759, 9564, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9563, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9562, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2759, 9561, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2759, 9561, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2759, 9561, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2759, 9560, 3), null, new Position(0, -1), new Animation(1118), null, 20, 2, 3, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2757, 9566, 3), false, true), new Position(2759, 9566, 3), 28, Course.OBSTACLES),

        BRIMHAVEN_HAND_HOLD_LEFT_1(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2792, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2790, 9592, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2790, 9592, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2790, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2787, 9592, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2787, 9592, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2787, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9592, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 0, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2792, 9594, 3), true, false), new Position(2792, 9592, 3), 26, Course.OBSTACLES),

        BRIMHAVEN_HAND_HOLD_RIGHT_1(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2785, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2787, 9592, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2787, 9592, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2787, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2790, 9592, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2790, 9592, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2790, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9592, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 0, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2792, 9594, 3), true, false), new Position(2785, 9592, 3), 26, Course.OBSTACLES),

        BRIMHAVEN_HAND_HOLD_LEFT_2(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2792, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2790, 9544, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2790, 9544, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2790, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2787, 9544, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2787, 9544, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2787, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9544, 3), null, new Position(-1, 0), new Animation(1118), null, 20, 2, 2, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2792, 9542, 3), true, false), new Position(2792, 9544, 3), 26, Course.OBSTACLES),

        BRIMHAVEN_HAND_HOLD_RIGHT_2(new SkillRequirement(3583, 20, 18),
                new MultipleAgilityObstacles(new SkillRequirement(3578, 1, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(2785, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2450), false),
                        new ForceMovementAgilityObstacle(new Position(2786, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2787, 9544, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2787, 9544, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2787, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2788, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2789, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2459, 35), false),

                        new FailAgilityObstacle(new Position(2790, 9544, 3), null, new Animation(1123), null, 100, 1, 4, new Position(2790, 9544, 0), "You missed a hand hold!", new Sound(Sounds.AGILITY_FALL_SOUND, 0, 5)),

                        new ForceMovementAgilityObstacle(new Position(2790, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2459, 35), false),
                        new ForceMovementAgilityObstacle(new Position(2791, 9544, 3), null, new Position(1, 0), new Animation(1121), null, 20, 2, 2, new Sound(2460), false)
                }, new int[]{3, 3, 1, 3, 3, 3, 1, 3, 3}, new Position(2792, 9542, 3), true, false), new Position(2785, 9544, 3), 26, Course.OBSTACLES),

        //Wilderness
        LAVA_MAZE_STEPPING_STONE_WEST(new SkillRequirement(14917, 82, 0),
                new ClimbAgilityObstacle(new Position(3092, 3880, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3093, 3879, 0), false, STEPPING_STONE, null, new Sound(2465))), new Position(3091, 3882), 4, Course.OBSTACLES),

        LAVA_MAZE_STEPPING_STONE_EAST(new SkillRequirement(14917, 82, 0),
                new ClimbAgilityObstacle(new Position(3092, 3880, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3091, 3882, 0), false, STEPPING_STONE, null, new Sound(2465))), new Position(3093, 3879, 0), 4, Course.OBSTACLES),

        /**
         * Misc Shortcuts
         */
        LUMBRIDGE_STALL_SHORTCUT_UP(new SkillRequirement(16518, 1, 0),
                new CrossAgilityObstacle(new Position(3240, 3333, 0), 1, null, new Position(0, 2), new Animation(1252), null, 50, 2, 1, new Sound(2453), false),
        new Position(3240, 3332, 0), 6, Course.OBSTACLES),

        LUMBRIDGE_STALL_SHORTCUT_DOWN(new SkillRequirement(16518, 1, 0),
                new CrossAgilityObstacle(new Position(3240, 3336, 0), 1,null, new Position(0, -2), new Animation(1252), null, 50, 2, 3, new Sound(2453), false),
        new Position(3240, 3336, 0), 6, Course.OBSTACLES),

        FALADOR_CRUMBLING_WALL_SHORTCUT_EAST(new SkillRequirement(24222, 1, 0),
                new CrossAgilityObstacle(new Position(2937, 3355, 0), 1,null, new Position(-2, 0), new Animation(1252), null, 50, 2, 0, new Sound(2453), false),
        new Position(2938, 3355, 0), 6, Course.OBSTACLES),

        FALADOR_CRUMBLING_WALL_SHORTCUT_WEST(new SkillRequirement(24222, 1, 0),
                new CrossAgilityObstacle(new Position(2934, 3355, 0), 1,null, new Position(2, 0), new Animation(1252), null, 50, 2, 2, new Sound(2453), false),
        new Position(2934, 3355, 0), 6, Course.OBSTACLES),

        ARDOUGNE_FARM_SHORTCUT_EAST(new SkillRequirement(993, 1, 0),
                new CrossAgilityObstacle(new Position(2649, 3375, 0), 1, null, new Position(-3, 0), new Animation(1252), null, 50, 2, 0, new Sound(2453), false),
        new Position(2650, 3375, 0), 6, Course.OBSTACLES),

        ARDOUGNE_FARM_SHORTCUT_WEST(new SkillRequirement(993, 1, 0),
                new CrossAgilityObstacle(new Position(2646, 3375, 0), 1,null, new Position(3, 0), new Animation(1252), null, 50, 2, 2, new Sound(2453), false),
        new Position(2645, 3375, 0), 6, Course.OBSTACLES),

        LUMBIRDGE_SWAMP_STEPPING_STONE_WE(new SkillRequirement(5948, 1, 3),
                new ClimbAgilityObstacle(new Position(3206, 9572, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3208, 9572, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3204, 9572, 0), 6, Course.OBSTACLES),

        LUMBIRDGE_SWAMP_STEPPING_STONE_EW(new SkillRequirement(5948, 1, 3),
                new ClimbAgilityObstacle(new Position(3206, 9572, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3204, 9572, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3208, 9572, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_SOUTH_TO_NORTH_1(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3220, 10086, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3220, 10088, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3220, 10084, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_NORTH_TO_SOUTH_1(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3220, 10086, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3220, 10084, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3220, 10088, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_SOUTH_TO_NORTH_2(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3180, 10209, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3180, 10207, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3180, 10211, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_NORTH_TO_SOUTH_2(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3180, 10209, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3180, 10211, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3180, 10207, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_WEST_TO_EAST_1(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3200, 10136, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3202, 10136, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3198, 10136, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_EAST_TO_WEST_1(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3200, 10136, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3198, 10136, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3202, 10136, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_WEST_TO_EAST_2(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3241, 10145, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3243, 10145, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3239, 10145, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_EAST_TO_WEST_2(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3241, 10145, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3239, 10145, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3243, 10145, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_WEST_TO_EAST_3(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3202, 10196, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3204, 10196, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3200, 10196, 0), 6, Course.OBSTACLES),

        REVENANT_CAVES_EAST_TO_WEST_3(new SkillRequirement(31561, 1, 3),
                new ClimbAgilityObstacle(new Position(3202, 10196, 0), true, STEPPING_STONE, null, new Sound(2465),
                        new ClimbAgilityObstacle(new Position(3200, 10196, 0), false, STEPPING_STONE, null, new Sound(2465))),
                new Position(3204, 10196, 0), 6, Course.OBSTACLES),

        EDGEVILLE_DUNGEON_MONKEY_BARS_SN_1(new SkillRequirement(23566, 15, 20),
                new CrossAgilityObstacle(new Position(3120, 9964, 0), new Animation(742), new Position(0, 8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(3119, 9964, 0), Course.OBSTACLES),

        EDGEVILLE_DUNGEON_MONKEY_BARS_SN_2(new SkillRequirement(23566, 15, 20),
                new CrossAgilityObstacle(new Position(3120, 9964, 0), new Animation(742), new Position(0, 8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(3120, 9964, 0), Course.OBSTACLES),

        EDGEVILLE_DUNGEON_MONKEY_BARS_NS_1(new SkillRequirement(23566, 15, 20),
                new CrossAgilityObstacle(new Position(3120, 9969, 0), new Animation(742), new Position(0, -8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(3119, 9969, 0),Course.OBSTACLES),

        EDGEVILLE_DUNGEON_MONKEY_BARS_NS_2(new SkillRequirement(23566, 15, 20),
                new CrossAgilityObstacle(new Position(3120, 9969, 0), new Animation(742), new Position(0, -8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(3120, 9969, 0),Course.OBSTACLES),

        AL_KHARID_MINE_WEST_TO_EAST(new SkillRequirement(16550, 38, 0),
                new MultipleAgilityObstacles(new SkillRequirement(16550, 38, 0), new AgilityObstacle[]{
                        new ForceMovementAgilityObstacle(new Position(3302, 3315, 0), null, new Position(3, 0), new Animation(740), null, 50, 2, 1, new Sound(2454), true),
                        new CrossAgilityObstacle(new Position(3305, 3315, 0), null, new Position(1, 0), new Animation(819), null, 100, 1, 0),
                }, new int[]{4, 1}),
                7, new Position(3303, 3315, 0), Course.OBSTACLES),

        AL_KHARID_MINE_EAST_TO_WEST(new SkillRequirement(16549, 38, 0),
                new MultipleAgilityObstacles(new SkillRequirement(16549, 38, 0), new AgilityObstacle[]{
                        new CrossAgilityObstacle(new Position(3306, 3315, 0), null, new Position(-1, 0), new Animation(819), null, 100, 1, 0),
                        new ForceMovementAgilityObstacle(new Position(3305, 3315, 0), null, new Position(-3, 0), new Animation(740), null, 50, 2, 1, new Sound(2454), true),
                }, new int[]{1, 4}),
                7, new Position(3305, 3315, 0), Course.OBSTACLES),

        COAL_TRUCKS_EW(new SkillRequirement(23274, 20, 8.5f),
                new CrossAgilityObstacle(new Position(2603, 3477, 0), null,
                        new Position(-5, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                5, new Position(2602, 3477, 0), Course.OBSTACLES),

        COAL_TRUCKS_WE(new SkillRequirement(23274, 20, 8.5f),
                new CrossAgilityObstacle(new Position(2598, 3477, 0), null,
                        new Position(5, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                5, new Position(2599, 3477, 0), Course.OBSTACLES),

        ARDOUGNE_LOG_BALANCE_EW(new SkillRequirement(16548, 33, 4),
                new CrossAgilityObstacle(new Position(2602, 3336, 0), null,
                        new Position(-4, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                4, new Position(2601, 3336, 0), Course.OBSTACLES),

        ARDOUGNE_LOG_BALANCE_WE(new SkillRequirement(16546, 33, 4),
                new CrossAgilityObstacle(new Position(2598, 3336, 0), null,
                        new Position(4, 0), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                4, new Position(2599, 3336, 0), Course.OBSTACLES),

        VARROCK_GE_SHORTCUT_EAST_TO_WEST(new SkillRequirement(16530, 21, 0), new MultipleAgilityObstacles(new SkillRequirement(16530, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(3141, 3513, 0), null, new Position(-1, 0), new Animation(2589), null, 20, 2, 3, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(3140, 3513, 0), null, new Position(-3, 3), new Animation(2590), null, 20, 2, 5),
                new ForceMovementAgilityObstacle(new Position(3138, 3516, 0), null, new Position(-1, 0), new Animation(2591), null, 20, 2, 3)
        }, new int[]{3, 4, 2}), 12, Course.OBSTACLES),

        VARROCK_GE_SHORTCUT_WEST_TO_EAST(new SkillRequirement(16529, 21, 0), new MultipleAgilityObstacles(new SkillRequirement(16530, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(3137, 3516, 0), null, new Position(1, 0), new Animation(2589), null, 20, 2, 1, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(3138, 3516, 0), null, new Position(3, -3), new Animation(2590), null, 20, 2, 6),
                new ForceMovementAgilityObstacle(new Position(3140, 3513, 0), null, new Position(1, 0), new Animation(2591), null, 20, 2, 1)
        }, new int[]{3, 4, 2}), 12, Course.OBSTACLES),

        FALADOR_SHORTCUT_NORTH_TO_SOUTH(new SkillRequirement(16528, 26, 0), new MultipleAgilityObstacles(new SkillRequirement(16528, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2948, 3313, 0), null, new Position(0, -1), new Animation(2589), null, 20, 2, 2, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(2948, 3312, 0), null, new Position(0, -2), new Animation(2590), null, 20, 2, 2),
                new ForceMovementAgilityObstacle(new Position(2948, 3310, 0), null, new Position(0, -1), new Animation(2591), null, 20, 2, 2)
        }, new int[]{3, 3, 3}), 10, Course.OBSTACLES),

        FALADOR_SHORTCUT_SOUTH_TO_NORTH(new SkillRequirement(16527, 26, 0), new MultipleAgilityObstacles(new SkillRequirement(16527, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2948, 3309, 0), null, new Position(0, 1), new Animation(2589), null, 20, 2, 0, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(2948, 3310, 0), null, new Position(0, 2), new Animation(2590), null, 20, 2, 0),
                new ForceMovementAgilityObstacle(new Position(2948, 3312, 0), null, new Position(0, 1), new Animation(2591), null, 20, 2, 0)
        }, new int[]{3, 3, 3}), 10, Course.OBSTACLES),

        YANILLE_SHORTCUT_NORTH_TO_SOUTH(new SkillRequirement(16520, 16, 0), new MultipleAgilityObstacles(new SkillRequirement(16528, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2575, 3112, 0), null, new Position(0, -1), new Animation(2589), null, 20, 2, 2, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(2575, 3111, 0), null, new Position(0, -3), new Animation(2590), null, 20, 2, 2),
                new ForceMovementAgilityObstacle(new Position(2575, 3108, 0), null, new Position(0, -1), new Animation(2591), null, 20, 2, 2)
        }, new int[]{3, 3, 3}), 10, Course.OBSTACLES),

        YANILLE_SHORTCUT_SOUTH_TO_NORTH(new SkillRequirement(16519, 16, 0), new MultipleAgilityObstacles(new SkillRequirement(16527, 21, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2575, 3107, 0), null, new Position(0, 1), new Animation(2589), null, 20, 2, 0, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(2575, 3108, 0), null, new Position(0, 3), new Animation(2590), null, 20, 2, 0),
                new ForceMovementAgilityObstacle(new Position(2575, 3111, 0), null, new Position(0, 1), new Animation(2591), null, 20, 2, 0)
        }, new int[]{3, 3, 3}), 10, Course.OBSTACLES),

        DRAYNOR_SHORTCUT_WEST_TO_EAST(new SkillRequirement(19036, 42, 0), new MultipleAgilityObstacles(new SkillRequirement(16528, 42, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(3070, 3260, 0), null, new Position(-1, 0), new Animation(2589), null, 20, 2, 3, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(3069, 3260, 0), null, new Position(-3, 0), new Animation(2590), null, 20, 2, 3),
                new ForceMovementAgilityObstacle(new Position(3066, 3260, 0), null, new Position(-1, 0), new Animation(2591), null, 20, 2, 3)
        }, new int[]{3, 4, 3}), 12, Course.OBSTACLES),

        DRAYNOR_SHORTCUT_EAST_TO_WEST(new SkillRequirement(19032, 42, 0), new MultipleAgilityObstacles(new SkillRequirement(19032, 42, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(3065, 3260, 0), null, new Position(1, 0), new Animation(2589), null, 20, 2, 1, new Sound(2452), false),
                new ForceMovementAgilityObstacle(new Position(3066, 3260, 0), null, new Position(3, 0), new Animation(2590), null, 20, 2, 1),
                new ForceMovementAgilityObstacle(new Position(3069, 3260, 0), null, new Position(1, 0), new Animation(2591), null, 20, 2, 1)
        }, new int[]{3, 4, 3}), 12, Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_STEPPING_STONES_WEST_TO_EAST(new SkillRequirement(19040, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(19040, 30, 0), new AgilityObstacle[] {
                        new ForceMovementAgilityObstacle(new Position(2682, 9548, 0), null, new Position(2, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2684, 9548, 0), null, new Position(2, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2686, 9548, 0), null, new Position(2, -1), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2688, 9547, 0), null, new Position(2, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                }, new int[] {3, 3, 3, 3}), 16, new Position(2684, 9548, 0), Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_STEPPING_STONES_EAST_TO_WEST(new SkillRequirement(19040, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(19040, 30, 0), new AgilityObstacle[] {
                        new ForceMovementAgilityObstacle(new Position(2690, 9547, 0), null, new Position(-2, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2688, 9547, 0), null, new Position(-2, 1), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2686, 9548, 0), null, new Position(-2, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2684, 9548, 0), null, new Position(-2, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                }, new int[] {3, 3, 3, 3}), 16, new Position(2688, 9547, 0), Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_STEPPING_STONES_NORTH_TO_SOUTH(new SkillRequirement(19040, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(19040, 30, 0), new AgilityObstacle[] {
                        new ForceMovementAgilityObstacle(new Position(2695, 9533, 0), null, new Position(0, -2), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2695, 9531, 0), null, new Position(0, -2), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2695, 9529, 0), null, new Position(1, -2), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2696, 9527, 0), null, new Position(1, -2), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                }, new int[] {3, 3, 3, 3}), 16, new Position(2695, 9531, 0), Course.OBSTACLES),

        BRIMHAVEN_DUNGEON_STEPPING_STONES_SOUTH_TO_NORTH(new SkillRequirement(19040, 30, 0),
                new MultipleAgilityObstacles(new SkillRequirement(19040, 30, 0), new AgilityObstacle[] {
                        new ForceMovementAgilityObstacle(new Position(2697, 9525, 0), null, new Position(-1, 2), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2696, 9527, 0), null, new Position(-1, 2), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2695, 9529, 0), null, new Position(0, 2), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                        new ForceMovementAgilityObstacle(new Position(2695, 9531, 0), null, new Position(0, 2), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                }, new int[] {3, 3, 3, 3}), 16, new Position(2696, 9527, 0), Course.OBSTACLES),

        DRAYNOR_MANOR_STEPPING_STONES_EAST_TO_WEST(new SkillRequirement(16533, 31, 3), new MultipleAgilityObstacles(new SkillRequirement(16533, 31, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(3154, 3363, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3153, 3363, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3152, 3363, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3151, 3363, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3150, 3363, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3}), 16, new Position(3153, 3363, 0), Course.OBSTACLES),

        DRAYNOR_MANOR_STEPPING_STONES_WEST_TO_EAST(new SkillRequirement(16533, 31, 3), new MultipleAgilityObstacles(new SkillRequirement(16533, 31, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(3149, 3363, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3150, 3363, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3151, 3363, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3152, 3363, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(3153, 3363, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3}), 16, new Position(3150, 3363, 0), Course.OBSTACLES),

        GODWARS_AGILITY_SN(new SkillRequirement(26768, 60, 6),
                new CrossAgilityObstacle(new Position(3066, 10147, 3), null, new Position(0, 2), new Animation(3277), null, 20, 2, 1, new Sound(2489), false),
                new Position(3066, 10146, 3), 3, Course.OBSTACLES),

        GODWARS_AGILITY_NS(new SkillRequirement(26768, 60, 6),
                new CrossAgilityObstacle(new Position(3066, 10149, 3), null, new Position(0, -2), new Animation(3276), null, 20, 2, 1, new Sound(2489), false),
                new Position(3066, 10150, 3), 3, Course.OBSTACLES),

        YANILLE_DUNGEON_LEDGE_SOUTH_TO_NORTH(new SkillRequirement(23548, 40, 22),
                new CrossAgilityObstacle(new Position(2580, 9512, 0), null, new Position(0, 8), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),
                new Position(2580, 9513, 0), 9, Course.OBSTACLES),

        YANILLE_DUNGEON_LEDGE_NORTH_TO_SOUTH(new SkillRequirement(23548, 40, 22),
                new CrossAgilityObstacle(new Position(2580, 9520, 0), null, new Position(0, -8), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),
                new Position(2580, 9519, 0), 9, Course.OBSTACLES),

        YANILLE_DUNGEON_MONKEY_BARS_SN_1(new SkillRequirement(23567, 15, 20),
                new CrossAgilityObstacle(new Position(2599, 9488, 0), new Animation(742), new Position(0, 8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(2597, 9489, 0), Course.OBSTACLES),

        YANILLE_DUNGEON_MONKEY_BARS_SN_2(new SkillRequirement(23567, 15, 20),
                new CrossAgilityObstacle(new Position(2599, 9488, 0), new Animation(742), new Position(0, 8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(2598, 9489, 0), Course.OBSTACLES),

        YANILLE_DUNGEON_MONKEY_BARS_NS_1(new SkillRequirement(23567, 15, 20),
                new CrossAgilityObstacle(new Position(2598, 9495, 0), new Animation(742), new Position(0, -8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(2597, 9494, 0), Course.OBSTACLES),

        YANILLE_DUNGEON_MONKEY_BARS_NS_2(new SkillRequirement(23567, 15, 20),
                new CrossAgilityObstacle(new Position(2598, 9495, 0), new Animation(742), new Position(0, -8), new Animation(744), new Animation(743), 200, 5, 0, new Sound(2466), true),
                6, new Position(2598, 9494, 0), Course.OBSTACLES),

        BKT_PIPE_WE(new SkillRequirement(OBSTACLE_PIPE_12, 60, 70),
                new CrossAgilityObstacle(new Position(2578, 9506, 0), new Animation(749), new Position(-6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                9, new Position(2576, 9506, 0), Course.OBSTACLES),

        BKT_PIPE_EW(new SkillRequirement(OBSTACLE_PIPE_12, 60, 70),
                new CrossAgilityObstacle(new Position(2572, 9506, 0), new Animation(749), new Position(6, 0), new Animation(2590), new Animation(748), 200, 6, 2, new Sound(2489), false),
                9, new Position(2573, 9506, 0), Course.OBSTACLES),

        COSMIC_NARROW_SOUTH_TO_NORTH(new SkillRequirement(17002, 46, 10),
                new CrossAgilityObstacle(new Position(2400, 4402, 0), null, new Position(0, 2), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),
                new Position(2400, 4401, 0), 3, Course.OBSTACLES),

        COSMIC_NARROW_NORTH_TO_SOUTH(new SkillRequirement(17002, 46, 10),
                new CrossAgilityObstacle(new Position(2400, 4404, 0), null, new Position(0, -2), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),
                new Position(2400, 4405, 0), 3, Course.OBSTACLES),

        COSMIC_NARROW_ADVANCED_SOUTH_TO_NORTH(new SkillRequirement(17002, 46, 10),
                new CrossAgilityObstacle(new Position(2409, 4400, 0), null, new Position(0, 2), new Animation(754), new Animation(755), 4, 2, 2, new Sound(2451), true),
                new Position(2409, 4399, 0), 3, Course.OBSTACLES),

        COSMIC_NARROW_ADVANCED_NORTH_TO_SOUTH(new SkillRequirement(17002, 46, 10),
                new CrossAgilityObstacle(new Position(2409, 4402, 0), null, new Position(0, -2), CROSS_LEDGE, new Animation(757), 4, 2, 2, new Sound(2451), true),
                new Position(2409, 4403, 0), 3, Course.OBSTACLES),

        NORTH_CAMELOT_LOG_BALANCE_NORTH_TO_SOUTH(new SkillRequirement(16548, 48, 0),
                new CrossAgilityObstacle(new Position(2722, 3596, 0), null,
                        new Position(0, -5), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                4, new Position(2722, 3595, 0), Course.OBSTACLES),

        NORTH_CAMELOT_LOG_BALANCE_SOUTH_TO_NORTH(new SkillRequirement(16542, 48, 0),
                new CrossAgilityObstacle(new Position(2722, 3592, 0), null,
                        new Position(0, 5), LOG_WALK, null, 4, 2, 0, new Sound(2480), true),
                4, new Position(2722, 3593, 0), Course.OBSTACLES),

        DRAYNOR_FARM_SHORTCUT_SOUTH_TO_NORTH(new SkillRequirement(7527, 1, 0),
                new CrossAgilityObstacle(new Position(3063, 3284, 0), 1, null, new Position(0, -3), new Animation(1252), null, 50, 2, 0, new Sound(2453), false),
                new Position(3063, 3285, 0), 5, Course.OBSTACLES),

        DRAYNOR_FARM_SHORTCUT_NORTH_TO_SOUTH(new SkillRequirement(7527, 1, 0),
                new CrossAgilityObstacle(new Position(3063, 3281, 0), 1, null, new Position(0, 3), new Animation(1252), null, 50, 2, 2, new Sound(2453), false),
                new Position(3063, 3280, 0), 5, Course.OBSTACLES),

        FRED_FARMER_SHORTCUT_SOUTH_TO_NORTH(new SkillRequirement(12982, 1, 0),
                new CrossAgilityObstacle(new Position(3197, 3278, 0), 1, null, new Position(0, -3), new Animation(1252), null, 50, 2, 0, new Sound(2453), false),
                new Position(3197, 3278, 0), 5, Course.OBSTACLES),

        FRED_FARMER_SHORTCUT_NORTH_TO_SOUTH(new SkillRequirement(12982, 1, 0),
                new CrossAgilityObstacle(new Position(3197, 3275, 0), 1, null, new Position(0, 3), new Animation(1252), null, 50, 2, 2, new Sound(2453), false),
                new Position(3197, 3275, 0), 5, Course.OBSTACLES),

        SHILO_VILLAGE_STONE_NORTH_TO_SOUTH(new SkillRequirement(16466, 77, 1.25f), new MultipleAgilityObstacles(new SkillRequirement(16466, 77, 0), new AgilityObstacle[]{
                new ForceMovementAgilityObstacle(new Position(2863, 2976, 0), null, new Position(0, -2), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2863, 2974, 0), null, new Position(0, -3), STEPPING_STONE, null, 20, 2, 2),
        }, new int[]{3, 3}), 9, Course.OBSTACLES),

        HEROES_GUILD_TUNNEL(new SkillRequirement(9739, 67, 0),
                new ClimbAgilityObstacle(new Position(2915, 9894, 0), true, new Animation(844), null),
                        4, Course.OBSTACLES),

        LIGHTHOUSE_BRIDGE_WEST_TO_EAST(new SkillRequirement(4616, 1, 0),
                new CrossAgilityObstacle(new Position(2598, 3608, 0),  null, new Position(-2, 0), new Animation(754), new Animation(755), 50, 2, 2, new Sound(2453), false),
                3, Course.OBSTACLES),

        LIGHTHOUSE_BRIDGE_EAST_TO_WEST(new SkillRequirement(4615, 1, 0),
                new CrossAgilityObstacle(new Position(2596, 3608, 0),  null, new Position(2, 0), CROSS_LEDGE, new Animation(757),50, 2, 2, new Sound(2453), false),
                3, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_1(new SkillRequirement(4558, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2514, 3619, 0), null, new Position(0, -2), new Animation(769), null, 20, 3, 2, new Sound(2461), false),
                new Position(2514, 3619, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_2(new SkillRequirement(4556, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2514, 3615, 0), null, new Position(0, -2), new Animation(769), null, 20, 3, 2, new Sound(2461), false),
                new Position(2514, 3615, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_3(new SkillRequirement(4554, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2516, 3611, 0), null, new Position(2, 0), new Animation(769), null, 20, 3, 1, new Sound(2461), false),
                new Position(2516, 3611, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_4(new SkillRequirement(4552, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2522, 3602, 0), null, new Position(0, -2), new Animation(769), null, 20, 3, 2, new Sound(2461), false),
                new Position(2522, 3602, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_5(new SkillRequirement(4550, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2522, 3597, 0), null, new Position(0, -2), new Animation(769), null, 20, 3, 2, new Sound(2461), false),
                new Position(2522, 3597, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_6(new SkillRequirement(4551, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2522, 3595, 0), null, new Position(0, 2), new Animation(769), null, 20, 3, 0, new Sound(2461), false),
                new Position(2522, 3595, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_7(new SkillRequirement(4553, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2522, 3600, 0), null, new Position(0, 2), new Animation(769), null, 20, 3, 0, new Sound(2461), false),
                new Position(2522, 3600, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_8(new SkillRequirement(4555, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2518, 3611, 0), null, new Position(-2, 0), new Animation(769), null, 20, 3, 3, new Sound(2461), false),
                new Position(2518, 3611, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_9(new SkillRequirement(4557, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2514, 3613, 0), null, new Position(0, 2), new Animation(769), null, 20, 3, 0, new Sound(2461), false),
                new Position(2514, 3613, 0), 4, Course.OBSTACLES),

        LIGHTHOUSE_ROCK_10(new SkillRequirement(4559, 1, 0),
                new ForceMovementAgilityObstacle(new Position(2514, 3617, 0), null, new Position(0, 2), new Animation(769), null, 20, 3, 0, new Sound(2461), false),
                new Position(2514, 3617, 0), 4, Course.OBSTACLES),

        CASTLE_WARS_NORTH_EAST_SOUTH_TO_NORTH(new SkillRequirement(4411, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(4411, 1, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(2420, 3122, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2420, 3123, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3123, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3124, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3125, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2418, 3125, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3, 3}), new Position(2420, 3122, 0), 19, Course.OBSTACLES),

        CASTLE_WARS_NORTH_EAST_NORTH_TO_SOUTH(new SkillRequirement(4411, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(4411, 1, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(2418, 3126, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2418, 3125, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3125, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3124, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2419, 3123, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2420, 3123, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3, 3}), new Position(2418, 3126, 0), 19, Course.OBSTACLES),

        CASTLE_WARS_SOUTH_WEST_SOUTH_TO_NORTH(new SkillRequirement(4411, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(4411, 1, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(2378, 3083, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2378, 3084, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2378, 3084, 0), null, new Position(-1, 0), STEPPING_STONE, null, 20, 2, 3, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3084, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3085, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3086, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3087, 0), null, new Position(0, 1), STEPPING_STONE, null, 20, 2, 0, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3, 3, 3}), new Position(2378, 3083, 0), 24, Course.OBSTACLES),

        CASTLE_WARS_SOUTH_WEST_NORTH_TO_SOUTH(new SkillRequirement(4411, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(4411, 1, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(2377, 3089, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3088, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3087, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3086, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2377, 3085, 0), null, new Position(1, 0), STEPPING_STONE, null, 20, 2, 1, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2378, 3085, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
                new ForceMovementAgilityObstacle(new Position(2378, 3084, 0), null, new Position(0, -1), STEPPING_STONE, null, 20, 2, 2, new Sound(2461), false),
        }, new int[] {3, 3, 3, 3, 3, 3, 3}), new Position(2377, 3089, 0), 24, Course.OBSTACLES),

        GNOME_ROCK_SHORTCUT_SOUTH_TO_NORTH(new SkillRequirement(16534, 37, 0), new MultipleAgilityObstacles(new SkillRequirement(16534, 37, 0), new AgilityObstacle[] {
                new CrossAgilityObstacle(new Position(2486, 3515, 0),  null, new Position(1, 0), new Animation(819), null, 50, 2, 2),
                new ForceMovementAgilityObstacle(new Position(2487, 3515, 0), null, new Position(1, 1), new Animation(1148), null, 15, 2, 2),
                new CrossAgilityObstacle(new Position(2488, 3516, 0),  null, new Position(1, 1), new Animation(819), null, 50, 2, 2),
                new ForceMovementAgilityObstacle(new Position(2489, 3517, 0), null, new Position(0, 4), new Animation(1148), null, 35, 4, 2),
        }, new int[] { 2, 2, 2, 5}), new Position(2486, 3515, 0), 18, Course.OBSTACLES),

        GNOME_ROCK_SHORTCUT_NORTH_TO_SOUTH(new SkillRequirement(16535, 37, 0), new MultipleAgilityObstacles(new SkillRequirement(16534, 37, 0), new AgilityObstacle[] {
                new ForceMovementAgilityObstacle(new Position(2489, 3521, 0), null, new Position(0, -4), new Animation(1148), null, 35, 4, 2),
                new CrossAgilityObstacle(new Position(2489, 3517, 0),  null, new Position(-1, -1), new Animation(819), null, 50, 2, 2),
                new ForceMovementAgilityObstacle(new Position(2488, 3516, 0), null, new Position(-2, -1), new Animation(1148), null, 15, 2, 2),
        }, new int[] { 5, 2, 2}), new Position(2489, 3521, 0), 16, Course.OBSTACLES),

        DWARVEN_MINE_CREVICE_WEST_TO_EAST(new SkillRequirement(16543, 42, 0), new MultipleAgilityObstacles(new SkillRequirement(16543, 42, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(3028, 9806, 0), null, new Position(1, 0), new Animation(2594), new Animation(2590), 20, 2, 1, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(3029, 9806, 0), null, new Position(5, 0), new Animation(2590), null, 40, 4, 1),
                new ForceMovementAgilityObstacle(new Position(3034, 9806, 0), new Animation(2590), new Position(1, 0), new Animation(2595), null, 20, 2, 1)
        }, new int[]{1, 5, 1}), new Position(3029, 9806, 0), 14, Course.OBSTACLES),

        DWARVEN_MINE_CREVICE_EAST_TO_WEST(new SkillRequirement(16543, 42, 0), new MultipleAgilityObstacles(new SkillRequirement(16543, 42, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(3035, 9806, 0), null, new Position(-1, 0), new Animation(2594), new Animation(2590), 20, 2, 3, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(3034, 9806, 0), null, new Position(-5, 0), new Animation(2590), null, 40, 4, 3),
                new ForceMovementAgilityObstacle(new Position(3029, 9806, 0), new Animation(2590), new Position(-1, 0), new Animation(2595), null, 20, 2, 3)
        }, new int[]{1, 5, 1}), new Position(3034, 9806, 0), 14, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_WE_1(new SkillRequirement(30384, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1319, 9966, 0), null, new Position(1, 0), new Animation(2594), null, 20, 2, 1, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1320, 9966, 0), null, new Position(2, 0), new Animation(2590), null, 20, 2, 1),
                new ForceMovementAgilityObstacle(new Position(1323, 9966, 0), null, new Position(1, 0), new Animation(2595), null, 20, 2, 1)
        }, new int[]{1, 3, 1}), new Position(1320, 9966, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_EW_1(new SkillRequirement(30385, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1323, 9966, 0), null, new Position(-1, 0), new Animation(2594), null, 20, 2, 3, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1322, 9966, 0), null, new Position(-2, 0), new Animation(2590), null, 20, 2, 3),
                new ForceMovementAgilityObstacle(new Position(1320, 9966, 0), null, new Position(-1, 0), new Animation(2595), null, 20, 2, 3)
        }, new int[]{1, 3, 1}), new Position(1322, 9966, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_WE_2(new SkillRequirement(30384, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1295, 9959, 0), null, new Position(1, 0), new Animation(2594), null, 20, 2, 1, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1296, 9959, 0), null, new Position(2, 0), new Animation(2590), null, 20, 2, 1),
                new ForceMovementAgilityObstacle(new Position(1298, 9959, 0), null, new Position(1, 0), new Animation(2595), null, 20, 2, 1)
        }, new int[]{1, 3, 1}), new Position(1296, 9959, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_EW_2(new SkillRequirement(30385, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1299, 9959, 0), null, new Position(-1, 0), new Animation(2594), null, 20, 2, 3, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1298, 9959, 0), null, new Position(-2, 0), new Animation(2590), null, 20, 2, 3),
                new ForceMovementAgilityObstacle(new Position(1296, 9959, 0), null, new Position(-1, 0), new Animation(2595), null, 20, 2, 3)
        }, new int[]{1, 3, 1}), new Position(1298, 9959, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_NS_1(new SkillRequirement(30383, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1318, 9960, 0), null, new Position(0, -1), new Animation(2594), null, 20, 2, 2, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1318, 9959, 0), null, new Position(0, -2), new Animation(2590), null, 20, 2, 2),
                new ForceMovementAgilityObstacle(new Position(1318, 9957, 0), null, new Position(0, -1), new Animation(2595), null, 20, 2, 2)
        }, new int[]{1, 3, 1}), new Position(1318, 9959, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_SN_1(new SkillRequirement(30382, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1318, 9956, 0), null, new Position(0, 1), new Animation(2594), null, 20, 2, 0, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1318, 9957, 0), null, new Position(0, 2), new Animation(2590), null, 20, 2, 0),
                new ForceMovementAgilityObstacle(new Position(1318, 9959, 0), null, new Position(0, 1), new Animation(2595), null, 20, 2, 0)
        }, new int[]{1, 3, 1}), new Position(1318, 9957, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_NS_2(new SkillRequirement(30383, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1305, 9957, 0), null, new Position(0, -1), new Animation(2594), null, 20, 2, 2, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1305, 9956, 0), null, new Position(0, -2), new Animation(2590), null, 20, 2, 2),
                new ForceMovementAgilityObstacle(new Position(1305, 9954, 0), null, new Position(0, -1), new Animation(2595), null, 20, 2, 2)
        }, new int[]{1, 3, 1}), new Position(1305, 9956, 0), 9, Course.OBSTACLES),

        LIZARD_MAN_CAVE_CREVICE_SN_2(new SkillRequirement(30382, 1, 0), new MultipleAgilityObstacles(new SkillRequirement(30384, 1, 0), new AgilityObstacle[]{
                new CrossAgilityObstacle(new Position(1305, 9953, 0), null, new Position(0, 1), new Animation(2594), null, 20, 2, 0, new Sound(2489), false),
                new ForceMovementAgilityObstacle(new Position(1305, 9954, 0), null, new Position(0, 2), new Animation(2590), null, 20, 2, 0),
                new ForceMovementAgilityObstacle(new Position(1305, 9956, 0), null, new Position(0, 1), new Animation(2595), null, 20, 2, 0)
        }, new int[]{1, 3, 1}), new Position(1305, 9954, 0), 9, Course.OBSTACLES);


        /**
         * The skill settings
         */
        private final SkillRequirement settings;

        /**
         * The agility sequence
         */
        private final AgilityObstacle sequence;

        /**
         * The delay
         */
        private final int delay;

        /**
         * The startNextRound position
         */
        private Position startPosition;

        /**
         * The player start position
         */
        private Position playerStartPosition;

        /**
         * The agility course
         */
        private Course course;

        /**
         * The Fail Position
         */
        private Position failPosition;

        /**
         * Fail Chance
         */
        private int failChance;

        /**
         * the Fail Delay until teleported
         */
        private int failDelay;

        /**
         * the Fail animation
         */
        private Animation failAnimation;

        /**
         * the Fail damage
         */
        private int failDamage;

        /**
         * Position of Mark of Grace
         */
        private Position markOfGracePosition;

        /**
         * Chance of Mark of Grace
         */
        private int chanceOfMarkOfGrace;

        /**
         * fail message
         */
        private String failMessage = "";

        /**
         * fail sound
         */
        private Sound failSound = null;

        /**
         * Represents an obstacle
         *
         * @param settings the settings
         * @param sequence the sequece
         * @param delay    the delay
         * @param course   the course
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Course course) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = null;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequece
         * @param delay               the delay
         * @param course              the course
         * @param markOfGracePosition mark of grace position
         * @param chanceOfMarkOfGrace chance of mark of grace
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Course course, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = null;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Represents an obstacle
         *
         * @param settings      the settings
         * @param sequence      the sequece
         * @param delay         the delay
         * @param course        the course
         * @param failPosition  position to go when failed
         * @param failChance    chance of failing
         * @param failDelay     delay before teleported to fail location
         * @param failAnimation animation to play when failed
         * @param failDamage    damage applied when failed
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = null;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequece
         * @param delay               the delay
         * @param course              the course
         * @param failPosition        position to go when failed
         * @param failChance          chance of failing
         * @param failDelay           delay before teleported to fail location
         * @param failAnimation       animation to play when failed
         * @param failDamage          damage applied when failed
         * @param markOfGracePosition mark of grace position
         * @param chanceOfMarkOfGrace chance of mark of grace
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = null;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Represents an obstacle
         *
         * @param settings      the settings
         * @param sequence      the sequece
         * @param delay         the delay
         * @param startPosition the startPosition
         * @param course        the course
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Position startPosition,
                  Course course) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = startPosition;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.failDamage = 0;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequece
         * @param delay               the delay
         * @param startPosition       the startPosition
         * @param course              the course
         * @param markOfGracePosition mark of grace position
         * @param chanceOfMarkOfGrace chance of mark of grace
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Position startPosition,
                  Course course, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = startPosition;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.failDamage = 0;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Represents an obstacle
         *
         * @param settings      the settings
         * @param sequence      the sequece
         * @param delay         the delay
         * @param startPosition the startPosition
         * @param course        the course
         * @param failPosition  position to go when failed
         * @param failChance    chance of failing
         * @param failDelay     delay before teleported to fail location
         * @param failAnimation animation to play when failed
         * @param failDamage    damage applied when failed
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Position startPosition,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = startPosition;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an obstacle
         *
         * @param settings      the settings
         * @param sequence      the sequece
         * @param delay         the delay
         * @param startPosition the startPosition
         * @param course        the course
         * @param failPosition  position to go when failed
         * @param failChance    chance of failing
         * @param failDelay     delay before teleported to fail location
         * @param failAnimation animation to play when failed
         * @param failDamage    damage applied when failed
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Position startPosition,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage, String failMessage, Sound failSound) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = startPosition;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
            this.failMessage = failMessage;
            this.failSound = failSound;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequece
         * @param delay               the delay
         * @param startPosition       the startPosition
         * @param course              the course
         * @param failPosition        position to go when failed
         * @param failChance          chance of failing
         * @param failDelay           delay before teleported to fail location
         * @param failAnimation       animation to play when failed
         * @param failDamage          damage applied when failed
         * @param markOfGracePosition mark of grace position
         * @param chanceOfMarkOfGrace chance of mark of grace
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, int delay, Position startPosition,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.startPosition = startPosition;
            this.playerStartPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequence
         * @param playerStartPosition the start position before starting agility obstacle
         * @param delay               how long the obstacle takes to complete for XP
         * @param course              what course is the obstacle from
         */

        Obstacles(SkillRequirement settings, AgilityObstacle sequence, Position playerStartPosition, int delay,
                  Course course) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.playerStartPosition = playerStartPosition;
            this.startPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.failDamage = 0;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an obstacle
         *
         * @param settings            the settings
         * @param sequence            the sequence
         * @param playerStartPosition the start position before starting agility obstacle
         * @param delay               how long the obstacle takes to complete for XP
         * @param course              what course is the obstacle from
         * @param markOfGracePosition mark of grace position
         * @param chanceOfMarkOfGrace chance to get a mark of grace
         */

        Obstacles(SkillRequirement settings, AgilityObstacle sequence, Position playerStartPosition, int delay,
                  Course course, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.playerStartPosition = playerStartPosition;
            this.startPosition = null;
            this.course = course;
            this.failPosition = null;
            this.failChance = 0;
            this.failDelay = 0;
            this.failAnimation = null;
            this.failDamage = 0;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Represents an agility obstacle
         *
         * @param settings            Settings of obstacle
         * @param sequence            Agility obstacle type
         * @param playerStartPosition Where player should be to start obstacle
         * @param delay               delay until player recieves experience
         * @param course              the course the obstacle is from
         * @param failPosition        position to go if player fails the obstacle
         * @param failChance          the chance of failing the obstacle
         * @param failDelay           delay until the fail is activated
         * @param failAnimation       animation to do as player fails the obstacle
         * @param failDamage          damage applied when player fails the obstacle
         * @param failMessage         message on fail
         * @param failSound           sound played on fail
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, Position playerStartPosition, int delay,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage, String failMessage, Sound failSound) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.playerStartPosition = playerStartPosition;
            this.startPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = null;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
            this.failMessage = failMessage;
            this.failSound = failSound;
        }

        /**
         * Represents an agility obstacle
         *
         * @param settings            Settings of obstacle
         * @param sequence            Agility obstacle type
         * @param playerStartPosition Where player should be to start obstacle
         * @param delay               delay until player recieves experience
         * @param course              the course the obstacle is from
         * @param failPosition        position to go if player fails the obstacle
         * @param failChance          the chance of failing the obstacle
         * @param failDelay           delay until the fail is activated
         * @param failAnimation       animation to do as player fails the obstacle
         * @param failDamage          damage applied when player fails the obstacle
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, Position playerStartPosition, int delay,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.playerStartPosition = playerStartPosition;
            this.startPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = null;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = null;
            this.chanceOfMarkOfGrace = 0;
        }

        /**
         * Represents an agility obstacle
         *
         * @param settings            Settings of obstacle
         * @param sequence            Agility obstacle type
         * @param playerStartPosition Where player should be to start obstacle
         * @param delay               delay until player recieves experience
         * @param course              the course the obstacle is from
         * @param failPosition        position to go if player fails the obstacle
         * @param failChance          the chance of failing the obstacle
         * @param failDelay           delay until the fail is activated
         * @param failAnimation       animation to do as player fails the obstacle
         * @param failDamage          damage applied when player fails the obstacle
         * @param markOfGracePosition position of mark of grace
         * @param chanceOfMarkOfGrace chance of getting a mark of grace during this obstacle
         */
        Obstacles(SkillRequirement settings, AgilityObstacle sequence, Position playerStartPosition, int delay,
                  Course course, Position failPosition, int failChance, int failDelay, Animation failAnimation, int failDamage, Position markOfGracePosition, int chanceOfMarkOfGrace) {
            this.settings = settings;
            this.sequence = sequence;
            this.delay = delay;
            this.playerStartPosition = playerStartPosition;
            this.startPosition = null;
            this.course = course;
            this.failPosition = failPosition;
            this.failChance = failChance;
            this.failDelay = failDelay;
            this.failAnimation = null;
            this.failAnimation = failAnimation;
            this.failDamage = failDamage;
            this.markOfGracePosition = markOfGracePosition;
            this.chanceOfMarkOfGrace = chanceOfMarkOfGrace;
        }

        /**
         * Sets the settings
         *
         * @return the settings
         */
        public SkillRequirement getSettings() {
            return settings;
        }

        /**
         * Gets the agility sequence
         *
         * @return the sequence
         */
        public AgilityObstacle getSequence() {
            return sequence;
        }

        /**
         * Sets the delay
         *
         * @return the delay
         */
        public int getDelay() {
            return delay;
        }

        public int getFailDelay() {
            return failDelay;
        }

        public int getFailChance() {
            return failChance;
        }

        public Position getFailPosition() {
            return failPosition;
        }

        public Animation getFailAnimation() {
            return failAnimation;
        }

        public int getFailDamage() {
            return failDamage;
        }

        public Position getMarkOfGracePosition() {
            return markOfGracePosition;
        }

        public int getChanceOfMarkOfGrace() {
            return chanceOfMarkOfGrace;
        }

        /**
         * Gets the startPosition
         *
         * @return the startPosition
         */
        public Position getStartPosition() {
            return startPosition;
        }

        /**
         * Gets the playerStartPosition
         *
         * @return the playerStartPosition
         */
        public Position getPlayerStartPosition() {
            return playerStartPosition;
        }

        /**
         * Gets the agility course.
         *
         * @return the agility course.
         */
        public Course getCourse() {
            return course;
        }

        /**
         * The values
         */
        private static final ImmutableSet<Obstacles> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Obstacles.class));

        /**
         * Gets the obstacle
         */
        public static Obstacles forObject(Player player, GameObject object) {
            if (object.getId() == 23135) {
                for (Obstacles ob : VALUES) {
                    if (ob.getPlayerStartPosition() != null && ob.getSettings().getId() == object.getId()) {
                        if (ob.getPlayerStartPosition().sameAs(player.getPosition())) {
                            return ob;
                        }
                    }
                }
                return null;
            }
            for (Obstacles o : VALUES) {
                if (o.getStartPosition() != null) {
                    if (object.getPosition().sameAs(o.getStartPosition())) {
                        return o;
                    }
                } else if (o.getPlayerStartPosition() != null && o.getSettings().getId() == object.getId()) {
                    if (o.getPlayerStartPosition().isWithinDistance(player.getPosition(), 2)) {
                        return o;
                    }
                } else if (o.getSettings().getId() == object.getId()) {
                    return o;
                }
            }
            return null;
        }
    }

    /**
     * Gets courses completed
     *
     * @param player the player
     * @param course the course
     * @return the total completed
     */
    private static int getCompletedAmount(Player player, Course course) {
        int total = 0;
        for (Obstacles ob : Obstacles.values()) {
            if (ob.getCourse().equals(course)) {
                if (player.getAgility().getCompleted()[ob.ordinal()]) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Reseting the course
     *
     * @param player the player
     * @param course the course
     */
    private static void resetCourse(Player player, Course course) {
        for (Obstacles ob : Obstacles.values()) {
            if (ob.getCourse().equals(course)) {
                player.getAgility().getCompleted()[ob.ordinal()] = false;
            }
        }
    }

    /**
     * Handles obstacles
     *
     * @param player the player
     * @param object the object
     */
    public static boolean handleObstacle(Player player, GameObject object) {
        Obstacles obstacle = Obstacles.forObject(player, object);

        return handleObstacle(player, obstacle, object);
    }

    public static boolean handleObstacle(Player player, Obstacles obstacle, GameObject object) {

        /*
         * No obstacle found
         */
        if (obstacle == null) {
            return false;
        }
        /*
         * Delay
         */
        if (!player.getClickDelay().elapsed(1200) || player.getAgility().getObstacle() != null) {
            return true;
        }

        if (!canStart(obstacle.getSequence(), player.getPosition(), object.getId() == 23132 ? 2 : 1)) {
            return true;
        }

        if (obstacle.getSequence() instanceof CrossAgilityObstacle) {
            Position start = ((CrossAgilityObstacle) obstacle.getSequence()).getStartPosition();
            if (!player.getPosition().sameAs(start)) {
                if (player == null)
                    return false;

                if (start != null)
                    player.getMotion().enqueuePathToWithoutCollisionChecks(start.getX(), start.getY());

                if (player.getSkills().getLevel(Skill.AGILITY) >= obstacle.getSettings().getLevelRequired()) {
                    player.BLOCK_ALL_BUT_TALKING = true;
                }
                TaskManager.submit(new Task(1) {
                    boolean shouldRun = false;
                    @Override
                    protected void execute() {
                        if (shouldRun) {
                            handleObstacle(player, obstacle, object);
                            stop();
                        }
                        if (player.getPosition().sameAs(((CrossAgilityObstacle) obstacle.getSequence()).getStartPosition())) {
                            shouldRun = true;
                            player.setPositionToFace(object.getPosition());
                        }
                    }

                });
                return true;
            } else {
                if (!player.crossAgilityReady) {
                    player.crossAgilityReady = true;
                    TaskManager.submit(1, () -> {
                        handleObstacle(player, obstacle, object);
                    });
                    return true;
                }
            }

        }

        if (obstacle.getSequence() instanceof ForceMovementAgilityObstacle) {
            Position start = ((ForceMovementAgilityObstacle) obstacle.getSequence()).getStartPosition();
            if (!player.getPosition().sameAs(start)) {
                if (player == null)
                    return false;

                if (start != null)
                    player.getMotion().enqueuePathToWithoutCollisionChecks(start.getX(), start.getY());

                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(new Task(1) {
                    boolean shouldRun = false;
                    @Override
                    protected void execute() {
                        if (shouldRun) {
                            handleObstacle(player, obstacle, object);
                            stop();
                        }
                        if (player.getPosition().sameAs(((ForceMovementAgilityObstacle) obstacle.getSequence()).getStartPosition())) {
                            shouldRun = true;
                            player.setPositionToFace(object.getPosition());
                        }
                    }

                });
                return true;
            } else {
                if (!player.crossAgilityReady) {
                    player.crossAgilityReady = true;
                    TaskManager.submit(1, () -> {
                        handleObstacle(player, obstacle, object);
                    });
                    return true;
                }
            }

        }

        if (!hasObstacleRequirements(player, object)) {
            return false;
        }
        /*
         * The skill action
         */
        SkillActionTask task = new SkillActionTask(obstacle.getSettings(), 1, Skill.AGILITY, obstacle.getDelay(), true) {
            @Override
            public void sendBeforeSkillAction(Player player) {
                EntityExtKt.setBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false, false);
                EntityExtKt.setBoolean(player, Attribute.STALL_HITS, true, false);
                obstacle.getSequence().execute(player);
                player.BLOCK_ALL_BUT_TALKING = true;
                //create mark of grace on floor
                if (obstacle.getMarkOfGracePosition() != null && Misc.random(1, obstacle.getChanceOfMarkOfGrace()) == 1) {
                    ItemOnGroundManager.registerNonGlobal(player, new Item(11849, 1), obstacle.getMarkOfGracePosition());
                }

                if (obstacle.getSequence() instanceof ClimbAgilityObstacle) {

                    ClimbAgilityObstacle obs = (ClimbAgilityObstacle) obstacle.getSequence();

                    if (obs.getNext() != null) {
                        TaskManager.submit(new Task(1) {
                            @Override
                            protected void execute() {
                                obs.getNext().execute(player);
                                stop();
                            }

                        });
                    }
                }

                if (obstacle.getFailPosition() != null) {
                    TaskManager.submit(new Task(1) {
                        int step = 0;
                        boolean failed = player.getSkillManager().getCurrentLevel(Skill.AGILITY) <= Misc.random(obstacle.getFailChance());

                        @Override
                        protected void execute() {
                            if (failed) {
                                if (step == obstacle.getFailDelay() - 1) {
                                    EntityExtKt.setBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, true, false);
                                    player.getMotion().clearSteps();
                                    player.forceAnimation(obstacle.getFailAnimation());

                                    if (obstacle.failMessage != "")
                                        player.sendMessage(obstacle.failMessage);
                                    if (obstacle.failSound != null)
                                        player.getPacketSender().sendSound(obstacle.failSound);

                                }
                                if (step == obstacle.getFailDelay())
                                    player.getMotion().clearSteps();

                            }
                            if (step == obstacle.getFailDelay() + 1) {
                                if (failed) {
                                    player.moveTo(obstacle.getFailPosition());
                                    player.getCombat().queue(new Damage(obstacle.getFailDamage(), DamageMask.REGULAR_HIT));
                                    player.getPacketSender().sendSound(player.getAppearance().isMale() ? (518 + Misc.random(4)) : 509);
                                    player.performAnimation(RESET_ANIMATION);

                                    player.crossAgilityReady = false;
                                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
                                    PlayerExtKt.unblock(player, true, true);
                                    PlayerExtKt.resetInteractions(player, true, false);
                                    //player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                                    player.getAgility().setObstacle(null);
                                    player.getClickDelay().reset();
                                    player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                                    player.getAppearance().setBas(new BasicAnimationSet());
                                    player.updateAppearance();
                                    player.setShouldNoClip(false);
                                }
                                stop();
                            }
                            step++;
                        }

                    });
                }
            }

            @Override
            public void sendEndAction(Player player) {
                if (!EntityExtKt.getBoolean(player, Attribute.DID_FAIL_AGILITY_OBSTACLE, false)) {
                    player.getAgility().getCompleted()[obstacle.ordinal()] = true;
                    player.crossAgilityReady = false;
                    EntityExtKt.setBoolean(player, Attribute.STALL_HITS, false, false);
                    PlayerExtKt.unblock(player, true, true);
                    PlayerExtKt.resetInteractions(player, true, false);
                    //player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                    player.getAgility().setObstacle(null);
                    player.getClickDelay().reset();
                    player.performAnimation(Animation.DEFAULT_RESET_ANIMATION);
                    player.resetBas();
                    player.updateAppearance();
                    player.setShouldNoClip(false);

                    player.BLOCK_ALL_BUT_TALKING = false;

                    if (player.shouldSetRunningBack) {
                        player.setRunning(player.wasRunningAgility);
                        player.shouldSetRunningBack = false;
                    }
                    if (getCompletedAmount(player, obstacle.getCourse()) >= obstacle.getCourse().getCourses() && obstacle.getCourse() != Course.OBSTACLES) {
                        //int courses = (obstacle.getCourse().getCourses() * 2);
                        //courses *= 1 + (obstacle.getSettings().getLevelRequired() / 10);
                        //courses *= 1 + (player.getSkillManager().getCurrentLevel(Skill.AGILITY) / 10);


                        // Process achievements
                        AchievementManager.processFor(AchievementType.NO_OBSTALCES, player);
                        AchievementManager.processFor(AchievementType.OBSTACLE_FREE, player);

                        // Send Message
                        player.getPacketSender().sendMessage("You have received some Agility arena tickets from completing the " + Misc.capitalize(obstacle.getCourse().name().toLowerCase().replace("_", " ")) + " Agility Course lap.");
                        Logging.log("agilitycourses", "" + player.getUsername() + " completed " + getCompletedAmount(player, obstacle.getCourse()) + " agility obstacles from " + Misc.capitalize(obstacle.getCourse().name().toLowerCase().replace("_", " ")) + " agility course.");

                        // Staff message warning
                        PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " completed " + getCompletedAmount(player, obstacle.getCourse()) + " agility obstacles from " + Misc.capitalize(obstacle.getCourse().name().toLowerCase().replace("_", " ")) + " agility course.");

                        // Reward player
                        int ticketsAmount = 1 + Misc.getRandomInclusive(2);
                        player.getInventory().add(new Item(ItemID.AGILITY_ARENA_TICKET, ticketsAmount));
                        player.getSkillManager().addExperience(Skill.AGILITY, obstacle.getCourse().getExperience());
                        player.getPoints().increase(AttributeManager.Points.LAPS_COMPLETED, 1); // Increase points
                        PlayerTaskManager.progressTask(player, DailyTask.AGILITY_LAPS);
                        PlayerTaskManager.progressTask(player, WeeklyTask.AGILITY_LAPS);
                        // Process Agility skilling task
                        SkillTaskManager.perform(player, ItemID.AGILITY_ARENA_TICKET, ticketsAmount, SkillMasterType.AGILITY);

                        // Roll for pet
                        PetHandler.onSkill(player, Skill.AGILITY);

                        // Add points and send message
                        if (obstacle.getCourse() == Course.GNOME) {
                            player.getPoints().increase(AttributeManager.Points.GNOME_LAPS, 1);
                            player.sendMessage("Your Gnome Stronghold Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.GNOME_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.BARBARIAN) {
                            player.getPoints().increase(AttributeManager.Points.BARABARIAN_LAPS, 1);
                            player.sendMessage("Your Barbarian Outpost Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.BARABARIAN_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.WILDERNESS) {
                            player.getPoints().increase(AttributeManager.Points.WILDERNESS_LAPS, 1);
                            player.sendMessage("Your Wilderness Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.WILDERNESS_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.PYRAMID) {
                            player.getPoints().increase(AttributeManager.Points.PYRAMID_LAPS, 1);
                            player.sendMessage("Your Pyramid Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.PYRAMID_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.DRAYNOR_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.DRAYNOR_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Draynor Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.DRAYNOR_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.AL_KHARID_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.AL_KHARID_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Al Kharid Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.AL_KHARID_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.VARROCK_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.VARROCK_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Varrock Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.VARROCK_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.CANIFIS_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.CANIFIS_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Canifis Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.CANIFIS_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.FALADOR_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.FALADOR_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Falador Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.FALADOR_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.SEERS_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.SEERS_ROOFTAP_LAPS, 1);
                            player.sendMessage("Your Seers Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.SEERS_ROOFTAP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.POLLNIVNEACH_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.POLLNIVEACH_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Pollnivneach Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.POLLNIVEACH_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.RELLEKKA_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.RELLEKKA_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Rellekka Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.RELLEKKA_ROOFTOP_LAPS) +"</col>.");
                        } else if (obstacle.getCourse() == Course.ARDOUNGE_ROOFTOP) {
                            player.getPoints().increase(AttributeManager.Points.ARDOUGNE_ROOFTOP_LAPS, 1);
                            player.sendMessage("Your Ardougne Rootftop Agility Course lap count is: @red@" + player.getPoints().get(AttributeManager.Points.ARDOUGNE_ROOFTOP_LAPS) +"</col>.");
                        }

                        resetCourse(player, obstacle.getCourse());
                    }

                    // Random event for anti-botting
                    if (EntityExtKt.passedTime(player, Attribute.RANDOM_EVENT_PUZZLE, 10, TimeUnit.MINUTES, false, false) && !player.busy() && !player.getCombat().isInCombat()) {
                        PlayerExtKt.tryRandomEventTrigger(player, 1.5F);
                    }

                    // Skill random messages while skilling
                    if (Misc.getRandomInclusive(3) == Misc.getRandomInclusive(3) && obstacle.getCourse() != Course.ARDOUNGE_ROOFTOP && obstacle.course != Course.WILDERNESS
                    && player.getSkillManager().getMaxLevel(Skill.AGILITY) < SkillUtil.maximumAchievableLevel()) {
                        sendSkillRandomMessages(player);
                    }

                    TaskManager.submit(new Task(3, false) {
                        @Override
                        protected void execute() {
                            if (!player.BLOCK_ALL_BUT_TALKING) {
                                stop();
                                return;
                            }
                            player.BLOCK_ALL_BUT_TALKING = false;
                            stop();
                        }
                    });

                }
            }
        };
        SkillUtil.startActionTask(player, task);
        return true;
    }

    /*
    * Processes and checks for any requirements before doing the Agility obstacle
    * For example: Key requirement, quest achievement, or any of that sort.
     */
    private static boolean hasObstacleRequirements(Player player, GameObject object) {

        /*if (object.getId() == ObjectID.OBSTACLE_PIPE_12) {
            if (player.getPosition().getX() >= 2575) {
                BossInstances.Companion.instanceDialogue(player, new Position(2572, 9506), BossInstances.BLACK_KNIGHT_TITAN, true, object);
                return false;
            }
        }*/

        if (object.getId() == ObjectID.STAIRS_50 && object.getPosition().sameAs(new Position(3354, 2831, 0))) {
            //First Agility Stairs, Set grabbed to false..
            EntityExtKt.setBoolean(player, Attribute.GRABBED_PYRAMID_TOP, false, false);
            AgilityPyramidManager.playersInArea.add(player);
        }
        if (object.getId() == ObjectID.MONKEYBARS_6) { // Yanille Dungeon Monkeybars
            if (player.getPosition().getY() <= 9491) {
                if (player.getInventory().contains(ItemID.HEART_CRYSTAL)) {
                    player.getInventory().delete(new Item(ItemID.HEART_CRYSTAL, 1));
                } else {
                    player.getPacketSender().sendMessage("You must sacrifice a Heart crystal from San'tojalan to the void to use these bars.", 1000);
                    player.BLOCK_ALL_BUT_TALKING = false;
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean canStart(AgilityObstacle obstacle, Position from, int distance) {

        if (obstacle instanceof CrossAgilityObstacle) {
            final Position startPosition = ((CrossAgilityObstacle) obstacle).getStartPosition();
            return startPosition.isWithinDistance(from, distance);
        }

        return true;
    }

    /**
     * Sets the completed
     *
     * @return the completed
     */
    public boolean[] getCompleted() {
        return completed;
    }

    /**
     * Sets the completed
     *
     * @param completed the completed
     */
    public void setCompleted(boolean[] completed) { this.completed = completed; }

    /**
     * Sets the obstacle
     *
     * @return the obstacle
     */
    public Task getObstacle() {
        return obstacle;
    }

    /**
     * Sets the obstacle
     *
     * @param obstacle the obstacle
     */
    public void setObstacle(Task obstacle) {
        /*
         * Stop previous
         */
        if (this.obstacle != null) {
            this.obstacle.stop();
        }
        /*
         * Set
         */
        this.obstacle = obstacle;
        /*
         * Start next
         */
        if (obstacle != null) {
            TaskManager.submit(obstacle);
        }
    }
}