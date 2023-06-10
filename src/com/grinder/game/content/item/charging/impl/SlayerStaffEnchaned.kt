package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.item.charging.impl.SlayerStaffEnchaned.PARTIALLY_CHARGED
import com.grinder.game.content.item.charging.impl.SlayerStaffEnchaned.UNCHARGED
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

@Charge(PARTIALLY_CHARGED, UNCHARGED)
object SlayerStaffEnchaned : ItemChargeable {

    val CHARGES = AttributeKey("slayer-staff-e-charges")

    const val UNCHARGED = ItemID.SLAYERS_STAFF
    const val PARTIALLY_CHARGED = ItemID.SLAYERS_STAFF_E_
    const val MAX_STAFF_CHARGES = 25_000


    init {
        onSecondInventoryAction(PARTIALLY_CHARGED) {
            emptyCharges(this)
        }
        onThirdInventoryAction(UNCHARGED, PARTIALLY_CHARGED){
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(UNCHARGED, PARTIALLY_CHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    private fun emptyCharges(it: ItemActions.ItemClickAction) {
        val player = it.player
        val item = player.inventory.atSlot(it.getSlot())
        val charges = getCharges(item)
        if (item.id == UNCHARGED || (item.id == PARTIALLY_CHARGED && charges <= 0)) {
            player.message("Your staff doesn't have any charges to uncharge.")
            return
        }

        if (item.id == PARTIALLY_CHARGED) {
            DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("You will NOT get the enchantment back.")
                .firstOption("Okay, uncharge it.") {
                    item.asAttributable.setAttribute(CHARGES, 0)
                    player.inventory.replace(item, Item(UNCHARGED))
                    DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(PARTIALLY_CHARGED, 200)
                        .setText("You have uncharged your staff.").start(player)
                }
                .addCancel("No, don't uncharge it.").start(player)
        } else if (item.id == UNCHARGED) {
            player.message("Your staff does not have any charges.")
        }
    }

    private fun checkCharges(player: Player, item: Item) {
        return when (item.id) {
            PARTIALLY_CHARGED -> {
                val charges = getCharges(item)
                if (charges > 0) {
                    player.message("Your staff has " + if (charges == 1) "one charge left." else "${Misc.format(charges)} charges left."+"")
                } else {
                    player.message("Your staff does not have any charges.")
                }
            }
            else -> player.message("Your staff does not have any charges.")
        }
    }

    override fun getCharges(item: Item) : Int {
        return if (!item.hasAttributes() || item.id == UNCHARGED) return 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        when (item.id) {
            PARTIALLY_CHARGED -> {
                if (item.hasAttributes()) {
                    val charges = item.asAttributable.decrement(CHARGES) ?: 0

                    if (charges <= 0) {
                        player.equipment.replace(item, Item(UNCHARGED))
                        WeaponInterfaces.assign(player)
                        player.combat.reset(false)
                        EquipmentBonuses.update(player)
                        player.equipment.refreshItems()
                        player.sendMessage("Your staff has run out of charges.")
                        return
                    }
                }
                return
            }
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        return emptyArray()
    }


    override fun charge(player: Player, used: Int, with: Int, withSlot: Int): Boolean {
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) return false
        if (player.BLOCK_ALL_BUT_TALKING) return false
        if (player.isInTutorial) return false
        if (player.status === PlayerStatus.AWAY_FROM_KEYBOARD) return false

        if (player.getBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
            player.sendMessage("Please finish your random event before doing anything else.")
            return false
        }
        if (player.busy()) {
            player.sendMessage("You cannot do that when you are busy.")
            return false;
        }

        if (!player.notTransformed("do this", true, true)) return false
        if(used != ItemID.SLAYERS_ENCHANTMENT) return false
        if(with != UNCHARGED && with != PARTIALLY_CHARGED) return false

        if (!player.inventory.contains(ItemID.SLAYERS_ENCHANTMENT)) {
            player.message("You need to have at least one slayer's enchantment to charge the staff.")
            return true
        }

        // Inventory runes
        val enchantments = player.inventory.getAmount(ItemID.SLAYERS_ENCHANTMENT).times(2500)
        var chargesToAdd = enchantments.coerceAtMost(MAX_STAFF_CHARGES)
        
        // Safety
        if(chargesToAdd <= 0) {
            player.message("You need to have at least one slayer's enchantment to charge the staff.")
            return true
        }

        // Charges
        val currentCharges = getCharges(player.inventory[withSlot])

        // Maximum fill amount
        if (chargesToAdd + currentCharges >= MAX_STAFF_CHARGES) {
            chargesToAdd = MAX_STAFF_CHARGES - currentCharges
        }
            // Fully charged
            if (currentCharges >= MAX_STAFF_CHARGES || chargesToAdd + currentCharges > MAX_STAFF_CHARGES) {
                player.sendMessage("Your staff is fully charged and cannot hold anymore charges.")
                return true
            }
            // Remove items
            player.removeInventoryItem(Item(ItemID.SLAYERS_ENCHANTMENT, chargesToAdd.div(2500)), -1)


            if(used == UNCHARGED || with == UNCHARGED) {
                val replace = AttributableItem(PARTIALLY_CHARGED)
                player.inventory.replace(player.inventory[withSlot], replace)
                replace.setAttribute(CHARGES, chargesToAdd)
            }

            // Add charges
            player.inventory[withSlot].asAttributable.setAttribute(CHARGES, (chargesToAdd + currentCharges).coerceAtMost(MAX_STAFF_CHARGES))
            player.sendMessage("You add " + Misc.format(chargesToAdd) +" charges to your staff.")
            player.inventory.refreshItems()
            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        return true
    }
}