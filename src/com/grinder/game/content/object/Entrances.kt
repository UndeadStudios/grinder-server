package com.grinder.game.content.`object`

import com.grinder.game.content.miscellaneous.TravelSystem.fadeTravel
import com.grinder.game.content.miscellaneous.TravelSystem.instantMoveTo
import com.grinder.game.content.miscellaneous.TravelSystem.scheduleMoveTo
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.areas.impl.BossInstances
import com.grinder.game.model.areas.impl.BossInstances.Companion.instanceDialogue
import com.grinder.game.model.areas.instanced.CerberusArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import java.util.*
import java.util.concurrent.TimeUnit

object Entrances {

    private val CRAWL_ANIMATION = Animation(844)

    init {

        configureCaves()
        configureTunnels()

        /**
         * TODO: figure out which roof entrance this is, add meta-description
         */
        onFirstObjectAction(31681) {
            it.player.message("The roof entrance can only be opened with a brittle key.")
        }

        // corp entrance
        onFirstObjectAction(ObjectID.PASSAGE) {

            val player = it.player

            if (!player.passedTime(Attribute.CORPOREAL_BEAST_ENTRANCE_TIMER, 10, TimeUnit.SECONDS))
                return@onFirstObjectAction

            if (player.position.x == 2970 || player.position.x == 2971) {
                instanceDialogue(
                    player,
                    Position(2974, player.position.y, 2),
                    BossInstances.CORP, true
                )
            } else
                player.moveTo(Position(2970, player.position.y, 2))
        }
    }

    private fun configureTunnels() {

        onFirstObjectAction(ObjectID.TUNNEL_ENTRANCE) {
            val destine = if (it.player.position.z == 0)
                Position(3484, 9510, 2)
            else
                Position(3507, 9494, 0)
            instantMoveTo(it.player, destine, Direction.EAST)
        }

        // kalphite queen instanced boss entrance
        onFirstObjectAction(10230) {
            instanceDialogue(
                it.player, Position(3507, 9494, 0), BossInstances.KALPHITE_QUEEN, moveTo = true
            )
        }

        onFirstObjectAction(ObjectID.TUNNEL, ObjectID.CAVE_ENTRANCE_4) {
            val destine = if (it.player.position.x == 2796)
                Position(2808, 10001, 0)
            else
                Position(2796, 3615, 0)
            instantMoveTo(it.player, destine, Direction.EAST)
        }
    }

    private fun configureCaves() {

        onFirstObjectAction(ObjectID.CAVE_40, ObjectID.CAVE_46, ObjectID.CAVE_47, ObjectID.CAVE_48) {
            CerberusArea.handleCaveEntrance(it.player)
        }

        onFirstObjectAction(32047) {
            fadeTravel(it.player, destination = Position(2247, 4078, 0))
        }
        onFirstObjectAction(31999) {
            fadeTravel(it.player, destination = Position(2259, 10482, 0))
        }

        // Demonic Gorillas entrance
        onFirstObjectAction(28686) {
            val player = it.player
            fadeTravel(player, destination = Position(2128, 5647, 0))
            player.message("You enter the cavern beneath the crash site.", 3)
            player.message("Why would you want to go in there?", 3)
        }

        onFirstObjectAction(ObjectID.CAVERN) {
            val player = it.player

            if (player.wildernessLevel >= 50 && player.position.x >= 2983 && player.position.x <= 2988) {
                DialogueBuilder(DialogueType.STATEMENT)
                    .setText(
                        "WARNING - This is an extremely dangerous boss capable of",
                        "tearing you a new one in seconds. Proceed with caution!"
                    )
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Do you wish to challenge Merodach?")
                    .firstOption("Yes - I got this.") { player ->
                        val destination = Position(2130, 4682, 0)
                        player.packetSender.sendInterfaceRemoval()
                        scheduleMoveTo(it, CRAWL_ANIMATION, 1, destination)
                    }
                    .addCancel("No - That's all the persuading I need...")
                    .start(it.player)
            } else {
                val yOffset = if (player.position.y == 3938) 6394 else 6401
                val destination = player.position
                    .clone()
                    .add(0, yOffset)
                    .setZ(0)
                scheduleMoveTo(it, CRAWL_ANIMATION, 1, destination)
            }
        }
        onFirstObjectAction(30878) {
            val player = it.player
            instantMoveTo(player, Position(2984, 3943, 0), Direction.WEST)
        }

        // jungle terror cave entrance
        onFirstObjectAction(ObjectID.CAVE_ENTRANCE_19) {
            instantMoveTo(it.player, Position(3748, 9373, 0), Direction.EAST)
        }
        // jungle terror cave exit
        onFirstObjectAction(ObjectID.CAVE_6, ObjectID.CAVE_8) {
            val player = it.player
            instantMoveTo(player, Position(3749, 2973, 0), Direction.WEST)
        }

        // dagannoth cave entrance
        onFirstObjectAction(ObjectID.CAVE_ENTRANCE_40) {
            instantMoveTo(it.player, Position(2442, 10147, 0), Direction.WEST)
        }
        // dagannoth cave exit
        onFirstObjectAction(ObjectID.STEPS_2) {
            instantMoveTo(it.player, Position(2523, 3739, 0), Direction.WEST)
        }

        // tzhaar cave entrance
        onFirstObjectAction(ObjectID.CAVE_ENTRANCE_46) {
            instantMoveTo(it.player, Position(2480, 5175, 0), Direction.NORTH)
        }
        // tzhaar cave exit
        onFirstObjectAction(ObjectID.CAVE_EXIT_20) {
            instantMoveTo(it.player, Position(2862, 9572, 0), Direction.EAST)
        }

        onFirstObjectAction(ObjectID.CREVICE_17) {
            val destination = when (it.player.x) {
                2730 -> Position(2735, 10008, 0)
                else -> Position(2730, 10008, 0)
            }
            scheduleMoveTo(it, CRAWL_ANIMATION, 1, destination)
        }

        onFirstObjectAction(ObjectID.CREVICE_21) {
            val destination = when (it.player.x) {
                3232 -> Position(3232, 3950, 0)
                3243 -> Position(3243, 3948, 0)
                else -> Position(3233, 3938, 0)
            }
            scheduleMoveTo(it, CRAWL_ANIMATION, 1, destination)
        }

        onFirstObjectAction(ObjectID.CREVICE_24) {
            val destination = when (it.player.x) {
                2684 -> Position(2697, 9436, 0)
                else -> Position(2684, 9436, 0)
            }
            scheduleMoveTo(it, CRAWL_ANIMATION, 1, destination)
            it.player.message("You have passed through the crevice, be careful!", 1)
        }

        // enter brimhaven
        onFirstObjectAction(ObjectID.DUNGEON_ENTRANCE_3) {
            val player = it.player
            if (!player.getBoolean(Attribute.PAID_BRIMHAVEN_FEE))
                promptBrimHavenFee(player)
            else if (fadeTravel(player, destination = Position(2713, 9564, 0)))
                player.setBoolean(Attribute.PAID_BRIMHAVEN_FEE, false)
        }

        // exit brimhaven
        onFirstObjectAction(ObjectID.EXIT_17) {
            scheduleMoveTo(it, destination = Position(2744, 3153, 0))
        }
    }

    private fun promptBrimHavenFee(player: Player) {
        val fee = Item(ItemID.COINS, 875)
        val feeAmount = fee.amount
        player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.SANIBOCH }
            .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
            .ifPresent { sanibochNPC: NPC ->
                player.positionToFace = sanibochNPC.position
                sanibochNPC.setEntityInteraction(player)
                TaskManager.submit(4) {
                    sanibochNPC.resetEntityInteraction()
                    sanibochNPC.handlePositionFacing()
                }
            }
        DialogueBuilder(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.SANIBOCH).setExpression(DialogueExpression.EVIL)
            .setText("Hey, there is a $feeAmount coin fee if you wish", "to enter this dungeon.")
            .add(DialogueType.OPTION)
            .firstOption("Ok, here's $feeAmount coins.") { it1 ->
                if (!it1.hasItemInInventory(fee)) {
                    it1.statement("You need $feeAmount coins in your inventory.")
                } else if (it1.removeInventoryItem(fee, -1)) {
                    it1.setBoolean(Attribute.PAID_BRIMHAVEN_FEE, true)
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Ok, here's $feeAmount coins.")
                        .add(DialogueType.STATEMENT)
                        .setText("You give Saniboch $feeAmount coins.")
                        .setPostAction {
                            it1.inventory.refreshItems()
                        }
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SANIBOCH)
                        .setExpression(DialogueExpression.HAPPY)
                        .setText("Many thanks. You may now pass the door.", "May your death be a glorious one!")
                        .start(it1)
                }
            }
            .addCancel("Nevermind.")
            .start(player)
    }
}