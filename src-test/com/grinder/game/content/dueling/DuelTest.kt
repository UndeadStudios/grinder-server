package com.grinder.game.content.dueling

import com.grinder.GrinderBiPlayerTest
import com.grinder.game.content.item.PlatinumToken
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DuelTest : GrinderBiPlayerTest("dueler") {

    @BeforeEach
    fun setup(){
        player1.area = AreaManager.DUEL_ARENA
        player2.area = AreaManager.DUEL_ARENA
    }

    @Test
    fun test(){

        val controller1 = player1.dueling
        val controller2 = player2.dueling

        controller1.requestDuel(player2)
        controller2.requestDuel(player1)
        resetTimers(controller1, controller2)

        player1.inventory[0] = Item(ItemID.COINS, Integer.MAX_VALUE)
        player2.inventory[0] = Item(ItemID.COINS, Integer.MAX_VALUE)

        controller1.switchItem(ItemID.COINS, Integer.MAX_VALUE, 0, player1.inventory, controller1.container)
        controller2.switchItem(ItemID.COINS, Integer.MAX_VALUE, 0, player2.inventory, controller2.container)
        resetTimers(controller1, controller2)

        controller1.acceptDuel()
        controller2.acceptDuel()
        resetTimers(controller1, controller2)

        controller1.acceptDuel()
        controller2.acceptDuel()
        resetTimers(controller1, controller2)

        while(controller1.state != DuelState.IN_DUEL || controller2.state != DuelState.IN_DUEL){
            TaskManager.sequence()
        }

        controller1.loseDuel()
        TaskManager.sequence() // for sequencing item on ground registration task

        Assertions.assertEquals(Integer.MAX_VALUE, player2.inventory.getAmount(ItemID.COINS))

        val result = PlatinumToken.convertCoins(Integer.MAX_VALUE.toLong())
        Assertions.assertEquals(result.tokensFromCoins, player2.inventory.getAmount(ItemID.PLATINUM_TOKEN))

        val itemOnGround = ItemOnGroundManager.getItemOnGround(Optional.of(player2.username), ItemID.COINS, player2.position)
        Assertions.assertTrue(itemOnGround.isPresent)
        Assertions.assertEquals(result.leftOverCoins, itemOnGround.get().item.amount)
    }

    private fun resetTimers(controller1: DuelController, controller2: DuelController) {
    }
}