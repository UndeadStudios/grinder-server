package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World.spawn
import com.grinder.game.content.pvm.MonsterKillTracker
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.event.impl.DragonFireEvent
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.NPCDropGenerator
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros.Nex
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.areas.godwars.NexChamber
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy
import java.util.stream.Stream


class MerodachBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {
    @Volatile
    private var fireType = FireType.DRAGON
    private var attackSpeed: Int = 0
    var skipCheck: Boolean = false
    override fun generateAttack(): BossAttack {
        val attack: BossAttack = object : BossAttack(this) {
            override fun postHitAction(actor: Boss, target: Agent) {
                if (type() == AttackType.SPECIAL) {
                    target.combat.submit(DragonFireEvent())
                }
            }

            override fun createHits(actor: Boss, target: Agent): Array<Hit> {
                val hits = super.createHits(actor, target)
                if (type() == AttackType.SPECIAL) {
                    if (Misc.random(4) == 1) {
                        skipCheck = !skipCheck
                        attackSpeed = 1;
                    } else {
                        if (!skipCheck) {
                            attackSpeed = 6;
                        }
                        skipCheck = false
                    }
                }
                if (type() == AttackType.SPECIAL) {
                    target.ifPlayer { player: Player ->
                        val antiFirePotion = !player.combat.fireImmunityTimer.finished() || !player.combat.superFireImmunityTimer.finished()
                        val antiFireShield = EquipmentUtil.isWearingDragonFireProtection(player)
                        val protectFromMagic = PrayerHandler.isActivated(player, PrayerHandler.PROTECT_FROM_MAGIC);
                        for (hit in hits) {
                            if (hit.attackType == AttackType.MELEE) continue
                            var updatedDamage = hit.totalDamage
                            if (fireType == FireType.DRAGON) {
                                if (antiFirePotion) updatedDamage -= 25
                                if (antiFireShield) updatedDamage -= 25
                                if (protectFromMagic) updatedDamage -= 25
                            } else {
                                if (antiFirePotion) updatedDamage -= 25
                                if (antiFireShield) updatedDamage *= 0.50.toInt()
                                //if(protectFromMagic) updatedDamage -= 5;
                                if (Misc.randomChance(10f)) {
                                    delayBy(1) {
                                        when (fireType) {
                                            FireType.ICE -> player.combat.submit(
                                                FreezeEvent(
                                                    11,
                                                    false
                                                )
                                            )
                                            FireType.POISON -> PoisonEffect.applyPoisonTo(
                                                player,
                                                PoisonType.SUPER
                                            )
                                            FireType.SHOCK -> for (combatSkill in Skill.COMBAT_SKILLS) player.skillManager.decreaseLevelTemporarily(
                                                combatSkill,
                                                10,
                                                0
                                            )
                                        }
                                    }
                                }
                            }
                            if (updatedDamage > 0) {
                                delayBy(1) {
                                    player.sendMessage("The dragon fire burns you!")
                                }
                                    //player.packetSender.sendSound(3750)
                                    hit.totalDamage = updatedDamage
                                    if (fireType == FireType.ICE) {
                                        hit.totalDamage += Misc.random(25);
                                    }
                            } else {
                                hit.totalDamage = 0
                                if (fireType == FireType.ICE) {
                                    hit.totalDamage = Misc.random(25);
                                }
                            }
                        }
                    }
                }
                return hits
            }
        }
        attack.setType(AttackType.SPECIAL)
        return attack
    }

    override fun appendDeath() {
        super.appendDeath()
        TaskManager.submit(object : Task(1, false) {
            var ticks = 0
            override fun execute() {

                if (ticks >= 4) {
                    stop()
                    val killer = combat.findKiller(false)
                    playerStream(15).filter { p: Player? ->
                        (p != null && p.area === area && (killer.isEmpty || p !== killer.get())
                                && combat.hasDoneDamage(p, 50))
                    }.forEach { p: Player ->
                        NPCDropGenerator.start(p, asNpc)
                        MonsterKillTracker.track(p, asNpc)
                        //PlayerUtil.broadcastMessage("<img=789> @whi@" + PlayerUtil.getImages(p) + "" + p.username + " has just defeated Merodach!")
                    }
                    stop()
                }
            }
        })
    }

    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER

    override fun randomizeAttack() {
        super.randomizeAttack()
        fireType =
            if (Misc.randomChance(40.0f)) FireType.DRAGON else if (Misc.randomChance(20.0f)) FireType.SHOCK else if (Misc.randomChance(
                    20.0f
                )
            ) FireType.POISON else FireType.ICE
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(Odds.ONE_THIRD, AttackType.SPECIAL)
            .add(Odds.TWO_THIRD, AttackType.MELEE)
            .build()
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    // Makes it so the NPC won't try to get close to you if it is using an attack with range.
    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 35
    }

    override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 8
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return attackSpeed
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.MELEE) MELEE_ANIMATION else MAGIC_ANIMATION
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MELEE) Stream.empty() else ProjectileTemplate
            .builder(fireType.projectileId)
            .setSourceSize(size)
            .setSourceOffset(size)
            .setStartHeight(31)
            .setEndHeight(32)
            .setCurve(280)
            .setSpeed(if (asNpc.combat.target != null) ((DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position)).coerceAtMost(50)) else 10)
            .setDelay(50)
            .buildAsStream()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return HitTemplate
            .builder(type)
            .setIgnoreAttackStats(type == AttackType.SPECIAL)
            .setDelay(if (type == AttackType.MELEE) 0 else if (asNpc.combat.target == null) 0 else 2)
            .buildAsStream()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 44 else 65
    }

    private enum class FireType(val projectileId: Int) {
        ICE(396), POISON(394), SHOCK(395), DRAGON(393);
    }

    companion object {
        private val MELEE_ANIMATION = Animation(91)
        private val MAGIC_ANIMATION = Animation(84)
    }

    init {
        race = MonsterRace.DRAGON
        size = 5
    }
}