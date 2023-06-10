package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.ClearRegionMessage;
import com.grinder.game.model.Position;
import com.grinder.net.packet.DataTransformation;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link ClearRegionMessage}.
 *
 * @author Major
 */
public final class ClearRegionMessageEncoder extends MessageEncoder<ClearRegionMessage> {

	@Override
	public Packet encode(ClearRegionMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(64);
		Position player = message.getPlayerPosition(), region = message.getRegionPosition();

		builder.put(DataType.BYTE, DataTransformation.NEGATE, region.getLocalX(player));
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, region.getLocalY(player));

		return builder.toGamePacket();
	}

}