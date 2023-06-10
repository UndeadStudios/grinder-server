package com.grinder.game.message.impl

import com.grinder.game.message.Message

/**
 * Represents a packet that is send whenever a player
 * clicks an option displayed when an item is right clicked.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class ItemActionMessage(val itemId: Int, val slot: Int, val interfaceId: Int, val opcode: Int) : Message
