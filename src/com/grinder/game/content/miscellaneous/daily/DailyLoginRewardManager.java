package com.grinder.game.content.miscellaneous.daily;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinder.Config;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.EffectTimer;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.FightCaveArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-01
 */
public class DailyLoginRewardManager {

    private static final Logger LOGGER = LogManager.getLogger(DailyLoginRewardManager.class.getSimpleName());

    /**
    * A list containing the {@link DailyLoginReward}'s.
    */
    private static final ArrayList<DailyLoginReward> loginRewards = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    /**
     * Initializes the {@link DailyLoginReward}'s.
     */
    public static void init() {
        loadEntries();
    }

    /**
     * Loading the data from the save file.
     */
    private static void loadEntries(){

        final File file = DailyLoginRewardConstants.PATH.toFile();

        if(!file.exists()){
            LOGGER.info("Did not load rewards file at "+DailyLoginRewardConstants.PATH);
            return;
        }

        try {

            final FileReader reader = new FileReader(file);

            try {

                final DailyLoginReward[] loginRewards = GSON.fromJson(reader, DailyLoginReward[].class);

                DailyLoginRewardManager.loginRewards.clear();

                if(loginRewards == null)
                    LOGGER.info("Loaded rewards are null (maybe the file is empty?)");
                else
                    Collections.addAll(DailyLoginRewardManager.loginRewards, loginRewards);

            } catch (Exception e){
                e.printStackTrace();
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saving the data into the save file.
     */
    private static void saveEntries() {
        final File file = DailyLoginRewardConstants.PATH.toFile();

        if(!file.exists()){
            LOGGER.info("file does not exist, creating...");
            try {
                if(file.createNewFile())
                    LOGGER.info("created file at '"+file.getPath()+"'.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {

            final FileWriter writer = new FileWriter(file);

            GSON.toJson(loginRewards.toArray(new DailyLoginReward[]{}), writer);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rewards the player on login.
     *
     * @param player the {@link Player} logging in.
     */
    public static void onLogin(final Player player) {
        if (cannotReceiveDailyReward(player)) {
            return;
        }
        // Player in a minigame
        //if (player.getMinigame() != null) {
        //    return;
        //}
        // Player in The Wilderness
       // if (AreaManager.inWilderness(player)) {
        //    return;
       // }
        // Player in a minigame
       // if (AreaManager.MINIGAME_LOBBY.contains(player)) {
      //      return;
       // }
        // Paayer in Fight caves
       // if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
       //     return;
       // }
        if (player.isNewPlayer()) {
            return;
        }
        try {

            final Date joinDate = DailyLoginRewardConstants.DATE_FORMAT.parse(player.getWelcome().getWelcome().getDate());
            final long timeSinceJoined = System.currentTimeMillis() - joinDate.getTime();

            if (timeSinceJoined < TimeUnit.DAYS.toMillis(DailyLoginRewardConstants.DAYS_REQUIRED))
                return;

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        final Iterator<DailyLoginReward> it = loginRewards.iterator();
        while (it.hasNext()) {

            final DailyLoginReward next = it.next();

            if (next.ipAddress.equals(player.getHostAddress())
                    || (PlayerUtil.isValidMacAddress(next.macAddress)
                    && next.macAddress.equals(player.getMacAddress()))) {

                final long timeSinceLastReward = System.currentTimeMillis() - next.lastReward;

                if (timeSinceLastReward < TimeUnit.HOURS.toMillis(12)) {
                    return;
                }

                if (timeSinceLastReward > TimeUnit.DAYS.toMillis(2)) {
                    // reset streaks
                    player.sendMessage("@red@Oh no! You have lost your daily login streak due to inactivity.");
                    player.getAttributes().numAttr(Attribute.DAILY_LOGIN_STREAK, 0).setValue(0);
                }

                it.remove();
            }
        }
        loginRewards.add(new DailyLoginReward(player.getHostAddress(), player.getMacAddress(), System.currentTimeMillis()));

        saveEntries();

        ParticipationPoints.addPoints(player, 5, "@dre@from daily login</col>.");

        AchievementManager.processFor(AchievementType.DAILY_ACTIVIST, player);
        AchievementManager.processFor(AchievementType.DAILY_SAVIOUR, player);
        AchievementManager.processFor(AchievementType.DAILY_FEED, player);

        player.getVotingBonusTimer().extendOrStart(1800);
        player.getPacketSender().sendEffectTimer(player.getVotingBonusTimer().secondsRemaining(), EffectTimer.VOTING_BONUS);


        int bonusReward = player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK) > 0 ? (500_000 * player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK)) : 0;
        int bonusBloodmoney = player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK) > 0 ? (1000 * player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK)) : 0;

        if (bonusBloodmoney >= 50_000) // set a cap
            bonusBloodmoney = 50_000;

        if (player.getInventory().countFreeSlots() > 4) {
            player.getInventory().add(995, 1_000_000 + bonusReward);
            player.getInventory().add(13307, 3500 + bonusBloodmoney);
            if (Misc.getRandomInclusive(5) == 1) {
            	player.getInventory().add(15267, 1);
            	player.getPacketSender().sendMessage("<img=779> @whi@You have received a Daily luck present as a bonus for daily login!");
            }
            if (PlayerUtil.isMediumStaff(player) && Misc.getRandomInclusive(5) == 1) {
                player.getInventory().add(15215, 1);
            	player.getPacketSender().sendMessage("<img=779> @whi@You have received a Staff's present as a bonus for daily login!");
            }
        } else {
            BankUtil.addToBank(player, new Item(995, 1_000_000 + bonusReward));
            BankUtil.addToBank(player, new Item(13307, 3500 + bonusBloodmoney));

            if (Misc.getRandomInclusive(5) == 1) {
                BankUtil.addToBank(player, new Item(15267, 1));
            	player.getPacketSender().sendMessage("<img=779> @whi@You have received a Daily luck present as a bonus for daily login!");
            }
            if (PlayerUtil.isMediumStaff(player) && Misc.getRandomInclusive(5) == 1) {
                BankUtil.addToBank(player, new Item(15215, 1));
            	player.getPacketSender().sendMessage("<img=779> @whi@You have received a Staff's present as a bonus for daily login!");
            }
            player.sendMessage("<img=779> @whi@The reward has been sent to your bank.");
        }

        // Add streaks
        player.getAttributes().numAttr(Attribute.TOTAL_LOGIN_STREAK, 0).incJ(1);
        player.getAttributes().numAttr(Attribute.DAILY_LOGIN_STREAK, 0).incJ(1);

        player.sendMessage("<img=779> @whi@You have received your daily login reward with a bonus of " + Misc.format(1_000_000 + bonusReward) +" coins and " + Misc.format(3500 + bonusBloodmoney) + " blood money for your streak.");
        player.sendMessage("<img=779> Your current daily login streak is " + player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK) +".");

        // We can later make bronze rank $35, donator $100, super $200, extreme $350, legendary $500, platinum $999, VIP $2499, Demon $4999
        // Platinum members get a free random mystery box every 3 days
        // VIP members get a free random mystery box every 2 days
        // Demon members get a free random mystery box everyday, no boss zone timer, items dont degrade, 25% bonus xp on all skills, triple voting points, 2x participation points, unique login gfx, etc

        // Reset weekly streak
        //if (player.getAttributes().numInt(Attribute.DAILY_LOGIN_STREAK) >= 7)
        //    player.getAttributes().numAttr(Attribute.DAILY_LOGIN_STREAK, 0).setValue(0);
        if (!player.getGameMode().isSpawn())
        Logging.log("DailyLoginRewards", "" + player.getUsername() +" received the daily reward from IP: " + player.getHostAddress() +" and MAC: " + player.getMacAddress());
    }

    private static boolean cannotReceiveDailyReward(final Player player) {

        if(!PlayerUtil.hasValidMacAddress(player))
            return true;
        if(player.getGameMode().isSpawn()){
            return true;
        }
        //return player.getGameMode().isAnyIronman();
        return false;
    }
}
