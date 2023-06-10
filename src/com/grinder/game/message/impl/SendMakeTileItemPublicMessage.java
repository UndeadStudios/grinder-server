package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.item.Item;

/**
 * A {@link Message} sent to the client that adds an item to a tile.
 *
 * @author Major
 */
public final class SendMakeTileItemPublicMessage extends RegionUpdateMessage {

	/**
	 * The item to add to the tile.
	 */
	private final Item item;

	/**
	 * The position offset
	 */
	private final int positionOffset, ownerIndex;

	/**
	 * Creates the SendTileItemMessage.
	 *
	 * @param item The item to add to the tile.
	 * @param positionOffset The offset from the 'base' position.
	 */
	public SendMakeTileItemPublicMessage(Item item, int positionOffset, int ownerIndex) {
		this.item = item;
		this.positionOffset = positionOffset;
		this.ownerIndex = ownerIndex;
	}

	/**
	 * Gets the id of the item.
	 *
	 * @return The id.
	 */
	public int getId() {
		return item.getId();
	}

	/**
	 * Gets the amount of the item.
	 *
	 * @return The amount.
	 */
	public int getAmount() {
		return item.getAmount();
	}

	/**
	 * Gets the offset from the 'base' position.
	 *
	 * @return The offset.
	 */
	public int getPositionOffset() {
		return positionOffset;
	}

	/**
	 * Gets the owner index of the player
	 * @return owner index of player
	 */
	public int getOwnerIndex() { return ownerIndex; }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendMakeTileItemPublicMessage) {
			SendMakeTileItemPublicMessage other = (SendMakeTileItemPublicMessage) obj;
			return item.equals(other.item) && positionOffset == other.positionOffset && ownerIndex == other.ownerIndex;
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return item.hashCode() * prime + positionOffset + ownerIndex;
	}

	@Override
	public int priority() {
		return LOW_PRIORITY;
	}

}