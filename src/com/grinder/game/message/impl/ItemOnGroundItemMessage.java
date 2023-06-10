package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.Position;

import java.util.OptionalInt;

/**
 * A {@link Message} sent by the client when an item is used on a ground item.
 *
 * @author Stan van der Bend
 */
public final class ItemOnGroundItemMessage extends InventoryItemMessage {

	/**
	 * The ground item id the item was used on.
	 */
	private final int groundItemId;

	/**
	 * The position of the ground item.
	 */
	private final Position position;

	/**
	 * Creates an item on object message.
	 *
	 * @param interfaceId The interface id.
	 * @param itemId The item id.
	 * @param itemSlot The slot the item is in.
	 * @param groundItemId The object id.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public ItemOnGroundItemMessage(int interfaceId, int itemId, int itemSlot, int groundItemId, int x, int y) {
		super(OptionalInt.empty(), interfaceId, itemId, itemSlot);
		this.groundItemId = groundItemId;
		position = new Position(x, y);
	}

	/**
	 * Gets the ground item id.
	 *
	 * @return The ground item id.
	 */
	public int getGroundItemId() {
		return groundItemId;
	}

	/**
	 * Gets the position of the ground item.
	 *
	 * @return The position.
	 */
	public Position getPosition() {
		return position;
	}

}