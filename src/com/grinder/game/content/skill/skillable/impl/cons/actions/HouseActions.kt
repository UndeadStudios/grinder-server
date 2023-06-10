package com.grinder.game.content.skill.skillable.impl.cons.actions

import com.grinder.game.content.skill.skillable.impl.cons.Construction
import com.grinder.game.content.skill.skillable.impl.cons.HouseFurnitureType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.ObjectActions.onClick
import com.grinder.game.model.areas.instanced.HouseInstance
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.ObjectID
import java.util.function.Consumer


/**
 * Generic player-owned-house actions should go here.
 *
 * @author  Simplex
 * @since  Mar 27, 2020
 */
object HouseActions {

    private fun joinFriend(player: Player) {
        player.requestInput(String::class.java,
                "Enter a friend's name:") {
            friendsNameInput: String? -> Construction.enterFriendsHouse(player, friendsNameInput)
        }
    }

    init {
        // Rimmington portal click

        onClick(ObjectID.PORTAL_51) { clickAction: ObjectActions.ClickAction ->
            val player = clickAction.player
            //val message = clickAction.objectActionMessage

            val type = clickAction.type

            player.pohPortalReturnPosition = player.position.copy()

            val enterHouse: Consumer<Player> =
                    Consumer {Construction.updateHouseInstance(player, null, false); }
            val enterHouseBuildingmode: Consumer<Player> =
                    Consumer {
                        Construction.updateHouseInstance(player, null, true)
                    }
            val enterFriendsHouse: Consumer<Player> =
                    Consumer {joinFriend(player)}

            when (type) {
                // "Enter portal"
                ObjectActions.ClickAction.Type.FIRST_OPTION ->
                    DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                            .firstOption("Go to your house", enterHouse)
                            .secondOption("Go to your house (building mode)", enterHouseBuildingmode)
                            .thirdOption("<str=ff981f>Go to a friend's house</str>", enterFriendsHouse)
                            .addCancel()
                            .start(player)

                // "Home"
                ObjectActions.ClickAction.Type.SECOND_OPTION -> enterHouse.accept(player)

                // "Build mode"
                ObjectActions.ClickAction.Type.THIRD_OPTION -> enterHouseBuildingmode.accept(player)

                // "Friend's house"
                ObjectActions.ClickAction.Type.FOURTH_OPTION -> enterFriendsHouse.accept(player)

                else -> {}
            }


            true
        }

        // Exit portal click
        onClick(HouseFurnitureType.EXIT_PORTAL.furnitureId) { clickAction: ObjectActions.ClickAction ->
            val player = clickAction.player
            //val message = clickAction.objectActionMessage
            //val objectX = message.x
            //val objectY = message.y
            //val objectId = message.objectId

            // move back to portal return position
            if(player.area != null && player.area is HouseInstance && player.pohPortalReturnPosition != null) {
                player.moveTo(player.pohPortalReturnPosition)
            }
            true
        }
    }

}