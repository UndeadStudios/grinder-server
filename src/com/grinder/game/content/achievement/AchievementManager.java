package com.grinder.game.content.achievement;

import static com.grinder.game.content.achievement.AchievementConstants.ACHIEVEMENT_COMPLETION_NAME;
import static com.grinder.game.content.achievement.AchievementConstants.ACHIEVEMENT_POINTS_LINE;
import static com.grinder.game.content.achievement.AchievementConstants.ACHIEVEMENT_PROGRESSION_BAR;
import static com.grinder.game.content.achievement.AchievementConstants.ACHIEVEMENT_PROGRESSION_DESC;
import static com.grinder.game.content.achievement.AchievementConstants.ACHIEVEMENT_PROGRESSION_NAME;
import static com.grinder.game.content.achievement.AchievementConstants.EASY_LINE;
import static com.grinder.game.content.achievement.AchievementConstants.TOTAL_COMPLETED_LINE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;

/**
 * Handles achievements
 *
 * @author 2012
 */
public class AchievementManager {



    /*
    * Jinglebit pool ids for finishing achievements
     */
    public static final int[] JINGEBIT_MUSIC_IDS = { 7, 193, 189, 205, 246, 264, 136 };


    private static final int MAIN_INTERFACE = 25_000;
    private static final int LINE_START = MAIN_INTERFACE + 13;
    private final int[] progress = new int[AchievementType.values().length];

    static {
        ButtonActions.INSTANCE.onClick(81025, (clickAction -> {
            final Player player = clickAction.getPlayer();
            if (player.getLastCompletedAchievement() != null)
                AchievementManager.sendAchievement(player, player.getLastCompletedAchievement());
        }));
    }

    public static void open(Player player) {
        int line = LINE_START;

        for (AchievementType a : AchievementType.VALUES) {
            player.getPacketSender().sendString(line,a.getName());
            player.getPacketSender().sendStringColour(line++, complete(player, a) ? 0x00ff00 : inProgress(player, a) ? 0xffff00 : 0xff0000);
        }

        int scrollSize = ((AchievementType.VALUES.length-1) * 12);

        player.getPacketSender().sendScrollbarHeight(25013, scrollSize);

        player.getPacketSender().sendString(MAIN_INTERFACE + 5, "Total completed: @whi@" + getTotalCompleted(player) + "/" + AchievementType.VALUES.length);
        player.getPacketSender().sendString(MAIN_INTERFACE + 6, "Achievement Points: @whi@" + player.getPoints().get(Points.ACHIEVEMENT_POINTS_NEW));

        view(player, 0);

        player.getPacketSender().sendInterface(MAIN_INTERFACE);
    }

    public static void view(Player player, int index) {
        if (index > AchievementType.VALUES.length - 1) {
            return;
        }

        AchievementType achievement = AchievementType.VALUES[index];

        for (int i = 0; i < 8; i++) {
            player.getPacketSender().sendItemOnInterface(25011, -1, i,0);
        }

        final int amount = achievement.getItemReward().length;
        int startId =  (amount == 1 ? 2 : 1)+(amount / 2);

        for (int i = 0; i < amount; i++) {
            final Item reward = achievement.getItemReward()[i];
            player.getPacketSender().sendItemOnInterface(25011, reward.getId(), startId+i, reward.getAmount());
        }

        player.getPacketSender().sendString(MAIN_INTERFACE + 7, achievement.getName());
        player.getPacketSender().sendString(MAIN_INTERFACE + 10, Misc.splitText(achievement.getDescription(), 20));
        player.getPacketSender().sendString(MAIN_INTERFACE + 9, achievement.getReward());

        player.getPacketSender().sendMessage(
                "percentage:25012:" + getPercentage(player, achievement));
    }

    public static boolean handleButton(Player player, int id) {
        if (id >= LINE_START && id <= (LINE_START + AchievementType.VALUES.length)) {
            int index = id - LINE_START;

            view(player, index);
            return true;
        }
        return false;
    }

    public static void processFor(AchievementType achievement, Player player) {

        final AchievementManager achievementManager = player.getAchievements();
        final PacketSender packetSender = player.getPacketSender();
        final String formattedAchievementName = Misc.formatName(achievement.name().toLowerCase());
        if (achievementManager.getProgress()[achievement.ordinal()] >= achievement.getAmount()) {
            return;
        }
        if (player.getMinigame() != null) {
            return;
        }
        achievementManager.getProgress()[achievement.ordinal()]++;

        if (achievement.getAmount() == achievementManager.getProgress()[achievement.ordinal()]) {

            final Inventory inventory = player.getInventory();

            player.getPoints().increase(Points.ACHIEVEMENT_POINTS_NEW, achievement.getPointsReward());
            player.sendMessage("<img=789> <col=1aff1a>Achievement: You have completed the achievement: ["
                    + formattedAchievementName + "]!");
            //player.sendMessage("<img=789> <col=1aff1a>Achievements completed: " + getTotalCompleted(player) + "/" + AchievementType.VALUES.length);
            //player.sendMessage("<img=789> <col=1aff1a>Achievement Points:  "+ player.getPoints().get(Points.ACHIEVEMENT_POINTS_NEW));


            // Send jinglebit
            if (EntityExtKt.passedTime(player, Attribute.LAST_ACHIEVMENT_COMPLETION, 5000, TimeUnit.MILLISECONDS, false, true)) {
                player.getPacketSender().sendJinglebitMusic(Misc.randomElement(JINGEBIT_MUSIC_IDS), 0);
            }

            ParticipationPoints.addPoints(player, 1 + Misc.getRandomInclusive(2) * achievement.getPointsReward(),
                    "@dre@from completing achievements</col>.");
            AchievementManager.processFor(AchievementType.OVER_ACHIEVER, player);


            final Item[] rewards = achievement.getItemReward();
            final int requiredSlots = Arrays.stream(rewards).filter(Objects::nonNull).mapToInt(Item::getAmount).sum();

            if (!player.getGameMode().isUltimate()) {
                if (inventory.countFreeSlots() >= requiredSlots) {
                    //if(Stream.of(achievement.getItemReward()).allMatch(item -> inventory.getFreeSlots() >= achievement.getItemReward().length)) {
                    if (!player.getGameMode().isSpawn())
                    inventory.addItemSet(achievement.getItemReward());
                } else {
                    if (!player.getGameMode().isSpawn()) {
                        player.sendMessage("@red@The reward has been sent to your bank.");
                        player.getBank(player.getCurrentBankTab()).addItemSet(achievement.getItemReward());
                    }
                }
            } else {
                player.sendMessage("Ultimate Iron Man accounts are not eligible for achievement rewards.");
            }
            sendTab(player);
            packetSender.sendString(ACHIEVEMENT_COMPLETION_NAME, formattedAchievementName);
            player.setLastCompletedAchievement(achievement);

        } else if (achievementManager.progressIn(achievement) < achievement.getAmount()) {
            packetSender.sendString(ACHIEVEMENT_PROGRESSION_NAME, formattedAchievementName);
            packetSender.sendString(ACHIEVEMENT_PROGRESSION_DESC, achievement.getDescription());
            packetSender.sendMessage(
                    "percentage:" + ACHIEVEMENT_PROGRESSION_BAR + ":" + getPercentage(player, achievement));
        }
    }

    public static void processFor(AchievementType achievement, int amount, Player player) {

        final AchievementManager achievementManager = player.getAchievements();
        final PacketSender packetSender = player.getPacketSender();
        final String formattedAchievementName = Misc.formatName(achievement.name().toLowerCase());


        if (achievementManager.getProgress()[achievement.ordinal()] >= achievement.getAmount()) {
            return;
        }
        if (player.getMinigame() != null) {
            return;
        }
        if (achievementManager.getProgress()[achievement.ordinal()]
                + amount > achievement.getAmount()) {
            amount = achievement.getAmount() - achievementManager.getProgress()[achievement.ordinal()];
        }
        achievementManager.getProgress()[achievement.ordinal()] += amount;

        if (achievement.getAmount() <= achievementManager.getProgress()[achievement.ordinal()]) {

            final Inventory inventory = player.getInventory();

            player.getPoints().increase(Points.ACHIEVEMENT_POINTS, achievement.getPointsReward());
            player.sendMessage("<img=789> <col=1aff1a>Achievement: You have completed the achievement: [" + formattedAchievementName + "]!");
            //player.sendMessage("<img=789> <col=1aff1a>Achievements completed: " + getTotalCompleted(player) + "/" + AchievementType.VALUES.length);
           // player.sendMessage("<img=789> <col=1aff1a>Achievement Points:  "+ player.getPoints().get(Points.ACHIEVEMENT_POINTS_NEW));


            achievementManager.getProgress()[achievement.ordinal()] = achievement.getAmount();
            ParticipationPoints.addPoints(player, 1 + Misc.getRandomInclusive(2) * achievement.getPointsReward(), "@dre@from completing achievements</col>.");
            AchievementManager.processFor(AchievementType.OVER_ACHIEVER, player);

            // Send jinglebit
            if (EntityExtKt.passedTime(player, Attribute.LAST_ACHIEVMENT_COMPLETION, 5000, TimeUnit.MILLISECONDS, false, true)) {
                player.getPacketSender().sendJinglebitMusic(Misc.randomElement(JINGEBIT_MUSIC_IDS), 0);
            }

            final Item[] rewards = achievement.getItemReward();
            final int requiredSlots = Arrays.stream(rewards).filter(Objects::nonNull).mapToInt(Item::getAmount).sum();

            if (!player.getGameMode().isUltimate()) {
                if (inventory.countFreeSlots() >= requiredSlots) {
                    //if(Stream.of(achievement.getItemReward()).allMatch(item -> inventory.getFreeSlots() >= achievement.getItemReward().length)) {
                    if (!player.getGameMode().isSpawn())
                    inventory.addItemSet(achievement.getItemReward());
                } else {
                    if (!player.getGameMode().isSpawn()) {
                        player.sendMessage("@red@The reward has been sent to your bank.");
                        player.getBank(player.getCurrentBankTab()).addItemSet(achievement.getItemReward());
                    }
                }
            } else {
                player.sendMessage("Ultimate Iron Man accounts are not eligible for achievement rewards.");
            }
            sendTab(player);
            packetSender.sendString(ACHIEVEMENT_COMPLETION_NAME, formattedAchievementName);
            player.setLastCompletedAchievement(achievement);

        } else if (achievementManager.progressIn(achievement) < achievement.getAmount()) {
            packetSender.sendString(ACHIEVEMENT_PROGRESSION_NAME, formattedAchievementName);
            packetSender.sendString(ACHIEVEMENT_PROGRESSION_DESC, achievement.getDescription());
            packetSender.sendMessage("percentage:" + ACHIEVEMENT_PROGRESSION_BAR + ":" + getPercentage(player, achievement));
        }
    }

    public static void sendTab(Player player) {
        for (AchievementDifficulty diff : AchievementDifficulty.values()) {
            display(player, diff);
        }
        player.getPacketSender().sendString(TOTAL_COMPLETED_LINE, getTotalCompleted(player) + "/" + AchievementType.values().length);
        player.getPacketSender().sendString(ACHIEVEMENT_POINTS_LINE, String.valueOf(player.getPoints().get(Points.ACHIEVEMENT_POINTS_NEW)));
    }

    private static void display(Player player, AchievementDifficulty diff) {
        ArrayList<AchievementType> list = getByDifficulty(diff);
        for (int i = 0; i < list.size(); i++) {
            player.getPacketSender().sendString(getLineId(diff) + i, Misc.formatName(list.get(i).name().toLowerCase()), true);
            player.getPacketSender().sendStringColour(getLineId(diff) + i, complete(player, list.get(i)) ? 0x00ff00 : inProgress(player, list.get(i)) ? 0xffff00 : 0xff0000);
        }
    }

    public static void displayFromTab(Player player, int button) {

        if (player.isInTutorial())
            return;

        Optional.ofNullable(getAchievement(button))
                .ifPresent(achievement -> sendAchievement(player, achievement));
    }

    public static void sendAchievement(Player player, AchievementType achievement) {

        final PacketSender packetSender = player.getPacketSender();
        final int amount = achievement.getItemReward().length;
        final boolean complete = complete(player, achievement);

        // Clear odd number items
        for (int i = 0; i < 9; i++)
            packetSender.sendItemOnInterface(77414 + i, -1, 1);
        // Clear even number items
        for (int i = 0; i < 8; i++)
            packetSender.sendItemOnInterface(77423 + i, -1, 1);

        // The id to start iterating on, depends on the amount of items and if the amount is even or odd
        int startId = (amount % 2 == 0 ? 77427 : 77418) - (amount / 2);

        for (int i = 0; i < amount; i++) {
            final Item reward = achievement.getItemReward()[i];
            packetSender.sendItemOnInterface(startId + i, reward.getId(), reward.getAmount());
        }

        packetSender.sendString(77403, Misc.uppercaseWords(achievement.name()));
        packetSender.sendString(77408, (complete ? "<str=" + 0xb8b8b8 + ">" : "") + achievement.getDescription() + (complete ? "</str>" : ""));
        packetSender.sendString(77413, achievement.getReward());
        packetSender.sendMessage("percentage:77436:" + getPercentage(player, achievement));
        packetSender.sendInterface(77400);
    }

    public static ArrayList<AchievementType> getByDifficulty(AchievementDifficulty diff) {
        ArrayList<AchievementType> achievements = new ArrayList<AchievementType>();
        for (AchievementType ach : AchievementType.values()) {
            if (ach.getDifficulty().equals(diff)) {
                achievements.add(ach);
            }
        }
        return achievements;
    }

    private static AchievementType getAchievement(int button) {
        int lineId = EASY_LINE;
        for (AchievementDifficulty difficulty : AchievementDifficulty.values()) {
            ArrayList<AchievementType> list = getByDifficulty(difficulty);
            if (button >= lineId && button < lineId + list.size()) {
                int id = button - lineId;
                return list.get(id);
            }
            lineId += list.size() + 1; // extra 1 for the subtitle
        }
        return null;
    }

    /**
     * The percentage completed for achievement
     */
    private static int getPercentage(Player player, AchievementType achievement) {
        int progress = player.getAchievements().getProgress()[achievement.ordinal()];
        if (progress >= achievement.getAmount()) {
            return 100;
        }
        return Math.toIntExact((progress * 100L / achievement.getAmount()));
    }

    /**
     * Gets total completed
     */
    private static int getTotalCompleted(Player player) {
        int total = 0;
        for (int i = 0; i < AchievementType.values().length; i++) {
            if (complete(player, AchievementType.values()[i])) {
                total++;
            }
        }
        return total;
    }

    public static boolean complete(Player player, AchievementType achievementType) {
        return player.getAchievements().getProgress()[achievementType
                .ordinal()] >= AchievementType.values()[achievementType.ordinal()].getAmount();
    }

    private static boolean inProgress(Player player, AchievementType achievementType) {
        int completedAmount = AchievementType.values()[achievementType.ordinal()].getAmount();

        return completedAmount > 1 && player.getAchievements().getProgress()[achievementType.ordinal()] > 0;
    }

    private int progressIn(AchievementType type) {
        return progress[type.ordinal()];
    }

    public int[] getProgress() {
        return progress;
    }

    public void copyProgress(int[] progress) {
        System.arraycopy(progress, 0, this.progress, 0, progress.length);
    }

    public static int getLineId(AchievementDifficulty difficulty) {
        int id = EASY_LINE;
        for (AchievementDifficulty diff : AchievementDifficulty.values()) {
            if (diff.equals(difficulty))
                return id;
            id += AchievementManager.getByDifficulty(diff).size() + 1; // extra 1 for the subtitle
        }
        return id;
    }
}
