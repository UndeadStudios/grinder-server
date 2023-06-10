package com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.model.Animation
import com.grinder.game.model.FacingDirection
import com.grinder.game.model.Position
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/10/2019
 * @version 1.0
 */
class VorkathBoss(position: Position)
    : Boss(NpcID.VORKATH_8061, position), AttackProvider {

    init {
        race = MonsterRace.DRAGON
        npcTransformationId = 8059
        motion.update(MovementStatus.DISABLED)
        combat.onIncomingHitApplied {
            if (stopAttacking) {
                setNegateDamages(true)
                attacker?.messageIfPlayer("The dragon is currently immune to your attacks.")
            }
        }
    }

    private var sleeping = true
    private var attackCount = 0
    private var currentAttack: VorkathAttack? = null

    var stopAttacking = true

    fun wakeUp() {
        if (sleeping) {
            removeRespawnMessage = true
            sleeping = false
            npcTransformationId = NpcID.VORKATH_8061
            owner.performAnimation(Animation(827))
            performAnimation(Animation(7950))
            TaskManager.submit(9) {
                stopAttacking = false
                combat.initiateCombat(owner)
            }
        }
    }

    override fun generateAttack() = object : BossAttack(this) {
        override fun sequence(actor: Boss, target: Agent) {
            if (actor is VorkathBoss) {
                if (type() == AttackType.SPECIAL) {
                    currentAttack?.sequence(actor, target)
                    return
                }
            }
            super.sequence(actor, target)
        }
    }.apply {
        setType(AttackType.RANGED)
    }

    override fun respawn() {}

    override fun randomizeAttack() {
        super.randomizeAttack()
        if (attackCount++ == 6) {
            bossAttack.setType(AttackType.SPECIAL)
            currentAttack = Misc.random(
                VorkathAttack.POISON_POOL_QUICKFIRE_BARRAGE,
                VorkathAttack.ZOMBIFIED_ICE_DRAGON_FIRE
            )
            attackCount = 0
        } else {
            currentAttack = when (bossAttack.type()) {
                AttackType.MELEE -> VorkathAttack.CLAW_SWIPE
                AttackType.RANGED -> VorkathAttack.RANGED
                AttackType.MAGIC -> VorkathAttack.MAGIC
                else -> Misc.random(
                    VorkathAttack.NORMAL_DRAGON_FIRE,
                    VorkathAttack.PINK_DRAGON_FIRE,
                    VorkathAttack.VENOM_DRAGON_FIRE,
                    VorkathAttack.DRAGON_FIRE_HIGH_DAMAGE
                )
            }
        }
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType) = type == AttackType.MELEE
    override fun attackTypes() = AttackType.equalChances(
        AttackType.MELEE,
        AttackType.RANGED,
        AttackType.MAGIC,
        AttackType.SPECIAL
    )

    override fun attackRange(type: AttackType) = if (type == AttackType.MELEE)
        VorkathAttack.CLAW_SWIPE.distance
    else currentAttack?.distance ?: 0

    override fun skipNextCombatSequence() = stopAttacking
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun getAttackAnimation(type: AttackType?) = currentAttack?.getAttackAnimation(type) ?: Animation.DEFAULT_RESET_ANIMATION
    override fun getMaxHit(type: AttackType?): Int {
        val maxHit = currentAttack?.maxHit ?: -1
        return if (maxHit != -1)
            maxHit;
        else
            super.getMaxHit(type)
    }

    override fun fetchAttackDuration(type: AttackType?) = currentAttack?.fetchAttackDuration(type) ?: 0
    override fun fetchProjectiles(type: AttackType?) = currentAttack?.fetchProjectiles(type) ?: Stream.empty()
    override fun fetchHits(type: AttackType?) = currentAttack?.fetchHits(type) ?: Stream.empty()
    override fun fetchAttackGraphic(type: AttackType?) = currentAttack?.fetchAttackGraphic(type) ?: Optional.empty()
    override fun fetchTextAboveHead(type: AttackType?) = currentAttack?.fetchTextAboveHead(type) ?: Optional.empty()

    override fun onDeath() {
        super.onDeath()
        face = FacingDirection.SOUTH
    }
}