package com.grinder.game.model.message

/**
 * Represents types of messages a player can send.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   21/10/2020
 * @version 1.0
 */
enum class MessageType {

    /**
     * Messages that can be send in the public chat,
     * any nearby player will receive the message.
     */
    PUBLIC_CHAT,

    /**
     * Messages that can be send to friends.
     */
    PRIVATE_CHAT,

    /**
     * Messages that can be send to clan members.
     */
    CLAN_CHAT;

    fun formatContext(context: String) : String {
        return when(this){
            PUBLIC_CHAT -> "`public_chat` message"
            PRIVATE_CHAT -> "`private_chat` message to player **$context**"
            CLAN_CHAT -> "`clan_chat` message to clan **$context**"
        }
    }
}