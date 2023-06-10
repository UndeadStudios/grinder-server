package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.item.GoldenGodswordSpecialAttacks
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Armadyl_godsword
 *
 * "the Armadyl godsword has a special attack, The Judgement.
 * In addition to doubled accuracy, it inflicts damage with a 37.5%
 * (125% multiplied by the hidden 110% every godsword special possesses"

 * Version 1.1: Adds Golden Spec
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author  R-Y-M-R
 * @see     <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 * @since   14/05/2020
 * @version 1.1
 */
class ArmadylGodswordSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.THE_JUDGEMENT

    override fun secondaryAccuracyModifier(context: AttackContext) = 3.0

    override fun secondaryDamageModifier(context: AttackContext) = 1.10

    override fun tertiaryDamageModifier(context: AttackContext) = 1.25

    override fun sequence(actor: Agent, target: Agent) {
        if (actor is Player) {
            if (actor.attributes.bool(Attribute.GOLDEN_AGS)) {
                actor.performGraphic(Graphic(GoldenGodswordSpecialAttacks.ARMA.goldenSpecId, Priority.HIGH))
            } else {
                actor.performGraphic(Graphic(1211, Priority.HIGH))
            }
        }
        super.sequence(actor, target)
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7644, Priority.HIGH)

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.ARMADYL_GODSWORD_SPECIAL_SOUND))

        /*override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic>? {
            return Optional.of(Graphic(1211, Priority.HIGH))
        }*/

        override fun fetchAttackDuration(type: AttackType?)
                = 6
    }
}