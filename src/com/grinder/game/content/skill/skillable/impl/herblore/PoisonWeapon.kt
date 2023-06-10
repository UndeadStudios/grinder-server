package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.model.ItemActions
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.lang.Integer.min

object PoisonWeapon {

    init {
        for (type in PoisonWeaponType.values()) {
            ItemActions.onItemOnItem(type.weaponId to ItemID.WEAPON_POISON) {
                val playerInventory = player.inventory
                val weaponDefinition = ItemDefinition.forId(type.weaponId);
                if (playerInventory.countFreeSlots() < 1 && weaponDefinition.isStackable && !playerInventory.contains(type.weaponId)) {
                    player.sendMessage("Not enough inventory space to do that.")
                    return@onItemOnItem true
                }
                val amount = min(playerInventory.getAmount(type.weaponId), type.amount)
                playerInventory.set(getSlot(ItemID.WEAPON_POISON), Item(ItemID.EMPTY_VIAL, 1))
                playerInventory.delete(type.weaponId, amount, false)
                playerInventory.add(Item(type.poisonId, amount), false)
                playerInventory.refreshItems()
                player.sendMessage("You poison the ${weaponDefinition.name.toLowerCase()}. " )
                return@onItemOnItem true
            }

            ItemActions.onItemOnItem(type.weaponId to ItemID.WEAPON_POISON_PLUS_) {
                val playerInventory = player.inventory
                val weaponDefinition = ItemDefinition.forId(type.weaponId);
                if (playerInventory.countFreeSlots() < 1 && weaponDefinition.isStackable && !playerInventory.contains(type.weaponId)) {
                    player.sendMessage("Not enough inventory space to do that.")
                    return@onItemOnItem true
                }
                val amount = min(playerInventory.getAmount(type.weaponId), type.amount)
                playerInventory.set(getSlot(ItemID.WEAPON_POISON_PLUS_), Item(ItemID.EMPTY_VIAL, 1))
                playerInventory.delete(type.weaponId, amount, false)
                playerInventory.add(Item(type.poisonPlusId, amount), false)
                playerInventory.refreshItems()
                player.sendMessage("You poison the ${weaponDefinition.name.toLowerCase()}. " )
                return@onItemOnItem true
            }

            ItemActions.onItemOnItem(type.weaponId to ItemID.WEAPON_POISON_PLUS_PLUS_) {
                val playerInventory = player.inventory
                val weaponDefinition = ItemDefinition.forId(type.weaponId);
                if (playerInventory.countFreeSlots() < 1 && weaponDefinition.isStackable && !playerInventory.contains(type.weaponId)) {
                    player.sendMessage("Not enough inventory space to do that.")
                    return@onItemOnItem true
                }
                val amount = min(playerInventory.getAmount(type.weaponId), type.amount)
                playerInventory.set(getSlot(ItemID.WEAPON_POISON_PLUS_PLUS_), Item(ItemID.EMPTY_VIAL, 1))
                playerInventory.delete(type.weaponId, amount, false)
                playerInventory.add(Item(type.poisonPlusPlusId, amount), false)
                playerInventory.refreshItems()
                player.sendMessage("You poison the ${weaponDefinition.name.toLowerCase()}. " )
                return@onItemOnItem true
            }
        }
    }
}