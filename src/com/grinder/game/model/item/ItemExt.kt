package com.grinder.game.model.item

import com.grinder.game.definition.ItemValueType
import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */

fun Item.name(addAorAn: Boolean = false) = definition?.name?:"null".let { (if (addAorAn) Misc.anOrA(it) else "") + " $it" }

fun Item.stackable() = definition?.isStackable == true

//TODO: add plural form
fun Item.nameAndQuantity() = "${Misc.formatWithAbbreviation2(amount.toLong())} ${name()}"

fun Item.price(): String = Misc.formatWithAbbreviation2(getValue(ItemValueType.PRICE_CHECKER))

fun Item.isLargeAmount(thresholdValue: Long = 4_000_000) : Boolean {
    return amount > thresholdValue
}

fun Item.isHighValued(thresholdValue: Long = 50_000_000) : Boolean {
    val totalItemPriceValue = getValue(ItemValueType.ITEM_PRICES) * amount
    val totalPriceCheckerValue = getValue(ItemValueType.PRICE_CHECKER) * amount
    return totalItemPriceValue >= thresholdValue || totalPriceCheckerValue >= thresholdValue
}