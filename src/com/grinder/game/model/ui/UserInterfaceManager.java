package com.grinder.game.model.ui;

import com.grinder.game.entity.agent.player.Player;

import java.util.HashMap;

/**
 * 
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class UserInterfaceManager {

	public static final HashMap<Integer, UserContainerInterface> CONTAINER = new HashMap<>();

	public static boolean handleContainer(Player player, int id, int item, int slot, int option) {
		if(CONTAINER.get(id) == null) {
			return false;
		}
		return CONTAINER.get(id).handleOption(player, item, slot, option);
	}
}
