package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Abyssal_whip
 *
 * "The abyssal whip has a special attack, Energy Drain,
 * which consumes 50% of the player's special attack energy,
 * increases accuracy by 25%, and in PVP, transfers 10% of
 * the target's run energy to the wielder."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class AbyssalWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.ENERGY_DRAIN

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.35

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun tertiaryDamageModifier(context: AttackContext) = 1.15

    override fun postHitEffect(hit: Hit) {
        val target = hit.target
        if (target is Player && target.isAlive) {
            val stealRunEnergy = target.runEnergy.times(0.10).toInt()
            if (stealRunEnergy > 1) {
                target.runEnergy = (target.runEnergy - stealRunEnergy).coerceAtLeast(0)
                target.packetSender.sendOrbConfig()
                if (target.runEnergy == 0) {
                    target.isRunning = false
                    target.packetSender.sendRunStatus()
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(181, GraphicHeight.HIGH, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}
