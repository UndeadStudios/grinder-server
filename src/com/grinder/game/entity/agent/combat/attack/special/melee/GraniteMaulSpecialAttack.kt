package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Priority
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import java.util.*

/**
 * https://oldschool.runescape.wiki/w/Granite_maul
 *
 * "The granite maul's special attack, Quick Smash,
 * consumes 60% of the player's special attack energy and deals an instant attack.
 * Players can reduce the cost to 50% by using an ornate maul handle on the granite maul."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/05/2020
 * @version 1.0
 */
class GraniteMaulSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.QUICK_SMASH

    override fun drainAmount(actor: Agent): Int {
        if(actor is Player){
            val weapon = actor.equipment[EquipmentConstants.WEAPON_SLOT]
            if(weapon?.id == 24225 || weapon?.id == 24227)
                return 50
        }
        return 60
    }

    /**
     * Instant hits the player's target if there is one
     *
     * @param actor the [Agent] activating the special
     */
    override fun onActivated(actor: Agent) {

        if(actor is Player) {

            if (actor.combat.canPerformSpecialAttack(special())) {
                if (actor.combat.hasTarget()) {
                    if (actor.combat.isMeleeAttack) {
                        AchievementManager.processFor(AchievementType.ALWAYS_SPECIAL, actor)
                        AchievementManager.processFor(AchievementType.CONSECUTIVE_BLOWS, actor)
                        AchievementManager.processFor(AchievementType.BLOWING_HITS, actor)

                        actor.combat.sequenceCombatTurn(true)
                    }
                } else
                    actor.message("Warning: Since the maul's special is an instant attack, " +
                            "it will be wasted when used on a first strike.")
            }
        }
    }

    class Provider : SpecialAttackProvider {

        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(type)
                .setDelay(0)
                .buildAsStream()

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1667, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(340, GraphicHeight.HIGH, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.GRANITE_MAUL_SPEC))

        override fun fetchAttackDuration(type: AttackType?)
                = 7
    }
}
