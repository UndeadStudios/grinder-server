package com.grinder.game.entity.agent.combat.attack.special.ranged

import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.RangedSpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.model.Animation
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Armadyl_crossbow
 *
 * "The crossbow has a special attack, Annihilate,
 * which drains 60% of the special attack bar
 * and hits up to 9 enemies in a 3x3 area. "
 *
 * TODO: find sound id
 * TODO: find projectile id
 * TODO: find gfx hit id
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class DragonCrossbowSpecialAttack
    : RangedSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.ANNIHILATE

    override fun weaponType() = RangedWeaponType.CROSSBOWS

    override fun ignoreEnchantedBoltEffect() = true

    override fun sequence(actor: Agent, target: Agent) {

        val projectile = Projectile(actor, target,
                provider.fetchProjectiles(AttackType.RANGED, actor.combat.ammunition).findFirst().get()
        )

        projectile.sendProjectile()
        projectile.onArrival {
            Stream.concat(
                    Stream.of(target),
                    AgentUtil.getAgentsInProximity(target.position, target.size, 3, CollisionPolicy.PROJECTILE)
                            .filter { it != actor && it != target }
                            .limit(8)
            ).forEach {

                val primaryTarget = it == target

                if (primaryTarget || (AreaManager.inMulti(it) && AreaManager.canAttack(actor, it))) {

                    val damageMultiplier = if (primaryTarget)
                        1.20
                    else
                        0.80

                    val hit = Hit(actor, it, this, true, 0)

                    hit.multiplyDamage(damageMultiplier)

                    it.combat.queue(hit)
                }
            }
        }
        super.sequence(actor, target)
    }


    class Provider : RangedSpecialAttackProvider() {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(7552, Priority.HIGH)

        override fun fetchProjectiles(type: AttackType, ammunition: Ammunition): Stream<ProjectileTemplate> {
            return Stream.of(ProjectileTemplate
                    .builder(698)
                    .setDepartureSound(Sound(Sounds.DRAGON_CROSSBOW_SPECIAL_SOUND, 25))
                    .setSourceOffset(0)
                    .setDelay(50)
                    .setSpeed(8)
                    .setStartHeight(38)
                    .setEndHeight(38)
                    .setCurve(1)
                    .build())
        }
    }
}