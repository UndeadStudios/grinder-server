package com.grinder.game.content.skill;

import com.grinder.game.model.Skill;

/**
 * TODO: add documentation
 *
 * @author 2012
 * @version 1.0
 * @since 12/10/2019
 */
public class SkillRequirement {
    private Skill skill;
    private int levelRequired;
    private double experienceReceived;
    private int id;

    public SkillRequirement(Skill skill, int levelRequired, double experienceReceived) {
        this.setSkill(skill);
        this.setId(0);
        this.setLevelRequired(levelRequired);
        this.setExperienceReceived(experienceReceived);
    }

    public SkillRequirement(int id, int levelRequired, double experienceReceived) {
        this.setSkill(skill);
        this.setId(id);
        this.setLevelRequired(levelRequired);
        this.setExperienceReceived(experienceReceived);
    }

    /**
     * Sets the skill
     *
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Sets the skill
     *
     * @param skill the skill
     */
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    /**
     * Sets the levelRequired
     *
     * @return the levelRequired
     */
    public int getLevelRequired() {
        return levelRequired;
    }

    /**
     * Sets the levelRequired
     *
     * @param levelRequired the levelRequired
     */
    public void setLevelRequired(int levelRequired) {
        this.levelRequired = levelRequired;
    }

    /**
     * Sets the experienceReceived
     *
     * @return the experienceReceived
     */
    public double getExperienceReceived() {
        return experienceReceived;
    }

    /**
     * Sets the experienceReceived
     *
     * @param experienceReceived the experienceReceived
     */
    public void setExperienceReceived(double experienceReceived) {
        this.experienceReceived = experienceReceived;
    }

    /**
     * Sets the id
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
}
