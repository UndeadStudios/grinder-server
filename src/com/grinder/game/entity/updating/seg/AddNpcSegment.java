package com.grinder.game.entity.updating.seg;

import com.grinder.game.entity.updating.SegmentType;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;

/**
 * A {@link UpdateSegment} that adds an npc.
 *
 * @author Major
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
public final class AddNpcSegment extends UpdateSegment {

	private final int index;
	private final int npcId;
	private final Position position;
	private final Direction facing;
	private final boolean largeScene;

	public AddNpcSegment(UpdateBlockSet blockSet, int index, Position position, int npcId, Direction facing, boolean largeScene) {
		super(blockSet);
		this.index = index;
		this.position = position;
		this.npcId = npcId;
		this.facing = facing;
		this.largeScene = largeScene;
	}

	public int getIndex() {
		return index;
	}

	public int getNpcId() {
		return npcId;
	}

	public Position getPosition() {
		return position;
	}

	public Direction getFacing() {
		return facing;
	}

	public boolean isLargeScene() {
		return largeScene;
	}

	@Override
	public SegmentType getType() {
		return SegmentType.ADD_MOB;
	}

}