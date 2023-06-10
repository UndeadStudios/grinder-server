package com.grinder.game.content.minigame

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.event.impl.PlayerLogoutEvent
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.PlayerActions
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption

/**
 * TODO: integrate this properly into minigame system
 */
object PublicMinigameHandler {

    init {
        //Will relocate our user if we are logged out during a minigame
/*        PlayerActions.onEvent(PlayerLogoutEvent::class) {
            if (player.minigame != null || MinigameManager.isInPublicMinigameArea(player)) {
                println("PublicMinigameHandler onLogout called")
                MinigameManager.leaveMinigame(player, MinigameManager.publicMinigame)
            }
        }*/
    }

    fun handleExitPortal(minigame: Minigame, player: Player) {
        DialogueBuilder(DialogueType.STATEMENT)
                .setText("This portal teleports you out of the minigame.", "Are you absolutely sure you want to leave?")
                .add(DialogueType.OPTION)
                .setOptionTitle("Select an Option")
                .firstOption("Leave Minigame.") {
                    if (it.minigame == minigame) {
                        it.removeInterfaces()
                        MinigameManager.leaveMinigame(it, minigame)
                    }
                }
                .addCancel("Don't leave.")
                .start(player)
    }


    fun canTeleport(minigame: Minigame, player: Player): Boolean {
        if (MinigameManager.publicMinigameStarted && MinigameManager.publicMinigame == minigame) {
            player.message("You cannot teleport while inside a minigame. Use the Exit portal to leave.")
            return false
        }
        return true
    }

    fun canAttack(minigame: Minigame, attacker: Agent?, target: Agent?): Boolean {
        if (MinigameManager.publicMinigameStarted && MinigameManager.publicMinigame == minigame) {
            if (MinigameManager.dangerousTimer > 0)
                return false
            if (attacker is Player) {
                if (!minigame.players.contains(attacker))
                    return false
                if (target is Player && !minigame.players.contains(target))
                    return false
                return true
            }
            return true
        }
        return false
    }

    fun onProcessMinigameArea(minigame: Minigame, agent: Agent) {
        if (MinigameManager.publicMinigameStarted && MinigameManager.publicMinigame == minigame) {
            if (agent is Player) {
                agent.packetSender.sendWalkableInterface(29377)
                MinigameManager.sendInterface(agent)
            }
        }
    }

    fun onEnterMinigameArea(minigame: Minigame, agent: Agent){
        if (agent is Player) {
            if (MinigameManager.isInPublicMinigameArea(agent) && (agent.minigame == null || !minigame.players.contains(agent))) {
                MinigameManager.resetMinigameState(agent)
                //println("Public onEnter zero");
                return
            }
            if (!MinigameManager.publicMinigameStarted || MinigameManager.publicMinigame != minigame) {
                //println("Public onEnter first");
                MinigameManager.resetMinigameState(agent)
                return
            }
            if (agent.minigame == null || !minigame.players.contains(agent)) {
                //println("PublicminigameHandler onEnterMinigame secoind")
                MinigameManager.leaveMinigame(agent, MinigameManager.publicMinigame)
            }
        }
    }

    fun onLeaveMinigameArea(minigame: Minigame, agent: Agent){
        if (agent is Player) {
            if (MinigameManager.isInPublicMinigameArea(agent) && (agent.minigame == null || !minigame.players.contains(agent))) {
                MinigameManager.resetMinigameState(agent)
                //    println("Public onLeave zero");
                return
            }
        }
        if (MinigameManager.publicMinigameStarted && MinigameManager.publicMinigame == minigame) {
            if (agent is Player) {
                if (minigame.players.contains(agent)) {
                    MinigameManager.leaveMinigame(agent, minigame)
                    //  println("Public onLeave first");
                }
                val lastPlayer = minigame.players.singleOrNull()
                if (lastPlayer != null && MinigameManager.initialParticipantsCount > 1) {
                    MinigameManager.wonMinigame(lastPlayer, minigame)
                    //println("Public onLeave second");
                }
            }
        }
    }
}