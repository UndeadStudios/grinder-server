package com.grinder.game.entity.agent.player.bot;

import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * A class that handles the {@link BotPlayer}'s actions.
 * 
 * @author Blake
 *
 */
public class BotActions {

	/**
	 * Gets the mob with the specified id.
	 * 
	 * @param id
	 *            The id.
	 * @return The mob.
	 */
	public static NPC getMob(int id) {
		for (NPC npc : World.getNpcs()) {
			if (npc == null) {
				continue;
			}

			if (npc.getId() == id) {
				return npc;
			}
		}

		return null;
	}

	/**
	 * Gets the mob with the specified id and position.
	 * 
	 * @param id
	 *            The id.
	 * @param position
	 *            The position.
	 * @return The mob.
	 */
	public static NPC getMob(int id, Position position) {
		for (NPC npc : World.getNpcs()) {
			if (npc == null) {
				continue;
			}

			if (npc.getId() == id && npc.getPosition().equals(position)) {
				return npc;
			}
		}

		return null;
	}

	/**
	 * Clicks the specified npc option.
	 * 
	 * @param player
	 *            The player.
	 * @param mob
	 *            The mob.
	 * @param option
	 *            The option.
	 */
	public static void clickNpc(Player player, NPC mob, int option) {

	}

	/**
	 * Clicks the object at the specified location.
	 * 
	 * @param player
	 *            The player.
	 * @param option
	 *            The option.
	 * @param id
	 *            The id.
	 * @param position
	 *            The position
	 */
	public static void clickObject(BotPlayer player, int option, int id, Position location) {

	}

}
