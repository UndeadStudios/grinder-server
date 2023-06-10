package com.grinder.game.content.item.jewerly

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.getInt
import com.grinder.game.entity.incInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID

/**
 * https://oldschool.runescape.wiki/w/Amulet_of_chemistry
 *
 * "The amulet of glory is a jade amulet enchanted via the Lvl-2 Enchant spell."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/02/2021
 * @version 1.0
 */
object AmuletOfChemistry {

    private const val MAX_CHARGES = 5

    init {
        onSecondInventoryAction(ItemID.AMULET_OF_CHEMISTRY) {
            if(player.busy()){
                player.message("You can't do that when you're busy.")
                return@onSecondInventoryAction
            }
            DialogueBuilder(DialogueType.STATEMENT)
                .setText("${createChargesText(player)} Continue?")
                .add(DialogueType.OPTION)
                .firstOption("Yes.") {
                    it.removeInterfaces()
                    if(it.removeInventoryItem(getItem()?:return@firstOption)){
                        it.attributes.reset(Attribute.AMULET_OF_CHEMISTRY_CHARGES)
                        it.message("Your Amulet of chemistry has degraded.")
                    }
                }
                .addCancel("No.")
                .start(player)
        }
        onSecondContainerEquipmentAction(ItemID.AMULET_OF_CHEMISTRY) {
            player.statement(createChargesText(player))
        }
        onThirdInventoryAction(ItemID.AMULET_OF_CHEMISTRY) {
            player.statement(createChargesText(player))
        }
    }

    private fun createChargesText(player: Player): String {
        val chargesLeft = MAX_CHARGES - player.getInt(Attribute.AMULET_OF_CHEMISTRY_CHARGES)
        return "You still have @dre@$chargesLeft</col> charges before it breaks."
    }

    fun hasEquippedAndHasCharge(player: Player) : Boolean {
        if (player.equipment.contains(ItemID.AMULET_OF_CHEMISTRY))
            return player.getInt(Attribute.AMULET_OF_CHEMISTRY_CHARGES) < MAX_CHARGES
        return false
    }

    fun brewExtraDoseForPotion(player: Player) : Boolean {
        if (player.equipment.contains(ItemID.AMULET_OF_CHEMISTRY)){
            if (player.getInt(Attribute.AMULET_OF_CHEMISTRY_CHARGES) < MAX_CHARGES){
                if (player.incInt(Attribute.AMULET_OF_CHEMISTRY_CHARGES, 1) >= MAX_CHARGES){
                    player.equipment.reset(EquipmentConstants.AMULET_SLOT)
                    WeaponInterfaces.assign(player)
                    player.combat.reset(false)
                    EquipmentBonuses.update(player)
                    player.equipment.refreshItems()
                    player.message("Your Amulet of chemistry has shattered.")
                }
                return true
            }
        }
        return false
    }
}

