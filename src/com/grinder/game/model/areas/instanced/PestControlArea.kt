package com.grinder.game.model.areas.instanced

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.model.areas.MapInstance
import java.util.*

class PestControlArea(map: MapInstance) : InstancedArea(Boundary(2623, 2683, 2560, 2624)) {

    init {
        map.area = this
    }

    override fun isMulti(agent: Agent?) = true
    override fun handleDeath(player: Player?, killer: Optional<Player>?) = false

    override fun handleDeath(npc: NPC?) = false

    override fun canAttack(attacker: Agent?, target: Agent?) = false
    override fun canEat(player: Player?, itemId: Int) = false

    override fun process(agent: Agent?) {

    }

    override fun enter(agent: Agent?) {
        super.enter(agent)
    }

    override fun leave(agent: Agent?) {
        super.leave(agent)
        if (agent is Player) {
            agent.moveTo(agent.position.clone().setZ(0))
        }
    }

    override fun canTeleport(player: Player?) = true

    override fun canTrade(player: Player?, target: Player?) = false

    override fun canDrink(player: Player?, itemId: Int) = false

    override fun handleObjectClick(player: Player?, obj:GameObject, type: Int) = false

    override fun onPlayerRightClick(player: Player?, rightClicked: Player?, option: Int) {

    }

    override fun isSafeForHardcore(): Boolean {
        return true
    }

    override fun dropItemsOnDeath(player: Player?, killer: Optional<Player>?) = false
    override fun defeated(player: Player?, agent: Agent?) {
    }
}