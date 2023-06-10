package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Abyssal_tentacle
 *
 * "The abyssal tentacle has a special attack, Binding Tentacle,
 * which consumes 50% of the player's special attack energy.
 * Regardless of whether the hit is successful, the target will be bound for 5 seconds,
 * and there will be approximately a 50% chance of applying poison,
 * starting at 4 damage."
 *
 * TODO: check if current sound id (same as whip) is the correct one
 * TODO: find correct hit graphic id
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class AbyssalTentacleSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.BINDING_TENTACLE

    override fun postHitEffect(hit: Hit) {

        val target = hit.target

        if (!target.isAlive)
            return

        target.combat.submit(FreezeEvent(5, false))

        if (Misc.randomChance(50F))
            PoisonEffect.applyPoisonTo(target, PoisonType.EXTRA)
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