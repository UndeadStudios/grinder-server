package com.grinder.game.content.skill.task

import com.grinder.game.model.Skill
import com.grinder.util.NpcID
import com.grinder.util.NpcID.*

/**
 * Handles the skill masters
 */
enum class SkillMasterType(master: Int,
                           skill: Skill,
                           shop: Int,
                           dialogueId: Int,
                           explainDialogue: Int,
                           tasks: SkillTaskType,
                           capeDialogue: Int,
                           masterCapeDialogue: Int,
                           cape: Int
) {
    THIEVING(MARTIN_THWAIT, Skill.THIEVING, 33, 2506, 2507, SkillTaskType.THIEVING, 2515, 2516, 9777),
    WOODCUTTING(WOODSMAN_TUTOR, Skill.WOODCUTTING, 18, 2526, 2527, SkillTaskType.WOODCUTTING, 2528, 2529, 9807),
    MINING(GADRIN, Skill.MINING, 34, 2530, 2531, SkillTaskType.MINING, 2532, 2533, 9792),
    SMITHING(MASTER_SMITHING_TUTOR, Skill.SMITHING, 35, 2536, 2537, SkillTaskType.SMITHING, 2538, 2539, 9795),
    FISHING(MASTER_FISHER, Skill.FISHING, 36, 2549, 2550, SkillTaskType.FISHING, 2557, 2558, 9798),
    CRAFTING(MASTER_CRAFTER, Skill.CRAFTING, 371, 2826, 2827, SkillTaskType.CRAFTING, 2828, 2829, 9780),
    FIREMAKING(IGNATIUS_VULCAN, Skill.FIREMAKING, 372, 2830, 2831, SkillTaskType.FIREMAKING, 2832, 2833, 9804),
    AGILITY(CAPN_IZZY_NOBEARD, Skill.AGILITY, 373, 2834, 2835, SkillTaskType.AGILITY, 2836, 2837, 9771),
    HERBLORE(KAQEMEEX, Skill.HERBLORE, 374, 2845, 2846, SkillTaskType.HERBLORE, 2847, 2848, 9774),
    PRAYER(BROTHER_JERED, Skill.PRAYER, 375, 2849, 2850, SkillTaskType.PRAYER, 2851, 2852, 9759),
    COOKING(HEAD_CHEF, Skill.COOKING, 376, 2853, 2854, SkillTaskType.COOKING, 2855, 2856, 9801),
    RUNECRAFTING(AUBURY_11435, Skill.RUNECRAFTING, 377, 2857, 2858, SkillTaskType.RUNECRAFTING, 2859, 2860, 9765),
    FLETCHING(HICKTON, Skill.FLETCHING, 385, 2861, 2862, SkillTaskType.FLETCHING, 2863, 2864, 9783),
    //FARMING(5832, Skill.CRAFTING, 371, 2826, 2827, SkillTaskType.CRAFTING, 2828, 2829, 9780),
    //Hunter
    ;

    var master = 0
    var skill: Skill? = null
    var shop = 0
    var dialogueId = 0
    var explainDialogue = 0
    var tasks: SkillTaskType? = null
    var capeDialogue = 0
    var masterCapeDialogue = 0
    var cape = 0

    companion object {
        @JvmStatic
        fun forId(id: Int) = values().find { it.master == id }
    }

    init {
        this.master = master
        this.skill = skill
        this.shop = shop
        this.dialogueId = dialogueId
        this.explainDialogue = explainDialogue
        this.tasks = tasks
        this.capeDialogue = capeDialogue
        this.masterCapeDialogue = masterCapeDialogue
        this.cape = cape
    }
}