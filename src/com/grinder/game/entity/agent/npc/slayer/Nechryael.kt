package com.grinder.game.entity.agent.npc.slayer

import com.grinder.game.World
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.combat.subscribe
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Position
import com.grinder.util.oldgrinder.Area
import kotlin.random.Random

/**
 * Nechryaels are demon-like NPCs that summon 'Death Spawns' upon being hit. Up to a max of 3 at a time.
 */
class Nechryael(id:Int, pos:Position): NPC(id, pos) {
    var spawnCounters = 0

    init {
        combat.subscribe { it ->
            if (it is IncomingHitApplied){
                val hit = it.hit
                if (spawnCounters  < 3 && Random.nextInt(4) == 0) {
                    val centerPos = hit.attacker?.position
                    Area(1)
                        .getAbsolute(centerPos)
                        .findRandomOpenPosition(plane, 1, centerPos)
                        .ifPresent { position ->
                            val spawn = BasicDeathSpawn(this, position)
                            spawn.combat.initiateCombat(hit.attacker)
                            World.npcAddQueue.add(spawn)
                            spawnCounters++
                        }
                }
            }
            return@subscribe false
        }
    }
}

/**
 * BasicDeathSpawn is the death spawn that
 */
class BasicDeathSpawn(val spawner:Nechryael, pos:Position): NPC(10, pos) {
    init {
        area = spawner.area
        attackStrategy = object : MeleeAttackStrategy() {
            override fun type() = AttackType.SPECIAL
        }
    }

    private var despawnTimer = 60

    override fun sequence() {
        if (--despawnTimer == 0) {
            spawner.spawnCounters--
            World.npcRemoveQueue.add(this)
            return
        }
        super.sequence()
    }

    override fun appendDeath() {
        spawner.spawnCounters--
        super.appendDeath()
    }

    override fun respawn() {} // do not respawn
}