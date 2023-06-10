package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

class AncientWizardMinion(npcId: Int, position: Position)
: Boss(npcId, position), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        fetchDefinition().isAggressive = true
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(AttackType.Builder.Odds.ONE_FIFTH, AttackType.MAGIC)
            .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.MELEE)
            .add(AttackType.Builder.Odds.TWO_TENTH, AttackType.SPECIAL)
            .build()
    }

    override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return 1
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MAGIC) CombatSpellType.BLOOD_BLITZ.baseMaxHit else 16
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    override fun fetchAttackDuration(type: AttackType) = 4

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MELEE || type == AttackType.SPECIAL) Stream.empty() else Stream.of(MAGIC_PROJECTILE)
    }

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {

        return when (type) {
            AttackType.SPECIAL -> Optional.of(AreaSound(Sounds.DRAGON_DAGGER_SPECIAL_SOUND, 0, 1, 7))
            AttackType.MAGIC -> Optional.of(Sound(Sounds.BLOOD_BLITZ_CAST))
            AttackType.MELEE -> Optional.of(Sound(Sounds.DAGGER_ATTACK_SOUND))
            else -> return Optional.empty()
        }
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return when(type) {
            AttackType.MAGIC -> MAGIC_ANIMATION
            AttackType.MELEE -> ATTACK_ANIMATION
            AttackType.SPECIAL -> SPECIAL_ANIMATION
            else -> {
                ATTACK_ANIMATION
            }
        }
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return when (type) {
            AttackType.SPECIAL -> return Optional.of(Graphic(252, 0, 80, Priority.HIGH))
            else -> {
                return Optional.empty()
            }
        }

    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> =
        when (type) {
            AttackType.SPECIAL -> HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setHitAmount(2)
                .setIgnoreAttackStats(true)
                .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                .buildAsStream()
            AttackType.MAGIC -> {
                HitTemplate
                    .builder(AttackType.MAGIC)
                    .setDelay(1)
                    .setAttackStat(EquipmentBonuses.ATTACK_MAGIC)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                    .buildAsStream()
            }
            else -> {
                HitTemplate
                    .builder(AttackType.MELEE)
                    .setDelay(0)
                    .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                    .buildAsStream()
            }
        }



    companion object {
        private val SPECIAL_ANIMATION = Animation(1062, Priority.HIGH)
        private val ATTACK_ANIMATION = Animation(376, Priority.HIGH)
        private val MAGIC_ANIMATION = Animation(1978, Priority.HIGH)
        private val MAGIC_PROJECTILE = ProjectileTemplate.builder(374)
            .setSourceSize(1)
            .setSourceOffset(1)
            .setStartHeight(42)
            .setEndHeight(34)
            .setSpeed(14)
            .setCurve(280)
            .setDelay(51)
            .build()
    }
}