package com.grinder.game.content.skill.skillable.impl.woodcutting

import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.degrading.DegradingType
import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.content.task_new.DailyTask
import com.grinder.game.content.task_new.PlayerTaskManager
import com.grinder.game.content.task_new.WeeklyTask
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.tryRandomEventTrigger
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   08/04/2020
 * @version 1.0
 */
class WoodCutting(
        private val treeObject: GameObject,
        private val treeType: TreeType) : DefaultSkillable() {

    override fun startAnimationLoop(player: Player) {
        val axe = findUsableAxe(player)?:return
        val animLoop: Task = object : Task(4, player, true) {
            override fun execute() {
                player.performAnimation(axe.animation)
            }
        }
        TaskManager.submit(animLoop)
        tasks.add(animLoop)
    }

    override fun startGraphicsLoop(player: Player?) {
    }

    override fun startSoundLoop(player: Player) {}
    override fun allowFullInventory() = false
    override fun loopRequirements() = true

    override fun onCycle(player: Player) {
    }

    fun handleTask(player: Player) {
        when(treeType) {
            TreeType.NORMAL -> PlayerTaskManager.progressTask(player, DailyTask.CHOP_LOGS)
            TreeType.WILLOW -> PlayerTaskManager.progressTask(player, DailyTask.CHOP_WILLOW_LOGS)
            TreeType.MAPLE -> PlayerTaskManager.progressTask(player, DailyTask.CHOP_MAPLE_LOGS)
            TreeType.YEW -> PlayerTaskManager.progressTask(player, DailyTask.CHOP_YEW_LOGS)
            TreeType.MAGIC -> PlayerTaskManager.progressTask(player, DailyTask.CHOP_MAGIC_LOGS)
        }
    }
    /**
     * Executes when player
     * @param player - Player
     */
    override fun finishedCycle(player: Player) {

        val axe = findUsableAxe(player)

        if(axe != null) {
            if (!infernalAxeEffect(player, axe))
                player.inventory.add(treeType.logId, 1)
        }

        getAchievements().forEach {
            AchievementManager.processFor(it, player)
        }

        player.sendMessage(getChopMessage())

        SkillTaskManager.perform(player, treeType.logId, 1, SkillMasterType.WOODCUTTING)
        handleTask(player)

        player.skillManager.addExperience(Skill.WOODCUTTING, treeType.xpReward)

        if(treeType == TreeType.VINES){
            World.deSpawn(treeObject)
            TaskManager.submit(treeType.respawnTimer) {
                World.addObject(treeObject)
            }
            player.motion.traceTo(getVineMovePosition(player))
            return
        }

        if(ignoreTreeRemoval())
            return

        val trunk = DynamicGameObject(1343, treeObject, true)

        World.deSpawn(treeObject)
        World.addObject(trunk)

        TaskManager.submit(getRespawnTimer()) {
            World.deSpawn(trunk)
            World.addObject(treeObject)
        }

        player.packetSender.sendAreaPlayerSound(2734)

        cancel(player)

        player.tryRandomEventTrigger(2f)
    }


    override fun cyclesRequired(player: Player): Int {
        val axeSpeed = findUsableAxe(player)?.speed?.toInt()?:0
        var cycles = treeType.cycles + Misc.getRandomInclusive(3)
        cycles -= player.skillManager.getMaxLevel(Skill.WOODCUTTING) * 0.1.toInt()
        cycles -= cycles * axeSpeed
        if (cycles < 3)
            cycles = 1 + Misc.getRandomInclusive(2)
        return cycles
    }

    /**
     * Checks if we have Woodcutting requirements
     * @see DefaultSkillable.hasRequirements
     */
    override fun hasRequirements(player: Player): Boolean {
        var axe = findAxe(player)

        if(axe == null)
            player.sendMessage("You need an axe to chop the ${getTreeName()}", 1000)
        else {
            axe = findUsableAxe(player)
            when {
                axe == null -> player.sendMessage("You do not have an axe which you have the Woodcutting level to use.", 1000)
                !canChopTree(player) -> player.sendMessage("You need a Woodcutting level of ${treeType.requiredLevel} to chop down this tree.")
                else -> return World.isSpawned(treeObject)
                        && super.hasRequirements(player)
            }
        }
        return false
    }

    private fun infernalAxeEffect(player: Player, axeType: AxeType): Boolean {
        if(axeType == AxeType.INFERNAL && Misc.randomChance(25f)){
            player.performGraphic(Graphic(86))
            player.skillManager.addExperience(Skill.FIREMAKING, treeType.burnXpReward)

            if(player.equipment.contains(axeType.id))
                player.itemDegradationManager.degrade(DegradingType.SKILLING, -1)
            else
                player.itemDegradationManager.degradeInventoryItems(DegradingType.SKILLING, -1, axeType.id)
            return true
        }
        return false
    }

    private fun getChopMessage() = when(treeType) {
        TreeType.VINES -> "You chop off the tangling vines."
        else -> "You get some ${ItemDefinition.forId(treeType.logId).name}."
    }

    private fun findUsableAxe(player: Player) = AxeType.values().findLast {
        (player.equipment.contains(it.id) || player.inventory.contains(it.id))
                &&   player.skillManager.getMaxLevel(Skill.WOODCUTTING) >= it.requiredLevel
    }

    private fun findAxe(player: Player) = AxeType.values().findLast {
        (player.equipment.contains(it.id) || player.inventory.contains(it.id))
    }

    private fun getAchievements() = when(treeType){
        TreeType.NORMAL -> arrayOf(AchievementType.CHOP_CHOP)
        TreeType.WILLOW -> arrayOf(AchievementType.CHOPPING_AWAY)
        TreeType.MAGIC -> arrayOf(AchievementType.TREES_ARE_LIFE, AchievementType.AXE_DOES_IT)
        else -> emptyArray()
    }

    private fun getTreeName() = when(treeType) {
        TreeType.VINES -> "vines"
        else -> "trees"
    }

    private fun canChopTree(player: Player) = treeType.requiredLevel <= player.skillManager.getMaxLevel(Skill.WOODCUTTING)

    private fun getRespawnTimer() = treeType.respawnTimer + Misc.getRandomExclusive(3) / 2

    private fun ignoreTreeRemoval() = treeType.isMulti && Misc.getRandomInclusive(treeType.cycles * 2) > 2

    private fun getVineMovePosition(player: Player) = when {
        treeObject.position == Position(2690, 9564, 0) && player.position.x >= 2691 -> Position(2689, 9564, 0)
        treeObject.position == Position(2690, 9564, 0) && player.position.x <= 2689 -> Position(2691, 9564, 0)
        treeObject.position == Position(2683, 9569, 0) && player.position.y <= 9568 -> Position(2683, 9570, 0)
        treeObject.position == Position(2683, 9569, 0) && player.position.y >= 9570 -> Position(2683, 9568, 0)
        treeObject.position == Position(2673, 9499, 0) && player.position.x >= 2674 -> Position(2672, 9499, 0)
        treeObject.position == Position(2673, 9499, 0) && player.position.x <= 2672 -> Position(2674, 9499, 0)
        treeObject.position == Position(2694, 9482, 0) && player.position.x >= 2695 -> Position(2693, 9482, 0)
        treeObject.position == Position(2694, 9482, 0) && player.position.x <= 2693 -> Position(2695, 9482, 0)
        treeObject.position == Position(2675, 9479, 0) && player.position.x >= 2676 -> Position(2674, 9479, 0)
        treeObject.position == Position(2675, 9479, 0) && player.position.x <= 2674 -> Position(2676, 9479, 0)
        else -> treeObject.position
    }

}