package com.grinder.game.content.pvm.contract

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.points.ParticipationPoints
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.NPCActions
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.NpcID
import org.apache.logging.log4j.LogManager
import java.nio.file.Paths
import java.text.NumberFormat

/**
 * TODO: add documentation
 *
 * @author  2012 - wrote base
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/04/2020
 * @version 1.0
 */
object MonsterHunting {

    private val PATH = Paths.get("data", "monster_hunting.json")
    private val CONTRACTS_TYPE = object : TypeToken<HashSet<MonsterContract>>() {}.type!!
    private val BOUNTIES_TYPE = object : TypeToken<HashSet<MonsterBounty>>() {}.type!!
    private val GSON = GsonBuilder()
            .setPrettyPrinting()
            .create()!!

    private val LOGGER = LogManager.getLogger(MonsterHunting::class.java.simpleName)

    private val COST = Item(ItemID.COINS, 2_500_000)

    // expired contracts are only used to notify players that
    // their contract has expired upon login.
    private val expired = ArrayList<MonsterContract>()
    private val contracts = ArrayList<MonsterContract>()
    private val bounties = ArrayList<MonsterBounty>()

    // Static init block, loaded by ServerClassPreloader.
    init {

        load()

        TaskManager.submit(object : Task(2, true) {
            /*
             * Schedule a clean-up task, check for expired contract
             */
            override fun execute() {
                val contractIterator = contracts.iterator()
                while (contractIterator.hasNext()) {
                    val next = contractIterator.next()
                    if (next.isExpired()) {
                        contractIterator.remove()
                        World.findPlayerByName(next.playerName).ifPresentOrElse({
                            it.sendMessage("<img=757> Your contract for '${next.bounty.getNpcName()}' has expired!")
                        }, {
                            expired.add(next)
                        })
                    }
                }
            }
        })

        NPCActions.onClick(NpcID.ASHILD) {
            if(it.type == NPCActions.ClickAction.Type.SECOND_OPTION){
                ShopManager.open(it.player, ShopManager.BOSS_CONTRACT_STORE)
                return@onClick true
            }
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(it.npc.id)
                    .setExpression(DialogueExpression.DEFAULT)
                    .setText("Greetings ${it.player.appearance.genderName},",
                            "You up for a hunt?",
                            "It will cost you ${getCostString()} coins.")
                    .add(DialogueType.OPTION)
                    .firstOption("Yes.", this::assignContract)
                    .secondOption("Cancel contract.", this::cancelContract)
                    .thirdOption("What is this?", DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(it.npc.id)
                            .setExpression(DialogueExpression.HAPPY)
                            .setText("I sell monster bounties for ${getCostString()} coins.",
                                    "If you accept a contract and finish it within",
                                    "the provided time-frame.",
                                    "You will be rewarded accordingly.")
                            .add(DialogueType.NPC_STATEMENT)
                            .setExpression(DialogueExpression.SLEEPY)
                            .setText("When you kill the contract target,",
                                    "you gain a 20% drop rate boost.",
                                    "Additionally you get participation points,",
                                    "which can be spent in other stores!")
                    ::start)
                    .addCancel("No thanks.")
                    .start(it.player)
            return@onClick true
        }
    }

    fun onLogin(player: Player){

        contracts.find { it.ownedBy(player) }?.let {
            if(it.started())
                player.packetSender.sendMonsterHuntTracker(it)
        }

        expired.removeIf {
            if(it.playerName == player.username){
                player.sendMessage("<img=757> Your contract for '${it.bounty.getNpcName()}' has expired!")
                return@removeIf true
            }
            return@removeIf false
        }
    }

    fun addBounty(bounty: MonsterBounty){
        bounties.add(bounty)
    }

    fun removeBountyFor(npcId: Int){
        bounties.removeIf { it.npcId == npcId }
    }

    fun onNpcFight(player: Player, npc: NPC){

        val contract = contracts.find {
            it.ownedBy(player) && it.isTarget(npc)
        }?:return

        if(contract.started())
            return

        contract.start()

        player.packetSender.sendMonsterHuntTracker(contract)
    }

    fun onNpcKill(player: Player, npc: NPC, duration: Long){

        val contract = contracts.find {
            it.ownedBy(player) && it.isTarget(npc)
        }?:return


        if(!contracts.remove(contract)){
            player.sendMessage("@red@COULD NOT REMOVE CONTRACT!")
        }

        save()

        if(contract.completedInTime(duration)){
            val reward = contract.bounty.itemReward.clone()

            // Process achievements
            AchievementManager.processFor(AchievementType.CONTRACT_MASTER, player)
            AchievementManager.processFor(AchievementType.CONTRACT_EXPERT, player)
            AchievementManager.processFor(AchievementType.CONTRACT_JUNIOR, player)

            PlayerUtil.broadcastMessage("<img=91> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just completed the boss contract and received @dre@${NumberFormat.getIntegerInstance().format(COST.amount.toLong())}</col> coins!")
            player.sendMessage("<img=757> You finished your contract, the reward has been sent to your bank!")
            BankUtil.addToBank(player, reward)
            //player.points.increase(Points.BOSS_CONTRACT_POINTS, contract.bounty.pointReward)

            // Increase points & send message
            player.points.increase(Points.BOSS_CONTRACTS_FINISHED, 1) // Increase points
            player.sendMessage("You have completed " + player.points.get(AttributeManager.Points.BOSS_CONTRACTS_FINISHED) +" boss contracts.")
            ParticipationPoints.addPoints(player, 3 + Misc.getRandomInclusive(1), "@dre@from boss contracts</col>.")
        } else {
            player.sendMessage("<img=757> You failed to complete the monster contract in time.")
            player.points.increase(Points.BOSS_CONTACTS_FAILED, 1) // Increase points
        }

        player.packetSender.sendMonsterHuntTrackerStop(contract)
    }

    fun assignContract(player: Player){

        player.packetSender.sendInterfaceRemoval()

        if(player.gameMode.isAnyIronman){
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.ASHILD)
                    .setExpression(DialogueExpression.DISTRESSED)
                    .setText("I don't hand-out contracts",
                            "to Iron Man players!",
                            "Your lot is way too persistent,",
                            "bad for business you see!")
                    .start(player)
            return
        } else if(player.gameMode.isSpawn){
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.ASHILD)
                .setExpression(DialogueExpression.DISTRESSED)
                .setText("Sorry! I don't hand-out contracts",
                    "to Spawn game mode players.")
                .start(player)
            return
        }

        contracts.find { it.ownedBy(player) }?.let {
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.ASHILD)
                    .setExpression(DialogueExpression.LAUGHING)
                    .setText("You have an unfinished contract!",
                            "You need to kill @red@${it.bounty.getNpcName()}@bla@",
                            "in under ${it.secondsRemaining()} seconds.")
                    .start(player)
            return
        }

        if(!player.inventory.contains(COST)){
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.ASHILD)
                    .setExpression(DialogueExpression.ANNOYED)
                    .setText("This ain't a charity!",
                            "You need to pay me ${getCostString()} coins first!")
                    .start(player)
            return
        }

        player.inventory.delete(COST)

        val contract = MonsterContract(player.username, bounties.random())

        contracts.add(contract)
        save()

        player.packetSender.sendMonsterHuntTracker(contract)

        val reward = contract.bounty.itemReward

        DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.ASHILD)
                .setExpression(DialogueExpression.LAUGHING)
                .setText("Your contract is to kill",
                        "@red@${contract.bounty.getNpcName()}@bla@ in ${contract.secondsRemaining()} seconds!")
                .add(DialogueType.ITEM_STATEMENT)
                .setItem(reward.id, 200, "Reward")
                .setText("Your reward for completing the contract",
                        "is ${NumberFormat.getIntegerInstance().format(reward.amount.toLong())}x ${reward.definition.name}!")
                .start(player)
    }

    private fun cancelContract(player: Player){
        val contract = contracts.find { it.ownedBy(player) }
        if(contract == null){
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.ASHILD)
                    .setExpression(DialogueExpression.ANNOYED)
                    .setText("You don't have a contract, foolish ${player.appearance.getGenderName()}!")
                    .start(player)
            return
        }
        DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.ASHILD)
                .setExpression(DialogueExpression.EVIL_DELIGHTED)
                .setText("Canceling your current contract",
                        "will cost you ${getCostString()} coins!")
                .add(DialogueType.OPTION)
                .firstOption("Alright."){
                    if(!it.inventory.contains(COST)){
                        DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(NpcID.ASHILD)
                                .setExpression(DialogueExpression.ANNOYED)
                                .setText("This ain't a charity!",
                                        "You need to pay me ${getCostString()} coins first!")
                                .start(it)
                    } else {
                        player.inventory.delete(COST)
                        if(contract.started())
                            player.packetSender.sendMonsterHuntTrackerStop(contract)
                        contracts.remove(contract)
                        DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(NpcID.ASHILD)
                                .setExpression(DialogueExpression.HAPPY)
                                .setText("Your contract has been canceled!")
                                .start(it)
                    }
                }
                .addCancel()
                .start(player)
    }

    private fun getCostString(): String = NumberFormat.getIntegerInstance().format(COST.amount.toLong())

    /**
     * Save [bounties] and [contracts] to file at [PATH] using [GSON].
     */
    private fun save() {
        try {
            val file = PATH.toFile()
            file.createNewFile()
            val writer = file.bufferedWriter()

            val obj = JsonObject()
            obj.add("bounties", GSON.toJsonTree(bounties))
            obj.add("contracts", GSON.toJsonTree(contracts))
            GSON.toJson(obj, writer)
            writer.flush()
            writer.close()
        } catch (e: Exception){
            LOGGER.error("Failed to save bounties and contracts", e)
        }
    }

    /**
     * Load serialised [bounties] and [contracts] and spawn into world.
     */
    private fun load(){
        try {
            val file = PATH.toFile()

            if(!file.exists())
                return

            val reader = file.reader()
            val obj = JsonParser().parse(reader).asJsonObject

            bounties.clear()
            bounties.addAll(GSON.fromJson<HashSet<MonsterBounty>>(obj.get("bounties"), BOUNTIES_TYPE))

            contracts.clear()
            contracts.addAll(GSON.fromJson<HashSet<MonsterContract>>(obj.get("contracts"), CONTRACTS_TYPE))

            reader.close()

            LOGGER.info("Loaded ${bounties.size} bounties and ${contracts.size} contracts!")

        } catch (e: Exception){
            LOGGER.error("Failed to load bounties and/or contracts", e)
        }
    }
}