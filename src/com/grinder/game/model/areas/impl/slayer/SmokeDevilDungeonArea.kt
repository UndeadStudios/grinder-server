package com.grinder.game.model.areas.impl.slayer

import com.grinder.game.World
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.Area
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import java.util.*
import java.util.concurrent.TimeUnit

class SmokeDevilDungeonArea : Area(cave) {


    override fun process(agent: Agent) {
        if (agent is Player) {
            val player = agent.getAsPlayer()
            if (World.tick % DAMAGE_INTERVAL == 0 && !isProtected(player) && player.hitpoints > 1) {
                var damage = 20
                if (player.hitpoints - damage < 1) damage = player.hitpoints - 1
                player.combat.queue(Damage.create(damage))
                player.say("*choke*")
            }
        }
    }
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

                    if (npc.id == NpcID.THERMONUCLEAR_SMOKE_DEVIL)
                        pl.sendMessage("The Thermonuclear smoke devil can only be attacked on a smoke devil task.")

                    false
                }
            }
        }
        return !(attacker.isPlayer && target.isPlayer)
    }

    override fun enter(agent: Agent?) {
        if(agent is Player){
            super.enter(agent)
            agent.packetSender.sendWalkableInterface(16152)
        }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player){
            super.leave(agent)
            agent.packetSender.sendWalkableInterface(-1)
            QuestManager.despawnNpcs(agent)
        }
    }

    override fun canTrade(player: Player, target: Player): Boolean {
        return true
    }

    /**
     * Determines if the player is protected from the smoke in this dungeon.
     *
     * @param player The player.
     * @return `true` if protected
     */
    private fun isProtected(player: Player): Boolean {
        return player.equipment.containsAtSlot(
            EquipmentConstants.HEAD_SLOT,
            ItemID.GAS_MASK
        ) || EquipmentUtil.isSmokeProtect(player.equipment)
    }

    override fun isMulti(agent: Agent): Boolean {
        return true
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
        /**
         * The damage interval.
         */
        private val captainCleiveDialogue = DialogueBuilder()
            .setNpcChatHead(7654)
            .setExpression(DialogueExpression.CALM)
            .setText("*cough* that isn't your assignment *wheeze*")
        private const val DAMAGE_INTERVAL = 20
        private val cave = Boundary(2325, 2435, 9410, 9470)
        private const val timerAttribute = "slayer_backoff_dialogue"
    }
}