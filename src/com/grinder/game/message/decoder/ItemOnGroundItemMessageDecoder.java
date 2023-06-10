package com.grinder.game.message.decoder;

import com.grinder.game.message.MessageDecoder;
import com.grinder.game.message.impl.ItemOnGroundItemMessage;
import com.grinder.game.message.impl.ItemOnItemMessage;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageDecoder} for the {@link ItemOnGroundItemMessage}.
 *
 * @author Stan van der Bend
 */
public final class ItemOnGroundItemMessageDecoder extends MessageDecoder<ItemOnGroundItemMessage> {

	@Override
	public ItemOnGroundItemMessage decode(@NotNull Packet packet) {
		final PacketReader reader = new PacketReader(packet);

		final int interfaceId = reader.readLEShort();
		final int itemId = reader.readShortA();
		final int groundItemId = reader.readShort();
		final int y = reader.readShortA();
		final int slot = reader.readLEShortA();
		final int x = reader.readShort();

		return new ItemOnGroundItemMessage(interfaceId, itemId, slot, groundItemId, x, y);
	}

}