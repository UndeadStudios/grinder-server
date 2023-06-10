package com.grinder.game.entity.agent.combat.attack.special

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.projectile.Projectile
import java.util.stream.Collectors

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/05/2020
 * @version 1.0
 */
abstract class RangedSpecialAttack(override val provider: RangedSpecialAttackProvider,
                                   private val useDefaultDuration: Boolean = true,
                                   private val useDefaultAttackCheck: Boolean = true
) : SpecialAttack(provider) {

    abstract fun weaponType(): RangedWeaponType

    override fun type() = AttackType.RANGED

    override fun requiredDistance(actor: Agent): Int {
        val weaponType = weaponType()
        val fightType = actor.combat.fightType
        return if (fightType == weaponType.longRangeFightType) {
            weaponType.longRangeDistance
        } else
            weaponType.defaultDistance
    }

    override fun sequence(actor: Agent, target: Agent) {

        val ammunition = actor.combat?.ammunition
        if(ammunition == null){
            System.err.println("No ammunition for actor sequencing $this, skipping sequence")
            return
        }

        val projectiles = provider.fetchProjectiles(type(), ammunition)
                .map { Projectile(actor, target, it)  }
                .collect(Collectors.toList())

        projectiles.forEach {
            it.sendProjectile()
        }

        decrementAmmo(actor, ammunition, projectiles)
    }

    private fun decrementAmmo(actor: Agent, ammunition: Ammunition?, projectiles: MutableList<Projectile>) {
        if (actor is Player && ammunition != null) {
            val cost = ammunitionCost()
            RangedAttackStrategy.processAmmunitionProjectile(actor, ammunition, projectiles.first(), cost)
        }
    }

    override fun duration(actor: Agent) = if(useDefaultDuration)
        RangedAttackStrategy.INSTANCE.duration(actor)
    else
        super.duration(actor)

    override fun canAttack(actor: Agent, target: Agent) = if(useDefaultAttackCheck)
        RangedAttackStrategy.INSTANCE.canAttack(actor, target)
    else
        super.canAttack(actor, target)

    open fun ignoreEnchantedBoltEffect() = true

    open fun ammunitionCost() = 1
}