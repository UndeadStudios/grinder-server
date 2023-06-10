package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SendObjectMessage;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link SendObjectMessage}.
 *
 * @author Major
 */
public final class SendObjectMessageEncoder extends MessageEncoder<SendObjectMessage> {

	@Override
	public Packet encode(SendObjectMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(151);
		builder.put(DataType.BYTE, DataTransformation.ADD, message.getPositionOffset());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getId());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getType() << 2 | message.getOrientation());
		return builder.toGamePacket();
	}

}