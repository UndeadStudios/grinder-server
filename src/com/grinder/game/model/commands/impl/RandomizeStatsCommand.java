package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

public class RandomizeStatsCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Randomizes all your stats.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (Skill skill : Skill.values()) {
            if (SkillUtil.isCombatSkill(skill)) {
                continue;
            }
            int level = Misc.random(99);
            player.getSkillManager().setCurrentLevel(skill, level, true).setMaxLevel(skill, level, true).setExperience(skill,
                    SkillUtil.calculateExperienceForLevel(level));
        }
        for (Skill skill : Skill.values()) {
            if (!SkillUtil.isCombatSkill(skill)) {
                continue;
            }
            int level = 60 + Misc.random(39);
            player.getSkillManager().setCurrentLevel(skill, level, true).setMaxLevel(skill, level, true).setExperience(skill,
                    SkillUtil.calculateExperienceForLevel(level));
        }
        WeaponInterfaces.INSTANCE.assign(player);
        player.updateAppearance();
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER/* || player.getUsername().equals("Mod Hellmage")*/);
    }

}
