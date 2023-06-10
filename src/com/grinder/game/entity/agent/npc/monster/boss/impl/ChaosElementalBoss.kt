package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.BossAttackUtil
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.Area
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class ChaosElementalBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    private var special: Special? = null

    init {
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                if (Misc.randomChance(33.3f)) {
                    when (special) {
                        Special.DISARM -> BossAttackUtil.disarm(it)
                        Special.TELEPORT -> {
                            BossAttackUtil.teleport(it, Area(4).getAbsolute(it.position))
                            it.message("The Chaos elemental has teleported you.")
                        }
                    }
                }
            }
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun randomizeAttack() {
        super.randomizeAttack()
        special = if (bossAttack.type() == AttackType.SPECIAL)
            Misc.random(Special.DISARM, Special.TELEPORT)
        else
            null
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
                .add(Odds.TWO_FIFTH, AttackType.MELEE, AttackType.MAGIC, AttackType.RANGED)
                .add(Odds.ONE_THIRD, AttackType.SPECIAL)
                .build()
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (special == Special.DISARM) 4 else if (special == Special.TELEPORT) 2 else 8
    }

    override fun attackRange(type: AttackType) = 15

    override fun fetchAttackDuration(type: AttackType) = 5

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(attackAnim)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return ProjectileTemplate.builder(if (type == AttackType.SPECIAL) special!!.projectileId else 558)
                .setSourceSize(5)
                .setStartHeight(45)
                .setEndHeight(33)
                .setCurve(4)
                .setSpeed(10 + (DistanceUtil.getChebyshevDistance(asNpc!!.position, asNpc!!.combat!!.target!!.position ) * 2))
                .setDelay(30)
                .buildAsStream()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate
                .builder(if (type == AttackType.SPECIAL)
                    Misc.random(AttackType.MAGIC, AttackType.MELEE, AttackType.RANGED)
                else
                    type)
                .setDelay(2)
        if (type == AttackType.SPECIAL) {
            builder.setOnSuccessOrFailed(special!!.onHit)
            builder.setSuccessOrFailedGraphic(Graphic(special!!.graphicId))
        }
        return builder.buildAsStream()
    }

    private enum class Special(val projectileId: Int, val graphicId: Int, val onHit: Consumer<Agent>) {
        DISARM(551, 868, Consumer<Agent> { target: Agent -> target.ifPlayer { player: Player? -> BossAttackUtil.disarm(player) } }), TELEPORT(554, 867, Consumer<Agent> { target: Agent ->
            target.ifPlayer {
                BossAttackUtil.teleport(it, Area(4).getAbsolute(it.position))
                it.message("The Chaos elemental has teleported you.")
                it.playAreaSound(346, 10, 1, 0)
            }
        });
    }
}