package com.grinder.game.content.miscellaneous.christmas._2022

import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.miscellaneous.christmas.SantaMinionNpc
import com.grinder.game.content.miscellaneous.christmas.SantaNpc
import com.grinder.game.content.miscellaneous.christmas.TownCrierNPC
import com.grinder.game.content.miscellaneous.christmas.WitchNpc
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022State.*
import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.entity.agent.movement.task.WalkToAction
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getEnum
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.entity.hasAttribute
import com.grinder.game.entity.setEnum
import com.grinder.game.model.*
import com.grinder.game.model.areas.instanced.NoraTheHagHouseArea
import com.grinder.game.model.attribute.Attribute.Companion.XMAS_2022
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueExpression.*
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.*
import com.grinder.util.NpcIdentifiers3.ELLAMARIA
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
object Christmas2022 {

    const val EMILY = 1315
    const val ZAMBO = 3205
    const val KAYLE = 1316
    const val BARTENDER = 687
    const val BARTENDER_1310 = 1310
    const val BARTENDER_1311 = 1311
    const val BARTENDER_1313 = 1313
    const val BARTENDER_1314 = 1314
    const val BARTENDER_1318 = 1318
    const val BARTENDER_1319 = 1319
    const val BARTENDER_1320 = 1320
    const val BAR_TENDER_ID = 1312
    private val SANTA = SantaNpc(1030, Position(3088, 3471, 0))
    private val SANTA_MINION = SantaMinionNpc(1478, Position(3093, 3469, 0))
    private val ITEMS_FOR_ANTIDOTE = intArrayOf(
            ItemID.BONES,
            ItemID.BANANA,
            ItemID.WEEDS,
            ItemID.EMPTY_VIAL)

    init {
        // you need to spawn em through here

       // World.npcAddQueue.add(SANTA)
      //  World.npcAddQueue.add(SANTA_MINION)
        World.npcAddQueue.add(TownCrierNPC())

        NPCActions.onClick(
                //SANTA.npcId(),
               // SANTA_MINION.npcId(),
                ZAMBO,
                EMILY,
                KAYLE,
                BARTENDER,
                BAR_TENDER_ID,
                BARTENDER_1310,
                BARTENDER_1311,
                BARTENDER_1313,
                BARTENDER_1314,
                BARTENDER_1318,
                BARTENDER_1319,
                BARTENDER_1320,
                NpcID.QUEEN_ELLAMARIA,
                NpcID.AEONISIG_RAISPHER_8043
                ) { action ->
            val player = action.player
            when (action.type) {
                NPCActions.ClickAction.Type.FIRST_OPTION -> player.getState().handleNpcOption(player, action.npc)

                else -> return@onClick false
            }
            return@onClick true
        }


        ObjectActions.onClick(
               // 27060, // xmas tree
              //  ObjectID.DRAWERS_14,
                ObjectID.STAIRCASE_61,
                ObjectID.STAIRCASE_152,// up stairs
                ObjectID.STAIRCASE_58,
                ObjectID.STAIRCASE_153 // down stairs

        ) {
            val player = it.player
            val objectMessage = it.objectActionMessage
            val objectId = objectMessage.objectId
            val objectPosition = Position(objectMessage.x, objectMessage.y, player.position.z)
            val objectDefinition = ObjectDefinition.forId(objectId)

//            if(objectId == ObjectID.EVERGREEN_5){
//                handleGiftTree(player, objectPosition)
//                return@onClick true
//            }


//            if (objectId == ObjectID.STAIRCASE_152 || objectId == ObjectID.STAIRCASE_153) {
//                handleNoraStairCase(objectMessage, objectId, player)
//                return@onClick true
//            }

//            if (objectId == ObjectID.STAIRCASE_61 || objectId == ObjectID.STAIRCASE_58) {
//                handleCastleStairCase(objectMessage, objectId, player)
//                return@onClick true
//            }

            return@onClick player.getState().handleObjectOption(player, objectId)
        }

        onFirstInventoryAction(ItemID.PRESENT_3) {
            if (player.getState() == RECEIVED_GIFT) {
                if (player.inventory.countFreeSlots() < 5) {
                    player.message("You must have 5 free inventory slots to open this present.")
                    return@onFirstInventoryAction
                }
                if (player.removeInventoryItem(getItem()?:return@onFirstInventoryAction)) {
                    player.addInventoryItems(listOf(
                            Item(ItemID.NULL_3060, 1),
                            Item(if (player.appearance.isMale) ItemID.NULL_3062 else ItemID.NULL_3068, 1),
                            Item(ItemID.NULL_3064, 1),
                            Item(ItemID.NULL_3066, 1),
                            Item(ItemID.NULL_3070, 1)
                    ))
                    player.message("<img=767> You have opened your @dre@Present</col>!")
                    player.setState(COMPLETED)
                }
            } else {
                player.message("I must have completed the Christmas 2022 event before trying to open this.")
                return@onFirstInventoryAction
            }
        }
        ItemActions.onClick(ItemID.LETTER) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if(isInInventory()) {
                player.packetSender.sendInterface(6965)
                player.packetSender.clearInterfaceText(6968, 6976)
                player.packetSender.sendString(6969, "Dear Ella,")
                player.packetSender.sendString(6971, "I completed the final draft!")
                player.packetSender.sendString(6972, "The potion is now effective.")
                player.packetSender.sendString(6974, "Regards,")
                player.packetSender.sendString(6975, "Nora")
                if (player.getState() == FOUND_LETTER)
                    player.setState(READ_LETTER)
                return@onClick true
            }
            return@onClick false
        }
    }

    private fun handleCastleStairCase(objectMessage: ObjectActions.ObjectActionDetails, objectId: Int, player: Player) {
        val up = objectId == ObjectID.STAIRCASE_61
        player.setWalkToTask(WalkToAction(player, objectMessage.gameObject,
            1,
            Executable {
                val endPosition = if (up)
                    Position(3213, 3476, 1)
                else
                    Position(3213, 3472, 0)

                player.block(blockMovement = true)
                player.setPositionToFace(endPosition, true)

                TaskManager.submit(1) {
                    player.unblock(unblockMovement = true)
                    player.moveTo(endPosition)
                }
            }
        ))
    }

    private fun handleNoraStairCase(objectMessage: ObjectActions.ObjectActionDetails, objectId: Int, player: Player) {
        val stairCase = objectMessage.gameObject

        val up = objectId == ObjectID.STAIRCASE_152
        player.setWalkToTask(WalkToAction(player, objectMessage.gameObject,
            1,
            Executable {

                val endPosition = if (up)
                    Position(2906, 3472, 1)
                else
                    Position(2906, 3468, 0)

                player.block(blockMovement = true)
                player.setPositionToFace(endPosition, true)

                //TaskManager.submit(1) {
                //    player.performAnimation(Animation(if (up) 828 else 827))
                //}
                if (up) {
                    when (player.getState()) {
                        FOUND_LETTER,
                        READ_LETTER,
                        SEE_WITCH -> {
                            player.packetSender.sendFadeScreen("You hear a mumbling...", 2, 3)
                            TaskManager.submit(1) {
                                player.packetSender.sendCameraNeutrality()
                            }
                        }
                        else -> {}
                    }
                }
                TaskManager.submit(2) {

                    val height = player.index shl 2

                    if (up) {

                        player.moveTo(endPosition.clone().setZ(height + 1))
                        when (player.getState()) {
                            FOUND_LETTER,
                            READ_LETTER,
                            SEE_WITCH -> {

                                val area = NoraTheHagHouseArea()
                                val nora = WitchNpc(Position(2903, 3468, height + 1))

                                World.npcAddQueue.add(nora)
                                area.add(player)
                                area.add(nora)
                                player.area = area
                                player.moveTo(Position(2906, 3472, height + 1))
                                TaskManager.submit(2) {
                                    player.setPositionToFace(nora.position, true)
                                }
                                nora.setPositionToFace(Position(2902, 3467), true)
                                player.packetSender.sendJinglebitMusic(2, 0)
                                TaskManager.submit(1) {
                                    nora.performAnimation(Animation(709))
                                    nora.say("Addictivo")
                                }
                                TaskManager.submit(4) {
                                    nora.performAnimation(Animation(802))
                                    nora.say("Momento")
                                }
                                TaskManager.submit(6) {
                                    nora.performGraphic(Graphic(1009, 0, 30))
                                    nora.performAnimation(Animation(1084))
                                    nora.say("Impregnato")
                                }
                                TaskManager.submit(7) {
                                    player.packetSender.sendCameraShake(3, 10, 3, 10)
                                }
                                TaskManager.submit(8) {
                                    player.packetSender.sendGraphic(
                                            Graphic(1010),
                                            Position(2902, 3467))
                                }

                                TaskManager.submit(9) {
                                    nora.setPositionToFace(player.position, true)
                                    nora.say("Oh my!")

                                    TaskManager.submit(1) {
                                        nora.performAnimation(Animation(714))
                                        nora.performGraphic(Graphic(308, 50, GraphicHeight.HIGH))

                                        TaskManager.submit(3) {
                                            ItemOnGroundManager.registerNonGlobal(player, Item(ItemID.BOOK_ON_CHEMICALS, 1), nora.position.clone())
                                            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                                                    .setText("Nora dropped a book!")
                                                    .setItem(ItemID.BOOK_ON_CHEMICALS, 200)
                                                    .start(player)
                                            World.remove(nora)
                                            area.remove(nora)
                                        }
                                    }
                                    player.packetSender.sendCameraNeutrality()
                                    player.unblock(unblockMovement = true)
                                }
                            }
                            else -> {
                                player.unblock(unblockMovement = true)
                            }
                        }
                    } else {
                        player.moveTo(endPosition.clone())
                        player.area?.let {
                            if (it is NoraTheHagHouseArea)
                                it.leave(player)
                        }
                        player.unblock(unblockMovement = true)
                    }
                }
            }))
    }

    private fun handleGiftTree(player: Player, objectPosition: Position) {
        player.positionToFace = objectPosition
        when {
            player.completedState(CURED_SANTA) -> {
                minionDialogue(HAPPY, "You already took a present!!").start(player)
            }
            player.getState() == CURED_SANTA -> {
                minionDialogue(HAPPY, "Thank you for saving Christmas!", "Please take a present from the tree.")
                        .add(DialogueType.ITEM_STATEMENT)
                        .setItem(ItemID.PRESENT_3, 200)
                        .setText("You take a present!")
                        .setAction(giftPlayer())
                        .start(player)
            }
            else -> {
                minionDialogue(ANGRY, "Shoo!!").start(player)
                SANTA_MINION.setEntityInteraction(player)
                SANTA_MINION.say("Shoo ${player.username}!")
                player.subscribe {
                    SANTA_MINION.resetEntityInteraction()
                    return@subscribe true
                }
            }
        }
    }

    private fun giftPlayer() = Consumer<Player> {
        if (it.beforeState(RECEIVED_GIFT)) {
            it.performAnimation(Animation(2281))
            if (it.inventory.countFreeSlots() < 1) {
                TaskManager.submit(1) {
                    it.statement("You don't have enough inventory space!")
                }
            } else {
                it.setState(RECEIVED_GIFT)
                it.addInventoryItem(Item(ItemID.PRESENT_3, 1), 1)
                it.message("@red@You have succesfully completed the Christmas 2022 event.")
                TaskManager.submit(1) {
                    it.progressAchievement(AchievementType.CHRISTMAS_2022)
                    PlayerUtil.broadcastMessage("<img=767> " + PlayerUtil.getImages(it) + "" + it.username +" has just completed the Christmas 2022 event!")
                }
            }
        }
    }

    fun handleItemOnItem(player: Player) : Boolean {

        val inventory = player.inventory

        if(inventory.containsAll(*ITEMS_FOR_ANTIDOTE)){

            if(player.getState() != MAKE_CURE)
                return false

            ITEMS_FOR_ANTIDOTE.forEach {
                inventory.delete(it, 1, false)
            }
            inventory.refreshItems()
            player.performAnimation(Animation(363))
            player.setState(HAND_OVER_CURE)
            player.addInventoryItem(Item(ItemID.MIXTURE, 1), 1)
            TaskManager.submit(1) {
                player.itemStatement(ItemID.MIXTURE, 200, "You created an antidote!")
            }
        }

        return false
    }

    fun Player.beforeState(state: Christmas2022State): Boolean{
        return getState().ordinal < state.ordinal
    }

    fun Player.completedState(state: Christmas2022State): Boolean{
        return getState().ordinal > state.ordinal
    }

    fun Player.getState(): Christmas2022State {
        if (!hasAttribute(XMAS_2022))
            return TALK_TO_MINIONS
        return getEnum(XMAS_2022)
    }

    fun Player.setState(state: Christmas2022State){
        setEnum(XMAS_2022, state)
    }

    fun minionDialogue(expression: DialogueExpression = DEFAULT, vararg text: String) = npcDialogue(SANTA_MINION.id, expression, *text)
    fun barDialogue(expression: DialogueExpression = DEFAULT, vararg text: String) = npcDialogue(BAR_TENDER_ID, expression, *text)

    fun npcDialogue(npcId: Int, expression: DialogueExpression = DEFAULT, vararg text: String) = DialogueBuilder(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(npcId)
            .setExpression(expression)
            .setText(*text)

    fun playerDialogue(expression: DialogueExpression = DEFAULT, vararg text: String) = DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(expression)
            .setText(*text)
}