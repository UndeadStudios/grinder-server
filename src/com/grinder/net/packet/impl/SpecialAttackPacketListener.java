package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener handles the action when pressing
 * a special attack bar.
 *
 * @author Professor Oak
 */

public class SpecialAttackPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        @SuppressWarnings("unused")
        int specialBarButton = packetReader.readInt();

        if (player.getHitpoints() <= 0) {
            return;
        }

        SpecialAttackType.activate(player);
    }
}
