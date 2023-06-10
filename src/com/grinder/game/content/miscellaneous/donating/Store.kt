package com.grinder.game.content.miscellaneous.donating

import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.VotingTicket
import com.grinder.game.content.miscellaneous.Broadcast
import com.grinder.game.content.points.ParticipationPoints
import com.grinder.game.content.points.PremiumPoints
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.getInt
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Direction
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.task.TaskManager
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.impl.LookUpPlayerPurchase
import com.grinder.util.DiscordBot
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.ShopIdentifiers
import com.grinder.util.time.TimeUnits


/**
 * This object handles donation related behaviour.
 *
 * @see LookUpPlayerPurchase for the sql task
 *
 * @author  Blaketon
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   23/04/2020
 * @version 1.0
 */
object Store {

    /**
     * The URL that redirects to the web store
     */
    const val STORE_URL = "http://www.grinderscape.org/store"

    /**
     * Attempts to execute a [LookUpPlayerPurchase] task for the provided [Player]
     * and teleport the player to the store.
     */
    fun requestPurchaseLookup(player: Player){
        requestPurchaseLookup(player, true)
    }

    /**
     * Attempts to execute a [LookUpPlayerPurchase] task for the provided [Player].
     *
     * @param player                the [Player] to lookup the purchases for
     * @param promptTeleportToStore `true` if the player should be prompted to teleport to the store owner
     */
    fun requestPurchaseLookup(player: Player, promptTeleportToStore: Boolean){

        player.packetSender.sendInterfaceRemoval()

        if(player.isAccountFlagged || player.BLOCK_ALL_BUT_TALKING)
            return

        if (player.gameMode.isOneLife && player.fallenOneLifeGameMode()) {
            player.sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.")
            return
        }

        if (!player.gameMode.isUltimate) { // Skip for UIM they cannot hve a bank PIN
            if (player.achievementProgress(AchievementType.SELF_SECURE) == 0) {
                player.message("You must have a bank PIN set before you can redeem purchases.", Color.RED)
                return
            }
        }

        if (player.getTimePlayed(TimeUnits.MINUTE) < 30) {
            val timeLeft = 30 - player.getTimePlayed(TimeUnits.MINUTE)
            player.message("You must have played for at least $timeLeft more minutes before you can redeem purchases.", Color.RED)
            return
        }

        if(player.illegalAction(
                        PlayerStatus.AWAY_FROM_KEYBOARD,
                        PlayerStatus.PRICE_CHECKING,
                        PlayerStatus.BANKING,
                        PlayerStatus.TRADING,
                        PlayerStatus.DUELING))
            return

        if (player.hitpoints <= 0)
            return

        if (player.isInTutorial) {
            return
        }
        if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
            return
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You must wait 10 seconds after being out of combat before doing this action!", 1000)
            return
        }
        if (AreaManager.inWilderness(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while your in the Wilderness...")
            return
        }
        if (AreaManager.DUEL_ARENA.contains(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while in duel arena...")
            return
        }
        if (AreaManager.DuelFightArena.contains(player)) {
            player.sendMessage("<img=794> @red@Err, it is not smart to do this while in duel arena...")
            return
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You can't do that when you're busy.", 1000)
            return
        }

        if (!player.notTransformed(blockNpcOnly = false))
            return

        if (!player.passedTime(Attribute.SQL_ACTION, 15))
            return

        val playerName = player.username
        val playerIp = player.hostAddress?:""
        val playerMac = player.macAddress?:""

        val statement = "Redeeming"
        player.statement("", statement, hideContinue = true)
        player.BLOCK_ALL_BUT_TALKING = true
        TaskManager.submit(player, 1) { player.statement("", "$statement.", hideContinue = true) }
        TaskManager.submit(player, 2) { player.statement("", "$statement..", hideContinue = true) }
        TaskManager.submit(player, 3) { player.statement("", "$statement...", hideContinue = true) }
        TaskManager.submit(player, 4) { player.statement("", "$statement....", hideContinue = true) }
        TaskManager.submit(player, 5) { player.statement("", "$statement.....", hideContinue = true)
            LookUpPlayerPurchase(
                    SQLManager.INSTANCE,
                    promptTeleportToStore,
                    playerName,
                    playerIp,
                    playerMac
            ).schedule(player)
            TaskManager.submit(player, 2) {
                player.BLOCK_ALL_BUT_TALKING = false
            }
        }

    }

    /**
     * Opens the [ShopIdentifiers.HOLIDAY_PREMIUM_STORE] for the provided [Player]
     * or [ShopIdentifiers.HOLIDAY_PREMIUM_STORE_IRONMAN] if the player is any ironman.
     *
     * @param player the [Player] to open the store for
     */
    fun openPremiumStore(player: Player){
        DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
            .firstOption("Mystery Boxes & XP Tomes.") {
                ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES)
        }.secondOption("Luxury Items & Equipment.") {
            ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS)
        }.thirdOption("Unique Pets & Rare Costumes.") {
            ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE_PETS_MISC)
        }.fourthOption("Miscellenous Resources.") {
            ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE_RESOURCES)
        }.addCancel("Cancel.").start(player);

        /*if (player.gameMode.isAnyIronman) {
            ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE_IRONMAN)
        } else {
            ShopManager.open(player, ShopIdentifiers.HOLIDAY_PREMIUM_STORE)
        }*/
    }

    /**
     * Teleports the provided [Player] to the premium store.
     *
     * @param player the [Player] to teleport
     */
    fun teleportToPremiumStoreOwner(player: Player) {
/*        World.findNpc(Position(3108, 3493, 0)).ifPresent { npc ->
            player.removeInterfaces()
            player.openInterface(12468)
            player.packetSender.sendString("Premium Point Store Owner", 12558)
            player.packetSender.sendString("Premium Point Store location!", 12560)
            player.teleportToCaster = npc
            player.teleportDestination = npc.position.clone().move(Direction.SOUTH)
        }*/
        World.findNpcById(5792).ifPresent { npc ->
            player.removeInterfaces()
            player.openInterface(12468)
            player.packetSender.sendString("Premium Point Store Owner", 12558)
            player.packetSender.sendString("Premium Point Store location!", 12560)
            player.teleportToCaster = npc
            player.teleportDestination = npc.position.clone().move(Direction.SOUTH)
        }
    }

    /**
     * Gives the provided [PurchaseRewards] to the [Player].
     *
     * @param details   the [PurchaseRewards] containing all rewards for the order
     * @param player    the [Player] receiving the rewards
     */
    fun handleRewards(details: PurchaseRewards, player: Player, roundedPrice: Int) {
        var premiumPoints = details.premiumPoints
        var participationPoints = details.participationPoints
        var votingTickets = details.votingTickets
        var mysteryBoxes = details.mysteryBoxes
        val giveDiceRank = details.giveDiceRank


        if (GameConstants.PLAYERS_SPENDING_HUNDRED_ORMORE_10K_BONUS_PREMIUM_POINTS_EVENT) {
            if (roundedPrice >= 99) {
                premiumPoints += 10_000;
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received 10,000 bonus premium points from 10k fixed store event.")
            }
        } else if (GameConstants.BONUS_FOURTY_PERCENT_PREMIUM_POINTS_EVENT) {
            premiumPoints += (premiumPoints * 0.20).toInt();
            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received " + (premiumPoints * 0.40).toInt() + " bonus premium points from 40% store event.")
        } else if (GameConstants.DOUBLE_REWARDS_FIRST_PURCHASE_ABOVE_500_TOTAL_LEVEL_EVENT) {
            if (player.attributes.numInt(Attribute.AMOUNT_PAID) <= 0 && player.skillManager.countTotalLevel() > 500) {
                premiumPoints *= 2;
                votingTickets *= 2;
                mysteryBoxes *= 2;
                participationPoints *= 2;
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received " + (premiumPoints * 2) + " bonus premium points from first time purchase store event.")
            }
        } else if (GameConstants.PLAYERS_SPENDING_250_ORMORE_FREE_TWISTEDBOW_EVENT) {
            if (roundedPrice >= 249) {
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.TWISTED_BOW, 1))
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received Twisted bow as a bonus from store event.")
            }
        } else if (GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT) {
            if (roundedPrice >= 49) {
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.OLD_SCHOOL_BOND, 1))
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received $50.00 Member's rank bond as a bonus from store event.")
                GameConstants.FIRST_PLAYER_50_ORMORE_FREE_BOND_EVENT = false;
                Broadcast.removeGlobalBroadcast(player, "[LIMITED]: First to redeem $50.00 or more will receive a bonus free $50.00 bond.")
            }
        } else if (GameConstants.ALL_ORDERS_ABOVE_250_PHAT_SET_EVENT) {
            if (roundedPrice >= 249) {
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.PARTYHAT_SET, 1))
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received a Partyhat set as a bonus from store event.")
            }
        } else if (GameConstants.ALL_ORDERS_ABOVE_100_GETS_50K_PREMIUM_POINTS_EVENT) {
            if (roundedPrice >= 99) {
                premiumPoints += 50_000;
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received bonus 50,000 premium points by spending $100.00 or more store event.")
            }
        } else if (GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT) {
            if (roundedPrice >= 499) {
                premiumPoints += 100_000;
                if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[STORE]: " + player.username + " has received bonus 500,000 premium points as a bonus from first player to spend $500.00 store event.")
                GameConstants.FIRST_PLAYER_SPEND_500_GETS_500K_PREM_POINTS_EVENT = false;
                Broadcast.removeGlobalBroadcast(
                    player,
                    "[LIMITED]: The first player to spend $500.00 will receive bonus 100k premium points."
                )
            }
        }


        if (premiumPoints > 0)
            PremiumPoints.rewardPremiumPoints(player, premiumPoints, "Purchasing")

        if (participationPoints > 0)
            ParticipationPoints.addPoints(player, participationPoints, "@dre@from store shopping</col>.")

        if (votingTickets > 0)
            VotingTicket.addVotingTickets(player, votingTickets)

        if (mysteryBoxes > 0) {
            if (player.inventory.countFreeSlots() >= mysteryBoxes) {
                player.inventory.add(6199, mysteryBoxes)
            } else {
                BankUtil.addToBank(player, Item(6199, mysteryBoxes))
                player.message("<shad=15536940>The mystery box reward has " + (if (player.gameMode.isUltimate) "dropped under you" else "sent to your bank") + ".")
            }
        }

        if (giveDiceRank)
            tryGiveDiceRank(player)

        //player.message("<img=779> You can select your primary rank from the rank chooser in your quest tab.");
        player.message("<img=749><shad=15536940> You have received " + Misc.format(premiumPoints) + " Premium points.");
        player.message("<img=749><shad=15536940> You will be able to exchange your points with Party Pete at Edgeville!");
        player.message("<img=749><shad=15536940> Thank you very much for contributing to the game!");

        //Jinglebit
        player.packetSender.sendJinglebitMusic(231, 0)

        val memberRights = PlayerUtil.getMemberRights(player)
/*        if (player.gameMode.isAnyIronman) {

        } else if (!PlayerUtil.isStaff(player) && player.rights != memberRights) {
            player.rights = memberRights
            player.crown = memberRights.ordinal

            player.sendMessage("Congratulations, you're now a @dre@" + player.rights.toString() + "</col>.");
        }*/
        if (player.getInt(Attribute.AMOUNT_PAID) >= 9) {
            val memberRights = PlayerUtil.getMemberRights(player)
            player.sendMessage("<shad=15536940>Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.");
        }
        if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
            AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
        }
        if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
            AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
        }
        if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 150) {
            AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
        }
        if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
            AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
        }
        if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
            AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
        }
        if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
            AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
        }
        if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
            AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
        }
        player.packetSender.sendRights()
        PlayerSaving.save(player);
    }

    /**
     * If the provided [Player] is not already a dicer, and is not a staff member,
     * then the [Player.isDicer] field is set to true.
     *
     * @param player the [Player] to give the rank to
     */
    private fun tryGiveDiceRank(player: Player) {
        if (!player.isDicer) {
            if (!PlayerUtil.isStaff(player)) {
                player.isDicer = true
                AchievementManager.processFor(AchievementType.TRUSTED_MEMBER, player)
                player.message("<img=770> <shad=15536940>Congratulations, you've now unlocked the @dre@dicer's rank</col>!")
                player.message("<img=779> <shad=15536940>You can select your primary rank from the rank chooser in your quest tab.")
            } else
                player.message("Did not reward you with the dicer rank, since you're a staff member.")
        }
    }


    /**
     * Check and apply any 'extra' rewards for each individual claimed order.
     *
     * @param rewards   the [PurchaseRewards] containing all extras
     * @param paidTotal the total paid amount (rounded to nearest integer)
     */
    fun checkExtraRewards(rewards: PurchaseRewards, paidTotal: Int) {

        if(!rewards.giveDiceRank)
            rewards.giveDiceRank = paidTotal >= 100

        when {
            paidTotal >= 999 -> {
                rewards.extraPremiumPoints += 300_000
                rewards.mysteryBoxes += 35
                rewards.votingTickets += 75
                rewards.participationPoints += 170
            }
            paidTotal >= 750 -> {
                rewards.extraPremiumPoints += 225_000
                rewards.mysteryBoxes += 30
                rewards.votingTickets += 50
                rewards.participationPoints += 170
            }
            paidTotal >= 500 -> {
                rewards.extraPremiumPoints += 150_000
                rewards.mysteryBoxes += 25
                rewards.votingTickets += 35
                rewards.participationPoints += 125
            }
            paidTotal >= 200 -> {
                rewards.extraPremiumPoints += 50_000
                rewards.mysteryBoxes += 15
                rewards.votingTickets += 25
                rewards.participationPoints += 58
            }
            paidTotal >= 150 -> {
                rewards.extraPremiumPoints += 30_000
                rewards.mysteryBoxes += 10
                rewards.votingTickets += 15
                rewards.participationPoints += 34
            }
            paidTotal >= 100 -> {
                rewards.extraPremiumPoints += 20_000
                rewards.mysteryBoxes += 5
                rewards.votingTickets += 12
                rewards.participationPoints += 22
            }
            paidTotal >= 50 -> {
                rewards.extraPremiumPoints += 7_500
                rewards.mysteryBoxes += 3
                rewards.votingTickets += 10
                rewards.participationPoints += 15
            }
            paidTotal >= 35 -> {
                rewards.extraPremiumPoints += 3_500
                rewards.mysteryBoxes += 2
                rewards.votingTickets += 5
                rewards.participationPoints += 12
            }
            paidTotal >= 20 -> {
                rewards.extraPremiumPoints += 2_000
                rewards.participationPoints += 8
            }
            paidTotal >= 10 -> {
                rewards.extraPremiumPoints += 1_000
                rewards.participationPoints += 5
            }
            paidTotal >= 5 -> {
                rewards.extraPremiumPoints += 250
                rewards.participationPoints += 2
            }
        }
    }
}