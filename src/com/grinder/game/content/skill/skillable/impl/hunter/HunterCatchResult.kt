package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player

/**
 * This class represents a simple report object that is generated as a result of a [HunterCatchAction].
 *
 * @param player    the [Player] initiating the [HunterCatchAction]
 * @param npc       the [HunterCatchAction.npc]
 * @param type      the [HunterCatchAction.type]
 * @param state     the [HunterCatchState] of the [HunterCatchAction]
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
class HunterCatchResult(
        val player: Player,
        val npc: NPC,
        val type: HunterCatchType,
        val state: HunterCatchState
)