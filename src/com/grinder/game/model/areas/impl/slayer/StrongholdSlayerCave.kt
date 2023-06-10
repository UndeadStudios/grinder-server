package com.grinder.game.model.areas.impl.slayer

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.slayer.BasicDeathSpawn
import com.grinder.game.entity.agent.npc.slayer.Nechryael
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Boundary
import com.grinder.game.model.Skill
import com.grinder.game.model.areas.Area
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.ItemID
import java.util.*
import java.util.concurrent.TimeUnit

class StrongholdSlayerCave : Area(cave) {
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
                        steveDialogue.start(pl)
                    pl.sendMessage("Steve wants you to stick to your Slayer assignments.")

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
        when (obj.id) {
            27257 -> {
                player.sendMessage("The tunnel is too small for you to enter.")
                return true
            }
            30174, 30175 ->  {
                if (player.getLevel(Skill.AGILITY) < 72)
                    shortcutDialogue.start(player)
                return true
            }
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
        private val steveDialogue = DialogueBuilder()
                .setNpcChatHead(6798)
                .setExpression(DialogueExpression.CALM)
                .setText("That's not your assigned Slayer target. In my cave,", "I expect people to focus on their Slayer training.")

        private val shortcutDialogue = DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("That tunnel gets very narrow.", "I don't think I can squeeze through.")
                .setNext(
                        DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                            .setItem(ItemID.AGILITY_CONTORTION_2, 200)
                            .setText("You'll need an Agility level of 72 to get through there."))
        private val cave = Boundary(2380, 2495, 9765, 9840)
        private const val timerAttribute = "slayer_backoff_dialogue"
    }
}