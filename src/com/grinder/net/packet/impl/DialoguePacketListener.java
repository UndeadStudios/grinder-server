package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * Represents a packet used for handling dialogues. This specific packet
 * currently handles the action for clicking the "next" option during a
 * dialogue.
 *
 * @author Professor Oak
 */

public class DialoguePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

		if (player == null || player.getHitpoints() <= 0) {
			return;
		}

		DialogueManager.next(player);
	}
}
