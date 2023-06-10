package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak

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
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import java.util.*
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Zakl%27n_Gritch
 *
 * "Zakl'n Gritch is one of K'ril Tsutsaroth's bodyguards,
 * along with Balfrug Kreeyath and Tstanon Karlak.
 * Zakl'n Gritch attacks with Ranged and has a maximum hit of 21.
 * His fearsome appearance and power over ice have earned him the nickname "Scourge of the Light",
 * being a prominent soldier in Zamorak's army.
 * He attacks by throwing large snowballs."
 *
 * @see TstanonKarlak
 * @see BalfrugKreeyath
 * @see KrilTsutsarothBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class ZaklnGritch(bossNPC: KrilTsutsarothBoss): GodMinion<KrilTsutsarothBoss>
(
        bossNPC,
        NpcID.ZAKLN_GRITCH,
        Position(2921, 5323, bossNPC.position.z)
) {

    init {
        race = MonsterRace.DEMON
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<ZaklnGritch>(provider) {
        override fun requiredDistance(actor: Agent) = 8
        override fun type() = AttackType.RANGED
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7077)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1223)
                .setSourceOffset(2)
                .setStartHeight(22)
                .setEndHeight(30)
                .setCurve(16)
                .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(30)) else 10)
                .setDelay(39)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                .buildAsStream()
        override fun fetchAttackGraphic(type: AttackType?) = Optional
                .of(Graphic(1222, GraphicHeight.LOW))
    }
}