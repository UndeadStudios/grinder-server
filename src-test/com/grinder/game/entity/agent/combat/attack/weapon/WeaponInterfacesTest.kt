package com.grinder.game.entity.agent.combat.attack.weapon

import com.grinder.GrinderPlayerTest
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class WeaponInterfacesTest : GrinderPlayerTest("weapon_interface_test") {

    @Test
    fun testAttackStyleCache(){

        val seercull = Item(ItemID.SEERCULL)
        val compbow = Item(ItemID.MAGIC_COMP_BOW)

        player.equipment[EquipSlot.WEAPON] = seercull
        WeaponInterfaces.INSTANCE.assign(player)
        Assertions.assertEquals(WeaponFightType.SHORTBOW_ACCURATE, player.combat.fightType)
        WeaponInterfaces.INSTANCE.handleButton(player, 1771)
        Assertions.assertEquals(WeaponFightType.SHORTBOW_RAPID, player.combat.fightType)

        player.equipment[EquipSlot.WEAPON] = compbow
        WeaponInterfaces.INSTANCE.assign(player)
        Assertions.assertEquals(WeaponFightType.SHORTBOW_ACCURATE, player.combat.fightType)

        player.equipment[EquipSlot.WEAPON] = seercull
        WeaponInterfaces.INSTANCE.assign(player)
        Assertions.assertEquals(WeaponFightType.SHORTBOW_RAPID, player.combat.fightType)
    }

}