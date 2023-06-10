package com.grinder.game.entity.agent.combat.attack;

/**
 * Represents a simple single-method interface that
 * returns an array of {@link AttackTypeChance probailties}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
public interface AttackTypeProvider {

    /**
     * Returns an array of probable attack types,
     *
     * @return an array of {@link AttackTypeChance probabilities}
     */
    AttackTypeChance[] provide();

}
