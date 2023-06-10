package com.grinder.game.message.encoder;


import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.UpdateTileItemMessage;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link UpdateTileItemMessage}.
 *
 * @author Major
 */
public final class UpdateTileItemMessageEncoder extends MessageEncoder<UpdateTileItemMessage> {

	@Override
	public Packet encode(UpdateTileItemMessage message) {
		GamePacketBuilder builder = new GamePacketBuilder(84);
		builder.put(DataType.BYTE, message.getPositionOffset());
		builder.put(DataType.SHORT, message.getId());
		builder.put(DataType.SHORT, message.getPreviousAmount());
		builder.put(DataType.SHORT, message.getAmount());
		return builder.toGamePacket();
	}

}