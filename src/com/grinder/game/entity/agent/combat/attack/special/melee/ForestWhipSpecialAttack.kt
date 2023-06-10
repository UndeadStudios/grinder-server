package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * This is a custom special attack for a custom weapon.
 *
 * TODO: find a nice sound
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class ForestWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.MOTHER_NATURE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate){

            hit.target.ifPlayer {
                if(!it.inventory.isFull){
                    if(it.equipment.isSlotOccupied(EquipmentConstants.WEAPON_SLOT)){
                        val weapon = it.equipment.get(EquipmentConstants.WEAPON_SLOT)
                        if(weapon.isValid){
                            it.equipment.delete(weapon, EquipmentConstants.WEAPON_SLOT)
                            it.inventory.add(weapon)
                            it.updateAppearance()
                            it.message("The vines disarm you!")
                            WeaponInterfaces.assign(it)
                            EquipmentBonuses.update(it)
                        }
                    }
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(8, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.FOREST_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}