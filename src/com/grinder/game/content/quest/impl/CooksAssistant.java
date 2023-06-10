package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class CooksAssistant extends Quest {

    private static final Item EGG = new Item(1944);

    private static final Item POT_OF_FLOUR = new Item(1933);

    private static final Item BUCKET_OF_MILK = new Item(1927);

    public CooksAssistant() {
        super("Cook's Assistant", false, 1, 2);
    }

    @Override
    public String[][] getDescription(Player player) {

        return new String[][]{
                {"@dre@The Lumbridge Castle<col=010080> is in a mess. It is",
                        "<col=010080>the @dre@Duke of Lumbridge's<col=010080> birthday and the",
                        "<col=010080>cook is making the cake.", "",
                        "<col=010080>He needs a lot of ingredients and doesn't", "<col=010080>have much time.", ""},

                {"I have agreed to help the Cook with the birthday cake",
                        "He instructed me that he specifically needs:", "",

                        QuestManager.hasItem(player, EGG, "1 Egg"),
                        QuestManager.hasItem(player, POT_OF_FLOUR, "Pot of flour"),
                        QuestManager.hasItem(player, BUCKET_OF_MILK, "Bucket of milk"),

                        "", "I can ask the Cook where I can find these.",},
                {""}, {"I have helped the Cook and he managed to finish the cake",
                "I did not get to go to the party but the cook has", "rewarded me",},
                {""},};
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4626};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        switch (getStage(player)) {
            case 1: // check for items
                QuestDialogueLoader.sendDialogue(player, quest,
                        player.getInventory().contains(new Item[]{BUCKET_OF_MILK, EGG, POT_OF_FLOUR}) ? 11 : 9);
                return true;
        }
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 3) {
            increaseStage(player);
            sendDialogue(player, 6);
        }

        switch (getStage(player)) {
            case 1:
                if (npcId == 4626) {
                    if (player.getInventory().contains(new Item[]{BUCKET_OF_MILK, EGG, POT_OF_FLOUR})) {
                        player.getInventory().delete(new Item[]{BUCKET_OF_MILK, EGG, POT_OF_FLOUR});
                        QuestManager.increaseStage(player, quest);
                        QuestManager.complete(player, quest,
                                new String[]{"15,000 Cooking XP", "Access to Cook's range!"}, 1891);
                        player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 15_000);
                    }
                }
                break;
        }
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
