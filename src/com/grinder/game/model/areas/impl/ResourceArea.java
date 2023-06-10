package com.grinder.game.model.areas.impl;

import com.grinder.game.model.Boundary;

public class ResourceArea extends WildernessArea {

	/**
	 * Wilderness agility course
	 */
	public ResourceArea() {
		super(new Boundary(3174, 3196, 3924, 3944));
	}

	@Override
	public String toString() {
		return "Wilderness Resource Area";
	}
}
