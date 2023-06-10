package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.entity.agent.player.Player

/**
 * This is currently not used for implementation!
 *
 * OS-RS Wiki:
 *
 * There are a couple designated Hunter areas that form a habitat housing multiple Hunter creatures.
 * These habitats are a desert, jungle, woodland and a snowy area.
 * These Hunter areas are not the only locations in Gielinor that house Hunter creatures.
 * There are also several other locations housing a single species.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
enum class HunterAreas(vararg area: HunterArea) {
    ;
    companion object {
        fun contains(player: Player) : Boolean {
            val position = player.position
            return if (position.x in 3119..3172 || position.y in 3754..3792) true
            else position.x in 2688..2751 && position.y >= 3741 && position.y <= 3810
        }
    }
}