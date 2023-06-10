package com.grinder.game.model.area.update;

import com.grinder.game.entity.Entity;
import com.grinder.game.message.Message;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;


/**
 * An type that is contained in the snapshot of a {@link Region}, which consists of an {@link Entity} being added,
 * removed, or moved.
 *
 * @param <E> The type of {@link Entity} in this type.
 * @author Stan
 */
public abstract class DynamicUpdateOperation<E extends Entity> extends UpdateOperation<E>{

	/**
	 * Creates the dynamic UpdateOperation.
	 *
	 * @param region The region in which the UpdateOperation occurred. Must not be {@code null}.
	 * @param type The type of {@link EntityUpdateType}. Must not be {@code null}.
	 * @param entity The {@link Entity} being added or removed. Must not be {@code null}.
	 */
	public DynamicUpdateOperation(Region region, EntityUpdateType type, E entity) {
		super(region, type, entity);
	}

	/**
	 * Returns this UpdateOperation as a {@link Message}.
	 *
	 * @return The Message.
	 */
	@Override
	public final RegionUpdateMessage toMessage() {
		if(type == EntityUpdateType.CHANGED)
			return change(getPositionOffset(entity.getPosition()));
		else
			return super.toMessage();
	}

	protected abstract RegionUpdateMessage change(int offset);
}