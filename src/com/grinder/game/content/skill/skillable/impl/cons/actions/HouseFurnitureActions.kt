package com.grinder.game.content.skill.skillable.impl.cons.actions

import com.grinder.game.content.skill.skillable.impl.cons.Construction
import com.grinder.game.content.skill.skillable.impl.cons.HouseFurnitureType
import com.grinder.game.content.skill.skillable.impl.cons.seating.impl.Chair
import com.grinder.game.content.skill.skillable.impl.cons.seating.impl.DiningBench
import com.grinder.game.model.ObjectActions

/**
 * @author  Simplex
 * @since  Apr 10, 2020
 */
object HouseFurnitureActions {

    /*
     * Some furniture are being reused from construction, clicks handled elsewhere
     */
    private val SKIP_FURNITURE = arrayOf(HouseFurnitureType.ALTAR_OF_THE_OCCULT)

    init {
        handleFurnitureObjectActions()
    }

    private fun handleFurnitureObjectActions() {
        HouseFurnitureType.values().filter{ f -> !SKIP_FURNITURE.contains(f)}.forEach { it: HouseFurnitureType ->
            ObjectActions.onClick(it.furnitureId) { clickAction: ObjectActions.ClickAction ->
                val x = clickAction.objectActionMessage.x
                val y = clickAction.objectActionMessage.y
                val id = clickAction.objectActionMessage.objectId
                val obj = clickAction.objectActionMessage.gameObject
                val type = clickAction.type
                val p = clickAction.player

                p.sendMessage("Face " + clickAction.objectActionMessage.gameObject.face)
                p.sendMessage("Type " + clickAction.objectActionMessage.gameObject.objectType)

                if(type == ObjectActions.ClickAction.Type.FIFTH_OPTION) {
                    Construction.handleRemoveClick(p, x, y, id)
                    return@onClick true
                }

                val possibleRoom = Construction.getHouseRoomAt(p, p.position)

                if(possibleRoom.isEmpty) {
                    println("Error: cannot find house room for ${p.username} at ${p.position}")
                    return@onClick true
                }

                // will be referenced statically once furniture is overhauled
                val chair = Chair.values().find { it.chairId == id }
                if(chair != null) {
                    p.sendMessage("Sit on " + obj.id)
                    chair.sit(p, obj)
                    return@onClick true
                }

                // will be referenced statically once furniture is overhauled
                val bench = DiningBench.values().find { it.benchId == id }
                if(bench != null) {
                    p.sendMessage("Sit on " + obj.id)
                    bench.sit(p, obj)
                    return@onClick true
                }

                //var houseRoom = possibleRoom.get()

                when (id) {
                    13503 -> {
                        Construction.exitDungeon(p)
                    }

                    13497, 13499, 13501, 13505 -> p.packetSender.sendMessage("stair climb")

                    4529 ->  // enter dungeon
                        Construction.enterDungeon(p)
                    else -> {}
                }
                return@onClick true
            }
        }
    }
}