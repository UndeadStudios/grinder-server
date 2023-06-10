@file:JvmName("Bryophyta")

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
 * Bryophyta is the moss giant Titan. Available to free-to-play in osrs.
 * @param position Spawn position.
 */
class Bryophyta(position: Position) : Boss(8195, position), AttackProvider {

    companion object {
    val MAGIC_GFX = Graphic(571, GraphicHeight.LOW)
        val SPAWN_POSITION = Position(3220, 9936, 0)

    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }


    // Not required, we base our attacks on the pray style.
    override fun attackTypes(): AttackTypeProvider {
        return AttackTypeProvider { return@AttackTypeProvider null }
    }

    override fun randomizeAttack() {
        val attacker = combat.target
        if (PrayerHandler.isActivated(attacker, PrayerHandler.PROTECT_FROM_MELEE))
            this.setPreferredAttackType(AttackType.MAGIC)
        else if (PrayerHandler.isActivated(attacker, PrayerHandler.PROTECT_FROM_MAGIC))
            this.setPreferredAttackType(AttackType.MELEE)
        else
            this.setPreferredAttackType(if (Random.nextBoolean()) AttackType.MELEE else AttackType.MAGIC)
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

    override fun getAttackAnimation(type: AttackType?) = Animation(4658)

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> {
        val hitTemplate = HitTemplateBuilder(type).setDelay(if (type == AttackType.MAGIC) 2 else 0)
        return hitTemplate.buildAsStream()
    }

    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> {
        if (type == AttackType.MAGIC)
            return Optional.of(MAGIC_GFX)
        return super.fetchAttackGraphic(type)
    }

    // instanced boss, no respawn.
    override fun respawn() {}
}