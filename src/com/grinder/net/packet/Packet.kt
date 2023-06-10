package com.grinder.net.packet

import com.google.common.base.MoreObjects
import io.netty.buffer.ByteBuf

/**
 * Manages reading packet information from the netty's channel.
 *
 * @param opcode unique packet identifier
 * @param payload buffer containing data
 *
 * @author relex lawl
 * @author Stan van der Bend
 */
class Packet(val opcode: Int, val type: PacketType, val payload: ByteBuf) {

    /**
     * Get the [ByteBuf.readableBytes] of byte buf of this packet.
     *
     * @return the amount of bytes in this packet
     */
    val length: Int = payload.readableBytes()

    override fun toString(): String = MoreObjects.toStringHelper(this).add("opcode", opcode).add("type", type).add("length", length).toString()
}