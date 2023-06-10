package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.incInt
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

object YellCredits {

    init {
        onFirstInventoryAction(ItemID.REWARD_TOKEN, ItemID.REWARD_TOKEN_2, ItemID.REWARD_TOKEN_3) {

            if(player.passedTime(Attribute.GENERIC_ACTION, 1)){
                if (PlayerUtil.isMember(player)) {
                    player.message("You already have unlimited yell credits as a member!")
                    return@onFirstInventoryAction
                }
                if(player.removeInventoryItem(Item(getItemId(), 1))){
                    val amount = when(getItemId()){
                        ItemID.REWARD_TOKEN -> 5
                        ItemID.REWARD_TOKEN_2 -> 10
                        ItemID.REWARD_TOKEN_3 -> 25
                        else -> 0
                    }
                    val newAmount = player.incInt(Attribute.YELL_CREDITS, amount)
                    player.message("You have successfully redeemed $amount yell credits to your account.")
                    player.message("You now have @dre@$newAmount</col> credits left.")
                }
            }
        }
    }
}