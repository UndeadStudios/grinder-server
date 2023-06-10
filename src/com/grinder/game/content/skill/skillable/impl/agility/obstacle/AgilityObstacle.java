package com.grinder.game.content.skill.skillable.impl.agility.obstacle;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * Represents an agility sequence
 *
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 */
public interface AgilityObstacle {

	/**
	 * Executes an agility sequence
	 *
	 * @param player the player
	 */
	void execute(Player player);
}
