package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.RemoveTileItemMessage;
import com.grinder.net.packet.DataTransformation;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link RemoveTileItemMessage}.
 *
 * @author Major
 */
public final class RemoveTileItemMessageEncoder extends MessageEncoder<RemoveTileItemMessage> {

	@Override
	public Packet encode(RemoveTileItemMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(156);
		builder.put(DataType.BYTE, DataTransformation.ADD, message.getPositionOffset());
		builder.put(DataType.SHORT, message.getId());
		return builder.toGamePacket();
	}

}