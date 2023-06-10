package com.grinder.game.entity.updating.sync

import com.grinder.game.entity.agent.AgentList
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.updating.UpdateTask
import com.grinder.game.entity.updating.task.*
import com.grinder.game.message.impl.RegionUpdateMessage
import com.grinder.game.model.area.RegionCoordinates
import com.grinder.util.benchmark.Monitors
import com.grinder.util.benchmark.SimpleBenchMarker
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 * A single-threaded [ClientSynchronizer] implementation.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/02/2020
 * @version 1.0
 */
class SequentialClientSynchronizer : ClientSynchronizer() {

    private val logger = LogManager.getLogger(SequentialClientSynchronizer::class.java)!!

    override fun synchronize(benchMarker: SimpleBenchMarker, players: AgentList<Player>, npcs: AgentList<NPC>) {

        //val encodes = HashMap<RegionCoordinates, Set<RegionUpdateMessage>>() // removed because unused
        val updates = HashMap<RegionCoordinates, List<RegionUpdateMessage>>()

        /*
        Here we cache the player's last position and process movement,
        alongside performing other content specific processes.
         */
        for(player in players){
            player ?: continue
            try {
                val task : UpdateTask = PrePlayerUpdateTask(player)
                task.run()
            } catch (t: Throwable){
                logger.error("player pre sync exception [$player], requesting logout for player", t)
                player.requestClientLogout()
            }
        }
        benchMarker.mark("players -> pre sync")

        /*
        Here we cache the NPC its movement and various other behaviour and state updating.
         */
        for(npc in npcs) {
            npc ?: continue
            try {
                npc.preSequence()
            }catch (t: Throwable){
                logger.error("npc pre sync exception [$npc]", t)
            }
        }
        benchMarker.mark("npcs -> pre sync")

        /*
        PID prevention, shuffle every 100 to 150 ticks.
        TODO: make it so players in duel arena have constant PID.
        */
      /*  if (pidCounter.incrementAndGet() == pidThreshold || randomizedIndices.isEmpty()){
            randomizedIndices.clear()
            randomizedIndices.addAll(0 until players.capacity())
            randomizedIndices.shuffle()
            pidCounter.set(0)
            pidThreshold = Random.nextInt(100, 150)
        }*/


        /*
        Sequence players after movement updating, explores the new player state.
         */
        for(player in players) {
            player ?:continue
            try {
                player.sequence(benchMarker)
            }catch (t: Throwable){
                logger.error("player sync exception [$player], requesting logout for player", t)
                player.requestClientLogout()
            }
        }
        benchMarker.mark("players -> sync")

        /*
        Sequence NPCs after movement updating, explores the new NPC state.
        */
        for(npc in npcs) {
            npc ?: continue
            try {
                Monitors.onStartCycle(npc)
                npc.sequence()
                npc.sequenceProperty().set(false)
                Monitors.onEndCycle(npc)
            } catch (t: Throwable) {
                logger.error("npc sync exception [$npc]", t)
            }
        }
        benchMarker.mark("npcs -> sync")

        /*
         * Create and queue packets from state changes about oneself, and other nearby players/NPCs.
         *
         * Note: this does not change any Player/NPC states (paves the way for layer async processing support).
         */
        for(player in players) {
            player ?: continue
            try {
                var task : UpdateTask = PlayerUpdateTask(player)
                task.run()
                task = NpcUpdateTask(player)
                task.run()
            } catch (t: Throwable){
                logger.error("player update exception [$player], requesting logout for player", t)
                player.requestClientLogout()
            }
        }
        benchMarker.mark("players -> player/npc update packet")

        /*
         * Reset update-specific states of NPCs and caches last position.
         */
        for(npc in npcs){
            npc ?: continue
            try {
                val task = PostNpcUpdateTask(npc)
                task.run()
            } catch (t: Throwable) {
                logger.error("npc post sync exception [$npc]", t)
            }
        }
        benchMarker.mark("npcs -> post sync")

        /*
         * Send previously queued packets to client and reset update-specific states.
         */
        for(player in players) {
            player ?: continue
            try {
                val task = PostPlayerUpdateTask(player, updates)
                task.run()
            }catch (t: Throwable){
                logger.error("player post sync exception [$player], requesting logout for player", t)
                player.requestClientLogout()
            }
        }
        benchMarker.mark("players -> post sync")
    }
}