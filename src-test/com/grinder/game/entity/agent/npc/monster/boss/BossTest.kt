package com.grinder.game.entity.agent.npc.monster.boss

import com.grinder.GrinderPlayerTest
import com.grinder.game.World
import com.grinder.game.entity.agent.npc.monster.boss.impl.SeaTrollQueenBoss
import com.grinder.util.NpcID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BossTest : GrinderPlayerTest("boss-killer") {

    @Test
    fun randomizeAttack(){

        repeat(1000) {
            val boss = SeaTrollQueenBoss(NpcID.SEA_TROLL_QUEEN, player.position.clone().add(1, 0))
            World.addNpc(boss)
            boss.randomizeAttack()
            Assertions.assertTrue(boss.bossAttack.hasPreferredType())
            World.remove(boss)
        }
    }

}