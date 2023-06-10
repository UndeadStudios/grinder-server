package com.grinder.game.entity.object;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link GameObject} that is loaded dynamically, usually for specific Players.
 *
 * @author Major
 */
public final class DynamicGameObject extends GameObject {

	/**
	 * Creates a DynamicGameObject that is visible only to {@link Player}s specified later.
	 *
	 * @param id The id of the DynamicGameObject
	 * @param position The {@link Position} of the DynamicGameObject.
	 * @param type The type of the DynamicGameObject.
	 * @param orientation The orientation of the DynamicGameObject.
	 * @return The DynamicGameObject.
	 */
	public static DynamicGameObject createLocal(int id, Position position, int type, int orientation) {
		return new DynamicGameObject(id, position, type, orientation, false);
	}

	/**
	 * Creates a DynamicGameObject that is always visible.
	 *
	 * @param id The id of the DynamicGameObject
	 * @param position The {@link Position} of the DynamicGameObject.
	 * @param type The type of the DynamicGameObject.
	 * @param orientation The orientation of the DynamicGameObject.
	 * @return The DynamicGameObject.
	 */
	public static DynamicGameObject createPublic(int id, Position position, int type,
			int orientation) {
		return new DynamicGameObject(id, position, type, orientation, true);
	}

	public static DynamicGameObject createPublic(int id, Position position) {
		return createPublic(id, position, 10, 0);
	}

	/**
	 * The flag indicating whether or not this DynamicGameObject is visible to every player.
	 */
	private final boolean alwaysVisible;

	public boolean isSpawned;

	private  GameObject originalObject;
	/**
	 * The Set of Player usernames that can view this DynamicGameObject.
	 */
	private final Set<String> players = new HashSet<>(); // TODO more appropriate type?

	/**
	 * Creates the DynamicGameObject.
	 *
	 * @param id The id of the DynamicGameObject
	 * @param position The {@link Position} of the DynamicGameObject.
	 * @param type The type of the DynamicGameObject.
	 * @param orientation The orientation of the DynamicGameObject.
	 * @param alwaysVisible The flag indicates whether or not this DynamicGameObject is visible to every player.
	 */
	private DynamicGameObject(int id, Position position, int type, int orientation,
			boolean alwaysVisible) {
		super(id, position.clone(), type, orientation);
		this.alwaysVisible = alwaysVisible;
	}

	public DynamicGameObject(int id, GameObject other, boolean alwaysVisible) {
		this(id, other.getPosition(), other.getObjectType(), other.getFace(), alwaysVisible);
	}

	/**
	 * Adds this DynamicGameObject to the view of the specified {@link Player}.
	 *
	 * @param player The Player.
	 * @return {@code true} if this DynamicGameObject was not already visible to the specified Player.
	 */
	public boolean addTo(Player player) {
		return players.add(player.getUsername());
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.DYNAMIC_OBJECT;
	}

	/**
	 * Removes this DynamicGameObject from the view of the specified {@link Player}.
	 *
	 * @param player The Player.
	 * @return {@code true} if this DynamicGameObject was visible to the specified Player.
	 */
	public boolean removeFrom(Player player) {
		return players.remove(player.getUsername());
	}

	@Override
	public boolean viewableBy(Player player) {
		return alwaysVisible || players.contains(player.getUsername());
	}

	public GameObject getOriginalObject() {
		return originalObject;
	}

	public void setOriginalObject(GameObject originalObject) {
		this.originalObject = originalObject;
	}
	public Set<String> getPlayers() {
		return players;
	}

	public boolean isAlwaysVisible() {
		return alwaysVisible;
	}

	public void despawn() {
		despawn(true);
	}

	public void despawn(boolean modifyClipping) {
		if (!isSpawned) return;
		World.deSpawn(this);
		if (modifyClipping)
			CollisionManager.removeObjectClipping(this);

		//if original object exists, return clipping of original object
		if (getOriginalObject() != null)
			CollisionManager.addObjectClipping(getOriginalObject());
	}
}