package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.RemoveObjectMessage;
import com.grinder.net.packet.DataTransformation;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link RemoveObjectMessage}.
 *
 * @author Major
 */
public final class RemoveObjectMessageEncoder extends MessageEncoder<RemoveObjectMessage> {

	@Override
	public Packet encode(RemoveObjectMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(101);
		builder.put(DataType.BYTE, DataTransformation.NEGATE, message.getType() << 2 | message.getOrientation());
		builder.put(DataType.BYTE, message.getPositionOffset());

		return builder.toGamePacket();
	}

}