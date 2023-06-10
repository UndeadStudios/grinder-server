package com.grinder.game.content.skill.skillable.impl.farming

import com.grinder.game.model.item.Item

/**
 * Represents types of compost that can be added to a [FarmingPatch].
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-27
 */
enum class CompostType(var item: Item?) {
    NONE(null),
    COMPOST(Item(6032)),
    SUPERCOMPOST(Item(6034));
}