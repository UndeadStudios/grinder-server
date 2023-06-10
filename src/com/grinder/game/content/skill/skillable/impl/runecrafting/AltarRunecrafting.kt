package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.impl.Mining
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.content.task_new.DailyTask
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.content.task_new.WeeklyTask
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sound
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

object AltarRunecrafting {

    /*
	 * Messages that are sent to the player while training Smithing skill
	 */

    /*
	 * Messages that are sent to the player while training Smithing skill
	 */
    private val RUNECRAFTING_MESSAGES = arrayOf(
        arrayOf("@whi@You can use Ourania altar for boosted experience and rune mixing."),
        arrayOf("@whi@Every equipped Decorative armor piece increases your experience gain in Runecrafting skill!"),
        arrayOf("@whi@You can take a Runecrafting skill task from your master for bonus rewards."),
        arrayOf("@whi@You can teleport to any altar using a talisman or The Abyss!"),
        arrayOf("@whi@Runecrafting with the skillcape equipped will give you 20% bonus experience gain!")
    )

    var currentMessage: String? = null

    fun sendSkillRandomMessages(player: Player) {
        currentMessage = RUNECRAFTING_MESSAGES[Misc.getRandomInclusive(RUNECRAFTING_MESSAGES.size - 1)][0]
        player.packetSender.sendMessage("<img=779> $currentMessage")
    }

    val TELEPORT_ANIM = Animation(827)

    val TELEPORT_SOUND = Sound(200)

    val CRAFT_ANIM = Animation(791)

    val CRAFT_SOUND = Sound(2710)

    val CRAFT_GRAPHIC = Graphic(186)

    /**
     *  Teleports the player to the [Altar]]
     */
    fun teleportToAltar(player: Player, talisman: Talisman) {

        // Check that the player has the talisman or tiara
        if(!talisman.playerHasTalisman(player)) {
            return
        }

        if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
            player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to do this.")
            return
        }

        // Perform the animation and move the player to inside the altar
        player.performAnimation(TELEPORT_ANIM)
        player.playSound(200)
        player.message("You feel a powerful force take hold of you...")

        AchievementManager.processFor(AchievementType.LOCATING_EYE, player)

        delayBy(3) {
            player.moveTo(talisman.altarPos)
        }
    }

    /**
     * Checks if a talisman was used on a mysterious ruin, and telepors if so.
     */
    fun handleItemOnObject(player: Player, item: Int, obj: Int): Boolean {
        val talisman = Talisman.getTalismanForRuin(abs(obj))
        talisman ?: return false

        // If the talisman is used on the correct altar, teleport the player
        return if(talisman.itemId == item) {
            teleportToAltar(player, talisman)
            true
        } else {
            false
        }
    }

    /**
     *  Remove essence, add runes and give the player experience.
     */
    fun craftRunes(player: Player, altar: Altar) {
        val essId = altar.rune.getEssenceId()

        // Find the amount of essence in the player's inventory
        var essence = player.inventory.getAmount(essId)

        // If the rune requires regular essence, allow pure essence as a substitute
        if(!altar.rune.pureEss) {
            essence += player.inventory.getAmount(ItemID.PURE_ESSENCE)
        }

        if(essence == 0) {
            val type = if(altar.rune.pureEss) "pure" else "rune"
            player.statement("You do not have any $type essence to bind.")
            return
        }

        if(player.getLevel(Skill.RUNECRAFTING) < altar.rune.requiredLevel()) {
            player.statement("You need a Runecrafting level of ${altar.rune.requiredLevel()} to craft this.")
            return
        }

        if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
            player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to do this.")
            return
        }

        player.BLOCK_ALL_BUT_TALKING = true
        delayBy(1) {
            player.performAnimation(CRAFT_ANIM)
            player.performGraphic(CRAFT_GRAPHIC)
            player.playSound(CRAFT_SOUND)
        }

        // Check if the player has a talisman to craft combination runes
        val talismans = Talisman.getTalismansInInventory(player)
        val comb = talismans
            .mapNotNull { CombinationRune.getCombination(altar, it) }
            .firstOrNull { player.getLevel(Skill.RUNECRAFTING) >= it.levelReq }


        delayBy(3) {
            // Delete the essence from the player's inventory
            player.inventory.deleteAll(essId)
            if (!altar.rune.pureEss) {
                player.inventory.deleteAll(ItemID.PURE_ESSENCE)
            }

            // Craft combination runes if possible
            if(comb != null) {
                val eleAmount = player.inventory.getAmount(comb.getOtherForAltar(altar).itemId)
                var amount = essence.coerceAtMost(eleAmount)

                // If the player is not wearing a binding necklace, fail ~half the time.
                if(!player.equipment.contains(ItemID.BINDING_NECKLACE)) {
                    amount = (amount / ThreadLocalRandom.current().nextDouble(1.25, 2.25)).toInt()
                }

                if(amount > 0) {
                    player.inventory.delete(Item(comb.getOthertalismanForAltar(altar).itemId, 1))
                }

                essence -= amount
                player.inventory.add(Item(comb.itemId, amount))
                player.inventory.delete(Item(comb.getOtherForAltar(altar).itemId, amount))
                player.skillManager.addExperience(Skill.RUNECRAFTING, altar.rune.exp * amount)
            }

            // Add the correct number of runes to the player's inventory
            val amount = essence * altar.rune.amountForLevel(player.getLevel(Skill.RUNECRAFTING))
            player.inventory.add(altar.rune.itemId, amount)

            // Skill task
            SkillTaskManager.perform(player, altar.rune.itemId, amount, SkillMasterType.RUNECRAFTING)

            PetHandler.onSkill(player, Skill.RUNECRAFTING)
            player.BLOCK_ALL_BUT_TALKING = false

            // Skill random messages while skilling

            // Skill random messages while skilling

            if (Misc.getRandomInclusive(5) == Misc.getRandomInclusive(5) && player.skillManager.getMaxLevel(Skill.AGILITY) < SkillUtil.maximumAchievableLevel()) {
                delayBy(3) {
                Mining.sendSkillRandomMessages(player)
            }
            }

            // Add experience
            player.skillManager.addExperience(Skill.RUNECRAFTING, altar.rune.exp * essence)
            player.message("You bind the temple's power into ${altar.name.toLowerCase()} runes.")

            player.points.increase(AttributeManager.Points.RUNES_CRAFTED, amount)
            PlayerTaskManager.progressTask(player, DailyTask.CRAFT_RUNES, amount)
            PlayerTaskManager.progressTask(player, WeeklyTask.CRAFT_RUNES, amount)
        }
    }

}