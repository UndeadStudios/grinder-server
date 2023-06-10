package com.grinder.game.content.skill.skillable.impl.cons.actions

import com.grinder.game.model.ObjectActions
import com.grinder.game.model.ObjectActions.onClick
import com.grinder.util.ObjectID


/**
 * @author  Simplex
 * @since  Apr 05, 2020
 */
object HouseGardenActions {

    init {

        // Rimmington portal click
        onClick(ObjectID.OAK_TREE) { clickAction: ObjectActions.ClickAction ->
            val player = clickAction.player
            val message = clickAction.objectActionMessage

            true
        }
    }

}