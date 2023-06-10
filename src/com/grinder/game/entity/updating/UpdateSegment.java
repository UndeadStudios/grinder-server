package com.grinder.game.entity.updating;


import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;

/**
 * A segment contains a set of {@link UpdateBlock}s, {@link Direction}s (or teleport {@link Position}s) and any
 * other things required for the update of a single player.
 *
 * @author Graham
 */
public abstract class UpdateSegment {

	/**
	 * The {@link UpdateBlockSet}.
	 */
	private final UpdateBlockSet blockSet;

	/**
	 * Creates the segment.
	 *
	 * @param blockSet The block set.
	 */
	public UpdateSegment(UpdateBlockSet blockSet) {
		this.blockSet = blockSet;
	}

	/**
	 * Gets the block set.
	 *
	 * @return The block set.
	 */
	public final UpdateBlockSet getBlockSet() {
		return blockSet;
	}

	/**
	 * Gets the type of segment.
	 *
	 * @return The type of segment.
	 */
	public abstract SegmentType getType();

}