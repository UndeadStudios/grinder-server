package com.grinder.game.model.areas.instanced

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraInstance
import com.grinder.game.model.*
import com.grinder.game.model.areas.MapInstance
import com.grinder.game.model.areas.MapInstancedBossArea
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ObjectID

class HydraArea(mapInstance: MapInstance?)
    : MapInstancedBossArea(mapInstance) {

    companion object {

        init {
            ObjectActions.onClick(ObjectID.ALCHEMICAL_DOOR_1, ObjectID.ALCHEMICAL_DOOR_2) {
                val obj = it.getObject()
                val player = it.player
                if (obj.position.x == player.x) { // outsize
                    player.moveTo(player.position.transform(1, 0, 0))
                } else {
                    player.moveTo(player.position.transform(-2, 0, 0))
                }
                return@onClick true
            }

            ObjectActions.onClick(ObjectID.ALCHEMICAL_ROCK_CLIMB) {
                val player = it.player
                AlchemicalHydraInstance.climbRocks(player)

                return@onClick true
            }
        }
    }


    override fun isMulti(agent: Agent): Boolean {
        return false
    }

    override fun isSafeForHardcore(): Boolean {
        return true
    }

    override fun handleDeath(npc: NPC?): Boolean {
        val owner = npc?.owner
        val map = npc?.mapInstance
        TaskManager.submit(object : Task(1) {
            var tick = 0
            override fun execute() {
                tick++
                if (destroyed || !hasPlayers()) {
                    stop()
                    return
                }
                if (tick > 33) {
                    stop()
                    AlchemicalHydraInstance.start(owner, map)
                }
            }
        })
        return super.handleDeath(npc)
    }
}