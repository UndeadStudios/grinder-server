package com.grinder.game.message.impl

import com.grinder.game.message.Message
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class PlayerOptionMessage(val index: Int, val opcode: Int) : Message
