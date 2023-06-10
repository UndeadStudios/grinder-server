package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*

/**
 * An enumerated type that contains jewelry types that can be enchanted
 * using the [EnchantItemSpellType].
 *
 * @author Blake
 */
internal enum class EnchantableJewelry(
        itemToEnchantId: Int,
        enchantedItemId: Int,
        enchantAnimationId: Int,
        enchantGfxId: Int,
        magicLevelRequirement: Int)
{

    SAPPHIRE_RING(ItemID.SAPPHIRE_RING, ItemID.RING_OF_RECOIL, 719, 114, 7),
    SAPPHIRE_AMULET(ItemID.SAPPHIRE_AMULET_2, ItemID.AMULET_OF_MAGIC, 719, 114, 7),
    SAPPHIRE_NECKLACE(ItemID.SAPPHIRE_NECKLACE, ItemID.GAMES_NECKLACE_8_, 719, 114, 7),

    OPAL_RING(ItemID.OPAL_RING, ItemID.RING_OF_PURSUIT, 719, 114, 7),
    OPAL_AMULET(ItemID.OPAL_AMULET, ItemID.AMULET_OF_BOUNTY, 719, 114, 7),
    OPAL_NECKLACE(ItemID.OPAL_NECKLACE, ItemID.DODGY_NECKLACE, 719, 114, 7),

    EMERALD_RING(ItemID.EMERALD_RING, ItemID.RING_OF_DUELING_8_, 719, 114, 27),
    EMERALD_AMULET(ItemID.EMERALD_AMULET_2, ItemID.AMULET_OF_DEFENCE, 719, 114, 27),
    EMERALD_NECKLACE(ItemID.SAPPHIRE_NECKLACE_2, ItemID.BINDING_NECKLACE, 719, 114, 27),

    JADE_RING(ItemID.JADE_RING, ItemID.RING_OF_RETURNING_5_, 719, 114, 27),
    JADE_AMULET(ItemID.JADE_AMULET, ItemID.AMULET_OF_CHEMISTRY, 719, 114, 27),
    JADE_NECKLACE(ItemID.JADE_NECKLACE, ItemID.NECKLACE_OF_PASSAGE_5_, 719, 114, 27),

    TOPAZ_RING(ItemID.TOPAZ_RING, ItemID.EFARITAYS_AID, 720, 115, 49),
    TOPAZ_AMULET(ItemID.TOPAZ_AMULET, ItemID.BURNING_AMULET_5_, 720, 115, 49),
    TOPAZ_NECKLACE(ItemID.TOPAZ_NECKLACE, ItemID.NECKLACE_OF_FAITH, 720, 115, 49),

    RUBY_RING(ItemID.RUBY_RING, 2568, 720, 115, 49),
    RUBY_AMULET(ItemID.RUBY_AMULET_2, 1725, 720, 115, 49),
    RUBY_NECKLACE(ItemID.RUBY_NECKLACE, 11194, 720, 115, 49),

    DIAMOND_RING(ItemID.DIAMOND_RING, 2570, 720, 115, 57),
    DIAMOND_AMULET(ItemID.DIAMOND_AMULET_2, 1731, 720, 115, 57),
    DIAMOND_NECKLACE(ItemID.DIAMOND_NECKLACE, 11090, 720, 115, 57),

    DRAGONSTONE_RING(1645, 2572, 721, 116, 68),
    DRAGONSTONE_AMULET(1702, 1712, 721, 116, 68),
    DRAGONSTONE_NECKLACE(1664, 11105, 721, 116, 68),

    ONYX_RING(6575, 6583, 721, 452, 87),
    ONYX_AMULET(6581, 6585, 721, 452, 87),
    ONYX_NECKLACE(6577, 11128, 721, 452, 87),

    ZENYTE_RING(19538, 19550, 721, 452, 93),
    ZENYTE_AMULET(19541, 19553, 721, 452, 93),
    ZENYTE_NECKLACE(19535, 19547, 721, 452, 93),
    ZENYTE_BRACELET(19532, 19544, 721, 452, 93);

    val itemToEnchant: Item
    val enchantedItem: Item
    val enchantAnimationId: Int
    val gFX: Int
    val enchantLevel: Int

    companion object {
        /**
         * Find an [EnchantableJewelry] whose [.itemToEnchant]
         * matches the argued item id.
         *
         * @param itemId the item id of a [.itemToEnchant].
         * @return an [Optional] that may or may not contain an [EnchantableJewelry].
         */
        @JvmStatic
        fun forId(itemId: Int): Optional<EnchantableJewelry> {
            val item = EnchantSpellCasting.enchantItems[itemId]
            return Optional.ofNullable(item)
        }
    }

    init {
        itemToEnchant = Item(itemToEnchantId)
        enchantedItem = Item(enchantedItemId)
        this.enchantAnimationId = enchantAnimationId
        gFX = enchantGfxId
        enchantLevel = magicLevelRequirement
    }
}