package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.RegionChangeMessage;
import com.grinder.net.packet.DataTransformation;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link RegionChangeMessage}.
 *
 * @author Graham
 */
public final class RegionChangeMessageEncoder extends MessageEncoder<RegionChangeMessage> {

	@Override
	public Packet encode(RegionChangeMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(73);
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getPosition().getCentralRegionX());
		builder.put(DataType.SHORT, message.getPosition().getCentralRegionY());
		return builder.toGamePacket();
	}

}