package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.RegionCoordinates;

/**
 * A {@link Message} sent to the client to remove all spawned objects and items from a Region.
 *
 * @author Major
 */
public final class ClearRegionMessage implements Message {

	/**
	 * The Position of the Player.
	 */
	private final Position player;

	/**
	 * The Position in the Region being cleared.
	 */
	private final Position region;

	/**
	 * Creates the ClearRegionMessage.
	 *
	 * @param player The {@link Position} of the Player this {@link Message} is being sent to.
	 * @param region The {@link RegionCoordinates} of the Region being cleared.
	 */
	public ClearRegionMessage(Position player, RegionCoordinates region) {
		this.player = player;
		this.region = new Position(region.getAbsoluteX(), region.getAbsoluteY());
	}

	/**
	 * Gets the {@link Position} of the Player this {@link Message} is being sent to..
	 *
	 * @return The Position.
	 */
	public Position getPlayerPosition() {
		return player;
	}

	/**
	 * Gets the {@link Position} of the Region being cleared.
	 *
	 * @return The Position.
	 */
	public Position getRegionPosition() {
		return region;
	}

}