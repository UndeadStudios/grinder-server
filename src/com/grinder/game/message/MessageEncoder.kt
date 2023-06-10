package com.grinder.game.message

import com.grinder.net.packet.Packet

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
abstract class MessageEncoder<M: Message>{

    abstract fun encode(message: M) : Packet

}