package com.grinder.game.entity.agent.combat.event.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item

/**
 * A [CombatEvent] fired for each item dropped on a npc kill.
 *
 * TODO: implement for player item drops?
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/12/2020
 * @version 1.0
 */
class DropItemLootEvent(val player: Player, val target: Agent, val item: Item) : CombatEvent {

    /**
     * If set to `false` the [item] will no longer be dropped.
     */
    var dropItemOnGround = true
}