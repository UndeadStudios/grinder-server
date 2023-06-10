package com.grinder.game.content.`object`

import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstObjectAction
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID

object HotVentDoor {
    
    init {


        onFirstObjectAction(30266) {
            val player = it.player
            if (player.getBoolean(Attribute.CAN_ENTER_MOR_UL_REK)) {

                player.block()
                if (player.position.x == 2493 || player.position.x == 2494 ||player.position.x == 2495) {
                    if (player.position.x <= 2494) {
                        player.motion.enqueuePathToWithoutCollisionChecks(player.position.x + 2, player.position.y)
                    } else {
                        player.motion.enqueuePathToWithoutCollisionChecks(player.position.x - 2, player.position.y)
                    }

                } else {
                    if (player.position.y >= 5120) {
                        player.motion.enqueuePathToWithoutCollisionChecks(player.position.x, player.position.y - 2)
                    } else {
                        player.motion.enqueuePathToWithoutCollisionChecks(player.position.x, player.position.y + 2)
                    }
                }
                TaskManager.submit(2) {
                    player.unblock()
                }

            } else {
                if (player.inventory.contains(ItemID.FIRE_CAPE) || player.equipment.contains(ItemID.FIRE_CAPE)) {

                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.TZHAARKET_2187)
                        .setText("Oy! Get back from there, no JalYt allowed through.")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("I managed to defeat TzTok-Jad and obtain", "this fire cape.")
                        .add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.FIRE_CAPE, 200)
                        .setText("You hold out your fire cape and show it to TzHaar-Ket.")
                        .add(DialogueType.NPC_STATEMENT)
                        .setText("That is most impressive JalYt-Ket-" + player.username + ".")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("Surely this proves I am capable?", "Can I pleeease come through now?")
                        .add(DialogueType.NPC_STATEMENT)
                        .setText("I suppose so, I'll grant you access to Mor Ul Rek.", "The guards will open the gates for you, ", "you are the first JalYt to pass these gates!")
                        .setPostAction {
                                player.setBoolean(Attribute.CAN_ENTER_MOR_UL_REK, true)
                        }.start(player)
                } else {
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.TZHAARKET_2187)
                        .setText("Oy! Get back from there, no JalYt allowed through.").start(player)
                }

            }
            return@onFirstObjectAction
        }
    }
}