package com.grinder.game.content.item

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.jewerly.DragonboneNecklace.hasDragonbonerNecklaceEffect
import com.grinder.game.content.skill.skillable.impl.Prayer.BuriableBone
import com.grinder.game.entity.*
import com.grinder.game.entity.agent.combat.event.impl.DropItemLootEvent
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc
import kotlin.math.min

/**
 * Handles the bonecrusher mechanics.
 *
 * @author  Blake
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
object Bonecrusher {

    private const val name = "Bonecrusher"

    init {
        CombatActions.onEvent(DropItemLootEvent::class) {
            if (!combatEvent.dropItemOnGround)
                return@onEvent
            val player = combatEvent.player
            if (player.getBoolean(Attribute.BONECRUSHER_ACTIVE) && player.getInt(Attribute.BONECRUSHER_CHARGES) > 0) {
                if (player.inventory.contains(ItemID.BONECRUSHER)) {
                    val dropped = combatEvent.item
                    BuriableBone.forId(dropped.id).ifPresent {
                        crush(player, it)
                        AchievementManager.processFor(AchievementType.CRUSHING_THEM, player)
                        combatEvent.dropItemOnGround = false
                    }
                }
            }
        }
        ItemActions.onItemOnItem(ItemID.BONECRUSHER to ItemID.ECTO_TOKEN) {
            val charges = player.getInt(Attribute.BONECRUSHER_CHARGES)
            if (charges == Int.MAX_VALUE){
                player.message("Your $name is fully charged.")
                return@onItemOnItem true
            }
            if (charges < 0) {
                player.removeAttribute(Attribute.BONECRUSHER_CHARGES)
            }
            val tokenMultiplier = getTokenMultiplier(player)
            val tokens = player.inventory[getOtherSlot(ItemID.BONECRUSHER)]?:return@onItemOnItem true
            //val availableCharges = min(tokens.amount.toLong() * tokenMultiplier.toLong(), Int.MAX_VALUE - charges.toLong()).toInt()
            val availableCharges = (tokens.amount * tokenMultiplier).coerceAtMost(Int.MAX_VALUE)
            if (availableCharges > 0) {
                if (player.removeInventoryItem(Item(tokens.id, (availableCharges / tokenMultiplier).toInt()))) {
                    player.incInt(Attribute.BONECRUSHER_CHARGES, availableCharges)
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    checkCharges(player)
                }
            }
            return@onItemOnItem true
        }
        onEquipAction(ItemID.BONECRUSHER) {
            checkCharges(player)
        }
        onSecondInventoryAction(ItemID.BONECRUSHER) {
            var charges = player.getInt(Attribute.BONECRUSHER_CHARGES)
            if (charges >= 100) {
                val amount = (charges / 25).div(getTokenMultiplier(player))
                player.removeAttribute(Attribute.BONECRUSHER_CHARGES)
                ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.ECTO_TOKEN, amount))
                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
            } else
                player.message("You need at least 100 charges to uncharge your $name.")
        }
        onThirdInventoryAction(ItemID.BONECRUSHER) {
            val active = player.toggleBoolean(Attribute.BONECRUSHER_ACTIVE)

            if (active)
                player.packetSender.sendSound(Sounds.ACTIVATE_CRUSHER)
            else
                player.packetSender.sendSound(Sounds.DEACTIVATE_CRUSHER)

            player.message("Your $name is now @dre@" + (if (active) "activated" else "deactivated") + "@bla@.")
        }
    }

    private fun getTokenMultiplier(player: Player): Int {
        return when {
            PlayerUtil.isDiamondMember(player) -> 50
            PlayerUtil.isTitaniumMember(player) -> 40
            PlayerUtil.isPlatinumMember(player) -> 30
            PlayerUtil.isLegendaryMember(player) -> 25
            PlayerUtil.isAmethystMember(player) -> 20
            PlayerUtil.isTopazMember(player) -> 15
            PlayerUtil.isRubyMember(player) -> 10
            PlayerUtil.isBronzeMember(player) -> 5
            else -> 2
        }
    }

    private fun crush(player: Player, bone: BuriableBone) {
        if (player.getInt(Attribute.BONECRUSHER_CHARGES) <= 0) {
            player.message("You do not have any charges left to use your $name.")
            return
        }
        player.decInt(Attribute.BONECRUSHER_CHARGES, 1)
        player.addExperience(Skill.PRAYER, bone.xp / 2)
        if (AreaManager.CATACOMBS_OF_KOUREND_AREA.contains(player) || player.hasDragonbonerNecklaceEffect())
            player.increaseLevel(Skill.PRAYER, bone.pointsRestored)
    }

    private fun checkCharges(player: Player) {
        val charges = player.getInt(Attribute.BONECRUSHER_CHARGES)
        if (charges > 0)
            player.message("Your $name has @dre@" + Misc.format(charges) + " @bla@charge" + (if (charges > 1) "s" else "") + ".")
        else
            player.message("Your $name has no charges.")
    }
}