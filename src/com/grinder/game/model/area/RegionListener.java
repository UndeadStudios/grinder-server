package com.grinder.game.model.area;

import com.grinder.game.entity.Entity;

/**
 * A class that should be implemented by listeners that execute actions when an entity is added, moved, or removed from
 * a region.
 *
 * @author Major
 */
@FunctionalInterface
public interface RegionListener {

	/**
	 * Executes the action for this listener.
	 *
	 * @param region The {@link Region} that was updated.
	 * @param entity The affected {@link Entity}.
	 * @param type The type of {@link EntityUpdateType}.
	 */
	void execute(Region region, Entity entity, EntityUpdateType type);

}