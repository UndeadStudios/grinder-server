package com.grinder.game.content.item

import com.grinder.game.entity.agent.combat.event.impl.WyvernIceEvent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.CombatActions
import com.grinder.util.oldgrinder.EquipSlot

/**
 *
 * @since 2019-03-15
 */
object ElementalShields {

    /**
     * The shields IDS
     */

    const val ELEMENTAL_SHIELD = 2890
    const val MIND_SHIELD = 9731

    init {
        CombatActions.onEvent(WyvernIceEvent::class) {
            ifActorIsPlayer {
                absorbDamage(it)
            }
        }
    }

    /**
     * Absorbing damage messages
     * The damage reduction is handled on isWearingWyvernBreathProtection
     */

    fun absorbDamage(target: Player) {

        val shield = target.equipment[EquipSlot.SHIELD]?:return

        if(shield.id == ELEMENTAL_SHIELD || shield.id == MIND_SHIELD)
            target.message("Your shield absorbs most of the wyvern's icy breath!")
    }
}