package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player

/**
 * Represents a [CombatEvent] that is fired at the last tick of the [target]'s death task.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/12/2020
 * @version 1.0
 */
class KilledTargetEvent(val killer: Agent, val target: Agent) : CombatEvent {

    /**
     * Execute the [function] only if [killer] is a [Player] and [target] is a [NPC].
     */
    fun ifPlayerKilledNpc(function: (Player, NPC) -> Unit) {
        if (killer is Player && target is NPC)
            function.invoke(killer, target)
    }
}