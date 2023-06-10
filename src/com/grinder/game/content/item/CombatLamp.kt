package com.grinder.game.content.item

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.*
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

/**
 * Item to give 1M experience in a combat skill.
 */
object CombatLamp {

    private const val COMBAT_LAMP_ATTACK = 77302
    private const val COMBAT_LAMP_STRENGTH = 77304
    private const val COMBAT_LAMP_RANGED = 77306
    private const val COMBAT_LAMP_MAGIC = 77308
    private const val COMBAT_LAMP_DEFENCE = 77310
    private const val COMBAT_LAMP_HITPOINTS = 77312
    private const val COMBAT_LAMP_CONFIRM = 77314

    private val buttonSkillMap = mapOf(
            COMBAT_LAMP_ATTACK to Skill.ATTACK,
            COMBAT_LAMP_STRENGTH to Skill.STRENGTH,
            COMBAT_LAMP_RANGED to Skill.RANGED,
            COMBAT_LAMP_MAGIC to Skill.MAGIC,
            COMBAT_LAMP_DEFENCE to Skill.DEFENCE,
            COMBAT_LAMP_HITPOINTS to Skill.HITPOINTS)

    init {
        onFirstInventoryAction(ItemID.COMBAT_LAMP) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                player.openInterface(77300)
                player.setInterfaceConfig(1170, 0)
                player.removeAttribute(Attribute.COMBAT_LAMP_SELECTED_SKILL)
            }
        }
        configureCombatLampsButtons()
    }

    private fun configureCombatLampsButtons(){
        ButtonActions.onClick(*buttonSkillMap.keys.toIntArray()) {
            player.setEnum(Attribute.COMBAT_LAMP_SELECTED_SKILL, buttonSkillMap[id]?:return@onClick)
        }
        ButtonActions.onClick(COMBAT_LAMP_CONFIRM) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)) {
//                if (!player.gameMode.isNormal) {
//                    player.sendMessage("This lamp can only be used by the regular game mode players.")
//                    return@onClick
//                }
                if (player.notInDangerOrAfkOrBusyOrInteracting()) {

                    if (!player.hasAttribute(Attribute.COMBAT_LAMP_SELECTED_SKILL)) {
                        player.message("You must select a skill before confirming.")
                        return@onClick
                    }

                    val selectedSkill = player.getEnum<Skill>(Attribute.COMBAT_LAMP_SELECTED_SKILL)
                    val item = Item(ItemID.COMBAT_LAMP, 1)

                    if (player.removeInventoryItem(item)) {
                        player.removeAttribute(Attribute.COMBAT_LAMP_SELECTED_SKILL)
                        player.removeInterfaces()
                        player.addExperience(selectedSkill, 1_000_000, noMultipliers = true)
                        player.playSound(Sounds.USING_LAMP_REWARD)
                        player.message("You have been rewarded with "+SkillUtil.applyExperienceGameModeModifiers(player.gameMode, 1_000_000)+" " + selectedSkill.getName() + " skill experience.")
                        player.resetInteractions()
                        player.performGraphic(Graphic(568))
                    }
                }
            }
        }
    }
}