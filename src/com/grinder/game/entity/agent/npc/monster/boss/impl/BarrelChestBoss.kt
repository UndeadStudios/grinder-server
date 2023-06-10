package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.decreaseLevel
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream
import kotlin.math.floor

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-13
 */
class BarrelChestBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    init {
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                if (attackType == AttackType.SPECIAL) {
                    if (Misc.randomChance(33.3f)) {
                        val prayerDrainAmount = Misc.getRandomInclusive(8) + this.totalDamage
                        val tempPrayerLevel = floor(it.getLevel(Skill.PRAYER).toDouble()).toInt() - prayerDrainAmount
                        it.decreaseLevel(Skill.PRAYER, tempPrayerLevel)
                        it.message("You feel a shock from the barrelchest slump!")
                    }
                }
            }
        }
    }

    override fun generateAttack() = BossAttack(this)

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 25 else super.getMaxHit(type)
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
                .add(AttackType.Builder.Odds.ONE_THIRD, AttackType.SPECIAL)
                .add(AttackType.Builder.Odds.TWO_THIRD, AttackType.MELEE)
                .build()
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 10 else 3
    }

    override fun fetchAttackDuration(type: AttackType) = 5

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.SPECIAL) 4 else 1
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.SPECIAL) EARTHQUAKE_ANIMATION else MELEE_ANIMATION
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return HitTemplate.builder(AttackType.MELEE).setIgnorePrayer(true).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .setDelay(if (type == AttackType.SPECIAL) 2 else 1)
                .buildAsStream()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(33.33f)) Optional.of(Misc.random(*CHATS_ABOVE_HEAD)) else Optional.empty()
    }

    companion object {
        private val CHATS_ABOVE_HEAD = arrayOf(
                "You've made a great mistake!", "Leave while you can! HAHAHAH", "No mercy!"
        )
        private val MELEE_ANIMATION = Animation(5894)
        private val EARTHQUAKE_ANIMATION = Animation(5895)
    }
}