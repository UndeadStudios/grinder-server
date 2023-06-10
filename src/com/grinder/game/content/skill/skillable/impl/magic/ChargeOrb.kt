package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*

internal enum class ChargeOrb(
        spell: Int,
        enchantedItemId: Int,
        enchantGfxId: Int,
        magicLevelRequirement: Int,
        runesRequired: Item,
        exp: Int,
        )
{

    WATER_ORB(1179, ItemID.WATER_ORB, 149, 56, Item(ItemID.WATER_RUNE, 30), 66),
    EARTH_ORB(1182, ItemID.EARTH_ORB, 151, 60, Item(ItemID.EARTH_RUNE, 30), 70),
    FIRE_ORB(1184, ItemID.FIRE_ORB, 152, 63, Item(ItemID.FIRE_RUNE, 30), 73),
    AIR_ORB(1186, ItemID.AIR_ORB,  150, 66, Item(ItemID.AIR_RUNE, 30), 76);

    val spellId: Int
    val enchantedItem: Item
    val gFX: Int
    val enchantLevel: Int
    val runes: Item
    val experience: Int

    companion object {
        /**
         * Find an [EnchantableJewelry] whose [.itemToEnchant]
         * matches the argued item id.
         *
         * @param itemId the item id of a [.itemToEnchant].
         * @return an [Optional] that may or may not contain an [EnchantableJewelry].
         */
        @JvmStatic
        fun forId(spellId: Int): Optional<ChargeOrb> {
            val spell = ChargeOrbSpellCasting.enchantItems[spellId]
            return Optional.ofNullable(spell)
        }
    }

    init {
        spellId = spell;
        enchantedItem = Item(enchantedItemId)
        gFX = enchantGfxId
        enchantLevel = magicLevelRequirement
        runes = runesRequired
        experience = exp
    }
}