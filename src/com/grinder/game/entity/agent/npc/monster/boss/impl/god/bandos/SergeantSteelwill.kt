package com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos

import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Sergeant_Steelwill
 *
 * "Sergeant Steelwill is one of the three sergeants found guarding General Graardor
 * in the Bandos section of the God Wars Dungeon,
 * along with Sergeant Strongstack and Sergeant Grimspike.
 * Sergeant Steelwill uses a magic attack with a max hit of up to 16 hitpoints."
 *
 * @see SergeantGrimspike
 * @see SergeantStrongstack
 * @see GeneralGraardorBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class SergeantSteelwill(bossNPC: GeneralGraardorBoss): GodMinion<GeneralGraardorBoss>
(
        bossNPC,
        NpcID.SERGEANT_STEELWILL,
        Position(2868, 5357, bossNPC.position.z)
) {

    init {
        race = MonsterRace.GOBLIN
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<SergeantSteelwill>(provider) {
        override fun requiredDistance(actor: Agent) = 8
        override fun type() = AttackType.MAGIC
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7071)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1217)
                .setSourceOffset(1)
                .setStartHeight(30)
                .setEndHeight(30)
                .setCurve(0)
                .setSpeed(if (asNpc.combat.target != null) (25 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 25)
                .setDelay(35)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MAGIC)
                  .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .setSuccessOrFailedGraphic(Graphic(1218, GraphicHeight.HIGH, Priority.HIGH))
                .buildAsStream()
        override fun fetchAttackGraphic(type: AttackType?) = Optional
                .of(Graphic(1216, GraphicHeight.MIDDLE, Priority.HIGH))
    }
}