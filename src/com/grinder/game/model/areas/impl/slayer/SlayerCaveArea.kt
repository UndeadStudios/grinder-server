package com.grinder.game.model.areas.impl.slayer

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.slayer.BasicDeathSpawn
import com.grinder.game.entity.agent.npc.slayer.Nechryael
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.Area
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.util.NpcID
import java.util.*
import java.util.concurrent.TimeUnit

class SlayerCaveArea : Area(cave, extendedCave) {
    override fun process(agent: Agent) {}

    override fun canTeleport(player: Player): Boolean {
        return true
    }

    override fun canAttack(attacker: Agent, target: Agent): Boolean {

        if (attacker.isPlayer && target.isNpc) {
            if (cave.contains(attacker.position) || extendedCave.contains(attacker.position)) {
                val npc = target.asNpc
                val pl = attacker.asPlayer
                return if (pl.slayer.task != null && pl.slayer.task.amountLeft > 0 && SlayerManager.isMonsterPartOfTask(pl, npc.fetchDefinition())
                    || SuperiorSlayerMonsters.forId(npc.id).isPresent) {
                    true
                } else {
                    if (pl.passedTime(timerAttribute, 10, TimeUnit.SECONDS, false))
                        jellyDialogue.start(pl)
                    pl.sendMessage("Captain Cleive wants you to stick to your Slayer assignments.")

                    false
                }
            }
        }
        return !(attacker.isPlayer && target.isPlayer)
    }

    override fun canTrade(player: Player, target: Player): Boolean {
        return true
    }

    override fun enter(agent: Agent?) {
        if(agent is Player) {
            super.enter(agent)
        }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player) {
            super.leave(agent)
        }
    }

    override fun isMulti(agent: Agent): Boolean {
        // this is abit of a hack.
        if (agent is BasicDeathSpawn || agent is Nechryael) {
            return true
        }
        return false
    }

    override fun canEat(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun canDrink(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>): Boolean {
        return true
    }

    override fun handleObjectClick(player: Player, obj: GameObject, actionType: Int): Boolean {
        return false
    }

    override fun handleDeath(player: Player, killer: Optional<Player>): Boolean {
        return false
    }

    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}
    override fun defeated(player: Player, agent: Agent) {}

    override fun handleDeath(npc: NPC): Boolean {
        return false
    }

    companion object {
        private val jellyDialogue = DialogueBuilder()
                .setNpcChatHead(NpcID.JELLY_7518)
                .setExpression(DialogueExpression.CALM)
                .setText("Naughty human! You not hunting Kurask! Jelly's", "Kurasks only for people on Slayer tasks.")
        private val cave = Boundary(2690, 2810, 9989, 10044)
        private val extendedCave = Boundary(2691, 2721, 9955, 9989)
        private const val timerAttribute = "slayer_backoff_dialogue"
    }
}