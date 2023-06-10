package com.grinder.game.content.quest.impl.rfd.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.quest.QuestType;
import com.grinder.game.content.quest.impl.rfd.RecipeForDisaster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDSkrachQuest extends Quest {
    private static final Item COOKED_JUBBLY = new Item(7568);

    public RFDSkrachQuest() {
        super("Skrach Quest", QuestType.SPECIAL, 1, 1);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "To protect Skrach Uglogwee from the Culinaromancer,",
                        "you need to feed him a cooked jubbly bird, the most",
                        "delicious type of Chompy in the Feldip Hills.",
                        "",
                        QuestManager.hasItem(player, COOKED_JUBBLY, "Cooked Jubbly"),
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[0];
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == COOKED_JUBBLY.getId()) {
            if (object.getId() == 12343) {
                if (getStage(player) == 0) {
                    increaseStage(player);
                    player.getInventory().delete(COOKED_JUBBLY);
                   QuestManager.complete(player, quest, new String[]{
                            "150,000 Cooking XP",
                            "150,000 Woodcutting XP",
                            "150,000 Ranged XP",
                            "150,000 Crafting XP",
                            "Increased Culinaromancer's",
                            "Chest Access",
                    }, COOKED_JUBBLY.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 150_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.WOODCUTTING, 150_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.RANGED, 150_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.CRAFTING, 150_000);
                    RecipeForDisaster.completeAllSubquest(player);
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12343:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }
    @Override
    public boolean hasRequirements(Player player) {
        if(!QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
            return false;
        }
        if (QuestManager.getStage(player, "Recipe for Disaster") == 3) {
            return true;
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
