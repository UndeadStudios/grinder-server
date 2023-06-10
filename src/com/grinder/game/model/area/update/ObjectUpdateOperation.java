package com.grinder.game.model.area.update;

import com.grinder.game.entity.object.GameObject;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.message.impl.RemoveObjectMessage;
import com.grinder.game.message.impl.SendObjectMessage;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;

/**
 * A {@link UpdateOperation} for addition or removal of {@link GameObject}s.
 *
 * @author Major
 */
public final class ObjectUpdateOperation extends UpdateOperation<GameObject> {

	/**
	 * Creates the ObjectUpdateOperation.
	 *
	 * @param region The {@link Region} in which the ObjectUpdateOperation occurred. Must not be {@code null}.
	 * @param type The {@link EntityUpdateType}. Must not be {@code null}.
	 * @param object The {@link GameObject}. Must not be {@code null}.
	 */
	public ObjectUpdateOperation(Region region, EntityUpdateType type, GameObject object) {
		super(region, type, object);
	}

	@Override
	protected RegionUpdateMessage add(int offset) {
		return new SendObjectMessage(entity, offset);
	}

	@Override
	protected RegionUpdateMessage remove(int offset) {
		return new RemoveObjectMessage(entity, offset);
	}

}