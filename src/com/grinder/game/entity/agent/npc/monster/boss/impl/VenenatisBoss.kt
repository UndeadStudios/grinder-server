package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.decreaseLevel
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Venenatis
 *
 * "Venenatis is a large poison spider that resides east of the Bone Yard"
 *
 * She can:
 * - use multi-target Magic
 * - use long-ranged melee attacks
 * - drain Prayer
 * - perform a lethal stun attack (50+ damage)
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class VenenatisBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    companion object {
        private val DRAIN_PRAYER_HIT_GRAPHIC = Graphic(172, GraphicHeight.MIDDLE)
        private val DRAIN_PRAYER_BOOST_GRAPHIC = Graphic(170, GraphicHeight.HIGH)
        private val DRAIN_PRAYER_PROJECTILE = ProjectileTemplate
                .builder(171)
                .setSourceSize(1)
                .setSourceOffset(1)
                .setStartHeight(50)
                .setEndHeight(33)
                .setSpeed(35)
                .setDelay(30)
                .setCurve(8)
                .build()
    }

    init {
        race = MonsterRace.SPIDER
        combat.onOutgoingHitApplied {
            if (isAccurate) {
                if (Misc.randomChance(15.0f)) {
                    target?.ifPlayer { player: Player ->
                        player.performGraphic(DRAIN_PRAYER_HIT_GRAPHIC)
                        player.message("Venenatis drained your prayer!")
                        player.decreaseLevel(Skill.PRAYER, (totalDamage * 0.35).toInt())
                        val projectile = Projectile(player, attacker, DRAIN_PRAYER_PROJECTILE)
                        projectile.sendProjectile()
                        projectile.onArrival {
                            attacker.performGraphic(DRAIN_PRAYER_BOOST_GRAPHIC)
                        }
                    }
                }
            }
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC)
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MAGIC) 4 else 1
    }

    override fun attackRange(type: AttackType): Int {
        return 2
    }

    override fun fetchAttackDuration(type: AttackType) = 6

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.MELEE)
            Animation(5319)
        else
            Animation(5322)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) ProjectileTemplate
                .builder(165)
                .setSourceOffset(2)
                .setSourceSize(2)
                .setStartHeight(34)
                .setEndHeight(28)
                .setCurve(280)
                .setSpeed(DistanceUtil.getChebyshevDistance(asNpc!!.position, asNpc!!.combat!!.target!!.position) * 2.coerceAtMost(40))
                .setDepartureSound(Sound(Sounds.EARTH_WAVE_CAST))
                .setSpeed(35)
                .buildAsStream() else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return when(type) {
            AttackType.MAGIC -> {
                return HitTemplate
                    .builder(AttackType.MAGIC)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                    .setDelay(MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc!!.combat.target))
                    .setSuccessOrFailedGraphic(Graphic(166, GraphicHeight.HIGH, Priority.HIGHEST))
                    .setSuccessOrFailedSound(Sound(Sounds.EARTH_WAVE_CONTACT))
                    .buildAsStream()
            }
            AttackType.MELEE -> {
                return HitTemplate
                    .builder(AttackType.MELEE)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_STAB)
                    .setDelay(0)
                    .buildAsStream()
            }
            else -> {
                Stream.empty()
            }
        }

    }
}