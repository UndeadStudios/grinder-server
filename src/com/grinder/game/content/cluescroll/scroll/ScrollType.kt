package com.grinder.game.content.cluescroll.scroll

import com.grinder.util.ItemID

enum class ScrollType(
        val scrollDifficulty: ScrollDifficulty,
        val scrollType: Int,
        val scrollRewardBox: Int,
        val scrollRewardCasket: Int,
        val scrollCompletionCasket: Int
) {

    EASY(ScrollDifficulty.EASY,
            2677,
            3519,
            2717,
        ItemID.REWARD_CASKET_EASY_),
    MEDIUM(ScrollDifficulty.MEDIUM,
            2801,
            3593,
            2806,
        ItemID.REWARD_CASKET_MEDIUM_),
    HARD(ScrollDifficulty.HARD,
            2722,
            3531,
            2728,
        ItemID.REWARD_CASKET_HARD_),
    ELITE(ScrollDifficulty.ELITE,
            ScrollConstants.ITEM_ELITE_SCROLL,
            ScrollConstants.ITEM_ELITE_SCROLL_BOX,
            ScrollConstants.ITEM_ELITE_SCROLL_REWARD_CASKET,
            ScrollConstants.ITEM_ELITE_SCROLL_COMPLETION_CASKET);

    companion object {

        @JvmStatic
		fun forDifficulty(difficulty: ScrollDifficulty) = values()
                .find { it.scrollDifficulty == difficulty }

        @JvmStatic
		fun forScrollRewardCasket(casket: Int) = values()
                .find { it.scrollRewardCasket == casket }

        @JvmStatic
		fun forScrollType(scrollType: Int) = values()
                .find { it.scrollType == scrollType }

        @JvmStatic
		fun forScrollBox(scrollBox: Int) = values()
                .find { it.scrollRewardBox == scrollBox }
    }
}