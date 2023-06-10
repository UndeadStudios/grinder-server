package com.grinder.game.message.decoder;

import com.grinder.game.message.MessageDecoder;
import com.grinder.game.message.impl.ItemOnNpcMessage;
import com.grinder.game.message.impl.ItemOnPlayerMessage;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageDecoder} for the {@link ItemOnPlayerMessage}.
 *
 * @author Stan van der Bend
 */
public final class ItemOnPlayerMessageDecoder extends MessageDecoder<ItemOnPlayerMessage> {

	@Override
	public ItemOnPlayerMessage decode(@NotNull Packet packet) {
		final PacketReader reader = new PacketReader(packet);

		final int interfaceId = reader.readUnsignedShortA();
		final int index = reader.readUnsignedShort();
		final int itemId = reader.readUnsignedShort();
		final int slot = reader.readLEShort();

		return new ItemOnPlayerMessage(itemId, index, slot, interfaceId);
	}

}