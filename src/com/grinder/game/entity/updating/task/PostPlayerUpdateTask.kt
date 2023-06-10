package com.grinder.game.entity.updating.task

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.updating.UpdateTask
import com.grinder.game.message.impl.ClearRegionMessage
import com.grinder.game.message.impl.GroupedRegionUpdateMessage
import com.grinder.game.message.impl.RegionUpdateMessage
import com.grinder.game.model.Position
import com.grinder.game.model.area.Region
import com.grinder.game.model.area.RegionCoordinates
import com.grinder.game.model.area.RegionRepository
import java.util.*

/**
 * An [UpdateTask] which does post-synchronization work for the specified [Player].
 *
 * @author  Graham
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   25/03/2020
 * @version 1.0
 *
 * @param player The player to do work for.
 */
class PostPlayerUpdateTask(private val player: Player,
                           /**
                            * The Map of RegionCoordinates to Sets of RegionUpdateMessages, which contain the updates for a Region a Player
                            * can already view.
                            */
                           private val updates: MutableMap<RegionCoordinates, List<RegionUpdateMessage>>)
    : UpdateTask() {

    companion object {
        @JvmStatic
        fun clearFloors(player: Player) {
            for (i in 0 until Position.HEIGHT_LEVELS) {
                player.resetRegions[i].clear()
                player.LOADED_FLOOR[i] = false
            }
        }
    }

    override fun run() {

        if (player.hasRegionChanged()) {
            val oldChunks: MutableSet<RegionCoordinates> = HashSet(player.renderedRegions)

            //Create a list of rendered regions
            val repository = World.regions
            val rendered = repository.fromPosition(player.lastKnownRegion).renderedRegionsFromCenter
            player.renderedRegions = rendered
            Region.removePlayerFromChunks(player, oldChunks)
            Region.addPlayerToRegionChunks(player, rendered)
            clearFloors(player)

            //Ignore old chunks from redrawing on the scene.
            val renderChunks: MutableSet<RegionCoordinates> = HashSet(rendered)
            renderChunks.removeAll(oldChunks)
            reloadItemsAndObjects(player, renderChunks)

        } else { //player moved up/down stairs
            if (!player.LOADED_FLOOR[player.plane and 3]) {
                reloadItemsAndObjects(player, player.renderedRegions)
            }
        }

        sendUpdates(player, player.updateRegionChunks, player.resetRegions[player.plane and 3])
        player.updateRegionChunks.clear()

        player.resetUpdateStates()
        player.setRegionChanged(false)
        //player.updateColorfulItem = false

        player.session.flush()

        player.localNpcs.forEach { it.sequenceProperty().set(true) }
    }

    private fun sendUpdates(player: Player, full: Set<RegionCoordinates>, resetChunk: MutableSet<RegionCoordinates>) {
        val repository = World.regions
        val height = player.plane and 3
        val position = player.lastKnownRegion
        for (coordinates in full) {
            val updateMsgs = updates.computeIfAbsent(coordinates)
            {  repository[it].getUpdates(height) }

            if (updateMsgs.isNotEmpty()) {
                player.send(GroupedRegionUpdateMessage(position, coordinates, updateMsgs))
            }
        }

        //reload items and objects in this chunk
        for (coordinates in resetChunk) {
            player.send(ClearRegionMessage(position, coordinates))
            val updateMsgs = encode(coordinates, repository, height, player)
            //encodes.computeIfAbsent(coordinates) {global} // removed because unused
            if (updateMsgs.isNotEmpty()) {
                player.send(GroupedRegionUpdateMessage(position, coordinates, updateMsgs))
            }
        }
        resetChunk.clear()
    }

    private fun reloadItemsAndObjects(player: Player, list: Set<RegionCoordinates>) {
        val repository = World.regions
        val height = player.plane and 3
        player.LOADED_FLOOR[height] = true;
        val position = player.lastKnownRegion
        for (coordinates in list) {
            // add to the updateMsgs, for player only stuff
            val updateMsgs = encode(coordinates, repository, height, player)
            if (updateMsgs.isNotEmpty()) {
                player.send(GroupedRegionUpdateMessage(position, coordinates, updateMsgs))
            }
        }
    }

    private fun encode(coordinates: RegionCoordinates,
                       repository: RegionRepository,
                       height: Int,
                       player: Player) =
            repository[coordinates].encode(height, player)
}