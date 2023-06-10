package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.event.impl.BloodSacrificeEvent
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.markTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Ancient_godsword
 *
 * Ancient godsword has a special attack, Blood Sacrifice. In addition to doubled accuracy, it inflicts damage with a 10% higher maximum hit than a normal attack, consuming 50% of the player's special attack energy.
 * Upon a successful hit, the target will be marked for sacrifice and will have eight ticks (4.8 seconds) to move at least five tiles away from the attacker. If the target fails to do so, they will take 25 damage and the attacker will be healed for the same amount.[1] Praying Protect from Magic will not reduce the sacrifice damage.
 *
 * @author  R-Y-M-R (https://www.rune-server.ee/members/Necrotic/)
 * @since   25/05/2022
 * @version 1.0
 */
class AncientGodswordSpecialAttack() : MeleeSpecialAttack(Provider()) {
    override fun special() = SpecialAttackType.BLOOD_SACRIFICE

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun tertiaryDamageModifier(context: AttackContext) = 1.0

    override fun postHitEffect(hit: Hit) {
        if(hit.isAccurate) {
            val target = hit.target
            if (target.isAlive) {
                hit.target.combat.submit(BloodSacrificeEvent(8, true))
            }
        }
    }
    class Provider : SpecialAttackProvider {
        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .setDelay(0)
            .setSuccessGraphic(Graphic(1435, GraphicHeight.LOW, Priority.HIGH))
            .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
            Animation(9171, Priority.HIGH) // OSRS ID: 9171, placeholder: 7644

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
            Sound(Sounds.GODSWORD_SPECIAL_SOUND)
        )

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
            Graphic(1996, Priority.HIGH)) // OSRS ID: 1996, placeholder: 1211

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}