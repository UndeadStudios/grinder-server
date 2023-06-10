package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
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
 * https://oldschool.runescape.wiki/w/Rune_claws
 *
 * "Rune claws have a special attack, Impale,
 * which consumes 25% of the player's special attack energy,
 * and deals 10% increased damage,
 * but increases the time until the next attack by one tick (0.6 seconds),
 * and delays the actual special attack itself by the same amount of time."
 *
 * TODO: find correct animation id
 * TODO: combat thread of bitterkoekje says 15% increased accuracy and damage
 *       wiki says 10% increased accuracy, figure out which one is correct
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
class RuneClawSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.IMPALE

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.15

    override fun secondaryDamageModifier(context: AttackContext) = 1.15

    override fun onHit(actor: Agent, target: Agent) {
        actor.combat.extendNextAttackDelay(1)
    }

    override fun onActivated(actor: Agent) {

        if (actor is Player) {

            if (actor.combat.canPerformSpecialAttack(special()))
                actor.combat.extendNextAttackDelay(1)
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(923, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(274, GraphicHeight.MIDDLE, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.RUNE_CLAWS_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}