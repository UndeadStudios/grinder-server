package com.grinder.game.message.encoder;


import com.grinder.game.message.MessageEncoder;
import com.grinder.game.message.impl.SendProjectileMessage;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;
import com.grinder.net.packet.Packet;

/**
 * A {@link MessageEncoder} for the {@link SendProjectileMessage}.
 */
public final class SendProjectileMessageEncoder extends MessageEncoder<SendProjectileMessage> {

	@Override
	public Packet encode(SendProjectileMessage message) {
		Projectile projectile = message.getProjectile();
		Position source = projectile.getPosition();
		Position destination = projectile.getTarget();

		GamePacketBuilder builder = new GamePacketBuilder(117);
		builder.put(DataType.BYTE, message.getPositionOffset());
		builder.put(DataType.BYTE, destination.getX() - source.getX());
		builder.put(DataType.BYTE, destination.getY() - source.getY());
		builder.put(DataType.SHORT, projectile.getLockon());
		builder.put(DataType.SHORT, projectile.getProjectileID());
		builder.put(DataType.BYTE, projectile.getStartHeight());
		builder.put(DataType.BYTE, projectile.getEndHeight());
		builder.put(DataType.SHORT, projectile.getDelay());
		builder.put(DataType.SHORT, projectile.getLifespan());
		builder.put(DataType.BYTE, projectile.getCurve());
		builder.put(DataType.BYTE, Math.min(255, (projectile.getCreatorSize() * 64) + projectile.getStartDistanceOffset()));
		return builder.toGamePacket();
	}

}
