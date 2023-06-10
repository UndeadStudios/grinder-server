package com.grinder.game.content.npc

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.util.NpcID

object Bardur {

    init {
        onFirstNPCAction(NpcID.BARDUR) {
            startDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.BARDUR) {
            // TODO: convert dialogue
            DialogueManager.start(player, 2551)
        }
    }

    private fun startDialogue(player: Player?, npc: NPC) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("What are you doing here?")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("I'm waiting the ancient dagannoth that dwells somewhere", "somewhere around here to show up again.").setExpression(DialogueExpression.DISTRESSED)
                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Where can I find the ancient dagannoth?")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("The last time I saw the dagannoth in this pond", "and totally disappeared after that.").setExpression(DialogueExpression.DISTRESSED)
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("Be careful there are many dangerous creatures out there.", "Good luck finding the dagannoth.").setExpression(DialogueExpression.CALM)
                .start(player!!)
    }
}