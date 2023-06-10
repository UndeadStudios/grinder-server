package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Smithing.Bar;
import com.grinder.game.content.skill.skillable.impl.Smithing.Smelting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class SmeltBarX implements EnterSyntax {

    /**
     * The bar to smelt.
     */
    private final Bar bar;

    public SmeltBarX(Bar bar) {
        this.bar = bar;
    }

    @Override
    public void handleSyntax(Player player, String input) {
    }

    @Override
    public void handleSyntax(Player player, int input) {
        if (input <= 0 || input > Integer.MAX_VALUE) {
            return;
        }
        SkillUtil.startSkillable(player, new Smelting(bar, input));
    }
}
