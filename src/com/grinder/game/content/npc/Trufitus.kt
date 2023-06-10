package com.grinder.game.content.npc

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstNPCAction
import com.grinder.util.NpcID

object Trufitus {

    init {
        onFirstNPCAction(NpcID.TRUFITUS) {
            startDialogue(player, npc)
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(npc.id)
                .setText("What do you want from me stranger?")
                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Umm, I was just wondering what you're doing here.").add(DialogueType.PLAYER_STATEMENT)
                .setText("I'm sailing back to Relleka, do you want to join me?").add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
                .setText("I would've sailed myself if I wanted to.", "Begone, stranger.")
                .start(player)
    }
}