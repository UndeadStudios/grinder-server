package com.grinder.game.model.message

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.grinder.Server
import com.grinder.game.GameConstants
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.statement
import com.grinder.util.DiscordBot
import com.grinder.util.Misc
import java.nio.file.Paths

/**
 * Represents a filter system for player messages.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   21/10/2020
 * @version 1.0
 */
object MessageFilterManager {

    val filters = HashSet<MessageFilter>()

    private val path = Paths.get(GameConstants.DATA_DIRECTORY, "message_filters.json")
    private val gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
    private val type = object: TypeToken<HashSet<MessageFilter>>(){}.type

    init {
        load()
    }

    fun serialise(){
        val file = path.toFile()
        if(!file.exists())
            file.createNewFile()
        val writer = file.writer()
        gson.toJson(filters, writer)
        writer.flush()
        writer.close()
    }

    fun load(){
        val file = path.toFile()
        if(file.exists()) {
            try {
                val reader = file.reader()
                filters.clear()
                filters.addAll(gson.fromJson(reader, type))
            } catch (e: Exception) {
                Server.getLogger().info("Did not parse message filters in $file (probably empty)")
            }
        }
    }

    fun blockMessage(player: Player, text: String?): Boolean {
        if (text == null || text.isEmpty())
            return true
        if (player.isMuted) {
            player.message("You are muted and cannot chat.")
            return true
        }
        if (player.hasAutoTalkerMessageActive()) {
            player.sendMessage("@red@The message auto-typer has been interrupted. Type ::repeat to repeat the last message.")
            player.setHasAutoTalkerMessageActive(false)
        }
        if (player.messageToAutoTalk != "") {
            player.messageToAutoTalk = ""
        }
        /*if (Misc.blockedWord(text)) {
            player.statement("A word was blocked in your sentence. Please do not repeat it!")
            return true
        }*/
        return false
    }

    fun reportMessage(player: Player, text: String, messageType: MessageType, context: String): Boolean {

        if(PlayerUtil.isHighStaff(player))
            return false

        for(filter in filters){
            if(!filter.passes(text, messageType)){
                DiscordBot.INSTANCE.sendModMessage("Player **${player.username}** had trigger word(s) in the following " + messageType.formatContext(context)
                        + "\n```$text```"
                        + "\ncaught by filter: "
                        + "\n\t**creator:** ${filter.creator}"
                        + "\n\t**triggers:** ${filter.triggers}")
                return true
            }
        }

        return false
    }

    fun addFilter(filter: MessageFilter) : Boolean{

        if(filters.size > 1_000)
            return false

        return filters.add(filter).also {
            if(it)
                serialise()
        }
    }

    fun filtersGroupedByUserName() = filters.groupBy { it.creator }
}