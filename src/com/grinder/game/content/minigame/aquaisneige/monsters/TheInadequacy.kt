package com.grinder.game.content.minigame.aquaisneige.monsters;

import com.grinder.game.World
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.aquaisneige.Constants
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.CombatEventListener
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.Cyrisus3462
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.Cyrisus3463
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.Cyrisus3464
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.areas.instanced.AquaisNeigeArea
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.stream.Stream


class TheInadequacy(position: Position?) : AquaisNeigeNpc(Constants.THE_INADEQUACY, position!!), AttackProvider {
    var minionsSpawned = false
    var channelling = false

    var minions = mutableListOf<AquaisNeigeNpc>()

    init {
        race = MonsterRace.HYDRA


        combat.subscribe(object : CombatEventListener {
            override fun on(event: CombatEvent): Boolean {
                return onCombatEvent(event)
            }
        })
        combat.onIncomingHitQueued {
            if (minionsSpawned) {
                multiplyDamage(0.50);
            }
        }
        combat.onOutgoingHitApplied {
            target.ifPlayer { target ->
                if (hitpoints <= 250 && !minionsSpawned) {
                    spawnMinions();
                }

                if (Misc.randomChance(10f) && !channelling) {
                    if (Misc.random(5) <= 3) {
                        spreadAttack(target)
                    } else {
                        earthquakeAttack(target);
                    }
                }
            }
        }
    }

    private fun onCombatEvent(event: CombatEvent): Boolean {
        if (event !is IncomingHitApplied) {
            return false
        }
        val hit = event.hit
        val details = hit.attackDetails
        if (details.used(RangedWeapon.TWISTED_BOW)) {
            hit.multiplyDamage(0.50)
        }
        return true
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC)
    }

    override fun fetchAttackDuration(type: AttackType) = 7

    override fun attackRange(type: AttackType) = when (type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    open fun spawnMinions() {
        val cave = area as AquaisNeigeArea

        minionsSpawned = true
        val minion1 = Cyrisus3462(Constants.INADEQUACY_MINION_1, Position(owner.x, owner.y, owner.z))
        minion1.owner = owner
        cave.add(minion1)
        minion1.spawn()

        val minion2 = Cyrisus3463(Constants.INADEQUACY_MINION_2, Position(owner.x, owner.y, owner.z))
        minion2.owner = owner
        cave.add(minion2)
        minion2.spawn()

        val minion3 = Cyrisus3464(Constants.INADEQUACY_MINION_3, Position(owner.x, owner.y, owner.z))
        minion3.owner = owner
        cave.add(minion3)
        minion3.spawn()

        minions.add(minion1)
        minions.add(minion2)
        minions.add(minion3)

        TaskManager.submit(object : Task(3) {
            override fun execute() {
                minion1.combat.target(owner)
                minion2.combat.target(owner)
                minion3.combat.target(owner)
                stop()
            }
        })
    }

    open fun spreadAttack(playerTarget: Player) {
        TaskManager.submit(object : Task(1, this, false) {
            var ticks = 0
            var throwIndex = 0

            var projectilePositions = mutableListOf<Position>()

            override fun execute() {
                if (ticks == 1) {
                    channelling = true
                    var npcPosition = position
                    motion.update(MovementStatus.DISABLED)

                    for (i in 0 until 30) {
                        projectilePositions.add(Position((npcPosition.x + Misc.random(-10, 10)), (npcPosition.y + Misc.random(-10, 10)), npcPosition.z))
                    }

                } else if (ticks > 2 && ticks < 15 && ticks % 3 == 0) {
                    World.spawn(TileGraphic(projectilePositions[throwIndex], Graphic(287, GraphicHeight.MIDDLE)))
                    World.spawn(TileGraphic(projectilePositions[throwIndex + 1], Graphic(287, GraphicHeight.MIDDLE)))

                    if (playerTarget.position == projectilePositions[throwIndex] || playerTarget.position == projectilePositions[throwIndex + 1]) {
                        playerTarget.performGraphic(Graphic(542))
                        playerTarget.combat.queue(Damage.create(15, 20))
                    }

                    throwIndex += 2
                } else if (ticks >= 20) {
                    motion.update(MovementStatus.MOVING)
                    channelling = false
                    stop()
                }
                ticks++
            }
        })
    }

    open fun earthquakeAttack(playerTarget: Player) {
        var channelDuration = 30;

        playerTarget.packetSender.sendCameraShake(3, 6, 3, 10)
        playerTarget.sendMessage("The Inadequacy is charging! Take cover under the waterfall to avoid the damage.")

        TaskManager.submit(object : Task(1, this, false) {
            var ticks = 0

            override fun execute() {
                if (ticks == 1) {
                    channelling = true
                    moveTo(Position(1312, 3103, position.z))
                    //motion.traceTo(Position(1312, 3103, position.z))
                    //motion.setCanReTrace(false)
                    //setEntityInteraction(playerTarget)
                    playerTarget.packetSender.sendPositionalHint(Position(1337, 3106, playerTarget.position.z), 10)
                    motion.update(MovementStatus.DISABLED)
                } else if (ticks < channelDuration && ticks % 4 == 0) {
                    if (playerTarget.position.x < 1339) {
                        playerTarget.performGraphic(Graphic(60))
                        playerTarget.combat.queue(Damage.create(5, 15))
                    }
                } else if (ticks >= channelDuration) {
                    playerTarget.packetSender.sendCameraNeutrality();
                    playerTarget.packetSender.sendEntityHintRemoval(false)

                    moveTo(Position(1321, 3101, position.z))
                    //motion.traceTo(Position(1320, 3101, position.z))
                    //motion.setCanReTrace(false)
                    //setEntityInteraction(playerTarget)


                    channelling = false
                    motion.update(MovementStatus.MOVING)
                    super.stop()
                }

                ticks++
            }
        })

    }

    override fun generateAttack(): BossAttack {
        val attack = AquaisNeigeAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.MAGIC) {
            Stream.empty()
        } else {
            var magicProjectile1 = ProjectileTemplateBuilder(448)
                    .setSourceSize(4)
                    .setSourceOffset(4)
                    .setStartHeight(124)
                    .setEndHeight(32)
                    .setCurve(16)
                    .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Fast
                    .setDelay(35)
                    .build();
            var magicProjectile2 = ProjectileTemplateBuilder(449)
                    .setSourceSize(4)
                    .setSourceOffset(4)
                    .setStartHeight(124)
                    .setEndHeight(32)
                    .setCurve(16)
                    .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Normal
                    .setDelay(40)
                    .build();
            var magicProjectile3 = ProjectileTemplateBuilder(450)
                    .setSourceSize(4)
                    .setSourceOffset(4)
                    .setStartHeight(124)
                    .setEndHeight(32)
                    .setCurve(16)
                    .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Slow
                    .setDelay(45)
                    .build();
            performGraphic(Graphic(447, 0, (height * 50) + 230));

            return Stream.of(magicProjectile1, magicProjectile2, magicProjectile3);
        }
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type).setDelay(if (type == AttackType.MELEE) 0 else 3)
        val player = owner.asPlayer
        when (type) {
            AttackType.MAGIC -> {
                builder.setSuccessOrFailedGraphic(Graphic(157, 25, GraphicHeight.HIGH, Priority.HIGH))
                player.playSound(Sound(1526, 20))
            }

            AttackType.MELEE -> {
                player.playSound(Sound(1525, 0))
            }
        }
        return builder.buildAsStream()
    }

}
