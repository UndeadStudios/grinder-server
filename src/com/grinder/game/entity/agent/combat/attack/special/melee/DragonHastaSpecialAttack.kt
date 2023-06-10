package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Dragon_hasta
 *
 * "The dragon hasta has a special attack called Unleash,
 * which causes the next attack to have a 5% boost
 * in accuracy and a 2.5% boost in damage for
 * every 5% of special attack energy used.
 *
 * For example, a player with 100% special attack
 * energy who performs the special attack will
 * cause their next attack to have 100%
 * increased accuracy and a damage boost of 50%."
 *
 * @author  Kyle
 * @since   8/07/2021
 * @version 1.0
 */
class DragonHastaSpecialAttack : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.UNLEASH

    override fun secondaryAccuracyModifier(context: AttackContext): Double {
        val amt = context.attackerEquipment.player.specialPercentage / 5
        return 1 + (amt * ACC_PER_SPEC)
    }

    override fun secondaryDamageModifier(context: AttackContext): Double {
        val amt = context.attackerEquipment.player.specialPercentage / 5
        return 1 + (amt * DMG_PER_SPEC)
    }

    override fun drainAmount(actor: Agent) = actor.specialPercentage

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .setDelay(0)
            .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) = Animation(7515, Priority.HIGH)
        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(Graphic(1369, GraphicHeight.HIGH, Priority.HIGH))
        override fun fetchAttackSound(type: AttackType?) = Optional.of(Sound(Sounds.BRINE_SABRE_LUNGE))
        override fun fetchAttackDuration(type: AttackType?) = 4

    }

    private companion object {

        private const val ACC_PER_SPEC = 0.05
        private const val DMG_PER_SPEC = 0.025

    }

}