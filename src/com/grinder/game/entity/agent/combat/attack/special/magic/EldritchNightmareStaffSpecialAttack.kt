package com.grinder.game.entity.agent.combat.attack.special.magic

import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.formula.CombatBonuses
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.increaseLevel
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sound
import com.grinder.util.Priority
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Eldritch_nightmare_staff
 *
 * @see CombatBonuses.determineMeleeMxHitFormula for max hit handling
 *
 * TODO: update cache with right graphics
 * TODO: find sound id to use
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class EldritchNightmareStaffSpecialAttack
    : SpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.INVOCATE

    override fun type() = AttackType.MAGIC

    override fun requiredDistance(actor: Agent) = 10

    override fun sequence(actor: Agent, target: Agent) {
        if (actor != null && actor.isPlayer) {
            if (actor.asPlayer.dueling.inDuel() && actor.asPlayer.dueling.rules[DuelRule.NO_MAGIC.ordinal]) {
                actor.asPlayer.sendMessage("Magic has been disabled in this duel!")
                return
            }
        }
        target.performGraphic(Graphic(1761))

        if (actor is Player) {
            target.combat.onIncomingHitQueued {
                if (totalDamage > 0)
                    actor.increaseLevel(Skill.PRAYER, totalDamage.div(2), capMax = 120)
            }
        }
    }

    override fun canAttack(actor: Agent, target: Agent) = MeleeAttackStrategy.INSTANCE.canAttack(actor, target)

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(8532, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1762, 30, GraphicHeight.HIGH, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.empty<Sound>()

        override fun fetchAttackDuration(type: AttackType?) = 5

        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(2)
            .buildAsStream()
    }
}