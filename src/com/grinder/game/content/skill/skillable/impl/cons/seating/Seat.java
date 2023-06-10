package com.grinder.game.content.skill.skillable.impl.cons.seating;

import com.grinder.game.entity.agent.player.Player;

/**
 * @author Simplex
 * @since Apr 08, 2020
 */
public interface Seat {

    void stand(Player player);

    int getEatAnimation(Player player);

    void restore(Player player);
}
