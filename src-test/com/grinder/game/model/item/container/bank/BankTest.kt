package com.grinder.game.model.item.container.bank

import com.grinder.GrinderPlayerTest
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

class BankTest : GrinderPlayerTest("bank_test") {

    @Test
    fun deposit(){
        val items = ItemDefinition.definitions
                .filter { it.value?.isTradeable == true }
                .toList().stream().limit(353)
                .map { Item(it.first, 1) }
                .collect(Collectors.toList())

        for(item in items){
            player.getBank(0).add(item, false)
        }

        Assertions.assertTrue(items.all { player.banks[0].contains(it) })

        val stackable = Item(ItemID.PRAYER_POTION_4_, 10000)
        player.getBank(0).add(stackable, false)

        Assertions.assertTrue(player.banks[0].contains(stackable))

        player.getBank(0).add(stackable, false)
        Assertions.assertTrue(player.banks[0].getAmount(stackable.id) == stackable.amount * 2)
    }

}