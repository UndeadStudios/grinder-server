package com.grinder.game.content.dueling

enum class DuelState {
    NONE,
    REQUESTED_DUEL,
    SELECT_DUEL_SETTINGS,
    ACCEPTED_DUEL_SCREEN,
    CONFIRM_SCREEN,
    ACCEPTED_CONFIRM_SCREEN,
    STARTING_DUEL,
    IN_DUEL
}