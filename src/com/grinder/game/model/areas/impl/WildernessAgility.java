package com.grinder.game.model.areas.impl;

import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;

/**
 * Handles the ice troll king mountain
 * 
 * @author 2012
 */
public class WildernessAgility extends WildernessArea {

	/**
	 * Wilderness agility course
	 */
	public WildernessAgility() {
		super(new Boundary(2989, 3012, 3935, 3967));
	}

	@Override
	public boolean contains(Position position) {
		return super.contains(position);
	}

	@Override
	public String toString() {
		return "Wilderness Agility Course";
	}
}
