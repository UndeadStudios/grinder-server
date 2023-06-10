package com.grinder.game.entity.agent.combat.attack.weapon

import com.grinder.util.ItemID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/10/2019
 * @version 1.0
 */
enum class WeaponType(vararg val itemIds: Int) {
    UNDEFINED,
    DEMONBANE(ItemID.SILVERLIGHT, ItemID.SILVERLIGHT_3, ItemID.DARKLIGHT, ItemID.HOLY_WATER),
    DRAGONBANE(ItemID.DRAGON_HUNTER_CROSSBOW, 25916, 25918),
    LEAF_BLADED(ItemID.LEAF_BLADED_BATTLEAXE, ItemID.LEAF_BLADED_SPEAR, ItemID.LEAF_BLADED_SWORD);

    companion object {

        fun getWeaponTypeFor(weaponId: Int) : WeaponType {

            for(type in values()){
                for(id in type.itemIds){
                    if(weaponId == id)
                        return type
                }
            }

            return UNDEFINED
        }
    }
}