package com.grinder.game.content.trading

import com.grinder.Config
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.StaffLogRelay
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.item.ItemUtil
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.impl.DatabaseTradeLogs
import com.grinder.util.ItemID
import com.grinder.util.Logging
import com.grinder.util.Misc
import com.grinder.util.time.TimeUnits

/**
 * TODO: add documentation
 *
 * @author Swiffy
 */
object TradeUtil {

    internal fun itemNotTradeable(player: Player, itemDefinition: ItemDefinition?): Boolean {
        if (itemDefinition == null || !itemDefinition.isTradeable) {
            player.message("This item can't be traded.")
            return true
        }
        if (itemDefinition.id == ItemID.MYSTERY_BOX || itemDefinition.id == ItemID.VOTING_TICKET) {
            if (player.getTimePlayed(TimeUnits.DAY) < 1) {
                player.message("You must have at least a play time of 24 hours to trade this item.")
                return true
            }
        }
        return false
    }

    internal fun logTradedItems(player: Player, other: Player) {
        for (item in other.trading.container.validItems) {

            if (ItemUtil.logItemIfValuable(item)) { // Other
                if (!player.gameMode.isSpawn && !other.gameMode.isSpawn) {
                    Logging.log(
                        "trades",
                        "'" + other.username + "' gave " + Misc.insertCommasToNumber(item.amount) + " x " + item.definition.name + " to '" + player.username + "'" + if (player.hostAddress == other.hostAddress) " Same Host" else " Different Host"
                    )
                    DatabaseTradeLogs(
                        SQLManager.INSTANCE,
                        other.username, // Player name
                        player.username, // Receiver name
                        item.definition.name,
                        item.amount,
                        sameHost = player.hostAddress == other.hostAddress
                    ).schedule(player)
                    StaffLogRelay.save(
                        StaffLogRelay.StaffLogType.TRADING,
                        other.username,
                        "gave @red@" + Misc.insertCommasToNumber(item.amount) + " </col> x @red@ " + item.definition.name + "</col> to '" + player.username + "'" + if (player.hostAddress == other.hostAddress) "@gre@Same Host" else "@red@Different Host"
                    )
                }
            }
        }
        for (item in player.trading.container.validItems) {

            if (ItemUtil.logItemIfValuable(item)) {
                if (!player.gameMode.isSpawn && !other.gameMode.isSpawn) {
                    Logging.log(
                        "trades",
                        "'" + player.username + "' gave " + Misc.insertCommasToNumber(item.amount) + " x " + item.definition.name + " to '" + other.username + "'" + if (player.hostAddress == other.hostAddress) " Same Host" else " Different Host"
                    )
                    DatabaseTradeLogs(
                        SQLManager.INSTANCE,
                        player.username, // Player Name
                        other.username, // Receiver name
                        item.definition.name,
                        item.amount,
                        sameHost = player.hostAddress == other.hostAddress
                    ).schedule(player)
                    StaffLogRelay.save(
                        StaffLogRelay.StaffLogType.TRADING,
                        player.username,
                        "gave @red@" + Misc.insertCommasToNumber(item.amount) + " </col> x @red@ " + item.definition.name + "</col> to '" + other.username + "'" + if (player.hostAddress == other.hostAddress) "@gre@Same Host" else "@red@Different Host"
                    )
                }
            }
        }
    }

    /**
     * Validates a player. Basically checks that all specified params add up.
     */
    internal fun cannotContinueState(player: Player?, interact: Player?, vararg states: TradeState): Boolean {
        if (player == null || interact == null) return true
        if (player.status !== PlayerStatus.TRADING) return true
        if (interact.status !== PlayerStatus.TRADING) return true
        if (player.trading.interact == null || player.trading.interact !== interact) return true
        if (interact.trading.interact == null || interact.trading.interact !== player) return true

        // Make sure we have proper duel state.
        var found = false
        for (duelState in states) {
            if (player.trading.state === duelState) {
                found = true
                break
            }
        }
        if (!found) {
            return true
        }

        // Do the same for our interact
        found = false
        for (state in states) {
            if (interact.trading.state === state) {
                found = true
                break
            }
        }
        return !found
    }

    internal fun cannotInitiateTrade(target: Player, player: Player): Boolean {

        if (!PlayerUtil.isDeveloper(player)) {
            if (player.achievements.progress[AchievementType.SELF_SECURE.ordinal] == 0) {
                player.message("You must have a bank PIN setup to trade other players.")
                return true
            }
        }

        if (!Config.trading_enabled) {
            player.sendMessage("The @red@[TRADING]</col> system has been switched @red@OFF</col> by the server administrator.")
            return true;
        }

/*        if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
            player.message("You must have at least a play time of 1 hour to be able to trade other players.")
            return true;
        }*/

        if (!player.notTransformed("trade", blockNpcOnly = false))
            return true

        if (player.BLOCK_ALL_BUT_TALKING || player.isInTutorial)
            return true

        if(player.illegalAction("duel", PlayerStatus.AWAY_FROM_KEYBOARD))
            return true

        if (target.gameMode.isSpawn && !player.gameMode.isSpawn) {
            player.message("You cannot trade with spawn game mode players.");
            return true;
        }
        if (player.gameMode.isSpawn && !target.gameMode.isSpawn) {
            player.message("You can only trade with spawn game mode players.");
            return true;
        }

        if (target.gameMode.isIronman && (/*!player.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(player) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message(target.username + " is an Iron Man. He stands alone.")
            return true
        }
        if (target.gameMode.isHardcore && (/*!player.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(player) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message(target.username + " is an Hardcore Iron Man. He stands alone.")
            return true
        }
        if (target.gameMode.isUltimate && (/*!player.username.equals("Mod Hellmage") && */!PlayerUtil.isDeveloper(player) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message(target.username + " is an Ultimate Iron Man. He stands alone.")
            return true
        }
        if (player.gameMode.isIronman && (!/*target.username.equals("Mod Hellmage") && !*/PlayerUtil.isDeveloper(target) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message("You can't trade as an Iron Man.")
            return true
        }
        if (player.gameMode.isHardcore && (!/*target.username.equals("Mod Hellmage") && !*/PlayerUtil.isDeveloper(player) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message("You can't trade as an Hardcore Iron Man.")
            return true
        }
        if (player.gameMode.isUltimate && (!/*target.username.equals("Mod Hellmage") && !*/PlayerUtil.isDeveloper(player) && !player.username.equals("Lord Hunterr")
                    && !target.username.equals("Lord Hunterr"))) {
            player.message("You can't trade as an Hardcore Iron Man.")
            return true
        }
        return target.minigame != null
    }

    internal fun cannotAcceptTrade(interact: Player, player: Player): Boolean {
        if (interact.busy() || interact.interfaceId > 0 || interact.status == PlayerStatus.TRADING || interact.status == PlayerStatus.AWAY_FROM_KEYBOARD || interact.status == PlayerStatus.DUELING || interact.status == PlayerStatus.PRICE_CHECKING || interact.status == PlayerStatus.BANKING || interact.status == PlayerStatus.DICING || interact.status == PlayerStatus.SHOPPING) {
            player.message("The other player is busy at the moment.")
            return true
        }
        if (interact.minigame != null) {
            player.message("You cannot trade players that are inside a minigame.")
            return true
        }
        if (player.minigame != null) {
            player.message("You cannot trade players that are inside a minigame.")
            return true
        }
        if (interact.area != null && interact.area == AreaManager.MINIGAME_LOBBY) {
            player.message("You cannot trade players that are inside a minigame.")
            return true
        }
        if (player.area != null && player.area == AreaManager.MINIGAME_LOBBY) {
            player.message("You cannot trade players while in a minigame.")
            return true
        }
        if (player.busy() || player.interfaceId > 0 || player.status == PlayerStatus.TRADING || player.status == PlayerStatus.AWAY_FROM_KEYBOARD || player.status == PlayerStatus.DUELING || player.status == PlayerStatus.PRICE_CHECKING || player.status == PlayerStatus.BANKING || player.status == PlayerStatus.DICING || player.status == PlayerStatus.SHOPPING) {
            player.message("The other player is busy at the moment.")
            return true
        }
        return false
    }
}