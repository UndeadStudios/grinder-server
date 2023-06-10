package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.ONE_FOURTH
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.THREE_FOURTH
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * Lizardman shaman is a [Boss] from OSRS.
 *
 * TODO: Shamans cannot jump if the player is next to an obstacle or wall, as they occupy a 3x3 space.
 * TODO: Spawns special attack
 *
 * @author MrJason83 (base)
 * @author Stan van der Bend (converted to new boss system)
 *
 * @param id        the id of the lizardman shaman instance (there are multiple).
 * @param position  the [Position] to spawn this instance at.
 */
class LizardmanShaman(id: Int, position: Position) : Boss(id, position), AttackProvider {

    // TODO: Make it not jump if your in a corner spot like OSRS
    // TODO: Add spawns that explode after 3-4 seconds and follow you (removed if the boss is dead ofc)

    private var specialAttackType: SpecialAttackType? = null

    override fun generateAttack() = object : BossAttack(this) {
        override fun sequence(actor: Boss, target: Agent) {

            when(specialAttackType) {
                SpecialAttackType.ACID_SHAYZIEN_ATTACK -> {

                    val targetedPosition = target.position.clone()

                    val projectile = Projectile(actor.position, targetedPosition, ProjectileTemplate
                            .builder(1293)
                            .setHeights(80, 43)
                            .setDelay(45)
                            .setSpeed(15)
                            .build())

                    projectile.onArrival {

                        World.spawn(TileGraphic(targetedPosition, Graphic(1294)))

                        if (target.position.sameAs(targetedPosition)) {

                            val hit = Hit(actor, target, this, HitTemplate
                                    .builder(AttackType.SPECIAL)
                                    .setDelay(1)
                                    .build())

                            hit.totalDamage = modifyOutgoingDamage(target, hit.totalDamage)

                            actor.combat.queueOutgoingHit(hit)
                        }
                    }
                    projectile.sendProjectile()
                }
                SpecialAttackType.JUMP_ATTACK -> {

                    val targetedPosition = target.position.clone()

                    TaskManager.submit(3) {

                        actor.moveTo(targetedPosition)
                        actor.performAnimation(Animation(6946, 0, 1, Priority.HIGH))

                        if (target.position.isWithinDistance(targetedPosition, 2)) {
                            actor.combat.queueOutgoingHit(Hit(actor, target, this, HitTemplate
                                    .builder(AttackType.SPECIAL)
                                    .setDelay(1)
                                    .build()))
                        }
                    }
                }
                else -> super.sequence(actor, target)
            }
        }
    }

    override fun randomizeAttack() {
        super.randomizeAttack()
        specialAttackType = if(bossAttack.type() == AttackType.SPECIAL)
            Misc.randomEnum(SpecialAttackType::class.java)
        else
            null
    }

    override fun attackTypes() = AttackType
            .builder()
            .add(THREE_FOURTH, AttackType.MELEE, AttackType.RANGED)
            .add(ONE_FOURTH, AttackType.SPECIAL)
            .build()

    override fun maxTargetsHitPerAttack(type: AttackType) = 1

    override fun attackRange(type: AttackType) = when(type){
        AttackType.MELEE -> 1
        else -> 6
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 31 else if (type == AttackType.RANGED) 21 else super.getMaxHit(type)
    }

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        return if(type == AttackType.RANGED)
            ProjectileTemplate
                    .builder(1291)
                    .setHeights(80, 43)
                    .setDelay(45)
                    .setSpeed(15)
                    .buildAsStream()
        else Stream.empty()
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate>? {
        return if(type == AttackType.SPECIAL)
            Stream.empty()
        else HitTemplate
                .builder(type)
                .setDelay(if (type == AttackType.MELEE) 0 else 2)
                .buildAsStream()
    }

    override fun getAttackAnimation(type: AttackType?): Animation {
        return if(specialAttackType == SpecialAttackType.JUMP_ATTACK)
            Animation(7152, Priority.HIGH)
        else
            Animation(7193, Priority.HIGH)
    }

    override fun fetchAttackDuration(type: AttackType?) = 4

    private fun modifyOutgoingDamage(agent: Agent, damage: Int): Int {

        if (agent !is Player)
            return damage

        val count = EquipmentUtil.countShayzienArmourPieces(agent.equipment)

        if(count >= 5)
            return 0

        return when (count) {
            4 -> (damage * 0.20).toInt()
            3 -> (damage * 0.40).toInt()
            2 -> (damage * 0.60).toInt()
            1 -> (damage * 0.80).toInt()
            else -> damage
        }
    }

    private enum class SpecialAttackType {
        ACID_SHAYZIEN_ATTACK,
        JUMP_ATTACK
    }
}