package com.grinder.game.entity.agent.player

/**
 * Represents contracts that determine how a player should be removed from the world.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-17
 */
enum class LogoutPolicy {

    /**
     * Server immediately adds player to removal queue,
     * i.e. queueing de-registration from the game world.
     */
    IMMEDIATE,

    /**
     * Used when channel is terminated because of an inactive channel,
     * or an exception occurring in the channel handler chain.
     *
     * This action may be blocked if the player is in a busy state, e.g. combat.
     */
    SAFE,

    /**
     * Used when a player-inactive packet is read from the client (opcode=202).
     *
     * This action will auto-logout the client after [Player.FORCE_DISCONNECT_TICKS] ticks.
     * This has the same priority as [SAFE] but it can also be canceled
     * if a non-idle packet is read from the client.
     */
    IDLE;

    fun canRemoveInCombat() = this == IMMEDIATE

    fun canBeCanceled() = this == IDLE
}