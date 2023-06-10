package com.grinder.game.entity.updating.task

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.updating.UpdateTask

/**
 * An [UpdateTask] which does post-synchronization work for the specified [NPC].
 *
 * @author  Major
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   25/03/2020
 * @version 1.0
 *
 * @param npc The npc to do work for.
 */
class PostNpcUpdateTask(private val npc: NPC) : UpdateTask() {

    override fun run() {

        val oldTile = npc.lastPosition
        val moved = oldTile == null || !oldTile.sameAs(npc.position)

        if(moved)
            npc.lastPosition = npc.position.clone()

        npc.resetUpdateStates()
        npc.sequenceProperty().set(false)
    }

}