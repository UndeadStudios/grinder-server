package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak

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
 * https://oldschool.runescape.wiki/w/Balfrug_Kreeyath
 *
 * "Balfrug Kreeyath is a three-faced black demon who destroyed the desert city of Ullek during the God Wars.
 * He is one of K'ril Tsutsaroth's bodyguards, along with Zakl'n Gritch and Tstanon Karlak.
 * He attacks with Magic by launching huge fireballs at the player and has a maximum hit of 16."
 *
 * @see ZaklnGritch
 * @see TstanonKarlak
 * @see KrilTsutsarothBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class BalfrugKreeyath(bossNPC: KrilTsutsarothBoss): GodMinion<KrilTsutsarothBoss>
(
        bossNPC,
        NpcID.BALFRUG_KREEYATH,
        Position(2933, 5323, bossNPC.position.z)
) {

    init {
        race = MonsterRace.DEMON
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<BalfrugKreeyath>(provider) {
        override fun requiredDistance(actor: Agent) = 8
        override fun type() = AttackType.MAGIC
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7077)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = Stream.of<ProjectileTemplate>(object : ProjectileTemplate {
            override fun sourceSize() = 3
            override fun sourceOffset() = 1
            override fun projectileId() = 1227
            override fun startHeight() = 20
            override fun endHeight() = 30
            override fun curve() = 15
            override fun lifetime() = if (asNpc.combat.target != null) (18 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 18
            override fun delay() = 40
        })
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .setSuccessOrFailedGraphic(Graphic(1218, GraphicHeight.HIGH, Priority.HIGH))
                .buildAsStream()

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(Graphic(1216, GraphicHeight.MIDDLE, Priority.HIGH))
    }
}