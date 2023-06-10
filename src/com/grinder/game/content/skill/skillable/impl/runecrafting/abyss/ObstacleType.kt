package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.grinder.game.model.Skill

enum class ObstacleType(
        val skill: Skill,
        val start : String,
        val fail: String,
        val success: String) {

    TENDRILS(
            Skill.RUNECRAFTING,
            "You attempt to chop your way through...",
            "You need an axe to get through here.",
            "... and manage to cut a way through the tendrils."
    ),

    ROCK(
            Skill.MINING,
            "You attempt to mine you way through...",
            "You need a pickaxe to mine this.",
            "You manage to break through the rock."
    ),

    BOIL(
            Skill.FIREMAKING,
            "You attempt to burn your way though...",
            "You need a tinderbox to do this.",
            "You manage to burn through the boil."
    ),

    EYES(
            Skill.THIEVING,
            "You attempt to distract the eyes...",
            "",
            "... and manage to sneak past."
    ),

    GAP(
            Skill.AGILITY,
            "You attempt to squeeze through the gap...",
            "",
            "... and succeed."
    ),

    PASSAGE(
            Skill.AGILITY,
            "You pass through the passage.",
            "",
            ""
    )
}