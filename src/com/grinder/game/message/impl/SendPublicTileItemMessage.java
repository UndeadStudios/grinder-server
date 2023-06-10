package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.item.Item;

/**
 * A {@link Message} sent to the client to display an item on a tile for every player.
 *
 * @author Major
 */
public final class SendPublicTileItemMessage extends RegionUpdateMessage {

	/**
	 * The index of the player who dropped the item.
	 */
	private final int index;

	/**
	 * The item to add to the tile.
	 */
	private final Item item;

	/**
	 * The position offset
	 */
	private final int positionOffset;

	/**
	 * Creates the SendPublicTileItemMessage.
	 *
	 * @param item The item to add to the tile.
	 * @param index The index of the player who dropped the item.
	 * @param positionOffset The offset from the 'base' position.
	 */
	public SendPublicTileItemMessage(Item item, int index, int positionOffset) {
		this.item = item;
		this.index = index;
		this.positionOffset = positionOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendPublicTileItemMessage) {
			SendPublicTileItemMessage other = (SendPublicTileItemMessage) obj;
			return item.equals(other.item) && index == other.index && positionOffset == other.positionOffset;
		}

		return false;
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
	 * Gets the id of the item.
	 *
	 * @return The id.
	 */
	public int getId() {
		return item.getId();
	}

	/**
	 * Gets the index of the player who dropped the item.
	 *
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the offset from the 'base' position.
	 *
	 * @return The offset.
	 */
	public int getPositionOffset() {
		return positionOffset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = item.hashCode() * prime + index;
		return result * prime + positionOffset;
	}

	@Override
	public int priority() {
		return LOW_PRIORITY;
	}

}