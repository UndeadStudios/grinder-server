package com.grinder.game.entity.updating.seg;

import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.model.Position;

/**
 * A {@link UpdateSegment} where the mob is teleported to a new location.
 *
 * @author Graham
 */
public final class TeleportSegment extends UpdateSegment {

	/**
	 * The destination.
	 */
	private final Position destination;

	/**
	 * Creates the teleport segment.
	 *
	 * @param blockSet The block set.
	 * @param destination The destination.
	 */
	public TeleportSegment(UpdateBlockSet blockSet, Position destination) {
		super(blockSet);
		this.destination = destination;
	}

	/**
	 * Gets the destination.
	 *
	 * @return The destination.
	 */
	public Position getDestination() {
		return destination;
	}

	@Override
	public SegmentType getType() {
		return SegmentType.TELEPORT;
	}

}