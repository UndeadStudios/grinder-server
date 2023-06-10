package com.grinder.game.entity.updating.block

import com.google.gson.annotations.Expose
import com.grinder.game.entity.updating.UpdateBlock
import com.grinder.game.model.Animation

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 */
data class AnimationBlock(@Expose val animation: Animation) : UpdateBlock()
