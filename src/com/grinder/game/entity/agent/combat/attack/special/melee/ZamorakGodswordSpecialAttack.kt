package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.item.GoldenGodswordSpecialAttacks
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Zamorak_godsword
 *
 * "The Zamorak godsword has a special attack, Ice Cleave,
 * which consumes 50% of the player's special attack energy.
 * It doubles the player's accuracy (slash or crush roll)
 * and increases their max hit by 10%. In addition,
 * it freezes the target in place for 20 seconds;
 * the same duration as Ice Barrage.
 * The sword's special attack must produce a successful hit
 * in order to have the freezing effect,
 * and it rolls against opponent's slash defence.
 * Protect from Melee only gives a damage reduction of 40%."

 * Version 1.1: adds golden spec
 * TODO: confirm godsword sound is correct
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author  R-Y-M-R
 * @see     <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 * @since   14/05/2020
 * @version 1.1
 */
class ZamorakGodswordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.ICE_CLEAVE

    override fun secondaryAccuracyModifier(context: AttackContext) = 2.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun postHitEffect(hit: Hit) {
        if(hit.isAccurate)
            hit.target.combat.submit(FreezeEvent(20, false))
    }

    override fun sequence(actor: Agent, target: Agent) {
        if (actor is Player) {
            if (actor.attributes.bool(Attribute.GOLDEN_ZGS)) {
                actor.performGraphic(Graphic(GoldenGodswordSpecialAttacks.ZAMMY.goldenSpecId, Priority.HIGH))
            } else {
                actor.performGraphic(Graphic(1210, Priority.HIGH))
            }
        }
        super.sequence(actor, target)
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                .setSuccessGraphic(Graphic(369, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7638, Priority.HIGH)

        /*
        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1210, Priority.HIGH))
         */

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GODSWORD_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}