package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.RegionCoordinates;

import java.util.List;
import java.util.Set;


/**
 * A {@link Message} sent to the client that contains multiple
 *
 * @author Major
 */
public final class GroupedRegionUpdateMessage implements Message {

	/**
	 * The last known region Position of the Player.
	 */
	private final Position lastKnownRegion;

	/**
	 * The Set of RegionUpdateMessages to be sent.
	 */
	private final List<RegionUpdateMessage> messages;

	/**
	 * The Position of the Region being updated.
	 */
	private final Position region;

	/**
	 * Creates the GroupedRegionUpdateMessage.
	 *
	 * @param lastKnownRegion The last known region {@link Position} of the Player.
	 * @param coordinates The {@link RegionCoordinates} of the Region being updated.
	 * @param messages The {@link Set} of {@link RegionUpdateMessage}s.
	 */
	public GroupedRegionUpdateMessage(Position lastKnownRegion, RegionCoordinates coordinates,
	                                  List<RegionUpdateMessage> messages) {
		this.lastKnownRegion = lastKnownRegion;
		region = new Position(coordinates.getAbsoluteX(), coordinates.getAbsoluteY());
		this.messages = messages;
	}

	/**
	 * Gets the {@link Position} of the Player.
	 *
	 * @return The Position.
	 */
	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Gets the {@link Set} of {@link RegionUpdateMessage}s.
	 *
	 * @return The Set.
	 */
	public List<RegionUpdateMessage> getMessages() {
		return messages;
	}

	/**
	 * Gets the {@link Position} of the Region these updates affect.
	 *
	 * @return The Position.
	 */
	public Position getRegionPosition() {
		return region;
	}

}