package com.grinder.game.model.areas.impl

import com.grinder.game.World.tick
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.AreaListener
import com.grinder.game.model.areas.impl.SmokeDungeonArea
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.util.ItemID
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.areas.Area
import com.grinder.game.model.attribute.Attribute
import java.util.*

/**
 * A class that represents the Smoke dungeon area.
 *
 * @author Blake
 */
class SmokeDungeonArea
/**
 * Constructs a new [SmokeDungeonArea].
 */
    : Area(Boundary(3200, 3327, 9344, 9407)), AreaListener {
    override fun process(agent: Agent) {
        if (agent is Player) {
            val player = agent.getAsPlayer()
            if (tick % DAMAGE_INTERVAL == 0 && !isProtected(player) && player.hitpoints > 1) {
                var damage = 10
                if (player.hitpoints - damage < 1) damage = player.hitpoints - 1
                player.combat.queue(Damage.create(damage))
                player.say("*choke*")
            }
        }
    }

    override fun enter(agent: Agent?) {
        if(agent is Player) {
            super.enter(agent)
        }
        if(agent is Player){
                agent.packetSender.sendWalkableInterface(16152)
            }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player){
            super.leave(agent)
            agent.packetSender.sendWalkableInterface(-1)
            QuestManager.despawnNpcs(agent)
        }
    }

    /**
     * Determines if the player is protected from the smoke in this dungeon.
     *
     * @param player The player.
     * @return `true` if protected
     */
    private fun isProtected(player: Player): Boolean {
        return player.equipment.containsAtSlot(
            EquipmentConstants.HEAD_SLOT,
            ItemID.GAS_MASK
        ) || EquipmentUtil.isSmokeProtect(player.equipment)
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

    companion object {
        /**
         * The damage interval.
         */
        private const val DAMAGE_INTERVAL = 20
    }
}