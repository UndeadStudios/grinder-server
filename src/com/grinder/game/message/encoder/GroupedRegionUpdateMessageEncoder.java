package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.GroupedRegionUpdateMessage;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.model.Position;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link GroupedRegionUpdateMessage}.
 *
 * @author Major
 */
public final class GroupedRegionUpdateMessageEncoder extends MessageEncoder<GroupedRegionUpdateMessage> {

	/**
	 * The Release containing the MessageEncoders for the RegionUpdateMessages.
	 */
	private final PacketConfiguration release;

	/**
	 * Creates the GroupedRegionUpdateMessageEncoder.
	 *
	 * @param release The {@link PacketConfiguration} containing the {@link MessageEncoder}s for the {@link RegionUpdateMessage}s.
	 */
	public GroupedRegionUpdateMessageEncoder(PacketConfiguration release) {
		this.release = release;
	}

	@Override
	public Packet encode(GroupedRegionUpdateMessage message) {
		final GamePacketBuilder builder = new GamePacketBuilder(60, PacketType.VARIABLE_SHORT);
		final Position base = message.getLastKnownRegion(), region = message.getRegionPosition();

		builder.put(DataType.BYTE, region.getLocalY(base));
		builder.put(DataType.BYTE, DataTransformation.NEGATE, region.getLocalX(base));

		for (RegionUpdateMessage update : message.getMessages()) {
			@SuppressWarnings("unchecked")
			final MessageEncoder<RegionUpdateMessage> encoder = (MessageEncoder<RegionUpdateMessage>) release
					.getMessageEncoder(update.getClass());
			final Packet packet = encoder.encode(update);
			builder.put(DataType.BYTE, packet.getOpcode());
			builder.putBytes(packet.getPayload());
		}

		return builder.toGamePacket();
	}

}