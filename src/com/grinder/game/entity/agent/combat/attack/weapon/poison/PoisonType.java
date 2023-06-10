package com.grinder.game.entity.agent.combat.attack.weapon.poison;

/**
 * Holds all of the different strengths of poisons.
 *
 * @author lare96
 */
public enum PoisonType {

    WEAK(2),

    MILD(4),
    EXTRA(5),
    SUPER(6),
    NEX(8),
    VENOM(12);

    /**
     * The starting damage for this poison type.
     */
    private final int damage;

    /**
     * Create a new {@link PoisonType}.
     *
     * @param damage
     *            the starting damage for this poison type.
     */
    PoisonType(int damage) {
        this.damage = damage;
    }

    /**
     * Gets the starting damage for this poison type.
     *
     * @return the starting damage for this poison type.
     */
    public int getDamage() {
        return damage;
    }
}
