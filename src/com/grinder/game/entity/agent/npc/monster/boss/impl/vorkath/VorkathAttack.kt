package com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.task.QuickfireTask
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.task.ZombifiedTask
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.util.timing.TimerKey
import java.util.stream.Stream

/**
 * Represents various [attack providers][AttackProvider] for [vorkath][VorkathBoss]'s combat.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/10/2019
 * @version 1.0
 */
enum class VorkathAttack(val maxHit: Int = -1, val distance: Int) : AttackProvider {

    CLAW_SWIPE(maxHit = 32, distance = 2) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7951)
        override fun fetchHits(type: AttackType?): Stream<HitTemplate>  = HitTemplate
                .builder(AttackType.MELEE)
                .buildAsStream()
    },
    MAGIC(maxHit = 30, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7952)
        override fun fetchHits(type: AttackType?): Stream<HitTemplate>  = HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(3)
                .setSuccessOrFailedGraphic(Graphic(1480, GraphicHeight.MIDDLE))
                //.setSuccessSound(Sound(222))
                .buildAsStream()
        override fun fetchProjectiles(type: AttackType?) = ProjectileTemplate
                .builder(1479)
                .setSourceSize(6)
                .setSourceOffset(1)
                .setStartHeight(35)
                .setEndHeight(28)
                .setCurve(1)
                .setSpeed(28)
                //.setArrivalSound(Sound(223))
                //.setDepartureSound(Sound(222))
                .setDelay(33)
                .buildAsStream()
    },
    RANGED(maxHit = 32, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) =  Animation(7952)
        override fun fetchHits(type: AttackType?): Stream<HitTemplate>  = HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(3)
                .setSuccessOrFailedGraphic(Graphic(1478, GraphicHeight.MIDDLE))
                //.setSuccessSound(Sound(1965))
                .buildAsStream()
        override fun fetchProjectiles(type: AttackType?) = ProjectileTemplate
                .builder(1477)
                .setSourceSize(5)
                .setSourceOffset(1)
                .setStartHeight(35)
                .setEndHeight(31)
                .setCurve(1)
                .setSpeed(38)
                //.setArrivalSound(Sound(3403))
                //.setDepartureSound(Sound(1965))
                .setDelay(28)
                .buildAsStream()
    },
    NORMAL_DRAGON_FIRE(maxHit = 73, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7952)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {
            val projectile = Projectile(vorkath, target, createDragonFireProjectile(393))
            projectile.sendProjectile()
            projectile.onArrival {
                target.ifPlayer { player ->
                    createDragonFireDamage(player).let {
                        if (target!!.isUntargetable || target!!.isTeleporting)
                            return@ifPlayer
                        player.performGraphic(Graphic(1466, GraphicHeight.MIDDLE))
                        player.packetSender.sendSound(3750)
                        player.combat.queue(it)
                    }
                }
            }
        }
    },
    VENOM_DRAGON_FIRE(maxHit = 73, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7952)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {
            val projectile = Projectile(vorkath, target, createDragonFireProjectile(1470))
            projectile.sendProjectile()
            projectile.onArrival {
                target.ifPlayer { player ->
                    createDragonFireDamage(player).let {
                        if (target!!.isUntargetable || target!!.isTeleporting)
                            return@ifPlayer
                        if(it.value > 0)
                            PoisonEffect.applyPoisonTo(player, PoisonType.VENOM)
                        player.performGraphic(Graphic(1472, GraphicHeight.MIDDLE))
                        player.packetSender.sendSound(129)
                        player.combat.queue(it)
                    }
                }
            }
        }
    },
    PINK_DRAGON_FIRE(maxHit = 73, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7952)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {
            val projectile = Projectile(vorkath, target, createDragonFireProjectile(1471))
            projectile.sendProjectile()
            projectile.onArrival {
                target.ifPlayer { player ->
                    createDragonFireDamage(player).let {
                        if (target!!.isUntargetable || target!!.isTeleporting)
                            return@ifPlayer
                        //if(it.value > 0){
                            PrayerHandler.deactivatePrayers(player)
                            player.sendMessage("Your prayers have been disabled!")
                        //}
                        player.performGraphic(Graphic(1473, GraphicHeight.MIDDLE))
                        player.packetSender.sendSound(223)
                        player.combat.queue(it)
                    }
                }
            }
        }
    },
    DRAGON_FIRE_HIGH_DAMAGE(maxHit = 121, distance = 20) {
        override fun getAttackAnimation(type: AttackType?) = Animation(7960)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {

            val tile = target.position.clone()
            val template = createDragonFireHeavyProjectile()
            val projectile = Projectile(vorkath.centerPosition, tile, template)

            projectile.sendProjectile()
            projectile.onArrival {
                target.ifPlayer {player ->
                    if (player!!.isUntargetable || player!!.isTeleporting)
                        return@ifPlayer
                    player.packetSender.sendAreaSound(tile, 163, 5, 1, 0)
                    player.packetSender.sendGraphic(Graphic(1466, GraphicHeight.LOW), tile)
                    if(player.position.sameAs(tile))
                        player.combat.queue(Damage.create(0, 115)).also {
                            player.sendMessage("You are severely burnt by the dragon fire!")
                        }
                    else if(player.position.isWithinDistance(tile, 1))
                        player.combat.queue(Damage.create(0, 57)).also {
                            player.sendMessage("You are badly burnt by the dragon fire!")
                        }
                }
            }
        }
    },
    POISON_POOL_QUICKFIRE_BARRAGE(distance = 20) {
        override fun fetchAttackDuration(type: AttackType?) = 31
        override fun getAttackAnimation(type: AttackType?) = Animation(7957)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {
            target.asPlayer.packetSender.sendSound(586)
            val task = QuickfireTask(vorkath)
            task.bind(vorkath)
            TaskManager.submit(task)
        }
    },
    ZOMBIFIED_ICE_DRAGON_FIRE(distance = 20) {
        override fun fetchAttackDuration(type: AttackType?) = 10
        override fun getAttackAnimation(type: AttackType?) = Animation(7952)
        override fun sequence(vorkath: VorkathBoss, target: Agent) {
            val iceProjectile = Projectile(vorkath, target, createDragonFireProjectile(395))
            iceProjectile.sendProjectile()
            iceProjectile.onArrival {
                target.timerRepository.register(TimerKey.FREEZE, 1_000)
                target.performGraphic(Graphic(369))
                target.asPlayer.packetSender.sendSound(168, 90)
                if (target is Player) {
                    val task = ZombifiedTask(vorkath, target)
                    task.bind(vorkath)
                    TaskManager.submit(task)
                }
            }
        }
    };

    open fun sequence(vorkath: VorkathBoss, target: Agent) {}

    override fun fetchAttackDuration(type: AttackType?) = 5

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = Stream.empty<ProjectileTemplate>()
    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = Stream.empty<HitTemplate>()

    internal fun createDragonFireDamage(player: Player) : Damage {
        var damageAmount = 30
        if (player.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC))
            damageAmount -= 10
        if (EquipmentUtil.isWearingDragonFireProtection(player))
            damageAmount -= 10
        if (!player.combat.fireImmunityTimer.finished())
            damageAmount -= 10
        if (!player.combat.superFireImmunityTimer.finished())
            damageAmount -= 20
        return when(damageAmount) {
            0 -> {
                player.sendMessage("You are protected from the dragon breath!")
                Damage.createBlockedHit()
            }
            10 -> {
                player.sendMessage("You are a little burnt by the dragon fire!")
                Damage.create(0, 10)
            }
            20 -> {
                player.sendMessage("You are a medium well burnt by the dragon fire!")
                Damage.create(0, 25)
            }
            else -> {
                player.sendMessage("You are badly burnt by the dragon fire!")
                Damage.create(0, 73)
            }
        }
    }

    fun createDragonFireProjectile(projectileId: Int) = ProjectileTemplate
            .builder(projectileId)
            .setSourceSize(5)
            .setSourceOffset(1)
            .setHeights(35, 28)
            .setCurve(0)
            .setSpeed(20)
            //.setDepartureSound(Sound(586))
            .setDelay(38)
            .build()

    fun createDragonFireHeavyProjectile() = ProjectileTemplate
            .builder(1481)
            .setSourceSize(5)
            .setSourceOffset(1)
            .setHeights(60, 0)
            .setCurve(45)
            .setSpeed(58)
            .setDepartureSound(Sound(1519, 10))
            .setDelay(40)
            .build()
}