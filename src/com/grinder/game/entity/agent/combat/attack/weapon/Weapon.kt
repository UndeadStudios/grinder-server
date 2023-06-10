package com.grinder.game.entity.agent.combat.attack.weapon

import com.grinder.game.entity.agent.combat.attack.weapon.Weapon.Companion.isShortbow
import com.grinder.game.model.item.Item
import java.util.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/10/2019
 * @version 1.0
 */
class Weapon(item: Item) {

    val id = item.id

    val weaponInterface = Optional.ofNullable<Item>(item)
            .filter { item -> item.id > 0 }
            .map { item -> item.definition.weaponInterface }
            .orElse(WeaponInterface.UNARMED);

    fun uses (`interface`: WeaponInterface) = `interface` == weaponInterface

    fun isThrowable() = weaponInterface.isThrowable
    fun getSpeed() = weaponInterface.getSpeed(id)
    fun getFightType() = arrayOf(*weaponInterface.fightType)
    fun getNameLineId() = weaponInterface.nameLineId
    fun getInterfaceId() = weaponInterface.interfaceId
    fun getSpecialBar() = weaponInterface.specialBar
    fun getSpecialMeter() = weaponInterface.specialMeter

    companion object {

        fun Weapon.isInterface(weaponInterface: WeaponInterface) = this.uses(weaponInterface)

        fun Weapon.isAny(vararg weaponInterfaces: WeaponInterface): Boolean {
            return weaponInterfaces.any { this.isInterface(it) }
        }

        fun Weapon.isCrossbow() = this.isAny(
            WeaponInterface.CROSSBOW,
            WeaponInterface.DORGRESHUUN,
            WeaponInterface.HUNTER_CROSSBOW
        )

        fun Weapon.isShortbow() = this.isAny(
            WeaponInterface.SHORTBOW,
            WeaponInterface.SEERCULL,
            WeaponInterface.COMPOSITE_BOW
        )

    }

}