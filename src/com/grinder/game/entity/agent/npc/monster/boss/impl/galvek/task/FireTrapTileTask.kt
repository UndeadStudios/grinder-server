package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task

import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.SpecialAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.monster.boss.BossTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekPhase
import com.grinder.game.model.Position
import com.grinder.util.Misc
import com.grinder.util.time.SecondsTimer


/**
 * A BossTask that sends a galvek fire trap to a Position.
 * The full attack sends 8 of these out during the fire phase
 *
 * @author Pea2nuts
 */
class FireTrapTileTask(boss: GalvekBoss, tile: Position)
    : BossTask<GalvekBoss>(boss, 20, 50, 1, false) {

    private val damageTimer = SecondsTimer()
    private var spot: Position = tile
    private val obj: DynamicGameObject = DynamicGameObject.createPublic(GalvekConstants.FIRE_BALL_LOC, spot, 10, 0)
    private val hitTemp: HitTemplate = HitTemplateBuilder(AttackType.SPECIAL)
        .setIgnoreAmmunitionEffects(true)
        .setIgnorePrayer(true)
        .setIgnorePoisonEffects(true)
        .setIgnoreAttackStats(true)
        .setIgnoreStrengthStats(true)
        .setDamageRange(75..110).build()

    override fun onCycle(cycle: Int) {
        if (boss.combat.target == null || !boss.isAlive || !boss.isActive || boss.isDying || boss.phase != GalvekPhase.FIRE) {
            if(obj.isSpawned) {
                obj.performGraphic(GalvekConstants.FIRE_ARC_BLAST)
                ObjectManager.remove(obj, true)
            }
            boss.fireTrapsActive = false
            stop()
            return
        }

        if(cycle == 0) {
            obj.setSpawnedFor(boss.combat.target.asPlayer)
            ObjectManager.add(obj, true)
        }

        // Make the fire traps explode if the phase ends or the timer ends
        if (boss.phase !== GalvekPhase.FIRE || damageTimer.finished()) {

            // Remove the object
            if(obj.isSpawned) {
                obj.performGraphic(GalvekConstants.FIRE_ARC_BLAST)
                ObjectManager.remove(obj, true)
            }

            // Explode for all players
            for(player in playerList) {

                // Explosion should hit players within 3 tiles
                //player.packetSender.sendGraphic(GalvekConstants.FIRE_ARC_BLAST, spot)
                if (player != null && Misc.getDistance(player.position, spot) <= 3) {
                    val hit = Hit(boss, player, SpecialAttackStrategy.INSTANCE, hitTemp)
                    player.combat.queue(hit)
                    break
                }
            }
            boss.fireTrapsActive = false
            stop()
            return
        }

        for(player in playerList) {
            // Do damage to any player that walks into the trap. This should 1 hit KO most.
            if (player != null && Misc.getDistance(player.position, spot) <= 1) {
                val hit = Hit(boss, player, SpecialAttackStrategy.INSTANCE, hitTemp)
                player.combat.queue(hit)
                break
            }
        }

    }

    override fun stop() {
//        CollisionManager.clearClipping(position.x, position.y, position.z)
        super.stop()
    }

    init {
        damageTimer.start(15)
    }
}