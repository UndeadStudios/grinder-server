package com.grinder.game.content.cluescroll.task

/**
 * Represents various types of clues that a player may encounter
 * while doing a treasure trail.
 */
enum class ClueType {

    /**
     * Clue type where the player has to answer a questions.
     */
    QUESTION,

    /**
     * Clue type where the player has to with an object.
     */
    OBJECT_CLICK,

    /**
     * Clue type where the player has to interact with a NPC.
     */
    NPC_CLICK,

    /**
     * Clue type where the player has to perform an emote.
     */
    DANCE,

    /**
     * Clue type where the player has to dig with a spade at some position.
     */
    DIG,

    /**
     * Clue type where the player has to complete a puzzle.
     */
    PUZZLE
}