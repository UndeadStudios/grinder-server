package com.grinder.game.model.area.update;

import com.grinder.game.entity.Entity;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.message.impl.SendProjectileMessage;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.projectile.Projectile;

/**
 * An {@link UpdateOperation} for addition of  {@link Projectile}s.
 */
public final class ProjectileUpdateOperation extends UpdateOperation<Projectile> {

	/**
	 * Creates the ProjectileUpdateOperation.
	 *
	 * @param region The region in which the UpdateOperation occurred. Must not be {@code null}.
	 * @param type The type of {@link EntityUpdateType}. Must not be {@code null}.
	 * @param entity The {@link Entity} being added or removed. Must not be {@code null}.
	 */
	public ProjectileUpdateOperation(Region region, EntityUpdateType type, Projectile entity) {
		super(region, type, entity);
	}

	@Override
	protected RegionUpdateMessage add(int offset) {
		return new SendProjectileMessage(entity, offset);
	}

	@Override
	protected RegionUpdateMessage remove(int offset) {
		throw new IllegalStateException("Projectiles cannot be removed.");
	}

}
