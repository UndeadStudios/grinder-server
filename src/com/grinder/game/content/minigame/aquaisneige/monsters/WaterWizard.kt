package com.grinder.game.content.minigame.aquaisneige.monsters;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import java.util.*
import java.util.stream.Stream


class WaterWizard(id: Int, position: Position?) : AquaisNeigeNpc(id, position!!), AttackProvider {

    private var spell: Spell? = null

    init {
        race = MonsterRace.HUMAN
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.MAGIC) {
                if (isAccurate && totalDamage > 0) {
                    target.ifPlayer { it.playSound(Sounds.WATER_BLAST_CONTACT) }
                } else {
                    target.ifPlayer { it.playSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.MAGIC
    }

    override fun generateAttack(): BossAttack {
        val attack = AquaisNeigeAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun fetchHits(type: AttackType) = HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
            .also {
                if (type == AttackType.MAGIC)
                    it.setSuccessGraphic(Graphic(137, GraphicHeight.HIGH))
                            .setFailedGraphic(Graphic(85, GraphicHeight.HIGH))
            }
            .buildAsStream()

    override fun fetchAttackDuration(type: AttackType?) = 4

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.MAGIC) {
            Stream.empty()
        } else {
            var projectile = ProjectileTemplate
                    .builder(136)
                    .setSourceOffset(1)
                    .setDelay(52)
                    .setSpeed(if (asNpc.combat.target != null) (5 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(35)) else 5)
                    .setHeights(55, 31)
                    .setCurve(280)
                    .build()

            return Stream.of(projectile);
        }
    }

    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> {
        return Optional.ofNullable(Graphic(135, GraphicHeight.HIGH))
    }

    override fun attackRange(type: AttackType): Int {
        return 7;
    }

    override fun getAttackAnimation(type: AttackType?) = Animation(attackAnim)

    enum class Spell(val projectileId: Int, val startGraphic: Graphic, val endGraphic: Graphic, val sound: Sound) {
        WATER_BLAST(136, Graphic(135, 1, 55), Graphic(137, 2, GraphicHeight.HIGH), Sound(Sounds.WATER_BLAST_CONTACT))
    }
}