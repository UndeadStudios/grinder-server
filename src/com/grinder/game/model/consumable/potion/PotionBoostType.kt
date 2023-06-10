package com.grinder.game.model.consumable.potion

/**
 * The enumerated type whose elements represent the boost types for potions.
 *
 * @author Ryley Kimmel (ryley.kimmel@live.com)
 * @author lare96 (github.com/lare96)
 *
 * @param amount the amount this type will boost by.
 */
internal enum class PotionBoostType(val amount: Float) {
    LOW(.10f),
    NORMAL(.13f),
    SUPER(.19f);
}