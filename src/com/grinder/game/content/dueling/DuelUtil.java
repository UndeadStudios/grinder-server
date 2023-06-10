package com.grinder.game.content.dueling;

import com.grinder.Config;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.areas.impl.DuelArenaArea;
import com.grinder.game.model.areas.impl.DuelFightArena;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseStakeLogs;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;

import java.util.concurrent.TimeUnit;

/**
 * TODO: cleanup
 */
public final class DuelUtil {

    static boolean cannotRequestDuel(Player player, Player other) {
        if(player.getRights() != PlayerRights.DEVELOPER) {
            if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
                player.sendMessage("You must have a bank PIN setup to duel other players.");
                return true;
            }
        }

        if (!Config.staking_enabled) {
            player.sendMessage("The @red@[STAKING]</col> system has been switched @red@OFF</col> by the server administrator.");
            return true;
        }

/*        if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
            player.sendMessage("You must have at least a play time of 1 hour to be able to duel other players.");
            return true;
        }*/

/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("Your account is not allowed to duel other players.");
            return true;
        }*/

        if (!(player.getArea() instanceof DuelArenaArea))
            return true;

        if (!(other.getArea() instanceof DuelArenaArea))
            return true;

        if (player.getCurrentPet() != null) {
            player.getPacketSender().sendMessage("Please pick up your pet before dueling.");
            return true;
        }
        if (!MorphItems.INSTANCE.notTransformed(player, "duel", true, false))
            return true;
        if (player.BLOCK_ALL_BUT_TALKING) {
            return true;
        }
        if (player.isInTutorial()) {
            return true;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.sendMessage("You can't duel while AFK.");
            return true;
        }
        if (other.getArea() != null && other.getArea() instanceof DuelFightArena) {
            player.sendMessage("You can't challenge other players while they are in a duel.");
            return true;
        }
        if (other.getCurrentPet() != null) {
            player.getPacketSender().sendMessage("You can't challenge other players with a pet.");
            return true;
        }
        return false;
    }

    static boolean notInDuelArena(Player player) {
        return !(player.getArea() instanceof DuelArenaArea);
    }

    static boolean isBusy(Player other, Player player) {
        if (other.busy() || other.getInterfaceId() > 0
                || other.getStatus().equals(PlayerStatus.TRADING)
                || other.getStatus().equals(PlayerStatus.AWAY_FROM_KEYBOARD)
                || other.getStatus().equals(PlayerStatus.DUELING)
                || other.getStatus().equals(PlayerStatus.PRICE_CHECKING)
                || other.getStatus().equals(PlayerStatus.BANKING)
                || other.getStatus().equals(PlayerStatus.DICING)
                || other.getStatus().equals(PlayerStatus.SHOPPING)) {
            player.sendMessage("The other player is busy at the moment.");
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
            player.sendMessage("The other player is busy at the moment.");
            return true;
        }
        return false;
    }

    public static Position getRandomSpawn(boolean obstacle) {
        if (obstacle) {
            return new Position(3367 + Misc.getRandomInclusive(11), 3247 + Misc.getRandomInclusive(6));
        }
        return new Position(3336 + Misc.getRandomInclusive(11), 3247 + Misc.getRandomInclusive(6));
    }

    /**
     * Validates a player. Basically checks that all specified params add up.
     */
    static boolean validate(Player player, Player interact, PlayerStatus playerStatus, DuelState... duelStates) {

        // Verify player...
        if (player == null || interact == null)
            return false;

        // Make sure we have proper status
        if (playerStatus != null) {
            if (player.getStatus() != playerStatus)
                return false;
            // Make sure we're interacting with eachother
            if (interact.getStatus() != playerStatus)
                return false;
        }

        if (player.getDueling().getInteract() == null || player.getDueling().getInteract() != interact)
            return false;

        if (interact.getDueling().getInteract() == null || interact.getDueling().getInteract() != player)
            return false;

        // Make sure we have proper duel state.
        boolean found = false;
        for (DuelState duelState : duelStates) {
            if (player.getDueling().getState() == duelState) {
                found = true;
                break;
            }
        }

        if (!found)
            return false;

        // Do the same for our interact
        found = false;
        for (DuelState duelState : duelStates) {
            if (interact.getDueling().getState() == duelState) {
                found = true;
                break;
            }
        }

        return found;
    }

    static boolean cannotStakeItems(Player interact, Player player) {
        if (player.getGameMode().isSpawn() && !interact.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't stake items except with spawn game mode players.", 1000);
            return true;
        }
        if (interact.getGameMode().isSpawn() && !player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't stake items except with spawn game mode players.", 1000);
            return true;
        }
        if (interact.getGameMode().isAnyIronman()) {
            player.getPacketSender().sendMessage("You can't stake items when dueling with an Iron Man.", 1000);
            return true;
        }
        if (player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't stake items in spawn game mode.", 1000);
            return true;
        }
        if (player.getGameMode().isIronman()) {
            player.getPacketSender().sendMessage("You can't stake items as an Iron Man.", 1000);
            return true;
        }
        if (player.getGameMode().isHardcore()) {
            player.getPacketSender().sendMessage("You can't stake items as an Hardcore Iron Man.", 1000);
            return true;
        }
        if (player.getGameMode().isUltimate()) {
            player.getPacketSender().sendMessage("You can't stake items as an Hardcore Iron Man.", 1000);
            return true;
        }
        return false;
    }

    static boolean cannotStakeItem(Item item, Player player) {
        if (!item.getDefinition().isTradeable()) {
            player.getPacketSender().sendMessage("You can't stake untradeable items!", 1000);
            return true;
        }

        if (item.getDefinition().getId() == 6199 && player.getTimePlayed(TimeUnits.DAY) < 1) {
            player.getPacketSender().sendMessage("You must have at least a play time of 24 hours to stake this item.", 1000);
            return true;
        }
        return false;
    }

    static boolean cannotAcceptDuelSettings(Player player) {
        if (!EntityExtKt.passedTime(player, Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, false, true))
            return true;
        return player.getMinigame() != null;
    }

    static void notifyRuleChange(DuelRule rule, Player player, Player interact) {
        if (rule == DuelRule.LOCK_WEAPON) {
            player.sendMessage("@red@Warning! The rule 'Lock Weapon' has been enabled. You won't be able to change");
            player.sendMessage("@red@weapon during the duel!", 600);
            interact.sendMessage("@red@Warning! The rule 'Lock Weapon' has been enabled. You won't be able to change");
            interact.sendMessage("@red@weapon during the duel!", 600);
        }
        if (rule == DuelRule.NO_FOOD) {
            player.sendMessage("@red@You won't be able to eat during this duel!", 600);
            interact.sendMessage("@red@You won't be able to eat during this duel!", 600);
        }
        if (rule == DuelRule.NO_POTIONS) {
            player.sendMessage("@red@You won't be able to drink during this duel!", 600);
            interact.sendMessage("@red@You won't be able to drink during this duel!", 600);
        }
    }

    static void logStakeWinnings(Player interact, Player player) {
        logWinnings(interact, interact, player.getUsername(), player.getHostAddress());
        logWinnings(interact, player, player.getUsername(), player.getHostAddress());
    }

    private static void logWinnings(Player interact, Player player, String username, String hostAddress) {
        for (Item t : player.getDueling().getContainer().getValidItems()) {
            if (ItemUtil.logItemIfValuable(t)) {
                if (!player.getGameMode().isSpawn()) {
                    Logging.log("staking", "'" + interact.getUsername() + "' WON " + Misc.insertCommasToNumber(t.getAmount()) + " x " + t.getDefinition().getName() + " by staking with '" + username + "'" + (interact.getHostAddress().equals(hostAddress) ? " Same Host" : " Different Host"));
                    new DatabaseStakeLogs(
                            SQLManager.Companion.getINSTANCE(),
                            interact.getUsername(), // Player Name
                            username, // Receiver name
                            t.getDefinition().getName(),
                            t.getAmount(),
                            (interact.getHostAddress().equals(hostAddress) ? " Same Host" : " Different Host")
                    ).schedule(player);
                    StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.DUELING, interact.getUsername(), "WON @red@" + Misc.insertCommasToNumber(t.getAmount()) + " </col> x @red@ " + t.getDefinition().getName() + "</col> by @red@staking</col> with '" + username + "'");
                }
            }
        }
    }

    static int getFreeSlotsRequired(ItemContainer container, Player interact, Player player) {
        int slots = 0;

        // Count equipment that needs to be taken off
        for (int i = 11; i < player.getDueling().getRules().length; i++) {
            DuelRule rule = DuelRule.values()[i];
            if (player.getDueling().getRules()[rule.ordinal()]) {
                Item item = player.getEquipment().getItems()[rule.getEquipmentSlot()];
                if (!item.isValid()) {
                    continue;
                }
                if (!(item.getDefinition().isStackable() && player.getInventory().contains(item.getId())))
                    slots += rule.getInventorySpaceReq();
            }
        }

        // Count inventory slots from interact's container aswell as ours
        for (Item item : container.getItems()) {
            if (item == null || !item.isValid())
                continue;
            if (!(item.getDefinition().isStackable() && player.getInventory().contains(item.getId()))) {
                slots++;
            }
        }

        for (Item item : interact.getDueling().getContainer().getItems()) {
            if (item == null || !item.isValid())
                continue;
            if (!(item.getDefinition().isStackable() && player.getInventory().contains(item.getId()))) {
                slots++;
            }
        }

        return slots;
    }
}
