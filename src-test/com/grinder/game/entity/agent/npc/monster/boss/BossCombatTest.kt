package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.GrinderBiPlayerTest
import com.grinder.game.World
import com.grinder.game.definition.loader.NpcStatsDefinitionLoader
import com.grinder.game.definition.loader.impl.NpcDefinitionLoader
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.util.NpcID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class BossCombatTest : GrinderBiPlayerTest("boss_combat", position = World.startPosition.clone().add(3, 0)) {

    lateinit var boss: Boss
    lateinit var combat: BossCombat

    @BeforeEach
    @Throws(Throwable::class)
    fun setUp() {
        NpcDefinitionLoader().load()
        NpcStatsDefinitionLoader().load()
        boss = GeneralGraardorBoss(NpcID.GENERAL_GRAARDOR, World.startPosition.clone(), false)
        World.addNpc(boss)
        boss.setPreferredAttackType(AttackType.RANGED)
        combat = boss.getCombat()
    }

    @Test
    fun findPreferredOpponent() {
        val hit1 = Hit(player1, boss, MeleeAttackStrategy.INSTANCE, HitTemplate.builder(AttackType.MELEE)
                .setIgnoreAttackStats(true)
                .setIgnoreStrengthStats(true)
                .setDamageRange(IntRange(10, 10))
                .build())
        combat.onHitApplied(hit1)
        var optionalPreferredOpponent = combat.findPreferredOpponent()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player1)
        val hit2 = Hit(player2, boss, MeleeAttackStrategy.INSTANCE, HitTemplate.builder(AttackType.MELEE)
                .setIgnoreAttackStats(true)
                .setIgnoreStrengthStats(true)
                .setDamageRange(IntRange(20, 20))
                .build())
        combat.onHitApplied(hit2)
        optionalPreferredOpponent = combat.findPreferredOpponent()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player2)
        World.players.remove(player2)
        optionalPreferredOpponent = combat.findPreferredOpponent()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player1)
        World.players.add(player2)
        optionalPreferredOpponent = combat.findPreferredOpponent()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player2)
    }

    @Test
    fun findCurrentOrPreferredOrNearestTarget() {
        combat.target = player1
        var optionalPreferredOpponent: Optional<Agent> = combat.findCurrentOrPreferredOrNearestTarget()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player1)
        combat.target = null
        val hit1 = Hit(player1, boss, MeleeAttackStrategy.INSTANCE, HitTemplate.builder(AttackType.MELEE)
                .setIgnoreAttackStats(true)
                .setIgnoreStrengthStats(true)
                .setDamageRange(IntRange(10, 10))
                .build())
        combat.onHitApplied(hit1)
        val hit2 = Hit(player2, boss, MeleeAttackStrategy.INSTANCE, HitTemplate.builder(AttackType.MELEE)
                .setIgnoreAttackStats(true)
                .setIgnoreStrengthStats(true)
                .setDamageRange(IntRange(20, 20))
                .build())
        combat.onHitApplied(hit2)
        optionalPreferredOpponent = combat.findCurrentOrPreferredOrNearestTarget()
        Assertions.assertTrue(optionalPreferredOpponent.isPresent)
        Assertions.assertEquals(optionalPreferredOpponent.get(), player2)
    }

    @Test
    fun findCurrentTarget() {
    }

    @Test
    fun findCurrentOrClosestTarget() {
    }

    @Test
    fun playerTargetStream() {
    }
}