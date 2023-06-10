package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.item.charging.impl.BloodFuryAmulet.PARTIALLY_CHARGED
import com.grinder.game.content.item.charging.impl.BloodFuryAmulet.UNCHARGED
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
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
object BloodFuryAmulet : ItemChargeable {

    val CHARGES = AttributeKey("blood-fury-amulet-charges")

    const val UNCHARGED = ItemID.AMULET_OF_FURY
    const val PARTIALLY_CHARGED = ItemID.AMULET_OF_BLOOD_FURY
    const val MAX_FURY_CHARGES = 30_000


    init {
        onSecondInventoryAction(PARTIALLY_CHARGED) {
            emptyCharges(this)
        }
        onThirdInventoryAction(PARTIALLY_CHARGED){
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(PARTIALLY_CHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    private fun emptyCharges(it: ItemActions.ItemClickAction) {
        val player = it.player
        val item = player.inventory.atSlot(it.getSlot())
        val charges = getCharges(item)
        if (item.id == PARTIALLY_CHARGED && charges <= 0) {
            player.message("Your amulet doesn't have any charges.")
            return
        }

        if (item.id == PARTIALLY_CHARGED) {
            /*DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("You will NOT get the shards back.")
                .firstOption("Okay, uncharge it.") {
                    item.asAttributable.setAttribute(CHARGES, 0)
                    player.inventory.replace(item, Item(UNCHARGED))
                    DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(PARTIALLY_CHARGED, 200)
                        .setText("You have uncharged your amulet.").start(player)
                }
                .addCancel("No, don't uncharge it.").start(player)*/
            val charges = getCharges(item)
            if (charges > 0) {
                player.message("Your amulet has " + if (charges == 1) "one charge left." else "${Misc.format(charges)} charges left."+"")
            } else {
                player.message("Your amulet does not have any charges.")
            }
        }
    }

    private fun checkCharges(player: Player, item: Item) {
        return when (item.id) {
            PARTIALLY_CHARGED -> {
                val charges = getCharges(item)
                if (charges > 0) {
                    player.message("Your amulet has " + if (charges == 1) "one charge left." else "${Misc.format(charges)} charges left."+"")
                } else {
                    player.message("Your amulet does not have any charges.")
                }
            }
            else -> player.message("Your amulet does not have any charges.")
        }
    }

    override fun getCharges(item: Item) : Int {
        return if (!item.hasAttributes()) return 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        when (item.id) {
            PARTIALLY_CHARGED -> {
                if (item.hasAttributes()) {
                    val charges = item.asAttributable.decrement(CHARGES) ?: 0

                    if (charges <= 0) {
                        //player.equipment.replace(item, Item(UNCHARGED))
                        //WeaponInterfaces.assign(player)
                       // player.combat.reset(false)
                       // EquipmentBonuses.update(player)
                       // player.equipment.refreshItems()
                        player.sendMessage("Your amulet has run out of charges.")
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
        if(with == UNCHARGED || used == UNCHARGED) return false
        if (!player.notTransformed("do this", true, true)) return false
        if(used != ItemID.BLOOD_SHARD) return false
        if(with != PARTIALLY_CHARGED) return false

        if (!player.inventory.contains(ItemID.BLOOD_SHARD)) {
            player.message("You need to have at least one blood shard to charge the amulet.")
            return true
        }

        // Inventory runes
        val enchantments = player.inventory.getAmount(ItemID.BLOOD_SHARD)
        var chargesToAdd = enchantments.coerceAtMost(MAX_FURY_CHARGES)
        
        // Safety
        if(chargesToAdd <= 0) {
            player.message("You need to have at least blood shard to charge the amulet.")
            return true
        }

        // Charges
        val currentCharges = getCharges(player.inventory[withSlot])

        // Maximum fill amount
        if (chargesToAdd + currentCharges >= MAX_FURY_CHARGES) {
            chargesToAdd = MAX_FURY_CHARGES - currentCharges
        }
            // Fully charged
            if (currentCharges >= MAX_FURY_CHARGES || chargesToAdd + currentCharges > MAX_FURY_CHARGES) {
                player.sendMessage("Your amulet is fully charged and cannot hold anymore charges.")
                return true
            }
            // Remove items
            player.removeInventoryItem(Item(ItemID.BLOOD_SHARD, chargesToAdd), -1)


            if (player.inventory[withSlot].id == PARTIALLY_CHARGED && player.inventory[withSlot].asAttributable == null) {
                if (used == ItemID.BLOOD_SHARD && with == PARTIALLY_CHARGED) {
                    val replace = AttributableItem(PARTIALLY_CHARGED)
                    player.inventory.replace(player.inventory[withSlot], replace)
                    replace.setAttribute(CHARGES, chargesToAdd)
                }
            }

            // Add charges
            player.inventory[withSlot].asAttributable.setAttribute(CHARGES, (chargesToAdd + currentCharges).coerceAtMost(MAX_FURY_CHARGES))
            player.sendMessage("You add " + Misc.format(chargesToAdd) +" charges to your amulet.")
            player.inventory.refreshItems()
            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        return true
    }
}