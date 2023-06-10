package com.grinder.game.entity.updating.block

import com.google.gson.annotations.Expose
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.updating.UpdateBlock

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 */
data class HitSecondUpdateBlock(
        @Expose val damage: Damage,
        @Expose val currentHealth: Int,
        @Expose val maximumHealth: Int
) : UpdateBlock()