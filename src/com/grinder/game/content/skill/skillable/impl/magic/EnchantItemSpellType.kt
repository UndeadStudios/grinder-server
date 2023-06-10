package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item
import java.util.*

/**
 * An enumerated type that represents an enchanting spell.
 *
 * @author Blake
 */
internal enum class EnchantItemSpellType(val spellId: Int, val level: Int, val experience: Int, vararg runes: Item) {

    SAPPHIRE(1155, 7, 18, Item(555, 1), Item(564, 1)),
    EMERALD(1165, 27, 37, Item(556, 3), Item(564, 1)),
    RUBY(1176, 49, 59, Item(554, 5), Item(564, 1)),
    DIAMOND(1180, 57, 67, Item(557, 10), Item(564, 1)),
    DRAGONSTONE(1187, 68, 78, Item(555, 15), Item(557, 15), Item(564, 1)),
    ONYX(6003, 87, 97, Item(557, 20), Item(554, 20), Item(564, 1)),
    ZENYTE(30332, 93, 110, Item(566, 20), Item(565, 20), Item(564, 1));

    val runes: Array<Item> = arrayOf(*runes)

    val spell = object : Spell() {

        override fun spellId() = spellId
        override fun levelRequired() = level
        override fun baseExperience() =  experience

        override fun itemsRequired(player: Player): Optional<Array<Item>> {
            return Optional.of(this@EnchantItemSpellType.runes)
        }

        override fun equipmentRequired(player: Player): Optional<Array<Item>> {
            return Optional.empty()
        }

        override fun startCast(cast: Agent, castOn: Agent) {}
    }

    override fun toString(): String {
        return "level-" + (ordinal + 1) + " enchantment spell"
    }

}