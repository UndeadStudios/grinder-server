package com.grinder.game.content.skill.skillable.impl.hunter

import com.google.gson.annotations.Expose
import com.grinder.game.World
import com.grinder.game.collision.CollisionManager
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Skill
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.NPCRespawnTask
import kotlin.random.Random

/**
 * Represents a trap that can be used through the Hunter skill.
 *
 * @apiNote The [Expose] annotation is used to indicate these variables should be serialised.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
class HunterTrap(
        player: Player,
        @Expose val toolType: HunterToolType
) {

    @Expose var obj: DynamicGameObject = DynamicGameObject.createPublic(toolType.waitingObjectId, player.position.clone(), 10, 0)
    @Expose var username: String = player.username
    @Expose var playerHunterLevel =  player.skillManager.getCurrentLevel(Skill.HUNTER)
    @Expose var timeOfPlacement: Long = 0
    @Expose var state: HunterTrapState = HunterTrapState.WAITING
    @Expose var caught: HunterCatchType? = null

    /**
     * Spawn the trap [obj] into the world,
     * and maybe schedule an update for all nearby players (1 tick delay).
     *
     * @param updateForPlayers indicate whether players should be notified of change.
     */
    fun spawn(updateForPlayers: Boolean) {
        ObjectManager.add(obj, false)
        if (state == HunterTrapState.SUCCESS)
            CollisionManager.addObjectClipping(obj)
        if(updateForPlayers) {
            TaskManager.submit(1) {
                ObjectManager.updateNearbyPlayers(obj, ObjectManager.OperationType.ADD)
            }
        }
    }

    /**
     * DeSpawn the trap [obj] out of the world,
     * and maybe schedule an update for all nearby players (1 tick delay).
     *
     * @param updateForPlayers indicate whether players should be notified of change.
     */
    fun deSpawn(updateForPlayers: Boolean) {
        World.deSpawn(obj)
        if (state == HunterTrapState.SUCCESS)
            CollisionManager.removeObjectClipping(obj)
//        if(updateForPlayers) { //this doesn't work anymore
//            TaskManager.submit(1) {
//                ObjectManager.updateNearbyPlayers(obj, ObjectManager.OperationType.REMOVE)
//            }
//        }
    }

    /**
     * Removes the trap [obj] and reference from the world,
     * and schedules and update for all nearby players (1 tick delay).
     */
    fun remove() {
        HunterTraps.unregister(this)
        deSpawn(true)
    }

    /**
     * Register this trap and spawn into the world.
     */
    fun place(){
        spawn(true)
        HunterTraps.register(this)
        timeOfPlacement = System.currentTimeMillis()
    }

    /**
     * Reset the trap to its waiting state and notify players.
     */
    fun reset() {

        val possibleTrap = ObjectManager.findDynamicObjectAt(obj.position)

        val targetTrap = if (possibleTrap.isPresent && possibleTrap.get().isGameObject) possibleTrap.get() else null

        ObjectManager.remove(if (targetTrap != null) targetTrap else obj, false)
        TaskManager.submit(1) {
            ObjectManager.updateNearbyPlayers(obj, ObjectManager.OperationType.REMOVE)
        }
        obj = DynamicGameObject.createPublic(toolType.waitingObjectId, obj.position.clone(), obj.objectType, obj.face)
        spawn(true)
        timeOfPlacement = System.currentTimeMillis()
        state = HunterTrapState.WAITING
    }

    /**
     * Sequence a [NPC] of type [HunterCatchType] and potentially catch it.
     *
     * @param catchType the [HunterCatchType] of the prey
     * @param prey      the [NPC] reference
     *
     * @return 'true' if caught,
     *          'false' if not caught.
     */
    fun sequencePray(catchType: HunterCatchType, prey: NPC) : Boolean {

        if(state != HunterTrapState.WAITING)
            return false

        if(!prey.position.sameAs(obj.position))
            return false

        val requiredLevel = catchType.requiredLevel

        if(requiredLevel > playerHunterLevel)
            return false

        state = if (Random.nextInt(requiredLevel) > Random.nextInt(playerHunterLevel + 8))
            HunterTrapState.FAILED
        else
            HunterTrapState.SUCCESS

        if (state == HunterTrapState.SUCCESS)
            prey.motion.update(MovementStatus.DISABLED)

        TaskManager.submit(1, 3) { tick ->

            if (!prey.isRegistered)
                return@submit

            when (tick) {
                0 -> catchType.investigate?.let { prey.performAnimation(it) }
                1 -> {
                    if (state == HunterTrapState.SUCCESS) {
                        caught = catchType
                        catchType.caught?.let { prey.performAnimation(it) }
                        TaskManager.submit(1) {
                            World.npcRemoveQueue.add(prey)
                            TaskManager.submit(NPCRespawnTask(prey, 100))
                        }
                    } else {
                        catchType.escaped?.let { prey.performAnimation(it) }
                    }
                }
                2 -> {
                    ObjectManager.remove(obj, true)
                    val id = if (state == HunterTrapState.SUCCESS)
                        toolType.successObjectId
                    else
                        toolType.failedObjectId
                    obj = DynamicGameObject.createPublic(id, obj.position.clone(), obj.objectType, obj.face)
                    ObjectManager.add(obj, true)

                    if (state == HunterTrapState.SUCCESS)
                        CollisionManager.addObjectClipping(obj)
                }
            }
        }
        return true
    }

}