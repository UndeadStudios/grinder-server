package com.grinder.game.entity.agent.combat.attack.special

import com.google.common.base.Preconditions
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.model.projectile.ProjectileTemplate
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
abstract class RangedSpecialAttackProvider : SpecialAttackProvider {

    open fun fetchProjectiles(type: AttackType, ammunition: Ammunition): Stream<ProjectileTemplate> {
        return Stream.empty()
    }

    override fun fetchAttackDuration(type: AttackType?): Int {
        // in the case of ranged, the default methods can be used from RangedAttackStrategy
        // by setting the constructor fields to true
        Preconditions.checkArgument(type != AttackType.RANGED, "Make sure to override the duration!")
        System.err.println("Undefined attack duration for $this using $type, please resolve!")
        return 0
    }

    final override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate>
            = Stream.empty()
}