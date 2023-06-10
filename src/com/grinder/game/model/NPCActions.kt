package com.grinder.game.model

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
object NPCActions {

    private val npcActions = HashMap<Int, ArrayList<(ClickAction) -> Boolean>>()

    fun onClick(vararg npcIds: Int, function: (ClickAction) -> Boolean) {
        npcIds.forEach {
            npcActions.putIfAbsent(it, ArrayList())
            npcActions[it]!!.add(function)
        }
    }

    fun handleClick(player: Player, npc: NPC, type: ClickAction.Type) : Boolean {
        npcActions[npc.id]?.let { listeners ->
            for (listener in listeners) {
                if(listener.invoke(ClickAction(player, npc, type)))
                    return true
            }
        }
        return false
    }

    class ClickAction(val player: Player, val npc: NPC, val type: Type) {
        enum class Type {
            ATTACK,
            FIRST_OPTION,
            SECOND_OPTION,
            THIRD_OPTION,
            FOURTH_OPTION,
            CAST_SPELL
        }
    }
}