package com.grinder.game.model.areas.instanced

import com.grinder.game.World
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.InstancedArea
import java.util.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
class BlueMoonInnArea(val barNpc: NPC) : InstancedArea(Boundary(3215, 3234, 3392, 3403)) {

    override fun isMulti(agent: Agent?) = false
    override fun handleDeath(player: Player?, killer: Optional<Player>?) = false

    override fun handleDeath(npc: NPC?) = false

    override fun canAttack(attacker: Agent?, target: Agent?) = false
    override fun canEat(player: Player?, itemId: Int) = false

    override fun process(agent: Agent?) {

    }
    override fun leave(agent: Agent?) {
        super.leave(agent)
        if (agent is Player) {
            agent.moveTo(agent.position.clone().setZ(0))
            super.leave(barNpc)
            World.remove(barNpc)
        }
    }

    override fun canTeleport(player: Player?) = true

    override fun canTrade(player: Player?, target: Player?) = false

    override fun canDrink(player: Player?, itemId: Int) = false

    override fun handleObjectClick(player: Player?, obj:GameObject, type: Int) = false

    override fun onPlayerRightClick(player: Player?, rightClicked: Player?, option: Int) {

    }

    override fun dropItemsOnDeath(player: Player?, killer: Optional<Player>?) = false
    override fun defeated(player: Player?, agent: Agent?) {
    }
}