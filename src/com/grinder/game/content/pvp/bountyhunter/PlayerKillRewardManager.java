package com.grinder.game.content.pvp.bountyhunter;

import com.grinder.game.content.pvp.WildernessScoreBoard;
import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.entity.agent.player.death.PlayerDeathUtil;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvp.bountyhunter.kill.Kill;
import com.grinder.game.content.pvp.bountyhunter.kill.KillVerification;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabasePkLogs;
import com.grinder.util.DiscordBot;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.model.attribute.AttributeManager.Points.*;
import static com.grinder.util.ItemID.BLOOD_MONEY;

/**
 * Handles PVP rewards
 *
 * @author 2012
 */
public class PlayerKillRewardManager {

    public static List<Kill> WILD_KILLS = new ArrayList<>();

    private static final int REPEATED_KILLS = 3;
    private static final int BASIC_REWARD = 3_000;
    private static final int CASH_REWARD = 1_250_000;
    private static final int CASH_KILL_STREAK_RATE = (int) 2.25;
    private static final int KILL_STREAK_RATE = 250;

    /**
     * Killing an opponent in the wild
     *
     * @param player   the player
     * @param opponent the opponent
     */
    public static void killedOpponent(final Player player, final Player opponent) {

        final String[] DEFEATED_MESSAGES = {"You were clearly a better fighter than " + opponent.getUsername() + "!", "" + opponent.getUsername() + " was no match for you!", "A humiliating defeat for " + opponent.getUsername() + ".", "You have defeated " + opponent.getUsername() + "!", "With a crushing blow you finish " + opponent.getUsername() + "!"};

        final BountyHuntController controller = player.getCombat().getBountyHuntController();
        final AttributeManager attributes = player.getPoints();
        final AttributeManager opponentAttributes = opponent.getPoints();

        player.sendMessage(Misc.randomString(DEFEATED_MESSAGES));

        if (!player.getGameMode().isSpawn() && !opponent.getGameMode().isSpawn()) {
            if (isUsingSameMachine(player, opponent)) {
                player.sendMessage("Your IP matches your opponent's IP address and you won't receive any rewards.");
                sendGlobalMessageOnKill(player, opponent, "same IP");

                Logging.log("InvalidPKKills", "Player: " + player.getUsername() + " uncounted kill " + opponent.getUsername() + " from IP: " + player.getHostAddress() + " MAC " + player.getMacAddress() + " Opponent's: " +
                        opponent.getHostAddress() + " MAC: " + opponent.getMacAddress() + " Levels: P/E: " + player.getSkillManager().calculateCombatLevel() + "/" + opponent.getSkillManager().calculateCombatLevel() + " Streaks: " + player.getPoints().check(KILLSTREAK) + "/" + opponent.getPoints().check(KILLSTREAK) + "");
                return;
            }
        }

        // Check that we haven't killed this player before..
        if (!player.getGameMode().isSpawn() && !opponent.getGameMode().isSpawn()) {
            if (controller.hasRecentlyKilled(opponent)) {
                player.sendMessage("You have recently killed this target and you won't receive any rewards.");
                sendGlobalMessageOnKill(player, opponent, "short time between kills");
                return;
            }
        }

        /*
         * The match counter
         */
        final KillVerification killVerification = KillVerification.getSerialMatchCount(player, opponent, TimeUnit.MINUTES.toMillis(15), true);

        /*
         * The amount of fights between the players
         */
        int matchCounts = killVerification.getTotalMatches();


        if (matchCounts < REPEATED_KILLS) {

            final int currentStreak = attributes.get(KILLSTREAK);

            if (currentStreak % 5 == 0 && currentStreak > 0) {
                PlayerUtil.broadcastMessage("<img=429>@red@ " + PlayerUtil.getImages(player) + "" + player.getUsername() +" is on a " + currentStreak
                        + " kill-streak. End his kill-streak for EXTRA " + NumberFormat.getInstance().format(currentStreak * KILL_STREAK_RATE + BASIC_REWARD)
                        + "  blood money and " + NumberFormat.getInstance().format(currentStreak * CASH_KILL_STREAK_RATE + CASH_REWARD) +" coins!");

                String reward = NumberFormat.getInstance().format(currentStreak * KILL_STREAK_RATE + BASIC_REWARD);
                String message = player.getUsername() + " is on a " + currentStreak + " kill streak - end it for "
                        + reward + " blood money and " + NumberFormat.getInstance().format(currentStreak * CASH_KILL_STREAK_RATE + CASH_REWARD) +" coins!";

                Broadcast.broadcast(null, 120, message, "");
            }

            int totalBloodMoneyLoot = BASIC_REWARD;
            int totalCashLoot = CASH_REWARD;

            if (player.getGameMode().isPure()|| player.getGameMode().isMaster())
                totalBloodMoneyLoot *= 1.15;

            if (currentStreak > 0) {
                totalBloodMoneyLoot += (currentStreak * KILL_STREAK_RATE);
                totalCashLoot += (currentStreak * CASH_KILL_STREAK_RATE);
            }

            totalBloodMoneyLoot += opponentAttributes.get(KILLSTREAK) * 250;
            totalCashLoot += opponentAttributes.get(KILLSTREAK) * 250_000;

            controller.addRecentKill(opponent.getMacAddress());

            if (opponentAttributes.get(KILLSTREAK) > 2) {
//                int ksReward = opponentAttributes.get(KILLSTREAK) * KILL_STREAK_RATE;
//                totalBloodMoneyLoot += ksReward;
                PlayerUtil.broadcastMessage("<img=429>@red@ " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has ended " + PlayerUtil.getImages(opponent) + "" + opponent.getUsername() +" kill-streak of " + opponentAttributes.get(KILLSTREAK) + " and was rewarded "
                        + NumberFormat.getInstance().format(totalBloodMoneyLoot) + " blood money and " + NumberFormat.getInstance().format(totalCashLoot) + " coins.");
            }

            attributes.increase(KILLS);
            attributes.increase(KILLSTREAK);
            opponentAttributes.set(KILLSTREAK, 0);

            if (AreaManager.inWilderness(player)) {

                int kills = attributes.get(AttributeManager.Points.KILLS);
                int killStreak = attributes.get(AttributeManager.Points.KILLSTREAK);

                WildernessScoreBoard.track(player.getUsername(), kills, killStreak);
            }

            controller.onKill();

            if (WILD_KILLS.size() >= 5)
                WILD_KILLS.remove(0);

            if (attributes.get(HIGHEST_KILLSTREAK) < attributes.get(KILLSTREAK))
                attributes.set(HIGHEST_KILLSTREAK, attributes.get(KILLSTREAK));

            if (attributes.get(KILLSTREAK) >= 10) {
                AchievementManager.processFor(AchievementType.RAMPAGE, player);
            }

            if (player.getWildernessLevel() >= 35) {
                AchievementManager.processFor(AchievementType.FEARLESS, player);
            }
            if (AreaManager.inMulti(player)) {
                AchievementManager.processFor(AchievementType.ASSISTANCE_REQUIRED, player);
            }
            if (player.getCurrentClanChat() != null) {
                AchievementManager.processFor(AchievementType.CLAN_WARFARE, player);
            }
            if (player.getSkillManager().getMaxLevel(Skill.HITPOINTS) == 99) {
                AchievementManager.processFor(AchievementType.SAFE_KILL, player);
            } else if (player.getSkillManager().getMaxLevel(Skill.HITPOINTS) < opponent.getSkillManager().getMaxLevel(Skill.HITPOINTS)) {
                AchievementManager.processFor(AchievementType.SAFE_KILL, player);
            }
            if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) == 1) {
                AchievementManager.processFor(AchievementType.DEAD_MAN_STALKING, player);
            }
            if (player.getEquipment().isEmpty()) {
                AchievementManager.processFor(AchievementType.MASTER_AT_ARMS, player);
            }
            if (player.getCombat().getRangedWeapon() != null && player.getEquipment().getItems()[EquipmentConstants.AMMUNITION_SLOT].getAmount() == 1) {
                AchievementManager.processFor(AchievementType.AMMO_CONSERVATION, player);
            }
            AchievementManager.processFor(AchievementType.THE_SAVIOUR, player);
            AchievementManager.processFor(AchievementType.COMMAND_AND_CONQUER, player);
            AchievementManager.processFor(AchievementType.CLEARING_THEM, player);
            AchievementManager.processFor(AchievementType.KILLING_THEM, player);

            player.sendMessage("<img=429>@red@ You have received " + NumberFormat.getInstance().format(totalBloodMoneyLoot) + " blood money and " + NumberFormat.getInstance().format(totalCashLoot) +" coins for defeating " + opponent.getUsername() + "!");
            // Logging
            if (!player.getGameMode().isSpawn() && !opponent.getGameMode().isSpawn()) {
                Logging.log("validPKKills", "Player: " + player.getUsername() + " killed " + opponent.getUsername() + " from IP: " + player.getHostAddress() + " MAC " + player.getMacAddress() + " Opponent's: " +
                        opponent.getHostAddress() + " MAC: " + opponent.getMacAddress() + " Levels: P/E: " + player.getSkillManager().calculateCombatLevel() + "/" + opponent.getSkillManager().calculateCombatLevel() + " Streaks: " + player.getPoints().check(KILLSTREAK) + "/" + opponent.getPoints().check(KILLSTREAK) + "");

                if (DiscordBot.ENABLED)
                    DiscordBot.INSTANCE.sendServerLogs("[VALID PK] " + player.getUsername() + " killed " + opponent.getUsername() + " from IP: " + player.getHostAddress() + " MAC " + player.getMacAddress() + " Opponent's: " +
                            opponent.getHostAddress() + " MAC: " + opponent.getMacAddress() + " Levels: P/E: " + player.getSkillManager().calculateCombatLevel() + "/" + opponent.getSkillManager().calculateCombatLevel() + " Streaks: " + player.getPoints().check(KILLSTREAK) + "/" + opponent.getPoints().check(KILLSTREAK) + "");

                // Database Logging
                new DatabasePkLogs(
                        SQLManager.Companion.getINSTANCE(),
                        player.getUsername(),
                        opponent.getUsername(),
                        player.getHostAddress(),
                        opponent.getHostAddress(),
                        player.getMacAddress(),
                        opponent.getMacAddress()
                ).schedule(player);
            }
            if (totalBloodMoneyLoot > 0)
                ItemOnGroundManager.register(player, new Item(BLOOD_MONEY, totalBloodMoneyLoot), opponent.getPosition().clone());

            StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.KILL, player.getUsername(),  "' has just killed " + opponent.getUsername() + " in wild with valid kill.");

            // If killed by player reward them an emblem with a low chance
            if (opponent != null) {
                opponent.ifPlayer(p -> {
                    // A slow chance of getting a bonus reward (emblems)
                    if (PlayerDeathUtil.rollRandomKillReward()) { // 25% chance of getting an emblem on killing a player as a bonus reward
                        final Item randomReward = PlayerDeathUtil.getRandomKillReward();
                        player.sendMessage("@red@" + p.getUsername() + " dropped a " + ItemUtil.format(randomReward) + "!");
                        PlayerUtil.broadcastMessage("<img=794> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has received a " + ItemUtil.format(randomReward) + " drop by killing " + PlayerUtil.getImages(p) + "" + p.getUsername() +"!");
                        ItemOnGroundManager.register(player, randomReward, opponent.getPosition().clone());
                    }
                });
            }

            register(player, opponent);

        } else
            player.sendMessage("You are not eligible for Blood money or coins reward as you have recently killed " + opponent.getUsername() + ".");

        opponentAttributes.set(KILLSTREAK, 0);

        AchievementManager.processFor(AchievementType.SAD_DEATH, opponent);
    }

    private static void sendGlobalMessageOnKill(Player player, Player opponent, String appendix) {
        opponent.getPoints().set(KILLSTREAK, 0);
        StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.INVALID_KILL, player.getUsername(), "invalidly killed "+opponent.getUsername()+" - reason: "+appendix);
    }

    /**
     * Checks if its a valid kill
     *
     * @param killer the killer
     * @param killed the killed
     * @return valid
     */
    private static boolean isUsingSameMachine(Player killer, Player killed) {
        return PlayerUtil.isUsingSameMachine(killer, killed);
    }

    /**
     * Registers a kill
     *
     * @param killer the killer
     * @param killed the killed
     */
    public static void register(final Player killer, final Player killed) {
        WILD_KILLS.add(new Kill(killer.getIndex(), killed.getIndex(), killer.getSnAddress(),
                killed.getSnAddress(), killer.getMacAddress(), killed.getMacAddress(), killer.getUsername(),
                killed.getUsername(), killer.getHostAddress(), killed.getHostAddress(), System.currentTimeMillis()));
    }

    /**
     * Resets kills
     */
    public static void resetKills() {
        WILD_KILLS.clear();
    }
}
