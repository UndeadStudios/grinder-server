package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.*
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.Priority
import com.grinder.util.time.SecondsTimer
import com.grinder.util.time.TimeUtil
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/09/2019
 * @version 1.0
 */
class CerberusBoss(npcId: Int, position: Position) : Boss(npcId, position), AttackProvider {

    private var special : Special? = Special.SUCCESSIVE_ATTACK_TYPES
    private val specialTimer = SecondsTimer()

    private var soulsAlive = 0

    init {
        motion.update(MovementStatus.DISABLED)
        race = MonsterRace.HELL_HOUND
        onEvent {
            if(it == MonsterEvents.REMOVED){
                World.npcRemoveQueue.add(this);
                TaskManager.cancelTasks(this)
            }
        }



        combat.onOutgoingHitApplied {
            if (target is Player) {
                val player = target as Player

                val correctPrayerId = PrayerHandler.getProtectingPrayer(attackType)

                if (player.prayerActive[correctPrayerId]) {
                    totalDamage = (totalDamage * .3).toInt()
                }

                //Cerberus can use all three forms of combat against the player, all having a max hit of 23
                if (totalDamage > 23) totalDamage = 23
                return@onOutgoingHitApplied
            }
        }
    }

    override fun generateAttack() = object: BossAttack(this) {
            override fun sequence(actor: Boss, target: Agent) {
                super.sequence(actor, target)
                special?.let {
                    when(it){
                        Special.SUMMON_SOULS -> {
                            TaskManager.submit(createSpawnSoulTask())
                            actor.combat.extendNextAttackDelay(4)
                            (actor as CerberusBoss).special = null
                        }
                        Special.SPIT_BALLS_OF_FIRE -> {
                            generateFallingProjectiles(target).forEach { fire ->
                                executeProjectile(target).accept(fire)
                            }
                            actor.combat.extendNextAttackDelay(3)
                        }
                        Special.SUCCESSIVE_ATTACK_TYPES -> {
                            TaskManager.submit(createSuccessiveAttackTask(actor, target))
                            actor.combat.extendNextAttackDelay(2)
                        }
                    }
                }
            }
        }

    override fun randomizeAttack() {
        super.randomizeAttack()
        if (specialTimer.finished()) {
            specialTimer.start(Random.nextInt(25, 35))
            bossAttack.setType(SPECIAL)
            special = if (hitpoints < 400 && soulsAlive == 0 && Random.nextBoolean()) {
                Special.SUMMON_SOULS
            } else {
                Special.SUCCESSIVE_ATTACK_TYPES
            }
            return
        }
        if (hitpoints < 200) {
            if (Misc.randomChance(20.0F)) {
                bossAttack.setType(SPECIAL)
                special = Special.SPIT_BALLS_OF_FIRE
                return
            }
        }
        special = null
    }

    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun attackTypes() : AttackTypeProvider {
        return equalChances(MAGIC, MELEE, RANGED)
    }

    override fun attackRange(type: AttackType) = when(type) {
        MELEE -> 2
        else -> 17
    }
    override fun maxTargetsHitPerAttack(type: AttackType) = 4
    override fun fetchAttackDuration(type: AttackType?) = 6
    override fun getAttackAnimation(type: AttackType?): Animation = special?.animation?:when(type) {
                MELEE -> Animation(4491, Priority.HIGH)
                MAGIC -> Animation(4489, Priority.HIGH)
                RANGED -> Animation(4490, Priority.HIGH)
                else -> Animation.DEFAULT_RESET_ANIMATION
            }
    override fun fetchProjectiles(type: AttackType?) = when(type) {
        MAGIC -> Stream.of(createMagicProjectile())
        RANGED -> Stream.of(createRangedProjectile())
        else -> Stream.empty()
    }

    override fun fetchHits(type: AttackType?) = (when (special) {
        null -> {
            if (type == SPECIAL)
                Stream.empty()
            else {
                val builder = HitTemplate
                        .builder(type)
                        .setDelay(if (type == MELEE) 0 else 3)
                if(type == RANGED)
                    builder.setDelay(1).setSuccessOrFailedGraphic(Graphic(1244, GraphicHeight.HIGH, Priority.HIGH))
                builder.buildAsStream()
            }
        }
        Special.SUCCESSIVE_ATTACK_TYPES -> Stream.of(
                HitTemplate.builder(RANGED).setDelay(2).build(),
                HitTemplate.builder(MAGIC).setDelay(3).build(),
                HitTemplate.builder(MELEE).setDelay(4).build()
        )
        else -> Stream.empty()
    })!!

    override fun fetchTextAboveHead(type: AttackType?) = Optional.ofNullable(special?.shout)

    private fun createSpawnSoulTask() = object : Task(1, this, false) {

        private val rangedSoul = SummonedSoulMinion(this@CerberusBoss,
                NpcID.SUMMONED_SOUL,
                Position(1239, 1263, position.z),
                RANGED
        )

        private val magicSoul = SummonedSoulMinion(this@CerberusBoss,
                NpcID.SUMMONED_SOUL_5868,
                Position(1240, 1263, position.z),
                MAGIC
        )

        private val meleeSoul = SummonedSoulMinion(this@CerberusBoss,
                NpcID.SUMMONED_SOUL_5869,
                Position(1241, 1263, position.z),
                MELEE
        )

        private var tick = 0

        override fun execute() {
            when(tick++) {
                0 -> {
                    meleeSoul.spawn()
                    rangedSoul.spawn()
                    magicSoul.spawn()
                }
                2 -> {
                    meleeSoul.walkSouth()
                    rangedSoul.walkSouth()
                    magicSoul.walkSouth()
                }
                12 -> meleeSoul.damageNearbyPlayers()
                14 -> meleeSoul.walkNorth()
                15 -> rangedSoul.damageNearbyPlayers()
                17 -> rangedSoul.walkNorth()
                18 -> magicSoul.damageNearbyPlayers()
                20 -> magicSoul.walkNorth()
                30 -> {
                    stop()
                }
            }
        }

        override fun stop() {
            super.stop()
            rangedSoul.remove()
            magicSoul.remove()
            meleeSoul.remove()
        }
    }

    private fun executeProjectile(target: Agent): Consumer<Projectile> {
        return Consumer { projectile ->

            val hitDelay = executeProjectile(projectile)

            World.spawn(TileGraphic(projectile.target, Graphic(1246, hitDelay / 2, GraphicHeight.LOW)))
            val spawnDelay = TimeUtil.CLIENT_CYCLES.toGameCycles(hitDelay.toLong())

            TaskManager.submit(object : Task(1, this, false) {
                var tick = 0

                override fun execute() {

                    if(tick >= spawnDelay + 4)
                        stop()
                    else if(tick >= spawnDelay){
                        if(target.position.sameAs(projectile.target)){
                            target.combat.queue(Damage(Random.nextInt(0, 30), DamageMask.REGULAR_HIT))
                        }
                    }
                    tick++
                }

                override fun stop() {
                    super.stop()
                    World.spawn(TileGraphic(projectile.target, Graphic(-1)))
                }
            })
        }
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType) = type == MELEE

    private fun generateFallingProjectiles(target: Agent): Array<Projectile> {

        val positions = arrayOfNulls<Position>(3)

        val throwCalculated = !target.motion.completed()
        val perfectLine = Misc.randomChance(50.0f)

        val projectileDirection = if (throwCalculated)
            target.walkingDirection
        else
            walkingDirection

        val deltaX = projectileDirection.directionDelta[0]
        val deltaY = projectileDirection.directionDelta[1]
        val standard = deltaX + deltaY

        val position1 = target.position.copy()
        val position2 = if (throwCalculated)
            position1.copy().add((if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard, (if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard)
        else
            position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))
        val position3 = if (throwCalculated)
            position2.copy().add((if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard, (if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard)
        else
            position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))

        positions[0] = position1
        positions[1] = position2
        positions[2] = position3

        return Array(positions.size) {
            Projectile(position, positions[it], 1247, 120, 30, 0, 0, 0)
        }
    }
    private fun createSuccessiveAttackTask(bossNPC: Boss, target: Agent) = object : Task(1) {

        private var tick = 0

        override fun execute() {
            when(tick++){
                0 -> bossNPC.executeProjectile(Projectile(bossNPC, target, createMagicProjectile()))
                1 -> bossNPC.executeProjectile(Projectile(bossNPC, target, createRangedProjectile()))
                2 -> {
                    stop()
                }
            }
        }
    }


    private fun createMagicProjectile(): ProjectileTemplate {
        return ProjectileTemplate.builder(1242)
                .setSourceSize(3)
                .setStartHeight(71)
                .setEndHeight(30)
                .setCurve(1)
                .setSpeed(52)
                .setDelay(30)
                .build()
    }
    private fun createRangedProjectile(): ProjectileTemplate {
        return ProjectileTemplate.builder(1245)
                .setSourceSize(3)
                .setSourceOffset(1)
                .setStartHeight(75)
                .setEndHeight(32)
                .setCurve(5)
                .setSpeed(8)
                .setDelay(30)
                .build()
    }

    enum class Special(val animation: Animation? = null,
                       val shout: String? = null
    ) {
        SUCCESSIVE_ATTACK_TYPES(Animation(4492, Priority.HIGH)),
        SPIT_BALLS_OF_FIRE(Animation(4493, Priority.HIGH), "Grrrrrrrrr"),
        SUMMON_SOULS(Animation(4494, Priority.HIGH), "Aaarrrooooooo")
    }

    class SummonedSoulMinion(
            bossNPC: CerberusBoss,
            id: Int,
            position: Position,
            val type: AttackType
    ) : BossMinion<CerberusBoss>(bossNPC, id, position,
            BossMinionPolicy.NO_RESPAWN,
            BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED)
    {

        init {
            motion.setCanReTrace(false)
            combat.disable()
            onEvent {
                if(it == MonsterEvents.ADDED){
                    bossNPC.soulsAlive++
                } else if(it == MonsterEvents.REMOVED){
                    bossNPC.soulsAlive--
                }
            }
        }

        fun walkSouth(){
            motion.setCanReTrace(true)
            motion.traceTo(position.clone().add(0, -6))
            motion.setCanReTrace(false)
            setEntityInteraction(bossNPC.combat.target)
        }

        fun walkNorth(){
            combat.disable()
            motion.setCanReTrace(true)
            motion.traceTo(position.clone().add(0, +6))
            motion.setCanReTrace(false)
            setEntityInteraction(null)
        }

        fun damageNearbyPlayers(){

            val animationId = when(type){
                RANGED -> 4503
                MAGIC -> 4504
                else -> -1
            }

            if(animationId != -1)
                performAnimation(Animation(animationId, Priority.HIGH))

            combat.enable()
            bossNPC.playerStream(20)
                    .forEach { player ->
                        player?.let {target ->

                            val protectivePrayerId = PrayerHandler.getProtectingPrayer(type)
                            val blocked = target.hasActivePrayer(protectivePrayerId)
                            val damage = if(blocked)
                                Damage.create(30)
                            else
                                Damage.createBlockedHit()

                            target.combat.queue(damage)

                            if(blocked){
                                var prayerDecrement = 30.0
                                if(EquipmentUtil.isWearingSpectralShield(target))
                                    prayerDecrement *= 0.5
                                target.skillManager.decreaseLevelTemporarily(Skill.PRAYER, prayerDecrement.toInt(), 0)
                            }

                            when(type) {
                                RANGED -> target.packetSender.sendGraphic(Graphic(1243), target.position)
                                MAGIC -> executeProjectile(Projectile(this, player, 100, 0, 55, 25, 43, 10))
                                MELEE -> executeProjectile(Projectile(this, player, 1248, 20, 55, 25, 43, 3))
                                else -> {}
                            }
                        }
                    }
        }
    }
}