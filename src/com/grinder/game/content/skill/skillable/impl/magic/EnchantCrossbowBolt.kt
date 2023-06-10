package com.grinder.game.content.skill.skillable.impl.magic

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.ButtonActions.onClick
import com.grinder.game.model.CommandActions
import com.grinder.game.model.ItemActions
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.item.Item
import java.util.concurrent.TimeUnit

/**
 * An object that handles the "Enchant crossbow bolt" spell.
 *
 * @author Blake
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
object EnchantCrossbowBolt {

    private const val INTERFACE_ID = 42750
    private const val CONTAINER_ID = 42752
    private const val SPELLBOOK_BUTTON_ID = 19207

    private val levels = intArrayOf(4, 7, 14, 24, 27, 29, 49, 57, 68, 87)

    private val runes = arrayOf(
            intArrayOf(564, 1), intArrayOf(556, 2),
            intArrayOf(564, 1), intArrayOf(555, 1),
            intArrayOf(558, 1), intArrayOf(564, 1),
            intArrayOf(557, 2), intArrayOf(564, 1),
            intArrayOf(555, 2), intArrayOf(564, 1),
            intArrayOf(556, 3), intArrayOf(561, 1),
            intArrayOf(564, 1), intArrayOf(554, 2),
            intArrayOf(564, 1), intArrayOf(554, 5),
            intArrayOf(565, 1), intArrayOf(564, 1),
            intArrayOf(557, 10), intArrayOf(563, 2),
            intArrayOf(564, 1), intArrayOf(557, 15),
            intArrayOf(566, 1), intArrayOf(564, 1),
            intArrayOf(554, 20), intArrayOf(560, 1))

    private val item = arrayOf(
            Item(9236), Item(9240),
            Item(9237), Item(9238),
            Item(9241), Item(9239),
            Item(9242), Item(9243),
            Item(9244), Item(9245))

    init {
        CommandActions.onCommand("ebolts", PlayerRights.HIGH_STAFF) {
            val regular = commandArguments.contains("r")
            val runes = HashSet<Int>()
            for (bolt in EnchantableBolts.values()) {
                for (rune in bolt.runes) {
                    runes.add(rune!!.id)
                }
                val addId = if (regular)
                    bolt.enchantPairs[0].first
                else
                    bolt.enchantPairs[1].first

                player.addInventoryItem(Item(addId, 1000), -1)
            }
            for (runeId in runes) {
                player.addInventoryItem(Item(runeId, 10_000), -1)
            }
            player.inventory.refreshItems()
            return@onCommand true
        }
        for (bolt in EnchantableBolts.values()) {
            for (enchantPair in bolt.enchantPairs) {
                val toEnchant = enchantPair.first
                val enchanted = enchantPair.second
                ItemActions.onContainerClick(toEnchant) {
                    if (getInterfaceId() == CONTAINER_ID) {

                        if (player.interfaceId != INTERFACE_ID) {
                            player.removeInterfaces()
                            return@onContainerClick true
                        }

                        if (player.passedTime(Attribute.ENCHANT_SPELL_TIMER, 1200, TimeUnit.MILLISECONDS, message = false)) {
                            val itemName = ItemDefinition.forId(toEnchant).name
                            val action = "to enchant $itemName"
                            val canCastSpell = player.hasSkillLevel(Skill.MAGIC, bolt.level, action)
                            val hasEnchantableBolts = player.hasItemInInventory(Item(toEnchant, 10), action)
                            val requiredRunes = bolt.runes.clone().also { runes -> ElementStaffType.suppressRunes(player, runes) }
                            val hasRequiredRunes = player.hasItemsInInventory(*requiredRunes.filterNotNull().toTypedArray(), idenitifer = "runes", action = action)
                            if (!canCastSpell || !hasEnchantableBolts || !hasRequiredRunes) {
                                player.markTime(Attribute.ENCHANT_SPELL_TIMER)
                                return@onContainerClick true
                            }
                            if(!player.inventory.canHold(enchanted,10)) {
                                player.sendMessage("You need atleast one free inventory slot to do this!")
                                return@onContainerClick true
                            }
                            player.tryRandomEventTrigger()

                            for (requiredRune in requiredRunes.filterNotNull())
                                player.removeInventoryItem(requiredRune, -1)

                            player.inventory.refreshItems()

                            player.performAnimation(Animation(4462))
                            player.removeInventoryItem(Item(toEnchant, 10), -1)
                            player.addInventoryItem(Item(enchanted, 10), 1)

                            AchievementManager.processFor(AchievementType.THE_ENCHANTER, 10, player)

                            for ((i, entry) in runes.withIndex()) {
                                val color = if (!player.hasItemInInventory(Item(entry[0], entry[1]), message = false))
                                    "@red@"
                                else
                                    "@gre@"
                                val amount = player.inventory.getAmount(entry[0])
                                val runeString = "${color}${(if (amount >= entry[1]) entry[1] else amount)}/${entry[1]}"
                                player.packetSender.sendString(42766 + i, runeString)
                            }

                            player.addExperience(Skill.MAGIC, bolt.magicExperienceGain)
                            player.points.increase(AttributeManager.Points.BOLTS_ENCHANTED, 10) // Increase points

                            player.markTime(Attribute.ENCHANT_SPELL_TIMER)
                        }
                        return@onContainerClick true
                    }
                    return@onContainerClick false
                }
            }
        }
        onClick(SPELLBOOK_BUTTON_ID) {

            if (!player.hasSkillLevel(Skill.MAGIC, 4, "to use this spell"))
                return@onClick

            if(player.passedTime(Attribute.ENCHANT_SPELL_TIMER, 1200, TimeUnit.MILLISECONDS)){

                for ((i, bolt) in EnchantableBolts.values().withIndex()){
                    val regularBolt = bolt.enchantPairs[0].first
                    val dragonStoneBolt = bolt.enchantPairs[1].first
                    val displayId = if (player.inventory.contains(dragonStoneBolt)) dragonStoneBolt else regularBolt
                    player.packetSender.sendItemOnInterface(42752, displayId, i, 10)
                }

                for (i in levels.indices) {
                    val color = if (!player.hasSkillLevel(Skill.MAGIC, levels[i], message = false))
                        "@red@"
                    else
                        "@gr3@"
                    player.packetSender.sendString(42756 + i, color + "Magic " + levels[i])
                }
                for (i in runes.indices) {
                    val color = if (!player.inventory.contains(Item(runes[i][0], runes[i][1]))
                            && !ElementStaffType.hasStaffForRune(player, runes[i][0]))
                        "@red@"
                    else
                        "@gre@"
                    val amount = player.inventory.getAmount(runes[i][0])
                    var required = (if (amount >= runes[i][1]) runes[i][1] else amount).toString()
                    if (ElementStaffType.hasStaffForRune(player, runes[i][0]))
                        required = "*"
                    player.packetSender.sendString(42766 + i, color + required + "/" + runes[i][1])
                }
                player.packetSender.sendInterface(INTERFACE_ID)
            }
        }
    }
}