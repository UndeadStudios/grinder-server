package com.grinder.net.packet.impl

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.clan.GlobalClanChatManager
import com.grinder.game.content.miscellaneous.npcs.CleverBot
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.event.PlayerEvents
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.message.ChatMessage
import com.grinder.game.model.message.MessageFilterManager
import com.grinder.game.model.message.MessageType
import com.grinder.net.packet.DataType
import com.grinder.net.packet.GamePacketReader
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader
import com.grinder.util.Logging
import com.grinder.util.Misc
import com.grinder.util.compress.HuffmanCodec
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * A [PacketListener] that listens to public chat messages.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/02/2020
 * @version 1.0
 */
class MessagePublicListener : PacketListener{

    override fun handleMessage(player: Player, packetReader: PacketReader, packetOpcode: Int) {

        val packet = packetReader.packet
        val reader = GamePacketReader(packet)

        val type = reader.getUnsigned(DataType.BYTE).toInt()
        val color = reader.getUnsigned(DataType.BYTE).toInt()
        val effect = reader.getUnsigned(DataType.BYTE).toInt()
        val length = reader.unsignedSmart
        val data = ByteArray(reader.readableBytes)

        reader.getBytes(data)

        val decompressed = ByteArray(256)
        huffman.decompress(data, decompressed, length)

        val unpacked = String(decompressed, 0, length)

        if (MessageFilterManager.blockMessage(player, unpacked))
            return

        if (MessageFilterManager.reportMessage(player, unpacked, MessageType.PUBLIC_CHAT, ""))
            Logging.log("PlayerChats", "${player.username} said: $unpacked")

        if (unpacked.toLowerCase() == "lol") {
            player.points.increase(AttributeManager.Points.LOL_MESSAGE_TIMES, 1) // Increase points
        }

        if (unpacked.toLowerCase().contains("<img")) {
            return;
        }

        if (player.chatMessageQueue.size >= 5)
            return

        if (!player.passedTime(Attribute.LAST_CHAT, 2, TimeUnit.SECONDS, message = false, updateIfPassed = false) && player.chatMessageQueue.size > 0) {
            player.chatTimer.extendOrStart(10)
            return
        }

        if (!player.chatTimer.finished())
            return

        if (Misc.blockedWord(unpacked)) {
            player.statement("A word was blocked in your sentence.", " Please do not repeat it!")
            return
        }

        if (!player.isInTutorial)
            AchievementManager.processFor(AchievementType.COMMUNICATING, player)

        if (PlayerUtil.isStaff(player))
            Logging.log("StaffChats", "${player.username} said: $unpacked")

        player.notify(PlayerEvents.TALKED)
        player.markTime(Attribute.LAST_CHAT)

        val chatType = ChatMessage.ChatType.values.firstOrNull { it.id == type } ?: ChatMessage.ChatType.NONE
        val chatEffect = ChatMessage.ChatEffect.values.firstOrNull { it.id == effect } ?: ChatMessage.ChatEffect.NONE
        val chatColor = ChatMessage.ChatColor.values.firstOrNull { it.id == color } ?: ChatMessage.ChatColor.NONE

        val message =  ChatMessage(unpacked, player.rights.ordinal, chatType, chatEffect, chatColor)

        if(chatType == ChatMessage.ChatType.CLANCHAT)
            GlobalClanChatManager.sendMessage(player, unpacked)
        else
            player.chatMessageQueue.add(message)

        CleverBot.aiBotList.forEach { aiBot ->
            if (player.isWithinDistance(aiBot, 3)) {
                aiBot.answer(message.text, player)
            }
        }
    }

    companion object {

        /**
         * The [HuffmanCodec] used to compress and decompress public chat messages.
         */
        val huffman by lazy {
            HuffmanCodec(Files.readAllBytes(Paths.get("data", "huffman.dat")))
        }
    }
}