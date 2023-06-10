package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class MasterCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Sets all your skills to the maximum level.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (Skill skill : Skill.values()) {
            int level = SkillUtil.maximumAchievableLevel();
            player.getSkillManager().setCurrentLevel(skill, level, true).setMaxLevel(skill, level, true).setExperienceIfMoreThanCurrent(skill,
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
