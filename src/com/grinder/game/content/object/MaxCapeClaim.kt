package com.grinder.game.content.`object`

import com.grinder.game.content.`object`.MaxCapeClaim.COST
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.ExchangeMaxCape
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getMaxLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.Animation
import com.grinder.game.model.ObjectActions.ClickAction.Type.*
import com.grinder.game.model.ObjectActions.onClick
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueOptions
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.Executable
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import java.util.*

/**
 * Handles the max cape stand, sells the player a max cape
 * for [COST] if the player has achieved 99 in all skills.
 *
 * @author 2012
 */
object MaxCapeClaim {

    /**
     * The cost of the max cape
     */
    private val COST = Item(995, 250000000)

    /**
     * Handles the admire option of the cape mount for the [player].
     */
	fun admire(player: Player) {
        DialogueManager.sendStatement(player, "You feel the glimmer of absolution as you vividly stare at the cape..")
        player.performAnimation(Animation(2106))
    }

    /**
     * Handles the take option of the cape mount for the [player].
     */
    fun take(player: Player) {
        if (!player.inventory.contains(COST)) {
            player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.LEON_DCOUR }
                    .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
                    .ifPresent { leonNpc: NPC ->
                        player.positionToFace = leonNpc.position
                        leonNpc.setEntityInteraction(player)
                        leonNpc.say("Get your hands off this master piece " + player.username + "!")
                        TaskManager.submit(8) {
                            leonNpc.resetEntityInteraction()
                            leonNpc.handlePositionFacing()
                        }
                    }
            DialogueManager.sendStatement(player, "You need to have at least 250,000,000 coins to claim the Max cape.")
            return
        }
        DialogueManager.start(player, 2540)
        player.dialogueOptions = object : DialogueOptions() {
            override fun handleOption(player: Player, option: Int) {
                when (option) {
                    1 -> if (isMaxed(player)) {
                        DialogueManager.start(player, 2544)
                        player.dialogueOptions = object : DialogueOptions() {
                            override fun handleOption(player: Player, option: Int) {
                                when (option) {
                                    1 -> {
                                        DialogueManager.start(player, 2547)
                                        player.say("I'm better than you.. so where's my cape!?")
                                    }
                                    2 -> {
                                        DialogueManager.start(player, 2546)
                                        player.dialogueContinueAction = Executable {
                                            if (player.inventory.contains(COST)) {
                                                if (player.inventory.countFreeSlots() > 2) {
                                                    player.inventory.add(Item(13281), false)
                                                    player.inventory.add(Item(13342), false)
                                                    player.inventory.delete(COST, false)
                                                    player.inventory.refreshItems()
                                                    player.removeInterfaces()
                                                    AchievementManager.processFor(AchievementType.COLOR_MAX, player)
                                                } else {
                                                    player.message("You need at least 2 inventory spaces to claim the max cape.")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        DialogueManager.start(player, 2542)
                    }
                }
            }
        }
    }

    /**
     * Checking if the [player] has 99 in all skills but construction and hunter.
     */
    private fun isMaxed(player: Player): Boolean {
        for (skill in Skill.values()) {
            if (player.getMaxLevel(skill) < 99 && skill != Skill.CONSTRUCTION && skill != Skill.HUNTER) {
                player.message("You should consider training some " + skill.getName() + " before coming here again.")
                return false
            }
        }
        return true
    }

    init {
        onClick(ObjectID.MOUNTED_MAX_CAPE_2) {
            when(it.type){
                FIRST_OPTION -> admire(it.player)
                THIRD_OPTION -> ExchangeMaxCape.exchange(it.player, 0)
                SECOND_OPTION,
                FOURTH_OPTION -> take(it.player)
                else -> {}
            }
            true
        }
    }
}