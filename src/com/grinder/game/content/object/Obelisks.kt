package com.grinder.game.content.`object`

import com.grinder.game.World
import com.grinder.game.entity.`object`.ClippedMapObjects
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.combat.isTeleblocked
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.block
import com.grinder.game.entity.agent.player.unblock
import com.grinder.game.entity.passedTimeGenericAction
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.TileGraphic
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.TimedObjectReplacementTask
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.Area
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Handles the obelisks which teleport players around in the Wilderness.
 *
 *
 * @author Swiffy
 */
object Obelisks {

    /**
     * Obelisk object ids
     */
    val OBELISK_IDS = intArrayOf(14829, 14830, 14827, 14828, 14826, 14831)

    /**
     * The obelisk [objects][GameObject].
     */
    val obelisks = arrayOfNulls<GameObject>(4)

    /**
     * Contains the states of the [obelisks].
     */
    private val OBELISK_ACTIVATED = BooleanArray(OBELISK_IDS.size)

    /**
     * Contains the [south east obelisk coordinates][Position] of the [obelisks].
     */
    private val OBELISK_COORDS = arrayOf(
            intArrayOf(3154, 3618),
            intArrayOf(3225, 3665),
            intArrayOf(3033, 3730),
            intArrayOf(3104, 3792),
            intArrayOf(2978, 3864),
            intArrayOf(3305, 3914))

    /**
     * Activates the Wilderness obelisks.
     *
     * @param objectId The object id
     * @return `true` if the object is an obelisk
     */
	@JvmStatic
	fun activate(player: Player, objectId: Int): Boolean {
        val index = indexOfObeliskForId(objectId)
        if (index >= 0) {
            if (player.passedTimeGenericAction() && !OBELISK_ACTIVATED[index]) {

                player.packetSender.sendAreaPlayerSound(204)
                OBELISK_ACTIVATED[index] = true
                TaskManager.submit(object : Task(1, true) {
                    var tick = 0
                    override fun execute() {

                        val obeliskArea = getObeliskArea(index)
                        val players = Stream
                                .concat(player.localPlayers.stream(), Stream.of(player))
                                .filter { obeliskArea.contains(it) }
                                .collect(Collectors.toSet())

                        tick++
                        when (tick) {
                            1 -> {
                                for (pos in getObeliskPositions(index)) {
                                    ClippedMapObjects.findObject(OBELISK_IDS[index], pos).ifPresent {
                                        TaskManager.submit(
                                                TimedObjectReplacementTask(
                                                        it,
                                                        DynamicGameObject.createPublic(14825, pos, it.objectType, it.face), 9))
                                    }
                                }
                            }
                            8 -> {
                                obeliskArea.findPositions(0)
                                        .forEach { World.spawn(TileGraphic(it, Graphic(342))) }

                                if (players.isEmpty()){
                                    stop()
                                    return
                                }

                                players.forEach {
                                    if (!it.combat.isTeleblocked(message = true)) {
                                        it.block(blockDisconnect = true, blockMovement = true)
                                        it.performAnimation(Animation(3945))
                                        it.setTeleporting(TeleportType.NORMAL)
                                    }
                                }
                            }
                            10 -> {
                                var random = Misc.getRandomInclusive(5)
                                while (random == index)
                                    random = Misc.getRandomInclusive(5)

                                val randomArea = getObeliskArea(random)

                                players.forEach {
                                    it.unblock(true, unblockMovement = true)
                                        if (it.isTeleporting) {
                                            it.setTeleporting(null)
                                            it.moveTo(randomArea.randomPosition)
                                            it.sendMessage("Ancient magic teleports you somewhere in the Wilderness.")
                                            it.performAnimation(Animation.DEFAULT_RESET_ANIMATION)
                                        }
                                }
                                stop()
                            }
                        }
                    }

                    override fun stop() {
                        setEventRunning(false)
                        OBELISK_ACTIVATED[index] = false
                    }
                })
            }
            return true
        }
        return false
    }

    fun getObeliskArea(index: Int): Area {
        val southEastX = OBELISK_COORDS[index][0]+1
        val southEastY = OBELISK_COORDS[index][1]+1
        return Area.of(southEastX, southEastY, southEastX+2, southEastY+2)
    }

    fun getObeliskPositions(index: Int): Array<Position> {
        return arrayOf(
                Position(OBELISK_COORDS[index][0], OBELISK_COORDS[index][1]),
                Position(OBELISK_COORDS[index][0] + 4, OBELISK_COORDS[index][1]),
                Position(OBELISK_COORDS[index][0], OBELISK_COORDS[index][1] + 4),
                Position(OBELISK_COORDS[index][0] + 4, OBELISK_COORDS[index][1] + 4)
        )
    }

    /**
     * Gets the array index for an obelisk
     */
    private fun indexOfObeliskForId(id: Int): Int {
        for (j in OBELISK_IDS.indices) {
            if (OBELISK_IDS[j] == id) return j
        }
        return -1
    }
}