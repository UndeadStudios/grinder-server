package com.grinder.game.message.decoder;

import com.grinder.game.message.MessageDecoder;
import com.grinder.game.message.impl.ItemOnItemMessage;
import com.grinder.game.message.impl.ItemOnNpcMessage;
import com.grinder.game.message.impl.ItemOnObjectMessage;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageDecoder} for the {@link ItemOnObjectMessage}.
 *
 * @author Major
 * @author Stan van der Bend
 */
public final class ItemOnObjectMessageDecoder extends MessageDecoder<ItemOnObjectMessage> {

	@Override
	public ItemOnObjectMessage decode(@NotNull Packet packet) {
		final PacketReader reader = new PacketReader(packet);

		final int interfaceId = reader.readShort();
		final int objectId = reader.readShort();
		final int y = reader.readLEShortA();
		final int slot = reader.readLEShort();
		final int x = reader.readLEShortA();
		final int itemId = reader.readShort();

		return new ItemOnObjectMessage(interfaceId, itemId, slot, objectId, x, y);
	}

}