package com.grinder.game.definition

import com.google.gson.reflect.TypeToken

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/01/2020
 * @version 1.0
 */
data class ItemValueDefinition(
        val items_name: String,
        val items_value: Long,
        val item_prices_value: Long,
        val price_checker_value: Long,
        val low_alch_value: Long,
        val high_alch_value: Long,
        val osrs_store_value: Long
) {

    companion object {

        val itemValueMap = HashMap<Int, ItemValueDefinition>()

        val itemValueMapType = object: TypeToken<HashMap<Int, ItemValueDefinition>>(){}.type!!

        fun getValue(itemId: Int, type: ItemValueType) : Long {
            return itemValueMap[itemId]?.let {
                when(type) {
                    ItemValueType.PRICE_CHECKER ->  {
                        if (it.price_checker_value > 1_500_000_000) {
                            it.price_checker_value.div(2)
                        } else if (it.price_checker_value in 10_000_000..30_000_000) {
                            it.price_checker_value.times(2)
                        } else {
                        it.price_checker_value
                    }
                    }
                    ItemValueType.HIGH_ALCHEMY -> it.high_alch_value
                    ItemValueType.LOW_ALCHEMY -> it.low_alch_value
                    ItemValueType.ITEM_PRICES -> it.item_prices_value
                    ItemValueType.ITEMS_VALUE -> it.items_value
                    ItemValueType.OSRS_STORE -> it.osrs_store_value
                }
            }?:0L
        }



    }
}