package com.grinder.game.message.impl;


import com.grinder.game.message.Message;
import com.grinder.game.model.projectile.Projectile;

/**
 * A {@link Message} sent to the client to display a projectile in the world.
 */
public final class SendProjectileMessage extends RegionUpdateMessage {

	/**
	 * The {@link Projectile} to be sent to the client.
	 */
	private final Projectile projectile;

	/**
	 * The position offset.
	 */
	private final int positionOffset;

	/**
	 * Creates the {@code SendProjectileMessage}.
	 *
	 * @param projectile The projectile to be sent to the client. Must not be {@code null}.
	 * @param positionOffset The position offset.
     */
	public SendProjectileMessage(Projectile projectile, int positionOffset) {
		this.projectile = projectile;
		this.positionOffset = positionOffset;
	}

	/**
	 * Gets the projectile.
	 *
	 * @return The projectile to be sent to the client.
     */
	public Projectile getProjectile() {
		return projectile;
	}

	/**
	 * Gets the position offset.
	 *
	 * @return The position offset.
     */
	public int getPositionOffset() {
		return positionOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendProjectileMessage) {
			SendProjectileMessage other = (SendProjectileMessage) obj;

			return projectile.equals(other.projectile) && positionOffset == other.positionOffset;
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + positionOffset;
		result = prime * result + projectile.hashCode();
		return result;
	}

	@Override
	public int priority() {
		return LOW_PRIORITY;
	}
}
