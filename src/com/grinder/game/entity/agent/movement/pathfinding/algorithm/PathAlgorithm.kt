package com.grinder.game.entity.agent.movement.pathfinding.algorithm

import com.grinder.game.entity.agent.movement.StepQueue
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy
import com.grinder.game.model.Position

interface PathAlgorithm {
    /**
     * Calculates a route from [position] to [strategy.tile]
     * taking into account movement allowed by [traversal]
     * appending the individual steps to [movement.steps].
     * @return Final path position (complete or partial) or null if failure
     */
    fun find(
        position: Position,
        width: Int,
        height: Int,
        movement: StepQueue,
        strategy: TargetStrategy,
        traversal: TraversalStrategy,
        shouldNoClip: Boolean
    ): Position?
}