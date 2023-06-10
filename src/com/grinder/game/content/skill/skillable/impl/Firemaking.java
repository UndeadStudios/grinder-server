package com.grinder.game.content.skill.skillable.impl;

import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.DefaultSkillable;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectSpawnTask;
import com.grinder.util.*;

import java.util.*;

import static com.grinder.game.content.skill.skillable.impl.Firemaking.LightableLog.OAK;

/**
 * Represents the Firemaking skill.
 * <p>
 * Has support for lighting logs that are on the ground
 * and for adding logs to fires (bonfire).
 *
 * @author Professor Oak
 */
public class Firemaking extends DefaultSkillable {

    /**
     * The {@link Animation} for lighting a fire.
     */
    private static final Animation LIGHT_FIRE = new Animation(733);
    /**
     * The {@link LightableLog} which we will be attempting
     * to light.
     */
    private final LightableLog log;
    /**
     * A log on the ground.
     * <p>
     * If present - we will focus on lighting this instead of
     * a log from the inventory.
     */
    private Optional<ItemOnGround> groundLog = Optional.empty();
    /**
     * Represents a bonfire, which we will be adding logs to
     * if present.
     */
    private Optional<GameObject> bonfire = Optional.empty();
    /**
     * Represents the amount of logs to add to a bonfire.
     */
    private int bonfireAmount;

    /**
     * Creates a Firemaking instance where we will be
     * lighting a {@link LightableLog} from our inventory.
     *
     * @param log
     */
    public Firemaking(LightableLog log) {
        this.log = log;
    }

    /**
     * Creates a Firemaking instance where
     * we'll be lighting a log which is
     * already on the ground.
     *
     * @param log
     * @param groundLog
     */
    public Firemaking(LightableLog log, ItemOnGround groundLog) {
        this.log = log;
        this.groundLog = Optional.of(groundLog);
    }

    /**
     * Creates a Firemaking instance where we'll
     * be adding logs to a bonfire.
     *
     * @param log
     * @param bonfire
     * @param bonfireAmount
     */
    public Firemaking(LightableLog log, GameObject bonfire, int bonfireAmount) {
        this.log = log;
        this.bonfire = Optional.of(bonfire);
        this.bonfireAmount = bonfireAmount;
    }

    /*
     * Messages that are sent to the player while training Firemaking skill
     */
    private static final String[][] FIREMAKING_MESSAGES = {
            { "@whi@You can use your logs on a fire for bonfire and easy quick experience!" },
            { "@whi@Lighting colored logs will create a colored bon fire for celebrations!" },
            { "@whi@Did you know Firemaking is the easiest skill to master in-game?" },
            { "@whi@Every equipped Pyromancer gear piece increases your experience gain in Firemaking skill!" },
            { "@whi@You can take a Firemaking skill task from your master for bonus rewards." },
            { "@whi@Firemaking in the Wilderness Resource Area provides 20% bonus experience gain!" },
            { "@whi@Firemaking with the skillcape equipped will give you 20% bonus experience gain!" },

    };

    public static String currentMessage;

    public static void sendSkillRandomMessages(Player player) {
        currentMessage = FIREMAKING_MESSAGES[Misc.getRandomInclusive(FIREMAKING_MESSAGES.length - 1)][0];
        player.getPacketSender().sendMessage("<img=779> " + currentMessage);
    }


    /**
     * Checks if we should light a log.
     */
    public static boolean init(Player player, int itemUsed, int itemUsedWith) {
        int logId = itemUsed == ItemID.TINDERBOX ? itemUsedWith : itemUsed;
        Optional<LightableLog> log = LightableLog.find(logId);
        if (log.isEmpty() && (itemUsedWith == ItemID.TINDERBOX || itemUsed == ItemID.TINDERBOX)) {
            player.sendMessage("You need logs to light fires.");
            return true;
        }


        if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
            return true;

        if ((itemUsed == ItemID.TINDERBOX || itemUsedWith == ItemID.TINDERBOX)) {
            if(AreaManager.BANK_AREAS.contains(player)){
                player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.BANKER_1613 || npc.getId() == NpcID.BANKER_1618)
                        .min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
                        .ifPresent(bankerNpc -> {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(bankerNpc.getId())
                                    .setExpression(DialogueExpression.ANGRY)
                                    .setText("Hey! you can't light a fire here..", "There is a lot of valuable stuff here you know.")
                                    .start(player);
                            player.setPositionToFace(bankerNpc.getPosition());
                            bankerNpc.setEntityInteraction(player);
                            bankerNpc.performAnimation(new Animation(859));
                            TaskManager.submit(4, () -> {
                                bankerNpc.resetEntityInteraction();
                                bankerNpc.handlePositionFacing();
                            });
                        });

                return true;
            }

            if (!World.getRegions().get(RegionCoordinates.fromPosition(player.getPosition())).getEntities(player.getPosition(), EntityType.NPC).isEmpty()
                    || ObjectManager.existsAt(player.getPosition())
                    || nearDoor(player.getPosition())) {
                player.getPacketSender().sendMessage("You can't light a fire here. Try moving around a bit.", 1000);
                return true;
            }

            // Random event
            if (PlayerExtKt.tryRandomEventTrigger(player, 2F))
                return true;

            log.ifPresent(lightableLog -> SkillUtil.startSkillable(player, new Firemaking(lightableLog)));
            return true;
        }
        return false;
    }

    /**
     * Checks if a position is near a door
     * @param startPos - The start position
     * @return True if position is near a door
     */
    private static boolean nearDoor(Position startPos) {

            final Region region = World.getRegions().fromPosition(startPos);
            final Set<GameObject> list = new HashSet<>();
            list.addAll(region.getStaticGameObjects(startPos.getZ()));
            list.addAll(region.getDynamicGameObjects(startPos.getZ()));

            for (GameObject gameObject: list) {
                if (gameObject.getDefinition() != null && gameObject.getDefinition().name != null) {
                    if (gameObject.getDefinition().name.toLowerCase().contains("door") || gameObject.getDefinition().name.toLowerCase().contains("gate")) {
                        if (gameObject.isWithinDistance(startPos, 2)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

    /**
     * Checks if we should light a log.
     */
    public static boolean initItemOnGround(Player player, int itemUsed, int itemUsedWith) {
        if (!player.getInventory().contains(ItemID.TINDERBOX)) {
            player.sendMessage("You need a tinderbox to light fires.");
            return true;
        }
        if ((itemUsed == ItemID.TINDERBOX || itemUsedWith == ItemID.TINDERBOX)) {
            if(AreaManager.BANK_AREAS.contains(player)){
                player.getLocalNpcs().stream().filter(npc -> npc.getId() == NpcID.BANKER_1613 || npc.getId() == NpcID.BANKER_1618)
                        .min(Comparator.comparingInt(npc -> npc.getPosition().getDistance(player.getPosition())))
                        .ifPresent(bankerNpc -> {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(bankerNpc.getId())
                                    .setExpression(DialogueExpression.ANGRY)
                                    .setText("Hey! you can't light a fire here..", "There is a lot of valuable stuff here you know.")
                                    .start(player);
                            player.setPositionToFace(bankerNpc.getPosition());
                            bankerNpc.setEntityInteraction(player);
                            bankerNpc.performAnimation(new Animation(859));
                            TaskManager.submit(4, () -> {
                                bankerNpc.resetEntityInteraction();
                                bankerNpc.handlePositionFacing();
                            });
                        });

                return true;
            }
            if (!World.getRegions().get(RegionCoordinates.fromPosition(player.getPosition())).getEntities(player.getPosition(), EntityType.NPC).isEmpty() ||
                    /*ClippedRegionManager.getObject(player.getPosition()).isPresent()
                    ||*/ ObjectManager.existsAt(player.getPosition())
                    || nearDoor(player.getPosition())) {
                player.getPacketSender().sendMessage("You can't light a fire here. Try moving around a bit.", 1000);
                return true;
            }

            // Random event
            if (PlayerExtKt.tryRandomEventTrigger(player, 2F))
                return true;
        }
        return false;
    }

    @Override
    public void start(Player player) {

        //Reset movement queue..
        player.getMotion().clearSteps();

        //Send message..
        player.getPacketSender().sendMessage("You attempt to light the logs..");

        //If we're lighting a log from our inventory..
        if (!groundLog.isPresent() && !bonfire.isPresent()) {
            //Delete logs from inventory..
            player.getInventory().delete(log.getLogId(), 1);

            //Place logs on ground..
            groundLog = Optional.of(ItemOnGroundManager.register(player, new Item(log.getLogId(), 1)));
        }

        //Face logs if present.
        groundLog.ifPresent(groundItem -> player.setPositionToFace(groundItem.getPosition()));

        //Start parent execution task..
        super.start(player);


    }

    @Override
    public void startAnimationLoop(Player player) {
        //If we're not adding to a bonfire
        //Simply do the regular animation.
        if (!bonfire.isPresent()) {
            player.performAnimation(LIGHT_FIRE);
            return;
        }
        Task animLoop = new Task(3, player, true) {
            @Override
            protected void execute() {
                player.performAnimation(LIGHT_FIRE); //Cooking anim looks fine for bonfires

            }
        };
        TaskManager.submit(animLoop);
        getTasks().add(animLoop);
    }

    @Override
    public void startGraphicsLoop(Player player) {

    }

    @Override
	public void startSoundLoop(Player player) {
		/*Task soundLoop = new Task(3, player, true) {
			@Override
			protected void execute() {
				player.getPacketSender().sendSound(Sounds.BURN_LOGS_SOUND, 200, 8, 0, 800);
			}
		};
		TaskManager.submit(soundLoop);
		getTasks().add(soundLoop);*/
	}

    @Override
    public void onCycle(Player player) {
    }

    @Override
    public void finishedCycle(Player player) {
        //Handle reset of skill..
        if (bonfire.isPresent()) {
            if (bonfireAmount-- <= 0) {
                cancel(player);
            }
        } else {
            cancel(player);
        }

        //If we're adding to a bonfire or the log on ground still exists... Reward player.
        if (bonfire.isPresent() || groundLog.isPresent() && ItemOnGroundManager.exists(groundLog.get())) {

            //If we aren't adding to a bonfire..
            if (!bonfire.isPresent()) {
                //The position to create the fire at..
                final Position pos = groundLog.get().getPosition().clone();

                //Delete logs from ground ..
                ItemOnGroundManager.deregister(groundLog.get());

                // Play sound
                player.getPacketSender().sendAreaEntitySound(groundLog.get(), Sounds.LOGS_LIT);

                //Create fire..
                GameObject fire = DynamicGameObject.createPublic(log.fireId, pos);
                fire.setSpawnedFor(player);

                TaskManager.submit(new TimedObjectSpawnTask(fire, log.getRespawnTimer(), Optional.of(() -> ItemOnGroundManager.register(player, new Item(ItemID.ASHES), pos))));

                //Step away from the fire..
                if (player.getPosition().equals(pos)) {
                    player.getMotion().enqueueStepAwayWithCollisionCheck();
                }
            } else {
                //Delete logs from inventory when using a bonfire..
                player.getInventory().delete(log.getLogId(), 1);
            }

            //Add experience..
            player.getSkillManager().addExperience(Skill.FIREMAKING, (int) (log.getExperience() * 1.5));

            // Task
            AchievementManager.processFor(AchievementType.LIGHT_UP, player);

            //Send message..
            player.getPacketSender().sendMessage("The fire catches and the logs begin to burn.");

            // Roll pet
            PetHandler.onSkill(player, Skill.FIREMAKING);

            // Process skillingtask
            SkillTaskManager.perform(player, log.getLogId(), 1, SkillMasterType.FIREMAKING);

            // Increase points
            player.getPoints().increase(AttributeManager.Points.LOGS_BURNED, 1); // Increase points

            // Skill random messages while skilling
            if (Misc.getRandomInclusive(10) == Misc.getRandomInclusive(10) && player.getSkillManager().getMaxLevel(Skill.FIREMAKING) < SkillUtil.maximumAchievableLevel()) {
                sendSkillRandomMessages(player);
            }

            handleTasks(player);
        }
    }

    private void handleTasks(Player player) {
        switch(log) {
            case OAK:
                PlayerTaskManager.progressTask(player, DailyTask.BURN_OAK_LOGS);
                break;
            case WILLOW:
                PlayerTaskManager.progressTask(player, DailyTask.BURN_WILLOW_LOGS);
                break;
            case YEW:
                PlayerTaskManager.progressTask(player, DailyTask.BURN_YEW_LOGS);
                break;


        }
    }

    @Override
    public int cyclesRequired(Player player) {
        if (bonfire.isPresent()) { //Cycle rate for adding to bonfire is constant.
            return 2;
        }
        int cycles = log.getCycles() + Misc.getRandomInclusive(4);
        cycles -= (int) player.getSkillManager().getMaxLevel(Skill.FIREMAKING) * 0.1;
        if (cycles < 3) {
            cycles = Misc.getRandomInclusive(3);
        }
        return cycles;
    }

    @Override
    public boolean hasRequirements(Player player) {
        //If we aren't adding logs to a fire - make sure player has a tinderbox..
        if (!bonfire.isPresent()) {
            if (!player.getInventory().contains(ItemID.TINDERBOX)) {
                player.sendMessage("You need a tinderbox to light fires.");
                return false;
            }
        }

        if (player.getSkillManager().getCurrentLevel(Skill.FIREMAKING) < log.getLevel()) {
			//player.sendMessage("You don't have the required Firemaking level to light up the logs.");
            player.sendMessage("You need a " + Skill.FIREMAKING.getName() +" level of " + log.getLevel() +" to burn " + log.name().toLowerCase() +" logs.");
        	return false;
        }

        //Check if we've burnt the amount of logs on the bonfire.
        if (bonfire.isPresent() && bonfireAmount <= 0) {
            return false;
        }

        //If we aren't lighting a log on the ground, make sure we have at least one in our inventory.
        if (!groundLog.isPresent()) {
            if (!player.getInventory().contains(log.getLogId())) {
                player.sendMessage("You've run out of logs.");
                return false;
            }
        }

        //If we're adding to a bonfire - make sure it still exists.
        //If we're not adding to a fire, make sure no object exists in our position.
        if (bonfire.isPresent()) {
            if (!ObjectManager.existsAt(ObjectID.FIRE_5, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_23, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_16, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_17, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_24, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_25, bonfire.get().getPosition())
                    && !ObjectManager.existsAt(ObjectID.FIRE_26, bonfire.get().getPosition())) {
                return false;
            }
        } else {
            //Check if there's already an object where the player wants to light a fire..
            if (/*ClippedRegionManager.getObject(player.getPosition()).isPresent()
                    ||*/ ObjectManager.existsAt(player.getPosition())) {
                player.sendMessage("You can't light a fire here. Try moving around a bit.", 1000);
                return false;
            }
        }

        return super.hasRequirements(player);
    }

    @Override
    public boolean loopRequirements() {
        //We may have run out of logs
        //when using bonfire.
        if (bonfire.isPresent()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean allowFullInventory() {
        return true;
    }




    /**
     * Represents a log which can be lit using
     * the Firemaking skill.
     *
     * @author Professor Oak
     */
    public enum LightableLog {
        NORMAL(1511, 1, 40, 5, 60, ObjectID.FIRE_23),
        WHITE_LOGS(ItemID.WHITE_LOGS, 1, 50, 5, 60, ObjectID.FIRE_16),
        PURPLE_LOGS(ItemID.PURPLE_LOGS, 1, 50, 5, 60, ObjectID.FIRE_17),
        RED_LOGS(ItemID.RED_LOGS, 1, 50, 5, 60, ObjectID.FIRE_24),
        GREEN_LOGS(ItemID.GREEN_LOGS, 1, 50, 5, 60, ObjectID.FIRE_25),
        BLUE_LOGS(ItemID.BLUE_LOGS, 1, 50, 5, 60, ObjectID.FIRE_26),
        ACHEY(2862, 1, 40, 7, 65, ObjectID.FIRE_23),
        OAK(1521, 15, 60, 9, 70, ObjectID.FIRE_23),
        WILLOW(1519, 30, 90, 11, 80, ObjectID.FIRE_23),
        TEAK(6333, 35, 105, 13, 80, ObjectID.FIRE_23),
        ARCTIC_PINE(10810, 42, 125, 10, 80, ObjectID.FIRE_23),
        MAPLE(1517, 45, 135, 14, 85, ObjectID.FIRE_23),
        MAHOGANY(6332, 50, 157, 18, 85, ObjectID.FIRE_23),
        EUCALYPTUS(12581, 58, 193, 10, 85, ObjectID.FIRE_23),
        YEW(1515, 60, 230, 18, 90, ObjectID.FIRE_23),
        MAGIC(1513, 75, 320, 20, 252, ObjectID.FIRE_23),
        REDWOOD(19669, 90, 390, 25, 280, ObjectID.FIRE_23);

        public static Map<Integer, LightableLog> lightableLogs = new HashMap<>();

        static {
            for (LightableLog log : LightableLog.values()) {
                lightableLogs.put(log.logId, log);
            }
        }

        private int logId;
        private int level;
        private int experience;
        private int cycles;
        private int respawnTimer;
        private int fireId;

        LightableLog(int logId, int level, int experience, int cycles, int respawnTimer, int fireId) {
            this.logId = logId;
            this.level = level;
            this.experience = experience;
            this.cycles = cycles;
            this.respawnTimer = respawnTimer;
            this.fireId = fireId;
        }


        public static Optional<LightableLog> find(int item) {
            LightableLog l = lightableLogs.get(item);
            if (l != null) {
                return Optional.of(l);
            }
            return Optional.empty();
        }

        public int getExperience() {
            return experience;
        }

        public int getLogId() {
            return logId;
        }

        public int getLevel() {
            return level;
        }

        public int getCycles() {
            return cycles;
        }

        public int getRespawnTimer() {
            return respawnTimer;
        }

        public int getFireId() {
            return fireId;
        }
    }


    /**
     * Handles using a log on a bonfire.
     * @param item The item being used.
     * @param player The player performing the action.
     * @param object The object the item is being used on.
     * @return If this is a fireMaking action.
     */
    public static Boolean handleBonfire(Item item, Player player, Optional<GameObject> object) {
        if (object.get().getId() == ObjectID.FIRE
                || object.get().getId() == ObjectID.FIRE_2
                || object.get().getId() == ObjectID.FIRE_3
                || object.get().getId() == ObjectID.FIRE_4
                || object.get().getId() == ObjectID.FIRE_5
                || object.get().getId() == ObjectID.FIRE_6
                || object.get().getId() == ObjectID.FIRE_7
                || object.get().getId() == ObjectID.FIRE_7
                || object.get().getId() == ObjectID.FIRE_8
                || object.get().getId() == ObjectID.FIRE_9
                || object.get().getId() == ObjectID.FIRE_10
                || object.get().getId() == ObjectID.FIRE_11
                || object.get().getId() == ObjectID.FIRE_12
                || object.get().getId() == ObjectID.FIRE_13
                || object.get().getId() == ObjectID.FIRE_14
                || object.get().getId() == ObjectID.FIRE_15
                || object.get().getId() == ObjectID.FIRE_16
                || object.get().getId() == ObjectID.FIRE_17
                || object.get().getId() == ObjectID.FIRE_18
                || object.get().getId() == ObjectID.FIRE_19
                || object.get().getId() == ObjectID.FIRE_20
                || object.get().getId() == ObjectID.FIRE_21
                || object.get().getId() == ObjectID.FIRE_22
                || object.get().getId() == ObjectID.FIRE_23
                || object.get().getId() == ObjectID.FIRE_24
                || object.get().getId() == ObjectID.FIRE_25
                || object.get().getId() == ObjectID.FIRE_26
                || object.get().getId() == ObjectID.FIRE_27
                || object.get().getId() == ObjectID.FIRE_28
                || object.get().getId() == ObjectID.FIRE_29
                || object.get().getId() == ObjectID.FIRE_30) {
            Optional<Firemaking.LightableLog> log = Firemaking.LightableLog.find(item.getId());
            if (log.isPresent()) {
                CreationMenu fmMenu = new SingleItemCreationMenu(player, log.get().getLogId(),
                        "How many would you like to burn?", (index, item1, amount) -> SkillUtil.startSkillable(
                                player, new Firemaking(log.get(), object.get(), amount))).open();
                player.setCreationMenu(Optional.of(fmMenu));
                return true;
            }
        }
        return false;
    }
}