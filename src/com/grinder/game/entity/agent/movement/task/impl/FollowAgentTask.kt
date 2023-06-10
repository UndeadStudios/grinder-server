package com.grinder.game.entity.agent.movement.task.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.movement.Motion.MAX_TARGET_FOLLOW_DISTANCE
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator
import com.grinder.game.entity.agent.movement.pathfinding.PathFinder.find
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.task.MovementTask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority

class FollowAgentTask<T : Agent> @JvmOverloads constructor(
    actor: T,
    private val target: Agent,
    private val follow: Boolean,
    private val combat: Boolean = false,
    private val maxDistance: Int = MAX_TARGET_FOLLOW_DISTANCE
) : MovementTask<T>(Priority.HIGH, actor) {

    init {
        actor.motion.target = target
    }

    private var attempts = 0

    private fun cancelEarly(): Boolean {

        if (actor.getBoolean(Attribute.STALL_HITS, false))
            return true;

        if (actor.isShouldNoClip) {
            return true;
        }

        if (!actor.isAlive || !target.isRegistered || !actor.isRegistered || !target.isAlive)
            return true

        if (combat && !actor.combat.isInCombatWith(target))
            return true

        if (actor is NPC) {

            if (actor.motion.movementDisabled())
                return true

            val coordinator = actor.movementCoordinator

            if (coordinator.goalState == NPCMovementCoordinator.GoalState.RETREAT_HOME) {
                return true
            }

            if (!AreaManager.inMulti(target) && target.combat.isBeingAttacked && !target.combat.isBeingAttackedBy(actor)) {
                return true
            }
        }
        return false
    }

    private fun withinDistance(strategy: TargetStrategy): Boolean {
        if (combat) {
            return actor.combat.isWithinAttackDistance(target)
        }
        val distance = if (actor.combat.target == null) 0 else actor.combat.requiredAttackDistance()
        return actor.isWithinDistance(strategy.tile, distance) && !actor.isUnder(strategy.tile, strategy.width, strategy.height)
    }

    override fun sequence() {
        if (cancelEarly()) {
            actor.motion.resetTargetFollowing()
            stop()
            return
        }

        //target.ifPlayerWith(PlayerRights.DEVELOPER) { player: Player -> player.packetSender.sendGraphic(Graphic(436, GraphicHeight.LOW), actor.position) }

        if (!actor.motion.canMove()) {
            return
        }

        val distance = DistanceUtil.calculateDistance(actor, target)
        if (actor.position.z != target.position.z || distance >= maxDistance) {
            actor.motion.onTargetOutOfReach(target)
            stop()
            return
        }

        val strategy = when {
            target is Player && follow -> target.followTarget
            else -> target.interactTarget
        }

        if (withinDistance(strategy)) {
            actor.motion.clearSteps()
            return
        }

        if (!strategy.reached(actor) && recalculate()) {
            val useSmartPathfindingAlgorithm = actor is Player || (actor is Monster && actor.useSmartPathfinding()) || (actor is Boss && attempts++ > 3)
            val position = find(actor, strategy, useSmartPathfindingAlgorithm)
            if (position != null) {
                actor.ifPlayer { player: Player -> player.packetSender.sendMinimapFlag(position) }
               // target.ifPlayerWith(PlayerRights.DEVELOPER) { player: Player -> player.packetSender.sendGraphic(Graphic(437, GraphicHeight.LOW), position) }
            }
        }
    }

    private fun recalculate(): Boolean {
        return !(actor.isUnder(target) && actor.motion.isMoving)
    }

    override fun stop() {
        super.stop()
    }
}