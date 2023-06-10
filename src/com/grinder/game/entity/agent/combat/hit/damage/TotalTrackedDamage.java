package com.grinder.game.entity.agent.combat.hit.damage;

import com.grinder.util.timing.Stopwatch;

public class TotalTrackedDamage {

    private final Stopwatch stopwatch;
    private int damage;

    /**
     * Create a new {@link TotalTrackedDamage}.
     *
     * @param damage the amount of cached damage.
     */
    public TotalTrackedDamage(int damage) {
        this.damage = damage;
        this.stopwatch = new Stopwatch().reset();
    }

    /**
     * Increments the amount of cached damage.
     *
     * @param damage the amount of cached damage to add.
     */
    public void incrementDamage(int damage) {
        this.damage += damage;
        this.stopwatch.reset();
    }

    /**
     * Gets the stopwatch to time how long the damage is cached.
     *
     * @return the stopwatch to time how long the damage is cached.
     */
    public Stopwatch getStopwatch() {
        return stopwatch;
    }

    /**
     * Gets the amount of cached damage.
     *
     * @return the amount of cached damage.
     */
    public int getDamage() {
        return damage;
    }
}
