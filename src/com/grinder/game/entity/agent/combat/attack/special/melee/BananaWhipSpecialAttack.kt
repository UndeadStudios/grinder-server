package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackContext
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.*
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.*

/**
 * This is a custom special attack for a custom weapon.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class BananaWhipSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.BANANA_WHIP

    override fun secondaryAccuracyModifier(context: AttackContext) = 1.20

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val hit1 = Hit(actor, target, this, HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(284, GraphicHeight.MIDDLE, Priority.HIGH))
                .build())
        if(target is NPC){
            if(actor is Player){
                val bananasInInventory = actor.inventory.getAmount(1963)
                if(bananasInInventory > 0){
                    hit1.damages.first()?.also { damage ->
                        damage.incrementDamage(bananasInInventory*2)
                    }
                }
            }
        }
        return arrayOf(hit1)
    }

    /*override fun postHitEffect(hit: Hit) {
        val target = hit.target
        if (target is Player && target.isAlive) {
            if (!target.isSkulled)
            target.combat.skull(SkullType.WHITE_SKULL, 180)
            hit.attacker.messageIfPlayer("Your target has been skulled.", 1_000)
        }
    }*/

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .setSuccessOrFailedGraphic(Graphic(284, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1658, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(607))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.BANANA_WHIP_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }
}
