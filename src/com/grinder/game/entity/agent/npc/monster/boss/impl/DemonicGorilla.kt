package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.combat.*
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

/**
 * Demonic gorillas is a [Boss] from OSRS.
 *
 * @author MrJason83 (base)
 * @author Stan van der Bend (converted to new boss system)
 *
 * @param id        the id of the gorilla instance (there are multiple).
 * @param position  the [Position] to spawn this instance at.
 */
class DemonicGorilla(id: Int, position: Position) : Boss(id, position), AttackProvider {

    private var prayerProtectedAttackType: AttackType? = null

    /**
     * Tracks incoming damage of the gorilla
     */
    private var damageTaken = 0

    /**
     * Track unsuccessful (0 damage) hits dealt by the gorilla
     */
    private var missedAttackCount = 0

    private var nextType: AttackType? = null

    init {

        combat.onIncomingHitQueued {
            if (attackType == prayerProtectedAttackType) {
                isAccurate = false
                totalDamage = 0
            }
        }
        combat.onIncomingHitApplied {

            if (totalDamage < 1 || attackType == prayerProtectedAttackType)
                return@onIncomingHitApplied

            damageTaken += totalDamage

            if (damageTaken > 50) {
                damageTaken = 0
                switchProtectionPrayer(attackType)
            }
        }
        combat.onOutgoingHitApplied {
            if (attackType == AttackType.SPECIAL)
                return@onOutgoingHitApplied

            if (totalDamage > 0) {
                missedAttackCount = 0
                nextType = attackType
                randomizeAttack()
            } else {
                missedAttackCount++
            }

            if(missedAttackCount == 2) {
                val target = target?:return@onOutgoingHitApplied
                val type = attackType
                nextType = next(type)
            }
        }
        combat.onState(CombatState.STARTING_ATTACK) {
            if (bossAttack.type() == AttackType.SPECIAL) {
                val target = combat.target ?: return@onState
                val start = target.position.clone().add(1, 0)
                val end = target.position.clone()

                val projectile = Projectile(
                    start, end, ProjectileTemplate
                        .builder(856)
                        .setSourceOffset(160)
                        .setHeights(250, 8)
                        .setSpeed(135)
                        .setDelay(30)
                        .setCurve(280)
                        .setArrivalSound(AreaSound(1442, 0, 1, 7))
                        .build()
                )

                projectile.onArrival {
                    if (target!!.isUntargetable || target!!.isTeleporting)
                        return@onArrival
                    World.spawn(TileGraphic(end, Graphic(166, 20, GraphicHeight.LOW)))

                    if (target.position.sameAs(end))
                        target.combat.queue(Damage.create(target.hitpoints / 3))
                }
                projectile.sendProjectile()
            }
        }
    }

    override fun getDebugMessages(): MutableList<String> {
        return mutableListOf(
            "Missed: $missedAttackCount"
        )
    }
    override fun generateAttack() = BossAttack(this)

    /**
     * Transforms this gorillas appearance and overhead prayer
     * based on the argued [type].
     */
    private fun switchProtectionPrayer(type: AttackType) {
        PrayerHandler.deactivatePrayers(this)
        val newNpcId = NPC_ID_BY_PROTECTED_ATTACK_MAP[type]?:return
        npcTransformationId = newNpcId
        prayerProtectedAttackType = type
        PrayerHandler.activatePrayer(this, PrayerHandler.getProtectingPrayer(type))
    }

    /**
     * Returns the next attack type based on the argued [type].
     */
    private fun next(type: AttackType): AttackType {
        val styles = arrayOf(AttackType.MELEE, AttackType.RANGED, AttackType.MAGIC)

        return styles.filterNot { t -> t == type }.random()
    }

    override fun randomizeAttack() {

        if(nextType != null){
            bossAttack.setType(nextType!!)
            nextType = null
        } else if(!bossAttack.hasPreferredType() || missedAttackCount >= 2) {
            debug("Missed 3 attacks, switching")
            missedAttackCount = 0
        }

        if(bossAttack.type() == AttackType.MELEE){
            if(Misc.randomChance(25F)){
                bossAttack.setType(AttackType.SPECIAL)
                missedAttackCount = 0
            }
        }

        val attackType = bossAttack.type()
        if(attackType == AttackType.SPECIAL){
            nextType = next(AttackType.SPECIAL)
            missedAttackCount = 2;
        }
    }

    override fun attackTypes(): AttackTypeProvider = AttackType.builder()
            .add(Odds.ONE_TENTH, AttackType.SPECIAL)
            .add(Odds.TWO_FOURTH, AttackType.MELEE, AttackType.RANGED, AttackType.MAGIC)
            .build()

    override fun maxTargetsHitPerAttack(type: AttackType) = 1

    override fun attackRange(type: AttackType): Int {
        return if(type == AttackType.MELEE) 1 else 4
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.MAGIC -> HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(2)
            .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
            .setSuccessOrFailedGraphic(Graphic(1305, Priority.HIGH))
            .setSuccessOrFailedSound(Sound(3528))
            .buildAsStream()
        AttackType.RANGED -> HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(2)
            .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
            .setSuccessOrFailedGraphic(Graphic(1303, Priority.HIGH))
            .buildAsStream()
        AttackType.MELEE -> HitTemplate
            .builder(AttackType.MELEE)
            .setDelay(0)
            .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
            .buildAsStream()
        else -> Stream.empty()
    }

    override fun getAttackAnimation(type: AttackType?): Animation {
        return Animation(when(type){
            AttackType.MELEE -> 7226
            AttackType.RANGED -> 7240
            AttackType.MAGIC -> 7238
            else -> 7228
        })
    }

    override fun fetchAttackDuration(type: AttackType?) = 5

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MELEE -> Optional.of(Sound(3350))
            AttackType.MAGIC -> Optional.of(Sound(3529, 15))
            AttackType.RANGED -> Optional.of(Sound(3191))
            AttackType.SPECIAL -> Optional.of(AreaSound(1444, 0, 1, 7))
            else -> Optional.empty()
        }
    }

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        return when(type) {
            AttackType.RANGED -> ProjectileTemplate
                    .builder(1302)
                    .setSourceSize(1)
                    .setHeights(64, 0)
                    .setSpeed(30)
                    .setDelay(50)
                    .setCurve(0)
                    .buildAsStream()
            AttackType.MAGIC -> ProjectileTemplate
                    .builder(1304)
                    .setHeights(64, 0)
                    .setDelay(65)
                    .setSpeed(30)
                    //.setDepartureSound(Sound(3529, 15))
                    //.setArrivalSound(Sound(3528))
                    .setCurve(0)
                    .buildAsStream()
            else -> Stream.empty()
        }
    }

    companion object {

        private const val MELEE_PROTECTED_NPC_ID = 7144
        private const val RANGED_PROTECTED_NPC_ID = 7145
        private const val MAGIC_PROTECTED_NPC_ID = 7146

        /**
         * The mapping which holds every gorilla with the correct attack type overhead.
         */
        private val NPC_ID_BY_PROTECTED_ATTACK_MAP = mapOf(
                AttackType.MELEE to MELEE_PROTECTED_NPC_ID,
                AttackType.RANGED to RANGED_PROTECTED_NPC_ID,
                AttackType.MAGIC to MAGIC_PROTECTED_NPC_ID)

    }
}