package com.grinder.game.message.impl;

import com.grinder.game.message.Message;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.TileGraphic;

/**
 * A {@link Message} sent to the client to display a graphic on a tile for every player.
 *
 * @author Stan van der Bend
 */
public final class SendTileGraphicMessage extends RegionUpdateMessage {

	/**
	 * The graphic to add to the tile.
	 */
	private final Graphic graphic;

	/**
	 * The position offset
	 */
	private final int positionOffset;

	/**
	 * Creates the {@link SendTileGraphicMessage}.
	 *
	 * @param tileGraphic The graphic to add to the tile.
	 * @param positionOffset The offset from the 'base' position.
	 */
	public SendTileGraphicMessage(TileGraphic tileGraphic, int positionOffset) {
		this.graphic = tileGraphic.getGraphic();
		this.positionOffset = positionOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SendTileGraphicMessage) {
			SendTileGraphicMessage other = (SendTileGraphicMessage) obj;
			return graphic.equals(other.graphic) && positionOffset == other.positionOffset;
		}

		return false;
	}

	public int getId() {
		return graphic.getId();
	}

	public int getHeight() {
		return graphic.getHeight();
	}

	public int getDelay() {
		return graphic.getDelay();
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
		int result = graphic.hashCode();
		result = 31 * result + positionOffset;
		return result;
	}

	@Override
	public int priority() {
		return LOW_PRIORITY;
	}

}