package com.grinder.game.model.consumable

import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.agent.player.resetInteractions
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.ItemActions
import com.grinder.game.model.ItemActions.ItemClickAction
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.ItemID
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object DwarvenRockCakePlugin {
    private const val ROCK_CAKE = ItemID.DWARVEN_ROCK_CAKE
    private const val CLICK_DELAY_MILLIS = 200L

    init {
        ItemActions.onClick(ROCK_CAKE) {
            if (player.isJailed || !player.passedTime(Attribute.ROCK_CAKE_WAIT, CLICK_DELAY_MILLIS, TimeUnit.MILLISECONDS, false)) {
                return@onClick true
            }
            when {
                isEatOption() -> {
                    player.eatRockCake()
                    if (player.getLevel(Skill.HITPOINTS) > 2) {
                        player.combat.queue(Damage(1, DamageMask.REGULAR_HIT))
                    }
                    player.sendMessage("Ow! You nearly broke a tooth!")
                    player.sendMessage("The rock cake resists all attempts to eat it.")
                    true
                }
                isGuzzleOption() -> {
                    player.eatRockCake()
                    val damage = if (player.getLevel(Skill.HITPOINTS) > 1) ceil(player.getLevel(Skill.HITPOINTS) * 0.1).toInt() else 0
                    player.combat.queue(Damage(damage, DamageMask.REGULAR_HIT))
                    player.sendMessage("You bite hard into the rock cake to guzzle it down.")
                    player.sendMessage("OW! A terrible shock jars through your skull.")
                    true
                }
                else -> false
            }
        }
    }

    private fun Player.eatRockCake() {
        performAnimation(Animation(829))
        playSound(1018)
        resetInteractions()
    }

    private fun ItemClickAction.isEatOption(): Boolean {
        return isFirstAction()
    }

    private fun ItemClickAction.isGuzzleOption(): Boolean {
        return isThirdAction()
    }
}
