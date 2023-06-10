package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This {@link PacketListener} receives the client's current plane
 * and compares it to the player's server-sided one.
 * 
 * If they do not match, we will manually send the proper plane
 * to the client.
 * 
 * This fixed the exploit where players would use third-party softwares
 * to teleport to different planes.
 * 
 * @author Gabriel Hannason
 */

public class HeightCheckPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int plane = packetReader.readByte();
        
        if (player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4) {
            if (plane != player.getPosition().getZ()) {
                if (player.getMotion().canMove()) {
                    player.getMotion().clearSteps();
                    player.setPendingTeleportUpdate(true);
                    player.getPacketSender().sendHeight();
                    player.getPacketSender().sendInterfaceRemoval();
                    player.setWalkToTask(null);
                }
            }
        }
    }
}
