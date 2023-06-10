package com.grinder.game.entity.agent.combat.formula

import com.grinder.GrinderBiPlayerTest
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Skill
import com.grinder.game.model.consumable.potion.Potion
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.oldgrinder.EquipSlot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CombatFormulaTest : GrinderBiPlayerTest("") {

    @BeforeEach
    fun setup(){


        setRangedLevel(player2, 99)

        setRangedLevel(player1, 99)

        val equipment = player1.equipment
        equipment[EquipSlot.HAT] = Item(ItemID.CORRUPTED_HELM)
        equipment[EquipSlot.CAPE] = Item(ItemID.AVAS_ACCUMULATOR)
        equipment[EquipSlot.AMULET] = Item(ItemID.NECKLACE_OF_ANGUISH)
        equipment[EquipSlot.ARROWS] = Item(ItemID.AMETHYST_ARROW, 100)
        equipment[EquipSlot.WEAPON] = Item(ItemID.MAGIC_SHORTBOW_I_)
        equipment[EquipSlot.CHEST] = Item(ItemID.ARMADYL_CHESTPLATE)
        equipment[EquipSlot.LEGS] = Item(ItemID.CORRUPTED_PLATESKIRT)
        equipment[EquipSlot.HANDS] = Item(ItemID.BARROWS_GLOVES)
        equipment[EquipSlot.FEET] = Item(ItemID.PEGASIAN_BOOTS)
        equipment[EquipSlot.RING] = Item(ItemID.ARCHERS_RING_I_)

        player1.specialAttackType = SpecialAttackType.SNAPSHOT
        player1.isSpecialActivated = true

        EquipmentBonuses.update(player1)
        WeaponInterfaces.INSTANCE.assign(player1)
        Potion.SUPER_RANGE_POTIONS.onEffect(player1)
        PrayerHandler.activatePrayer(player1, PrayerHandler.RIGOUR)
    }

    private fun setRangedLevel(player: Player, level: Int) {
        player.skillManager
            .setCurrentLevel(Skill.RANGED, level, true)
            .setMaxLevel(Skill.RANGED, level, true)
            .setExperienceIfMoreThanCurrent(
                Skill.RANGED,
                SkillUtil.calculateExperienceForLevel(level)
            )
    }

    @Test
    fun testNPC(){
        val npc = NPCFactory.create(NpcID.EXPERIMENT, player1.position.clone().add(1, 1))
        val combat = player1.combat
        combat.target = npc
        combat.opponent = npc

        Assertions.assertEquals(combat.rangedWeapon, RangedWeapon.MAGIC_SHORTBOW)

        val snapshot1 = CombatSnapshot.of(player1, npc, AttackType.RANGED)
        val snapshot2 = CombatSnapshot.of(npc, player1, AttackType.RANGED)
        val strength = CombatFormulaType.RANGED.calculateStrength(snapshot1, snapshot2)

        println(strength)
    }

    @Test
    fun testPlayer(){
        val combat = player1.combat
        combat.target = player2
        combat.opponent = player2

        Assertions.assertEquals(combat.rangedWeapon, RangedWeapon.MAGIC_SHORTBOW)

        val snapshot1 = CombatSnapshot.of(player1, player2, AttackType.RANGED)
        val snapshot2 = CombatSnapshot.of(player2, player1, AttackType.RANGED)
        val strength = CombatFormulaType
            .RANGED
            .calculateStrength(snapshot1, snapshot2)

        println(strength)
    }
}