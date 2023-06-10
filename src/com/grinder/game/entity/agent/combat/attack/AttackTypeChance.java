package com.grinder.game.entity.agent.combat.attack;

import com.grinder.util.Misc;

import java.util.Optional;

/**
 * Represents a {@link AttackType[]} wrapper that adds
 * a probability component for the odds to roll one
 * of the types contained in {@link #attackTypes}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
public class AttackTypeChance {

    private final AttackType[] attackTypes;

    private final float probability;

    /**
     * Creates a new {@link AttackTypeChance}.
     *
     * @param attackTypes the {@link AttackType} objects with the provided probability.
     * @param probability the probability of getting any one of the attack types (expressed in percentage of 100).
     */
    public AttackTypeChance(float probability, AttackType... attackTypes) {
        this.probability = probability;
        this.attackTypes = attackTypes;
    }

    /**
     * Return one of the {@link AttackType} contained
     * within the {@link #attackTypes} with probability {@link #probability}.
     *
     * @return an {@link Optional<AttackType>} that may or may not contain one of
     *          the {@link #attackTypes}.
     */
    public Optional<AttackType> roll(){

        if(Misc.randomChance(probability))
            return Optional.of(Misc.random(attackTypes));

        return Optional.empty();
    }

    /**
     * Gets the {@link #attackTypes}.
     *
     * @return an array of {@link AttackType types}.
     */
    public AttackType[] getAttackTypes() {
        return attackTypes;
    }
}
