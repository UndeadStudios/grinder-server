package com.grinder.game.message.decoder;

import com.grinder.game.message.MessageDecoder;
import com.grinder.game.message.impl.ItemOnItemMessage;
import com.grinder.game.message.impl.ItemOnNpcMessage;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MessageDecoder} for the {@link ItemOnItemMessage}.
 *
 * @author Chris Fletcher
 * @author Stan van der Bend
 */
public final class ItemOnItemMessageDecoder extends MessageDecoder<ItemOnItemMessage> {

	@Override
	public ItemOnItemMessage decode(@NotNull Packet packet) {
		final PacketReader reader = new PacketReader(packet);

		final int targetSlot = reader.readShort();
		final int usedSlot = reader.readUnsignedShortA();

		final int targetId = reader.readLEShortA();
		final int targetInterface = reader.readShort();

		final int usedId = reader.readLEShort();
		final int usedInterface = reader.readShort();

		return new ItemOnItemMessage(usedInterface, usedId, usedSlot, targetInterface, targetId, targetSlot);
	}

}