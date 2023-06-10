package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.tryRandomEventTrigger
import com.grinder.game.model.AnimationLoop
import com.grinder.game.model.ItemActions
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.RequiredItem
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.SoundLoop
import java.util.*

object BarbarianPotion {
    init {
        for (type in BarbarianPotionType.values()){
            ItemActions.onItemOnItem(type.potionRequired to type.secondIngredient) {
                if (player.skills.getLevel(Skill.HERBLORE) <= type.levelRequired) {
                    player.sendMessage("You don't have the required level to do that.")
                    return@onItemOnItem true
                }
                val playerInventory = player.inventory
                playerInventory.delete(type.secondIngredient, 1)
                playerInventory.set(getSlot(type.potionRequired), Item(type.barbarianPotion, 1))
                playerInventory.refreshItems()
                return@onItemOnItem true
            }
        }
    }
}