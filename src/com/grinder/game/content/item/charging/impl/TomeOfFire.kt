package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.skill.skillable.impl.magic.Spell
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.event.impl.SpellCastEvent
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.CombatActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onThirdContainerEquipmentAction
import com.grinder.net.packet.PacketConstants
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.EquipSlot
import java.lang.Math.floorDiv
import java.util.function.Consumer

/**
 * Implemented according to https://oldschool.runescape.wiki/w/Tome_of_fire#Charged
 *
 * @see Spell for tome activation
 *
 * TODO: find correct interface and messages
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   10/04/2020
 * @version 1.0
 */
@Charge(TomeOfFire.CHARGED, TomeOfFire.UNCHARGED)
object TomeOfFire : ItemChargeable {

    const val CHARGED = ItemID.TOME_OF_FIRE
    const val UNCHARGED = ItemID.TOME_OF_FIRE_EMPTY_

    val CHARGES = AttributeKey("tome-of-fire-charges")

    init {
        ItemActions.onClick(CHARGED) {
            if (isInInventory()) {
                if (isDropAction()) { //Allows users to drop items
                    return@onClick false
                }
                when (itemActionMessage.opcode) {
                    PacketConstants.EQUIP_ITEM_OPCODE -> EquipPacketListener.equip(this)
                    PacketConstants.SECOND_ITEM_ACTION_OPCODE -> addOrRemove(player, getSlot())
                    PacketConstants.THIRD_ITEM_ACTION_OPCODE -> check(player, getSlot())
                    else -> { }
                }
                return@onClick true
            }
            return@onClick false
        }
        ItemActions.onClick(UNCHARGED) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if (isInInventory()) {
                when (itemActionMessage.opcode) {
                    PacketConstants.EQUIP_ITEM_OPCODE -> EquipPacketListener.equip(this)
                    PacketConstants.SECOND_ITEM_ACTION_OPCODE -> addPages(player, getSlot())
                    else -> { }
                }
                return@onClick true
            }
            return@onClick false
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            if (getItemId() == UNCHARGED) {
                player.message("Your tome of fire does not have any more charges.")
            } else {
                val charges = getCharges(player)
                player.message("Your tome of fire has ${Misc.format(charges)} charges.")
            }
        }
        onThirdContainerEquipmentAction(CHARGED) {
            check(player, getSlot())
        }
        CombatActions.onEvent(SpellCastEvent::class) {
            ifActorIsPlayer { player ->
                decrementCharges(player, player.equipment[EquipSlot.SHIELD])
            }
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item) / 20
        return if (charges > 0)
            arrayOf(Item(ItemID.BURNT_PAGE, charges))
        else
            emptyArray()
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
        if (used == ItemID.BURNT_PAGE) {
            if (with == CHARGED || with == UNCHARGED) {
                addPages(player, withSlot)
                return true
            }
        }
        return false
    }

    override fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0
            if(charges <= 0)
                player.replaceEquipmentItem(Item(CHARGED), Item(UNCHARGED), 0)
        }
    }

    fun suppressRunes(player: Player, spell: Spell, requiredRunes: Array<Item>): Array<Item> {
        if (player.equipment.containsAny(CHARGED, UNCHARGED)) {

            if (player.equipment.contains(CHARGED)) {
                if (getShieldCharges(player) <= 0) {
                    replaceTomeWithEmpty(player)
                    return requiredRunes
                }
            }

            if (requiredRunes.any { it.id == ItemID.FIRE_RUNE }) {
                if (spell is CombatSpell && player.equipment.contains(CHARGED)) {
                    val combatSpell = CombatSpellType.values().find { it.spell.spellId() == spell.spellId() }
                    if (combatSpell != null) {
                        var tomeActivated = when (combatSpell) {
                            CombatSpellType.FIRE_STRIKE,
                            CombatSpellType.FIRE_BOLT,
                            CombatSpellType.FIRE_BLAST,
                            CombatSpellType.FIRE_WAVE,
                            CombatSpellType.FIRE_SURGE -> true
                            else -> false
                        }
                        player.setBoolean(Attribute.FIRE_TOME_ACTIVATED, tomeActivated, false)
                        if (player.getBoolean(Attribute.FIRE_TOME_ACTIVATED)) {
                            return requiredRunes.filter { it.id != ItemID.FIRE_RUNE }.toTypedArray()
                        }
                    }
                } else
                    return requiredRunes.filter { it.id != ItemID.FIRE_RUNE }.toTypedArray()
            }
        }
        return requiredRunes
    }

    private fun getShieldCharges(player: Player): Int {
        val tome = player.equipment[EquipSlot.SHIELD].asAttributable ?: return 0
        return tome.getAttribute(CHARGES) ?: 0
    }

    fun getCharges(player: Player, slot: Int): Int {
        val tome = player.inventory[slot].asAttributable ?: return 0
        return tome.getAttribute(CHARGES) ?: 0
    }

    fun getCharges(player: Player): Int {
        val tome = player.equipment[EquipSlot.SHIELD].asAttributable ?: return 0
        return tome.getAttribute(CHARGES) ?: 0
    }

    fun check(player: Player, slot: Int) {
        player.sendMessage("Your tome has ${Misc.format(getCharges(player, slot))} charges left!")
    }

    private fun replaceTomeWithEmpty(player: Player) {
        player.replaceEquipmentItem(Item(CHARGED), Item(UNCHARGED), 0)
        player.message("Your tome of fire ran out of charges!")
    }

    private fun addOrRemove(player: Player, slot: Int) {
        player.sendOptions(
                "Add." to Consumer { addPages(it, slot) },
                "Remove." to Consumer { removePages(it, slot) },
                title = "Add or Remove pages?",
                addCancel = true
        )
    }

    fun addPages(player: Player, slot: Int) {
        if (!player.inventory.contains(ItemID.BURNT_PAGE)) {
            player.itemStatement(ItemID.BURNT_PAGE, 200,
                    "You don't have any burnt pages", "in your inventory!")
        } else {
            player.requestInput(Integer::class.java, "Enter number of pages to add:") {

                val pagesToAdd = (1000 - getPages(player, slot))
                        .coerceAtMost(player.inventory.getAmount(ItemID.BURNT_PAGE)
                                .coerceAtMost(it.toInt()))
                if (pagesToAdd > 0) {
                    val tomeItem = player.inventory[slot]

                    player.inventory.delete(ItemID.BURNT_PAGE, pagesToAdd)
                    if (!tomeItem.hasAttributes()) {
                        val chargedItem = AttributableItem(CHARGED)
                        chargedItem.setAttribute(CHARGES, pagesToAdd * 20)
                        player.inventory.replace(tomeItem, chargedItem)
                        player.inventory.refreshItems()
                    } else {
                        tomeItem.asAttributable.increase(CHARGES, pagesToAdd * 20)
                    }

                    player.itemStatement(ItemID.BURNT_PAGE, 200,
                            "Added $pagesToAdd burnt pages to tome,",
                            "the tome now contains ${getPages(player, slot)} pages!")
                } else {
                    player.itemStatement(CHARGED, 200,
                            "You cannot add any more burnt pages,",
                            "maximum capacity of 1,000 pages has been reached!")
                }
            }
        }
    }

    private fun removePages(player: Player, slot: Int) {
        if (getPages(player, slot) <= 0)
            player.itemStatement(CHARGED, 200, "This tome does not contain any pages!")
        else {
            player.requestInput(Integer::class.java, "Enter number of pages to remove:") {
                var pagesToRemove = getPages(player, slot).coerceAtMost(it.toInt())
                val newTotal = pagesToRemove + player.inventory.getAmount(ItemID.BURNT_PAGE).toLong()

                if (newTotal > Integer.MAX_VALUE)
                    pagesToRemove -= (newTotal - Integer.MAX_VALUE).toInt()

                if (pagesToRemove > 0) {
                    player.addInventoryItem(Item(ItemID.BURNT_PAGE, pagesToRemove), 1)

                    val tome = player.inventory[slot].asAttributable ?: return@requestInput
                    tome.increase(CHARGES, -pagesToRemove * 20)
                    val charges = tome.getAttribute(CHARGES)!!

                    if (charges < 20)
                        player.inventory[slot] = Item(UNCHARGED)

                    player.itemStatement(ItemID.BURNT_PAGE, 200,
                            "Removed ${Misc.format(pagesToRemove)} burnt pages to tome,",
                            "the tome now contains ${Misc.format(getPages(player, slot))} pages!")
                } else {
                    player.itemStatement(CHARGED, 200,
                            "You cannot remove any more burnt pages,",
                            "you already have 2147m burnt pages in your inventory!")
                }
            }
        }
    }

    fun getPages(player: Player, slot: Int) = floorDiv(getCharges(player, slot), 20)
}