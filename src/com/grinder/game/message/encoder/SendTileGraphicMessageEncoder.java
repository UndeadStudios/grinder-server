package com.grinder.game.message.encoder;

import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SendTileGraphicMessage;
import com.grinder.game.message.impl.SendTileItemMessage;
import com.grinder.game.model.TileGraphic;
import com.grinder.net.packet.*;

/**
 * A {@link MessageEncoder} for the {@link SendTileItemMessage}.
 *
 * @author Stan van der Bend
 */
public final class SendTileGraphicMessageEncoder extends MessageEncoder<SendTileGraphicMessage> {

	@Override
	public Packet encode(SendTileGraphicMessage message) {
		final GamePacketBuilder builder = new GamePacketBuilder(4);
		builder.put(DataType.BYTE, message.getPositionOffset());
		builder.put(DataType.SHORT,message.getId());
		builder.put(DataType.BYTE, message.getHeight());
		builder.put(DataType.SHORT, message.getDelay());
		return builder.toGamePacket();
	}

}