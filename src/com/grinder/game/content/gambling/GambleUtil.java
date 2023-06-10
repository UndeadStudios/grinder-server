package com.grinder.game.content.gambling;

import com.grinder.Config;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.definition.ItemValueDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.areas.impl.DicingArea;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseGambleLogs;
import com.grinder.net.codec.database.impl.DatabaseStakeLogs;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;

import java.util.List;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

public final class GambleUtil {

    public static void onCloseGambleWindow(Player player){
        final Player other = player.getGambling().getOther();
        player.getGambling().decline();
        other.getGambling().decline();
    }

    static boolean cannotRequestGamble(Player requested, Player player) {
        if (!Config.gambling_enabled) {
            player.sendMessage("The @red@[GAMBLING]</col> system has been switched @red@OFF</col> by the server administrator.");
            return true;
        }
        if (!(player.getArea() instanceof DicingArea)) {
            return true;
        }
        if (!(requested.getArea() instanceof DicingArea)) {
            return true;
        }
        if (!PlayerUtil.isDeveloper(player)) {
            if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
                player.sendMessage("You must have a bank PIN setup to gamble with other players.");
                return true;
            }
        }
/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("Your account is not allowed to gamble with other players.");
            return true;
        }*/
        if (player.getGameMode().isSpawn() && !requested.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't gamble except with spawn game mode players.", 1000);
            return true;
        }
        if (requested.getGameMode().isSpawn() && !player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't gamble except with spawn game mode players.", 1000);
            return true;
        }
        if (!MorphItems.INSTANCE.notTransformed(player, "gamble", true, false))
            return true;
        if (player.BLOCK_ALL_BUT_TALKING) {
            return true;
        }
        if (player.isInTutorial()) {
            return true;
        }
        if (player.getMinigame() != null) {
            return true;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.sendMessage("You can't gamble while AFK.");
            return true;
        }
        if (requested.getGameMode().isIronman() || requested.getGameMode().isHardcore() || requested.getGameMode().isUltimate()) {
            player.sendMessage("You can't gamble with Iron Man players.");
            return true;
        }

        if (player.getGameMode().isIronman()) {
            player.sendMessage("You can't gamble as an Iron Man.");
            return true;
        }
        if (player.getGameMode().isHardcore()) {
            player.sendMessage("You can't gamble as an Hardcore Iron Man.");
            return true;
        }
        if (player.getGameMode().isUltimate()) {
            player.sendMessage("You can't gamble as an Hardcore Iron Man.");
            return true;
        }

        if (!player.isDicer()) {
            player.sendMessage("You must have the dicer's rank to gamble with other players.");
            return true;
        }

        if (!requested.isDicer()) {
            player.sendMessage("You can only gamble with players that have the dicer's rank.");
            return true;
        }
        return false;
    }

    static void logWonItems(Player winner, Player loser, List<Item> winnings) {
        for (Item t : winnings) {
            if (ItemUtil.logItemIfValuable(t)) {
                if (!winner.getGameMode().isSpawn() && !loser.getGameMode().isSpawn()) {
                    Logging.log("gamble", "'" + winner.getUsername() + "' WON " + Misc.insertCommasToNumber(t.getAmount()) + " x " + t.getDefinition().getName() + " by gambling with '" + loser.getUsername() + "'" + (winner.getHostAddress().equals(loser.getHostAddress()) ? " Same Host" : " Different Host") + " Mode: " + winner.getGambling().getType());
                    new DatabaseGambleLogs(
                            SQLManager.Companion.getINSTANCE(),
                            winner.getUsername(),
                            loser.getUsername(),
                            t.getDefinition().getName(),
                            t.getAmount(),
                            (winner.getHostAddress().equals(loser.getHostAddress()) ? " Same Host" : " Different Host"),
                            winner.getGambling().getType().toString()
                    ).schedule(winner);
                    PlayerUtil.broadcastPlayerHighStaffMessage("'" + winner.getUsername() + "' WON @red@" + Misc.insertCommasToNumber(t.getAmount()) + " </col> x @red@ " + t.getDefinition().getName() + "</col> by gambling with '" + loser.getUsername() + "'");
                }
            }
        }
    }

    static boolean isBusy(Player requested, Player player) {
        // Safety Checks
        if (requested.busy() || requested.getInterfaceId() > 0
                || requested.getStatus().equals(PlayerStatus.TRADING)
                || requested.getStatus().equals(PlayerStatus.AWAY_FROM_KEYBOARD)
                || requested.getStatus().equals(PlayerStatus.DUELING)
                || requested.getStatus().equals(PlayerStatus.PRICE_CHECKING)
                || requested.getStatus().equals(PlayerStatus.BANKING)
                || requested.getStatus().equals(PlayerStatus.DICING)
                || requested.getStatus().equals(PlayerStatus.SHOPPING)) {
            player.debug("State: "+ requested.getStatus()+" Busy: "+ requested.busy());
            player.getPacketSender().sendMessage("The other player is busy at the moment.");
            return true;
        }
        if (player.busy() || player.getInterfaceId() > 0
                || player.getStatus().equals(PlayerStatus.TRADING)
                || player.getStatus().equals(PlayerStatus.AWAY_FROM_KEYBOARD)
                || player.getStatus().equals(PlayerStatus.DUELING)
                || player.getStatus().equals(PlayerStatus.PRICE_CHECKING)
                || player.getStatus().equals(PlayerStatus.BANKING)
                || player.getStatus().equals(PlayerStatus.DICING)
                || player.getStatus().equals(PlayerStatus.SHOPPING)) {
            player.debug("State: "+ player.getStatus()+" Busy: "+ player.busy());
            player.sendMessage("You are busy at the moment.");
            return true;
        }
        return false;
    }

    static boolean notInDicingArea(Player player2) {
        if (!(player2.getArea() instanceof DicingArea)) {
            return true;
        }
        return false;
    }

    static int countRequiredSlots(Player other, Player player, List<Item> playerItems, List<Item> otherItems) {
        int slotsNeeded = 0;
        for (Item t : playerItems) {
            slotsNeeded += t.getDefinition().isStackable() && other.getInventory().contains(t.getId()) ? 0 : 1;
        }

        for (Item t : otherItems) {
            slotsNeeded += t.getDefinition().isStackable() && player.getInventory().contains(t.getId()) ? 0 : 1;
        }
        return slotsNeeded;
    }

    static boolean maxStackItems(ItemContainer otherContainer, List<Item> playerItems, Player other) {
        return playerItems.stream().anyMatch(item -> {
            final long amount = item.getAmount();
            final long otherAmount = otherContainer.getAmount(item);
            final long otherInventoryAmount = other.getInventory().getAmount(item);
            final long totalAmount = amount + otherAmount + otherInventoryAmount;
            return totalAmount > Integer.MAX_VALUE;
        });
    }

    static boolean cannotGambleItem(int id, Item item, Player player) {
        if ((ItemValueDefinition.Companion.getValue(id, ItemValueType.ITEMS_VALUE) == 0 && id != BLOOD_MONEY && id != COINS)) {
            player.sendMessage("You can't gamble items that do not have a value!");
            return true;
        }

        if (!item.getDefinition().isTradeable()) {
            player.sendMessage("You can't gamble untradeable items!");
            return true;
        }

        if (item.getDefinition().getId() == ItemID.MYSTERY_BOX || item.getDefinition().getId() == ItemID.VOTING_TICKET) {
            if (player.getTimePlayed(TimeUnits.DAY) < 1) {
                player.getPacketSender().sendMessage("You must have at least a play time of 24 hours to gamble this item.", 1000);
                return true;
            }
        }
        return false;
    }
}
