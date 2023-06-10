package com.grinder.net.packet.impl;

import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.message.decoder.AddBroadcastMessageDecoder;
import com.grinder.game.message.impl.AddBroadcastMessage;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * A packet that is called when a request of a broadcast message is being
 * received from the client.
 * 
 * @author Blake
 */
public class AddBroadcastPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

		final AddBroadcastMessageDecoder messageDecoder = new AddBroadcastMessageDecoder();
		final AddBroadcastMessage message = messageDecoder.decode(packetReader.getPacket());

		if (player.getInterfaceId() != Broadcast.INTERFACE_ID)
			return;

		final int duration = message.getDuration();
		final String text = message.getText();
		final String link = message.getLink();
		
		Broadcast.broadcast(player, duration, text, link);
	}

}
