package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SetUpdatedRegionMessage;
import com.grinder.game.model.Position;
import com.grinder.net.packet.DataTransformation;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link SetUpdatedRegionMessage}.
 *
 * @author Chris Fletcher
 */
public final class SetUpdatedRegionMessageEncoder extends MessageEncoder<SetUpdatedRegionMessage> {

	@Override
	public Packet encode(SetUpdatedRegionMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(85);
		Position player = message.getPlayerPosition(), region = message.getRegionPosition();

		builder.put(DataType.BYTE, DataTransformation.NEGATE, region.getLocalY(player));
		builder.put(DataType.BYTE, DataTransformation.NEGATE, region.getLocalX(player));

		return builder.toGamePacket();
	}

}