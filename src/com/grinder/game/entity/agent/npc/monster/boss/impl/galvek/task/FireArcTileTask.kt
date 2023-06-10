package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.SpecialAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.monster.boss.BossTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.sound.Sound


/**
 * Arcing fire blast attack. Only using BossTask for access to playersList
 * and the cycle delay.
 *
 * @author Pea2nuts
 */
class FireArcTileTask(boss: GalvekBoss, agent: Agent)
    : BossTask<GalvekBoss>(boss, 20, 50, 1, false) {

    private var selectedSpot = Position(0,0)
    private val hitTemp: HitTemplate = HitTemplateBuilder(AttackType.SPECIAL)
        .setIgnoreAmmunitionEffects(true)
        .setIgnorePrayer(true)
        .setIgnorePoisonEffects(true)
        .setIgnoreAttackStats(true)
        .setIgnoreStrengthStats(true)
        .setDamageRange(75..110).build()

    override fun onCycle(cycle: Int) {
        if(cycle == 1) {

            val projectile = Projectile(boss.position, selectedSpot, GalvekConstants.FIRE_ARC_ATTACK)
            projectile.sendProjectile()
            projectile.onArrival {

                // Make sure every player in the area can see / be hit by it
                for (t in playerList) {
                    if (t == null || t.isDying || t.isTeleporting || t.isUntargetable) {
                        continue
                    }
                    t.packetSender.sendGraphic(GalvekConstants.FIRE_ARC_BLAST, selectedSpot)
                    t.playSound(Sound(163))

                    if (t != null && t.position == selectedSpot) {
                        val hit = Hit(boss, t, SpecialAttackStrategy.INSTANCE, hitTemp)
                        t.combat.queue(hit)
                    }
                }
            }

            stop()
        }



    }

    override fun stop() {
//        CollisionManager.clearClipping(position.x, position.y, position.z)
        super.stop()
    }

    init {
        selectedSpot = agent.position
        //boss.performAnimation(GalvekConstants.arcAttack)
    }
}