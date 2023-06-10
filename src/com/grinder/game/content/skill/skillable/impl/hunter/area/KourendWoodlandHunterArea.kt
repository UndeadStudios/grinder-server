package com.grinder.game.content.skill.skillable.impl.hunter.area

import com.grinder.game.content.skill.skillable.impl.hunter.HunterArea
import com.grinder.game.content.skill.skillable.impl.hunter.HunterCatchType
import com.grinder.game.model.Boundary

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
class KourendWoodlandHunterArea : HunterArea(
        Boundary(1471, 1599, 3519, 3392),
        HunterCatchType.COPPER_LONGTAIL,
        HunterCatchType.RUBY_HARVEST,
        HunterCatchType.SPOTTED_KEBBIT
)