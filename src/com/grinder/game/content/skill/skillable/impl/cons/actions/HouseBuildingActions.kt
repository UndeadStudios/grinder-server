package com.grinder.game.content.skill.skillable.impl.cons.actions

import com.grinder.game.content.skill.skillable.impl.cons.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.areas.instanced.HouseInstance
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import java.util.*
import java.util.function.Consumer

/**
 * @author Simplex
 * @since Mar 27, 2020
 */
object HouseBuildingActions {

    private const val FURN_BUILDER_INTERFACE = 138274

    init {
        handleDoorActions()
        handleHotspotObjectActions()
        handleRoomBuildingInterfaceButtons()
        handleFurnitureBuilderInterfaceActions()
    }

    private fun handleFurnitureBuilderInterfaceActions() {

        HouseFurnitureType.values().forEach { hs ->

            ItemActions.onContainerClick(hs.itemId) {
                if (getInterfaceId() == FURN_BUILDER_INTERFACE && buildingChecks(player)) {
                    Construction.buildFurnitureFromInterface(player, getItemId(), getSlot())
                    return@onContainerClick true
                }
                return@onContainerClick false
            }
        }
    }

    private fun handleHotspotObjectActions() {
        HotSpotType.values().forEach {
            ObjectActions.onClick(it.objectId) { clickAction: ObjectActions.ClickAction ->
                val player = clickAction.player
                val message = clickAction.objectActionMessage
                val objectX = message.x
                val objectY = message.y
                val objectId = message.objectId
                //val type = clickAction.type
                //val myTiles = Construction.getCurrentChunk(player)

                //var houseRoom: HouseRoom = player.currentRoomSet[0][myTiles!![0] - 1][myTiles[1] - 1]

                player.house.buildFurnitureX = objectX
                player.house.buildFurnitureY = objectY
                player.house.setBuildFuritureId(objectId)

                val possibleHotspot = Construction.verifyHotspot(player, objectId)

                if(possibleHotspot.isEmpty) {
                    println("[Con] Error: Could not find hotspot with given object Id: $objectId for plr: " + player.username)
                    return@onClick true
                }

                val hotspot = possibleHotspot.get()

                val furniture = HouseFurnitureType.getForHotSpotId(hotspot.hotSpotId)

                if (furniture == null) {
                    println("[Con] Error: Could not find furniture with given hotspot Id: ${hotspot.hotSpotId} for plr: " + player.username)
                    return@onClick true
                }

                Construction.sendFurnitureBuilderItemSet(furniture, player)
                Construction.sendFurnitureBuilderCrosses(furniture, player, hotspot)
                player.packetSender.sendInterface(138272)

                return@onClick true
            }
        }


    }

    /**
     * TODO (Unimplemented)
     */
    /*private fun placeRoomDialogue(player: Player) {
        val clockwiseRotateConsumer: Consumer<Player> =
                Consumer {Construction.rotateRoom(player, true) }
        val antiClockwiseRotateConsumer: Consumer<Player> =
                Consumer {
                    Construction.rotateRoom(player, false)
                }
        val buildConsumer: Consumer<Player> =
                Consumer {/* TODO: Implement */}

        DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                .firstOption("Rotate clockwise", clockwiseRotateConsumer)
                .secondOption("Rotate anticlockwise", antiClockwiseRotateConsumer)
                .thirdOption("Build", buildConsumer)
                .addCancel()
                .start(player)
    }*/

    private fun handleDoorActions() {
        ObjectActions.onClick(*ConstructionUtils.DOORSPACEIDS) { clickAction: ObjectActions.ClickAction ->
            val player = clickAction.player
            val roomType = Construction.checkForRoom(player)

            if (roomType.isEmpty) {
                player.packetSender.sendInterface(128643)
            } else {
                val rotateRoomConsumer: Consumer<Player> =
                        Consumer {Construction.rotateRoom(player, true) }
                val deleteRoomConsumer: Consumer<Player> =
                        Consumer {
                            Construction.deleteRoom(player)
                        }
                val roomTypeName = HouseRoomType.values()[roomType.get().type].toString()

                DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Rotate the $roomTypeName", rotateRoomConsumer)
                        .secondOption("Delete the $roomTypeName", deleteRoomConsumer)
                        .addCancel()
                        .start(player)
            }

            true
        }
    }

    private fun handleRoomBuildingInterfaceButtons() {
        // close button on room builder
        ButtonActions.onClick(138323) {
            player.removeInterfaces()
        }

        // room creation buttons on room buildSer interface
        Arrays.stream(HouseRoomType.values()).forEach { room ->
            run {
                ButtonActions.onClick(room.roomBuilderButton) {

                    if (!buildingChecks(player))
                        return@onClick

                    Construction.createRoom(room.id, player, player.z)
                }
            }
        }
    }

    fun buildingChecks(player: Player): Boolean {
        if (player.area == null) {
            if (player.rights == PlayerRights.DEVELOPER) {
                player.sendMessage("Construction Area is null!")
            }
            return false
        }
        if ((player.area as HouseInstance).houseOwner !== player) {
            player.sendMessage("Only the house owner may do that.")
            return false
        }
        if (!player.inBuildingMode()) {
            player.sendMessage("You must be in building mode to do that.")
            return false
        }
        return true
    }

}