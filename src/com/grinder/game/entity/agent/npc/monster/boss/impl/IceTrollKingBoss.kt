package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackMode
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy.*
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.RegularProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 *          Jack Barnett      (https://www.rune-server.ee/members/raccas/)
 * @since   29/09/2019
 * @version 1.0
 */
class IceTrollKingBoss(npcId: Int, position : Position) : Boss(npcId, position), AttackProvider {

    companion object {
        private val SHOUTS = arrayOf("Be gone from my land!", "Vulgar creatures!", "You are not welcome here!")
        private val RUNT_SPAWN = Position(2856, 3810, 1)
        private val PROJECTILE = RegularProjectileTemplate(368, 2, 10, 32, 280, 0, 0, 20)
    }

    init {
        race = MonsterRace.TROLL
        combat.onIncomingHitApplied {
            if(!spawnedMinions && hitpoints < 190)
                spawnRunts()
        }
    }

    private var aliveMinions = 0

    private var spawnedMinions = false

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    private fun spawnRunts() {

        say("Argghhhh! Help your king now!")

        spawnedMinions = true
        aliveMinions = 2

        Stream.of(
                IceTrollRunt(this),
                IceTrollRunt(this)
        ).forEach { it.spawn() }
    }

    override fun attackTypes(): AttackTypeProvider = AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC)

    override fun immuneToAttack(type: AttackType) = Pair(aliveMinions > 0, "You must kill all the runts before attacking the king!")

    override fun skipProjectileClipping() = false

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MAGIC -> Optional.of(Sound(Sounds.SMOKE_BLITZ_CAST))
            else -> Optional.of(Sound(Sounds.MACE_ATTACK_SOUND))
        }
    }

    override fun useSmartPathfinding() = true

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = when(type) {
        AttackType.MAGIC -> Stream.of(PROJECTILE)
        else -> Stream.empty()
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12 //8
    }

    override fun maxTargetsHitPerAttack(type: AttackType) = 5

    override fun fetchAttackDuration(type: AttackType?) = 3

    override fun getAttackAnimation(type: AttackType?): Animation {
        return when(type) {
            AttackType.MELEE -> Animation(284)
            else ->  Animation(285)
        }
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> {
        return when (type) {
            AttackType.MAGIC -> {
                HitTemplate
                    .builder(AttackType.MAGIC).setDelay(1)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                    .buildAsStream()
            }
            else -> {
                HitTemplate
                    .builder(AttackType.MELEE)
                    .setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                    .buildAsStream()
            }
        }
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(15F)) {
            Optional.ofNullable(SHOUTS.random())
        } else {
            Optional.empty()
        }
    }

    class IceWarrior(boss: IceTrollKingBoss, position: Position) : BossMinion<IceTrollKingBoss>(
        boss,
        NpcID.ICE_WARRIOR,
        position,
        NO_RESPAWN,
        REMOVE_WHEN_BOSS_REMOVED,
        ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class IceSpider(boss: IceTrollKingBoss, position: Position) : BossMinion<IceTrollKingBoss>(
        boss,
        NpcID.ICE_SPIDER,
        position,
        NO_RESPAWN,
        REMOVE_WHEN_BOSS_REMOVED,
        ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class IceWolf(boss: IceTrollKingBoss, position: Position) : BossMinion<IceTrollKingBoss>(
        boss,
        NpcID.ICE_WOLF_646,
        position,
        NO_RESPAWN,
        REMOVE_WHEN_BOSS_REMOVED,
        ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }


    class IceTrollRunt(bossNPC: IceTrollKingBoss)
        : BossMinion<IceTrollKingBoss>(bossNPC, NpcID.ICE_TROLL_RUNT_5823,
            RUNT_SPAWN.copy().setZ(bossNPC.position.z).randomize(2),
            REMOVE_WHEN_BOSS_REMOVED,
            ATTACK_PREFERRED_OPPONENT,
            NO_RESPAWN
    ) {

        init {
            onEvent {
                if(it == MonsterEvents.ADDED){
                    combat.setAutoRetaliate(true)
                    say("We will die for our king!")
                } else if(it == MonsterEvents.REMOVED){
                    bossNPC.aliveMinions--
                    if(bossNPC.aliveMinions == 0){
                        bossNPC.say("NOOOOO! You will pay for this!")
                    }
                }
            }
        }
    }
}