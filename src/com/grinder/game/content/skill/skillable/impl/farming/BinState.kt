package com.grinder.game.content.skill.skillable.impl.farming

enum class BinState(var objectId: Int, var childIndex: Int) {
    EMPTY(7808, 0),
    PARTIALLY_FILLED(3830, 1),
    PARTIALLY_FILLED_SUPER(3830, 1),
    FILLED(3848, 15),
    FILLED_SUPER(3848, 15),
    CLOSED(3849, 31),
    CLOSED_SUPER(3849, 32),
    COMPOSTED(3851, 16),
    COMPOSTED_SUPER(3851, 16);
}