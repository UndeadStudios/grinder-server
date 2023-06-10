package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.*
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.subscribe
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.time.SecondsTimer

/**
 * Represents a tortoise with riders.
 *
 * https://oldschool.runescape.wiki/w/Tortoise#With_riders
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/09/2020
 */
class TortoiseBoss(position: Position)
    : Boss(NpcID.TORTOISE_6076, position), AttackProvider {

    private lateinit var driver: GnomeDriver
    private lateinit var archer: GnomeArcher
    private lateinit var mage: GnomeMage

    init {
        onEvent {
            if(it == MonsterEvents.DYING){
                driver = GnomeDriver(this, getPositionOfGnome(MELEE))
                archer = GnomeArcher(this, getPositionOfGnome(RANGED))
                mage = GnomeMage(this, getPositionOfGnome(MAGIC))
                driver.spawn()
                archer.spawn()
                mage.spawn()
            }
        }
    }

    fun getPositionOfGnome(attackType: AttackType): Position {
        val face = face.direction
        val base = centerPosition.clone()
        return when(attackType){
            MELEE -> base.add(face.x, face.y + 1)
            RANGED -> base.add(face.x+1, face.y)
            MAGIC -> base.add(face.x-1, face.y)
            else -> base
        }
    }

    override fun generateAttack() = object : BossAttack(this) {
        override fun sequence(actor: Boss, target: Agent) {
            super.sequence(actor, target)
            if(type() != MELEE) {
                val template = ProjectileTemplate
                        .builder(10)
                        .setSourceOffset(3)
                        .setHeights(50, 34)
                        .setCurve(10)
                        .setDelay(20)
                        .setSpeed(50)
                        .build()

                val projectile = Projectile(getPositionOfGnome(type()), target, template)

                projectile.sendProjectile()
            }
        }
    }
    override fun attackTypes(): AttackTypeProvider {
        return equalChances(MELEE, MAGIC, RANGED)
    }

    override fun maxTargetsHitPerAttack(type: AttackType) = 1

    override fun attackRange(type: AttackType) = when(type) {
        MAGIC,
        RANGED -> 6
        else -> 1
    }

    override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(type)
            .buildAsStream()

    override fun getAttackAnimation(type: AttackType?) = when(type) {
        MELEE -> Animation(0)
        RANGED,
        MAGIC -> Animation(3954)
        else -> Animation(attackAnim)
    }

    override fun fetchAttackDuration(type: AttackType?) = 4

    class GnomeDriver(bossNPC: TortoiseBoss, position: Position)
        : GnomeMinion(bossNPC, NpcID.GNOME_DRIVER, position)

    class GnomeArcher(bossNPC: TortoiseBoss, position: Position)
        : GnomeMinion(bossNPC, NpcID.GNOME_ARCHER, position)

    class GnomeMage(bossNPC: TortoiseBoss, position: Position)
        : GnomeMinion(bossNPC, NpcID.GNOME_MAGE, position)

    /**
     * Represents a gnome riding on the tortoise.
     *
     * TODO: While in multi combat sometimes the gnomes don't attack the player or only attack one at a time.
     */
    open class GnomeMinion(bossNPC: TortoiseBoss, id: Int, position: Position)
        : BossMinion<TortoiseBoss>(bossNPC,
            id,
            position,
            BossMinionPolicy.NO_RESPAWN,
            BossMinionPolicy.ATTACK_PREFERRED_OPPONENT) {

        val despawnTimer = SecondsTimer()

        init {
            onEvent {
                when (it) {
                    MonsterEvents.ADDED -> {
                        despawnTimer.start(48)
                    }
                    MonsterEvents.PRE_SEQUENCE -> {
                        if(this != bossNPC.driver && bossNPC.driver.combat.isAttacking)
                            combat.isProcessAggression = AreaManager.inMulti(this)
                        else if(!combat.isProcessAggression)
                            combat.isProcessAggression = true
                    }
                    MonsterEvents.POST_SEQUENCE -> {
                        if(despawnTimer.finished()){
                            remove()
                        } else {
                            if(Misc.randomChance(20.0F)){
                                say("Dobbie")
                            }
                        }
                    }
                    else -> {}
                }
            }
            combat.subscribe {

                if(it is IncomingHitQueued)
                    it.hit.isRewardingExperience = false

                return@subscribe false
            }
        }
    }
}