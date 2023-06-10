package com.grinder.game.content.dueling

import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.util.Misc

/**
 * Represents rules that can be set for a duel.
 *
 * @param configId          the interface config id (used for toggling the state of the button).
 * @param buttonId          the id of the button that toggles this rule.
 * @param inventorySpaceReq the amount of free slots required for this rule (-1 if not applicable)
 * @param equipmentSlot     the slot of the item targeted by this rule.
 */
enum class DuelRule(
        val configId: Int,
        val buttonId: Int,
        val inventorySpaceReq: Int,
        val equipmentSlot: Int
) {

    NO_RANGED(16, 6725, -1, -1),
    NO_MELEE(32, 6726, -1, -1),
    NO_MAGIC(64, 6727, -1, -1),
    NO_SPECIAL_ATTACKS(8192, 7816, -1, -1),
    LOCK_WEAPON(4096, 670, -1, -1),
    NO_FORFEIT(1, 6721, -1, -1),
    NO_POTIONS(128, 6728, -1, -1),
    NO_FOOD(256, 6729, -1, -1),
    NO_PRAYER(512, 6730, -1, -1),
    NO_MOVEMENT(2, 6722, -1, -1),
    OBSTACLES(1024, 6732, -1, -1),
    NO_HELM(16384, 13813, 1, EquipmentConstants.HEAD_SLOT),
    NO_CAPE(32768, 13814, 1, EquipmentConstants.CAPE_SLOT),
    NO_AMULET(65536, 13815, 1, EquipmentConstants.AMULET_SLOT),
    NO_AMMUNITION(134217728, 13816, 1, EquipmentConstants.AMMUNITION_SLOT),
    NO_WEAPON(131072, 13817, 1, EquipmentConstants.WEAPON_SLOT),
    NO_BODY(262144, 13818, 1, EquipmentConstants.BODY_SLOT),
    NO_SHIELD(524288, 13819, 1, EquipmentConstants.SHIELD_SLOT),
    NO_LEGS(2097152, 13820, 1, EquipmentConstants.LEG_SLOT),
    NO_RING(67108864, 13821, 1, EquipmentConstants.RING_SLOT),
    NO_BOOTS(16777216, 13822, 1, EquipmentConstants.FEET_SLOT),
    NO_GLOVES(8388608, 13823, 1, EquipmentConstants.HANDS_SLOT);

    override fun toString() = Misc.formatText(name.toLowerCase())

    companion object {

        @JvmStatic
        fun forId(i: Int) = values().find { it.ordinal == i }

        @JvmStatic
        fun forButtonId(buttonId: Int) = values().find { it.buttonId == buttonId }
    }
}