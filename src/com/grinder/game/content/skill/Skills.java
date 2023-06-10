package com.grinder.game.content.skill;

import com.grinder.game.model.Skill;

import java.util.Arrays;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/10/2019
 */
public class Skills {

    int[] level, maxLevel, experience;

    public Skills() {
        level = new int[SkillConstants.SKILL_COUNT];
        maxLevel = new int[SkillConstants.SKILL_COUNT];
        experience = new int[SkillConstants.SKILL_COUNT];
    }
    public void set(Skill skill, int currentLevel, int maxLevel) {
        this.level[skill.ordinal()] = currentLevel;
        this.maxLevel[skill.ordinal()] = maxLevel;
    }

    public int getLevel(Skill skill) {
        return level[skill.ordinal()];
    }

    public int getMaximumLevel(Skill skill) {
        return maxLevel[skill.ordinal()];
    }

    public int[] getLevels() {
        return level;
    }

    public int[] getMaxLevels() {
        return maxLevel;
    }

    public int[] getExperiences() {
        return experience;
    }

    public Skills copy() {
        final Skills skills = new Skills();
        skills.level = Arrays.copyOf(level, level.length);
        skills.maxLevel = Arrays.copyOf(maxLevel, maxLevel.length);
        skills.experience = Arrays.copyOf(experience, experience.length);
        return skills;
    }
}
