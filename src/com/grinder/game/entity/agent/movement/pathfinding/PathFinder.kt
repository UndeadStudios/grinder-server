package com.grinder.game.entity.agent.movement.pathfinding

import com.grinder.game.entity.Entity
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.movement.pathfinding.algorithm.AxisAlignment
import com.grinder.game.entity.agent.movement.pathfinding.algorithm.BreadthFirstSearch
import com.grinder.game.entity.agent.movement.pathfinding.algorithm.DirectDiagonalSearch
import com.grinder.game.entity.agent.movement.pathfinding.algorithm.PathAlgorithm
import com.grinder.game.entity.agent.movement.pathfinding.target.EntityTileTargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.target.PositionTargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.model.Position

object PathFinder {

    private val aa = AxisAlignment()
    private val dd = DirectDiagonalSearch()
    private val bfs = BreadthFirstSearch()

    @JvmOverloads
    fun find(source: Agent, tile: Position, smart: Boolean = true): Position? {
        val strategy = getStrategy(tile)
        return find(source, strategy, smart)
    }

    @JvmOverloads
    fun find(source: Agent, target: Entity, smart: Boolean = true): Position? {
        return find(source, getEntityStrategy(target), smart)
    }

    @JvmOverloads
    fun find(source: Agent, strategy: TargetStrategy, smart: Boolean = true): Position? {
        if (strategy.reached(source)) {
            source.motion.clearSteps()
            return source.position
        }
        source.motion.clearSteps()
        val algorithm = getAlgorithm(source, smart)
        return algorithm.find(source.position, source.width, source.height, source.motion, strategy, source.traversal, source.isShouldNoClip)
    }

    private fun getAlgorithm(source: Agent, smart: Boolean): PathAlgorithm {
        return when {
            smart -> bfs
            source is Player -> dd
            else -> aa
        }
    }

    @Throws(IllegalArgumentException::class)
    fun getStrategy(any: Any): TargetStrategy {
        return when (any) {
            is Position -> PositionTargetStrategy(any)
            is Entity -> getEntityStrategy(any)
            else -> throw IllegalArgumentException("No target strategy found for $any")
        }
    }

    private fun getEntityStrategy(entity: Entity): TargetStrategy {
        return when (entity) {
            is Agent -> entity.interactTarget
            is GameObject -> entity.interactTarget
            is ItemOnGround -> entity.interactTarget
            else -> EntityTileTargetStrategy(entity)
        }
    }
}