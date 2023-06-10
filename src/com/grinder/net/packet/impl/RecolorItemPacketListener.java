package com.grinder.net.packet.impl;

import com.grinder.game.content.item.coloring.ItemColorCustomizer;
import com.grinder.game.content.item.coloring.ItemColorCustomizer.ColorfulItem;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.message.decoder.RecolorItemMessageDecoder;
import com.grinder.game.message.impl.RecolorItemMessage;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

import java.util.Arrays;

/**
 * This packet edits a player's custom item color
 *
 * @author xplicit
 */

public class RecolorItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final RecolorItemMessage message = RecolorItemMessageDecoder.Companion.decode(packetReader);

        if (player == null || player.getHitpoints() <= 0) {
            return;
        }

        if (player.getInterfaceId() != ItemColorCustomizer.INTERFACE_ID) {
            return;
        }

        int itemId = message.getItemId();

        if (ColorfulItem.getItemIds().containsKey(itemId)) {
            final int[] colors = message.getColors();
            ItemColorCustomizer.saveColor(player, itemId, Arrays
                    .stream(colors)
                    .boxed()
                    .toArray(Integer[]::new)
            );
        }
    }
}
