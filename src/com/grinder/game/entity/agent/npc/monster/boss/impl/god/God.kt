package com.grinder.game.entity.agent.npc.monster.boss.impl.god

import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.net.packet.sourcesPath

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
abstract class God(id: Int,
                   position: Position,
                   val inGodWars: Boolean = true
) : Boss(id, position), Religious {

    init {
        movementCoordinator.radius = 20
    }

    var minionsAlive = 3

    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

    override val circumscribedBoundaries: List<Boundary>
        get() = if(inGodWars) chamber().area.boundaries() else emptyList()

    override fun respawn() {
        spawn()
    }
}