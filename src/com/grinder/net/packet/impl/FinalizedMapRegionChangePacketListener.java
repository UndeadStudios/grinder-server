package com.grinder.net.packet.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when a player's region has been loaded.
 *
 * @author relex lawl
 */

public class FinalizedMapRegionChangePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
    }
}