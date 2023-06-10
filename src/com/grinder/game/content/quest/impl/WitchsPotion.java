package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class WitchsPotion extends Quest {

    public WitchsPotion() {
        super("Witch's Potion", false, 1, 3);
    }

    private static final Item BURNT_MEAT = new Item(2146);
    private static final Item EYE_OF_NEWT = new Item(221);
    private static final Item ONION = new Item(1957);
    private static final Item RATS_TAIL = new Item(300);

    private static final Item[] REQUIRED = {BURNT_MEAT, EYE_OF_NEWT, ONION, RATS_TAIL};

    public static final int CAULDRON = 2024;
    private static final int HETTY = 4619;

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{{"",

                "<col=010080>Become one with your darker side. Tap into your ",
                "<col=010080>hidden depths of @dre@magical <col=010080>potential by making a",
                "@dre@potion<col=010080> with the help of @dre@Hetty the Rimmington witch.",},

                {"", "<col=010080>I have agreed to help @dre@Hetty <col=010080>create her potion", "<col=010080>She has asked me to bring back a few items:",
                        "", QuestManager.hasItem(player, BURNT_MEAT, "@dre@Burnt meat"),
                        QuestManager.hasItem(player, EYE_OF_NEWT, "@dre@Eye of newt"),
                        QuestManager.hasItem(player, ONION, "@dre@Onion"),
                        QuestManager.hasItem(player, RATS_TAIL, "@dre@Rats tail"),},

                {"", "<col=010080>I have gave the ingridients to @dre@Hetty <col=010080>and she told me", "<col=010080>to take a drink from the @dre@cauldron..",},

                {""}

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{HETTY};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public DialogueOptions getDialogueOptions(Player player) {
        return null;
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int dialogue = player.getDialogue().id();
        int stage = quest.getStage(player);
        if (npcId == HETTY) {
            if (stage == 0) {
                if (dialogue == 7) {
                    QuestDialogueLoader.sendDialogue(player, quest, 9);
                    QuestManager.increaseStage(player, quest);
                }
            } else if (stage == 1) {
                if (dialogue == 20) {
                    if (player.getInventory().contains(REQUIRED)) {
                        player.getInventory().delete(REQUIRED);
                        QuestDialogueLoader.sendDialogue(player, quest, 22);
                        QuestManager.increaseStage(player, quest);
                    } else {
                        QuestDialogueLoader.sendDialogue(player, quest, 30);
                    }
                }
            }
        }
    }

    public static void drinkFromCauldron(Player player) {
        if (QuestManager.getStage(player, QuestManager.WITCHS_POTION.name) == 2) {
            QuestManager.increaseStage(player, QuestManager.WITCHS_POTION);
            QuestManager.complete(player, QuestManager.WITCHS_POTION, new String[]{"50,000 Magic XP", ""}, EYE_OF_NEWT.getId());
            player.getSkillManager().addFixedDelayedExperience(Skill.MAGIC, 50_000);
        } else {
            player.getPacketSender().sendMessage("I shouldn't be doing this..");
        }
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}