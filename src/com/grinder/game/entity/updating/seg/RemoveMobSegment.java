package com.grinder.game.entity.updating.seg;

import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.UpdateSegment;

/**
 * A {@link UpdateSegment} which removes a mob.
 *
 * @author Graham
 */
public final class RemoveMobSegment extends UpdateSegment {

	/**
	 * An empty {@link UpdateBlockSet}.
	 */
	private static final UpdateBlockSet EMPTY_BLOCK_SET = new UpdateBlockSet();

	/**
	 * Creates the remove mob segment.
	 */
	public RemoveMobSegment() {
		super(EMPTY_BLOCK_SET);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.REMOVE_MOB;
	}

}