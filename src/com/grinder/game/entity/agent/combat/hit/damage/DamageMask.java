package com.grinder.game.entity.agent.combat.hit.damage;

/**
 * @author Savions
 */
public enum DamageMask {

    BLOCK(0),
    REGULAR_HIT(1),
    POISON(2),
    YELLOW(3),
    SHIELD_OTHER(5),
    SHIELD(6),
    HEAL(7),
    BLOCK_OTHER(8),
    REGULAR_HIT_OTHER(9),
    VENOM(10);

    private final int spriteId;

    private DamageMask(int spriteId) {
        this.spriteId = spriteId;
    }

    public int spriteId() { return spriteId; }
}