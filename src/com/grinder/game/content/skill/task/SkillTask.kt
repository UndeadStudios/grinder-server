package com.grinder.game.content.skill.task

import com.grinder.game.model.Skill

/**
 * Represents a skill task
 *
 * @author 2012
 */
class SkillTask(val id: Set<Integer>, var amount: Int, val skill: Skill, val description: String) {


    var initialAmount = amount

    fun hasInteraction(objectId: Integer) : Boolean {
        return this.id.contains(objectId);
    }
}