package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.game.model.Animation
import com.grinder.util.ItemID

/**
 * @author Professor Oak
 * @author Zach (zach@findzach.com)
 * @since 12/17/2020
 */
enum class PickaxeType(val id: Int, val requiredLevel: Int,val animaion: Animation,val secondAnimation: Animation,val speed: Double) { // Animation is spelled wrong?

    BRONZE(1265, 1, Animation(625, 25), Animation(6753, 25), 0.03),
    IRON(1267, 1, Animation(626, 25), Animation(6754, 25), 0.05),
    STEEL(1269, 6, Animation(627, 25), Animation(6755, 25), 0.09),
    MITHRIL(1273, 21, Animation(629, 25), Animation(6757, 25), 0.13),
    ADAMANT(1271, 31, Animation(628, 25), Animation(6756, 25), 0.16),
    RUNE(1275, 41, Animation(624, 25), Animation(6752, 25), 0.30),
    GILDED(15221, 41, Animation(8314, 25), Animation(8312, 25), 0.30),
    INFERNAL(13243, 61, Animation(4483, 25), Animation(4481, 25), 0.50),
    INFERNAL_UNCHARGED(13244, 61, Animation(4483, 25), Animation(4481, 25),0.40),
    DRAGON(11920, 61, Animation(7139, 25), Animation(6758, 25), 0.40),
    DRAGON_OR(12797, 61, Animation(8361, 25), Animation(8344, 25), 0.40),
    TRAILBLAZER(ItemID.TRAILBLAZER_PICKAXE, 1, Animation(8787, 25), Animation(8786, 25), 0.40),
    THIRD_AGE(20014, 61, Animation(7283, 25), Animation(7282, 25), 0.55),
    CRYSTAL_PICKAXE(23680, 71, Animation(8347, 25), Animation(8345, 25), 0.5);
}