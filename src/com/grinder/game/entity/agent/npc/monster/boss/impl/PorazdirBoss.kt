package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World.spawn
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.CombatEventListener
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.event.impl.OutgoingHitApplied
import com.grinder.game.entity.agent.combat.event.impl.OutgoingHitQueued
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.timing.TimerKey
import java.util.*
import java.util.stream.Stream


class PorazdirBoss(id: Int, position: Position) : Boss(id, position), AttackProvider {

    private var spell: Spell? = null
    private var previousSpell: Spell? = null;
    private var attackType: Int = 0;
    private var special: Boolean = false;

    init {
        race = MonsterRace.DEMON
        combat.subscribe(object : CombatEventListener {
            override fun on(event: CombatEvent): Boolean {
                if (event is OutgoingHitApplied) {
                    if (attackType == 0 && event.hit.target is Player) {
                        var player = event.hit.target as Player;
                        if (Spell.WIND_BLAST.equals(previousSpell)) {
                            PoisonEffect.applyPoisonTo(event.hit.target, PoisonType.SUPER);
                        } else if (Spell.WATER_BLAST.equals(previousSpell)) {
                            if (Misc.random(3) == 0 && !PrayerHandler.isActivated(player, PrayerHandler.PROTECT_FROM_MAGIC)) {
                                player.getCombat().submit(FreezeEvent(10, false))
                                if (!player.getTimerRepository().has(TimerKey.FREEZE) && !player.getTimerRepository()
                                        .has(TimerKey.FREEZE_IMMUNITY)
                                ) {
                                    player.performGraphic(Graphic(2005))
                                }
                            }
                        } else if (Spell.EARTH_BLAST.equals(previousSpell)) {
                            if (Misc.random(2) == 0) {
                                player.skillManager.setCurrentLevel(
                                    Skill.DEFENCE,
                                    player.skillManager.getCurrentLevel(Skill.DEFENCE) - 5,
                                    true
                                );
                                player.message("Your defence has been drained!");
                            }
                        }
                    }
                } else if (event is OutgoingHitQueued && attackType == 0 && Spell.FIRE_BLAST.equals(previousSpell)) {
                    event.hit.multiplyDamage(1.5); //double damage for fire blast
                }
                return false
            }
        })
    }

    override fun attackTypes() = AttackType.equalChances(AttackType.MAGIC, AttackType.MELEE)

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun randomizeAttack() {
        var attack = generateAttack();
        if (hitpoints < maxHitpoints / 2 && Misc.random(3) == 0) {
            attack.setType(AttackType.SPECIAL)
            special = true;
            say("YOU WILL FACE DEATH!");
            combat.extendNextAttackDelay(6)
            performAnimation(Animation(69))
            playerStream(32).filter { p: Player? -> p != null && p.area === area }.forEach { p: Player ->
                val builder = ProjectileTemplateBuilder(2010)
                builder.setCurve(10)
                builder.setStartHeight(100)
                builder.setEndHeight(20)
                builder.setSpeed(if (asNpc.combat.target != null) (40 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60)
                builder.build()
                Projectile(centerPosition, p.position, builder.build()).sendProjectile()
                val currentPos = Position(p.position.x, p.position.y, p.position.z);
                p.message("<col=ff0000>Porazdir has sent a deadly projectile towards your current position, MOVE!");
                TaskManager.submit(object : Task(5, this, false) {
                    override fun execute() {
                        special = false;
                        if (isActive() && !isDying()) {
                            spawn(TileGraphic(currentPos, Graphic(1677)))
                            if (currentPos.equals(p.position)) {
                                p.combat.queue(Damage(15 + Misc.random(30), DamageMask.REGULAR_HIT))
                            }
                        }
                        stop()
                    }
                })
            }
        } else if (bossAttack.type() == AttackType.MAGIC) {
            previousSpell = spell;
            spell = Misc.random(*Spell.values())
        }
        bossAttack = attack;
    }
    override fun maxTargetsHitPerAttack(type: AttackType) = 64;
    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MAGIC -> 15
        else -> 2
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun skipNextCombatSequence(): Boolean {
        return special;
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.SPECIAL -> {
            Stream.empty<HitTemplate>()
        }
        AttackType.MAGIC ->
        {
            HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(if (type == AttackType.MAGIC) (if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)) else 0)
                .setIgnorePrayer(Misc.random(3) == 1)
                .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .also {
                    if(type == AttackType.MAGIC)
                        it.setSuccessOrFailedGraphic(spell?.endGraphic)
                            .setSuccessSound(spell?.sound)
                }.buildAsStream()
        }
        else ->
            HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()
    }

    override fun getAttackAnimation(type: AttackType?) = Animation(if(type == AttackType.MAGIC) 69 else 64)
    override fun fetchAttackDuration(type: AttackType?) = 6
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        if(type == AttackType.MAGIC){
            return (spell?.let {
                Stream.of(ProjectileTemplate.builder(it.projectileId)
                        .setSourceSize(3)
                        .setSourceOffset(3)
                        .setStartHeight(93)
                        .setEndHeight(40)
                        .setSpeed(if (asNpc.combat.target != null) (45 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3.5).coerceAtMost(
                            50.0
                        ).toInt()) else 45)
                        .setDelay(22)
                        .setCurve(280)
                        .build())
            })?:Stream.empty()
        }
        return Stream.empty()
    }

    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> {
        if(type == AttackType.MAGIC)
            return Optional.ofNullable(spell?.startGraphic)
        return Optional.empty()
    }

    enum class Spell(val projectileId: Int, val startGraphic: Graphic, val endGraphic: Graphic, val sound: Sound)
    {
        WIND_BLAST(159, Graphic(158, 2, 320), Graphic(160, 3, GraphicHeight.HIGH), Sound(Sounds.WIND_WAVE_CONTACT)),
        WATER_BLAST(162, Graphic(161, 2, 320), Graphic(163, 3, GraphicHeight.HIGH), Sound(Sounds.WATER_WAVE_CONTACT)),
        EARTH_BLAST(165, Graphic(164, 2, 320), Graphic(166, 3, GraphicHeight.HIGH), Sound(Sounds.EARTH_WAVE_CONTACT)),
        FIRE_BLAST(156, Graphic(155, 2, 320), Graphic(157, 3, GraphicHeight.HIGH), Sound(Sounds.FIRE_WAVE_CONTACT))
    }
}