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
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.oldgrinder.EquipSlot
import java.util.*
import java.util.concurrent.TimeUnit

class SlayerTowerArea : Area(tower, basement) {

    override fun process(agent: Agent) {}

    override fun canTeleport(player: Player) = true

    // basement is slayer task only
    override fun canAttack(attacker: Agent, target: Agent): Boolean {

        if (target is NPC && (basement.contains(attacker.position) || basement.contains(target.position))) {
            if (attacker is Player) {
                val npc = target.asNpc
                val pl = attacker.asPlayer
                return if (pl.slayer.task != null && pl.slayer.task.amountLeft > 0 && SlayerManager.isMonsterPartOfTask(pl, npc.fetchDefinition())
                    || SuperiorSlayerMonsters.forId(npc.id).isPresent) {
                    true
                } else {
                    if (attacker.passedTime(timerAttribute, 10, TimeUnit.SECONDS, false)) {
                        raulnDialogue.setText(
                            if (attacker.equipment.containsAtSlot(EquipSlot.AMULET, ItemID.GHOSTSPEAK_AMULET))
                                "I don't think that's what you're meant to be slaying."
                            else
                                "wooooo ooo woo-woo"
                        ).start(attacker)
                    }
                    attacker.sendMessage("Raulyn wants you to stick to your Slayer assignments.")
                    false
                }
            }
        }
        return !(attacker.isPlayer && target.isPlayer)
    }

    override fun canTrade(player: Player, target: Player) = true

    override fun isMulti(agent: Agent) = agent is BasicDeathSpawn || agent is Nechryael

    override fun canEat(player: Player, itemId: Int) = true

    override fun canDrink(player: Player, itemId: Int) = true

    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>) = true

    override fun handleObjectClick(player: Player?, obj: GameObject, actionType: Int): Boolean {
        when (obj.id) {
            // Magical chest (Items go here on death)
            31675 -> {
                player?.sendMessage("The chest seems magically empty.")
                return true
            }
            // You don't have anything to unlock the gateway with
        }
        return false
    }

    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}
    override fun defeated(player: Player, agent: Agent) {}

    override fun handleDeath(player: Player, killer: Optional<Player>) = false
    override fun handleDeath(npc: NPC) = false

    companion object {
        private val raulnDialogue = DialogueBuilder()
                .setNpcChatHead(NpcID.RAULYN)
                .setExpression(DialogueExpression.CALM)
        private val tower = Boundary(3400, 3455, 3530, 3580)
        private val basement = Boundary(3400, 3450, 9926, 9981)
        private const val timerAttribute = "slayer_backoff_dialogue"
    }
}