package com.grinder.game.content.skill.skillable.impl.crafting;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public class Snelms {

    private static final int[][] SNELMS = {
            {3353, 3335},
            {3347, 3329},
            {3349, 3331},
            {3351, 3333},
            {3341, 3359},
            {3345, 3327},
            {3357, 3339},
            {3355, 3337},
            {3361, 3343}
    };

    public static boolean cutShell(Player player, int item) {

        for (int i=0; i<9; i++) {
            if (SNELMS[i][0] == item) {
                if (player.getSkills().getLevel(Skill.CRAFTING) < 15) {
                    player.sendMessage("You need at least level 15 crafting to do that!");
                } else {
                    player.performAnimation(new Animation(1280));
                    player.getInventory().delete(item, 1);
                    player.getInventory().add(SNELMS[i][1], 1);
                    player.getSkillManager().addExperience(Skill.CRAFTING, 32.5f);
                    player.sendMessage("You chisel the shell into a helmet.");
                }
                return true;
            }
        }

        return false;
    }

}
