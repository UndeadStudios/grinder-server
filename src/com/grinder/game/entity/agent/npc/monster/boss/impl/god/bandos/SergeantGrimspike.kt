package com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Sergeant_Grimspike
 *
 * "Sergeant Grimspike is one of three sergeants found guarding General Graardor
 * in the Bandos section of the God Wars Dungeon,
 * alongside Sergeant Strongstack and Sergeant Steelwill.
 * Sergeant Grimspike uses rather accurate ranged attacks with a max hit of 21 Hitpoints."
 *
 * TODO: find projectile id
 *
 * @see SergeantStrongstack
 * @see SergeantSteelwill
 * @see GeneralGraardorBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class SergeantGrimspike(bossNPC: GeneralGraardorBoss): GodMinion<GeneralGraardorBoss>
(
        bossNPC,
        NpcID.SERGEANT_GRIMSPIKE,
        Position(2873, 5355, bossNPC.position.z)
) {

    init {
        race = MonsterRace.GOBLIN
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<SergeantGrimspike>(provider) {
        override fun requiredDistance(actor: Agent) = 7
        override fun type() = AttackType.RANGED
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7073)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1220)
                .setSourceOffset(1)
                .setStartHeight(40)
                .setEndHeight(30)
                .setCurve(10)
                .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(30)) else 10)
                .setDelay(27)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.RANGED)
                .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                .buildAsStream()
    }
}