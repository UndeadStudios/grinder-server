package com.grinder.game.entity.updating.task

import com.grinder.game.World
import com.grinder.game.entity.EntityType
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.updating.UpdateBlock
import com.grinder.game.entity.updating.UpdateSegment
import com.grinder.game.entity.updating.UpdateTask
import com.grinder.game.entity.updating.block.InteractingMobBlock
import com.grinder.game.entity.updating.block.TurnToPositionBlock
import com.grinder.game.entity.updating.seg.AddNpcSegment
import com.grinder.game.entity.updating.seg.MovementSegment
import com.grinder.game.entity.updating.seg.RemoveMobSegment
import com.grinder.game.message.impl.NpcSynchronizationMessage
import com.grinder.game.model.Position
import java.util.*

/**
 * An [UpdateTask] which synchronizes npcs with the specified [Player].
 *
 * @author  Major
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   26/03/2020
 * @version 1.0
 *
 * @param player The player to sync npcs for.
 */
class NpcUpdateTask(private val player: Player) : UpdateTask() {

    override fun run() {
        val locals= player.localNpcs
        val segments = ArrayList<UpdateSegment>()
        val originalCount = locals.size
        val playerPosition = player.position
        val lastKnownRegion = player.lastKnownRegion

        val iterator = locals.iterator()

        val viewingDistance = if (player.isLargeViewport)
            Player.LARGE_VIEW_DISTANCE
        else
            Player.NORMAL_VIEW_DISTANCE

        while (iterator.hasNext()) {
            val npc = iterator.next()
            val position = npc.position
            if (removeMob(npc, position, playerPosition, player.isLargeViewport)) {
                iterator.remove()
                segments.add(RemoveMobSegment())
            } else {
                segments.add(MovementSegment(npc.blockSet, npc.directions))
            }
        }

        var added = 0
        var count = locals.size

        val repository = World.regions
        val current = repository.fromPosition(playerPosition)

        val regions = current.surrounding
        regions.add(current.coordinates)

        val npcs = regions.stream().map(repository::get)
                .flatMap<NPC?> { it.npcs.stream() }

        for (npc in npcs){

            if (count >= MAXIMUM_LOCAL_NPCS || added >= NEW_NPCS_PER_CYCLE)
                break

            if(npc == null || !npc.visibilityProperty().get())
                continue

            val position = npc.position

            if (canViewNpc(playerPosition, npc.position, player.isLargeViewport) && !locals.contains(npc)) {
                locals.add(npc)
                count++
                added++

                var blockSet = npc.blockSet

                if(!blockSet.contains(TurnToPositionBlock::class.java) && !blockSet.contains(InteractingMobBlock::class.java)) {
                    blockSet = blockSet.clone()
                    if(npc.faceDegrees != 0){
                        blockSet.add(UpdateBlock.createTurnToPositionBlock(npc.faceDegrees))
                    } else if(npc.interactingEntity != null){
                        blockSet.add(UpdateBlock.createInteractingMobBlock(npc.interactingEntity))
                    }
                }
                segments.add(AddNpcSegment(blockSet, npc.index, position, if(npc.npcTransformationId != -1) npc.npcTransformationId else npc.id, npc.lastFacingDirection, player.isLargeViewport))
            }
        }

        val message = NpcSynchronizationMessage(player, lastKnownRegion, playerPosition, segments, originalCount, player.isLargeViewport)

        player.send(message)
    }

    private fun removeMob(npc: NPC, position: Position, playerPosition: Position, extendedView: Boolean) =
            !npc.isActive
                    || !npc.visibilityProperty().get()
                    || npc.hasPendingTeleportUpdate()
                    || !canViewNpc(playerPosition, npc.position, extendedView)

    companion object {


        /**
         * The maximum amount of local npcs.
         */
        private const val MAXIMUM_LOCAL_NPCS = 255

        /**
         * The maximum number of npcs to load per cycle. This prevents the update packet from becoming too large (the
         * client uses a 5000 byte buffer) and also stops old spec PCs from crashing when they login or teleport.
         */
        private const val NEW_NPCS_PER_CYCLE = 20

        private fun canViewNpc(playerPos: Position, npcPos: Position, extendedView: Boolean) : Boolean {
            if (playerPos.z != npcPos.z)
                return false
            return npcPos.isWithinDistance(playerPos, if(extendedView) Player.LARGE_VIEW_DISTANCE else Player.NORMAL_VIEW_DISTANCE)
        }
    }
}