package com.grinder.net.packet.impl

import com.grinder.game.content.clan.GlobalClanChatManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.message.MessageFilterManager
import com.grinder.game.model.message.MessageType
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * A [PacketListener] that listens to clan chat messages.
 *
 * @author Gabriel Hannason
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
class MessageClanListener : PacketListener {

    override fun handleMessage(player: Player, packetReader: PacketReader, packetOpcode: Int) {

        val clanMessage = packetReader.readString()

        if (MessageFilterManager.blockMessage(player, clanMessage))
            return

        MessageFilterManager.reportMessage(player, clanMessage, MessageType.CLAN_CHAT, player.currentClanChat?.name?:"")

        GlobalClanChatManager.sendMessage(player, clanMessage)
    }
}