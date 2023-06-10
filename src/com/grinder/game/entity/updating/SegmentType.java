package com.grinder.game.entity.updating;

/**
 * An enumeration which contains the types of segments.
 *
 * @author Graham
 */
public enum SegmentType {

	/**
	 * A segment where the mob is added.
	 */
	ADD_MOB,

	/**
	 * A segment without any movement.
	 */
	NO_MOVEMENT,

	/**
	 * A segment where the mob is removed.
	 */
	REMOVE_MOB,

	/**
	 * A segment with movement in two directions.
	 */
	RUN,

	/**
	 * A segment where the mob is teleported.
	 */
	TELEPORT,

	/**
	 * A segment with movement in a single direction.
	 */
	WALK;

	/**
	 * Check whether this type represents directional movement.
	 *
	 * @return {@code true} if this equals {@link #WALK} or {@link #RUN},
	 * 			{@code false} if otherwise
	 */
	public boolean isDirectionalMovement(){
		return this == WALK || this == RUN;
	}
}