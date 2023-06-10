package com.grinder.game.entity.updating.block

import com.google.gson.annotations.Expose
import com.grinder.game.entity.updating.UpdateBlock
import com.grinder.game.model.ForceMovement
import com.grinder.game.model.Position

/**
 * Represents an [UpdateBlock] for [ForceMovement] actions.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 *
 * @param startTick     the amount of client cycles to wait before starting the movement.
 * @param endTick       the amount of client cycles after which this movement should end.
 */
data class ForceMovementBlock(
        @Expose val startPosition: Position,
        @Expose val endPosition: Position,
        @Expose val startTick: Int,
        @Expose val endTick: Int,
        @Expose val animation: Int,
        @Expose val direction: Int
) : UpdateBlock()
