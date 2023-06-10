package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.item.charging.impl.AbyssalTentacle.ABYSSAL_TENTACLE
import com.grinder.game.content.item.charging.impl.AbyssalTentacle.UNCHARGED
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

@Charge(ABYSSAL_TENTACLE, ItemID.KRAKEN_TENTACLE)
object AbyssalTentacle : ItemChargeable {

    val CHARGES = AttributeKey("abyssals-tentacle-charges")

    const val UNCHARGED = ItemID.ABYSSAL_WHIP
    const val ABYSSAL_TENTACLE = ItemID.ABYSSAL_TENTACLE
    const val CHARGES_PER_TENTACLE = 1_000
    const val TENTACLE_MAX_CHARGES = 20_000


    init {
        onSecondInventoryAction( ABYSSAL_TENTACLE){
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(ABYSSAL_TENTACLE) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    private fun checkCharges(player: Player, item: Item) {
        return when (item.id) {
            ABYSSAL_TENTACLE -> {
                val charges = getCharges(item)
                if (charges > 0) {
                    player.message("Your tentacle has " + if (charges == 1) "one hit left before it degrades." else "${Misc.format(charges)} hits left before it degrades."+"")
                } else {
                    player.message("Your tentacle does not have any charges.")
                }
            }
            else -> player.message("Your tentacle does not have any charges.")
        }
    }

    override fun getCharges(item: Item) : Int {
        return if (!item.hasAttributes() && item.id == ABYSSAL_TENTACLE) return 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        when (item.id) {
            ABYSSAL_TENTACLE -> {
                if (item.hasAttributes()) {
                    val charges = item.asAttributable.decrement(CHARGES) ?: 0

                    if (charges <= 0) { // Full inventory
                        if (player.inventory.countFreeSlots() <= 0) {
                            player.equipment.replace(item, Item(ItemID.KRAKEN_TENTACLE))
                            WeaponInterfaces.assign(player)
                            player.combat.reset(false)
                            EquipmentBonuses.update(player)
                            player.equipment.refreshItems()
                        } else { // Have space
                            player.equipment.reset(EquipmentConstants.WEAPON_SLOT)
                            WeaponInterfaces.assign(player)
                            player.combat.reset(false)
                            EquipmentBonuses.update(player)
                            player.equipment.refreshItems()
                            player.inventory.add(Item(ItemID.KRAKEN_TENTACLE))
                        }
                        player.sendMessage("Your tentacle has completely degraded and can no longer be used.")
                        return
                    }
                } else {
                    if (player.inventory.countFreeSlots() <= 0) {
                        player.equipment.replace(item, Item(ItemID.KRAKEN_TENTACLE))
                        WeaponInterfaces.assign(player)
                        player.combat.reset(false)
                        EquipmentBonuses.update(player)
                        player.equipment.refreshItems()
                    } else { // Have space
                        player.equipment.reset(EquipmentConstants.WEAPON_SLOT)
                        WeaponInterfaces.assign(player)
                        player.combat.reset(false)
                        EquipmentBonuses.update(player)
                        player.equipment.refreshItems()
                        player.inventory.add(Item(ItemID.KRAKEN_TENTACLE))
                    }
                    player.sendMessage("Your tentacle has completely degraded and can no longer be used.")
                }
                return
            }
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        return emptyArray()
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED


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
        if(used != ItemID.KRAKEN_TENTACLE && used != ItemID.ABYSSAL_TENTACLE && used != UNCHARGED) return false
        if(with != UNCHARGED && with != ABYSSAL_TENTACLE) return false
        if(used == ItemID.KRAKEN_TENTACLE && with == ABYSSAL_TENTACLE) return false
        if(used == UNCHARGED && with == ABYSSAL_TENTACLE) return false
        if(with == UNCHARGED && used == ABYSSAL_TENTACLE) return false

        // Whip with tentacle
        if (with == UNCHARGED && used == ItemID.KRAKEN_TENTACLE) {
            DialogueBuilder(DialogueType.STATEMENT)
                .setText("The tentacle will gradually consume your whip and destroy it.",
                    "You won't be able to get the whip out again.", "The combined item is not tradeable."
                ).add(DialogueType.OPTION)
                .firstOption("Confirm.") {
                    player.removeInventoryItem(Item(ItemID.KRAKEN_TENTACLE, 1), -1)
                    val replace = AttributableItem(ABYSSAL_TENTACLE)
                    player.inventory.replace(player.inventory[withSlot], replace)
                    replace.setAttribute(CHARGES, CHARGES_PER_TENTACLE)
                    player.inventory.refreshItems()
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    player.packetSender.sendInterfaceRemoval();
                }
                .addCancel("Cancel.")
                .start(player)
                return true

        } else if (used == ABYSSAL_TENTACLE && with == ABYSSAL_TENTACLE) {

            val usedCharges = getCharges(player.inventory.get(player.inventory.getSlot(with)))
            val withCharges = getCharges(player.inventory[withSlot])

            // Safety
            if (usedCharges <= 0 || withCharges <= 0) {
                return true
            }

            // Fully charged
            if (usedCharges >= TENTACLE_MAX_CHARGES || withCharges >= TENTACLE_MAX_CHARGES) {
                player.sendMessage("Your tentacle is fully charged and cannot hold anymore charges.")
                return true
            } else if (usedCharges + withCharges > TENTACLE_MAX_CHARGES) {
                player.sendMessage("Your tentacle can't hold over 20,000 charges at once.")
                return true
            }

            if (getCharges(player.inventory[withSlot]) > getCharges(player.inventory.get(player.inventory.getSlot(with)))) {

                player.inventory[withSlot].asAttributable.setAttribute(
                    CHARGES,
                    (withCharges + usedCharges).coerceAtMost(TENTACLE_MAX_CHARGES)
                )
                player.sendMessage("You infuse both tentacles and it adds up " + Misc.format(usedCharges) + " charges to your tentacle.")
                player.removeInventoryItem(player.inventory.get(player.inventory.getSlot(with)), -1)

                //Common
                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                player.inventory.refreshItems()
                return true
            } else if (getCharges(player.inventory[withSlot]) == CHARGES_PER_TENTACLE) {


                player.inventory[withSlot].asAttributable.setAttribute(CHARGES, (withCharges + usedCharges))
                player.removeInventoryItem(player.inventory.get(player.inventory.getSlot(with)), -1)

                player.sendMessage("You infuse both tentacles and it adds up " + Misc.format(usedCharges) + " charges to your tentacle.")

                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                player.inventory.refreshItems()
                return true
            } else {
                DialogueManager.sendStatement(
                    player,
                    "You should use the whip with higher charges on the lower one to add charges."
                )
            }

        }


        return true
    }
}