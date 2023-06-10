package com.grinder.game.model.area.update;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.message.impl.*;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;

import java.util.Optional;

/**
 * A {@link UpdateOperation} for {@link ItemOnGround}s.
 *
 * @author Major
 */
public final class ItemUpdateOperation extends DynamicUpdateOperation<ItemOnGround> {

	/**
	 * Creates the ItemUpdateOperation.
	 *
	 * @param region The {@link Region} the type occurred in. Must not be {@code null}.
	 * @param type The {@link EntityUpdateType}. Must not be {@code null}.
	 * @param item The modified {@link ItemOnGround}. Must not be {@code null}.
	 */
	public ItemUpdateOperation(Region region, EntityUpdateType type, ItemOnGround item) {
		super(region, type, item);
	}

	@Override
	protected RegionUpdateMessage change(int offset) {
		return new UpdateTileItemMessage(entity.getItem(), entity.getOldAmount(), offset);
	}

	@Override
	protected RegionUpdateMessage add(int offset) {
		switch (type) {
			case MAKE_PUBLIC:
				int ownerIndex = -1;
				if (entity.findOwner().isPresent()) {
					String name = entity.findOwner().get();
					Optional<Player> p = World.findPlayerByName(name);
					if (p.isPresent())
						ownerIndex = p.get().getIndex();
				}
				return new SendMakeTileItemPublicMessage(entity.getItem(), offset, ownerIndex);

			default:
				return new SendTileItemMessage(entity.getItem(), offset);
		}
//		if(entity.getState() == ItemOnGround.State.SEEN_BY_PLAYER){
//			// todo: make this distinction
//		}
	}

	@Override
	protected RegionUpdateMessage remove(int offset) {
		return new RemoveTileItemMessage(entity.getItem(), offset);
	}

}