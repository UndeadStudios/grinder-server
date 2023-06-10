package com.grinder.game.content.item

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.*
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.Graphic
import com.grinder.game.model.ItemActions
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * Represents an [Item] that can be used to give minor experience
 * in a [Skill] of choice.
 */
object BookOfKnowledge {
    
    init {
        configureSkillButtons()

        ItemActions.onClick(ItemID.BOOK_OF_KNOWLEDGE) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if(isInInventory()) {
                if (player.inventory.contains(ItemID.BOOK_OF_KNOWLEDGE)) {
                    player.removeAttribute(Attribute.SKILL_BOOK_OF_KNOWLEDGE)
                    player.removeAttribute(Attribute.SKILL_RESET_LAMP)
                    player.removeAttribute(Attribute.MAX_XP_LAMP)
                    player.removeAttribute(Attribute.ANTIQUE_LAMP)
                    player.setBoolean(Attribute.SKILL_BOOK_OPEN, true)
                    player.setBoolean(Attribute.MAX_XP_LAMP_OPEN, false)
                    player.setBoolean(Attribute.RESET_LAMP_OPEN, false)
                    player.setBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)
                    player.setInterfaceConfig(261, 1)
                    player.packetSender.sendString(2810, "Choose the stat you wish to advance!")
                    player.openInterface(2808)
                }
                return@onClick true
            }
            return@onClick false
        }

        ItemActions.onClick(ItemID.ANTIQUE_LAMP_4) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if(isInInventory()) {
                if (player.inventory.contains(ItemID.ANTIQUE_LAMP_4)) {
                    player.removeAttribute(Attribute.SKILL_BOOK_OF_KNOWLEDGE)
                    player.removeAttribute(Attribute.SKILL_RESET_LAMP)
                    player.removeAttribute(Attribute.MAX_XP_LAMP)
                    player.removeAttribute(Attribute.ANTIQUE_LAMP)
                    player.setBoolean(Attribute.SKILL_BOOK_OPEN, false)
                    player.setBoolean(Attribute.MAX_XP_LAMP_OPEN, false)
                    player.setBoolean(Attribute.RESET_LAMP_OPEN, false)
                    player.setBoolean(Attribute.ANTIQUE_LAMP_OPEN, true)
                    player.setInterfaceConfig(261, 1)
                    player.packetSender.sendString(2810, "Choose the stat you wish to advance!")
                    player.openInterface(2808)
                }
                return@onClick true
            }
            return@onClick false
        }

        ItemActions.onClick(15827) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if(isInInventory()) {
                if (player.inventory.contains(15827)) {
                    player.removeAttribute(Attribute.SKILL_BOOK_OF_KNOWLEDGE)
                    player.removeAttribute(Attribute.SKILL_RESET_LAMP)
                    player.removeAttribute(Attribute.MAX_XP_LAMP)
                    player.removeAttribute(Attribute.ANTIQUE_LAMP)
                    player.setBoolean(Attribute.MAX_XP_LAMP_OPEN, true)
                    player.setBoolean(Attribute.SKILL_BOOK_OPEN, false)
                    player.setBoolean(Attribute.RESET_LAMP_OPEN, false)
                    player.setBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)
                    player.setInterfaceConfig(261, 1)
                    player.packetSender.sendString(2810, "Choose the stat you wish to max to 99!")
                    player.openInterface(2808)
                }
                return@onClick true
            }
            return@onClick false
        }

        ItemActions.onClick(ItemID.LAMP) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }
            if(isInInventory()) {
                if (player.inventory.contains(ItemID.LAMP)) {
                    player.removeAttribute(Attribute.SKILL_RESET_LAMP)
                    player.removeAttribute(Attribute.SKILL_BOOK_OF_KNOWLEDGE)
                    player.removeAttribute(Attribute.MAX_XP_LAMP)
                    player.removeAttribute(Attribute.ANTIQUE_LAMP)
                    player.setBoolean(Attribute.SKILL_BOOK_OPEN, false)
                    player.setBoolean(Attribute.MAX_XP_LAMP_OPEN, false)
                    player.setBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)
                    player.setBoolean(Attribute.RESET_LAMP_OPEN, true)
                    player.setInterfaceConfig(261, 1)
                    player.packetSender.sendString(2810, "Choose the stat you wish to be reset!")
                    player.openInterface(2808)
                }
                return@onClick true
            }
            return@onClick false
        }


            ButtonActions.onClick(2831) {
                if(player.getBoolean(Attribute.RESET_LAMP_OPEN, false)) {
                if (canUse(player)) {
                    val skill = Skill.values()[player.getInt(Attribute.SKILL_RESET_LAMP)]
                    player.removeInventoryItem(Item(ItemID.LAMP, 1))
                    player.removeInterfaces()
                    player.resetSkill(skill)
                    player.playSound(Sounds.USING_LAMP_REWARD)
                    player.removeAttribute(Attribute.SKILL_RESET_LAMP)
                    player.removeAttribute(Attribute.SKILL_BOOK_OPEN)
                    player.removeAttribute(Attribute.RESET_LAMP_OPEN)
                    player.removeAttribute(Attribute.MAX_XP_LAMP_OPEN)
                    player.removeAttribute(Attribute.ANTIQUE_LAMP_OPEN)
                    player.setInterfaceConfig(261, 0)
                    player.message("You have reset your ${skill.getName()} level back to level one.")
                    player.skillManager.calculateCombatLevel()
                } else
                    player.statement("You must first select a skill.")
            } else if (player.getBoolean(Attribute.SKILL_BOOK_OPEN, false)) {
                    if (canUse(player)) {
                        val skill = Skill.values()[player.getInt(Attribute.SKILL_BOOK_OF_KNOWLEDGE)]
                        player.removeInventoryItem(Item(ItemID.BOOK_OF_KNOWLEDGE, 1))
                        player.removeInterfaces()
                        val experienceReward = 25_000 + Misc.random(150_000)
                        player.addExperience(skill, experienceReward, noMultipliers = true)
                        player.playSound(Sounds.USING_LAMP_REWARD)
                        player.message(
                            "You have been rewarded with " + Misc.format(
                                SkillUtil.applyExperienceGameModeModifiers(
                                    player.gameMode,
                                    experienceReward
                                )
                            ) + " ${skill.getName()} skill experience."
                        )
                        player.resetInteractions()
                        player.performGraphic(Graphic(568))
                        player.removeAttribute(Attribute.SKILL_BOOK_OF_KNOWLEDGE)
                        player.removeAttribute(Attribute.SKILL_BOOK_OPEN)
                        player.removeAttribute(Attribute.MAX_XP_LAMP_OPEN)
                        player.removeAttribute(Attribute.RESET_LAMP_OPEN)
                        player.removeAttribute(Attribute.ANTIQUE_LAMP_OPEN)
                        player.setInterfaceConfig(261, 0)
                        player.skillManager.calculateCombatLevel()
                    } else
                        player.statement("You must first select a skill.")
                } else if (player.getBoolean(Attribute.MAX_XP_LAMP_OPEN, false)) {
                    if (canUse(player)) {
                        val skill = Skill.values()[player.getInt(Attribute.MAX_XP_LAMP)]
                        if (skill == Skill.PRAYER) {
                            DialogueManager.sendStatement(player, "You cannot use this lamp for the Prayer skill.");
                            return@onClick
                        }
                        player.removeInventoryItem(Item(15827, 1))
                        player.removeInterfaces()
                        val experienceReward = 13034431
                        player.addExperience(skill, experienceReward, noMultipliers = true)
                        player.playSound(Sounds.USING_LAMP_REWARD)
                        player.message(
                            "You have been rewarded with " + Misc.format(
                                SkillUtil.applyExperienceGameModeModifiers(
                                    player.gameMode,
                                    experienceReward
                                )
                            ) + " ${skill.getName()} skill experience."
                        )
                        player.resetInteractions()
                        player.performGraphic(Graphic(568))
                        player.removeAttribute(Attribute.MAX_XP_LAMP)
                        player.removeAttribute(Attribute.MAX_XP_LAMP_OPEN)
                        player.removeAttribute(Attribute.SKILL_BOOK_OPEN)
                        player.removeAttribute(Attribute.RESET_LAMP_OPEN)
                        player.setInterfaceConfig(261, 0)
                        player.skillManager.calculateCombatLevel()
                    } else
                        player.statement("You must first select a skill.")
                } else if (player.getBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)) {
                    if (canUse(player)) {
                        val skill = Skill.values()[player.getInt(Attribute.ANTIQUE_LAMP)]
                        if (skill == Skill.PRAYER) {
                            DialogueManager.sendStatement(player, "You cannot use this lamp for the Prayer skill.");
                            return@onClick
                        }
                        player.removeInventoryItem(Item(ItemID.ANTIQUE_LAMP_4, 1))
                        player.removeInterfaces()
                        val experienceReward = Misc.randomInclusive(150_000, 500_000) + Misc.random(50_000)
                        player.addExperience(skill, experienceReward, noMultipliers = true)
                        player.playSound(Sounds.USING_LAMP_REWARD)
                        player.message(
                            "You have been rewarded with " + Misc.format(
                                SkillUtil.applyExperienceGameModeModifiers(
                                    player.gameMode,
                                    experienceReward
                                )
                            ) + " ${skill.getName()} skill experience."
                        )
                        player.resetInteractions()
                        player.performGraphic(Graphic(568))
                        player.removeAttribute(Attribute.ANTIQUE_LAMP)
                        player.removeAttribute(Attribute.MAX_XP_LAMP_OPEN)
                        player.removeAttribute(Attribute.SKILL_BOOK_OPEN)
                        player.removeAttribute(Attribute.RESET_LAMP_OPEN)
                        player.removeAttribute(Attribute.ANTIQUE_LAMP_OPEN)
                        player.setInterfaceConfig(261, 0)
                        player.skillManager.calculateCombatLevel()
                    } else
                        player.statement("You must first select a skill.")
                }
            }
        }

    private fun canUse(player: Player): Boolean{
        if (player.getBoolean(Attribute.RESET_LAMP_OPEN, false)) {
                return player.inventory.contains(ItemID.LAMP) && player.inventory.getAmount(ItemID.LAMP) > 0
            } else if (player.getBoolean(Attribute.SKILL_BOOK_OPEN, false)) {
            return player.inventory.contains(ItemID.BOOK_OF_KNOWLEDGE) && player.inventory.getAmount(ItemID.BOOK_OF_KNOWLEDGE) > 0
        } else if (player.getBoolean(Attribute.MAX_XP_LAMP_OPEN, false)) {
            return player.inventory.contains(15827) && player.inventory.getAmount(15827) > 0
        } else if (player.getBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)) {
            return player.inventory.contains(ItemID.ANTIQUE_LAMP_4) && player.inventory.getAmount(ItemID.ANTIQUE_LAMP_4) > 0
        }

        return false
    }

    private fun configureSkillButtons() {
        configureSkillButton(2812, Skill.ATTACK, 1)
        configureSkillButton(2813, Skill.STRENGTH, 2)
        configureSkillButton(2814, Skill.RANGED, 3)
        configureSkillButton(2815, Skill.MAGIC, 4)
        configureSkillButton(2816, Skill.DEFENCE, 5)
        configureSkillButton(2817, Skill.HITPOINTS, 6)
        configureSkillButton(2818, Skill.PRAYER, 7)
        configureSkillButton(2819, Skill.AGILITY, 8)
        configureSkillButton(2820, Skill.HERBLORE, 9)
        configureSkillButton(2821, Skill.THIEVING, 10)
        configureSkillButton(2822, Skill.CRAFTING, 11)
        configureSkillButton(2823, Skill.RUNECRAFTING, 12)
        configureSkillButton(12034, Skill.SLAYER, 20)
        configureSkillButton(13914, Skill.FARMING, 21)
        configureSkillButton(2824, Skill.MINING, 13)
        configureSkillButton(2825, Skill.SMITHING, 14)
        configureSkillButton(2826, Skill.FISHING, 15)
        configureSkillButton(2827, Skill.COOKING, 16)
        configureSkillButton(2828, Skill.FIREMAKING, 17)
        configureSkillButton(2829, Skill.WOODCUTTING, 18)
        configureSkillButton(2830, Skill.FLETCHING, 19)
    }

    private fun configureSkillButton(buttonId: Int, skill: Skill, index: Int){
        ButtonActions.onClick(buttonId) {
            if(player.getBoolean(Attribute.SKILL_BOOK_OPEN, false)) {
                player.setInt(Attribute.SKILL_BOOK_OF_KNOWLEDGE, skill.ordinal)
            }
            if(player.getBoolean(Attribute.RESET_LAMP_OPEN, false)) {
                player.setInt(Attribute.SKILL_RESET_LAMP, skill.ordinal)
            }
            if(player.getBoolean(Attribute.MAX_XP_LAMP_OPEN, false)) {
                player.setInt(Attribute.MAX_XP_LAMP, skill.ordinal)
            }
            if(player.getBoolean(Attribute.ANTIQUE_LAMP_OPEN, false)) {
                player.setInt(Attribute.ANTIQUE_LAMP, skill.ordinal)
            }
            player.setInterfaceConfig(261, index)
            player.message("You've selected ${skill.getName()}.")
        }
    }
}