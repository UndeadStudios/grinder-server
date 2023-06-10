package com.grinder.game.content.skill

import com.grinder.game.model.Animation
import com.grinder.game.model.Skill

/**
 * Represents the required skill, level, and experience associated with a resource node.
 *
 * @param skill SkillID
 * @param lv Level required to obtain.
 * @param exp Amount of experience given.
 */
open class ResourceSkillReq(val skill: Skill, val lv: Int, val exp: Int)

/**
 * Represents a tool used to gather resources in a skill.
 *
 * @param id ItemID for a tool.
 * @param startAnim Animation associated with the tool.
 * @param useMessage Message displayed during the initial cast. Default: empty
 * @param modifier The increase resource gathering chance. (0-1) Default:0.0
 */
data class GatherTool(val id: Int, val startAnim: Animation, val useMessage: String="", val modifier: Double=0.00)