package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.melee.DragonScimitarSpecialAttack.Companion.DISABLED_PRAYER_MESSAGE
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
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
 * https://oldschool.runescape.wiki/w/Dragon_scimitar
 *
 * "The dragon scimitar has a special attack, Sever,
 * which hits with increased accuracy and prevents the target (players only)
 * from using the Protect from Magic, Protect from Missiles,
 * and Protect from Melee prayers for 5 seconds,
 * consuming 55% of the player's special attack energy.
 * Sever does not affect the Protect Item prayer.
 * Rolls against the target's slash defence."
 *
 * TODO: figure out whether hit must be accurate for [Attribute.SEVER_EFFECT]
 * TODO: confirm whether also damage is modified, conflicting reports in wiki and forums
 * TODO: figure out correct message (if any) for [DISABLED_PRAYER_MESSAGE]
 *
 * @see Attribute.SEVER_EFFECT for disabled protection prayer effect
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonScimitarSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SEVER

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.25

    override fun postHitEffect(hit: Hit) {

        if(hit.isAccurate) {

            val target = hit.target

            if (target is Player && target.isAlive) {

                // disables protect item prayer for 90 seconds
                target.markTime(Attribute.SEVER_EFFECT)

                if (PrayerHandler.PROTECTION_PRAYERS.any { target.hasActivePrayer(it) }) {
                    target.message(PRAYER_DISABLED_MESSAGE)
                    PrayerHandler.resetPrayers(target, PrayerHandler.PROTECTION_PRAYERS)
                }
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1872, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(347, GraphicHeight.HIGH, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_SCIM_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

    companion object {

        private const val DISABLED_PRAYER_MESSAGE = "A mystical force prevents you from using this prayer"
        private const val PRAYER_DISABLED_MESSAGE = "A mystical force has disabled your potect item prayer"

        fun canUseProtectionPrayer(agent: Agent) : Boolean {

            if(agent.passedTime(Attribute.SEVER_EFFECT, 5, TimeUnit.SECONDS, updateIfPassed = false, message = false)){
                agent.removeAttribute(Attribute.SEVER_EFFECT)
                return true
            }

            agent.messageIfPlayer(DISABLED_PRAYER_MESSAGE, 1000)
            return false
        }
    }
}
