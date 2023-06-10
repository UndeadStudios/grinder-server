package com.grinder.game.content.skill.skillable.impl.cons.actions

import com.grinder.game.content.skill.skillable.impl.cons.ButlerType
import com.grinder.game.model.NPCActions
import com.grinder.game.model.NPCActions.onClick

/**
 * @author  Simplex
 * @since  Mar 27, 2020
 */
object HouseNPCActions {

    init {
        onClick(ButlerType.BUTLER.npcId) { clickAction: NPCActions.ClickAction ->
            val player = clickAction.player
            val npc = clickAction.npc
            true
        }
    }

}