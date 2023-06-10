package com.grinder.game.entity.updating.seg;

import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.model.Position;

/**
 * A {@link UpdateSegment} which adds a player.
 *
 * @author Graham
 */
public final class AddPlayerSegment extends UpdateSegment {

	/**
	 * The index.
	 */
	private final int index;

	/**
	 * The position.
	 */
	private final Position position;

	/**
	 * Creates the add player segment.
	 *
	 * @param blockSet The block set.
	 * @param index The player's index.
	 * @param position The position.
	 */
	public AddPlayerSegment(UpdateBlockSet blockSet, int index, Position position) {
		super(blockSet);
		this.index = index;
		this.position = position;
	}

	/**
	 * Gets the player's index.
	 *
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the position.
	 *
	 * @return The position.
	 */
	public Position getPosition() {
		return position;
	}

	@Override
	public SegmentType getType() {
		return SegmentType.ADD_MOB;
	}

}