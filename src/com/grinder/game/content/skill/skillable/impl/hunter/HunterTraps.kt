package com.grinder.game.content.skill.skillable.impl.hunter

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.time.SecondsTimer
import com.grinder.util.json.SecondsTimerAdapter
import org.apache.logging.log4j.LogManager
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Object for handling [HunterTrap] mechanics.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/11/2019
 * @version 1.0
 */
object HunterTraps {

    /**
     * Unsure as to why we were saving Traps. Disabled for now.
     */
    private val SAVE_AND_LOAD_TRAPS = false;

    private val PATH = Paths.get("data", "hunter_traps_new.json")
    private val TYPE = object : TypeToken<HashMap<String?, List<HunterTrap?>?>?>() {}.type!!
    private val GSON = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(SecondsTimer::class.java, SecondsTimerAdapter())
            .create()!!

    private val LOGGER = LogManager.getLogger(javaClass.simpleName)

    val PLAYER_TRAPS = HashMap<String, ArrayList<HunterTrap>>()

    // Static init block, loaded by ServerClassPreloader.
    init {

        load()

        TaskManager.submit(object : Task(60) {
            /*
             * Schedule a clean-up task, check for expired traps (life-time > 1 hour)
             */
            override fun execute() {
                var count = 0
                PLAYER_TRAPS.values.forEach { trapList ->
                    val iterator = trapList.iterator()
                    while (iterator.hasNext()){
                        val trap = iterator.next()
                        val timeSincePlacement = System.currentTimeMillis() - trap.timeOfPlacement

                        if(TimeUnit.MILLISECONDS.toMinutes(timeSincePlacement) > 1){
                            iterator.remove()
                            HunterActions.dropTrap(trap)
                            trap.deSpawn(true)
                            count++
                        }
                    }
                }
                if(count > 0)
                    save()
            }
        })
    }

    /**
     * Add the argued [HunterTrap] to the [PLAYER_TRAPS] map,
     * and [save] the new [PLAYER_TRAPS] map to a file.
     *
     * @param trap the [HunterTrap] to be added.
     */
    fun register(trap: HunterTrap){
        PLAYER_TRAPS.putIfAbsent(trap.username, ArrayList())
        PLAYER_TRAPS[trap.username]!!.add(trap)
        save()
    }

    /**
     * Remove the argued [HunterTrap] from the [PLAYER_TRAPS] map,
     * and [save] the new [PLAYER_TRAPS] map to a file.
     *
     * @param trap the [HunterTrap] to be removed.
     */
    fun unregister(trap: HunterTrap){
        PLAYER_TRAPS[trap.username]!!.remove(trap)
        save()
    }

    /**
     * Process the argued [NPC], check if target,
     * and if caught by trap.
     *
     * @see HunterTrap.sequencePray
     */
    fun process(npc: NPC) : Boolean {

        if(!HunterCatchType.map.containsKey(npc.id))
            return false

        HunterCatchType.map[npc.id]?.let {
            loop@for(traps in PLAYER_TRAPS.values){
                for(trap in traps){
                    if(trap.sequencePray(it, npc)) {
                        return true
                    }
                }
            }
        }

        return true
    }

    /**
     * Load serialised [PLAYER_TRAPS] and spawn into world.
     */
    fun load(){
        if (SAVE_AND_LOAD_TRAPS) {
            try {
                val file = PATH.toFile()

                if (!file.exists())
                    return

                val reader = file.reader()
                val traps = GSON.fromJson<HashMap<String, ArrayList<HunterTrap>>>(reader, TYPE)
                reader.close()

                if (traps == null) {
                    LOGGER.error("Could not read file {${file}}")
                    return
                }

                PLAYER_TRAPS.clear()
                PLAYER_TRAPS.putAll(traps)
                var i = 0
                PLAYER_TRAPS.values.forEach {
                    it.forEach { trap ->
                        trap.spawn(false)
                        i++
                    }
                }
                LOGGER.info("Spawned $i traps!")

            } catch (e: Exception) {
                LOGGER.error("Failed to load traps", e)
            }
        }
    }

    /**
     * Save [PLAYER_TRAPS] to file at [PATH] using [GSON].
     */
    fun save() {
        if (SAVE_AND_LOAD_TRAPS) {
            try {
                val file = PATH.toFile()
                file.createNewFile()
                val writer = file.bufferedWriter()
                GSON.toJson(PLAYER_TRAPS, writer)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                LOGGER.error("Failed to save traps", e)
            }
        }
    }

    /**
     * Find any trap of the [Player] at the argued [Position]
     * in [PLAYER_TRAPS].
     *
     * @param player    the [Player] owner
     * @param position  the [Position] of the [HunterTrap] (if any)
     *
     * @return [HunterTrap] if exists, NULL if not
     */
    fun findTrap(player: Player, position: Position) = PLAYER_TRAPS[player.username]?.find {
        it.obj.position.`is`(position, true)
    }

    /**
     * Count the list size of traps belonging to the argued [Player].
     *
     * @param player the [Player] owner
     *
     * @return [Int] list size if player has traps,
     *              0 if not.
     */
    fun countTrapsOf(player: Player) = PLAYER_TRAPS[player.username]?.size?:0

    /**
     * Get the maximum amount of traps the argued [Player] can register,
     * this is based on the player's Hunter skill level.
     *
     * @param player the [Player] owner
     *
     * @return [Int] max number of traps (range 1..5)
     */
    fun maxTrapsFor(player: Player) : Int {
        val hunterLevel = player.skillManager.getCurrentLevel(Skill.HUNTER)
        return when {
            hunterLevel >= 80 -> 5
            hunterLevel >= 60 -> 4
            hunterLevel >= 40 -> 3
            hunterLevel >= 20 -> 2
            else -> 1
        }
    }
}