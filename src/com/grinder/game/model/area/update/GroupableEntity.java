package com.grinder.game.model.area.update;

import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;

/**
 * An entity that can be sent as part of a grouped region update message.
 * <p>
 * Only {@link com.grinder.game.entity.Entity} extensions may implement this interface.
 * 
 * @author Major
 */
public interface GroupableEntity {

	/**
	 * Gets this Entity, as an {@link UpdateOperation} of a {@link Region}.
	 *
	 * @param region The Region.
	 * @param type The EntityUpdateType.
	 * @return The UpdateOperation.
	 */
	UpdateOperation<?> toUpdateOperation(Region region, EntityUpdateType type);

}