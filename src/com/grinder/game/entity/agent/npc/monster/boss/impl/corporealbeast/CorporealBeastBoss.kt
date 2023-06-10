package com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast


import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.DarkEnergyCore
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.event.CombatEventListener
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.DistanceUtil
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class CorporealBeastBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {
    var darkEnergyCore: DarkEnergyCore
    private fun onCombatEvent(event: CombatEvent): Boolean {
        if (event !is IncomingHitApplied) {
            return false
        }
        val hit = event.hit
        val details = hit.attackDetails
        if (!details.usedAny(WeaponInterface.SPEAR, WeaponInterface.HALBERD) && details.used(ItemID.OSMUMTENS_FANG) && details.used(ItemID.OSMUMTENS_FANG_OR)) {
            hit.multiplyDamage(0.50)
        }
        if (details.used(RangedWeapon.TWISTED_BOW)) {
            hit.multiplyDamage(0.50)
        }
        if (hit.totalDamage >= 31) {
            if (Misc.random(8) % 8 == 0) {
                darkEnergyCore.spawn()
            }
        }
        return true
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun protectionPrayerReductionMultiplier(type: AttackType): Double {
        return if (type == AttackType.MAGIC) 0.6666 else super.protectionPrayerReductionMultiplier(type)
    }

    public override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC)
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 6
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 12
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true //type == MAGIC;
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MAGIC) MAGIC_DISTANCE else 1
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.MAGIC) MAGIC_ANIMATION else MELEE_ANIMATION
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) Stream.of(MAGIC_PROJECTILE) else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        if (type == AttackType.MELEE) {
            return Stream.of(HitTemplate.builder(type).setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH).build())
        }
        return Stream.of(HitTemplate.builder(type).setDelay(if (asNpc.combat.target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)).build())
    }

    override fun appendDeath() {
        super.appendDeath()
        darkEnergyCore.kill()
    }


        private val MAGIC_DISTANCE = 12
        private val MELEE_ANIMATION = Animation(1683)
        private val MAGIC_ANIMATION = Animation(1680)
        private val MAGIC_PROJECTILE = ProjectileTemplate.builder(316)
            .setSourceSize(5).setStartHeight(36).setEndHeight(43).setSpeed(if (asNpc.combat.target != null) (15 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(30)) else 15).setDelay(35).setCurve(280).build()


    init {
        darkEnergyCore = DarkEnergyCore(this)
        combat.subscribe(object : CombatEventListener {
            override fun on(event: CombatEvent): Boolean {
                return onCombatEvent(event)
            }
        })
    }
}