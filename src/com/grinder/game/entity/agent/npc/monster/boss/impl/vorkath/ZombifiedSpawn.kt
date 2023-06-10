package com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath

import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.movement.task.MovementTask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.NpcID
import com.grinder.util.Priority
import com.grinder.util.timing.TimerKey

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/10/2019
 * @version 1.0
 */
class ZombifiedSpawn(bossNPC: VorkathBoss, position: Position, private val target: Player)
    : BossMinion<VorkathBoss>(bossNPC, NpcID.ZOMBIFIED_SPAWN, position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED) {

    private var isCrumbled = false
    private var inReach = false

    init {
        race = MonsterRace.UNDEAD
        combat.disable()
        combat.onIncomingHitQueued {
            if(context.usedAny(CombatSpellType.CRUMBLE_UNDEAD, CombatSpellType.UNDEAD_BASH)){
                isCrumbled = true
                setCheckAccuracy(false)
                setCheckStrength(false)
                setDamageRange(38..38)
                createHits(1)
            }
        }
        onEvent { event ->
            when (event) {
                MonsterEvents.ADDED -> {
                    bossNPC.stopAttacking = true

                    motion.start(object : MovementTask<NPC?>(Priority.HIGHEST, this) {
                        override fun sequence() {
                            if (getPosition().sameAs(target.position)) {
                                stop()
                                return
                            }

                            if (!motion.isMoving)
                                motion.traceTo(target.position)
                        }

                    }, true)

                    TaskManager.submit(this, 20) {
                        if(!isCrumbled && isActive && isAlive && !isDying)
                            remove()
                    }
                }
                MonsterEvents.REMOVED -> {
                    bossNPC.stopAttacking = false
                    target.timerRepository.cancel(TimerKey.FREEZE)
                    target.ifPlayer {
                        it.sendMessage("You become unfrozen as you kill the spawn.")
                    }
                }
                MonsterEvents.POST_SEQUENCE -> {

                    if(isDying || isCrumbled) {
                        isCrumbled = false
                        return@onEvent
                    }

                    if(inReach)
                        return@onEvent

                    if(target.position.isWithinDistance(this.position, 2)){
                        inReach = true

                        TaskManager.submit(object : Task(1, this, false) {
                            var count = 0
                            override fun execute() {
                                if (isDying || !isAlive || !isActive) {
                                    stop()
                                    return
                                }

                                if (count == 1) {
                                    motion.update(MovementStatus.DISABLED)
                                    target.packetSender.sendGraphic(Graphic(1436, 0), getPosition())
                                } else if (count == 2) {
                                    target.combat.queue(Damage.create(30, 57))
                                    target.combat.reset(false, true)
                                    remove()
                                    stop()
                                }

                                count++;
                            }
                        })
                    }
                }
                else -> {}
            }
        }
    }

    override fun skipNextRetreatSequence() = true
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun useSmartPathfinding() = true

}