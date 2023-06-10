package com.grinder.game.content.npc

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstNPCAction
import com.grinder.util.Misc
import com.grinder.util.NpcID

object Giles {

    init {
        onFirstNPCAction(NpcID.GILES_5441) {
            startDialogue(player, npc)
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        when {
            Misc.random(3) == 1 -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Why are you standing near the deposit safe?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Only a few Ultimate Iron Man warriors", "store their items here.").setExpression(DialogueExpression.ANGRY_4).add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Keeping them safe is my job.")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("Perhaps I can store my items in here too?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("That's right.")
                        .start(player)
            }
            Misc.random(3) == 2 -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Why are you standing near the deposit safe?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("I'm here watching for any criminals that try to steal", "the safe.").setExpression(DialogueExpression.DISTRESSED_2)
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("Fare well.").start(player)
            }
            Misc.random(3) == 3 -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("What's on your head?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("IT IS OBVIOUSLY TO PROTECT MY HEAD.", "ITS CALLED PROTECTION GEAR.").setExpression(DialogueExpression.DISTRESSED_2)
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("OKAYY!").setExpression(DialogueExpression.EVIL_3).start(player)
            }
            else -> {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Can you tell me what is the Bank Deposit Box", "used for?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                        .setText("Definitely! The Bank Deposit Box is used to store", "your items in the safe. It's not related to", "your bank in any way, so this means you", "can have extra free space.")
                        .setText("However, the safe can only fit up to 27 items.")
                        .start(player)
            }
        }
    }
}