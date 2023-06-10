package com.grinder.game.content.skill.skillable.impl;

import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.DefaultSkillable;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handles the ingame Prayer skill.
 *
 * @author Professor Oak
 */
public class Prayer {

    /**
     * The animation for burying a bone.
     */
    private static final Animation BONE_BURY = new Animation(827);
    /**
     * The amount of milliseconds a player must wait between
     * each bone-bury.
     */
    private static final long BONE_BURY_DELAY = 1000;
    /**
     * The experience multiplier when using bones on a x altar.
     */
    private static double REGULAR_ALTAR_EXPERIENCE_MULTIPLIER = 2.5;
    private static double CHOAS_ALTAR_EXPERIENCE_MULTIPLIER = 3.5;
    private static double ECTOFUNTUS_ALTAR_EXPERIENCE_MULTIPLIER = 4.0;
    private static double GILDED_ALTAR_EXPERIENCE_MULTIPLIER = 3.5;

    /*
     * Messages that are sent to the player while training Prayer skill
     */
    private static final String[][] PRAYER_MESSAGES = {
        { "@whi@You can buy bones off players or Prayer points exchange for fast XP!" },
        { "@whi@You can take a Prayer skill task from your master for bonus rewards." },
        { "@whi@The regular altar gives 250% bonus experience, Chaos altar 350%, Gilded altar 350%, and Ectofuntus 400%." },
        { "@whi@The best training altars are Wilderness Chaos Altar, Ecto-funtus, or the Gilded Altar." },
        { "@whi@The Chaos Altar in the Wilderness has 50% chance to skip bone deletion." },
        { "@whi@You can use ensouled heads on altars for Prayer and Slayer skill experience." },
        { "@whi@Training Prayer in the Wilderness Resource Area provides 20% bonus experience gain!" },
        { "@whi@Training Prayer with the skillcape equipped will give you 20% bonus experience gain!" },
        { "@whi@Training Prayer with the Zealot robes equipped will give you up to 25% bonus experience gain!" },
        { "@whi@Players with Members rank can train in unique skilling zones with the Gilded Altar." },
    };

    public static String currentMessage;

    public static void sendSkillRandomMessages(Player player) {
        currentMessage = PRAYER_MESSAGES[Misc.getRandomInclusive(PRAYER_MESSAGES.length - 1)][0];
        player.getPacketSender().sendMessage("<img=779> " + currentMessage);
    }

    /**
     * Checks if we should bury a bone.
     *
     * @param player
     * @param itemId
     * @return
     */
    public static boolean buryBone(Player player, int itemId) {
        Optional<BuriableBone> optionalBuriableBone = BuriableBone.forId(itemId);
        if (optionalBuriableBone.isPresent()) {
            final BuriableBone buriableBone = optionalBuriableBone.get();
            if (player.getClickDelay().elapsed(BONE_BURY_DELAY)) {

                // Stop other skilling
                SkillUtil.stopSkillable(player);

                // Reset interactions
                PlayerExtKt.resetInteractions(player, true, true);


                // Process achivement
                AchievementManager.processFor(AchievementType.GRAVE_MASTER, player);
                player.getPoints().increase(AttributeManager.Points.BONES_BURIED, 1); // Increase points
                if (player.getSkillManager().getMaxLevel(Skill.PRAYER) == 99) {
                    AchievementManager.processFor(AchievementType.SPRAY_AND_PRAY, player);
                }
                AchievementManager.processFor(AchievementType.GRAVE_DIGGER, player);

                // Remove any interface
                player.getPacketSender().sendInterfaceRemoval();

                // Perform animation
                player.performAnimation(BONE_BURY);

                // Send sound
                player.getPacketSender().sendSound(Sounds.BURY_BONES);

                // Send message
                player.getPacketSender().sendMessage("You dig a hole in the ground..");

                // Remove bone item
                player.getInventory().delete(itemId, 1);

                // Skill random messages while skilling
                if (Misc.getRandomInclusive(5) == Misc.getRandomInclusive(5) && player.getSkillManager().getMaxLevel(Skill.MINING) < SkillUtil.maximumAchievableLevel()) {
                    sendSkillRandomMessages(player);
                }

                TaskManager.submit(new Task(1, player, false) {
                    @Override
                    protected void execute() {
                        player.getPacketSender().sendMessage("..and bury the " + ItemDefinition.forId(itemId).getName() + ".");
                        player.getSkillManager().addExperience(Skill.PRAYER, buriableBone.getXp());
                        if (AreaManager.CATACOMBS_OF_KOUREND_AREA.contains(player)) {
                            player.getSkillManager().increaseLevelTemporarily(Skill.PRAYER,
                                buriableBone.getPointsRestored(),
                                player.getSkillManager().getMaxLevel(Skill.PRAYER));
                        }

                        switch(buriableBone) {
                            case BIG_BONES:
                                PlayerTaskManager.progressTask(player, DailyTask.OFFER_BIG_BONES);
                                break;
                            case DRAGON_BONES:
                                PlayerTaskManager.progressTask(player, DailyTask.OFFER_DRAGON_BONES);
                                break;
                            case OURG_BONES:
                                PlayerTaskManager.progressTask(player, DailyTask.OFFER_OURG_BONES);
                                break;
                        }

                        if (player.getEquipment().containsAny( ItemID.DRAGONBONE_NECKLACE, ItemID.BONECRUSHER_NECKALCE)
                                && !AreaManager.CATACOMBS_OF_KOUREND_AREA.contains(player)) {
                            boolean passedBonecrusherTime = EntityExtKt.passedTime(player, Attribute.BONECRUSHER_NECKLACE_WEAR_TIMER, 9, TimeUnit.SECONDS, false, false);
                            boolean passedDragonBoneTime = EntityExtKt.passedTime(player, Attribute.DRAGONBONE_NECKLACE_WEAR_TIMER, 9, TimeUnit.SECONDS, false, false);
                            if ((passedBonecrusherTime || passedDragonBoneTime) && buriableBone.getPointsRestored() > 0)
                                PlayerExtKt.increaseLevel(player, Skill.PRAYER, buriableBone.getPointsRestored(), true, PlayerExtKt.getMaxLevel(player, Skill.PRAYER));
                        }
                        stop();
                    }
                });
                player.getClickDelay().reset();
                SkillTaskManager.perform(player, buriableBone.boneId, 1, SkillMasterType.PRAYER);
            }
            return true;
        }
        return false;
    }

    /**
     * Represents a bone which can be buried or used
     * on an altar ingame to train the Prayer skill.
     *
     * @author Professor Oak
     */
    public enum BuriableBone {


        // Ensouled heads
        ENSOULED_GOBLIN_HEAD(ItemID.ENSOULED_GOBLIN_HEAD, 75, 0),
        ENSOULED_MONKEY_HEAD(ItemID.ENSOULED_MONKEY_HEAD, 75, 0),
        ENSOULED_IMP_HEAD(ItemID.ENSOULED_IMP_HEAD, 75, 0),
        ENSOULED_MINOTAUR_HEAD(ItemID.ENSOULED_MINOTAUR_HEAD, 90, 0),
        ENSOULED_SCORPION_HEAD(ItemID.ENSOULED_SCORPION_HEAD, 90, 0),
        ENSOULED_BEAR_HEAD(ItemID.ENSOULED_BEAR_HEAD, 90, 0),
        ENSOULED_UNICORN_HEAD(ItemID.ENSOULED_UNICORN_HEAD, 90, 0),
        ENSOULED_DOG_HEAD(ItemID.ENSOULED_DOG_HEAD, 150, 0),
        ENSOULED_CHAOS_DRUID_HEAD(ItemID.ENSOULED_CHAOS_DRUID_HEAD, 150, 0),
        ENSOULED_GIANT_HEAD(ItemID.ENSOULED_GIANT_HEAD, 180, 0),
        ENSOULED_OGRE_HEAD(ItemID.ENSOULED_OGRE_HEAD, 180, 0),
        ENSOULED_ELF_HEAD(ItemID.ENSOULED_ELF_HEAD, 180, 0),
        ENSOULED_TROLL_HEAD(ItemID.ENSOULED_TROLL_HEAD, 180, 0),
        ENSOULED_HORROR_HEAD(ItemID.ENSOULED_HORROR_HEAD, 210, 0),
        ENSOULED_KALPHITE_HEAD(ItemID.ENSOULED_KALPHITE_HEAD, 210, 0),
        ENSOULED_DAGANNOTH_HEAD(ItemID.ENSOULED_DAGANNOTH_HEAD, 275, 0),
        ENSOULED_BLOODVELD_HEAD(ItemID.ENSOULED_BLOODVELD_HEAD, 275, 0),
        ENSOULED_TZHAAR_HEAD(ItemID.ENSOULED_TZHAAR_HEAD, 340, 0),
        ENSOULED_DEMON_HEAD(ItemID.ENSOULED_DEMON_HEAD, 410, 0),
        ENSOULED_HELLHOUND_HEAD(ItemID.ENSOULED_HELLHOUND_HEAD, 410, 0),
        ENSOULED_AVIANSIE_HEAD(ItemID.ENSOULED_AVIANSIE_HEAD, 410, 0),
        ENSOULED_ABYSSAL_HEAD(ItemID.ENSOULED_ABYSSAL_HEAD, 450, 0),
        ENSOULED_DRAGON_HEAD(ItemID.ENSOULED_DRAGON_HEAD, 480, 0),
        // End of ensouled heads


        BONES(526, 5, 1),
        BURNED_BONES(528, 5, 1),
        WOLF_BONES(2859, 5, 1),
        MONKEY_BONES(3179, 5, 1),
        BAT_BONES(530, 6, 1),
        BIG_BONES(532, 15, 2),
        JOGRE_BONE(3125, 15, 2),
        ZOGRE_BONES(4812, 23, 2),
        SHAIKAHAN_BONES(3123, 25, 2),
        BABYDRAGON_BONES(534, 30, 3),
        WYRM_BONES(22780, 50, 3),
        DRAGON_BONES(536, 72, 4),
        WYVERN_BONES(6812, 72, 4),
        DRAKE_BONES(22783, 80, 4),
        FAYRG_BONES(4830, 84, 2),
        LAVA_DRAGON_BONES(11943, 85, 4),
        RAURG_BONES(4832, 96, 2),
        SEA_DRAGON_BONES(15800, 101, 4),
        HYDRA_BONES(22786, 110, 4),
        DAGANNOTH_BONES(6729, 125, 2),
        OURG_BONES(4834, 140, 2),
        SUPERIOR_DRAGON_BONES(22124, 150, 5),
        GILDED_DRAGON_BONE(15261, 265, 4),

        LONG_BONES(10976, 800, 2),
        CURVED_BONE(10977, 1000, 2)


        ;

        static final Map<Integer, BuriableBone> bones = new HashMap<>();

        static {
            for (BuriableBone b : BuriableBone.values()) {
                bones.put(b.boneId, b);
            }
        }

        private final int boneId;
        private final int xp;
        private final int pointsRestored;

        BuriableBone(int boneId, double buryXP, int pointsRestored) {
            this.boneId = boneId;
            this.xp = (int) buryXP;
            this.pointsRestored = pointsRestored;
        }

        public static Optional<BuriableBone> forId(int itemId) {
            BuriableBone b = bones.get(itemId);
            if (b != null) {
                return Optional.of(b);
            }
            return Optional.empty();
        }

        public int getBoneID() {
            return boneId;
        }

        public int getXp() {
            return xp;
        }

        public int getPointsRestored() {
            return pointsRestored;
        }
    }

    /**
     * Handles the altar offering.
     *
     * @author Professor Oak
     */
    public static final class AltarOffering extends DefaultSkillable {
        /**
         * The {@link Animation} used for offering bones on the altar.
         */
        private static final Animation ALTAR_OFFERING_ANIMATION = new Animation(3705);

        /**
         * The {@link Graphic} which will be performed by the {@link GameObject}
         * altar once bones are offered on it.
         */
        private static final Graphic ALTAR_OFFERING_GRAPHIC = new Graphic(624, GraphicHeight.MIDDLE);

        /**
         * The {@link BuriableBone} that's being offered.
         */
        private final BuriableBone bone;

        /**
         * The {@link GameObject} altar which we're using
         * to offer the bones on.
         */
        private final GameObject altar;

        /**
         * The amount of bones that are being offered.
         */
        private int amount;

        /**
         * Constructs this {@link DefaultSkillable}.
         *
         * @param bone
         */
        public AltarOffering(BuriableBone bone, GameObject altar, int amount) {
            this.bone = bone;
            this.altar = altar;
            this.amount = amount;
        }

        @Override
        public void startAnimationLoop(Player player) {
            Task task = new Task(5, player, true) {
                @Override
                protected void execute() {

                    // Perform animation
                    player.performAnimation(ALTAR_OFFERING_ANIMATION);

                    // Increase points
                    player.getPoints().increase(AttributeManager.Points.BONES_USED_ON_ALTAR, 1); // Increase points

                    PlayerTaskManager.progressTask(player, DailyTask.BONES_ALTAR);
                    PlayerTaskManager.progressTask(player, WeeklyTask.BONES_ALTAR);

                    // Send sound
                    player.getPacketSender().sendSound(Sounds.OFFER_BONES_SOUND);
                }
            };
            TaskManager.submit(task);
            getTasks().add(task);
        }

        @Override
        public void startGraphicsLoop(Player player) {}

        @Override
    	public void startSoundLoop(Player player) {
    		
    	}

        @Override
        public void finishedCycle(Player player) {
            if (amount-- <= 0) {
                cancel(player);
            }

            // Perform altar graphic
            //player.getPacketSender().sendGraphic(ALTAR_OFFERING_GRAPHIC, altar.getPosition());
            World.spawn(new TileGraphic(altar.getPosition(), ALTAR_OFFERING_GRAPHIC));


            boolean skipBoneDeletion = altar.getId() == ObjectID.CHAOS_ALTAR_2 && player.getWildernessLevel() >= 30 && Misc.random(2) == 1 && bone.getPointsRestored() != 0;
            if (!skipBoneDeletion)
            player.getInventory().delete(bone.getBoneID(), 1);
            if (altar.getId() == 18258 || altar.getId() == 20377) { // Gilded altars in d zones
                player.getSkillManager().addExperience(Skill.PRAYER, (int) (bone.getXp() * GILDED_ALTAR_EXPERIENCE_MULTIPLIER)); // 3rd best
            } else if (altar.getId() == 16648) { // Ectofuntus
                player.getSkillManager().addExperience(Skill.PRAYER, (int) (bone.getXp() * ECTOFUNTUS_ALTAR_EXPERIENCE_MULTIPLIER)); // Best altar location
            } else if (altar.getId() == 411) { // Chaas atlar in many places including wilderness
                player.getSkillManager().addExperience(Skill.PRAYER, (int) (bone.getXp() * CHOAS_ALTAR_EXPERIENCE_MULTIPLIER)); // Second best
            } else {
            player.getSkillManager().addExperience(Skill.PRAYER, (int) (bone.getXp() * REGULAR_ALTAR_EXPERIENCE_MULTIPLIER)); // Regular altars
            }

            // Ensouled heads giving some Slayer experience
            if (bone.getPointsRestored() == 0) {
                player.getSkillManager().addExperience(Skill.SLAYER, (bone.getXp() / 2.5));
            }

            // Process achievement
            AchievementManager.processFor(AchievementType.GRAVE_MASTER, player);

            // Send message
            player.getPacketSender().sendMessage("The gods are pleased with your offering.");

            // Skill random messages while skilling
            if (Misc.getRandomInclusive(8) == Misc.getRandomInclusive(8) && player.getSkillManager().getMaxLevel(Skill.PRAYER) < SkillUtil.maximumAchievableLevel()) {
                sendSkillRandomMessages(player);
            }
        }

        @Override
        public int cyclesRequired(Player player) {
            return 2;
        }

        @Override
        public boolean hasRequirements(Player player) {
            //Check if player has bones..
            if (!player.getInventory().contains(bone.getBoneID())) {
                return false;
            }
            //Check if we offered all bones..
            if (amount <= 0) {
                return false;
            }
            return super.hasRequirements(player);
        }

        @Override
        public boolean loopRequirements() {
            return true;
        }

        @Override
        public boolean allowFullInventory() {
            return true;
        }
    }
}
