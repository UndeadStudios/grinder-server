package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SendMakeTileItemPublicMessage;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link MakeTileItemPublicMessageEncoder}.
 *
 * @author Major
 */
public final class MakeTileItemPublicMessageEncoder extends MessageEncoder<SendMakeTileItemPublicMessage> {

	@Override
	public Packet encode(SendMakeTileItemPublicMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(216);
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getId());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getPositionOffset());
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getOwnerIndex());
		builder.put(DataType.SHORT, message.getAmount());
		return builder.toGamePacket();
	}

}