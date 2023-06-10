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
public class RFDGoblinGeneralQuest extends Quest {

    private static final Item CHARCOAL = new Item(973);
    private static final Item ORANGE_SLICES = new Item(2112);
    private static final Item ORANGE_DYE = new Item(1769);
    private static final Item DYED_ORANGE = new Item(7514);
    private static final Item SPICY_MAGGOTS = new Item(7513);
    private static final Item SPICE = new Item(2007);
    private static final Item FISHING_BAIT = new Item(313);
    private static final Item SOGGY_BREAD = new Item(7512);
    private static final Item BUCKET_OF_WATER = new Item(1929);
    private static final Item BREAD = new Item(2309);
    private static final Item SLOP_OF_COMPROMISE = new Item(7511);
    private static final Item[] ALL_INGREDIENTS = {
        DYED_ORANGE, SPICY_MAGGOTS, SOGGY_BREAD,
    };

    public RFDGoblinGeneralQuest() {
        super("Goblin Generals", QuestType.SPECIAL, 1, 4);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "The goblin generals need a food that they'll both eat.",
                        "Find a chef in Goblin Village to learn how to make it.",
                },
                {
                        "",
                        "The goblin cook said he needs charcoal..",
                },
                {
                        "",
                        "Mudknuckles said he needs a few ingredients, he said",
                        "to bring him the following:",
                        "",
                        QuestManager.hasItem(player, DYED_ORANGE, "Dyed Orange"),
                        QuestManager.hasItem(player, SPICY_MAGGOTS, "Spicy maggots"),
                        QuestManager.hasItem(player, SOGGY_BREAD, "Soggy bread"),
                        "",
                        "I can dye orange slices to make dyed orange",
                        "I can use spice on fishing bait to make spicy maggots",
                        "I can use bucket of water on bread to make soggy bread.",
                        "",
                },
                {"",
                        "I must return to the feast and free the goblins!",
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4850};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 9) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 11) {
            boolean hasCharcoal = player.getInventory().contains(CHARCOAL);

            sendDialogue(player, hasCharcoal ? 13 : 12);
        } else if(id == 67) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 69) {
            boolean hasAll = player.getInventory().contains(ALL_INGREDIENTS);

            sendDialogue(player, hasAll ? 72: 71);
        } else if(id == 72) {
            player.getInventory().delete(ALL_INGREDIENTS);

            sendDialogue(player, 74);
        } else if(id == 75) {
            player.getInventory().add(SLOP_OF_COMPROMISE);
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 80) {
            player.getInventory().add(SLOP_OF_COMPROMISE);
            sendDialogue(player, 82);
        }
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == ORANGE_DYE.getId() && usedWith.getId() == ORANGE_SLICES.getId())
        || (use.getId() == ORANGE_SLICES.getId() && usedWith.getId() == ORANGE_DYE.getId())) {
            player.getInventory().combine(ORANGE_DYE, ORANGE_SLICES, DYED_ORANGE);
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        } else if ((use.getId() == SPICE.getId() && usedWith.getId() == FISHING_BAIT.getId())
        || (use.getId() == FISHING_BAIT.getId() && usedWith.getId() == SPICE.getId())) {
            player.getInventory().combine(SPICE, FISHING_BAIT, SPICY_MAGGOTS);
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        } else if ((use.getId() == BUCKET_OF_WATER.getId() && usedWith.getId() == BREAD.getId())
                || (use.getId() == BREAD.getId() && usedWith.getId() == BUCKET_OF_WATER.getId())) {
            player.getInventory().combine(BUCKET_OF_WATER, BREAD, SOGGY_BREAD);
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            return true;
        }
        return false;
    }
    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if(item.getId() == SLOP_OF_COMPROMISE.getId()) {
            if(object.getId() == 12332) {
                if(getStage(player) == 3) {
                    player.getInventory().delete(SLOP_OF_COMPROMISE);
                    increaseStage(player);
                    QuestManager.complete(player, quest, new String[]{
                            "300,000 Cooking XP",
                            "100,000 Crafting XP",
                            "200,000 Farming XP",
                            "Increased Culinaromancer's",
                            "Chest Access",
                    }, SLOP_OF_COMPROMISE.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 300_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.CRAFTING, 100_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.FARMING, 200_000);
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
            case 12332:
            case 12334:
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
