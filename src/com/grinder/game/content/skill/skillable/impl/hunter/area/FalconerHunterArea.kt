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
class FalconerHunterArea : HunterArea(
        Boundary(2363, 2394, 3621, 3572),
        HunterCatchType.DARK_KEBBIT,
        HunterCatchType.DASHING_KEBBIT,
        HunterCatchType.SPOTTED_KEBBIT
)