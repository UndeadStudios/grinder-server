package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*
import java.util.stream.Stream


class KamilBossSpirit(npcId: Int, position: Position) : Boss(npcId, position), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        movementCoordinator.radius = 4
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.MAGIC) {
                if (isAccurate && totalDamage > 0) {
                    target.ifPlayer { it.playSound(Sounds.ICE_BARRAGE_CONTACT) }
                } else {
                    target.ifPlayer { it.playSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes() = AttackType.builder()
            .add(AttackType.Builder.Odds.ONE_THIRD, AttackType.MAGIC)
            .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.MELEE)
            .build()!!

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MAGIC) 6 else 1
    }

    override fun fetchAttackDuration(type: AttackType?) = 4

    override fun getAttackAnimation(type: AttackType?) = Animation(when (type) {
        AttackType.MAGIC -> 1979
        else -> attackAnim
    })

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {

        return when(type) {
            AttackType.MAGIC -> Optional.of(Sound(171))
            else -> Optional.of(Sound(2500))
        }
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.MAGIC -> HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(2)
            .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
            .also {
                if(type == AttackType.MAGIC){
                    it.setSuccessGraphic(Graphic(369))
                    it.setFailedGraphic(Graphic(85, GraphicHeight.HIGH))
                    it.setOnSuccessKt { target ->
                        target.combat.submit(FreezeEvent(15, false))
                    }
                }
            }
            .buildAsStream()
        AttackType.MELEE ->
            HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                .buildAsStream()
        else -> Stream.empty()
    }

    override fun fetchTextAboveHead(type: AttackType?) = when (type) {
        AttackType.MAGIC -> Optional.of("Sallamakar Ro!")
        else -> Optional.empty()
    }

    companion object {

        private val minionNpcIds = intArrayOf(648, 649, 650, 651, 652, 653, 654, 710, 711)

        /**
         * Check if the [id] is in [minionNpcIds].
         */
        fun isKamilMinion(id: Int) = minionNpcIds.contains(id)
    }
}