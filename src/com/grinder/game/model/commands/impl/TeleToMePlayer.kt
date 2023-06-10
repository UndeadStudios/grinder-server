package com.grinder.game.model.commands.impl

import com.grinder.game.World
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.commands.Command

class TeleToMePlayer : Command {

    override fun getSyntax(): String {
        return "[playerName]"
    }

    override fun getDescription(): String {
        return "Teleports the target player to your location."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (command.length <= 8) {
            player.message("Wrong usage of the command!")
            return
        }

        val optionalTarget = World.findPlayerByName(command.substring(parts[0].length + 1))
        if (optionalTarget.isPresent) {
            val target = optionalTarget.get()
            if (target.busy()) {
                player.message("<img=742> The player is currently busy and can't be teleported.")
                return
            }
            if (target.relations.ignoreList.contains(player.longUsername)) {
                player.message("<img=742> You can't teleport players that have you on their ignore list.")
                return
            }
            if (target.hitpoints <= 0) return
            if (target.status === PlayerStatus.TRADING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.busy() || target.interfaceId > 0) {
                player.packetSender.sendMessage("The player is currently busy and can't be teleported.", 1000)
                return
            }
            if (target.status === PlayerStatus.BANKING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.status === PlayerStatus.PRICE_CHECKING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.BLOCK_ALL_BUT_TALKING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.isInTutorial) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || target.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.combat.isInCombat) {
                player.message("<img=742> The player is currently in combat and can't be teleported.")
                return
            }
            if (target.isJailed) {
                player.sendMessage("The player is currently jailed and can't be teleported.")
                return
            }
            if (target.status === PlayerStatus.AWAY_FROM_KEYBOARD) {
                player.message("<img=779> You can't use afk command when it's already active.")
                return
            }
            if (!target.notTransformed(message = false, blockNpcOnly = false))
                return

            if (target.status === PlayerStatus.TRADING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (target.status === PlayerStatus.DUELING) {
                player.message("The player is currently busy and can't be teleported.")
                return
            }
            if (PlayerUtil.isDeveloper(target) && target.getBoolean(Attribute.INVISIBLE)) {
                player.message("<img=742> The player that you're trying to teleport to you is not currently online!")
                return
            }
            if (target.minigame != null) {
                player.sendMessage("<img=742> The player you're trying to teleport to you is currently in a minigame.")
                return
            }
            player.performAnimation(Animation(1818))
            player.performGraphic(Graphic(343))
            //player.getPacketSender().sendSound(199)
            target.setTeleportToCaster(player.asPlayer)
            target.setTeleportDestination(null)
            player.message("<img=742> You have sent a teleport request to @dre@" + target.username + "</col>!")
            target.packetSender.sendInterface(12468)
            target.packetSender.sendString("" + PlayerUtil.getImages(player) + "" + player.username + "", 12558)
            target.packetSender.sendString("" + (if (player.appearance.isMale) "His" else "Her") + "" + " location!", 12560)
            //player.packetSender.sendSound(225)
            player.packetSender.sendAreaPlayerSound(225, 8, 1, 0)
        } else {
            player.message("<img=742> The player that you're trying to teleport to you is not currently online!")
            return
        }
    }

    override fun canUse(player: Player): Boolean {
        val rights = player.rights
        return rights === PlayerRights.OWNER || rights === PlayerRights.DEVELOPER
                || rights === PlayerRights.ADMINISTRATOR || rights === PlayerRights.GLOBAL_MODERATOR
                || rights === PlayerRights.CO_OWNER
    }
}