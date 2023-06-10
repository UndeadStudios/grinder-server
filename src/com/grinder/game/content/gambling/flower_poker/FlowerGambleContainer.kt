package com.grinder.game.content.gambling.flower_poker

import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.container.StackType

/**
 * Represents a [ItemContainer] that is used as a holder for gambled items.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/01/2020
 * @version 1.0
 */
class FlowerGambleContainer : ItemContainer() {

    override fun full() = this

    override fun stackType() = StackType.DEFAULT

    override fun capacity() = 5

    override fun refreshItems() = this

    fun getResult(): FlowerResult {

        val ids = validItems.map { it.id }
        val idCountMap =  HashMap<Int, Int>()

        for(id in ids){
            idCountMap.putIfAbsent(id, 0)
            idCountMap[id] = idCountMap[id]!! + 1
        }

        return when {
            idCountMap.containsValue(5) -> FlowerResult.FIVE_OF_A_KIND
            idCountMap.containsValue(4) -> FlowerResult.FOUR_OF_A_KIND
            idCountMap.containsValue(3) -> return if(idCountMap.containsValue(2))
                FlowerResult.FULL_HOUSE
            else
                FlowerResult.THREE_OF_A_KIND
            idCountMap.count { it.value == 2 } == 2 -> FlowerResult.TWO_PAIRS
            idCountMap.containsValue(2) -> FlowerResult.ONE_PAIR
            else -> FlowerResult.BUST
        }
    }
}