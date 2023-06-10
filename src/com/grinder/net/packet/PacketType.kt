package com.grinder.net.packet

enum class PacketType {
    /**
     * A packet with no header.
     */
    RAW,
    FIXED,
    VARIABLE_BYTE,
    VARIABLE_SHORT
}