package com.grinder.game.content.skill.skillable.impl.woodcutting

import com.grinder.game.model.Animation
import com.grinder.util.ItemID

/**
 * TODO: add documentation
 *
 * @author Professor Oak
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 08/04/2020
 */
enum class AxeType(val id: Int, val requiredLevel: Int, val speed: Double, val animation: Animation) {
    BRONZE_AXE(ItemID.BRONZE_AXE, 1, 0.03, Animation(879)),
    IRON_AXE(ItemID.IRON_AXE, 1, 0.05, Animation(877)),
    STEEL_AXE(ItemID.STEEL_AXE, 6, 0.09, Animation(875)),
    BLACK_AXE(ItemID.BLACK_AXE, 6, 0.11, Animation(873)),
    MITHRIL_AXE(ItemID.MITHRIL_AXE, 21, 0.13, Animation(871)),
    ADAMANT_AXE(ItemID.ADAMANT_AXE, 31, 0.16, Animation(869)),
    RUNE_AXE(ItemID.RUNE_AXE, 41, 0.20, Animation(867)),
    GILDED_AXE(ItemID.NULL_3004, 41, 0.20, Animation(8303)),
    DRAGON_AXE(ItemID.DRAGON_AXE, 61, 0.25, Animation(2846)),
    TRAILBLAZER_AXE(ItemID.TRAILBLAZER_AXE, 1, 0.25, Animation(8778)),
    INFERNAL(ItemID.INFERNAL_AXE, 61, 0.35, Animation(2117)),
    INFERNAL_UNCHARGED(ItemID.INFERNAL_AXE_UNCHARGED_, 61, 0.30, Animation(2846)),
    THIRD_AGE(ItemID._3RD_AGE_AXE, 61, 0.50, Animation(7264)),
    CRYSTAL_AXE(ItemID.CRYSTAL_AXE, 71, 0.50, Animation(8324));
}