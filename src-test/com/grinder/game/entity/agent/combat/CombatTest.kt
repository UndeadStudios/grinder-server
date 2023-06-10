package com.grinder.game.entity.agent.combat

import com.grinder.game.GrinderTriPlayerTest
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CombatTest : GrinderTriPlayerTest("") {

    @Test
    fun `test single zone combat`(){

        val base = Position(3075, 3535, 0)
        player1.position = base.clone()
        player2.position = base.clone().add(1, 0)
        player3.position = base.clone().add(0, 1)
        Assertions.assertFalse(AreaManager.inMulti(player1))
        Assertions.assertFalse(AreaManager.inMulti(player2))
        Assertions.assertFalse(AreaManager.inMulti(player3))

        player1.combat.setOpponent(player2)
        Assertions.assertFalse(player3.combat.canAttack(player1, true))
        player1.combat.clearOpponent()

        player2.combat.initiateCombat(player1, true)
        player3.combat.initiateCombat(player1, true)

        Assertions.assertTrue(player2.combat.hasTargeted(player1))
        Assertions.assertTrue(player3.combat.hasTargeted(player1))

        player2.motion.sequence()
        player2.combat.sequence()

        player3.motion.sequence()
        player3.combat.sequence()

        repeat(2) {
            player1.combat.sequenceHitsAndDamages()
        }

        Assertions.assertTrue(player1.combat.isBeingAttackedBy(player2))
        Assertions.assertFalse(player1.combat.isBeingAttackedBy(player3))
    }
}