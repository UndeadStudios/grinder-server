package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class SetLevel implements EnterSyntax {

    private Skill skill;

    public SetLevel(Skill skill) {
        this.skill = skill;
    }

    @Override
    public void handleSyntax(Player player, String input) {

    }

    @Override
    public void handleSyntax(Player player, int input) {
        int max = 99;
        if (player.getGameMode().isSpawn())
            max = 99;
        else if (player.getRights().anyMatch(PlayerRights.OWNER, PlayerRights.DEVELOPER))
            max = 9999;

        if (input > max) {
            player.getPacketSender().sendMessage("The max level you can set is " + max +".");
            return;
        }

        SkillUtil.setLevel(player, skill, Math.max(0, Math.min(input, max)));
    }
}
