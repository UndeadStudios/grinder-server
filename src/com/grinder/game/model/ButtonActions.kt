package com.grinder.game.model

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.ButtonActions.ClickAction
import com.grinder.game.model.ButtonActions.onClick
import java.util.function.Consumer

/**
 * This class can be used to configure actions to button ids.
 *
 * Each button id can have multiple mappings.
 *
 * @see onClick to configure actions for [ClickAction]
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/11/2019
 * @version 1.0
 */
object ButtonActions {

    private val buttonActions = HashMap<Int, (ClickAction) -> Unit>()
    private val disabledButtons = HashSet<Int>()
    private val ignoreRestrictions = HashSet<Int>()

    fun onClick(buttonIdRange: IntRange, ignoreRestriction: Boolean = false, function: ClickAction.() -> Unit) {
        buttonIdRange.forEach {
            buttonActions[it] = function
            if (ignoreRestriction)
                ignoreRestrictions.add(it)
        }
    }

    fun onClick(vararg buttonIds: Int, ignoreRestriction: Boolean = false, function: ClickAction.() -> Unit) {
        buttonIds.forEach {
            buttonActions[it] = function
            if (ignoreRestriction)
                ignoreRestrictions.add(it)
        }
    }

    fun onClick(buttonId: Int, consumer: Consumer<ClickAction>)
    {
        buttonActions[buttonId] = fromConsumer(consumer)
    }

    fun onClick(buttonId: Int, buttonId2: Int, consumer: Consumer<ClickAction>) {
        val function = fromConsumer(consumer)
        buttonActions[buttonId] = function
        buttonActions[buttonId2] = function
    }

    fun handleClick(player: Player, buttonId: Int) : Boolean {

        if(disabledButtons.contains(buttonId)){
            player.statement("This button is currently disabled :(")
            return true
        }

        val action = buttonActions[buttonId]?:return false

        if (!ignoreRestrictions(buttonId)){
            if (player.BLOCK_ALL_BUT_TALKING) return true
            if (player.isInTutorial) return true
        }

        action.invoke(ClickAction(player, buttonId))
        return true
    }

    fun ignoreRestrictions(buttonId: Int) = ignoreRestrictions.contains(buttonId)

    fun setIgnoreRestriction(buttonId: Int, ignore: Boolean) {
        if (ignore)
            ignoreRestrictions.add(buttonId)
        else
            ignoreRestrictions.remove(buttonId)
    }

    fun toggleClickable(buttonId: Int) : Boolean {
        return if(disabledButtons.contains(buttonId)) {
            disabledButtons.remove(buttonId)
            false
        } else {
            disabledButtons.add(buttonId)
            true
        }
    }

    class ClickAction(val player: Player, val id: Int)

    private fun fromConsumer(callable: Consumer<ClickAction>) =  { t: ClickAction -> callable.accept(t) }
}