package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.skill.skillable.impl.crafting.GlassBlowing;
import com.grinder.game.content.skill.skillable.impl.crafting.GlassBlowingData;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class GlassBlowMenuX implements EnterSyntax {

    private final GlassBlowingData glassBlowingData;

    public GlassBlowMenuX(GlassBlowingData glassBlowingData) {
        this.glassBlowingData = glassBlowingData;
    }

    @Override
    public void handleSyntax(Player player, String input) {

    }

    @Override
    public void handleSyntax(Player player, int input) {
        if (input <= 0 || glassBlowingData == null)
            return;

        GlassBlowing.Companion.performGlassBlow(player, glassBlowingData, input);
    }
}
