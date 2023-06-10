package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.commands.Command
import com.grinder.util.Misc

class TeleportOtherToHomeCommand : Command {

    override fun getSyntax(): String {
        return ""
    }

    override fun getDescription(): String {
        return "Attempt to teleport a player to home area."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val plr = World.findPlayerByName(command.substring(parts[0].length + 1))
        if (plr.isPresent) {
            if (plr.get().busy()) {
                player.sendMessage("<img=742> The player is currently busy and can't be teleported.")
                return
            }
            if (plr.get().relations.ignoreList.contains(player.longUsername)) {
                player.sendMessage("<img=742> You can't teleport players that have you on their ignore list.")
                return
            }
            if (plr.get().hitpoints <= 0) return
            if (plr.get().status === PlayerStatus.TRADING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().status === PlayerStatus.BANKING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().status === PlayerStatus.PRICE_CHECKING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().BLOCK_ALL_BUT_TALKING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().isJailed) {
                player.sendMessage("The player is currently jailed and can't be teleported.")
                return
            }
            if (plr.get().isInTutorial) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || plr.get().getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().combat.isInCombat) {
                player.sendMessage("<img=742> The player is currently in combat and can't be teleported.", 1000)
                return
            }
            if (plr.get().status === PlayerStatus.AWAY_FROM_KEYBOARD) {
                player.sendMessage("<img=779> You can't use afk command when it's already active.", 1000)
                return
            }
            if (!plr.get().notTransformed(message = false, blockNpcOnly = false))
                return
            if (plr.get().status === PlayerStatus.TRADING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (plr.get().status === PlayerStatus.DUELING) {
                player.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            player.performAnimation(Animation(1818))
            player.performGraphic(Graphic(343))
            //player.getPacketSender().sendSound(199)
            plr.get().setTeleportToCaster(player.asPlayer)
            player.sendMessage("<img=742> You have sent a home teleport request to @dre@" + plr.get().username + "</col>!")
            plr.get().packetSender.sendInterface(12468)
            plr.get().packetSender.sendString("" + PlayerUtil.getImages(player) + "" + player.username + "", 12558)
            plr.get().packetSender.sendString("Home area!", 12560)
            plr.get().setTeleportDestination(Position(3087 + Misc.getRandomInclusive(3), 3486 + Misc.getRandomInclusive(3), 0))
            //player.packetSender.sendSound(225)
            player.packetSender.sendAreaPlayerSound(225, 8, 1, 0)
        } else {
            player.sendMessage("<img=742> The player that you're trying to teleport home is not currently online!")
            return
        }
    }

    override fun canUse(player: Player): Boolean {
        return player.rights.anyMatch(PlayerRights.SERVER_SUPPORTER, PlayerRights.MODERATOR, PlayerRights.GLOBAL_MODERATOR, PlayerRights.ADMINISTRATOR, PlayerRights.DEVELOPER, PlayerRights.CO_OWNER, PlayerRights.OWNER)
    }
}