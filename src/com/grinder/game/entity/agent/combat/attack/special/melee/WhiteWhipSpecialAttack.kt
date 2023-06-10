package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * This is a custom special attack for a custom weapon.
 *
 * TODO: set proper modifiers
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class WhiteWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.IMPALER

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.25

    override fun secondaryDamageModifier(context: AttackContext) = 1.35

    override fun onHit(actor: Agent, target: Agent) {
        if(target is Player && target.isAlive){
            val seconds = if(target.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC))
                60
            else
                120
            target.combat.teleBlockTimer.extendOrStart(seconds)
            target.message(DISABLED_TELEPORTING_MESSAGE)
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(345, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.WHITE_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

    companion object {
        private const val DISABLED_TELEPORTING_MESSAGE = "A mystical force prevents you from teleporting"
    }
}