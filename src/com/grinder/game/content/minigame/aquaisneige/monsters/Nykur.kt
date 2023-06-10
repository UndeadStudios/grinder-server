package com.grinder.game.content.minigame.aquaisneige.monsters

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.content.minigame.aquaisneige.monsters.Nykur
import com.grinder.game.entity.agent.Agent
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.stream.Stream

class Nykur(id: Int, position: Position?) : AquaisNeigeNpc(id, position!!), AttackProvider {
    override fun attackTypes(): AttackTypeProvider {
        return AttackType.MAGIC
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type).setDelay(3)
        builder.setSuccessOrFailedGraphic(Graphic(446, Priority.HIGHEST))
        //println("distance to target" + DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position))
        //println("projectile speed:" + if (asNpc.combat.target != null) (65 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(30)) else 65)
        return builder.buildAsStream()
    }

    override fun generateAttack(): BossAttack {
        val attack = AquaisNeigeAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MAGIC) 49 else super.getMaxHit(type)
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return ATTACK_ANIMATION
    }

    /* @Override
    public int attackRange(@NotNull AttackType type) {
        return 15;
    }*/
    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) {
            ProjectileTemplate
                .builder(445)
                .setSourceSize(2)
                .setSourceOffset(2)
                .setStartHeight(104)
                .setEndHeight(32)
                .setCurve(25)
                .setSpeed(if (asNpc.combat.target != null) (65 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 65)
                .setDelay(20)
                .buildAsStream()
        } else Stream.empty()
    }

    /*
    * Projectile have the following hit delay based on distance (squares)
     */
    private fun getProjectileHitDelay(attacker: Agent, target: Agent): Int { // Works well for 3 ticks fixed hit delay on any distance (based on 20 delay)
        return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
            // This code works perfect: .setSpeed(if (asNpc.combat.target != null) (65 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 65)
            in 0..4 -> 60
            in 5..6 -> 55
            in 7..8 -> 45
            in 9..11 -> 40
            in 12..14 -> 35
            in 15..16 -> 30
            else -> 25
        }
    }

    companion object {
        private val ATTACK_ANIMATION = Animation(6345, Priority.HIGHEST)
    }

    init {
        race = MonsterRace.HYDRA
    }
}