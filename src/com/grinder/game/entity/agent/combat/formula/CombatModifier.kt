package com.grinder.game.entity.agent.combat.formula

import com.grinder.game.content.item.charging.impl.CrawsBow
import com.grinder.game.content.item.charging.impl.ThammaronsSceptre
import com.grinder.game.content.item.charging.impl.ViggorasChainmace
import com.grinder.game.content.skill.skillable.impl.magic.ElementStaffType
import com.grinder.game.content.skill.skillable.impl.slayer.hasAnySlayerHelmet
import com.grinder.game.content.skill.skillable.impl.slayer.hasBlackMask
import com.grinder.game.content.skill.skillable.impl.slayer.hasImbuedMask
import com.grinder.game.content.skill.skillable.impl.slayer.hasImbuedSlayerHelmet
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackMode
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType.SHIELD_BASH
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.inWilderness
import com.grinder.game.entity.agent.npc.monster.MonsterRace.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil.*
import com.grinder.game.model.MagicSpellbook
import com.grinder.game.model.Skill
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.EquipSlot
import kotlin.math.pow

/**
 * This class is a helper for [CombatBonuses].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/04/2020
 * @version 1.0
 */
object CombatModifier {

    /**
     * Determines the extra strength bonus for wearing void.
     */
    fun armourStrengthBonus(
            fightType: WeaponFightType,
            type: AttackType,
            equipment: Equipment
    ): Double {
        return when {
            isWearingAnyVoidSet(equipment, type) -> {
                when (type) {
                    AttackType.MELEE -> 1.10
                    AttackType.RANGED -> if (isWearingEliteVoidSet(equipment, type)) 1.125 else 1.10
                    else -> if (isWearingEliteVoidSet(equipment, type)) 1.025 else 0.0
                }
            }
            isWearingSuperiorVoidSet(equipment, type) -> {
                when (type) {
                    AttackType.MELEE -> 1.175 //17.5 % melee boost from previous 10 %
                    AttackType.RANGED -> 1.15 // 15 % range boost from previous 12.5 %
                    else -> 1.10 // 10 % mage boost from previous 2.5 %
                }
            }
            isWearingAnyInquisitors(equipment) -> {
                when (fightType.mode) {
                    AttackMode.CRUSH -> getInquisitorsBonus(equipment)
                    else -> 1.0
                }
            }
            else -> 1.0
        }
    }

    /**
     * Determines the extra accuracy bonus for wearing void.
     */
    fun armourAccuracyBonus(
            fightType: WeaponFightType,
            type: AttackType,
            equipment: Equipment
    ): Double {
        return when {
            isWearingAnyVoidSet(equipment, type) -> {
                when (type) {
                    AttackType.MELEE -> 1.10
                    AttackType.RANGED -> 1.10
                    AttackType.MAGIC -> 1.45
                    else -> 1.0
                }
            }
            isWearingSuperiorVoidSet(equipment, type) -> {
                when (type) {
                    AttackType.MELEE -> 1.175 //17.5 % melee boost from previous 10 %
                    AttackType.RANGED -> 1.15 // 15 % range boost from previous 12.5 %
                    else -> 1.10 // 10 % mage boost from previous 2.5 %
                }
            }
            isWearingAnyInquisitors(equipment) -> {
                when (fightType.mode) {
                    AttackMode.CRUSH -> getInquisitorsBonus(equipment)
                    else -> 1.0
                }
            }
            else -> 1.0
        }
    }

    /**
     * Determines the first multiplier applied to attack roll.
     */
    fun primaryAccuracyMultiplier(context: AttackContext): Double {

        val equipment = context.attackerEquipment ?: return 1.0
        when {
            context.used(AttackType.MELEE) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                    if (hasEnchantedSalveAmulet(equipment)) return 1.20
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasSalveAmulet(equipment)) return 1.15
                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    if (hasBlackMask(equipment) || hasImbuedMask(equipment) || hasAnySlayerHelmet(equipment)) return 1.1667
                }
            }
            context.used(AttackType.RANGED) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasImbuedSalveAmulet(equipment)) return 1.15
                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    if (hasImbuedMask(equipment) || hasImbuedSlayerHelmet(equipment)) return 1.15
                }
            }
            context.used(AttackType.MAGIC) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasImbuedSalveAmulet(equipment)) return 1.15
                    if (context.used(CombatSpellType.TELEBLOCK)) return 1.25
                    if (context.used(ItemID.KODAI_WAND)) return 1.10;
                    if (context.used(ItemID.STAFF_OF_LIGHT)) return 1.10;
                    if (context.used(ItemID.AHRIMS_STAFF)) return 1.05;
                    if (context.used(ItemID.ELDRITCH_NIGHTMARE_STAFF) || context.used(ItemID.VOLATILE_NIGHTMARE_STAFF) || context.used(ItemID.HARMONISED_NIGHTMARE_STAFF) || context.used(
                            ItemID.NIGHTMARE_STAFF)) return 1.10;

                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    if (hasImbuedMask(equipment) || hasImbuedSlayerHelmet(equipment)) return 1.15
                }
            }
        }
        return 1.0
    }

    /**
     * Determines the second multiplier applied to damage output.
     */
    fun secondaryAccuracyMultiplier(targetSnapshot: CombatSnapshot): Double {
        val context = targetSnapshot.context ?: return 1.0
        val strategy = context.strategy

        if (strategy is SpecialAttack)
            return strategy.secondaryAccuracyModifier(context)

        when {

            context.used(AttackType.MELEE) -> {
                if (context.used(SHIELD_BASH)) return 1.20
                context.attackerEquipment?.let {
                    when {
                        context.isFightingAny(DEMON) -> {
                            if (it.contains(ItemID.ARCLIGHT))
                                return 1.70
                            else if (it.contains(ItemID.SILVERLIGHT) || it.contains(ItemID.SILVERLIGHT_2))
                                return 1.25
                            else if (it.contains(ItemID.DARKLIGHT))
                                return 1.50
                        }
                        context.isFightingAny(DRAGON, WYVERN, OLM) -> {
                            if (it.contains(ItemID.DRAGON_HUNTER_LANCE))
                                return 1.20
                        }
                        /*context.isFighting(GOLEM) -> {
                            if (it.contains(ItemID.BARRONITE_MACE))
                                return 1.25
                        }*/
                        context.isFightingAny(KURASK, TUROTH) -> {
                            if (context.isFightingSlayerTask) {
                                if (it.contains(ItemID.LEAF_BLADED_BATTLEAXE))
                                    return 1.175
                            }
                        }
                        isWearingObsidianSet(it) && hasAnyObsidianWeapons(it) -> return 1.10
                    }
                }
            }
            context.used(AttackType.RANGED) -> {
                when {
                    context.isFightingAny(DRAGON, WYVERN, OLM) && (context.used(RangedWeapon.DRAGON_HUNTER_CROSSBOW) || context.used(RangedWeapon.DRAGON_HUNTER_BOW)) -> {
                        if (context.used(RangedWeapon.DRAGON_HUNTER_CROSSBOW))
                            return 1.30
                        if (context.used(RangedWeapon.DRAGON_HUNTER_BOW))
                            return 1.65
                    }
                    context.used(RangedWeapon.TWISTED_BOW) -> {
                        val magic = targetSnapshot.skills.getLevel(Skill.MAGIC)
                        return (140.0
                                + ((((3.0 * magic) - 10.0) / 100.0))
                                - ((((3.0 * magic) / 10) - 100.0).pow(2.0) / 100.0))
                                .coerceAtMost(140.0)
                                .div(100.0)
                    }
                    context.used(RangedWeapon.DRAGON_HUNTER_BOW) -> {
                        return 1.35;
                    }
                }
            }
            context.used(AttackType.MAGIC) -> {
                context.attackerEquipment?.let {
                    if (context.used(MagicSpellbook.NORMAL)) {
                        if (isWieldingStaff(it, ElementStaffType.SMOKE)) {
                            return 1.10
                        }
                    }
                }
            }
        }

        return 1.0
    }

    /**
     * Determines the third multiplier applied to the attack roll.
     */
    fun tertiaryAccuracyMultiplier(attacker: Agent, targetSnapshot: CombatSnapshot): Double {
        val context = targetSnapshot.context ?: return 1.0
        val strategy = context.strategy;

        if (strategy is SpecialAttack)
            return strategy.tertiaryAccuracyModifier(context)

        if (context.used(AttackType.RANGED)) {
            if(attacker is Player) {
            // Craw's bow
            when {
                context.used(CRAWS_BOW_ITEM_ID) -> {
                    if (attacker is Player) {
                        val weapon = attacker.equipment[EquipSlot.WEAPON]
                        if(CrawsBow.getCharges(weapon) > 0) {
                            CrawsBow.decrementCharges(attacker, weapon)

                            return if (context.isFightingInWilderness
                                && context.isFightingNpc) {
                                1.5
                            } else {
                                1.0
                            }
                    }
                }
                }

                context.usedAny(
                        ItemID.CHINCHOMPA_2,
                        ItemID.RED_CHINCHOMPA_2,
                        ItemID.BLACK_CHINCHOMPA
                ) -> {
                    if (attacker.combat.target == targetSnapshot.agent) {
                        val distance = attacker.position.getDistance(targetSnapshot.agent.position)
                        return when (context.fightType) {
                            WeaponFightType.CHIN_SHORT -> {
                                when (distance) {
                                    in 0..3 -> 1.0
                                    in 4..6 -> 0.75
                                    else -> 0.50
                                }
                            }
                            WeaponFightType.CHIN_MED -> {
                                when (distance) {
                                    in 0..3 -> 0.75
                                    in 4..6 -> 1.0
                                    else -> 0.75
                                }
                            }
                            else -> {
                                when (distance) {
                                    in 0..3 -> 0.50
                                    in 4..6 -> 0.75
                                    else -> 1.0
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        if(attacker is Player) {

            val weapon = attacker.equipment[EquipSlot.WEAPON]

              if (weapon.id == ItemID.VIGGORA_CHAINMACE && ViggorasChainmace.getCharges(weapon) > 0) {
                  ViggorasChainmace.decrementCharges(attacker, weapon)
                  return if (context.isFightingInWilderness
                      && context.isFightingNpc) {
                      1.5
                  } else {
                      1.0
                  }
              }

              if (weapon.id == 22555 && ThammaronsSceptre.getCharges(weapon) > 0) {
                  return if (context.isFightingInWilderness
                      && context.isFightingNpc) {
                      2.0
                  } else {
                      1.0
                  }
              }
        }


        return 1.0
    }

    /**
     * Determines the first multiplier applied to damage output.
     */
    fun primaryDamageMultiplier(context: AttackContext): Double {

        val equipment = context.attackerEquipment ?: return 1.0
        when {
            context.used(AttackType.MELEE) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                   // println("REACHED TOP")
                    if (hasEnchantedSalveAmulet(equipment)) return 1.20
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasSalveAmulet(equipment)) return 1.15
                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    //println("REACHED BELOW")
                    if (hasBlackMask(equipment) || hasImbuedMask(equipment) || hasAnySlayerHelmet(equipment)) return 1.1667
                }
            }
            context.used(AttackType.RANGED) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasImbuedSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasImbuedSalveAmulet(equipment)) return 1.15
                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    if (hasImbuedMask(equipment) || hasImbuedSlayerHelmet(equipment)) return 1.15
                }
            }
            context.used(AttackType.MAGIC) -> {
                if ((hasEnchantedSalveAmulet(equipment) || hasSalveAmulet(equipment) || hasImbuedSalveAmulet(equipment) || hasEnchantedImbuedSalveAmulet(equipment)) && (context.isFighting(UNDEAD) || context.isFightingAny(UNDEAD_COMBAT_DUMMY))) {
                    if (hasEnchantedImbuedSalveAmulet(equipment)) return 1.20
                    if (hasImbuedSalveAmulet(equipment)) return 1.15
                } else if (context.isFightingSlayerTask || context.isFightingAny(UNDEAD_COMBAT_DUMMY)) {
                    if (hasImbuedMask(equipment) || hasImbuedSlayerHelmet(equipment)) return 1.15
                }
            }
        }
        return 1.0
    }

    /**
     * Determines the second multiplier applied to damage output.
     */
    fun secondaryDamageMultiplier(targetSnapshot: CombatSnapshot): Double {
        val context = targetSnapshot.context ?: return 1.0
        val strategy = context.strategy;

        if (strategy is SpecialAttack)
            return strategy.secondaryDamageModifier(context)

        when {
            context.used(AttackType.MELEE) -> {
                if (context.isShatterEffect) return 1.50
                context.attackerEquipment?.let {
                    when {
                        context.isFightingAny(DEMON, UNDEAD_COMBAT_DUMMY) && (it.contains(ItemID.ARCLIGHT) || it.contains(ItemID.SILVERLIGHT) || it.contains(ItemID.SILVERLIGHT_2)
                                || it.contains(ItemID.DARKLIGHT) || it.contains(ItemID.HOLY_WATER))-> {
                            if (it.contains(ItemID.ARCLIGHT))
                                return 1.70
                            else if (it.contains(ItemID.SILVERLIGHT) || it.contains(ItemID.SILVERLIGHT_2)) // TODO: Use WeaponType.kt instead for dragonbane weapons
                                return 1.25
                            else if (it.contains(ItemID.DARKLIGHT))
                                return 1.50
                            else if (it.contains(ItemID.HOLY_WATER))
                                return 1.50
                        }
                        context.isFightingAny(DRAGON, UNDEAD_COMBAT_DUMMY, WYVERN, OLM) && it.contains(ItemID.DRAGON_HUNTER_LANCE) -> {
                            if (it.contains(ItemID.DRAGON_HUNTER_LANCE))
                                return 1.20
                        }
                        context.isFighting(GOLEM) && it.contains(ItemID.BARRONITE_MACE) -> {
                            if (it.contains(ItemID.BARRONITE_MACE))
                                return 1.25
                        }
                        context.isFightingAny(KURASK, TUROTH) && context.isFightingSlayerTask && it.contains(ItemID.LEAF_BLADED_BATTLEAXE) -> {
                            if (context.isFightingSlayerTask) {
                                if (it.contains(ItemID.LEAF_BLADED_BATTLEAXE))
                                    return 1.175
                            }
                        }
                        isWearingObsidianSet(it) && hasAnyObsidianWeapons(it) -> return 1.10
                    }
                }
            }
            context.used(AttackType.RANGED) -> {
                when {
                    context.isFightingAny(DRAGON, UNDEAD_COMBAT_DUMMY, WYVERN, OLM) && (context.used(RangedWeapon.DRAGON_HUNTER_CROSSBOW) || context.used(RangedWeapon.DRAGON_HUNTER_BOW)) -> {
                        if (context.used(RangedWeapon.DRAGON_HUNTER_CROSSBOW))
                            return 1.25
                        if (context.used(RangedWeapon.DRAGON_HUNTER_BOW))
                            return 1.60
                    }
                    context.used(RangedWeapon.TWISTED_BOW) -> {
                        val magic = targetSnapshot.skills.getLevel(Skill.MAGIC)
                        return (250.0
                                + ((((3.0 * magic) - 14.0) / 100.0))
                                - ((((3.0 * magic) / 10) - 140.0).pow(2.0) / 100.0))
                                .coerceAtMost(250.0)
                                .div(100.0)
                    }
                    context.used(RangedWeapon.DRAGON_HUNTER_BOW) -> {
                        return 1.35;
                    }
                }
            }
        }

        return wildernessItemBonus(targetSnapshot)
    }

    /**
     * Determines the third multiplier applied to damage output.
     */
    fun tertiaryDamageMultiplier(attacker: Agent, targetSnapshot: CombatSnapshot): Double {
        val strategy = attacker.combat.determineStrategy()
        val context = targetSnapshot.context

        if (strategy is SpecialAttack) {
            return strategy.tertiaryDamageModifier(context)
        }

        if (context.used(AttackType.MELEE)) {
            context.attackerEquipment?.let {
                when {
                    hasAnyObsidianWeapons(it) -> {
                        if (isWearingBerserkerNecklace(it) || isWearingBerserkerNecklaceOR(it))
                            return 1.20
                    }
                    context.isFighting(SHADE) -> {
                        if (it.contains(ItemID.GADDERHAMMER))
                            return if (Misc.randomChances(5.0)) 2.0 else 1.25
                    }
                    context.isFightingAny(KALPHITE, SCABARITES) -> {
                        if (isWieldingKeris(it))
                            return if (Misc.randomChance(100f / 51f)) 3.0 else 4.0 / 3.0
                    }
                }
            }
        }

        return 1.0
    }

    private fun wildernessItemBonus(snapshot: CombatSnapshot): Double {
        val target = snapshot.agent.combat.target
//System.out.println(snapshot.agent)
        //System.out.println("target $target")
        if(target !is Player)
            return 1.0

        if (target.combat.hasTarget()) {
            if (!target.inWilderness() || !snapshot.context.isFightingNpc) {
                return 1.0
            }
        }

        val charges: Int
        val accuracy: Double
        when {
            snapshot.context.used(CrawsBow.CHARGED) -> {
                charges = CrawsBow.getCharges(target.equipment[EquipmentConstants.WEAPON_SLOT])
                accuracy = if(charges > 0) 1.5 else 1.0
            }
            snapshot.context.used(ViggorasChainmace.CHARGED) -> {
                charges = ViggorasChainmace.getCharges(target.equipment[EquipmentConstants.WEAPON_SLOT])
                accuracy = if(charges > 0) 1.5 else 1.0
            }
            snapshot.context.used(ThammaronsSceptre.CHARGED) -> {
                charges = ViggorasChainmace.getCharges(target.equipment[EquipmentConstants.WEAPON_SLOT])
                accuracy = if(charges > 0) 1.2 else 1.0
            }
            else -> {
                return 1.0
            }
        }

        return if (charges <= 0 && target.inWilderness()) {
            //target.message("Your weapon needs to be charged with revenant ether.")
            // We were 3 players attacking vetion and one player had uncharged viggora chainmace. I was using rapier weapon and received the message above.
            // This could be fucking the whole combat system unsure if that is the case.

            // I added some prints above and weirdly enough it does not print the message every time I attack,it does randomly.
            // Does that mean sometimes that bonus effects are not calculated or what?
            1.0
        } else {
            accuracy
        }
    }

}