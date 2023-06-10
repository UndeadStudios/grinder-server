package com.grinder.game.message.decoder;

import com.grinder.game.message.MessageDecoder;
import com.grinder.game.message.impl.ItemOnNpcMessage;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageDecoder} for the {@link ItemOnNpcMessage}.
 *
 * @author Stan van der Bend
 */
public final class ItemOnNpcMessageDecoder extends MessageDecoder<ItemOnNpcMessage> {

	@Override
	public ItemOnNpcMessage decode(@NotNull Packet packet) {
		final PacketReader reader = new PacketReader(packet);
		final int id = reader.readShortA();
		final int index = reader.readShortA();
		final int slot = reader.readLEShort();
		final int interfaceId = reader.readUnsignedShortA();
		return new ItemOnNpcMessage(id, index, slot, interfaceId);
	}

}