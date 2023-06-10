package com.grinder.game.entity.agent.npc.monster.boss.impl


import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.boss.impl.ScorpiaBoss
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class ScorpiaBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {
    override fun generateAttack(): BossAttack {
        return BossAttack(this)
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.RANGED)
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.RANGED) 2 else 1
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.RANGED) 10 else 2
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 4
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(attackAnim)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MELEE) Stream.empty() else ProjectileTemplate
            .builder(330)
            .setSourceSize(5)
            .setStartHeight(45)
            .setEndHeight(33)
            .setCurve(4)
            .setSpeed(30)
            .setDepartureSound(Sound(Sounds.MAGIC_DART_CAST))
            .setDelay(30)
            .buildAsStream()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return HitTemplate
            .builder(type)
            .setDelay(if (type == AttackType.RANGED) 2 else 0)
            .setSuccessOrFailedSound((if (type == AttackType.RANGED) Sound(Sounds.MAGIC_DART_CONTACT) else Sound(-1)))
            .setIgnorePrayer(Misc.random(5) == 1)
            .buildAsStream()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(33.33f)) Optional.of(CHAT_ABOVE_HEAD) else Optional.empty()
    }

    companion object {
        private const val CHAT_ABOVE_HEAD = "Scssss!"
    }

    init {
        race = MonsterRace.SCORPION
    }
}