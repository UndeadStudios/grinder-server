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
import com.grinder.util.NpcID
import java.util.stream.Stream

/**
 * TODO: Implement
 */
class AvatarOfMeleeBoss(position: Position)
    : Boss(NpcID.ARZINIAN_AVATAR_OF_STRENGTH, position), AttackProvider {

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes() = AttackType.MELEE

    override fun attackRange(type: AttackType) = 1

    override fun fetchAttackDuration(type: AttackType) = 6

    public override fun maxTargetsHitPerAttack(type: AttackType) = 1

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(1840)
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return Stream.empty()
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    init {
        race = MonsterRace.ARZINIAN
    }
}