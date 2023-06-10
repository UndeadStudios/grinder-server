package com.grinder.game.entity.agent.combat.attack;

import java.util.*;

/**
 * A set of constants representing the three different types of combat that can
 * be used.
 *
 * @author lare96
 * @author Stan van der Bend
 */
public enum AttackType implements AttackTypeProvider {

    MELEE,
    RANGED,
    MAGIC,
    SPECIAL;

    @Override
    public AttackTypeChance[] provide() {
        return new AttackTypeChance[]{new AttackTypeChance(100.0f, this)};
    }

    /**
     * Creates a builder for configuring an {@link AttackTypeProvider}.
     *
     * @return a new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates an {@link AttackTypeProvider} that returns one of the given types
     * with an equal probability.
     *
     * @param attackTypes an array of {@link AttackType types}.
     *
     * @return a {@link AttackTypeProvider} composed of equally
     *         probable {@link AttackTypeChance chances}.
     */
    public static AttackTypeProvider equalChances(AttackType... attackTypes){
        return () -> new AttackTypeChance[]{new AttackTypeChance(100.0f / attackTypes.length, attackTypes)};
    }

    public static AttackTypeProvider chances(AttackTypeChance... attackTypeChances){
        return () -> attackTypeChances;
    }

    public static final class Builder {

        private final ArrayList<AttackTypeChance> typeChances = new ArrayList<>();

        public Builder add(float chance, AttackType... types){

            final AttackTypeChance typeChance = new AttackTypeChance(chance, types);

            typeChances.add(typeChance);

            return Builder.this;
        }

        /**
         * Adds the specified types with a 33.3/100 probability.
         *
         * @param types the {@link AttackType} that share the probability.
         *
         * @return this {@link Builder} instance.
         */
        public Builder add(Odds odds, AttackType... types){
            return add(odds.chance, types);
        }

        public AttackTypeProvider build(){
            Collections.shuffle(typeChances);
            return () -> typeChances.toArray(new AttackTypeChance[]{});
        }

        public enum Odds {

            ONE_THIRD(33.3F),
            TWO_THIRD(66.6F),
            ONE_FOURTH(25.0F),
            TWO_FOURTH(50.0F),
            THREE_FOURTH(75.0F),
            ONE_FIFTH(20.0F),
            TWO_FIFTH(40.0F),
            THREE_FIFTH(60.0F),
            FOUR_FIFTH(80.0F),
            ONE_TENTH(10.0F),
            TWO_TENTH(20.0F),
            THREE_TENTH(30.0F),
            FOUR_TENTH(40.0F),
            FIVE_TENTH(50.0F),
            SIX_TENTH(60.0F),
            SEVEN_TENTH(70.0F),
            EIGHT_TENTH(80.0F),
            NINE_TENTH(90.0F),
            TEN_TENTH(100.0F);

            private final float chance;

            Odds(float chance) {
                this.chance = chance;
            }
        }

    }
}