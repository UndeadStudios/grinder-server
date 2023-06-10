package com.grinder.game.entity.object;

import com.grinder.game.World;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.area.RegionRepository;

/**
 * A {@link GameObject} that is a static part of the game world (i.e. is stored in the game resources).
 *
 * @author Major
 */
public final class StaticGameObject extends GameObject {

	/**
	 * Creates the StaticGameObject.
	 *
	 * @param id The id of the StaticGameObject
	 * @param position The {@link Position} of the StaticGameObject.
	 * @param type The type code of the StaticGameObject.
	 * @param orientation The orientation of the StaticGameObject.
	 */
	public StaticGameObject(int id, Position position, int type, int orientation) {
		super(id, position, type, orientation);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.STATIC_OBJECT;
	}

	@Override
	public boolean viewableBy(Player player) {
		return true; // if we call this region chunk, then object is viewable to players
//		final RegionRepository repository = World.getRegions();
//		final RegionCoordinates coordinates = getPosition().getRegionCoordinates();
//		return repository.get(coordinates).contains(this);
	}

}