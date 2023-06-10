package com.grinder.game.entity.updating.task

import com.grinder.game.World
import com.grinder.game.entity.EntityType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.updating.UpdateBlock
import com.grinder.game.entity.updating.UpdateSegment
import com.grinder.game.entity.updating.UpdateTask
import com.grinder.game.entity.updating.block.AppearanceBlock
import com.grinder.game.entity.updating.block.InteractingMobBlock
import com.grinder.game.entity.updating.block.TurnToPositionBlock
import com.grinder.game.entity.updating.seg.AddPlayerSegment
import com.grinder.game.entity.updating.seg.MovementSegment
import com.grinder.game.entity.updating.seg.RemoveMobSegment
import com.grinder.game.entity.updating.seg.TeleportSegment
import com.grinder.game.message.impl.PlayerSynchronizationMessage
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import kotlin.math.atan2

/**
 * An [UpdateTask] which synchronizes the specified [Player].
 *
 * @author  Graham
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   25/03/2020
 * @version 1.0
 *
 * @param player The player to sync players for.
 */
class PlayerUpdateTask(private val player: Player) : UpdateTask() {

    override fun run() {

        val lastKnownRegion = player.lastKnownRegion
        val regionChanged: Boolean = player.hasRegionChanged()
        val appearanceTickets = player.appearanceTickets
        var blockSet = player.blockSet

        val position = player.position

        val segment = if(player.hasPendingTeleportUpdate() || regionChanged)
            TeleportSegment(blockSet, position)
        else {
            MovementSegment(blockSet, player.directions)
        }

        val localPlayers = player.localPlayers
        val oldCount = localPlayers.size

        val segments = ArrayList<UpdateSegment>()

        val playerIterator = localPlayers.iterator()
        while (playerIterator.hasNext()) {
            val other = playerIterator.next()
            if (removeable(position, other)) {
                playerIterator.remove()
                segments.add(RemoveMobSegment())
            } else {
                segments.add(MovementSegment(other.blockSet, other.directions))
            }
        }

        var added = 0
        var count = localPlayers.size

        val currentRegion = World.regions.fromPosition(position)
        val viewableRegionCoordinates = currentRegion.surrounding

        viewableRegionCoordinates.add(currentRegion.coordinates)

        val viewablePlayers = viewableRegionCoordinates.map {
            World.regions.get(it)
        }.stream().flatMap {
            it.players.stream()
        }

        val iterator = viewablePlayers.iterator()
        while (iterator.hasNext()) {
            if (count >= MAXIMUM_LOCAL_PLAYERS) {
//                player.flagExcessivePlayers()
                break
            } else if (added >= NEW_PLAYERS_PER_CYCLE) {
                break
            }
            val other = iterator.next()
            val local = other.position
            if (other !== player
                    && local.isWithinDistance(position, DEFAULT_VIEWING_DISTANCE)
                    && !localPlayers.contains(other)) {
                localPlayers.add(other)
                count++
                added++
                blockSet = other.blockSet
                val index = other.index

                if (!blockSet.contains(AppearanceBlock::class.java)
                        && !hasCachedAppearance(appearanceTickets, index, other.appearanceTicket)) {
                    blockSet = blockSet.clone()
                    blockSet.add(UpdateBlock.createAppearanceBlock(other, other.getBoolean(Attribute.UPDATE_COLORFUL_ITEM)))
                }

                if(!blockSet.contains(TurnToPositionBlock::class.java) && !blockSet.contains(InteractingMobBlock::class.java)) {
                    blockSet = blockSet.clone()
                    when {
                        other.faceDegrees != 0 -> blockSet.add(UpdateBlock.createTurnToPositionBlock(other.faceDegrees))
                        other.interactingEntity != null -> blockSet.add(UpdateBlock.createInteractingMobBlock(other.interactingEntity))
                        other.motion.lastDirectionToFace != Direction.NONE -> {
                            val dst = other.position.clone().move(other.motion.lastDirectionToFace)
                            val srcX = other.position.x * 64
                            val srcY = other.position.y * 64
                            val dstX = dst.x * 64
                            val dstY = dst.y * 64
                            val degreesX = (srcX - dstX).toDouble()
                            val degreesY = (srcY - dstY).toDouble()
                            blockSet.add(UpdateBlock.createTurnToPositionBlock((atan2(degreesX, degreesY) * 325.949).toInt() and 0x7ff))
                        }
                    }
                }

                segments.add(AddPlayerSegment(blockSet, index, local))
            }
        }

        val message = PlayerSynchronizationMessage(player, lastKnownRegion, position,
                regionChanged, segment, oldCount, segments)

        player.send(message)
    }

    /**
     * Returns whether or not the specified [Player] should be removed.
     *
     * @param position The [Position] of the Player being updated.
     * @param other The Player being tested.
     * @return `true` iff the specified Player should be removed.
     */
    private fun removeable(position: Position, other: Player): Boolean {

        if (other.hasPendingTeleportUpdate() || !other.isActive)
            return true

        val otherPosition: Position = other.position
        return otherPosition.getLongestDelta(position) > DEFAULT_VIEWING_DISTANCE
                || !otherPosition.isWithinDistance(position, DEFAULT_VIEWING_DISTANCE)
    }

    /**
     * Tests whether or not the specified Player has a cached appearance within
     * the specified appearance ticket array.
     *
     * @param appearanceTickets The appearance tickets.
     * @param index The index of the Player.
     * @param appearanceTicket The current appearance ticket for the Player.
     * @return `true` if the specified Player has a cached appearance
     * otherwise `false`.
     */
    private fun hasCachedAppearance(appearanceTickets: IntArray, index: Int, appearanceTicket: Int): Boolean {
        if (appearanceTickets[index] != appearanceTicket) {
            appearanceTickets[index] = appearanceTicket
            return false
        }
        return true
    }

    companion object {

        /**
         * The default viewing distance, in tiles.
         */
        private const val DEFAULT_VIEWING_DISTANCE = 15

        /**
         * The maximum amount of local players.
         */
        private const val MAXIMUM_LOCAL_PLAYERS = 255

        /**
         * The maximum number of players to load per cycle. This prevents the update packet from becoming too large (the
         * client uses a 5000 byte buffer) and also stops old spec PCs from crashing when they login or teleport.
         */
        private const val NEW_PLAYERS_PER_CYCLE = 15

    }


}