package com.grinder.game.content.item

import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.hasAttribute
import com.grinder.game.entity.removeAttribute
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.ButtonActions.onClick
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.consumable.ConsumableUtil
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.NpcID
import kotlin.math.max
import kotlin.random.Random

object MorphItems {

    init {

        onClick(6020, ignoreRestriction = true) {
            player.unblock()
            player.removeAttribute(Attribute.IS_ANY_NPC)
            player.resetTransformation()
            player.motion.update(MovementStatus.NONE)
            player.packetSender.sendTabs()
        }

        onFirstInventoryAction(
                ItemID.BEER,
                ItemID.BEER_3) {
            val beerItem = getItem()?:return@onFirstInventoryAction
            if (player.canConsumeDrink(beerItem, allowInWilderness = false)){

                if (player.isDemon()){
                    player.message("You can't drink this as a demon.")
                    return@onFirstInventoryAction
                }

                if (player.replaceInventoryItem(beerItem, getEmptyGlass(beerItem), 1))
                    consumeBeer(player)
            }
        }

        onFirstInventoryAction(ItemID.WIZARDS_MIND_BOMB) {
            val beerItem = getItem()?:return@onFirstInventoryAction
            if (player.canConsumeDrink(beerItem,
                            allowInWilderness = false,
                            allowWhileTransformed = false)){
                if (player.replaceInventoryItem(beerItem, getEmptyGlass(beerItem), 1)) {
                    consumeMindBomb(player)
                }
            }
        }

        onThirdInventoryAction(ItemID.RING_OF_CHAROS_A_) {
            handleRingOfCharosA(player)
        }
        onSecondContainerEquipmentAction(ItemID.RING_OF_CHAROS_A_) {
            handleRingOfCharosA(player)
        }

        onEquipAction(ItemID.RING_OF_STONE) {
            morph(player, NpcID.ROCKS_2188)
        }
        onEquipAction(ItemID.MONKEY_TALISMAN) {
            morph(player, NpcID.MONKEY)
        }
        onEquipAction(ItemID.EASTER_RING) {
            morph(player, NpcID.EGG + Random.nextInt(5))
        }
        onEquipAction(ItemID.RING_OF_NATURE) {
            morph(player, 7314) // bush
        }
        onEquipAction(ItemID.RING_OF_COINS) {
            morph(player, 7315) // monkey guard
        }
    }

    fun morph(player: Player, npcId: Int) {

        if (AreaManager.DuelFightArena.contains(player))
            return

        if (player.notTransformed() && player.notInDangerOrAfkOrBusyOrInteracting()){
            player.resetInteractions()
            if (npcId != NpcID.MONKEY) {
                player.motion.update(MovementStatus.DISABLED)
                player.block()
            }  else
                player.setBoolean(Attribute.IS_ANY_NPC, true)
            player.packetSender.sendTab(3)
            player.packetSender.sendTabInterface(3, 6014)
            player.npcTransformationId = npcId
        }
    }

    private fun consumeMindBomb(player: Player) {
        player.resetInteractions()

        ConsumableUtil.onDrink(player)
        TaskManager.submit(2) {
            player.say("RAAAAAAAAAARRRRRRGHHH!!!!!")
        }

        player.progressAchievement(AchievementType.TRANSMOGRIFIED)
        TaskManager.submit(4) {
            resetRunning(player)
            TaskManager.submit(createNpcTransformTask(player,
                    duration = 20,
                    movementAnimationId = 66,
                    npcTransformId = 7584,
                    attributeKey = Attribute.IS_DEMON,
                    stopMessage = "The effect fades away..and you're back to a human."))
        }
    }

    private fun consumeBeer(player: Player) {
        player.resetInteractions()

        val currentHealth = player.getLevel(Skill.HITPOINTS)
        val maxHealth = player.getMaxLevel(Skill.HITPOINTS)
        val restoreHealth = max(0, maxHealth - currentHealth)
        player.increaseLevel(Skill.HITPOINTS, 2 + Misc.random(5))

        ConsumableUtil.onDrink(player)

        if (player.isDrunk())
            player.message("@dre@You get even more drunk...")
        else {
            player.message("@dre@You feel dizzy...")
            player.setBoolean(Attribute.IS_DRUNK, true)
        }

        resetRunning(player)

        TaskManager.submit(createDrunkTask(player))
    }

    private fun handleRingOfCharosA(player: Player) {
        if (player.dueling.inDuel()) {
            player.message("You're not allowed to transform during a duel.")
            return
        }

        if (player.notTransformed(blockNpcOnly = false) && player.notInDangerOrAfkOrBusyOrInteracting()) {
            player.resetInteractions()
            player.playSound(Sounds.DEATH_SOUND)
            player.performAnimation(Animation(734))
            player.block()
            TaskManager.submit(4) {
                player.say("What!?? What is happening..!?")
                player.unblock()
                player.progressAchievement(AchievementType.HARD_AS_ROCK)
                resetRunning(player)
                TaskManager.submit(createNpcTransformTask(player,
                        duration = 5,
                        movementAnimationId = 1310,
                        npcTransformId = Random.nextInt(100, 101),
                        attributeKey = Attribute.IS_CRAB,
                        stopMessage = "The fairy transformation effect has disappeared."))
            }
        }
    }

    fun resetRunning(player: Player) {
        if (player.isRunning) {
            player.isRunning = false
            player.packetSender.sendRunStatus()
            if (player.isDrunk())
                player.message("It is advised not to run when you're drunk.")
        }
    }

    private fun createNpcTransformTask(player: Player,
                                       duration: Int,
                                       movementAnimationId: Int,
                                       npcTransformId: Int,
                                       attributeKey: String,
                                       stopMessage: String): Task {
        return object : Task(1, player, true) {
            var ticks = 0
            init {
                player.setBoolean(attributeKey, true)
            }
            override fun execute() {

                if (!player.getBoolean(attributeKey) || ++ticks == duration) {
                    stop()
                    return
                }
                if (!player.isMorphed)
                    player.npcTransformationId = npcTransformId
                if (!player.motion.notCompleted()) {
                    player.setBas(movementAnimationId)
                    player.updateAppearance()
                }
            }

            override fun stop() {
                super.stop()
                player.message(stopMessage)
                player.removeAttribute(attributeKey)
                resetMorph(player)
            }
        }
    }


    private fun createDrunkTask(player: Player): Task {
        return object : Task(1, player, true) {
            var drunkTicks = 0
            override fun execute() {
                if (!player.isDrunk() || ++drunkTicks == 60) {
                    stop()
                    return
                }
                if (!player.motion.notCompleted()) {
                    player.setBas(3040)
                    player.updateAppearance()
                }
            }

            override fun stop() {
                super.stop()
                player.message("You're no longer drunk!")
                player.removeAttribute(Attribute.IS_DRUNK)
                resetMorph(player)
            }
        }
    }

    fun resetMorph(player: Player) {
        player.resetTransformation()
        player.resetBas()
        player.performAnimation(Animation(65535))
        player.updateAppearance()
    }

    private fun getEmptyGlass(item: Item) = Item(when (item.id) {
        ItemID.BEER_3 -> ItemID.BEER_GLASS_4
        else -> ItemID.BEER_GLASS
    }, 1)

    fun Player.isFlying() = hasAttribute(Attribute.IS_FLYING) && getBoolean(Attribute.IS_FLYING)

    fun Player.isCrab() = hasAttribute(Attribute.IS_CRAB) && getBoolean(Attribute.IS_CRAB)

    fun Player.isDemon() = hasAttribute(Attribute.IS_DEMON) && getBoolean(Attribute.IS_DEMON)

    fun Player.isDrunk() = hasAttribute(Attribute.IS_DRUNK) && getBoolean(Attribute.IS_DRUNK)

    fun Player.isAnyNPC() = hasAttribute(Attribute.IS_ANY_NPC) && getBoolean(Attribute.IS_ANY_NPC)

    fun Player.notTransformed(action: String = "do this", message: Boolean = true, blockNpcOnly: Boolean = false): Boolean {
        if (!blockNpcOnly) {
            if (isDrunk()) {
                if (message)
                    message("You can't $action whilst drunk.")
                return false
            }
            if (isFlying()) {
                if (message)
                    message("You can't $action whilst flying.")
                return false
            }
        }
        if (isAnyNPC()){
            if (message)
                message("You can't $action whilst being a npc.")
            return false
        }
        if (isDemon()) {
            if (message)
                message("You can't $action whilst being a demon.")
            return false
        }
        if (isCrab()) {
            if (message)
                message("You can't $action whilst being a crab.")
            return false
        }
        return true
    }
}