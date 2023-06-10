package com.grinder.net.codec.game;

import com.grinder.game.message.Message;
import com.grinder.game.message.MessageEncoder;
import com.grinder.net.packet.PacketConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * A {@link MessageToMessageEncoder} which encodes {@link Message}s into {@link com.grinder.net.packet.Packet}s.
 *
 * @author Graham
 */
public final class GameMessageEncoder extends MessageToMessageEncoder<Message> {

	/**
	 * The current release.
	 */
	private final PacketConfiguration release;

	/**
	 * Creates the game message encoder with the specified release.
	 *
	 * @param release The release.
	 */
	public GameMessageEncoder(PacketConfiguration release) {
		this.release = release;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, List<Object> out) {
		MessageEncoder<Message> encoder = (MessageEncoder<Message>) release.getMessageEncoder(message.getClass());
		if (encoder != null) {
			out.add(encoder.encode(message));
		}
	}

}