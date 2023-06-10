package com.grinder.game.content.skill.skillable.impl.cons.seating.impl

import com.grinder.game.content.skill.skillable.impl.cons.Construction
import com.grinder.game.content.skill.skillable.impl.cons.ConstructionUtils
import com.grinder.game.content.skill.skillable.impl.cons.seating.Seat
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.`object`.ClippedMapObjects
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.ForceMovement
import com.grinder.game.model.Position
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.ForceMovementTask
import java.util.*

/**
 * @author Simplex
 * @since Apr 08, 2020
 */
enum class Chair
constructor(val chairId: Int,
                          val idle: Int,
                          val eat: Int,
                          val idleDiagonal: Int = -1,
                          val eatDiagonal: Int = -1) : Seat {
    CRUDE_WOODEN_CHAIR(6752, 4073, 4141, 4074, 4142),
    WOODEN_CHAIR(6753, 4075, 4143, 4076, 4144),
    ROCKING_CHAIR(6754, 4079, 4145, 4080, 4146),
    OAK_CHAIR(6755, 4081, 4147, 4082, 4148),
    OAK_ARMCHAIR(6756, 4083, 4149, 4084, 4150),
    TEAK_ARMCHAIR(6757, 4085, 4151, 4086, 4152),
    MAHOGANY_ARMCHAIR(6758, 4087, 4154, 4088, 4153),
    WOODEN_STOOL(6806, 4107, 7059),  // stools cant be type 11
    OAK_STOOL(6807, 4108, 7087),
    CARVED_TEAK_BENCH_2(13694, 4097, 4098),  // Throne room ones
    MAHOGANY_BENCH_2(13695, 4099, 4100),
    GILDED_BENCH_2(13696, 4101, 4102),
    OAK_THRONE(13665, 4111, 7088),
    TEAK_THRONE(13666, 4112, 7089),
    MAHOGANY_THRONE(13667, 4113, 7090),
    GILDED_THRONE(13668, 4114, 7091),
    SKELETON_THRONE(13669, 4115, 7092),
    CRYSTAL_THRONE(13670, 4116, 7093),
    DEMONIC_THRONE(13671, 4117, 7094),
    TEAK_GARDEN_BENCH_RIGHT(29270, 7285, 7286),
    TEAK_GARDEN_BENCH_LEFT(29271, 7287, 7288),
    GNOME_GARDEN_BENCH_RIGHT(29272, 7289, 7290),
    GNOME_GARDEN_BENCH_LEFT(29273, 7291, 7292);

    fun sit(player: Player, obj: GameObject) {
        val diagonal = obj.objectType == 11
        val sitAnim = if (diagonal) ConstructionUtils.SIT_DIAGONAL.id else ConstructionUtils.SIT.id;
        var chunkPos = Construction.getCurrentChunk(player);
        val x = Construction.getPlayerChunkX(chunkPos, player)
        val y = Construction.getPlayerChunkY(chunkPos, player)

        player.seat = this
        player.setBas(if(diagonal) idleDiagonal else idle);
        player.updateAppearance()
        TaskManager.submit(object : Task(1, player, true) {
            var cycle = 0
            var oldPos = player.position.clone()
            override fun execute() {
                when (cycle++) {
                    0 -> {
                        TaskManager.submit(
                                ForceMovementTask(player, 1,
                                        ForceMovement(player.position.copy(),
                                                obj.position.getDelta(player.position),
                                                25,
                                                0,
                                                obj.getDirection(player.position).forceMovementMask,
                                                sitAnim)))
                    }
                    1 -> {
                        //player.packetSender.sendObject(ObjectFactory.produce(ConstructionUtils.OCCUPIED_SEAT, obj.position));
                        player.BLOCK_ALL_BUT_TALKING = false
                        stop()
                    }
                }
            }
        })
    }

    override fun stand(player: Player) {
        val mapObj: Optional<GameObject> = ClippedMapObjects
                .findObject(ConstructionUtils.OCCUPIED_SEAT, player.position)
        if (mapObj.isEmpty) {
            player.performAnimation(Animation.DEFAULT_RESET_ANIMATION)
            return
        }

        val obj = mapObj.get()
        val diagonal = obj.objectType == 11
        val dir = obj.facing

        player.seat = null
        player.forceAnimation(if (diagonal) ConstructionUtils.STAND_UP_DIAGONAL else ConstructionUtils.STAND_UP)
        var dest = Position(player.x + dir.x, player.y + dir.y);
        var sitAnim = if (diagonal) ConstructionUtils.SIT_DIAGONAL.id else ConstructionUtils.SIT.id;
        player.setForceMovement(ForceMovement(player.position, dest, 25, 5, dir.toInteger(), sitAnim))
        obj.id = chairId
    }

    override fun getEatAnimation(player: Player): Int {
        val obj: Optional<GameObject> = ClippedMapObjects
                .findObject(ConstructionUtils.OCCUPIED_SEAT, player.position)
        val diagonal = obj.get().objectType == 11
        return if (diagonal) eatDiagonal else eat
    }

    override fun restore(player: Player) {
        val seat: Optional<GameObject> = ClippedMapObjects
                .findObject(ConstructionUtils.OCCUPIED_SEAT, player.position)
        if (seat.isPresent) {
            seat.get().id = chairId
        }
    }

}