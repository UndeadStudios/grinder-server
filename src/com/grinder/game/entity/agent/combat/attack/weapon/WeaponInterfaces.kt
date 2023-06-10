package com.grinder.game.entity.agent.combat.attack.weapon

import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon.Companion.isCrossbow
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon.Companion.isInterface
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon.Companion.isShortbow
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType.*
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.value.WeaponFightTypeMapValueHolder
import com.grinder.game.model.item.name

/**
 * Handles weapon interface settings and assignation.
 *
 * @author lare96
 * @author Stan van der Bend
 * @author Mark_
 */
object WeaponInterfaces {

    var INSTANCE = WeaponInterfaces;
    
    /**
     * Assigns an interface to the combat sidebar based on the argued weapon.
     *
     * @      player that the interface will be assigned for.
     */
	fun assign(player: Player) {

        val packetSender = player.packetSender
        val combat = player.combat
        val equippedWeapon = player.equipment[EquipmentConstants.WEAPON_SLOT]

        if (equippedWeapon == null) {
            player.sendDevelopersMessage("Could not assigned weapon, equipped item is null!")
            return
        }

        val weapon = Weapon(equippedWeapon)


        packetSender.sendInteractionOption(if(weapon.isInterface(SNOWBALL)) "Throw-at" else "null", 5, false)

        if (weapon.isInterface(UNARMED)) {
            packetSender.sendTabInterface(0, weapon.getInterfaceId())
            packetSender.sendString(weapon.getNameLineId(), "Unarmed", true)
        } else if (weapon.isCrossbow() || weapon.isInterface(WHIP)) {
            packetSender.sendString(weapon.getNameLineId() - 1, "Weapon: ", true)
        }

        packetSender.sendTabInterface(0, weapon.getInterfaceId())
        packetSender.sendString(weapon.getNameLineId(), if (weapon.uses(UNARMED)) "Unarmed" else equippedWeapon.name(), true)

        combat.weapon = weapon
        SpecialAttackType.assign(player)
        SpecialAttackType.updateBar(player)

        // Set default attack style to aggressive!
        if (!player.isLoggedIn)
            return

        val oldWeaponFightType = player.weaponFightTypeHashMap.getOrDefault(weapon.weaponInterface, weapon.getFightType()[0])
        val weaponFightType = getFightTypeMap(player).getOrDefault(weapon.id, oldWeaponFightType)

        combat.fightType = weaponFightType

        packetSender.sendConfig(weaponFightType.parentId, weaponFightType.childId)
    }

    fun getFightTypeMap(player: Player) : HashMap<Int, WeaponFightType> {
        return player.attributes.getValue(Attribute.WEAPON_FIGHT_TYPE_CONFIG) { WeaponFightTypeMapValueHolder() }
    }

	fun handleButton(player: Player, buttonId: Int): Boolean {
        if (changeCombatSettings(player, buttonId)) {
            EquipmentBonuses.update(player)
            return true
        }
        return false
    }

    private fun changeCombatSettings(player: Player, button: Int): Boolean {
        val combat = player.combat
        val weapon = combat.weapon
        
        when (button) {
            1772 -> {
                when {
                    weapon.isShortbow() -> combat.fightType = SHORTBOW_ACCURATE
                    weapon.isInterface(LONGBOW) -> combat.fightType = LONGBOW_ACCURATE
                    weapon.isInterface(DARK_BOW) -> combat.fightType = DARKBOW_ACCURATE
                    weapon.isCrossbow() -> combat.fightType = CROSSBOW_ACCURATE
                    weapon.isInterface(KARILS_CROSSBOW) -> combat.fightType = KARILS_CROSSBOW_ACCURATE
                    weapon.isInterface(CRYSTALBOW) -> combat.fightType = CRYSTALBOW_ACCURATE
                    weapon.isInterface(CRAWS_BOW) -> combat.fightType = CRYSTALBOW_ACCURATE
                    weapon.isInterface(BOFA) -> combat.fightType = CRYSTALBOW_ACCURATE
                    weapon.isInterface(BLOWPIPE) -> combat.fightType = BLOWPIPE_ACCURATE
                }
                return true
            }
            1771 -> {
                when {
                    weapon.isShortbow() -> combat.fightType = SHORTBOW_RAPID
                    weapon.isInterface(LONGBOW) || weapon.isInterface(DARK_BOW) -> combat.fightType = LONGBOW_RAPID
                    weapon.isInterface(DARK_BOW) -> combat.fightType = DARKBOW_RAPID
                    weapon.isCrossbow() -> combat.fightType = CROSSBOW_RAPID
                    weapon.isInterface(CRYSTALBOW) -> combat.fightType = CRYSTALBOW_RAPID
                    weapon.isInterface(CRAWS_BOW) -> combat.fightType = CRYSTALBOW_RAPID
                    weapon.isInterface(BOFA) -> combat.fightType = CRYSTALBOW_RAPID
                    weapon.isInterface(KARILS_CROSSBOW) -> combat.fightType = KARILS_CROSSBOW_RAPID
                    weapon.isInterface(BLOWPIPE) -> combat.fightType = BLOWPIPE_RAPID
                }
                return true
            }
            1770 -> {
                when {
                    weapon.isShortbow() -> combat.fightType = SHORTBOW_LONGRANGE
                    weapon.isInterface(LONGBOW) -> combat.fightType = LONGBOW_LONGRANGE
                    weapon.isInterface(DARK_BOW) -> combat.fightType = DARKBOW_LONGRANGE
                    weapon.isCrossbow() -> combat.fightType = CROSSBOW_LONGRANGE
                    weapon.isInterface(CRYSTALBOW) -> combat.fightType = CROSSBOW_LONGRANGE
                    weapon.isInterface(CRAWS_BOW) -> combat.fightType = CROSSBOW_LONGRANGE
                    weapon.isInterface(BOFA) -> combat.fightType = CROSSBOW_LONGRANGE
                    weapon.isInterface(KARILS_CROSSBOW) -> combat.fightType = KARILS_CROSSBOW_LONGRANGE
                    weapon.isInterface(BLOWPIPE) -> combat.fightType = BLOWPIPE_LONGRANGE
                }
                return true
            }
            2282 -> {
                when {
                    weapon.isInterface(DAGGER) -> combat.fightType = DAGGER_STAB
                    weapon.isInterface(BONE_DAGGER) -> combat.fightType = BONE_DAGGER_STAB
                    weapon.isInterface(DRAGON_DAGGER) -> combat.fightType = DRAGON_DAGGER_STAB
                    weapon.isInterface(SWORD) -> combat.fightType = SWORD_STAB
                    weapon.isInterface(RAPIER) -> combat.fightType = RAPIER_STAB
                }
                return true
            }
            2285 -> {
                when {
                    weapon.isInterface(DAGGER) -> combat.fightType = DAGGER_LUNGE
                    weapon.isInterface(BONE_DAGGER) -> combat.fightType = BONE_DAGGER_LUNGE
                    weapon.isInterface(DRAGON_DAGGER) -> combat.fightType = DRAGON_DAGGER_LUNGE
                    weapon.isInterface(SWORD) -> combat.fightType = SWORD_LUNGE
                    weapon.isInterface(RAPIER) -> combat.fightType = RAPIER_LUNGE
                }
                return true
            }
            2284 -> {
                when {
                    weapon.isInterface(DAGGER) -> combat.fightType = DAGGER_SLASH
                    weapon.isInterface(BONE_DAGGER) -> combat.fightType = BONE_DAGGER_SLASH
                    weapon.isInterface(DRAGON_DAGGER) -> combat.fightType = DRAGON_DAGGER_SLASH
                    weapon.isInterface(SWORD) -> combat.fightType = SWORD_SLASH
                    weapon.isInterface(RAPIER) -> combat.fightType = RAPIER_SLASH
                }
                return true
            }
            2283 -> {
                when {
                    weapon.isInterface(DAGGER) -> combat.fightType = DAGGER_BLOCK
                    weapon.isInterface(BONE_DAGGER) -> combat.fightType = BONE_DAGGER_BLOCK
                    weapon.isInterface(BONE_DAGGER) -> combat.fightType = BONE_DAGGER_BLOCK
                    weapon.isInterface(DRAGON_DAGGER) -> combat.fightType = DRAGON_DAGGER_BLOCK
                    weapon.isInterface(SWORD) -> combat.fightType = SWORD_BLOCK
                    weapon.isInterface(RAPIER) -> combat.fightType = RAPIER_BLOCK
                }
                return true
            }
            2429 -> {
                if (weapon.isInterface(SCIMITAR)) combat.fightType = SCIMITAR_CHOP
                else if (weapon.isInterface(LONGSWORD) || weapon.isInterface(ARCLIGHT)) combat.fightType = LONGSWORD_CHOP
                return true
            }
            2432 -> {
                if (weapon.isInterface(SCIMITAR)) combat.fightType = SCIMITAR_SLASH
                else if (weapon.isInterface(LONGSWORD) || weapon.isInterface(ARCLIGHT)) combat.fightType = LONGSWORD_SLASH
                return true
            }
            2431 -> {
                if (weapon.isInterface(SCIMITAR)) combat.fightType = SCIMITAR_LUNGE
                else if (weapon.isInterface(LONGSWORD) || weapon.isInterface(ARCLIGHT)) combat.fightType = LONGSWORD_LUNGE
                return true
            }
            2430 -> {
                if (weapon.isInterface(SCIMITAR)) combat.fightType = SCIMITAR_BLOCK
                else if (weapon.isInterface(LONGSWORD) || weapon.isInterface(ARCLIGHT)) combat.fightType = LONGSWORD_BLOCK
                return true
            }
            3802 -> {
                when {
                    weapon.isInterface(VERACS_FLAIL) -> combat.fightType = VERACS_FLAIL_POUND
                    weapon.isInterface(VIGGORA_CHAINMACE) -> combat.fightType = VIGGORA_POUND
                    weapon.isInterface(INQUISITORS_MACE) -> combat.fightType = INQ_MACE_POUND
                    weapon.isInterface(CANE) -> combat.fightType = CANE_POUND
                    else -> combat.fightType = MACE_POUND
                }
                return true
            }
            3805 -> {
                when {
                    weapon.isInterface(VERACS_FLAIL) -> combat.fightType = VERACS_FLAIL_PUMMEL
                    weapon.isInterface(VIGGORA_CHAINMACE) -> combat.fightType = VIGGORA_PUMMEL
                    weapon.isInterface(INQUISITORS_MACE) -> combat.fightType = INQ_MACE_PUMMEL
                    weapon.isInterface(CANE) -> combat.fightType = CANE_PUMMEL
                    else -> combat.fightType = MACE_PUMMEL
                }
                return true
            }
            3804 -> {
                when {
                    weapon.isInterface(VERACS_FLAIL) -> combat.fightType = VERACS_FLAIL_SPIKE
                    weapon.isInterface(VIGGORA_CHAINMACE) -> combat.fightType = VIGGORA_SPIKE
                    weapon.isInterface(INQUISITORS_MACE) -> combat.fightType = INQ_MACE_SPIKE
                    weapon.isInterface(CANE) -> combat.fightType = CANE_SPIKE
                    else -> combat.fightType = MACE_SPIKE
                }
                return true
            }
            3803 -> {
                when {
                    weapon.isInterface(VERACS_FLAIL) -> combat.fightType = VERACS_FLAIL_BLOCK
                    weapon.isInterface(VIGGORA_CHAINMACE) -> combat.fightType = VIGGORA_BLOCK
                    weapon.isInterface(INQUISITORS_MACE) -> combat.fightType = INQ_MACE_BLOCK
                    weapon.isInterface(CANE) -> combat.fightType = CANE_BLOCK
                    else -> combat.fightType = MACE_BLOCK
                }
                return true
            }
            4454 -> {
                when {
                    weapon.isInterface(KNIFE) -> combat.fightType = KNIFE_ACCURATE
                    weapon.isInterface(OBBY_RINGS) -> combat.fightType = OBBY_RING_ACCURATE
                    weapon.isInterface(THROWNAXE) -> combat.fightType = THROWNAXE_ACCURATE
                    weapon.isInterface(DART) -> combat.fightType = DART_ACCURATE
                    weapon.isInterface(JAVELIN) -> combat.fightType = JAVELIN_ACCURATE
                }
                return true
            }
            4453 -> {
                when {
                    weapon.isInterface(KNIFE) -> combat.fightType = KNIFE_RAPID
                    weapon.isInterface(OBBY_RINGS) -> combat.fightType = OBBY_RING_RAPID
                    weapon.isInterface(THROWNAXE) -> combat.fightType = THROWNAXE_RAPID
                    weapon.isInterface(DART) -> combat.fightType = DART_RAPID
                    weapon.isInterface(JAVELIN) -> combat.fightType = JAVELIN_RAPID
                }
                return true
            }
            4452 -> {
                when {
                    weapon.isInterface(KNIFE) -> combat.fightType = KNIFE_LONGRANGE
                    weapon.isInterface(OBBY_RINGS) -> combat.fightType = OBBY_RING_LONGRANGE
                    weapon.isInterface(THROWNAXE) -> combat.fightType = THROWNAXE_LONGRANGE
                    weapon.isInterface(DART) -> combat.fightType = DART_LONGRANGE
                    weapon.isInterface(JAVELIN) -> combat.fightType = JAVELIN_LONGRANGE
                }
                return true
            }
            4685 -> {
                when {
                    weapon.isInterface(DRAGON_LANCE) -> combat.fightType = DRAGON_LANCE_LUNGE
                    weapon.isInterface(SPEAR) -> combat.fightType = SPEAR_LUNGE
                    weapon.isInterface(HASTA) -> combat.fightType = HASTA_LUNGE
                }
                return true
            }
            4688 -> {
                when {
                    weapon.isInterface(DRAGON_LANCE) -> combat.fightType = DRAGON_LANCE_SWIPE
                    weapon.isInterface(SPEAR) -> combat.fightType = SPEAR_SWIPE
                    weapon.isInterface(HASTA) -> combat.fightType = HASTA_SWIPE
                }
                return true
            }
            4687 -> {
                when {
                    weapon.isInterface(DRAGON_LANCE) -> combat.fightType = DRAGON_LANCE_POUND
                    weapon.isInterface(SPEAR) -> combat.fightType = SPEAR_POUND
                    weapon.isInterface(HASTA) -> combat.fightType = HASTA_POUND
                }
                return true
            }
            4686 -> {
                when {
                    weapon.isInterface(DRAGON_LANCE) -> combat.fightType = DRAGON_LANCE_BLOCK
                    weapon.isInterface(SPEAR) -> combat.fightType = SPEAR_BLOCK
                    weapon.isInterface(HASTA) -> combat.fightType = HASTA_BLOCK
                }
                return true
            }
            4711 -> {
                when {
                    weapon.isInterface(GODSWORD) -> combat.fightType = GODSWORD_CHOP
                    weapon.isInterface(TWO_HANDED_SWORD) -> combat.fightType = TWOHANDEDSWORD_CHOP
                    weapon.isInterface(SARADOMIN_SWORD) -> combat.fightType = SARADOMIN_SWORD_CHOP
                }
                return true
            }
            4714 -> {
                when {
                    weapon.isInterface(GODSWORD) -> combat.fightType = GODSWORD_SLASH
                    weapon.isInterface(TWO_HANDED_SWORD) -> combat.fightType = TWOHANDEDSWORD_SLASH
                    weapon.isInterface(SARADOMIN_SWORD) -> combat.fightType = SARADOMIN_SWORD_SLASH
                }
                return true
            }
            4713 -> {
                when {
                    weapon.isInterface(GODSWORD) -> combat.fightType = GODSWORD_SMASH
                    weapon.isInterface(TWO_HANDED_SWORD) -> combat.fightType = TWOHANDEDSWORD_SMASH
                    weapon.isInterface(SARADOMIN_SWORD) -> combat.fightType = SARADOMIN_SWORD_SMASH
                    weapon.isInterface(ABYSSAL_BLUDGEON) -> combat.fightType = ABYSSAL_BLUDGEON_SMASH
                }
                return true
            }
            4712 -> {
                when {
                    weapon.isInterface(GODSWORD) -> combat.fightType = GODSWORD_BLOCK
                    weapon.isInterface(TWO_HANDED_SWORD) -> combat.fightType = TWOHANDEDSWORD_BLOCK
                    weapon.isInterface(SARADOMIN_SWORD) -> combat.fightType = SARADOMIN_SWORD_BLOCK
                }
                return true
            }
            5576 -> {
                combat.fightType = PICKAXE_SPIKE
                return true
            }
            5579 -> {
                combat.fightType = PICKAXE_IMPALE
                return true
            }
            5578 -> {
                combat.fightType = PICKAXE_SMASH
                return true
            }
            5577 -> {
                combat.fightType = PICKAXE_BLOCK
                return true
            }
            7768 -> {
                when {
                    weapon.isInterface(CLAWS) -> combat.fightType = CLAWS_CHOP
                    weapon.isInterface(BOXING) -> combat.fightType = BOXING_CHOP
                }
                return true
            }
            7771 -> {
                when {
                    weapon.isInterface(CLAWS) -> combat.fightType = CLAWS_SLASH
                    weapon.isInterface(BOXING) -> combat.fightType = BOXING_SLASH
                }
                return true
            }
            7770 -> {
                when {
                    weapon.isInterface(CLAWS) -> combat.fightType = CLAWS_LUNGE
                    weapon.isInterface(BOXING) -> combat.fightType = BOXING_LUNGE
                }
                return true
            }
            7769 -> {
                when {
                    weapon.isInterface(CLAWS) -> combat.fightType = CLAWS_BLOCK
                    weapon.isInterface(BOXING) -> combat.fightType = BOXING_BLOCK
                }
                return true
            }
            8466 -> {
                combat.fightType = HALBERD_JAB
                return true
            }
            8468 -> {
                combat.fightType = HALBERD_SWIPE
                return true
            }
            8467 -> {
                combat.fightType = HALBERD_FEND
                return true
            }
            5861 -> {
                combat.fightType = UNARMED_BLOCK
                return true
            }
            5862 -> {
                combat.fightType = UNARMED_KICK
                return true
            }
            5860 -> {
                combat.fightType = UNARMED_PUNCH
                return true
            }
            12298 -> {
                combat.fightType = WHIP_FLICK
                return true
            }
            12297 -> {
                combat.fightType = WHIP_LASH
                return true
            }
            12296 -> {
                combat.fightType = WHIP_DEFLECT
                return true
            }
            336 -> {
                combat.fightType = STAFF_BASH
                return true
            }
            335 -> {
                combat.fightType = STAFF_POUND
                return true
            }
            334 -> {
                combat.fightType = STAFF_FOCUS
                return true
            }
            65021 -> {
                combat.fightType = STAFF_BASH
                return true
            }
            65020 -> {
                combat.fightType = STAFF_POUND
                return true
            }
            65019 -> {
                combat.fightType = STAFF_FOCUS
                return true
            }
            433 -> {
                when {
                    weapon.isInterface(GRANITE_MAUL) -> combat.fightType = GRANITE_MAUL_POUND
                    weapon.isInterface(MAUL) -> combat.fightType = MAUL_POUND
                    weapon.isInterface(MEAT_TENDERISER) -> combat.fightType = TENDERISER_POUND
                    weapon.isInterface(WARHAMMER) -> combat.fightType = WARHAMMER_POUND
                    weapon.isInterface(TORAG_HAMMER) -> combat.fightType = TORAG_WARHAMMER_POUND
                    weapon.isInterface(HUNTING_KNIFE) -> combat.fightType = HUNTING_KNIFE_POUND
                    weapon.isInterface(BARRELCHEST_ANCHOR) -> combat.fightType = BARRELCHEST_POUND
                    weapon.isInterface(WARHAMMER_CASKET) -> combat.fightType = WARHAMMER_CASKET_POUND
                    weapon.isInterface(ELDER_MAUL) -> combat.fightType = ELDER_MAUL_POUND
                    weapon.isInterface(BIRTHDAY_BALLOON) -> combat.fightType = BIRTHDAY_BALLOON_POUND
                    weapon.isInterface(BIRTHDAY_CAKE) -> combat.fightType = BIRTHDAY_CAKE_POUND
                }
                return true
            }
            432 -> {
                when {
                    weapon.isInterface(GRANITE_MAUL) -> combat.fightType = GRANITE_MAUL_PUMMEL
                    weapon.isInterface(MAUL) -> combat.fightType = MAUL_PUMMEL
                    weapon.isInterface(MEAT_TENDERISER) -> combat.fightType = TENDERISER_PUMMEL
                    weapon.isInterface(WARHAMMER) -> combat.fightType = WARHAMMER_PUMMEL
                    weapon.isInterface(TORAG_HAMMER) -> combat.fightType = TORAG_WARHAMMER_PUMMEL
                    weapon.isInterface(HUNTING_KNIFE) -> combat.fightType = HUNTING_KNIFE_PUMMEL
                    weapon.isInterface(BARRELCHEST_ANCHOR) -> combat.fightType = BARRELCHEST_PUMMEL
                    weapon.isInterface(WARHAMMER_CASKET) -> combat.fightType = WARHAMMER_CASKET_PUMMEL
                    weapon.isInterface(ELDER_MAUL) -> combat.fightType = ELDER_MAUL_PUMMEL
                    weapon.isInterface(BIRTHDAY_BALLOON) -> combat.fightType = BIRTHDAY_BALLOON_PUMMEL
                    weapon.isInterface(BIRTHDAY_CAKE) -> combat.fightType = BIRTHDAY_CAKE_PUMMEL
                }
                return true
            }
            431 -> {
                when {
                    weapon.isInterface(GRANITE_MAUL) -> combat.fightType = GRANITE_MAUL_BLOCK
                    weapon.isInterface(MAUL) -> combat.fightType = MAUL_BLOCK
                    weapon.isInterface(MEAT_TENDERISER) -> combat.fightType = TENDERISER_BLOCK
                    weapon.isInterface(WARHAMMER) -> combat.fightType = WARHAMMER_BLOCK
                    weapon.isInterface(TORAG_HAMMER) -> combat.fightType = TORAG_WARHAMMER_BLOCK
                    weapon.isInterface(HUNTING_KNIFE) -> combat.fightType = HUNTING_KNIFE_BLOCK
                    weapon.isInterface(BARRELCHEST_ANCHOR) -> combat.fightType = BARRELCHEST_BLOCK
                    weapon.isInterface(WARHAMMER_CASKET) -> combat.fightType = WARHAMMER_CASKET_BLOCK
                    weapon.isInterface(ELDER_MAUL) -> combat.fightType = ELDER_MAUL_BLOCK
                    weapon.isInterface(BIRTHDAY_BALLOON) -> combat.fightType = BIRTHDAY_BALLOON_BLOCK
                    weapon.isInterface(BIRTHDAY_CAKE) -> combat.fightType = BIRTHDAY_CAKE_BLOCK
                }
                return true
            }
            782 -> {
                when {
                    weapon.isInterface(SCYTHE_VITUR) -> combat.fightType = SCYTHE_VITUR_REAP
                    weapon.isInterface(SCYTHE) -> combat.fightType = SCYTHE_REAP
                }
                return true
            }
            784 -> {
                when {
                    weapon.isInterface(SCYTHE_VITUR) -> combat.fightType = SCYTHE_VITUR_CHOP
                    weapon.isInterface(SCYTHE) -> combat.fightType = SCYTHE_CHOP
                }
                return true
            }
            785 -> {
                when {
                    weapon.isInterface(SCYTHE_VITUR) -> combat.fightType = SCYTHE_VITUR_JAB
                    weapon.isInterface(SCYTHE) -> combat.fightType = SCYTHE_JAB
                }
                return true
            }
            783 -> {
                when {
                    weapon.isInterface(SCYTHE_VITUR) -> combat.fightType = SCYTHE_VITUR_BLOCK
                    weapon.isInterface(SCYTHE) -> combat.fightType = SCYTHE_BLOCK
                }
                return true
            }
            1704 -> {
                when {
                    weapon.isInterface(GREATAXE) -> combat.fightType = GREATAXE_CHOP
                    weapon.isInterface(LEAF_BLADED_BATTLEAXE) -> combat.fightType = LEAF_BLADED_BATTLEAXE_CHOP
                    else -> combat.fightType = BATTLEAXE_CHOP
                }
                return true
            }
            1707 -> {
                when {
                    weapon.isInterface(GREATAXE) -> combat.fightType = GREATAXE_HACK
                    weapon.isInterface(LEAF_BLADED_BATTLEAXE) -> combat.fightType = LEAF_BLADED_BATTLEAXE_HACK
                    else -> combat.fightType = BATTLEAXE_HACK
                }
                return true
            }
            1706 -> {
                when {
                    weapon.isInterface(GREATAXE) -> combat.fightType = GREATAXE_SMASH
                    weapon.isInterface(LEAF_BLADED_BATTLEAXE) -> combat.fightType = LEAF_BLADED_BATTLEAXE_SMASH
                    else -> combat.fightType = BATTLEAXE_SMASH
                }
                return true
            }
            1705 -> {
                when {
                    weapon.isInterface(GREATAXE) -> combat.fightType = GREATAXE_BLOCK
                    weapon.isInterface(LEAF_BLADED_BATTLEAXE) -> combat.fightType = LEAF_BLADED_BATTLEAXE_BLOCK
                    else -> combat.fightType = BATTLEAXE_BLOCK
                }
                return true
            }
            24059 -> {
                combat.fightType = CHIN_SHORT
                return true
            }
            24060 -> {
                combat.fightType = CHIN_MED
                return true
            }
            24061 -> {
                combat.fightType = CHIN_LONG
                return true
            }
            24078 -> {
                when {
                    weapon.isInterface(LIZARD) -> combat.fightType = LIZARD_SCORCH
                    else -> combat.fightType = SALAMANDER_SCORCH
                }
                return true
            }
            24079 -> {
                when {
                    weapon.isInterface(LIZARD) -> combat.fightType = LIZARD_FLARE
                    else -> combat.fightType = SALAMANDER_FLARE
                }
                return true
            }
            24080 -> {
                when {
                    weapon.isInterface(LIZARD) -> combat.fightType = LIZARD_BLAZE
                    else -> combat.fightType = SALAMANDER_BLAZE
                }
                return true
            }
            65002, 29138, 29038, 29063, 29113, 29163, 29188, 29213, 29238, 30007, 48023, 33033, 30108, 7473, 7562, 7487, 7788, 8481, 7612, 7587, 7662, 7462, 7548, 7687, 7537, 7623, 12322, 7637, 12311, 155, 7723, 7498 -> {
                SpecialAttackType.activate(player)
                return true
            }
        }
        return false
    }

}