package com.grinder.game.content.minigame.aquaisneige.monsters

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.*
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

class HydroWarrior(npcId: Int, position: Position?) : AquaisNeigeNpc(npcId, position!!), AttackProvider {

    override fun attackTypes() = AttackType.builder()
        .add(AttackType.Builder.Odds.ONE_THIRD, AttackType.MAGIC)
        .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.MELEE)
        .build()!!

    override fun getAttackAnimation(type: AttackType?) = when (type) {
        AttackType.MAGIC -> MAGIC_ANIM
        else -> MELEE_ANIM
    }

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {

        return when(type) {
            AttackType.MAGIC -> Optional.of(Sound(171))
            else -> Optional.of(Sound(2500))
        }
    }

    override fun attackRange(type: AttackType) = 1

    override fun generateAttack() = BossAttack(this)

    override fun fetchHits(type: AttackType?) = HitTemplate
        .builder(type)
        .setDelay(2)
        .also {
            if(type == AttackType.MAGIC){
                it.setSuccessGraphic(Graphic(363))
                it.setFailedGraphic(Graphic(85, GraphicHeight.HIGH))
                it.setOnSuccessKt { target ->
                    target.combat.submit(FreezeEvent(3, false))
                }
            } else
                return HitTemplate.builder(type).setDelay(0).buildAsStream()
        }
        .buildAsStream()

    /*override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.MAGIC) {
            Stream.empty()
        } else {
            var projectile = ProjectileTemplateBuilder(362)
                    .setSourceSize(1)
                    .setSourceOffset(2)
                    .setHeights(5, 0)
                    .setCurve(0)
                    .setSpeed(80)
                    .setDelay(60)
                    .build()

            return Stream.of(projectile);
        }
    }*/

    init {
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.MAGIC) {
                if (isAccurate && totalDamage > 0) {
                    target.ifPlayer { it.playSound(Sounds.ICE_BURST_CONTACT) }
                } else {
                    target.ifPlayer { it.playSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    companion object {
        val MELEE_ANIM = Animation(1979, Priority.HIGHEST)
        val MAGIC_ANIM = Animation(391, Priority.HIGHEST)
    }
}