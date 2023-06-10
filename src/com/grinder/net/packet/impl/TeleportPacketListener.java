package com.grinder.net.packet.impl;

import com.grinder.game.content.item.MorphItems;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener handles the action when pressing a teleport menu in the
 * chatbox teleport interface.
 *
 * @author Professor Oak
 */

public class TeleportPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

		int type = packetReader.readByte();
		int index = packetReader.readByte();

		if (player.getHitpoints() <= 0)
			return;
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isInTutorial()) {
        	return;
        }
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
    		return;
    	}
		if (!MorphItems.INSTANCE.notTransformed(player, "teleport", true, true))
			return;
        if (player.isJailed()) {
        	player.getPacketSender().sendMessage("You're not allowed to teleport when you're jailed!", 1000);
            return;
        }


		if (!player.isTeleportInterfaceOpen()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"Selected a teleport. Type: " + Integer.toString(type) + ", index: " + Integer.toString(index) + ".");
		}
	}
}
