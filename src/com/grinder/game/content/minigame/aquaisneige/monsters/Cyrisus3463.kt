package com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

class Cyrisus3463(id: Int, position: Position) : AquaisNeigeNpc(id, position!!), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        fetchDefinition().isAggressive = true
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                if (attackType == AttackType.SPECIAL) {
                    if (target is Player && target.isAlive) {
                        val stealRunEnergy = it.runEnergy.times(0.10).toInt()
                        if (stealRunEnergy > 1) {
                            it.runEnergy = (it.runEnergy - stealRunEnergy).coerceAtLeast(0)
                            it.packetSender.sendOrbConfig()
                            if (it.runEnergy == 0) {
                                it.isRunning = false
                                it.packetSender.sendRunStatus()
                            }
                        }
                        }
                    }
                }
            }
    }

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(AttackType.Builder.Odds.THREE_TENTH, AttackType.SPECIAL)
            .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.MELEE)
            .build()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 35 else super.getMaxHit(type)
    }

    override fun attackRange(type: AttackType) = 1

    override fun skipNextRetreatSequence() = true
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

    //override fun useSmartPathfinding() = true

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun fetchAttackDuration(type: AttackType) = 4

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MELEE -> Optional.of(Sound(2720))
            AttackType.SPECIAL -> Optional.of(AreaSound(Sounds.WHIP_SPECIAL_SOUND, 0, 1, 7))
            else -> Optional.empty()
        }
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.SPECIAL) SPECIAL_ANIMATION else ATTACK_ANIMATION
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = if(type == AttackType.SPECIAL)
        HitTemplate
            .builder(type)
            .setDelay(0)
            .setAttackStat(EquipmentBonuses.ATTACK_SLASH)
            .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
            .setSuccessOrFailedGraphic(Graphic(181, GraphicHeight.HIGH, Priority.HIGH))
            .buildAsStream()
        else
        Stream.of(MELEE_HIT)

    companion object {
        private val MELEE_HIT = HitTemplate.builder(AttackType.MELEE).setAttackStat(EquipmentBonuses.ATTACK_SLASH).setDefenceStat(EquipmentBonuses.DEFENCE_SLASH).build()
        private val SPECIAL_ANIMATION = Animation(1658, Priority.HIGH)
        private val ATTACK_ANIMATION = Animation(1658, Priority.HIGH)
    }
}