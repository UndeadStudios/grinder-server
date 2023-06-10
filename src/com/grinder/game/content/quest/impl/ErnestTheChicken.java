package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

import java.util.Optional;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class ErnestTheChicken extends Quest {

    public ErnestTheChicken() {
        super("Ernest The Chicken", false, 3, 3);
    }

    private static final Item PRESSURE_GAUGE = new Item(271);
    private static final Item OIL_CAN = new Item(277);
    private static final Item RUBBER_TUBE = new Item(276);
    private static final Item FISH_FOOD = new Item(272);
    private static final Item POISON = new Item(273);
    private static final Item POISONED_FISH = new Item(274);

    private static final Item KEY = new Item(275);

    private static final int PROFESSOR_ODDENSTEIN = 3562;
    private static final int CHICKEN = 3661;
    private static final int ERNEST = 3563;

    private static final Graphic BIRDS = new Graphic(80, GraphicHeight.HIGH);

    private static final Animation FIX_MACHINE = new Animation(896);

    private void chickenToHuman(Player player) {
        player.getInventory().delete(PRESSURE_GAUGE);
        player.getInventory().delete(OIL_CAN);
        player.getInventory().delete(RUBBER_TUBE);
        player.getPacketSender().sendInterfaceRemoval();
        player.BLOCK_ALL_BUT_TALKING = true;
        player.getPacketSender().sendMessage("You give a rubber tube, a pressure gauge,");
        new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
                .setText("You give a rubber tube, a pressure gauge,", "and a can of oil to the professor.").start(player);
        final Optional<NPC> chicken = (Optional<NPC>) QuestManager.getNpcById(CHICKEN);
        final Optional<NPC> professor = (Optional<NPC>) QuestManager.getNpcById(PROFESSOR_ODDENSTEIN);
        if (!chicken.isPresent() || !professor.isPresent()) {
            QuestManager.increaseStage(player, quest);
            QuestDialogueLoader.sendDialogue(player, quest, 30);
            return;
        }
        TaskManager.submit(new Task(1) {

            int time = 0;

            @Override
            protected void execute() {
                if (time == 1) {
                    player.getPacketSender().sendMessage("Oddestein starts up the machine.");
                    new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
                            .setText("Oddestein starts up the machine.").start(player);
                    professor.get().performAnimation(FIX_MACHINE);
                } else if (time == 3) {
                    player.getPacketSender().sendMessage("The machine hums and shakes.");
                    new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
                            .setText("The machine hums and shakes.").start(player);
                } else if (time == 5) {
                    chicken.get().performGraphic(BIRDS);
                    chicken.get().setNpcTransformationId(ERNEST);
                    QuestManager.increaseStage(player, quest);
                    QuestDialogueLoader.sendDialogue(player, quest, 30);
                    player.BLOCK_ALL_BUT_TALKING = false;
                } else if (time == 15) {
                    chicken.get().setNpcTransformationId(CHICKEN);
                    stop();
                }
                time++;
            }
        });
    }

    private void poisonFish(Player p) {
        if (!p.getInventory().contains(POISONED_FISH)) {
            return;
        }

        p.sendMessage("You poison the piranhas..");

        p.performAnimation(new Animation(832));
        p.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
        p.BLOCK_ALL_BUT_TALKING = true;
        TaskManager.submit(2, () -> {
            p.performAnimation(new Animation(835));
            p.getInventory().delete(POISONED_FISH);
            p.getInventory().add(PRESSURE_GAUGE);
            p.BLOCK_ALL_BUT_TALKING = false;
            new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText("A pressure guage!? I wonder what I can do with it.")
                    .setExpression(DialogueExpression.THINKING)
                    .start(p);
            p.sendMessage("..and retrieve a pressure gauge!");
        });
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"", "@dre@Veronica<col=010080> is very worried. Her @dre@fiance<col=010080> went into the big", "<col=010080>spooky @dre@manor house<col=010080> to ask for directions. An hour later", "<col=010080>and he's still not out yet",},

                {"", "I have agreed to help Veronica, she said I should", "check the manor out first.",},

                {"", "I have found Ernest, he's a chicken. I spoke to the", "professor who changed him into that. He said he's willing", "to return him back to normal although he needs a few", "parts to complete the process.", "", "I need:", "", QuestManager.hasItem(player, PRESSURE_GAUGE, "Pressure gauge"), QuestManager.hasItem(player, OIL_CAN, "Oil can"), QuestManager.hasItem(player, RUBBER_TUBE, "Rubber tube"),},

                {"", "I have helped Veronica by freeing Ernest", "from being a chicken."}};
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{3561, 3562};
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public DialogueOptions getDialogueOptions(Player player) {
        return new DialogueOptions() {
            @Override
            public void handleOption(Player player, int option) {
                switch (option) {
                    case 1:
                        if (quest.getStage(player) == 0) {
                            QuestManager.increaseStage(player, quest);
                            QuestDialogueLoader.sendDialogue(player, quest, 3);
                        }
                        break;
                    case 2:
                        QuestDialogueLoader.sendDialogue(player, quest, 5);
                        break;
                }
            }
        };
    }

    @Override
    public boolean hasStartDialogue(Player player, int npcId) {
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();
        int stage = quest.getStage(player);

        if (npcId == PROFESSOR_ODDENSTEIN) {
            if (stage == 1) {
                if (id == 22) {
                    QuestManager.increaseStage(player, quest);
                    player.getPacketSender().sendInterfaceRemoval();
                }
            } else if (stage == 2) {
                if (id == 24) {
                    QuestDialogueLoader.sendDialogue(player, quest, player.getInventory().contains(new Item[]{OIL_CAN, PRESSURE_GAUGE, RUBBER_TUBE}) ? 28 : 26);
                } else if (id == 28) {
                    chickenToHuman(player);
                }
            }
        } else if (npcId == 3563 && stage == 3 && id == 32) {
            QuestManager.complete(player, quest, new String[]{"5,000,000 coins.", "Ability to equip Ava's equipment."}, 314);
            player.getInventory().add(new Item(995, 5_000_000));
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        if (type == 1) {
            switch (object.getId()) {

                case 11499:
                    player.moveTo(new Position(3109, 3361, 0));
                    return true;
                case 11498:
                    player.moveTo(new Position(3109, 3366, 1));
                    return true;
               /* case 134:
                case 135:
                    player.moveTo(new Position(3108, 3354));
                    break;
                case 9584:
                    player.moveTo(new Position(3105, 3364, 1));
                    break;
                case 11511:
                    player.moveTo(new Position(3105, 3364, 2));*/
                   // return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (use.getId() == POISON.getId() && usedWith.getId() == FISH_FOOD.getId()
        || usedWith.getId() == POISON.getId() && use.getId() == FISH_FOOD.getId()) {
            player.getInventory().delete(POISON);
            player.getInventory().delete(FISH_FOOD);
            player.getInventory().add(POISONED_FISH);
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(272, 250)
                    .setText("You poisoned the fish food.").start(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (getStage(player) == 1 || getStage(player) == 2) {
        if (item.getId() == POISONED_FISH.getId() && object.getId() == 153) {
            poisonFish(player);
            return true;
        }
            if (item.getId() == 952 && object.getId() == 152) {
                player.performAnimation(new Animation(830));
                player.getPacketSender().sendSound(Sounds.DIGGING_SPADE);
                player.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(1, () -> {
                    player.BLOCK_ALL_BUT_TALKING = false;
                    player.getInventory().add(KEY);
                    player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                    new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("I wonder what I can do with a key found down here..")
                            .setExpression(DialogueExpression.THINKING)
                            .start(player);
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
