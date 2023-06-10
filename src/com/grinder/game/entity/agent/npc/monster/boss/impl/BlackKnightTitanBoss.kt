package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-13
 */
class BlackKnightTitanBoss(npcId: Int, position: Position?, private val inDefaultArea: Boolean) : Boss(npcId, position!!), AttackProvider {

    init {
        race = MonsterRace.GIANT
    }

    override val circumscribedBoundaries: List<Boundary>
        get() = if (inDefaultArea) listOf(Boundary(2561, 2572, 9505, 9517))
        else emptyList()

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
                .add(AttackType.Builder.Odds.ONE_THIRD, AttackType.SPECIAL)
                .add(AttackType.Builder.Odds.THREE_FOURTH, AttackType.MAGIC)
                .add(AttackType.Builder.Odds.ONE_THIRD, AttackType.RANGED)
                .add(AttackType.Builder.Odds.TWO_FIFTH, AttackType.MELEE)
                .build()
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    override fun fetchAttackDuration(type: AttackType) = 5

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 5
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(128)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.RANGED) ProjectileTemplate.builder(if (type == AttackType.SPECIAL) 88 else 1632)
                .setStartHeight(50)
                .setSourceOffset(1)
                .setEndHeight(35)
                .setCurve(5)
                .setSpeed(if (asNpc.combat.target != null) (15 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 15)
                .setDelay(35)
                .buildAsStream()
        else ProjectileTemplate
                .builder(1230)
             .setSourceOffset(1)
                .setStartHeight(50)
                .setEndHeight(35)
                .setCurve(15)
                .setSpeed(if (asNpc.combat.target != null) (5 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 5)
                .setDelay(55)
                .buildAsStream()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 42 else if (type == AttackType.MAGIC) 55 else if (type == AttackType.SPECIAL) 30 else if (type == AttackType.RANGED) 22 else 45
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(if (type == AttackType.SPECIAL) AttackType.RANGED else type)
        if (type == AttackType.MELEE) {
            builder.setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
        } else if (type == AttackType.MAGIC) {
            builder.setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)).setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                    .setSuccessGraphic(Graphic(320, GraphicHeight.LOW))
                    .setFailedGraphic(Graphic(85, GraphicHeight.HIGH))
        } else if (type == AttackType.SPECIAL) {
            builder.setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)).setIgnoreAttackStats(true)
                    .setOnSuccessOrFailed(Consumer { target: Agent ->
                        target.ifPlayer { player: Player ->
                            if (player.skullTimer <= 0) {
                                player.combat.skull(SkullType.RED_SKULL, TimeUnit.MINUTES.toSeconds(60).toInt())
                                player.sendMessage("You have been skulled by the Black Knight Titan!")
                            }
                        }
                    })
                    .setSuccessOrFailedGraphic(Graphic(89, GraphicHeight.HIGH))
        }
        return builder.buildAsStream()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (type != AttackType.MELEE && Misc.randomChance(20.0f)) Optional.of(Misc.random(*CHATS_ABOVE_HEAD)) else if (type == AttackType.MELEE && Misc.randomChance(33.3f)) Optional.of("My blade will tear you apart MUHAHAHAHA") else Optional.empty()
    }

    class BKTBlackKnight(boss: BlackKnightTitanBoss, position: Position) : BossMinion<BlackKnightTitanBoss>(
        boss,
        NpcID.BLACK_KNIGHT,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class BlackKnightNonCombat(boss: BlackKnightTitanBoss, position: Position) : BossMinion<BlackKnightTitanBoss>(
        boss,
        NpcID.BLACK_KNIGHT_4960,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class BKTGiantBat(boss: BlackKnightTitanBoss, position: Position) : BossMinion<BlackKnightTitanBoss>(
        boss,
        NpcID.GIANT_BAT_4562,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class BKTChaosDruidWarrior(boss: BlackKnightTitanBoss, position: Position) : BossMinion<BlackKnightTitanBoss>(
        boss,
        NpcID.CHAOS_DRUID_WARRIOR,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    companion object {
        private val CHATS_ABOVE_HEAD = arrayOf(
                "I bring da pain!", "Your gonna get more than you bargain for!", "You have been judged!", "A single touch could kill you!"
        )
    }
}