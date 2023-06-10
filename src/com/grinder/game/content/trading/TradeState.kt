package com.grinder.game.content.trading

/**
 * Represents the possible states in a trade interaction.
 *
 * @author Swiffy
 */
enum class TradeState {
    NONE,
    REQUESTED_TRADE,
    TRADE_SCREEN,
    ACCEPTED_TRADE_SCREEN,
    CONFIRM_SCREEN,
    ACCEPTED_CONFIRM_SCREEN
}