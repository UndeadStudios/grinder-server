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
import com.grinder.game.model.sound.Sounds;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDEvilDaveQuest extends Quest {

    private static final Item SPICY_STEW = new Item(7479);
    private static final Item STEW = new Item(2003);
    private static final Item BROWN_SPICE = new Item(7488);
    private static final Item YELLOW_SPICE = new Item(7492);
    private static final Item RED_SPICE = new Item(7480);
    private static final Item ORANGE_SPICE = new Item(7484);

    private static final Item[] ALL_SPICES = {
            BROWN_SPICE, YELLOW_SPICE, RED_SPICE, ORANGE_SPICE
    };

    public RFDEvilDaveQuest() {
        super("Evil Dave", QuestType.SPECIAL, 1, 1);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "Evil Dave needs some kind of spicy stew.",
                },
                {""}
        };
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (use.getId() == BROWN_SPICE.getId() || use.getId() == YELLOW_SPICE.getId() || use.getId() == RED_SPICE.getId() || use.getId() == ORANGE_SPICE.getId()) {
            if (usedWith.getId() == STEW.getId()) {
                if (!player.getInventory().contains(ALL_SPICES)) {
                    player.getPacketSender().sendMessage("You don't have all the spices to make the stew!");
                    return true;
                }
                player.getInventory().delete(ALL_SPICES);
                player.getInventory().delete(STEW);
                player.getInventory().add(SPICY_STEW);
                player.getPacketSender().sendMessage("You combine all the spices into the stew and make a spicy stew.");
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == 7479) {
            if (object.getId() == 12341) {
                if (getStage(player) == 0) {
                    increaseStage(player);
                    player.getInventory().delete(SPICY_STEW);
                    QuestManager.complete(player, quest, new String[]{
                            "70,000 Cooking XP",
                            "Ability to make Spicy Stew",
                            "Increased Culinaromancer's",
                            "Chest Access",
                    }, SPICY_STEW.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 70_000);
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
            case 12341:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[0];
    }

    @Override
    public boolean hasRequirements(Player player) {
        if (!QuestManager.hasCompletedQuest(player, "Cook's Assistant")) {
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
