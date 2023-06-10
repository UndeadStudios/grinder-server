package com.grinder.game.content.npc

import com.grinder.game.content.skill.SkillStats
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.Animation
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.NpcID
import kotlin.random.Random

object LeonDcour {

    init {
        onFirstNPCAction(NpcID.LEON_DCOUR) {
            startDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.LEON_DCOUR) {
            DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(npc.id)
                    .setText("You can claim it from the Mounted cape over here.").setExpression(DialogueExpression.ANGRY)
                    .start(player)
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        if (SkillStats.isMaxed(player)) { // If maxed
            npc.say("Welcome here champion " + player.username + ", good to see you!")
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id).setExpression(DialogueExpression.HAPPY)
                    .setText("Glad to see you around champion!")
                    .start(player)
        } else { // Player not maxed
            player.performAnimation(Animation(1064))
            TaskManager.submit(1) {
                player.playAreaSound(Sounds.getBlockSound(player, if (Random.nextBoolean()) 0 else 1))
                player.combat.queue(Damage(0, DamageMask.BLOCK))
                player.performAnimation(Animation(player.blockAnim))
                npc.performAnimation(Animation(407))
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Go away! Get maxed before talking to me.").setExpression(DialogueExpression.ANGRY)
                        .start(player)
            }
        }
    }
}