package com.grinder.game.entity.agent.movement;

import com.grinder.game.entity.agent.Agent;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-23
 */
public interface FollowController {

    void onTargetOutOfReach(Agent target);

}
