package com.grinder.game.model.areas.instanced

import com.grinder.game.World
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.areas.InstancedArea
import com.grinder.util.NpcID
import java.util.*

class SkeletonHellhoundInstance(val height : Int) : InstancedArea(Boundary(3305, 3325, 9361, 9393)) {

    init {
        listOf(
                NPCFactory.create(NpcID.GREATER_SKELETON_HELLHOUND, Position(3317, 9364, height)),
                //NPCFactory.create(6614 , Position(3312, 9369, height)),
                NPCFactory.create(NpcID.GREATER_SKELETON_HELLHOUND, Position(3319, 9369, height)),
                NPCFactory.create(NpcID.GREATER_SKELETON_HELLHOUND, Position(3310, 9373, height)),
                NPCFactory.create(NpcID.GREATER_SKELETON_HELLHOUND, Position(3309, 9378, height)),
                NPCFactory.create(NpcID.GREATER_SKELETON_HELLHOUND, Position(3317, 9388, height))
        ).forEach {
            World.npcAddQueue.add(it)
            add(it)
        }
    }

    override fun process(agent: Agent) {}
    override fun defeated(player: Player, agent: Agent) {}
    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}

    override fun isMulti(agent: Agent) = true

    override fun canTeleport(player: Player) = true

    override fun canAttack(attacker: Agent, target: Agent) = !(attacker.isPlayer && target.isPlayer)

    override fun canTrade(player: Player, target: Player) = true

    override fun canDrink(player: Player, itemId: Int) = true

    override fun canEat(player: Player, itemId: Int) = true

    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>) = true

    override fun handleObjectClick(player:Player, obj: GameObject, actionType:Int) = false

    override fun handleDeath(player: Player, killer: Optional<Player>) = false

    override fun handleDeath(npc: NPC) = false
}