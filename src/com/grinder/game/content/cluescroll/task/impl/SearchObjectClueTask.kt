package com.grinder.game.content.cluescroll.task.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.grinder.game.content.`object`.Chests
import com.grinder.game.content.`object`.SearchObjectActions
import com.grinder.game.content.cluescroll.ClueScroll
import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty
import com.grinder.game.content.cluescroll.task.ClueTask
import com.grinder.game.entity.`object`.name
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Coordinate
import com.grinder.game.model.ObjectActions
import java.util.*

/**
 * This task represents a [ClueTask] that includes searching and
 * interacting with a certain [GameObject] at the [coordinate].
 *
 * The [coordinate] is randomly picked from [possibleCoordinates]
 * by calling [randomize], this method is only effective at first time called during runtime.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
class SearchObjectClueTask(
    taskID: Int,
    clueScroll: ClueScroll?,
    private val clickOption: Int,
    private val isCopy: Boolean,
    private vararg val possibleCoordinates: Coordinate
) : ClueTask(taskID, clueScroll) {

    var coordinate: Coordinate? = null

    @Transient
    private var randomized = false

    override fun isPerformingTask(player: Player, vararg args: Any): Boolean {
        if (args.size < 3) return false
        val clickX = args[0] as Int
        val clickY = args[1] as Int
        val clickOption = args[2] as Int
        randomize()
        return clickOption == this.clickOption && clickX == coordinate!!.x && clickY == coordinate!!.y && player.position.z == coordinate!!.h
    }

    override fun preventDefault() = true

    override fun getNextTaskRewardType(difficulty: ScrollDifficulty) = difficulty.scrollID

    override fun toString(): String {
        return "FindObjectTask [" +
                "clickOption=$clickOption, " +
                "possibleCoordinates=" + possibleCoordinates.contentToString() + ", " +
                "currentCoordinate=" + coordinate + "]"
    }

    override fun randomize(): ClueTask {
        if (!randomized) {
            randomized = true
            coordinate = possibleCoordinates.random()
        }
        return this
    }

    override fun clone() = SearchObjectClueTask(taskID, clueScroll, clickOption, true, *possibleCoordinates)
        .setRequiredEquipments(*requiredEquipments?: emptyArray())
        .setAgent(clueTaskAgent)
        .randomize()


    override fun serialize(gson: Gson, jsonObject: JsonObject) {}
    override fun deserialize(jsonObject: JsonObject) {}
    override fun onComplete() {}

    init {
        if (!isCopy) {
            val positions = possibleCoordinates.map { it.toPosition() }.toTypedArray()
            ObjectActions.onClick(*positions) { clickAction: ObjectActions.ClickAction ->
                val player = clickAction.player
                val gameObject = clickAction.getObject()
                val clueScrollManager = player.clueScrollManager
                val clueTask = clueScrollManager.findTask(javaClass).orElse(null)
                if (clueTask is SearchObjectClueTask) {
                    val menuIndex = clickAction.type.ordinal + 1
                    val position = gameObject.position.clone()
                    SearchObjectActions.handle(player, gameObject) {
                        clueScrollManager.handleObjectAction(menuIndex, position)
                    }
                } else {
                    if (gameObject.name().contains("closed chest"))
                        Chests.handleClosedChest(clickAction)
                    else
                        SearchObjectActions.handle(player, gameObject, null)
                }
                true
            }
        }
    }
}