package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse;

/**
 * @author Zach (zach@findzach.com)
 * @since 12/21/2020
 *
 * don't want adjust the order of the first 3 elements.
 */
public enum BirdHouseState {

    // can remove ()
    BUILT_EMPTY(0),
    BUILT_COLLECTING(1),
    BUILT_FULL(2),
    BUILT_FULL_COLLECTED(2),
    NOT_BUILT(-1); // no need for semicolon at the end

    public final int index;

    BirdHouseState(int index) {
        this.index = index;
    }
}
