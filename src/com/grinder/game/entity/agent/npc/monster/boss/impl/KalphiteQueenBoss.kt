package com.grinder.game.entity.agent.npc.monster.boss.impl


import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.*
import com.grinder.game.model.areas.impl.KalphiteLair
import com.grinder.game.model.areas.impl.KalphiteLair.Cocoon
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.Priority
import com.grinder.util.time.SecondsTimer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * Represents the kalphite queen [Boss].
 *
 * See https://oldschool.runescape.wiki/w/Kalphite_Queen
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class KalphiteQueenBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {
    private val cocoonWorkerTimer = SecondsTimer()
    private val secondFormTimer = SecondsTimer()
    private var morphing = false
    private fun transformToBeetle() {
        PrayerHandler.deactivatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.MELEE))
        PrayerHandler.activatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.MAGIC))
        PrayerHandler.activatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.RANGED))
        resetTransformation()
    }

    private fun transformToWasp() {
        hitpoints = fetchDefinition().hitpoints
        PrayerHandler.activatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.MELEE))
        PrayerHandler.deactivatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.MAGIC))
        PrayerHandler.deactivatePrayer(this, PrayerHandler.getProtectingPrayer(AttackType.RANGED))
        npcTransformationId = NpcID.KALPHITE_QUEEN_6501
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.RANGED)
        return attack
    }

    override fun sequence() {
        if (isAlive) {
            if (combat.isUnderAttack) {
                val area = area
                if (area is KalphiteLair) {
                    area.findNearbyCocoon(this)
                        .ifPresent { cocoon: Cocoon ->
                            cocoon.spawnWorker(this)
                            cocoonWorkerTimer.start(3)
                        }
                }
            }
        }
        if (!isDying && isMorphed) {
            if (secondFormTimer.finished()) {
                transformToBeetle()
            }
        }
        super.sequence()
    }

    override fun appendDeath() {
        if (isMorphed) {
            secondFormTimer.stop()
            super.appendDeath()
            return
        }
        morphing = true
        combat.reset(true)
        playerStream(20).forEach { player: Player ->
            player.packetSender.sendGraphic(
                SHELL_BREAKING,
                centerPosition.copy()
            )
        }
        performAnimation(Animation(6242, Priority.HIGH))
        TaskManager.submit(5) {
            setVisible(false)
            TaskManager.submit(9) {
                morphing = false
                setVisible(true)
                transformToWasp()
                secondFormTimer.start(Math.toIntExact(TimeUnit.MINUTES.toSeconds(5)))
            }
        }
    }

    override fun fetchDefinition(): NpcDefinition {
        return if (isMorphed) NpcDefinition.forId(NpcID.KALPHITE_QUEEN_6501) else super.fetchDefinition()
    }

    override fun skipNextCombatSequence(): Boolean {
        return morphing
    }

    override fun skipNextRetreatSequence(): Boolean {
        return morphing
    }

    override fun negateAllIncomingDamage(details: AttackContext): Boolean {
        val morphed = isMorphed
        val usedMelee = details.used(AttackType.MELEE)
        val usedRanged = details.used(AttackType.RANGED)
        val usedMagic = details.used(AttackType.MAGIC)
        val usedVerac = EquipmentUtil.isWearingVeracSet(details.attackerEquipment)
        if (morphed && usedMelee && !usedVerac) return true else if (!morphed && !usedMelee) return true
        if (usedMelee && PrayerHandler.isActivated(this, PrayerHandler.PROTECT_FROM_MELEE)) return true
        if (usedRanged && PrayerHandler.isActivated(this, PrayerHandler.PROTECT_FROM_MISSILES)) return true
        if (usedMagic && PrayerHandler.isActivated(this, PrayerHandler.PROTECT_FROM_MAGIC)) return true
        return morphing
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MAGIC, AttackType.RANGED, AttackType.MELEE)
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MAGIC || type == AttackType.RANGED) 4 else 1
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 12
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 4
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        if (isMorphed) return if (type == AttackType.MELEE) Animation(6235) else Animation(6234)
        return if (type == AttackType.MELEE) Animation(6241) else Animation(6240)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) Stream.of(MAGIC_PROJECTILE) else if (type == AttackType.RANGED) Stream.of(
            RANGED_PROJECTILE
        ) else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type)
        if (type == AttackType.RANGED || type == AttackType.MAGIC) {
            builder.setIgnoreAttackStats(true)
            if (type == AttackType.RANGED)
            builder.setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
            else if (type == AttackType.MAGIC)
            builder.setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
            else if (type == AttackType.MELEE)
                builder.setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_STAB)
            if (type == AttackType.MAGIC) builder.setSuccessOrFailedGraphic(Graphic(281))
            if (type == AttackType.RANGED) {
                builder.setOnSuccess { target: Agent ->
                    target.ifPlayer { player: Player ->
                        if (!EquipmentUtil.isWearingSpectralShield(player) || Misc.randomChance(50.0f)) player.skillManager.decreaseLevelTemporarily(
                            Skill.PRAYER, 1, 0
                        )
                    }
                }
            }
        }
        return builder.buildAsStream()
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.MAGIC || type == AttackType.RANGED) Optional.of(
            Graphic(
                if (isMorphed) 279 else 278,
                GraphicHeight.LOW
            )
        ) else Optional.empty()
    }

    class KalphiteSoldier(boss: KalphiteQueenBoss, position: Position) : BossMinion<KalphiteQueenBoss>(
        boss,
        NpcID.KALPHITE_SOLDIER,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            race = MonsterRace.KALPHITE
            movementCoordinator.radius = 4
        }
    }


        private val SHELL_BREAKING = Graphic(1055, 50, GraphicHeight.LOW, Priority.HIGH)
        private val RANGED_PROJECTILE = ProjectileTemplate.builder(473)
            .setSourceSize(5)
            .setStartHeight(45)
            .setEndHeight(33)
            .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 10)
            .setDelay(35)
            .build()
        private val MAGIC_PROJECTILE = ProjectileTemplate.builder(280)
            .setSourceSize(5)
            .setStartHeight(45)
            .setDelay(33)
            .setCurve(4)
            .setSpeed(if (asNpc.combat.target != null) (20 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 20)
            .setDelay(35)
            .build()

    init {
        race = MonsterRace.KALPHITE
        onEvent { monsterEvents: MonsterEvents ->
            if (monsterEvents === MonsterEvents.ADDED) transformToBeetle()

        }
    }
}