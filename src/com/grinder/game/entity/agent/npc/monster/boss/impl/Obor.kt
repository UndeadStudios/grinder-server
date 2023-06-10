@file:JvmName("Obor")

package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.model.*
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.CameraShakeTask
import com.grinder.game.task.impl.ForceMovementTask
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

/**
 * Obor is the hill giant Titan. Available to free-to-play in osrs.
 * @param position Spawn position.
 */
class Obor(position: Position) : Boss(7416, position), AttackProvider {

    companion object {
        val ROCK_FALL_GFX = 60
        val STOMP_GFX = Graphic(140, GraphicHeight.LOW)
        val CLUB_ANIM = Animation(4666)
        val SLAM_ANIM = Animation(7183)
        val FALL_ANIM = Animation(7210, Priority.LOW)
        val SPAWN_POSITION = Position(3094, 9800, 0)
        val ARENA_BOUNDARY = listOf(Boundary(3084, 3099, 9797, 9804))
        val ROCK_FALL_SOUND = Sound(1464)

        /**
         * Performs a knockback hit to the target.
         * @param knockbacker The one performing the knockback.
         * @param hit The one being hit.
         */
        fun knockback(knockbacker: Agent, hit: Agent) {
            if (!hit.isPlayer || hit.asPlayer.forceMovement != null)
                return
            val dir = Direction.getDirection(knockbacker.centerPosition, hit.position)
            var knockLoc = hit.position.clone()
            while (CollisionManager.canMove(knockLoc, dir.id)) {
                knockLoc = knockLoc.transform(dir.x, dir.y, 0)
            }
            if (knockLoc == hit.position)
                hit.performAnimation(FALL_ANIM)
            else
                TaskManager.submit(
                    ForceMovementTask(
                        hit.asPlayer, 3,
                        ForceMovement(hit.position.clone(), knockLoc.getDelta(hit.position), 0, 15, dir.id, 1157)
                    )
                )
        }
    }

    override fun generateAttack(): BossAttack {
        return object : BossAttack(this) {
            override fun postHitAction(actor: Boss, target: Agent) {
                if (target.isPlayer) {
                    if (type() == AttackType.MELEE) {
                        knockback(actor, target)
                    } else if (type() == AttackType.RANGED) {
                        target.asPlayer.playSound(ROCK_FALL_SOUND)
                        TaskManager.submit(CameraShakeTask(target.asPlayer, 1, 10, 40, 3, 1))

                        // Line of sight
                        val sight = CollisionManager.LineOfSightTiles(actor.position, target.position)
                        sight.forEachIndexed { index, point ->
                            val gfx = Graphic(ROCK_FALL_GFX, index * 10)
                            target.asPlayer.packetSender.sendGraphic(gfx, Position(point.location.x, point.location.y, target.position.z))
                        }
                    }
                }
            }
        }
    }

    // Not required, we base our attacks on the pray style.
    override fun attackTypes(): AttackTypeProvider {
        return AttackTypeProvider { return@AttackTypeProvider null }
    }

    override fun randomizeAttack() {
        val attacker = combat.target
        if (PrayerHandler.isActivated(attacker, PrayerHandler.PROTECT_FROM_MELEE))
            this.setPreferredAttackType(AttackType.RANGED)
        else if (PrayerHandler.isActivated(attacker, PrayerHandler.PROTECT_FROM_MISSILES))
            this.setPreferredAttackType(AttackType.MELEE)
        else
            this.setPreferredAttackType(if (Random.nextBoolean()) AttackType.MELEE else AttackType.RANGED)
    }

    override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return 1
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 6
    }

    override fun fetchAttackDuration(type: AttackType?): Int {
        return 6
    }

    override fun getAttackAnimation(type: AttackType?): Animation {
        // 4666 - melee
        // 7183 - range (on ground gfx 140), player hit gfx 60
        // entrance: 3091 9815 0, Spawn: 3094 9800
        return if (type == AttackType.MELEE) CLUB_ANIM else SLAM_ANIM
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> {
        val hitTemplate = HitTemplateBuilder(type).setDelay(if (type == AttackType.RANGED) 2 else 0)
        return hitTemplate.buildAsStream()
    }

    override val circumscribedBoundaries: List<Boundary>
        get() = ARENA_BOUNDARY

    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> {
        if (type == AttackType.RANGED)
            return Optional.of(STOMP_GFX)
        return super.fetchAttackGraphic(type)
    }

    // instanced boss, no respawn.
    override fun respawn() {}
}