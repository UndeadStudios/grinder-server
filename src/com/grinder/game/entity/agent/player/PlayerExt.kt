package com.grinder.game.entity.agent.player

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.miscellaneous.randomevent.RandomEvents
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentType
import com.grinder.game.entity.agent.player.event.PlayerEvent
import com.grinder.game.entity.agent.player.event.PlayerEventListener
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.nameAndQuantity
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.Misc
import com.grinder.util.timing.TimerKey
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.math.min
import kotlin.random.Random

/**
 * This file contains extension methods of the Player class.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */


fun Player.progressAchievement(vararg achievementTypes: AchievementType) {
    achievementTypes.forEach {
        AchievementManager.processFor(it, this)
    }
}

fun Player.subscribe(handler: (PlayerEvent) -> Boolean) {
    subscribe(object : PlayerEventListener {
        override fun on(event: PlayerEvent): Boolean {
            return handler.invoke(event)
        }
    })
}

fun Player.resetInteractions(motion: Boolean = true, combat: Boolean = false){
    if (motion) {
        this.motion.reset()
        this.motion.resetTargetFollowing()
        packetSender.sendMinimapFlagRemoval()
    }
    if (combat)
        this.combat.reset(false)
}


fun Player.notInDangerOrAfkOrBusyOrInteracting(): Boolean {
    if (combat.isInCombat) {
        message("You can't do this while in combat.")
        return false
    }
    if (wildernessLevel > 0) {
        message("You can't do this while in the wilderness.")
        return false
    }
    return notAfkOrBusyOrInteracting()
}

fun Player.notAfkOrBusyOrInteracting(): Boolean {
    if (busy()) {
        message("You can't do that when you are busy.")
        return false
    }
    if (illegalAction(
                    PlayerStatus.AWAY_FROM_KEYBOARD,
                    PlayerStatus.TRADING,
                    PlayerStatus.DUELING,
                    PlayerStatus.DICING))
        return false
    return true
}

fun Player.canConsumeDrink(item: Item,
                           allowInWilderness: Boolean = true,
                           allowWhileTransformed: Boolean = true
): Boolean {
    return canConsumeDrink(item.id, allowInWilderness, allowWhileTransformed)
}

fun Player.canConsumeDrink(itemId: Int,
                           allowInWilderness: Boolean = true,
                           allowWhileTransformed: Boolean = true
): Boolean {
    if (dueling.selectedRule(DuelRule.NO_FOOD)){
        message("You're not allowed to drink during this duel.")
        return false
    }
    if (area?.canDrink(this, itemId) == false){
        message("You can't use potions here.")
        return false
    }
    if (!allowInWilderness && wildernessLevel > 0) {
        message("You can't drink this in the wilderness.")
        return false
    }
    if (!allowWhileTransformed && !notTransformed("drink", blockNpcOnly = false))
        return false
    if (isJailed) {
        message("You can't drink when you're in jail.")
        return false
    }
    if (timerRepository.has(TimerKey.STUN)) {
        message("You're currently stunned and can't drink.")
        return false
    }
    if (timerRepository.has(TimerKey.POTION))
        return false
    return true
}

fun Player.illegalAction(vararg states: PlayerStatus): Boolean{
    return illegalAction("do this", *states)
}

fun Player.illegalAction(action: String, vararg states: PlayerStatus): Boolean{
    if(states.contains(status)){
        var message = "You can't $action"
        status.optionalIdentifier().ifPresent {
            message += " while $it"
        }
        message += "!"
        message(message)
        return true
    }
    return false
}

fun Player.tryRandomEventTrigger(odds: Float = 2.8F): Boolean {
    if (passedTime(Attribute.RANDOM_EVENT_PUZZLE, 10, TimeUnit.MINUTES, updateIfPassed = false, message = false)) {
        if (Misc.randomChance(odds)) {
            if (Random.nextBoolean() && wildernessLevel <= 0) {
                RandomEvents.triggerPuzzle(this)
            } else {
                RandomEvents.triggerRefreshments(this)
            }
            return true
        }
    }
    return false
}

fun Player.achievementProgress(type: AchievementType) = achievements.progress[type.ordinal]

fun Player.resetSkill(skill: Skill, update: Boolean = true){
    skillManager.setDefaults(skill, update)
}
fun Player.decreaseLevel(skill: Skill, amount: Int, update: Boolean = true){
    skillManager.setCurrentLevel(skill, skillManager.getCurrentLevel(skill) - amount, update)
}
fun Player.increaseLevel(skill: Skill, amount: Int, update: Boolean = true, capMax: Int = getMaxLevel(skill)){
    val level = min(getLevel(skill)+amount, capMax)
    skillManager.setCurrentLevel(skill, level, update)
}
fun Player.getLevel(skill: Skill) = skillManager.getCurrentLevel(skill)
fun Player.getMaxLevel(skill: Skill) = skillManager.getMaxLevel(skill)
fun Player.getLevelMissing(skill: Skill) = getMaxLevel(skill) - getLevel(skill)
fun Player.checkLevel(skill: Skill, requiredLevel: Int) : Boolean {
    if (getMaxLevel(skill) < requiredLevel){
        message("You need a ${skill.getName()} level of $requiredLevel to do this.")
        return false
    }
    return true
}

fun Player.addExperience(skill: Skill, amount: Int, noMultipliers: Boolean = false){
    if (noMultipliers)
        skillManager.addFixedExperience(skill, amount)
    else
        skillManager.addExperience(skill, amount)
}

fun Player.hasSkillLevel(skill: Skill, minimumLevel: Int, action: String = "to do this", message: Boolean = true): Boolean {
    val passed = skillManager.getCurrentLevel(skill) >= minimumLevel
    if(!passed && message){
        message("You need a ${skill.getName()} level of $minimumLevel $action.")
    }
    return passed
}

fun Player.checkFreeInventorySlots(requiredSlot: Int = 1, message: Boolean = true): Boolean{
    if (inventory.countFreeSlots() < requiredSlot){
        if (message)
            message("You need at least $requiredSlot free inventory slots in order to do this.")
        return false
    }
    return true
}

fun Player.updateInventoryDelayed(delay: Int = 0){
    updateContainerDelayed(inventory, delay)
}

private fun updateContainerDelayed(container: ItemContainer, delay: Int) {
    if (delay == 0) {
        container.refreshItems()
    } else if (delay > 0) {
        TaskManager.submit(delay) {
            container.refreshItems()
        }
    }
}
fun Player.tempBlockMovement(ticks: Int){
    motion.update(MovementStatus.DISABLED)
    TaskManager.submit(ticks) {
        motion.update(MovementStatus.NONE)
    }
}
fun Player.block(blockDisconnect: Boolean = false, blockMovement: Boolean = false){
    BLOCK_ALL_BUT_TALKING = true
    if (blockDisconnect)
        setBlockLogout(true)
    if (blockMovement)
        motion.update(MovementStatus.DISABLED)
}
fun Player.unblock(unblockDisconnect: Boolean = false, unblockMovement: Boolean = false) {
    BLOCK_ALL_BUT_TALKING = false
    if (unblockDisconnect)
        setBlockLogout(false)
    if (unblockMovement)
        motion.update(MovementStatus.NONE)
}
fun Player.removeInterfaces(){
    packetSender.sendInterfaceRemoval()
}
fun Player.setInterfaceConfig(child: Int, state: Int){
    packetSender.sendConfig(child, state)
}
fun Player.openInterface(id: Int) {
    packetSender.sendInterface(id)
}
fun Player.countQuantityInAccount(itemId: Int): Long {
    var total = 0L
    total += countQuanity(inventory, itemId)
    total += countQuanity(equipment, itemId)
    total += lootingBag?.container?.let { countQuanity(it, itemId) }?:0
    for(bank in banks){
        total +=bank?.let { countQuanity(bank, itemId) }?:0
    }
    return total
}

fun countQuanity(container: ItemContainer, itemId: Int) = container.getAmount(itemId)

fun Player.hasItemInInventory(item: Item, action: String = "to do this", message: Boolean = true): Boolean{
    if(inventory.contains(item))
        return true
    if(message)
        message("You need ${item.nameAndQuantity()} $action.")
    return false
}
fun Player.hasItemsInInventory(vararg items: Item, idenitifer: String = "items", action: String = "to do this", message: Boolean = true): Boolean{
    if(inventory.contains(items))
        return true
    if(message)
        message("You don't have the $idenitifer required $action.")
    return false
}
fun Player.addInventoryItem(item: Item, updateDelay: Int = 0): Boolean {
    return addContainerItem(inventory, item, updateDelay)
}
fun Player.addInventoryItems(items: Collection<Item>, updateDelay: Int = 0): Boolean {
    var addedItemsCount = 0
    for (item in items){
        if (addInventoryItem(item, -1))
            addedItemsCount++
    }
    queueContainerUpdate(updateDelay, inventory)
    if (addedItemsCount != items.size){
        sendDevelopersMessage("Could not add ${items.size-addedItemsCount} out of ${items.size} items in container.")
        return false
    }
    return true
}
fun addContainerItem(container: ItemContainer, item: Item, updateDelay: Int = 0): Boolean {
    if (!container.canHold(item)) {
        container.player?.sendDevelopersMessage("Could not add item in container {$item}")
        return false
    }
    container.add(item, false)
    queueContainerUpdate(updateDelay, container)
    return true
}
fun Player.removeInventoryItem(toFind: Item, updateDelay: Int = 0): Boolean {
    return removeContainerItem(inventory, toFind, updateDelay)
}
fun Player.removeEquipmentItem(toFind: Item, updateDelay: Int = 0): Boolean {
    return removeContainerItem(equipment, toFind, updateDelay)
}
private fun removeContainerItem(container: ItemContainer, toFind: Item, updateDelay: Int): Boolean {
    if (!container.contains(toFind)) {
        container.player?.sendDevelopersMessage("Could not remove item in container {$toFind}")
        return false
    }
    container.delete(toFind, false)
    queueContainerUpdate(updateDelay, container)
    return true
}

fun Player.setInventoryItem(slot: Int, toSet: Item, updateDelay: Int = 0) : Boolean {
    return setContainerItem(inventory, slot, toSet, updateDelay)
}

private fun setContainerItem(container: ItemContainer, slot: Int, toSet: Item, updateDelay: Int): Boolean {
    container.set(slot, toSet)
    queueContainerUpdate(updateDelay, container)
    return true
}

fun Player.replaceInventoryItem(toFind: Item, toReplace: Item, updateDelay: Int = 0) : Boolean {
    return replaceContainerItem(inventory, toFind, toReplace, updateDelay)
}
fun Player.replaceEquipmentItem(toFind: Item, toReplace: Item, updateDelay: Int = 0, updateStats: Boolean = true) : Boolean {
    if(replaceContainerItem(equipment, toFind, toReplace, updateDelay)){
        if(updateStats) {
            val toFindEquipType = toFind.definition.equipmentType
            val toReplaceEquipType = toReplace.definition.equipmentType
            if (toFindEquipType == EquipmentType.WEAPON
                    || toFindEquipType == EquipmentType.SHIELD
                    || toReplaceEquipType == EquipmentType.WEAPON
                    || toReplaceEquipType == EquipmentType.SHIELD) {
                EquipPacketListener.resetWeapon(this)
                WeaponInterfaces.assign(this)
            }
            EquipmentBonuses.update(this)
        }
        return true
    }
    return false
}
private fun replaceContainerItem(container: ItemContainer, toFind: Item, toReplace: Item, updateDelay: Int): Boolean {
    if (!container.contains(toFind)) {
        container.player?.sendDevelopersMessage("Could not replace item in inventory {$toFind} {$toReplace}")
        return false
    }

    container.set(container.getSlot(toFind), toReplace)

    queueContainerUpdate(updateDelay, container)
    return true
}

private fun queueContainerUpdate(updateDelay: Int, container: ItemContainer) {
    if (updateDelay > 0)
        updateContainerDelayed(container, updateDelay)
    else if (updateDelay == 0)
        container.refreshItems()
}

fun Player.createGroundItem(item: Item){
    createGroundItem(item, position)
}
fun Player.createGroundItem(item: Item, position: Position){
    ItemOnGroundManager.register(this, item, position.clone())
}
fun Player.playSound(sound: Sound){
    playSound(sound.id, sound.delay, sound.loopCount)
}
fun Player.playSound(id: Int, delay: Int = 0, playCount: Int = 1){
    packetSender.sendSound(id, delay, playCount)
}
fun Player.playAreaSound(sound: AreaSound){
    playAreaSound(sound.id, sound.radius, sound.loopCount, sound.delay)
}
fun Player.playAreaSound(id: Int, radius: Int = 12, playCount: Int = 1, delay: Int = 0){
    packetSender.sendAreaPlayerSound(id, radius, playCount, delay)
}
fun Player.playAreaSound(id: Int, entity: Entity, playCount: Int = 1, delay: Int = 0){
    packetSender.sendAreaPlayerSound(id, entity.size, playCount, delay)
}
fun Player.message(contents: String){
    message(contents, Color.NONE, 0)
}
fun Player.message(contents: String, color: Color){
    message(contents, color, 0)
}
fun Player.message(contents: String, tickDelay: Int){
    message(contents, Color.NONE, tickDelay)
}
fun Player.message(contents: String, color: Color = Color.NONE, tickDelay: Int = 0){
    if(tickDelay > 0) TaskManager.submit(tickDelay) {
        sendMessage(color.prefix + contents)
    } else
        sendMessage(color.prefix + contents)
}
fun Player.itemStatement(itemId: Int, itemZoom: Int = 200, vararg contents: String, tickDelay: Int = 0) {
    val dialogue = DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
            .setItem(itemId, itemZoom)
            .setText(*contents)
    if(tickDelay > 0) TaskManager.submit(tickDelay) {
        dialogue.start(this)
    } else
        dialogue.start(this)
}
fun Player.statement(vararg contents: String, tickDelay: Int = 0, hideContinue: Boolean = false) {
    val dialogue = DialogueBuilder(if (hideContinue) DialogueType.TITLED_STATEMENT_NO_CONTINUE else DialogueType.STATEMENT)
            .setText(*contents)
    if(tickDelay > 0) TaskManager.submit(tickDelay) {
        dialogue.start(this)
    } else
        dialogue.start(this)
}
fun Player.titledStatement(title: String, vararg contents: String, tickDelay: Int = 0) {
    val dialogue = DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
            .setStatementTitle(title)
            .setText(*contents)

    if(tickDelay > 0) TaskManager.submit(tickDelay) {
        dialogue.start(this)
    } else
        dialogue.start(this)
}
enum class Color(val prefix: String = "") {
    NONE,

    MAGENTA("@mag@"),
    STAMINA_POTION("<col=8B4513>"),
    RED("@red@"),
    DARK_RED("@dre@"),

    //RED("FF0000"),
    YELLOW("f4d03f"),
    GREEN("27ae60"),

    ORANGE("FFA500"),
    ORANGE_RED("FF4500"),
    TOMATO("FF6347"),
    CRIMSON("DC143C"),
    MAROON("800000"),

    BLUE("0000FF"),
    COOL_BLUE("0040ff"),
    BABY_BLUE("1E90FF"),
    CYAN("00FFFF"),

    PURPLE("800080"),
    VIOLET("EE82EE"),
    PINK("FFC0CB"),

    WHITE("FFFFFF"),
    WHEAT("F5DEB3"),
    SILVER("C0C0C0"),

    OLIVE("808000"),
    BRONZE("D37E2A"),
    GOLD("FFD700"),

   // DARK_RED("6f0000"),
    DARK_GREEN("006600"),

    RAID_PURPLE("ef20ff"),

    JEWERLY("<col=8B008B>");

    fun tag(): String? {
        return "<col=$prefix>"
    }
    fun wrap(s: String): String? {
        return tag() + s + "</col>"
    }
}

fun Player.sendOptions(
        vararg pairs: Pair<String, Consumer<Player>>,
        title: String? = null,
        addCancel: Boolean = true)
{
    DialogueBuilder(DialogueType.OPTION).also {
        if(title != null)
            it.setOptionTitle(title)
        it.addOptions(*pairs)
        if (addCancel)
            it.addCancel()
        it.start(this)
    }
}
fun Player.sendOptionsKt(
        vararg pairs: Pair<String, () -> Unit>,
        title: String? = null,
        addCancel: Boolean = true)
{
    sendOptions(*pairs
            .map { Pair(it.first, Consumer<Player> { _ -> it.second.invoke() }) }
            .toTypedArray(),
            title = title,
            addCancel = addCancel)
}
fun Player.sendRubbingOptions(
        vararg pairs: Pair<String, Consumer<Player>>,
        addCancel: Boolean = true)
{
    DialogueBuilder(DialogueType.OPTION).also {
        it.addOptions(*pairs)
        if (addCancel)
            it.addCancel("Nowhere.")
        it.start(this)
    }
}

fun Player.teleport(position: Position,
                    type: TeleportType = TeleportType.NORMAL,
                    warning: Boolean = false,
                    wildernessCheck: Boolean = true)
{
    TeleportHandler.teleport(this, position, type, warning, wildernessCheck)
}

fun teleportConsumer(position: Position,
                     type: TeleportType = TeleportType.NORMAL,
                     warning: Boolean = true,
                     wildernessCheck: Boolean = true)
= Consumer<Player> {
    it.teleport(position, type, warning, wildernessCheck)
}

/**
 * Dragonstone jewerly is used to allow players to teleport from level 30 Wilderness
 * while everything else is level 20
 */
fun dragonstoneTeleport(position: Position,
                        type: TeleportType = TeleportType.NORMAL,
                        warning: Boolean = false,
                        wildernessCheck: Boolean = true)
= Consumer<Player> {
    TeleportHandler.dragonStoneJewerlyTeleport(it, position, type, warning, wildernessCheck)
}

enum class FailSafePolicy {
    NONE,
    DROP_ON_GROUND_LOCAL,
    DROP_ON_GROUND_PUBLIC,
    TO_BANK,
    ANY
}