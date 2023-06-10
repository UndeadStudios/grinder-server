package com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import java.util.stream.Stream

class AvatarOfRangingBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes() = AttackType.RANGED

    override fun attackRange(type: AttackType) = 7

    override fun fetchAttackDuration(type: AttackType) = 5

    public override fun maxTargetsHitPerAttack(type: AttackType) = 3

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(1843)
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return ProjectileTemplate.builder(995)
                .setStartHeight(45)
                .setSourceOffset(2)
                .setEndHeight(40)
                .setSpeed(10)
                .setDelay(30)
                .buildAsStream()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return HitTemplate.builder(AttackType.RANGED).setDelay(1).buildAsStream()
    }

    init {
        race = MonsterRace.ARZINIAN
    }
}