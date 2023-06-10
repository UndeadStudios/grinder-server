package com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import java.util.*
import java.util.stream.Stream

class AvatarOfMagicBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    init {
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.MAGIC) {
                if (isAccurate && totalDamage > 0) {
                    target.combat.submit(FreezeEvent(3, false))
                    target.performGraphic(MAGIC_HIT_GRAPHIC)
                    target.ifPlayer { it.playSound(1404) }
                } else {
                    target.performGraphic(MAGIC_SPLASH_GFX)
                    target.ifPlayer { it.playSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes() = AttackType.MAGIC

    override fun attackRange(type: AttackType) = 7

    override fun fetchAttackDuration(type: AttackType) = 5

    public override fun maxTargetsHitPerAttack(type: AttackType) = 3

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(1844)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return Stream.of(MAGIC_PROJECTILE)
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return Stream.of(MAGIC_HIT)
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return Optional.of(Graphic(366))
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }



    companion object {
        private val MAGIC_HIT_GRAPHIC = Graphic(369, GraphicHeight.MIDDLE)
        private val MAGIC_SPLASH_GFX = Graphic(85, GraphicHeight.HIGH)
        private val MAGIC_HIT = HitTemplate.builder(AttackType.MAGIC).setDelay(2).build()
        private val MAGIC_PROJECTILE = ProjectileTemplate.builder(362)
            .setSourceOffset(2)
                .setStartHeight(20)
                .setEndHeight(20)
                .setSpeed(15)
                .setDelay(35)
                .build()
    }

    init {
        race = MonsterRace.ARZINIAN
    }
}