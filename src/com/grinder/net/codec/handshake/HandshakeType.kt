package com.grinder.net.codec.handshake

/**
 * @author Tom <rspsmods@gmail.com>
 * @author Stan van der Bend
 */
enum class HandshakeType(val id: Int) {
    LOGIN(14),
    FILESTORE(15);

    companion object {
        val values = enumValues<HandshakeType>()
    }
}