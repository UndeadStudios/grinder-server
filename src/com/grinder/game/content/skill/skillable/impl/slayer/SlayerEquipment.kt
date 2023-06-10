@file:JvmName("SlayerEquipment")

package com.grinder.game.content.skill.skillable.impl.slayer

import com.grinder.game.model.item.container.player.Equipment
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot

// list of slayer helmets, their Ids are not sequential
val slayerHelmets = intArrayOf(
        ItemID.SLAYER_HELMET,
        ItemID.BLACK_SLAYER_HELMET,
        ItemID.GREEN_SLAYER_HELMET,
        ItemID.PURPLE_SLAYER_HELMET,
        ItemID.RED_SLAYER_HELMET,
        21888, // Turquoise Slayer Helmet
        23073, // Hydra Slayer Helmet
        ItemID.TWISTED_SLAYER_HELMET,
        ItemID.TZTOK_SLAYER_HELMET,
        ItemID.VAMPYRIC_SLAYER_HELMET,
        ItemID.TZKAL_SLAYER_HELMET
)
// list of imbued slayer helmets, their Ids are not sequential
val imbuedSlayerHelmets = intArrayOf(
        ItemID.SLAYER_HELMET_I_,
        ItemID.BLACK_SLAYER_HELMET_I_,
        ItemID.GREEN_SLAYER_HELMET_I_,
        ItemID.PURPLE_SLAYER_HELMET_I_,
        ItemID.RED_SLAYER_HELMET_I_,
        21890, // Turquoise Slayer Helmet
        23075, // Hydra Slayer Helmet
        15910, // Maranami's Custom slayer helmet
        16109, // Brat Custom slayer helmet
        ItemID.TWISTED_SLAYER_HELMET_I,
        ItemID.TZTOK_SLAYER_HELMET_I,
        ItemID.VAMPYRIC_SLAYER_HELMET_I,
        ItemID.TZKAL_SLAYER_HELMET_I
)

val leafBladedWeapons = intArrayOf(
        ItemID.LEAF_BLADED_BATTLEAXE,
        ItemID.LEAF_BLADED_SPEAR,
        ItemID.LEAF_BLADED_SWORD,
        ItemID.SLAYERS_STAFF,
        ItemID.SLAYERS_STAFF_E_
)

val leafAmmunition = intArrayOf(
        ItemID.BROAD_ARROWS,
        ItemID.BROAD_ARROWS_2,
        ItemID.BROAD_BOLTS,
        ItemID.AMETHYST_BROAD_BOLTS
)

/**
 * Determines if the player is using a broad weapon.
 * @param equipment The equipment container.
 */
fun usingBroadWeapon(equipment:Equipment) = equipment[EquipSlot.WEAPON].id in leafBladedWeapons

/**
 * Determines if the player is using broad ammunication, should be used in conjunction with a weapon check.
 */
fun usingBroadAmmo(equipment:Equipment) = equipment[EquipSlot.ARROWS].id  in leafAmmunition

/**
 * Determines if the container has a black mask variant.
 *
 * @param equipment The equipment container.
 */
fun hasBlackMask(equipment: Equipment): Boolean {
    val headId = equipment[EquipSlot.HAT] ?: return false
    return headId.id in 8901..8921
}
/**
 * Determines if the container has a black mask(i) variant.
 *
 * @param equipment The equipment container.
 */
fun hasImbuedMask(equipment: Equipment): Boolean {
    val headId = equipment[EquipSlot.HAT] ?: return false
    return headId.id in 11774..11784
}
/**
 * Determines if the container has a regular SlayerHelmet variant.
 *
 * @param equipment The equipment container.
 */
fun hasRegularSlayerHelmet(equipment: Equipment): Boolean  {
    val headId = equipment[EquipSlot.HAT] ?: return false
    return headId.id in slayerHelmets
}
/**
 * Determines if the container has a SlayerHelmet(i) variant.
 *
 * @param equipment The equipment container.
 */
fun hasImbuedSlayerHelmet(equipment: Equipment): Boolean {
    val headId = equipment[EquipSlot.HAT] ?: return false
    return headId.id in imbuedSlayerHelmets
}
/**
 * Determines if the container has a any SlayerHelmet variant.
 *
 * @param equipment The equipment container.
 */
fun hasAnySlayerHelmet(equipment:Equipment) = hasRegularSlayerHelmet(equipment) || hasImbuedSlayerHelmet(equipment)