package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SendTileItemMessage;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link SendTileItemMessage}.
 *
 * @author Major
 */
public final class AddTileItemMessageEncoder extends MessageEncoder<SendTileItemMessage> {

	@Override
	public Packet encode(SendTileItemMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(44);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getId());
		builder.put(DataType.INT, message.getAmount());
		builder.put(DataType.BYTE, message.getPositionOffset());
		return builder.toGamePacket();
	}

}