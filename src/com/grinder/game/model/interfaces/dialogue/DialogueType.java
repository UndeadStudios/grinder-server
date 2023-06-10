package com.grinder.game.model.interfaces.dialogue;

/**
 * Represents a type of dialogue.
 *
 * @author relex lawl
 */

public enum DialogueType {

    /*
     * Gives variable options for a player to choose.
     */
    OPTION,

    /*
     * Gives a statement.
     */
    STATEMENT,

    /*
     * Gives a statement without a continue dialogue button.
     */
    TITLED_STATEMENT_NO_CONTINUE,

    /*
     * Gives a dialogue said by an npc.
     */
    NPC_STATEMENT,

    /*
     * Gives a dialogue with an item model next to it.
     */
    ITEM_STATEMENT,

    /*
     * Gives a dialogue with an item model next to it without a title.
     */
    ITEM_STATEMENT_NO_HEADER,

    /*
     * Gives a dialogue said by a player.
     */
    PLAYER_STATEMENT,

    QUEST_STAGE;
}
