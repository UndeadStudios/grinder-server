package com.grinder.game.model.message

import com.google.gson.annotations.Expose
import net.dv8tion.jda.api.entities.User

/**
 * Represents a filter that can be added by staff member through the [DiscordBot].
 *
 * @see [DiscordBot.onMessageReceived] for implementation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   26/03/2020
 * @version 1.0
 *
 * @param creator the name of the discord [User] that created this filter
 * @param triggers the key words searched for in a string
 */
class MessageFilter(@Expose val creator: String,
                    @Expose val triggers: ArrayList<String>,
                    @Expose val filterType: MessageFilterType,
                    @Expose val scope: MessageFilterScope) {

    fun passes(string: String, messageType: MessageType): Boolean {

        if(scope == MessageFilterScope.PRIVATE_ONLY && messageType != MessageType.PRIVATE_CHAT)
            return true

        if(scope == MessageFilterScope.PUBLIC_ONLY && messageType != MessageType.PUBLIC_CHAT)
            return true

        if(scope == MessageFilterScope.CLAN_ONLY && messageType != MessageType.CLAN_CHAT)
            return true

        return triggers.none {
            if(filterType == MessageFilterType.SENTENCE_CONTAINS)
                string.contains(it, ignoreCase = true)
            else {
                val split = string.split('-', ' ', '|', ',', '.', ':', ';', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '[', ']', '{', '}', '\\')
                if (filterType == MessageFilterType.SPLIT_CONTAINS)
                    split.any { word -> word.contains(it, true) }
                else
                    split.any { word -> word.equals(it, true)}
            }
        }
    }

    fun toFormattedString() = "$triggers"
}