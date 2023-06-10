package com.grinder.game.content.cluescroll

import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty
import com.grinder.game.content.cluescroll.task.ClueType

class ClueScroll(
        val difficulty: ScrollDifficulty,
        val taskType: ClueType,
        val clueGuide: ClueGuide
) {
    override fun toString(): String {
        return "ClueScroll [difficulty=$difficulty, taskType=$taskType, clueGuide=$clueGuide]"
    }
}