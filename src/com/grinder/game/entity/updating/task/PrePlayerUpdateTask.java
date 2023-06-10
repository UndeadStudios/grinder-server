package com.grinder.game.entity.updating.task;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.updating.UpdateTask;
import com.grinder.game.message.impl.ClearRegionMessage;
import com.grinder.game.message.impl.GroupedRegionUpdateMessage;
import com.grinder.game.message.impl.RegionChangeMessage;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.area.RegionRepository;
import com.grinder.game.model.areas.InstanceManager;

/**
 * A {@link UpdateTask} which does pre-synchronization work for the specified {@link Player}.
 *
 * @author Graham
 * @author Major
 */
public final class PrePlayerUpdateTask extends UpdateTask {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the {@link PrePlayerUpdateTask} for the specified {@link Player}.
	 *
	 * @param player The Player.
	 */
	public PrePlayerUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		Position old = player.getPosition().clone();
		player.preSequence();

		boolean local = true;

		if (player.isTeleporting())
			local = false;

		Position position = player.getPosition();

		boolean hasLastKnownRegion = player.hasLastKnownRegion();

		if (!hasLastKnownRegion || isRegionUpdateRequired()) {
			player.setRegionChanged(true);
			local = false;

			player.setLastKnownRegion(position.clone());
			if (InstanceManager.isInConstructedMap(position)) {
				player.getPacketSender().constructDynamicMapRegion();
			} else {
				if (ClippedMapObjects.USE_NEW_OBJECT_UPDATES)
					player.send(new RegionChangeMessage(position));
				else
					player.getPacketSender().sendMapRegion(position);
			}

		}
	}

	/**
	 * Checks if a region update is required.
	 *
	 * @return {@code true} if a Region update is required, {@code false} if not.
	 */
	private boolean isRegionUpdateRequired() {
		Position current = player.getPosition();
		Position last = player.getLastKnownRegion();

		int deltaX = current.getLocalX(last);
		int deltaY = current.getLocalY(last);

		return deltaX <= Position.MAX_DISTANCE || deltaX >= Region.VIEWPORT_WIDTH - Position.MAX_DISTANCE - 1
			|| deltaY <= Position.MAX_DISTANCE || deltaY >= Region.VIEWPORT_WIDTH - Position.MAX_DISTANCE - 1;
	}

}