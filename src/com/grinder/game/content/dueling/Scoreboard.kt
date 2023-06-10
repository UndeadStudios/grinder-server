package com.grinder.game.content.dueling

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.grinder.game.GameConstants
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.openInterface
import com.grinder.game.model.ObjectActions
import com.grinder.game.service.ServiceManager
import com.grinder.game.service.tasks.TaskRequest
import com.grinder.util.ObjectID
import org.apache.commons.lang.WordUtils
import java.io.File
import java.nio.file.Paths

/**
 * Represents a scoreboard for the last 50 duels in the game.
 *
 * @author Stan van der Bend
 */
object Scoreboard {

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    private val duelArenaPath = Paths.get(GameConstants.DATA_DIRECTORY, "duel_scoreboard.json")
    private val gamblingPath = Paths.get(GameConstants.DATA_DIRECTORY, "gambling_scoreboard.json")

    private val duelArenaEntries = ArrayList<ScoreBoardEntry>()
    private val gamblingEntries = ArrayList<ScoreBoardEntry>()

    private val type = object : TypeToken<ArrayList<ScoreBoardEntry>>() {}.type

    init {
        load()

        ObjectActions.onClick(ObjectID.SCOREBOARD) {
            val player = it.player
            var entryIndex = 0

            if (player.area != null) {
                if (player.area.toString() == "DuelArenaArea") {
                    player.packetSender.sendString(6399, "Last fifty duels on this server:")
                    for (i in 6402..6411) {
                        val entry = duelArenaEntries.getOrNull(entryIndex++)?.toString() ?: ""
                        player.packetSender.sendString(i, entry)
                    }
                    for (i in 8578..8617) {
                        val entry = duelArenaEntries.getOrNull(entryIndex++)?.toString() ?: ""
                        player.packetSender.sendString(i, entry)
                    }
                } else if (player.area.toString() == "DicingArea") {
                    player.packetSender.sendString(6399, "Last fifty gambles on this server:")
                    for (i in 6402..6411) {
                        val entry = gamblingEntries.getOrNull(entryIndex++)?.toStringGambles() ?: ""
                        player.packetSender.sendString(i, entry)
                    }
                    for (i in 8578..8617) {
                        val entry = gamblingEntries.getOrNull(entryIndex++)?.toStringGambles() ?: ""
                        player.packetSender.sendString(i, entry)
                    }
                }
                player.openInterface(6308)
            }


            return@onClick true
        }
    }

    fun addEntry(p1: Player, p2: Player, draw: Boolean, type: String, mode: String) {
        if (type == "duel_arena") {
            duelArenaEntries.add(ScoreBoardEntry(p1.username, p1.skillManager.calculateCombatLevel(), p2.username, p2.skillManager.calculateCombatLevel(), draw, mode))
            if (duelArenaEntries.size == 50)
                duelArenaEntries.removeAt(duelArenaEntries.lastIndex)
            save("duel_arena")
        } else if (type == "gambling") {
            gamblingEntries.add(ScoreBoardEntry(p1.username, p1.skillManager.calculateCombatLevel(), p2.username, p2.skillManager.calculateCombatLevel(), draw, mode))
            if (gamblingEntries.size == 50)
                gamblingEntries.removeAt(gamblingEntries.lastIndex)
            save("gambling")
        }
    }

    fun save(type: String) {
        ServiceManager.taskService.addTaskRequest(object : TaskRequest(Runnable {

            val file: File = if (type == "duel_arena") {
                duelArenaPath.toFile()
            } else {
                gamblingPath.toFile()
            }

            if (!file.exists())
                file.createNewFile()
            val writer = file.writer()

            if (type == "duel_arena") {
                gson.toJson(duelArenaEntries, writer)
            } else {
                gson.toJson(gamblingEntries, writer)
            }

            writer.flush()
            writer.close()
        }, true){
            override fun toString(): String {
                return "Scoreboard Save Task"
            }
        })
    }

    fun load() {
        // Loading Duel Arena Entries.
        val file = duelArenaPath.toFile()

        if (file.exists()) {
            val reader = file.reader()
            duelArenaEntries.clear()
            duelArenaEntries.addAll(gson.fromJson(reader, type))
            reader.close()
        }

        // Loading Gambling Entries.
        val file2 = gamblingPath.toFile()

        if (file2.exists()) {
            val reader = file2.reader()
            gamblingEntries.clear()
            gamblingEntries.addAll(gson.fromJson(reader, type))
            reader.close()
        }
    }

    /**
     * Returns the gambling mode in a formatted form.
     */
    fun formatMode(mode: String) : String {
        var formattedString = mode.replace("_", " ")
        formattedString = WordUtils.capitalizeFully(formattedString)

        return formattedString
    }

    class ScoreBoardEntry(
            private val p1Name: String,
            private val p1Combat: Int,
            private val p2Name: String,
            private val p2Combat: Int,
            private val draw: Boolean,
            private val mode: String) {

        val finalModel = formatMode(mode)

        override fun toString() = "@dre@$p1Name ($p1Combat)</col> ${if (draw) "@whi@drew</col> against" else "has defeated"} @dre@$p2Name ($p2Combat)</col>"
        fun toStringGambles() = "@dre@$p1Name</col> ${if (draw) "@whi@drew</col> against" else "has won against"} @dre@$p2Name</col> via @red@$finalModel</col>"

    }
}