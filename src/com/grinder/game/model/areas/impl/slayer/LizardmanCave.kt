package com.grinder.game.model.areas.impl.slayer

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.Area
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import java.util.*
import java.util.concurrent.TimeUnit

class LizardmanCave : Area(cave) {
    override fun process(agent: Agent) {}

    override fun canTeleport(player: Player): Boolean {
        return true
    }

    override fun canAttack(attacker: Agent, target: Agent): Boolean {

        if (attacker.isPlayer && target.isNpc) {
            if (cave.contains(attacker.position)) {
                val npc = target.asNpc
                val pl = attacker.asPlayer
                return if (pl.slayer.task != null && pl.slayer.task.amountLeft > 0 && SlayerManager.isMonsterPartOfTask(pl, npc.fetchDefinition())
                    || SuperiorSlayerMonsters.forId(npc.id).isPresent) {
                    true
                } else {
                    if (pl.passedTime(timerAttribute, 10, TimeUnit.SECONDS, false))
                        captainCleiveDialogue.start(pl)
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

    override fun isMulti(agent: Agent): Boolean {
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
        when (obj.id) {
            /*27257 -> {
                player.sendMessage("The tunnel is too small for you to enter.")
                return true
            }
            30174, 30175 ->  {
                if (player.getLevel(Skill.AGILITY) < 72)
                    shortcutDialogue.start(player)
                return true
            }*/
        }
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
        private val captainCleiveDialogue = DialogueBuilder()
                .setNpcChatHead(7742)
                .setExpression(DialogueExpression.CALM)
                .setText("That's not your assigned Slayer target. In my cave,", "I expect people to focus on their Slayer training.")


        private val cave = Boundary(1280, 1341, 9937, 9982)
        private const val timerAttribute = "slayer_backoff_dialogue"
    }
}