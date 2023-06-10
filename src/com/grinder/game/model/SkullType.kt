package com.grinder.game.model

import java.util.concurrent.TimeUnit

/**
 * Represents a status effect that when applied to a player,
 * will cause them to risk losing their items upon death.
 */
enum class SkullType(val iconId: Int) {

    WHITE_SKULL(0),
    RED_SKULL(1);

    /**
     * The duration of the skull effect, after which it is removed.
     */
    val duration: Int
        get() = Math.toIntExact(TimeUnit.MINUTES.toSeconds(60))
}