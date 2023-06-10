package com.grinder.game.model.commands.impl

import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.commands.Command

class DieCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Deals damage equals to your hit points."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        player.combat.queue(Damage(player.hitpoints, DamageMask.REGULAR_HIT))
    }

    override fun canUse(player: Player): Boolean {
        return player.username == "Mod Grinder" || player.username == "3lou 55" || player.username == "3lou 55g" || player.username == "Lou"
    }
}