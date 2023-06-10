package com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.task

import com.grinder.game.collision.CollisionManager
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.areas.instanced.VorkathArea
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Priority
import kotlin.random.Random

/**
 * Poison Pool Quickfire Barrage: Launches acid around the arena and proceeds to fire a barrage of 25 dragonfires at the player.
 * Any damage taken while standing on the acid heals Vorkath for the same amount; up to 10 damage per tick.
 * Should the player stand still, the dragonfire barrage deals 30-40 damage per hit regardless of prayers.
 * Additionally, Vorkath takes 50% reduced damage until the special attack ends.
 *
 * @author  Leviticus
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/10/2019
 * @version 1.0
 */
class QuickfireTask(private val vorkathBoss: VorkathBoss) : Task(1, vorkathBoss, true) {


    private val baseX: Int = vorkathBoss.x shr 8 shl 8
    private val baseY: Int = vorkathBoss.y shr 8 shl 8

    private val poolLocations = List(Random.nextInt(50, 60)) {
        possibleSplatLocations.random().transform(baseX, baseY, 0)
    }

    private var cycle = 0

    override fun execute() {

        if(!vorkathBoss.isAlive){
            stop()
            return
        }

        when(cycle++) {
            0 -> {
                val target = vorkathBoss.owner

                if(target is Player) {
                    val area = target.area
                    if(area is VorkathArea) {
                        vorkathBoss.performAnimation(Animation(7957, Priority.HIGH))
                        poolLocations.forEach {
                            val projectile = Projectile(vorkathBoss.centerPosition, it, createPoisonProjectile())
                            projectile.sendProjectile()
                            projectile.onArrival {
                                PoisonSplashObject(it, area).spawn()
                            }
                        }
                    }
                }
            }
            30 -> {
                stop()
                return
            }
        }
        if(cycle > 3){
            vorkathBoss.owner?.let {
                if(!it.position.isWithinDistance(vorkathBoss.position, 20)){
                    stop()
                    return
                }
                val damageTick = cycle % 2 == 0

                if(damageTick){
                    for(pool in poolLocations){
                        if(it.position.`is`(pool, true)){
                            PoisonEffect.applyPoisonTo(it, PoisonType.MILD)
                            val poisonDamage = Damage.createPoisonHit(10)
                            it.combat.queue(poisonDamage)
                            vorkathBoss.heal(poisonDamage.value)
                        }
                    }
                }

                val target = it.position.clone()
                val projectile = Projectile(vorkathBoss.centerPosition, target, createFireProjectile())

                projectile.sendProjectile()
                projectile.onArrival {
                    if (it!!.isUntargetable || it!!.isTeleporting)
                        return@onArrival
                    if (it.position.sameAs(target))
                        it.combat.queue(Damage.create(20, 30))
                    it.packetSender.sendGraphic(Graphic(1436, GraphicHeight.LOW), target)
                }
            }
        }
    }

    companion object {

        val possibleSplatLocations = ArrayList<Position>()

        init {
            val boundary = Boundary(92, 100, 93, 101)

            (VorkathArea.fightBounds.x..VorkathArea.fightBounds.x2).forEach { x->
                (VorkathArea.fightBounds.y..VorkathArea.fightBounds.y2).forEach {y ->
                    val position = Position(x, y)
                    if(!boundary.contains(position)){
                        possibleSplatLocations.add(position)
                    }
                }
            }
        }

        fun createPoisonProjectile() = object : ProjectileTemplate {
            override fun sourceSize() = 5
            override fun sourceOffset() = 0
            override fun projectileId() = 1483
            override fun startHeight() = 31
            override fun endHeight() = 0
            override fun curve() = 45
            override fun lifetime() = 25
            override fun delay() = 60
        }
        fun createFireProjectile() = object : ProjectileTemplate {
            override fun sourceSize() = 1
            override fun sourceOffset() = 0
            override fun projectileId() = 1482
            override fun startHeight() = 43
            override fun endHeight() = 5
            override fun curve() = 0
            override fun lifetime() = 0
            override fun delay() = 10
        }

        class PoisonSplashObject(position: Position, private val area: VorkathArea)  {

            val obj: DynamicGameObject = DynamicGameObject.createPublic(32000, position, 10, Direction.getRandomValidDirection().toInteger())!!

            fun spawn(){

                area.add(obj)

                TaskManager.submit(object: Task(1) {
                    var ticks = 30
                    override fun execute() {
                        if(--ticks <= 0 || area.players.isEmpty()){
                            stop()
                        }
                    }

                    override fun stop() {
                        super.stop()
                        area.remove(obj)
                    }
                })
            }
        }
    }
}