package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.Area
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import java.util.*
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
abstract class GodChamberArea<G : God>(vararg boundaries:  Boundary) : Area(*boundaries) {

    abstract val god: G
    abstract val minions: Array<GodMinion<G>>

    init {
        TaskManager.submit(object : Task(2, false) {
            override fun execute() {
                stop()
                god.spawn()
                god.onEvent {
                    if (it == MonsterEvents.ADDED) {

                        god.regenerateFullHealth()
                        god.minionsAlive = 3

                        minions.forEach { minion ->

                            minion.regenerateFullHealth()

                            if (!minion.isRegistered) {
                                TaskManager.cancelTasks(minion)
                                minion.spawn()
                            }
                        }
                    }
                }
                minions.forEach { minion ->
                    minion.onEvent {
                        if (it == MonsterEvents.REMOVED) {
                            god.minionsAlive--
                        }
                    }
                }
            }
        })
    }

    override fun enter(agent: Agent?) {
        AreaManager.GOD_WARS_AREA.enter(agent)
        if (agent is Player) {
            super.enter(agent)
        }
        if (agent is Player)
            attackMaybe(agent)
    }

    override fun leave(agent: Agent?) {
        if (agent is Player) {
            super.leave(agent)
            if (AreaManager.getPlayersInArena(this) == 0) {
                restoreFullHealth(god, *minions)
                resetMovement(god, *minions)
            }
            resetAttackWith(agent, god, *minions)
        }
        AreaManager.GOD_WARS_AREA.leave(agent)
    }

    private fun attackMaybe(agent: Agent?) {
        if (agent is Player) {
            if (god.isRegistered) {
                god.combat.let {
                    if (!it.isAttacking) {
                        it.initiateCombat(agent)
                    }
                }
                for (minion in minions) {
                    minion.combat.let {
                        if (!it.isAttacking || Random.nextBoolean()) {
                            it.initiateCombat(agent)
                        }
                    }
                }
            }
        }
    }

    private fun resetMovement(vararg npcs: NPC) {
        npcs.forEach {
            it.movementCoordinator.retreatHome()
        }
    }

    private fun resetAttackWith(agent: Agent?, vararg npcs: NPC){
        npcs.forEach {
            it.combat.resetCombatWith(agent)
        }
    }

    private fun restoreFullHealth(vararg npcs: NPC){
        npcs.forEach { it.regenerateFullHealth() }
    }

    override fun process(agent: Agent?) {
        AreaManager.GOD_WARS_AREA.process(agent)
    }
    override fun defeated(player: Player?, agent: Agent?) {
        AreaManager.GOD_WARS_AREA.defeated(player, agent)
    }
    override fun onPlayerRightClick(player: Player?, rightClicked: Player?, option: Int) {}
    override fun isMulti(agent: Agent?) = true
    override fun canTeleport(player: Player?) = true
    override fun canAttack(attacker: Agent?, target: Agent?) = true
    override fun canTrade(player: Player?, target: Player?) = true
    override fun canDrink(player: Player?, itemId: Int) = true
    override fun canEat(player: Player?, itemId: Int) = true
    override fun dropItemsOnDeath(player: Player?, killer: Optional<Player>?) = true
    override fun handleObjectClick(player: Player?, obj: GameObject, type: Int) = false
    override fun handleDeath(player: Player?, killer: Optional<Player>?) = false
    override fun handleDeath(npc: NPC?) = false

    override fun isCannonProhibited() = true

}