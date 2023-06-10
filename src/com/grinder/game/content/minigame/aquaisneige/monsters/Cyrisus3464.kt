package com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

class Cyrisus3464(id: Int, position: Position) : AquaisNeigeNpc(id, position!!), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        fetchDefinition().isAggressive = true
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.MAGIC) {
                if (isAccurate && totalDamage > 0) {
                    target.combat.submit(FreezeEvent(3, false))
                    target.performGraphic(MAGIC_HIT_GRAPHIC)
                    target.ifPlayer { it.playSound(168) }
                } else {
                    target.performGraphic(MAGIC_SPLASH_GFX)
                    target.ifPlayer { it.playSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun attackTypes() = AttackType.MAGIC

    override fun attackRange(type: AttackType) = 8

    override fun getAttackAnimation(type: AttackType): Animation {
        return ATTACK_ANIMATION
    }

    override fun skipNextRetreatSequence() = true
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

    //override fun useSmartPathfinding() = true

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun fetchAttackDuration(type: AttackType) = 4

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return Stream.of(MAGIC_HIT)
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return Optional.of(Graphic(366, Priority.HIGH))
    }

    companion object {
        private val MAGIC_HIT_GRAPHIC = Graphic(369, GraphicHeight.HIGH)
        private val MAGIC_SPLASH_GFX = Graphic(85, GraphicHeight.HIGH)
        private val MAGIC_HIT = HitTemplate.builder(AttackType.MAGIC).setDelay(2).build()
        private val ATTACK_ANIMATION = Animation(1979, Priority.HIGH)
    }
}