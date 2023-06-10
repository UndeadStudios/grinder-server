package com.grinder.game.entity.agent.player;

import com.grinder.Config;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.content.miscellaneous.PlayerTitles;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.util.time.TimeUnits;
import net.runelite.cache.SpriteManager;

import java.util.*;
import java.util.stream.Stream;

/**
 * TODO: add documentation.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-22
 */
public class PlayerUtil {

    protected static final String NULLED_MAC = "null";
    protected static final String MAC_DENIED_ACCESS = "deniedAccess";
    protected static final String MAC_EXCEPTION = "exception";

    public static boolean isOnline(final String userName) {
        return World.findPlayerByName(userName).filter(Agent::isRegistered).isPresent();
    }

    /**
     * Determines whether the two specified players are using the same machine.
     *
     * @param playerA the first {@link Player}.
     * @param playerB the second {@link Player}.
     * @return {@code true} in case the players are likely to be using the same machine,
     * {@code false} if otherwise.
     */
    public static boolean isUsingSameMachine(final Player playerA, final Player playerB) {

        if (hasValidMacAddress(playerA) && hasValidMacAddress(playerB)) {
            if (playerA.getMacAddress().equals(playerB.getMacAddress())) {
                return true;
            }
        }
        if (playerA.getHostAddress().equals(playerB.getHostAddress())) {
            return true;
        }

        if (hasValidSerialNumber(playerA) && hasValidSerialNumber(playerB)) {
            if (playerA.getHdSerialNumber().equals(playerB.getHdSerialNumber())) {
                return true;
            }
        }
        if (hasValidSerialAddress(playerA) && hasValidSerialAddress(playerB)) {
            if (playerA.getSnAddress().equals(playerB.getSnAddress())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasValidMacAddress(final Player player) {
        return isValidMacAddress(player.getMacAddress());
    }

    public static boolean isValidMacAddress(final String macAddress) {
        return macAddress != null && !macAddress.equals(NULLED_MAC) && !macAddress.equals(MAC_DENIED_ACCESS) && !macAddress.equals(MAC_EXCEPTION);
    }

    public static boolean hasValidSerialNumber(final Player player) {
        final String serialNumber = player.getHdSerialNumber();
        if (serialNumber == null)
            return false;
        return !serialNumber.isEmpty();
    }

    public static boolean hasValidSerialAddress(final Player player) {
        final String serialAddress = player.getSnAddress();
        if (serialAddress == null)
            return false;
        return !serialAddress.isEmpty();
    }

    public static void configureDoubleRewardsAndXP() {
        final Calendar calendar = new GregorianCalendar();
        final int day = calendar.get(Calendar.DAY_OF_WEEK);
        final int hour = calendar.get(Calendar.HOUR);
        GameConstants.BONUS_DOUBLE_EXP_WEEKEND = false;
        GameConstants.BONUS_DOUBLE_REWARDS = false;
        if (day > 6 && hour >= 2 && hour <= 23) {
            GameConstants.BONUS_DOUBLE_EXP_WEEKEND = true;
            GameConstants.BONUS_DOUBLE_REWARDS = true;
        } else if (day < 2 && hour >= 2 && hour <= 23) {
            GameConstants.BONUS_DOUBLE_EXP_WEEKEND = true;
            GameConstants.BONUS_DOUBLE_REWARDS = true;
        }
    }

    public static void configureTripleVotingRewards() {
        final Calendar calendar = new GregorianCalendar();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        GameConstants.TRIPLE_VOTING_POINTS_EVENT = false;
        if (day > 0 && day <= 3) {
            GameConstants.TRIPLE_VOTING_POINTS_EVENT = true;
        }
    }

    public static Stream<Player> getStaffOnlineList() {
        return World.playerStream()
                .filter(player -> ((player.getRights().isStaff() || player.getRights().isHighStaff()) && !EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false)));
    }

    public static Stream<Player> getMiddleOnlineList() {
        return World.playerStream()
                .filter(player -> EntityExtKt.getBoolean(player, Attribute.MIDDLEMAN, false) && !EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false));
    }

    private static int slowCount = 0;

    public static int transformPlayerCount() { // Discord Bot

        final int playerCount = World.countPlayersOnline();

        if (!Config.DEVELOPER_MACHINE) {
            if ((World.getTick() & 31) == 0) {
                if (playerCount > 80) {
                    int countReach = (int) (playerCount * 2.6);
                    if (playerCount + slowCount < countReach)
                        slowCount++;
                } else if (playerCount > 40) {
                    int countReach = (int) (playerCount * 2.4);
                    int increasedCount = playerCount + slowCount;
                    if (increasedCount < countReach)
                        slowCount++;
                    else if (increasedCount > countReach) {
                        slowCount = Math.max(0, slowCount-1);
                    }
                } else if (playerCount > 5) {
                    int countReach = (int) (playerCount * 2.2);
                    int increasedCount = playerCount + slowCount;
                    if (increasedCount < countReach)
                        slowCount++;
                    else if (increasedCount > countReach) {
                        slowCount = Math.max(0, slowCount-1);
                    }
                }
            }
        }
        return playerCount + slowCount;
    }

    public static String getImages(Player player) {

        // Temp cheap fix (remove later)
/*        if (player.getCrown() > 27)
            player.setCrown(0);

        if (player.getCrown() > 0 && !isStaff(player) && !player.getRights().anyMatch(
                PlayerRights.YOUTUBER,
                PlayerRights.WIKI_EDITOR,
                PlayerRights.MIDDLEMAN,
                PlayerRights.EVENT_HOST,
                PlayerRights.EX_STAFF,
                PlayerRights.VETERAN,
                PlayerRights.RESPECTED,
                PlayerRights.CAMPAIGN_DEVELOPER,
                PlayerRights.CONTRIBUTOR,
                PlayerRights.MOTM
        )) {
            player.setCrown(0);
        }*/


        String primary = player.getRights().getImage() + " ";

        if (primary.equals("<img=1228> ") || primary.equals("<img=1025> ") || primary.equals("<img=745> ") || primary.equals("<img=746> ") || primary.equals("<img=747> ") || primary.equals("<img=1026> ")
            || primary.equals("<img=1227> ") || primary.equals("<img=1027> ") || primary.equals("<img=770> ")) {
            primary = "";
        }

        if (player.getRights().equals("NONE") && EntityExtKt.getBoolean(player, Attribute.MIDDLEMAN, false)) {
            primary = "<img=939> ";
        }

        if (player.getCrown() > 0) {
            primary = PlayerRights.values()[player.getCrown()].getImage() + " ";
        }

        String gameModeImage = player.getSkillManager().getGameModeRankStringCrown(player);

        if (!gameModeImage.isEmpty()) {
            gameModeImage += " ";
        }

        final PlayerRights memberRights = PlayerUtil.getMemberRights(player);

        String memberImage = "";

        if (memberRights.isMember()) {
            memberImage = memberRights.getImage() + " ";
        } else if (player.isDicer()) {
            memberImage = PlayerRights.DICER.getImage() + " ";
        }

        return (primary + gameModeImage + memberImage);
    }

    public static Stream<Player> playerStreamInDistanceOf(final Position position) {
        return World.playerStream().filter(player -> player != null && player.getPosition().isWithinDistance(position));
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastMessage(final String message) {
        World.playerStream().forEach(player -> {
            if(player != null)
                player.getPacketSender().sendMessage(message);
        });
    }

    /**
     * Broadcasts a special message to all players in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastSpecialMessage(String name, int type, final String message) {
        World.playerStream().forEach(player -> {
            if(player != null)
                player.sendSpecialMessage(name, type, message);
        });
    }

    /**
     * Broadcasts a message to all staff-members in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastPlayerStaffMessage(String message) {
        World.getStaffMembers().forEach((rights, players) -> {
            players.forEach(player -> {
                if (player != null && isStaff(player))
                    player.getPacketSender().sendMessage(message);
            });
        });
    }

    /**
     * Broadcasts a message to all Medium staff-members in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastPlayerMediumStaffMessage(String message) {
        World.getStaffMembers().forEach((rights, players) -> {
            players.forEach(player -> {
                if (player != null && (isMediumStaff(player) || EntityExtKt.getBoolean(player, Attribute.CAMPAIGN_DEVELOPER, false)))
                    player.getPacketSender().sendMessage(message);
            });
        });
    }

    /**
     * Broadcasts a message to all High staff-members in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastPlayerHighStaffMessage(String message) {
        World.getStaffMembers().forEach((rights, players) -> {
            players.forEach(player -> {
                if (player != null && (isHighStaff(player)))
                    player.getPacketSender().sendMessage(message);
            });
        });
    }

    /**
     * Broadcasts a message to all developers in the game.
     *
     * @param message The message to broadcast.
     */
    public static void broadcastPlayerDeveloperMessage(String message) {
        final HashSet<Player> developers = World.getStaffMembers().get(PlayerRights.DEVELOPER);
        if (developers != null && !developers.isEmpty()) {
            developers.removeIf(Objects::isNull);
            developers.forEach(developerPlayer -> developerPlayer.getPacketSender().sendMessage(message));
        }
    }

    public static String sendPlayTime(Player player) {
        TimeUnits firstUnit = TimeUnits.getHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));
        TimeUnits secondUnit = TimeUnits
                .getSecondHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));

        long firstAmount = player.getTimePlayed(TimeUnits.MILLISECOND) / firstUnit.getMilisecondValue();
        long secondAmount = player.getTimePlayed(TimeUnits.MILLISECOND) % firstUnit.getMilisecondValue()
                / secondUnit.getMilisecondValue();

        return "You've been playing for a total of " + firstAmount + " " + firstUnit.name().toLowerCase()
                + (firstAmount != 1 ? "s, and " : ", and ") + secondAmount + " " + secondUnit.name().toLowerCase()
                + (secondAmount != 1 ? "s." : ".");
    }

    public static String sendPlayTimeHans(Player player) {
        TimeUnits firstUnit = TimeUnits.getHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));
        TimeUnits secondUnit = TimeUnits
                .getSecondHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));

        long firstAmount = player.getTimePlayed(TimeUnits.MILLISECOND) / firstUnit.getMilisecondValue();
        long secondAmount = player.getTimePlayed(TimeUnits.MILLISECOND) % firstUnit.getMilisecondValue()
                / secondUnit.getMilisecondValue();

        return "You've spent " + firstAmount + " " + firstUnit.name().toLowerCase()
                + (firstAmount != 1 ? "s, and " : ", and ") + secondAmount + " " + secondUnit.name().toLowerCase()
                + (secondAmount != 1 ? "s" : "") + " in the world";
    }

    public static boolean hasAnyItems(Player player, int... itemIds) {
        for (int itemId : itemIds) {
            for (Bank bank : player.getBanks()) {
                if (bank != null) {
                    if (bank.contains(itemId))
                        return true;
                }
            }
            if (player.getInventory().contains(itemId))
                return true;
            if (player.getEquipment().contains(itemId))
                return true;
            if (player.getLootingBag().getContainer().contains(itemId))
                return true;
        }
        return false;
    }

    public static boolean carriesAnyItems(Player player, int... itemIds) {
        for (int itemId : itemIds) {
            if (player.getInventory().contains(itemId))
                return true;
            if (player.getEquipment().contains(itemId))
                return true;
            if (player.getLootingBag().getContainer().contains(itemId))
                return true;
        }
        return false;
    }

    public static void deleteAllItems(Player player, int... itemIds) {
        for (int itemId : itemIds) {
            for (Bank bank : player.getBanks()) {
                if (bank != null) {
                    bank.delete(itemId, 1);
                }
            }
            player.getInventory().delete(itemId, 1);
            player.getEquipment().delete(itemId, 1);
            player.getLootingBag().getContainer().delete(itemId, 1);
        }
    }

    public static boolean isStaff(Player player) { // All Staff
        return isMediumStaff(player) || player.getRights().anyMatch(
                PlayerRights.SERVER_SUPPORTER);
    }

    public static boolean isMediumStaff(Player player) { // Mod+
        return isHighStaff(player) || player.getRights().anyMatch(
                PlayerRights.MODERATOR,
                PlayerRights.GLOBAL_MODERATOR,
                PlayerRights.CAMPAIGN_DEVELOPER);
    }

    public static boolean isHighStaff(Player player) { // Admin+
        return isDeveloper(player) || player.getRights().anyMatch(
                PlayerRights.ADMINISTRATOR,
                PlayerRights.CO_OWNER);
    }

    public static boolean isDeveloper(Player player) {
        return player.getRights().anyMatch(PlayerRights.DEVELOPER, PlayerRights.OWNER);
    }

    public static String getNameWithTitle(Player player) {

        final String username = player.getUsername();

        if (!player.hasTitle())
            return username;

        final String title = player.getTitle();
        final PlayerTitles.PlayerTitle playerTitle = PlayerTitles.PlayerTitle.getTitlesWithColorWithSuffix().get(title);
        if (playerTitle != null && playerTitle.isSuffix())
            return username + " " + title.substring(3);
        return title + " " + username;
    }

    public static boolean isMember(Player player) {
        final PlayerRights rights = player.getRights();
        return rights.anyMatch(PlayerRights.RUBY_MEMBER,PlayerRights.TOPAZ_MEMBER,PlayerRights.AMETHYST_MEMBER,PlayerRights.LEGENDARY_MEMBER, PlayerRights.PLATINUM_MEMBER, PlayerRights.TITANIUM_MEMBER, PlayerRights.DIAMOND_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || player.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK);
    }

    public static boolean hasMemberYellPriviliges(Player player) {
        final PlayerRights rights = player.getRights();
        return rights.anyMatch(PlayerRights.RUBY_MEMBER, PlayerRights.TOPAZ_MEMBER, PlayerRights.AMETHYST_MEMBER, PlayerRights.LEGENDARY_MEMBER, PlayerRights.PLATINUM_MEMBER,
                PlayerRights.TITANIUM_MEMBER, PlayerRights.DIAMOND_MEMBER)
                || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49;
    }

    public static PlayerRights getMemberRights(Player player) {
        if (isDiamondMember(player))
            return PlayerRights.DIAMOND_MEMBER;
        if (isTitaniumMember(player))
            return PlayerRights.TITANIUM_MEMBER;
        if (isPlatinumMember(player))
            return PlayerRights.PLATINUM_MEMBER;
        if (isLegendaryMember(player))
            return PlayerRights.LEGENDARY_MEMBER;
        if (isAmethystMember(player))
            return PlayerRights.AMETHYST_MEMBER;
        if (isTopazMember(player))
            return PlayerRights.TOPAZ_MEMBER;
        if (isRubyMember(player))
            return PlayerRights.RUBY_MEMBER;
        if (isBronzeMember(player))
            return PlayerRights.BRONZE_MEMBER;
        return player.getRights(); // TODO: this isn't correct tbh since it can return anything, should actually return NONE, but better not change atm
    }

    public static String getMemberRightsName(Player player) {
        if (isDiamondMember(player))
            return "Diamond member";
        if (isTitaniumMember(player))
            return "Titanium member";
        if (isPlatinumMember(player))
            return "Platinum member";
        if (isLegendaryMember(player))
            return "Legendary member";
        if (isAmethystMember(player))
            return "Amethyst member";
        if (isTopazMember(player))
            return "Topaz member";
        if (isRubyMember(player))
            return "Ruby member";
        if (isBronzeMember(player))
            return "Bronze member";

        return "";
    }

    public static boolean isDiamondMember(Player player) {
        return player.getRights().equals(PlayerRights.DIAMOND_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999;
    }

    public static boolean isTitaniumMember(Player player) {
        return player.getRights().equals(PlayerRights.TITANIUM_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749;
    }

    public static boolean isPlatinumMember(Player player) {
        return player.getRights().equals(PlayerRights.PLATINUM_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499;
    }

    public static boolean isLegendaryMember(Player player) {
        return player.getRights().equals(PlayerRights.LEGENDARY_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249;
    }

    public static boolean isAmethystMember(Player player) {
        return player.getRights().equals(PlayerRights.AMETHYST_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 149;
    }

    public static boolean isTopazMember(Player player) {
        return player.getRights().equals(PlayerRights.TOPAZ_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99;
    }

    public static boolean isRubyMember(Player player) {
        return player.getRights().equals(PlayerRights.RUBY_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || player.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK);
    }

    public static boolean isBronzeMember(Player player) {
        return player.getRights().equals(PlayerRights.BRONZE_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 9;
    }

    public static boolean isDicer(Player player) {
        return player.isDicer();
    }
}
