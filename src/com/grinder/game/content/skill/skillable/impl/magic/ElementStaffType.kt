package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item
import java.util.*

/**
 * A set of constants representing the staves that can be used in place of
 * runes.
 *
 * @author lare96
 *
 * @param staves the staves that can be used in place of runes.
 * @param runes  the runes that the staves can be used for.
 */
enum class ElementStaffType(val staves: IntArray, private val runes: IntArray) {


    AIR(intArrayOf(1381, 1397, 1405, 11998, 12000, 20730, 20733, 20736, 20739, 15718), intArrayOf(556)),
    WATER(intArrayOf(1383, 1395, 1403, 6562, 6563, 11787, 11789, 12795, 12796, 20730, 20733, 25574, 21006), intArrayOf(555)),
    EARTH(intArrayOf(1385, 1399, 1407, 3053, 3054, 6562, 6563, 20736, 20739, 21198, 21200), intArrayOf(557)),
    FIRE(intArrayOf(1387, 1393, 1401, 3053, 3054, 11787, 11789, 12795, 12796, 11998, 12000, 20714, 20716, 21198, 21200), intArrayOf(554)),
    MUD(intArrayOf(6562, 6563), intArrayOf(555, 557)),
    LAVA(intArrayOf(3053, 3054, 21198, 21200), intArrayOf(554, 557)),
    MIST(intArrayOf(20730, 20733), intArrayOf(555, 556)),
    DUST(intArrayOf(20736, 20739), intArrayOf(556, 557)),
    SMOKE(intArrayOf(11998, 12000), intArrayOf(554, 556)),
    STEAM(intArrayOf(11787, 11789, 12795, 12796), intArrayOf(554, 555));

    companion object {

        /**
         * Suppress items in the argued array if any of the items match the runes
         * that are represented by the staff the argued player is wielding.
         *
         * @param player        the player to suppress runes for.
         * @param runesRequired the runes to suppress.
         * @return the new array of items with suppressed runes removed.
         */
        @JvmStatic
        fun suppressRunes(player: Player, runesRequired: Array<Item?>): Array<Item?> {
            if (player.combat.uses(WeaponInterface.STAFF)) {
                for (m in values()) {
                    if (player.equipment.containsAny(*m.staves)) {
                        for (id in m.runes) {
                            for (i in runesRequired.indices) {
                                if (runesRequired[i] != null && runesRequired[i]!!.id == id) {
                                    runesRequired[i] = null
                                }
                            }
                        }
                    }
                }
                return runesRequired
            }
            return runesRequired
        }

        private fun getStaffForRune(runeId: Int): Optional<ElementStaffType> {
            return Optional.ofNullable(values().firstOrNull { it.runes.contains(runeId) })
        }

        fun hasStaffForRune(player: Player, runeId: Int): Boolean {
            val staff = getStaffForRune(runeId)
            return if (staff.isEmpty)
                false
            else
                staff.get().staves.any { player.equipment.contains(it) }
        }
    }
}