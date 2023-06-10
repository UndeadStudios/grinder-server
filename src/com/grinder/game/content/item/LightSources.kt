package com.grinder.game.content.item

import com.grinder.game.content.item.LightSources.LightSource.Companion.litItemIds
import com.grinder.game.entity.agent.combat.event.impl.ExtinguishLightEvent
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.event.impl.AreaChangedEvent
import com.grinder.game.model.CombatActions
import com.grinder.game.model.CommandActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.PlayerActions
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.impl.GiantMoleCave
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import com.grinder.net.packet.PacketConstants
import com.grinder.util.ItemID

/**
 * Handles light sources, such as candles and lanterns.
 *
 * These items have a lit and an unlit state, unlit items can be lit
 * by using a tinderbox on it, and lit items can be extinguished by interacting with it.
 *
 * There is also a [ExtinguishLightEvent] that can be submitted in combat
 * that will extinguish all light sources in the player's inventory/equipment.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/11/2020
 * @version 1.0
 */
object LightSources {

    enum class LightSource(val requiredLevel: Int, val unlitId: Int, val litId: Int, val opcode: Int){
        TORCH(1, ItemID.UNLIT_TORCH, ItemID.LIT_TORCH, PacketConstants.SECOND_ITEM_ACTION_OPCODE),
		CANDLE(1, ItemID.CANDLE, ItemID.LIT_CANDLE, PacketConstants.FIRST_ITEM_ACTION_OPCODE),
		BLACK_CANDLE(1, ItemID.BLACK_CANDLE, ItemID.LIT_BLACK_CANDLE, PacketConstants.FIRST_ITEM_ACTION_OPCODE),
		CANDLE_LANTERN(4, ItemID.CANDLE_LANTERN, ItemID.CANDLE_LANTERN_3, PacketConstants.EQUIP_ITEM_OPCODE),
		OIL_LAMP(12, ItemID.OIL_LAMP, ItemID.OIL_LAMP_3, PacketConstants.EQUIP_ITEM_OPCODE),
		OIL_LANTERN(26, ItemID.OIL_LANTERN, ItemID.OIL_LANTERN_3, PacketConstants.EQUIP_ITEM_OPCODE),
		BUG_LANTERN(49, ItemID.UNLIT_BUG_LANTERN, ItemID.LIT_BUG_LANTERN, PacketConstants.THIRD_ITEM_ACTION_OPCODE),
		BULLSEYE_LANTERN(49, ItemID.BULLSEYE_LANTERN, ItemID.BULLSEYE_LANTERN_3, PacketConstants.EQUIP_ITEM_OPCODE),
		SAPPHIRE_LANTERN(49, ItemID.SAPPHIRE_LANTERN, ItemID.SAPPHIRE_LANTERN_3, PacketConstants.FIRST_ITEM_ACTION_OPCODE),
		EMERALD_LANTERN(49, ItemID.EMERALD_LANTERN, ItemID.EMERALD_LANTERN_2, PacketConstants.THIRD_ITEM_ACTION_OPCODE),
		MINING_HELMET(65, ItemID.MINING_HELMET_2, ItemID.MINING_HELMET, PacketConstants.THIRD_ITEM_ACTION_OPCODE);

        override fun toString(): String {
            return name.substringAfterLast("_").toLowerCase()
        }

        companion object {
            val litItemIds = values().map { it.litId }.toIntArray()
        }
    }

    init {
        CommandActions.onCommand("lightsources", PlayerRights.HIGH_STAFF) {
            player.addInventoryItem(Item(ItemID.TINDERBOX, 1))
            LightSource.values().forEach {
                player.addInventoryItem(Item(it.litId, 1))
            }
            return@onCommand true
        }
        PlayerActions.onEvent(AreaChangedEvent::class) {
            if(event.newArea is GiantMoleCave)
                updateLightInterface(player)
            else if(event.oldArea is GiantMoleCave)
                player.packetSender.sendWalkableInterface(-1)
        }
        CombatActions.onEvent(ExtinguishLightEvent::class) {
            ifActorIsPlayer {
                for (source in LightSource.values()) {
                    val litSourceItem = Item(source.litId, 1)
                    val unlitSourceItem = Item(source.unlitId, 1)
                    it.replaceInventoryItem(litSourceItem.clone(), unlitSourceItem.clone())
                    it.replaceEquipmentItem(litSourceItem.clone(), unlitSourceItem.clone())
                }
                updateLightInterface(it)
            }
        }
        for(source in LightSource.values()) {
            ItemActions.onClick(source.litId) {
                if(getOpcode() == source.opcode) {
                    player.setInventoryItem(getSlot(), Item(source.unlitId, 1))
                    player.playSound(Sounds.EXTINGUISH_FIRE)
                    player.message("You extinguish the $source.")
                    updateLightInterface(player)
                    return@onClick true
                }
                return@onClick false
            }
            ItemActions.onItemOnItem(ItemID.TINDERBOX to source.unlitId) {
                player.setInventoryItem(getSlot(source.unlitId), Item(source.litId, 1))
                player.playSound(Sounds.BURN_LOGS_SOUND)
                player.message("You light the $source.")
                updateLightInterface(player)
                return@onItemOnItem true
            }
        }
    }

    fun updateLightInterface(player: Player){
        if (player.area == AreaManager.GIANT_MOLE_CAVE || AreaManager.GIANT_MOLE_CAVE.contains(player)) {
            val hasLitLightSource = player.inventory.containsAny(*litItemIds)
                    || player.equipment.containsAny(*litItemIds) || player.equipment.contains(ItemID.KANDARIN_HEADGEAR_1) ||
                    player.inventory.contains(ItemID.KANDARIN_HEADGEAR_1)
            player.packetSender.sendWalkableInterface(if (!hasLitLightSource) 18679 else -1)
        }
    }
}