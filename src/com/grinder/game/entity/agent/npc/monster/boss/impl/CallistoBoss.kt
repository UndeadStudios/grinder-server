package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.event.impl.StunEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.passedTime
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.ForceMovementTask
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.Area
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class CallistoBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    companion object {
        private val KNOCK_BACK_AREA = Area.of(2, 2, 2, 2)
        private val MELEE_ANIMATION = Animation(4925)
        private val SPECIAL_ANIMATION = Animation(4926)
        private const val COMBO_TIMER = "callisto_combo_timer"
    }

    init {
        race = MonsterRace.BEAR
        combat.onOutgoingHitApplied {
            if (!AgentUtil.isStunned(target)) {
                target.ifPlayer {
                    if (Misc.randomChance(5.0f)) {
                        val moveToPosition = it.position.clone()
                        KNOCK_BACK_AREA
                            .getAbsolute(moveToPosition)
                            .findRandomOpenPosition(moveToPosition.z, 3, moveToPosition)
                            .ifPresent { knockBackPosition: Position ->
                                it.combat.submit(StunEvent(4, false))
                                it.motion.clearSteps()
                                it.message("Callisto's roar knocks you backwards.")
                                it.performGraphic(Graphic(80, GraphicHeight.MIDDLE))
                                knockTowards(it, knockBackPosition)
                            }
                    }
                }
            }
        }
    }

    private fun knockTowards(playerTarget: Player, knockBackPosition: Position) {
        TaskManager.submit(ForceMovementTask(playerTarget, 3, ForceMovement(playerTarget.position.clone(), knockBackPosition.getDelta(playerTarget.position), 0, 15, 0, 0)))
    }

    override fun skipNextRetreatSequence() = true

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        if (passedTime(COMBO_TIMER, 5, TimeUnit.SECONDS)) {
            if (Misc.randomChance(20.0f)) {
                combat.setNextAttackDelay(0)
                return AttackType.SPECIAL
            }
        }
        return AttackType.MELEE
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 5 else 1
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 14 else 2
    }

    override fun negateAllIncomingDamage(details: AttackContext): Boolean {
        return details.used(AttackType.MAGIC)
    }

    override fun fetchAttackDuration(type: AttackType) = 4

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.SPECIAL) SPECIAL_ANIMATION else MELEE_ANIMATION
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 60 else 50
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.SPECIAL) ProjectileTemplate.builder(395)
                .setSourceSize(5)
                .setStartHeight(31)
                .setEndHeight(30)
                .setSpeed(30)
                .setDelay(40)
                .setCurve(280)
                .buildAsStream() else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return when (type) {
            AttackType.SPECIAL -> {
                return HitTemplate
                    .builder(AttackType.SPECIAL)
                    .setDelay(2)
                    .setIgnorePrayer(true)
                    .setSuccessOrFailedGraphic(Graphic(359, GraphicHeight.HIGH))
                    .buildAsStream()
            }
/*            AttackType.MAGIC -> {
                return HitTemplate
                    .builder(AttackType.MAGIC)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                    .setDelay(2)
                    .buildAsStream()
            }*/
            AttackType.MELEE -> {
                return HitTemplate
                    .builder(AttackType.MELEE)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                    .setDelay(0)
                    .buildAsStream()
            }
            else -> {
                Stream.empty()
            }
        }
    }
}