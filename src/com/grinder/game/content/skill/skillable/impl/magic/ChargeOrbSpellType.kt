package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item
import java.util.*


internal enum class ChargeOrbSpellType(val spellId: Int, val level: Int, val experience: Int, vararg runes: Item) {

    WATER_ORB(1179, 56, 66, Item(555, 30)),
    EARTH_ORB(1179, 60, 70, Item(557, 30)),
    FIRE_ORB(1179, 63, 73, Item(554, 30)),
    AIR_ORB(1179, 66, 76, Item(556, 30));

    val runes: Array<Item> = arrayOf(*runes)

    val spell = object : Spell() {

        override fun spellId() = spellId
        override fun levelRequired() = level
        override fun baseExperience() =  experience

        override fun itemsRequired(player: Player): Optional<Array<Item>> {
            return Optional.of(this@ChargeOrbSpellType.runes)
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