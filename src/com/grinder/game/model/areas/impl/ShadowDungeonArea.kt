package com.grinder.game.model.areas.impl

import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.quest.impl.DesertTreasure
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.AreaListener
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.areas.Area
import java.util.*

/**
 * A class that represents the Smoke dungeon area.
 *
 * @author Blake
 */
class ShadowDungeonArea
/**
 * Constructs a new [ShadowDungeonArea].
 */
    : Area(Boundary(2622, 2795, 5054, 5104)), AreaListener {
    override fun process(agent: Agent) {
    }

    override fun enter(agent: Agent?) {
        if(agent is Player) {
            super.enter(agent)
            DesertTreasure.spawnDamis(agent)
        }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player){
            super.leave(agent)
            agent.packetSender.sendWalkableInterface(-1)
            QuestManager.despawnNpcs(agent)
        }
    }

    override fun defeated(player: Player, agent: Agent) {}
    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}
    override fun isMulti(agent: Agent): Boolean {
        return false
    }

    override fun canTeleport(player: Player): Boolean {
        return true
    }

    override fun canAttack(attacker: Agent, target: Agent): Boolean {
        return !attacker.isPlayer || !target.isPlayer
    }

    override fun canTrade(player: Player, target: Player): Boolean {
        return true
    }

    override fun canDrink(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun canEat(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>): Boolean {
        return true
    }

    override fun handleObjectClick(player: Player, obj: GameObject, actionType: Int): Boolean {
        return false
    }

    override fun handleDeath(player: Player, killer: Optional<Player>): Boolean {
        return false
    }

    override fun handleDeath(npc: NPC): Boolean {
        return false
    }
}