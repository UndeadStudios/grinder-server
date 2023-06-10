package com.grinder.game.content.gambling.flower_poker

enum class FlowersData(var objectId: Int, var itemId: Int) {

    PASTEL_FLOWERS(objectId = 2980, itemId = 2460),
    RED_FLOWERS(objectId = 2981, itemId = 2462),
    BLUE_FLOWERS(objectId = 2982, itemId = 2464),
    YELLOW_FLOWERS(objectId = 2983, itemId = 2466),
    PURPLE_FLOWERS(objectId = 2984, itemId = 2468),
    ORANGE_FLOWERS(objectId = 2985, itemId = 2470),
    RAINBOW_FLOWERS(objectId = 2986, itemId = 2472);

    companion object {
        @JvmStatic
        fun generate() = values().random()
    }
}