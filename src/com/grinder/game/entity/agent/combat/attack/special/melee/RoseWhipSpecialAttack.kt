package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.removeAttribute
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This is a custom special attack for a custom weapon.
 *
 * TODO: find a nice sound
 * TODO: set proper modifiers
 *
 * @see Attribute.FEAR_EFFECT for disabled protect item prayer effect
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class RoseWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.FEAR

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target

            if (target is Player && target.isAlive) {

                // disables protect item prayer for 90 seconds
                target.markTime(Attribute.FEAR_EFFECT)

                if (target.hasActivePrayer(PrayerHandler.PROTECT_ITEM)) {
                    target.message(PRAYER_DISABLED_MESSAGE)
                    PrayerHandler.deactivatePrayer(target, PrayerHandler.PROTECT_ITEM)
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(505, GraphicHeight.HIGH, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.ROSE_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

    companion object {

        private const val DISABLED_PRAYER_MESSAGE = "A mystical force prevents you from using this prayer"
        private const val PRAYER_DISABLED_MESSAGE = "A mystical force has disabled your potect item prayer"

        fun canUseProtectItemPrayer(agent: Agent) : Boolean {

            if(agent.passedTime(Attribute.FEAR_EFFECT, 90, TimeUnit.SECONDS, updateIfPassed = false, message = false)){
                agent.removeAttribute(Attribute.FEAR_EFFECT)
                return true
            }

            agent.messageIfPlayer(DISABLED_PRAYER_MESSAGE, 1000)
            return false
        }
    }
}