package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Misc

/**
 * Handles the wizard mage's combat.
 */
class EarthWizardAttack : AttackStrategy<NPC> {

    override fun type() = AttackType.MAGIC

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, MagicAttackStrategy.getMagicSpellHitDelay(actor, target)))
    }

    override fun sequence(actor: NPC, target: Agent) {
        if (Misc.random(3) == 1) {
            actor.say("Grabtias tibi ago!")
        }
        Projectile(actor, target, PROJECTILE).sendProjectile()
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 5

    override fun animate(actor: NPC) {
        actor.performGraphic(Graphic(96, GraphicHeight.HIGH))
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target != null) {
            if (hit.totalDamage != 0) {
                hit.target.performGraphic(MAGIC_COMBAT_GFX)
                hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(Sounds.EARTH_STRIKE_CONTACT) }
            } else {
                hit.target.performGraphic(MAGIC_SPLASH_GFX)
                hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(Sounds.MAGIC_SPLASH) }
            }
        }
    }

    companion object {
        private val MAGIC_COMBAT_GFX = Graphic(98, GraphicHeight.HIGH)
        private val MAGIC_SPLASH_GFX = Graphic(85, GraphicHeight.HIGH)
        private val PROJECTILE = ProjectileTemplate
                .builder(97)
                .setSourceOffset(1)
                .setDelay(52)
                .setSpeed(11)
                .setHeights(42, 31)
                .setCurve(280)
                .build()
    }
}