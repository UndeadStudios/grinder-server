package com.grinder.game.entity.agent.npc.monster

import com.grinder.game.World
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossCombat
import com.grinder.game.entity.incInt
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute

/**
 * Represents a combative [NPC].
 *
 * TODO: turn all combat-enabled npcs to monsters.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   15/10/2019
 * @version 1.0
 */
abstract class Monster(id: Int, position: Position) : NPC(id, position) {

    private val combat = MonsterCombat(this)

    private val eventListeners = ArrayList<MonsterEventListener>()

    private val onKillListeners = ArrayList<(Agent) -> Unit>()

    lateinit var race: MonsterRace

    override fun getCombat() = combat

    abstract fun attackRange(type: AttackType): Int

    open fun skipRespawnSequence() = false

    open fun skipProjectileClipping() = false

    /**
     * Should the next [BossCombat.sequence] be skipped for this [Boss],
     * this does not skip damage applications?
     */
    open fun skipNextCombatSequence() = false

    /**
     * Should the next [BossCombat.retreatProcess] sequence be skipped?
     */
    open fun skipNextRetreatSequence() = false

    /**
     * Should a smart pathfinding algorithm be used when tracing to a target?
     */
    open fun useSmartPathfinding() = false

    override fun sequence() {
        notify(MonsterEvents.PRE_SEQUENCE)
        super.sequence()
        incInt(Attribute.SEQUENCE_COUNT, 1)
        notify(MonsterEvents.POST_SEQUENCE)
    }

    final override fun onAdd() {
        super.onAdd()
        notify(MonsterEvents.ADDED)
    }

    final override fun onRemove() {
        super.onRemove()
        notify(MonsterEvents.REMOVED)
    }

    override fun appendDeath() {
        super.appendDeath()
        notify(MonsterEvents.DYING)
    }

    fun notify(event: MonsterEvents) {
        eventListeners.forEach {
            it.on(event)
        }
    }

    fun onEvent(function: (MonsterEvents) -> Unit) {
        eventListeners += object : MonsterEventListener {
            override fun on(event: MonsterEvents) {
                function.invoke(event)
            }
        }
    }


    fun onKilled(function: (Agent) -> Unit){
        onKillListeners += function
    }
    
    fun killedBy(killer: Agent) {
        onKillListeners.forEach {
            it.invoke(killer)
        }
    }

    /**
     * Add this [Monster] to the [World.npcAddQueue].
     */
    fun spawn() {
        World.npcAddQueue.add(this)
    }

    /**
     * Add this [Monster] to the [World.npcRemoveQueue].
     */
    fun remove() {
        World.npcRemoveQueue.add(this)
    }
}