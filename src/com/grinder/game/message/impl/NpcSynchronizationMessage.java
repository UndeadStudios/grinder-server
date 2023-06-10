package com.grinder.game.message.impl;

		import com.grinder.game.entity.agent.player.Player;
		import com.grinder.game.entity.updating.UpdateSegment;
		import com.grinder.game.message.Message;
		import com.grinder.game.model.Position;

		import java.util.List;
/**
 * A {@link Message} sent to the client to synchronize npcs with players.
 *
 * @author Major
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
public final class NpcSynchronizationMessage implements Message {

	private final Player player;

	/**
	 * The amount of local npcs.
	 */
	private final int localNpcs;

	/**
	 * The npc's position.
	 */
	private final Position position;

	private final Position lastKnownRegion;

	/**
	 * A list of segments.
	 */
	private final List<UpdateSegment> segments;

	private final boolean largeScene;

	/**
	 * Creates a new {@link NpcSynchronizationMessage}.
	 *
	 * @param position The position of the {@link com.grinder.game.entity.agent.npc.NPC}.
	 * @param segments The list of segments.
	 * @param localNpcs The amount of local npcs.
	 */
	public NpcSynchronizationMessage(Player player, Position lastKnownRegion, Position position, List<UpdateSegment> segments, int localNpcs, boolean largeScene) {
		this.player = player;
		this.lastKnownRegion = lastKnownRegion;
		this.position = position;
		this.segments = segments;
		this.localNpcs = localNpcs;
		this.largeScene = largeScene;
	}

	/**
	 * Gets the number of local npcs.
	 *
	 * @return The number of local npcs.
	 */
	public int getLocalNpcCount() {
		return localNpcs;
	}

	/**
	 * Gets the npc's position.
	 *
	 * @return The npc's position.
	 */
	public Position getPosition() {
		return position;
	}

	public Position getLastKnownRegion() { return lastKnownRegion; }

	/**
	 * Gets the synchronization segments.
	 *
	 * @return The segments.
	 */
	public List<UpdateSegment> getSegments() {
		return segments;
	}

	public boolean isLargeScene() {
		return largeScene;
	}

	public Player getPlayer() { return player; }
}