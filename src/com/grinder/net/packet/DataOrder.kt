package com.grinder.net.packet

/**
 * The enumerated type whose elements represent the possible order in which
 * bytes are written in a multiple-byte value. Also known as "endianness".
 *
 * Represents the order of bytes in a [DataType] when [DataType.getBytes] `> 1`.
 *
 * @author blakeman8192
 * @author Graham
 */
enum class DataOrder {

    /**
     * Least significant byte to most significant byte.
     */
    LITTLE,

    /**
     * Most significant byte to least significant byte.
     */
    BIG,

    /**
     * Also known as the V1 order.
     */
    MIDDLE,

    /**
     * Also known as the V2 order.
     */
    INVERSE_MIDDLE,

    TRIPLE_INT
}