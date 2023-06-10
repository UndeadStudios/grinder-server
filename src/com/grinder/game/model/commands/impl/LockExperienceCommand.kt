package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerSettings
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.entity.toggleBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.Command

class LockExperienceCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Set your experience lock to on/off."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        PlayerSettings.toggleExperience(player)
    }

    override fun canUse(player: Player): Boolean {
        return true
    }
}