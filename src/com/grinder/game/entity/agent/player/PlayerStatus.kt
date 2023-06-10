package com.grinder.game.entity.agent.player

import java.util.*

enum class PlayerStatus(private val identifier: String = "") {

    NONE,
    AWAY_FROM_KEYBOARD("being AFK"),
    SHOPPING("shopping"),
    TRADING("trading"),
    DUELING("dueling"),
    BANKING("banking"),
    PRICE_CHECKING("price checking"),
    DICING("dicing"),
    IN_TUTORIAL("in the tutorial"),
    IN_RANDOM_EVENT("in a random event"),
    TRANSFORMED("transformed"),
    JAILED("jailed"),
    BLOCK_ALL_BUT_TALKING;

    fun blockTeleportation() = this == TRADING
            || this == BANKING
            || this == PRICE_CHECKING
            || this == AWAY_FROM_KEYBOARD
            || this == IN_TUTORIAL
            || this == IN_RANDOM_EVENT
            || this == TRANSFORMED
            || this == JAILED

    fun optionalIdentifier() = if(identifier.isEmpty())
        Optional.empty<String>()
    else
        Optional.of(identifier)
}