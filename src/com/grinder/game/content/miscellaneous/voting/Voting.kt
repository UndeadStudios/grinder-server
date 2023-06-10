package com.grinder.game.content.miscellaneous.voting

import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.miscellaneous.Broadcast
import com.grinder.game.content.points.ParticipationPoints
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Direction
import com.grinder.game.model.EffectTimer
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.model.item.nameAndQuantity
import com.grinder.game.task.TaskManager
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.impl.LookUpPlayerVote
import com.grinder.util.DiscordBot
import com.grinder.util.Logging
import com.grinder.util.ShopIdentifiers
import com.grinder.util.time.TimeUnits
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Blaketon
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   23/04/2020
 * @version 1.0
 */
object Voting {

    const val VOTE_URL = "http://www.grinderscape.org/vote"

    fun requestVoteLookup(player: Player){
        requestVoteLookup(player, true)
    }

    fun requestVoteLookup(player: Player, promptTeleport: Boolean){

        player.packetSender.sendInterfaceRemoval()

        if (failsGeneralPredicates(player))
            return

        if (!player.passedTime(Attribute.SQL_ACTION, 15))
            return

        if (!player.passedTime(Attribute.LAST_VOTE, 11, TimeUnit.HOURS, updateIfPassed = false))
            return

        val statement = "Redeeming"
        player.statement("", statement, hideContinue = true)
        player.BLOCK_ALL_BUT_TALKING = true
        TaskManager.submit(player, 1) { player.statement("", "$statement.", hideContinue = true) }
        TaskManager.submit(player, 2) { player.statement("", "$statement..", hideContinue = true) }
        TaskManager.submit(player, 3) { player.statement("", "$statement...", hideContinue = true) }
        TaskManager.submit(player, 4) { player.statement("", "$statement....", hideContinue = true) }
        TaskManager.submit(player, 5) { player.statement("", "$statement.....", hideContinue = true)
            LookUpPlayerVote(
                    SQLManager.INSTANCE,
                    promptTeleport,
                    player.username
            ).schedule(player)
            TaskManager.submit(player, 2) { player.BLOCK_ALL_BUT_TALKING = false }
        }
    }

    fun openVotePointStore(player: Player){
        ShopManager.open(player, ShopIdentifiers.VOTING_STORE_IRONMAN)
/*        if (player.gameMode.isAnyIronman) {
            ShopManager.open(player, ShopIdentifiers.VOTING_STORE_IRONMAN)
        } else {
            ShopManager.open(player, ShopIdentifiers.VOTING_STORE)
        }*/
    }

    fun teleportToVotePointStore(it: Player) {
        World.findNpc(Position(3068, 3505, 0)).ifPresent { npc ->
            it.removeInterfaces()
            it.openInterface(12468)
            it.packetSender.sendString("Vote Point Store Owner", 12558)
            it.packetSender.sendString("Vote Point Store location!", 12560)
            it.teleportToCaster = npc
            it.teleportDestination = npc.position.clone().move(Direction.NORTH)
        }
    }

    fun giveRewards(player: Player, count: Int, totalVotePoints: Int) {
        var initialTotalVotePoints = totalVotePoints

        if (GameConstants.TRIPLE_VOTING_POINTS_EVENT) {
            initialTotalVotePoints *= 3
        }


        val votingTickets = Item(15031, initialTotalVotePoints)
        val votingBox = Item(15207, 1)
        val ticketsText = votingTickets.nameAndQuantity()


        player.points.increase(Points.TOTAL_VOTES)
        player.votingBonusTimer.extendOrStart(3600)
        player.packetSender.sendEffectTimer(player.votingBonusTimer.secondsRemaining(), EffectTimer.VOTING_BONUS)

        Broadcast.removeBroadcast(player, "You're eligible to vote for the server for great rewards. Click here!");

        //player.message("<img=766>@red@ Thank you for voting " + player.getUsername() + "!");
        //player.message("<img=766> You have received " + totalVotePoints + " voting points! "
        //		+ (extraPoints > 0 ? "@or3@You've received " + extraPoints + " extra points from bonus day!" : ""));
        // TODO: GET THIS TO WORK LIKE IT USED TO WORK ON OLD GS

        AchievementManager.processFor(AchievementType.COWBOW_DIPLOMACY, player)
        ParticipationPoints.addPoints(player, 6, "@dre@from voting</col>.")
        PlayerUtil.broadcastMessage(
                "<img=766> @red@" + PlayerUtil.getImages(player) + "${player.username} has just voted to the server and received $ticketsText!")
        player.message("<img=766> Thank you for voting ${player.username}!", Color.DARK_RED)
        player.message("You can redeem or exchange your voting tickets for blood money at the store in Edgeville bank.", Color.RED)

        if (player.points.get(Points.TOTAL_VOTES) > 1) {
        player.message("Your account has @red@" + player.points.get(Points.TOTAL_VOTES) + "</col> votes completed!")
        } else {
            player.message("Your account has @red@1</col> vote completed!")
        }

        // Discord logging
        if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendVoteLogs("[VOTING]: " + player.username + " voted to the server. Votes received: " + initialTotalVotePoints +", Total Votes Made: " + player.points.get(Points.TOTAL_VOTES) + ", IP Address: " + player.hostAddress + ", MAC: " + player.macAddress)

        //Jinglebit
        player.packetSender.sendJinglebitMusic(231, 0)

        // Unique case for UIM
        if (player.gameMode.isUltimate) {
            ItemContainerUtil.addOrDrop(player.inventory, player, votingBox)
            ItemContainerUtil.addOrDrop(player.inventory, player, votingTickets)
        } else {
            if (player.inventory.countFreeSlots() < 1 + initialTotalVotePoints) {
                BankUtil.addToBank(player, votingTickets, false)
                BankUtil.addToBank(player, votingBox, false)
                player.message("<img=766> Your vote rewards have been added to your bank!")
            } else {
                ItemContainerUtil.addOrDrop(player.inventory, player, votingBox)
                ItemContainerUtil.addOrDrop(player.inventory, player, votingTickets)
            }
        }
        if (player.inventory.getAmount(15031) > 50 || player.inventory.getAmount(15207) > 50) {
            PlayerUtil.broadcastPlayerMediumStaffMessage("<img=750> @red@ " + player.username + " got 50+ voting tickets/boxes.")
            Logging.log("massvotingtickets", player.username + " got 50+ voting tickets/boxes in inventory")
        }
        player.message("<img=766> For the next hour you will receive 25% bonus exeperience and 10% boosted drop rates.", Color.RED)
    }

    fun isEligibleToVote(player: Player): Boolean {
        if (player.getTimePlayed(TimeUnits.MINUTE) < 30) {
            val timeLeft = 30 - player.getTimePlayed(TimeUnits.MINUTE)
            player.message("You must have played for at least $timeLeft more minutes before you can redeem votes.", Color.RED)
            return false
        }
        if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
            return false
        }
        if (!player.passedTime(Attribute.SQL_ACTION, 15))
            return false

        if (!player.passedTime(Attribute.LAST_VOTE, 11, TimeUnit.HOURS, updateIfPassed = false))
            return false

        return true
    }

    fun hasPendingVote(player: Player): Boolean {
        if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
            return false
        }

        return true
    }

    /**
     * TODO: these checks should be moved to a static method, so
     * we don't keep duplicating the same code. Makes it hard to maintain etc..
     */
    private fun failsGeneralPredicates(player: Player): Boolean {
        if (!player.gameMode.isUltimate) { // Skip for UIM they cannot hve a bank PIN
            if (player.achievementProgress(AchievementType.SELF_SECURE) == 0) {
                player.message("You must have a bank PIN set before you can redeem votes.", Color.RED)
                return true
            }
        }

        if (player.getTimePlayed(TimeUnits.MINUTE) < 30) {
            val timeLeft = 30 - player.getTimePlayed(TimeUnits.MINUTE)
            player.message("You must have played for at least $timeLeft more minutes before you can redeem votes.", Color.RED)
            return true
        }

        if (player.hitpoints <= 0) return true

        if(player.illegalAction(
                        PlayerStatus.AWAY_FROM_KEYBOARD,
                        PlayerStatus.PRICE_CHECKING,
                        PlayerStatus.BANKING,
                        PlayerStatus.TRADING,
                        PlayerStatus.DUELING))
            return true

        if (player.isInTutorial) {
            return true
        }
        if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
            return true
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You must wait 10 seconds after being out of combat before doing this action!", 1000)
            return true
        }
        if (AreaManager.inWilderness(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while your in the Wilderness...")
            return true
        }
        if (AreaManager.DUEL_ARENA.contains(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while in duel arena...")
            return true
        }
        if (AreaManager.DuelFightArena.contains(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while in duel arena...")
            return true
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You can't do that when you're busy.", 1000)
            return true
        }

        if (!player.notTransformed(blockNpcOnly = false))
            return true

        return false
    }
}