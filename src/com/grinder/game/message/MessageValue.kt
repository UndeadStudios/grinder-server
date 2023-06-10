package com.grinder.game.message

import com.grinder.net.packet.DataOrder
import com.grinder.net.packet.DataSignature
import com.grinder.net.packet.DataTransformation
import com.grinder.net.packet.DataType

/**
 * A [MessageValue] represents a single value which can be operated on throughout
 * a [Message]. A [Message] can hold multiple [MessageValue]s.
 *
 * @param id
 * A unique name that will be used to decode and encode the value.
 *
 * @author Tom <rspsmods@gmail.com>
 */
data class MessageValue(val id: String,
                        val order: DataOrder,
                        val transformation: DataTransformation,
                        val type: DataType,
                        val signature: DataSignature)