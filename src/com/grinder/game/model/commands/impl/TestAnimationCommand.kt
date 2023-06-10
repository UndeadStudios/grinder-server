package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.commands.DeveloperCommand

class TestAnimationCommand : DeveloperCommand() {

    override fun getSyntax(): String {
        return "[id]"
    }

    override fun getDescription(): String {
        return "Perform an animation."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        if (command.length <= 5) {
            player.message("Wrong usage of the command!")
            return
        }
        val anim = parts[1].toInt()
        player.performAnimation(Animation(anim))
    }
}