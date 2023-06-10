package com.grinder.game.content.item.jewerly

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.skill.skillable.impl.Prayer.BuriableBone
import com.grinder.game.entity.*
import com.grinder.game.entity.agent.combat.event.impl.DropItemLootEvent
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * Handles the bonecrusher necklace mechanics.
 *
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
object BonecrusherNecklace {

    private const val name = "Bonecrusher necklace"

    init {
        CombatActions.onEvent(DropItemLootEvent::class) {
            if (!combatEvent.dropItemOnGround)
                return@onEvent
            val player = combatEvent.player
            if (player.getBoolean(Attribute.BONECRUSHER_NECKLACE_ACTIVE) && player.getInt(Attribute.BONECRUSHER_NECKLACE_CHARGES) > 0) {
                if (player.equipment.contains(ItemID.BONECRUSHER_NECKALCE)) {
                    val dropped = combatEvent.item
                    BuriableBone.forId(dropped.id).ifPresent {
                        crush(player, it)
                        AchievementManager.processFor(AchievementType.CRUSHING_THEM, player)
                        combatEvent.dropItemOnGround = false
                    }
                }
            }
        }
        ItemActions.onItemOnItem(ItemID.BONECRUSHER_NECKALCE to ItemID.ECTO_TOKEN) {
            val charges = player.getInt(Attribute.BONECRUSHER_NECKLACE_CHARGES)
            if (charges == Int.MAX_VALUE) {
                player.message("Your $name is fully charged.")
                return@onItemOnItem true
            }
            val tokenMultiplier = 25
            val tokens = player.inventory[getOtherSlot(ItemID.BONECRUSHER_NECKALCE)] ?: return@onItemOnItem true
            val availableCharges = min(tokens.amount.toLong() * tokenMultiplier.toLong(), Int.MAX_VALUE - charges.toLong()).toInt()
            if (availableCharges > 0) {
                if (player.removeInventoryItem(Item(tokens.id, availableCharges / tokenMultiplier))) {
                    player.incInt(Attribute.BONECRUSHER_NECKLACE_CHARGES, availableCharges)
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    checkCharges(player)
                }
            }
            return@onItemOnItem true
        }
        onEquipAction(ItemID.BONECRUSHER_NECKALCE) {
            if (player.getBoolean(Attribute.BONECRUSHER_NECKLACE_ACTIVE)) {
                player.markTime(Attribute.BONECRUSHER_NECKLACE_WEAR_TIMER)
                val timer = player.attributes[Attribute.BONECRUSHER_NECKLACE_WEAR_TIMER] ?: return@onEquipAction
                TaskManager.cancelTasks(timer)
                TaskManager.submit(timer, 9) {
                    if (player.equipment.containsAtSlot(EquipSlot.AMULET, ItemID.BONECRUSHER_NECKALCE)) {
                        player.packetSender.sendSound(Sounds.DRAGONBONE_NECKLACE_ACTIVE_TIMER)
                        player.message("Your $name restoration effect is now active!")
                    }
                }
            }
            EquipPacketListener.equip(this)
        }
        onSecondInventoryAction(ItemID.BONECRUSHER_NECKALCE) {
            player.sendOptionsKt(
                    "Check." to {
                        player.removeInterfaces()
                        checkCharges(player)
                    },
                    "Uncharge." to {
                        player.removeInterfaces()
                        val charges = player.getInt(Attribute.BONECRUSHER_NECKLACE_CHARGES)
                        if (charges >= 25) {
                            val amount = charges / 25
                            player.removeAttribute(Attribute.BONECRUSHER_NECKLACE_CHARGES)
                            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                            ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.ECTO_TOKEN, amount))

                        } else
                            player.message("You need at least 25 charges to uncharge your $name.")
                    }
            )
        }
        onSecondContainerEquipmentAction(ItemID.BONECRUSHER_NECKALCE) {
            checkCharges(player)
        }
        onThirdInventoryAction(ItemID.BONECRUSHER_NECKALCE) {
            val active = player.toggleBoolean(Attribute.BONECRUSHER_NECKLACE_ACTIVE)
            if (active)
                player.packetSender.sendSound(Sounds.ACTIVATE_CRUSHER)
            else
                player.packetSender.sendSound(Sounds.DEACTIVATE_CRUSHER)

            player.message("Your $name is now @dre@" + (if (active) "activated" else "deactivated") + "@bla@.")
        }
    }

    /*private fun getTokenMultiplier(player: Player): Int {
        return when {
            PlayerUtil.isMember(player) -> 5
            PlayerUtil.isSuperMember(player) -> 8
            PlayerUtil.isExtremeMember(player) -> 12
            PlayerUtil.isLegendaryMember(player) -> 15
            PlayerUtil.isPlatinumMember(player) -> 18
            else -> 2
        }
    }*/

    private fun crush(player: Player, bone: BuriableBone) {

        if (player.getInt(Attribute.BONECRUSHER_NECKLACE_CHARGES) <= 0) {
            player.message("You do not have any charges left to use your $name.")
            return
        }
        val newCharges = player.decInt(Attribute.BONECRUSHER_NECKLACE_CHARGES, 1)
        player.addExperience(Skill.PRAYER, bone.xp)
        if (AreaManager.CATACOMBS_OF_KOUREND_AREA.contains(player) || player.hasBonecrusherNecklaceEffect())
            player.increaseLevel(Skill.PRAYER, bone.pointsRestored)

        if (player.equipment.items[EquipmentConstants.AMULET_SLOT].id == ItemID.BONECRUSHER_NECKALCE
            && !AreaManager.CATACOMBS_OF_KOUREND_AREA.contains(player)
            && (player.passedTime(Attribute.BONECRUSHER_NECKLACE_WEAR_TIMER, 9, TimeUnit.SECONDS, false))) {
            player.increaseLevel(Skill.PRAYER, bone.pointsRestored)
        }
    }

    private fun checkCharges(player: Player) {
        val charges = player.getInt(Attribute.BONECRUSHER_NECKLACE_CHARGES)
        if (charges > 0)
            player.message("Your $name has @dre@" + charges + " @bla@charge" + (if (charges > 1) "s" else "") + ".")
        else
            player.message("Your $name has no charges.")
    }

    fun Player.hasBonecrusherNecklaceEffect() = equipment.containsAtSlot(EquipSlot.AMULET, ItemID.BONECRUSHER_NECKALCE)
            && passedTime(Attribute.BONECRUSHER_NECKLACE_WEAR_TIMER, 9, TimeUnit.SECONDS, false)
}