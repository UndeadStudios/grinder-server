package com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah

import com.grinder.Server
import com.grinder.game.World
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.impl.StunEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.model.Animation
import com.grinder.game.model.Direction
import com.grinder.game.model.Position
import com.grinder.game.model.areas.instanced.ZulrahShrine
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.Priority
import com.grinder.util.oldgrinder.EquipSlot
import com.grinder.util.time.SecondsTimer
import java.util.stream.Stream
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   02/12/2019
 * @version 1.0
 */
class ZulrahBoss(position: Position, basePos: Position): Boss(NpcID.ZULRAH, position) {

    private var diving = false
    private val pendingSnakelings = ArrayList<SnakelingMinion>()
    private var patternType = ZulrahPatternType.values().random()
    private var pattern = patternType.patterns.first()

    private var base = basePos;

    private val diveTimer = SecondsTimer()
    private val snakelingTimer = SecondsTimer()
    private val fumeTimer = SecondsTimer()
    private var patternAccountCount = 0
    private var targeting = false
    private var skipCombat = false
    private var spawningFumes = false

    private val whiteOrbPositions = HashSet<Position>()

    val fumePositions = HashSet<Position>()

    override fun negateAllIncomingDamage(details: AttackContext): Boolean {
        return details.attackerEquipment.get(EquipSlot.WEAPON).definition.name.contains("halberd") && details.used(AttackType.MELEE)
    }

    init {
        diveTimer.start(17)
        snakelingTimer.start(10)
        fumeTimer.start(20)
        combat.extendNextAttackDelay(4)
        skipCombat = true
        onEvent {
            if(it == MonsterEvents.ADDED){
                TaskManager.submit(this,4) {
                    skipCombat = false
                    combat.initiateCombat(owner)
                    createFumes(ZulrahCloudLocation.MIDDLE)
                }
            }
            if(it == MonsterEvents.REMOVED || it == MonsterEvents.DYING) {
                World.npcRemoveQueue.add(this);
                TaskManager.cancelTasks(this);
            }
        }
    }

    override fun sequence() {

        if(!isDying && !targeting && !diving && !spawningFumes) {
            val canFume = fumeTimer.finished() && pattern.cloudLocation != null

            if (diveTimer.finished() && Misc.randomChance(33F))
                switchPattern()
            else if (canCreateSnakeling() && snakelingTimer.finished() && Misc.randomChance(if(canFume) 40f else 20f))
                createSnakeling()
            else if(canFume){
                createFumes(pattern.cloudLocation!!)
            }
        }

        super.sequence()
    }

    override fun setEntityInteraction(entity: Entity?): Agent {
        if(skipCombat || targeting)
            return this
        return super.setEntityInteraction(entity)
    }

    override fun immuneToAttack(type: AttackType): Pair<Boolean, String?> {
        return super.immuneToAttack(AttackType.MELEE)
    }

    private fun switchPattern(){
        val patterns = patternType.patterns
        val currentIndex = patterns.indexOf(pattern)
        var nextIndex = currentIndex + 1
        if(nextIndex >= patterns.size) {
            nextIndex = 0
            patternType = ZulrahPatternType.values().random()
        }
        dive(patternType.patterns[nextIndex])
    }

    private fun dive(nextPattern: ZulrahPattern) {

        patternAccountCount = 0
        diving = true
        diveTimer.start(16)

        TaskManager.submit(this,1) {
            performAnimation(Animation(5072, Priority.MEDIUM))
            owner?.sendMessage("Zulrah dives into the swamp...")
        }

        TaskManager.submit(this,3) {
            val offset = nextPattern.location.position
            val nextPosition = base.transform(offset.x, offset.y, 0)
            if(!nextPosition.sameAs(position))
                moveTo(nextPosition)
        }
        TaskManager.submit(this,4) {
            npcTransformationId = nextPattern.state.npcId
            pattern = nextPattern
            performAnimation(Animation(5073, Priority.HIGH))
            pendingSnakelings.forEach {
                it.spawn()
                (area as? ZulrahShrine)?.add(it)
            }
            pendingSnakelings.clear()
            whiteOrbPositions.clear()
            combat.setNextAttackDelay(5)
        }
        TaskManager.submit(this,5) {
            diving = false
        }
    }

    private fun createFumes(locations: ZulrahCloudLocation) {

        fumeTimer.start(15)

        val newFumes = HashSet<Position>()

        ZulrahCloudLocation.values().map {
            it.positions.map { fumePos -> base.transform(fumePos.x, fumePos.y, 0) }
        }.forEach {
            for (pos in it) {
                if (fumePositions.add(pos) && !whiteOrbPositions.contains(pos)) {
                    newFumes.add(pos)
                }
            }
        }

        if (newFumes.isNotEmpty()) {
            spawningFumes = true
            val sortedFumes = newFumes.toList().sortedBy {
                when (locations) {
                    ZulrahCloudLocation.MIDDLE -> it.y
                    ZulrahCloudLocation.LEFT -> it.x
                    else -> -it.x
                }
            }.toMutableList()
            TaskManager.submit(object : Task(1, this, false) {
                var cycle = 0
                override fun execute() {

                    if(cycle % 3 == 0) {

                        if (sortedFumes.isEmpty()) {
                            spawningFumes = false
                            stop()
                            return
                        }

                        val first = sortedFumes.first()
                        val firstFume = ZulrahToxicFume(this@ZulrahBoss, first)
                        val projectile1 = Projectile(centerPosition, firstFume.getCenterPosition(), fumeProjectileTemplate)
                        projectile1.sendProjectile()
                        projectile1.onArrival {
                            firstFume.spawn()
                        }
                        sortedFumes.remove(first)

                        var facingPosition = first

                        if (sortedFumes.isNotEmpty()) {
                            val secondPosition = sortedFumes.first()
                            val secondFume = ZulrahToxicFume(this@ZulrahBoss, secondPosition)
                            val secondProjectile = Projectile(centerPosition, secondFume.getCenterPosition(), fumeProjectileTemplate)
                            secondProjectile.sendProjectile()
                            secondProjectile.onArrival {
                                secondFume.spawn()
                            }
                            val delta = first.getDelta(secondPosition).divide(2)
                            facingPosition = first.clone().add(delta.x, delta.y)
                            sortedFumes.remove(secondPosition)
                        }

                        setPositionToFace(facingPosition, true, 3, 3)
                        performAnimation(Animation(5068, Priority.HIGH))
                    }
                    cycle++
                }
            })
        }
    }

    private fun canCreateSnakeling(): Boolean {
        val area = this.area;
        return area is ZulrahShrine && area.npcs.size < 30;
    }

    private fun createSnakeling() {
        val area = this.area

        if (area is ZulrahShrine) {
            if (area.npcs.size > 30) {
//                Server.getLogger().warn("Too many npcs in zulrah instance for $owner at ${position.compactString()}")
//                TaskManager.cancelTasks(this);
//                World.npcRemoveQueue.add(this);
                return
            }
        }

        skipCombat = true
        snakelingTimer.start(10)
        combat.setNextAttackDelay(3)

        val offset = SnakelingMinion.snakelingPositions.random()
        val position = base.transform(offset.x, offset.y, 0)
        setPositionToFace(position, true)
        val whiteOrbProjectile = Projectile(centerPosition, position, SnakelingMinion.projectileTemplate)

        whiteOrbPositions.add(position)

        performAnimation(Animation(5068))

        TaskManager.submit(this,3) {
            skipCombat = false
            positionToFace = null
            setEntityInteraction(owner)
        }

        whiteOrbProjectile.sendProjectile()
        whiteOrbProjectile.onArrival {
            val snakeling = SnakelingMinion(this, position)
            if (diving)
                pendingSnakelings.add(snakeling)
            else {
                snakeling.spawn()
                (area as? ZulrahShrine)?.add(snakeling)
            }
        }
    }

    //override fun useSmartPathfinding() = true
    override fun respawn() {}
    override fun attackTypes() = pattern.attackTypeProvider()
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun attackRange(type: AttackType) = 30
    override fun skipNextCombatSequence() = diving || skipCombat || spawningFumes
    override fun skipProjectileClipping() = true
    override fun generateAttack() = object : BossAttack(object : AttackProvider{
        override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .setDelay(if (type == AttackType.MELEE) 0 else 2)
            .buildAsStream()
        override fun getAttackAnimation(type: AttackType?) = pattern.state.animation
        override fun fetchAttackDuration(type: AttackType?) = pattern.attackSpeed()
        override fun fetchProjectiles(type: AttackType?) = if(type == AttackType.MELEE)
            Stream.empty()
        else
            Stream.of(ProjectileTemplate
                .builder(if(type == AttackType.MAGIC) 1046 else 1044)
                .setDelay(36)
                .setSpeed(18)
                .setStartHeight(80)
                .setEndHeight(25)
                .setCurve(5)
                .build())
    }) {
        override fun sequence(actor: Boss, target: Agent) {
            patternAccountCount++
            if (pattern.state == ZulrahState.MAGMA) {

                targeting = true

                val animation = when (actor.getDirection(target.position)) {
                    Direction.WEST -> Animation(5806, Priority.HIGH)
                    Direction.EAST -> Animation(5807, Priority.HIGH)
                    else -> Animation(Misc.random(5806, 5807), Priority.HIGH)
                }

                actor.performAnimation(animation)

                val position = target.position.clone()

                TaskManager.submit(6) {
                    targeting = false
                    if(target.position.isWithinDistance(position, 2)){
                        target.combat.submit(StunEvent(4, false))
                        actor.combat.queueOutgoingHit(Hit(
                            actor,
                            target,
                            this,
                            HitTemplate.builder(AttackType.MELEE).setDelay(0).build()
                        ))
                    }
                }
            } else
                super.sequence(actor, target)
        }
    }

    companion object {
        val fumeProjectileTemplate = ProjectileTemplate.builder(1045)
            .setSourceSize(4)
            .setDelay(40)
            .setSpeed(35)
            .setStartHeight(80)
            .setEndHeight(2)
            .build()
    }

    class SnakelingMinion(zulrahBoss: ZulrahBoss, position: Position)
        : BossMinion<ZulrahBoss>(zulrahBoss,
        NpcID.SNAKELING,
        position,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED) {

        init {
            onEvent {
                if(it == MonsterEvents.ADDED)
                    performAnimation(Animation(2413, Priority.HIGH))
                if(it == MonsterEvents.REMOVED || it == MonsterEvents.DYING) {
                    World.npcRemoveQueue.add(this);
                    TaskManager.cancelTasks(this);
                }
            }
        }

        private val strategy = Strategy(Provider(), if(Random.nextBoolean())
            AttackType.MELEE
        else
            AttackType.MAGIC)

        override fun getAttackStrategy() = strategy

        override fun respawn() {
        }

        override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

        override fun useSmartPathfinding() = true

        class Strategy(provider: AttackProvider, private val type: AttackType) : Attack<SnakelingMinion>(provider) {
            override fun requiredDistance(actor: Agent) = if(type == AttackType.MELEE) 1 else 6
            override fun type() = this.type
        }

        class Provider : AttackProvider {
            override fun fetchAttackDuration(type: AttackType?) = 6
            override fun getAttackAnimation(type: AttackType?) = Animation(1741)
            override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = if(type == AttackType.MELEE)
                Stream.empty()
            else ProjectileTemplate
                .builder(1044)
                .setSourceOffset(1)
                .setStartHeight(20)
                .setEndHeight(31)
                .setCurve(7)
                .setSpeed(18)
                .setDelay(40)
                .buildAsStream()
            override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(if(type == AttackType.MELEE) 0 else 2)
                .buildAsStream()
        }

        companion object {

            val snakelingPositions = listOf(
                Position(2263 - ZulrahShrine.MAP_BASE.x, 3075 - ZulrahShrine.MAP_BASE.y),
                Position(2263 - ZulrahShrine.MAP_BASE.x, 3071 - ZulrahShrine.MAP_BASE.y),
                Position(2268 - ZulrahShrine.MAP_BASE.x, 3069 - ZulrahShrine.MAP_BASE.y),
                Position(2273 - ZulrahShrine.MAP_BASE.x, 3071 - ZulrahShrine.MAP_BASE.y),
                Position(2273 - ZulrahShrine.MAP_BASE.x, 3077 - ZulrahShrine.MAP_BASE.y))

            val projectileTemplate = ProjectileTemplate
                .builder(1047)
                .setDelay(35)
                .setSpeed(42)
                .setStartHeight(70)
                .setEndHeight(0)
                .setCurve(20)
                .build()
        }
    }
}