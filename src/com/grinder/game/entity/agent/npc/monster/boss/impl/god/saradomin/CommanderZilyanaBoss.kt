package com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin


import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.model.areas.godwars.GodChamber
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.CommanderZilyanaBoss
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
class CommanderZilyanaBoss(npcId: Int, position: Position?, inGodWars: Boolean) : God(npcId, position!!, inGodWars),
    AttackProvider {
    override fun chamber(): GodChamber {
        return GodChamber.SARADOMIN
    }

    override fun generateAttack(): BossAttack {
        return BossAttack(this)
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(Odds.ONE_THIRD, AttackType.MAGIC)
            .add(Odds.TWO_THIRD, AttackType.MELEE)
            .build()
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MAGIC) 2 else 1
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 2
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MAGIC) 4 else 1
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 27 else if (type == AttackType.MAGIC) 20 else super.getMaxHit(type)
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.MAGIC) MAGIC_ANIMATION else MELEE_ANIMATION
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) ProjectileTemplate.builder(1198)
            .setSourceOffset(1)
            .setStartHeight(43)
            .setEndHeight(31)
            .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 10)
            .setDelay(55)
            .buildAsStream() else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type)
        if (type == AttackType.MAGIC) {
            builder.setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)).setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .setSuccessOrFailedGraphic(Graphic(1196, GraphicHeight.MIDDLE))
        }
        return builder.setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH).buildAsStream()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(66.66f)) Optional.of(Misc.random(*CHATS_ABOVE_HEAD)) else Optional.empty()
    }

    companion object {
        private val CHATS_ABOVE_HEAD = arrayOf(
            "Death to the enemies of the light!", "Slay the evil ones!",
            "Saradomin lend me strength!", "By the power of Saradomin!", "May Saradomin be my sword!",
            "Good will always triumph!", "Forward! Our allies are with us!", "Saradomin is with us!",
            "In the name of Saradomin!", "All praise Saradomin!", "Attack! Find the Godsword!"
        )
        private val MELEE_ANIMATION = Animation(6967)
        private val MAGIC_ANIMATION = Animation(6970)
    }
}