package com.grinder.game.model.areas.instanced

import com.grinder.game.World
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.areas.InstanceManager
import com.grinder.game.model.areas.MapBuilder
import com.grinder.game.model.areas.MapInstance
import com.grinder.game.model.areas.MapInstancedBossArea
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.ForceMovementTask
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import java.util.concurrent.TimeUnit

class VorkathArea(mapInstance: MapInstance?)
    : MapInstancedBossArea(mapInstance) {

    override fun isSafeForHardcore() = false
    override fun handleObjectClick(player: Player?, obj: GameObject?, actionType: Int) = false

    companion object {

        val fightBounds = Boundary(85, 107, 86, 108)

        init {
            NPCActions.onClick(NpcID.VORKATH_8059) {
                val npc = it.npc
                if(npc is VorkathBoss){
                    if(npc.owner == it.player){
                        npc.wakeUp()
                    }
                }
                return@onClick true
            }

            ObjectActions.onClick(ObjectID.ICE_CHUNKS_7) {
                val player = it.player
                val obj = it.getObject()
                handleIceChunksEntrance(player, obj);

                return@onClick true
            }
        }

        fun spawnBoss(map: MapInstance, player: Player) {
            var basePos = map.basePosition;
            val vorkathPos = basePos.transform(29+64, 30+64, 0)
            val vorkathBoss = VorkathBoss(vorkathPos)

            vorkathBoss.positionToFace = vorkathPos.transform(-3, -5, 0)
            vorkathBoss.motion.update(MovementStatus.DISABLED)
            vorkathBoss.owner = player
            vorkathBoss.spawn()
//            val vArea = map.area as VorkathArea
//            vArea.add(vorkathBoss)
        }

        val MAP_BASE = Position(2176, 3968)

        @JvmStatic
        fun handleIceChunksEntrance(player: Player, gameObject: GameObject) {
            if (player.busy()) return

            if (gameObject.y > player.y) { //entering boss instance

                val mapInstance = InstanceManager.getOrCreate(player, InstanceManager.SinglePlayerMapType.VORKATH)
                val base = mapInstance.basePosition

                if (!mapInstance.isMapBuilt) {
                    mapInstance.area = VorkathArea(mapInstance)
                    mapInstance.copyPlane(MAP_BASE.regionCoordinates, 0, 0, 0, 0, MapBuilder.ChunkSizes.TWENTY_FOUR, 0)
                }

                ForceMovementTask(player, 2,
                        ForceMovement(player.position.clone(), Position(0, 2), 0, 40, 0, 6132))

                TaskManager.submit(3) {
                    player.forceMovement = null
                    spawnBoss(mapInstance, player) //spawn boss first time only
                    player.moveTo(base.transform(32+64, 22+64, 0))
                }




            } else { //exiting boss instance
                ForceMovementTask(player, 2,
                        ForceMovement(player.position.clone(), Position(0, -2), 0, 40, 2, 6132))

                TaskManager.submit(3) {
                    player.forceMovement = null
                    player.moveTo(Position(2272, 4052))
                }
            }
        }
    }

    override fun isCannonProhibited() = true

    override fun handleDeath(npc: NPC?): Boolean {
        if (npc != null) {
            if (!destroyed && npc.owner != null && npc.owner.isActive)
                spawnBoss(npc.mapInstance, npc.owner)
        }
        return super.handleDeath(npc);
    }


}