package com.grinder.net.packet;

import com.grinder.game.entity.agent.player.Player;


/**
 * Represents a Packet received from client.
 *
 * @author Gabriel Hannason
 */
public interface PacketListener {

    /**
     * Handle the packet
     *  @param player       the recipient of the packet
     * @param packetReader the reader of the packet (for io)
     * @param packetOpcode the unique packet identifier
     */
    void handleMessage(Player player, PacketReader packetReader, int packetOpcode);

}
