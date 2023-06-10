package com.grinder.game.content.item

import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc

/**
 * Handles the custom skilling tones that reward 1-10m experience
 * in the respective skill.
 */
object SkillingTomes {

    // custom item id, cannot use ItemIdentifiers
    private val map = mapOf(
            15272 to Triple(Skill.HUNTER, 10_000_000, 1387),
            15273 to Triple(Skill.HUNTER, 1_000_000, 1387),
            7779 to Triple(Skill.FISHING, 10_000_000, 568),
            7780 to Triple(Skill.FISHING, 1_000_000, 1389),
            7793 to Triple(Skill.MINING, 1_000_000, 1389),
            7795 to Triple(Skill.FIREMAKING, 1_000_000, 1389),
            7795 to Triple(Skill.FIREMAKING, 10_000_000, 568),
            7796 to Triple(Skill.FIREMAKING, 10_000_000, 568),
            7785 to Triple(Skill.THIEVING, 10_000_000, 1389),
            7786 to Triple(Skill.THIEVING, 5_000_000, 1389),
            7787 to Triple(Skill.THIEVING, 1_000_000, 1389),
            7782 to Triple(Skill.AGILITY, 10_000_000, 568),
            7783 to Triple(Skill.AGILITY, 1_000_000, 1389),
            7789 to Triple(Skill.SLAYER, 1_000_000, 1389),
            7788 to Triple(Skill.SLAYER, 10_000_000, 1389),
            7797 to Triple(Skill.WOODCUTTING, 10_000_000, 568),
            7798 to Triple(Skill.WOODCUTTING, 1_000_000, 1389),
            7791 to Triple(Skill.MINING, 10_000_000, 568))
    init {
        CommandActions.onCommand("skilltomes", PlayerRights.HIGH_STAFF) {
            for (id in map.keys)
                player.addInventoryItem(Item(id, 1), -1)
            player.inventory.refreshItems()
            return@onCommand true
        }
        onFirstInventoryAction(*map.keys.toIntArray()) {
            val entry = map[getItemId()]?:return@onFirstInventoryAction
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                val tomeItem = getItem()?:return@onFirstInventoryAction
                DialogueBuilder(DialogueType.STATEMENT)
                        .setText("Are you use you want to consume this item?",
                                "Once you do, you wont be able to undo this.")
                        .add(DialogueType.OPTION)
                        .firstOption("Yes I'am sure!") {
                            if (it.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                                it.removeInterfaces()
                                it.resetInteractions()
                                it.performAnimation(Animation(1309))
                                it.motion.update(MovementStatus.DISABLED)
                                TaskManager.submit(3) {
                                    it.motion.update(MovementStatus.NONE)
                                    if (it.removeInventoryItem(tomeItem)) {
                                        val skill = entry.first
                                        val skillName = skill.getName()
                                        val experienceAmount = entry.second
                                        val gfxId = entry.third
                                        it.performGraphic(Graphic(gfxId))
                                        it.addExperience(entry.first, experienceAmount, true)
                                        it.playSound(Sounds.USING_LAMP_REWARD)
                                        it.message("If we would have new knowledge, we must get a whole world of new questions. - S. Langer")
                                        it.packetSender.sendJinglebitMusic(134, 0)
                                        it.statement("<img=${skill.imageIcon}> You have been rewarded with ${Misc.formatWithAbbreviation2(experienceAmount.toLong())} $skillName skill experience.")
                                    }
                                }
                            }
                        }
                        .addCancel("No, I have changed my mind.")
                        .start(player)
            }
        }
    }

}