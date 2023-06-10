package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants.*
import com.grinder.game.model.commands.Command
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID

/**
 * This [Command] is a developer-only command, which should only be used for combat-testing purposes.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/09/2019
 * @version 1.0
 */
class CombatSetupCommand : Command {

    override fun getSyntax() = ""

    override fun getDescription() = "Gives your account combat setup items."

    override fun execute(player: Player, command: String, parts: Array<out String>) {
        DialogueBuilder(DialogueType.OPTION)
                .firstOption("Ranged") {
                    it.equipment[HEAD_SLOT] = Item(ItemID.ARCHER_HELM)
                    it.equipment[CAPE_SLOT] = Item(ItemID.AVAS_ACCUMULATOR)
                    it.equipment[AMMUNITION_SLOT] = Item(ItemID.RUBY_BOLTS, 10_000)
                    it.equipment[AMULET_SLOT] = Item(ItemID.AMULET_OF_FURY)
                    it.equipment[BODY_SLOT] = Item(ItemID.BLACK_DHIDE_BODY_G_)
                    it.equipment[WEAPON_SLOT] = Item(ItemID.ARMADYL_CROSSBOW)
                    it.equipment[HANDS_SLOT] = Item(ItemID.RANGER_GLOVES)
                    it.equipment[RING_SLOT] = Item(ItemID.ARCHERS_RING)
                    it.equipment[LEG_SLOT] = Item(ItemID.BLACK_DHIDE_CHAPS_G_)
                    it.equipment[FEET_SLOT] = Item(ItemID.RANGER_BOOTS)
                    it.equipment.refreshItems()
                    EquipmentBonuses.update(it)
                    WeaponInterfaces.assign(it)
                    player.packetSender.sendInterfaceReset()
                    player.updateAppearance()
                }.secondOption("Mage"){
                    it.equipment[HEAD_SLOT] = Item(ItemID.INFINITY_HAT)
                    it.equipment[CAPE_SLOT] = Item(ItemID.MAGIC_CAPE_T_)
                    it.equipment[AMULET_SLOT] = Item(ItemID.AMULET_OF_MAGIC_T_)
                    it.equipment[BODY_SLOT] = Item(ItemID.INFINITY_TOP)
                    it.equipment[WEAPON_SLOT] = Item(ItemID.MASTER_WAND)
                    it.equipment[SHIELD_SLOT] = Item(ItemID.ELYSIAN_SPIRIT_SHIELD)
                    it.equipment[HANDS_SLOT] = Item(ItemID.INFINITY_GLOVES)
                    it.equipment[RING_SLOT] = Item(ItemID.RING_OF_WEALTH)
                    it.equipment[LEG_SLOT] = Item(ItemID.INFINITY_BOTTOMS)
                    it.equipment[FEET_SLOT] = Item(ItemID.INFINITY_BOOTS)
                    it.equipment.refreshItems()
                    it.sendMessage("Do ::runes to spawn runes")
                    EquipmentBonuses.update(it)
                    WeaponInterfaces.assign(it)
                    player.packetSender.sendInterfaceReset()
                    player.updateAppearance()
                }.thirdOption("melee") {
                    it.equipment[HEAD_SLOT] = Item(ItemID.HELM_OF_NEITIZNOT)
                    it.equipment[CAPE_SLOT] = Item(ItemID.FIRE_CAPE)
                    it.equipment[AMULET_SLOT] = Item(ItemID.AMULET_OF_FURY)
                    it.equipment[BODY_SLOT] = Item(ItemID.FIGHTER_TORSO)
                    it.equipment[WEAPON_SLOT] = Item(ItemID.ABYSSAL_WHIP)
                    it.equipment[SHIELD_SLOT] = Item(ItemID.DRAGONFIRE_SHIELD)
                    it.equipment[HANDS_SLOT] = Item(ItemID.BARROWS_GLOVES)
                    it.equipment[RING_SLOT] = Item(ItemID.RING_OF_WEALTH)
                    it.equipment[LEG_SLOT] = Item(ItemID.DRAGON_PLATELEGS)
                    it.equipment[FEET_SLOT] = Item(ItemID.DRAGON_BOOTS)
                    it.equipment.refreshItems()
                    EquipmentBonuses.update(it)
                    WeaponInterfaces.assign(it)
                    player.packetSender.sendInterfaceReset()
                    player.updateAppearance()
                }
                .addCancel().start(player)
    }

    override fun canUse(player: Player?) = player?.rights?.isHighStaff?:false
}