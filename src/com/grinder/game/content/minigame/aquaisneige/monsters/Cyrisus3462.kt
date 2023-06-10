package com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onState
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

class Cyrisus3462(id: Int, position: Position) : AquaisNeigeNpc(id, position!!), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        fetchDefinition().isAggressive = true
        combat.onState(CombatState.STARTING_ATTACK) {
            if (bossAttack.type() == AttackType.SPECIAL) {
                val target = combat.target ?: return@onState
                val builder = ProjectileTemplateBuilder(249)
                        .setSourceSize(1)
                        .setSourceOffset(0)
                        .setDelay(50)
                        .setSpeed(-2)
                        .setStartHeight(43)
                        .setEndHeight(target, 0.8)
                        .setCurve(5)

                val p1 = Projectile(this, target, builder.build())
                builder.setDelay(30)
                builder.setSpeed(0)
                val p2 = Projectile(this, target, builder.build())
                p1.sendProjectile()
                p2.sendProjectile()

                if (target is Player) {
                    target.playAreaSound(2545, 12, 1, 0)
                }
            }
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.RANGED)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
                .add(AttackType.Builder.Odds.THREE_TENTH, AttackType.SPECIAL)
                .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.RANGED)
                .build()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 35 else super.getMaxHit(type)
    }

    override fun skipNextRetreatSequence() = true
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

    //override fun useSmartPathfinding() = true

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun attackRange(type: AttackType) = 8

    override fun fetchAttackDuration(type: AttackType) = 4

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.SPECIAL) Stream.empty() else Stream.of(RANGE_PROJECTILE)
    }

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {

        return when(type) {
            AttackType.SPECIAL -> Optional.of(AreaSound(Sounds.MSB_SPECIAL_SOUND, 0, 1, 7))
            else -> super<AquaisNeigeNpc>.fetchAttackSound(type)
        }
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.SPECIAL) SPECIAL_ANIMATION else ATTACK_ANIMATION
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.SPECIAL) Optional.empty() else Optional.of(Graphic(24, GraphicHeight.HIGH))
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> =
            if (type == AttackType.SPECIAL)
                HitTemplate
                        .builder(type)
                        .setDelay(1)
                        .setHitAmount(2)
                        .setAttackStat(EquipmentBonuses.ATTACK_RANGE)
                        .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                        .buildAsStream()
            else
                Stream.of(RANGE_HIT)


    companion object {
        private val RANGE_HIT = HitTemplate.builder(AttackType.RANGED).setDelay(1).setAttackStat(EquipmentBonuses.ATTACK_RANGE).setDefenceStat(EquipmentBonuses.DEFENCE_RANGE).build()
        private val RANGE_PROJECTILE = ProjectileTemplate.builder(15)
                .setSourceSize(1)
                .setSourceOffset(0)
                .setStartHeight(46)
                .setEndHeight(31)
                .setSpeed(6)
                .setCurve(15)
                .setDelay(40)
                .build()
        private val SPECIAL_ANIMATION = Animation(1074, Priority.HIGH)
        private val ATTACK_ANIMATION = Animation(426, Priority.HIGH)

    }
}