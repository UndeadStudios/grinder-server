package com.grinder.game.content.`object`.climbing

import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.util.ObjectID
import kotlin.math.abs

/**
 * Utility class for [ClimbObjectActions].
 *
 * @author ?
 * @since 19/01/2021
 */
internal object ClimbObjectUtil {

    @JvmStatic
    fun ignore(objectDefinition: ObjectDefinition): Boolean {
        val id = objectDefinition.id
        return id == ObjectID.STAIRS_122
                || id == ObjectID.STAIRS_123
                || id == ObjectID.STAIRS_124
                || id == ObjectID.STAIRS_125
                || id == ObjectID.STAIRCASE_105
                || id == ObjectID.STAIRCASE_66
                || id == ObjectID.STAIRS_126
                || id == ObjectID.STAIRCASE_90
                || id == ObjectID.STAIRCASE_97
                || id == ObjectID.STAIRS_37
                || id == ObjectID.TIGHTROPE_9
                || id == ObjectID.KBD_LADDER_UP
                || id == ObjectID.LADDER_37
                || id == ObjectID.KBD_LADDER_DOWN
                || id == ObjectID.STAIRCASE_3
                || id == ObjectID.LADDER_188
                || id == ObjectID.STAIRCASE
                || id == ObjectID.VINES_5
                || id == ObjectID.LADDER_169
                || id == ObjectID.ROPE_17
                || id == ObjectID.LADDER_29
                || id == ObjectID.STAIRCASE_141
                || id == ObjectID.LADDER_135
                || id == ObjectID.STAIRCASE_142
                || id == ObjectID.ESCAPE_ROPE
                || id == ObjectID.LADDER_196
    }
}