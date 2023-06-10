package com.grinder.game.message.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.updating.UpdateSegment;
import com.grinder.game.message.Message;
import com.grinder.game.model.Position;

import java.util.List;
/**
 * A {@link Message} sent to the client to synchronize players.
 *
 * @author Graham
 */
public final class PlayerSynchronizationMessage implements Message {

	private final Player player;

	/**
	 * The Position in the last known region.
	 */
	private final Position lastKnownRegion;

	/**
	 * The number of local players.
	 */
	private final int localPlayers;

	/**
	 * The player's position.
	 */
	private final Position position;

	/**
	 * A flag indicating if the region has changed.
	 */
	private final boolean regionChanged;

	/**
	 * The current player's synchronization segment.
	 */
	private final UpdateSegment segment;

	/**
	 * A list of segments.
	 */
	private final List<UpdateSegment> segments;

	/**
	 * Creates the player synchronization message.
	 *
	 * @param lastKnownRegion The last known region.
	 * @param position The player's current position.
	 * @param regionChanged A flag indicating if the region has changed.
	 * @param segment The current player's synchronization segment.
	 * @param localPlayers The number of local players.
	 * @param segments A list of segments.
	 */
	public PlayerSynchronizationMessage(Player player, Position lastKnownRegion, Position position, boolean regionChanged, UpdateSegment segment, int localPlayers, List<UpdateSegment> segments) {
		this.player = player;
		this.lastKnownRegion = lastKnownRegion;
		this.position = position;
		this.regionChanged = regionChanged;
		this.segment = segment;
		this.localPlayers = localPlayers;
		this.segments = segments;
	}

	public Player getPlayer() { return player; }

	/**
	 * Gets the last known region.
	 *
	 * @return The last known region.
	 */
	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Gets the number of local players.
	 *
	 * @return The number of local players.
	 */
	public int getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * Gets the player's position.
	 *
	 * @return The player's position.
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Gets the current player's segment.
	 *
	 * @return The current player's segment.
	 */
	public UpdateSegment getSegment() {
		return segment;
	}

	/**
	 * Gets the synchronization segments.
	 *
	 * @return The segments.
	 */
	public List<UpdateSegment> getSegments() {
		return segments;
	}

	/**
	 * Checks if the region has changed.
	 *
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasRegionChanged() {
		return regionChanged;
	}

}