package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.sound.Sounds

/**
 * Handles the Saradomin mage's combat.
 *
 */
class SaradominWizardAttack : AttackStrategy<NPC> {

    override fun type() = AttackType.MAGIC

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, 2))
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 5

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target != null) {
            if (hit.totalDamage != 0) {
                hit.target.performGraphic(MAGIC_COMBAT_GFX)
                hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(244) }
            } else {
                hit.target.performGraphic(MAGIC_SPLASH_GFX)
                hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(Sounds.MAGIC_SPLASH) }
            }
        }
    }

    companion object {
        private val MAGIC_COMBAT_GFX = Graphic(76)
        private val MAGIC_SPLASH_GFX = Graphic(85, GraphicHeight.HIGH)
    }
}