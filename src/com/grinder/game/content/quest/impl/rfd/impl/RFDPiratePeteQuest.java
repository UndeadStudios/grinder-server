package com.grinder.game.content.quest.impl.rfd.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.quest.QuestType;
import com.grinder.game.content.quest.impl.rfd.RecipeForDisaster;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDPiratePeteQuest extends Quest {

    private static final Position UNDER_WATER = new Position(2973, 9506);

    private static final Item RAW_COD = new Item(341);
    private static final Item GROUND_COD = new Item(7528);
    private static final Item BREADCRUMBS = new Item(7515);
    private static final Item BREAD = new Item(2309);
    private static final Item CRAB_MEAT = new Item(7518);
    private static final Item GROUND_CRAB_MEAT = new Item(7527);
    private static final Item KELP = new Item(7516);
    private static final Item GROUND_KELP = new Item(7517);
    private static final Item COOKED_FISHCAKE = new Item(7530);
    private static final Item PESTLE_AND_MORTAR = new Item(233);
    private static final Item DIVING_APPARATUS = new Item(7535);
    private static final Item FISHBOWL_HELMET = new Item(7534);

    private static final Item[] DIVING_SET = {
            DIVING_APPARATUS, FISHBOWL_HELMET,
    };

    private static final Item[] FISH_CAKE_INGREDIENTS = {
            GROUND_COD, BREADCRUMBS, GROUND_CRAB_MEAT, GROUND_KELP,
    };

    public RFDPiratePeteQuest() {
        super("Pirate Pete Quest", QuestType.SPECIAL, 1, 3);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "Pirate Pete needs a fishcake to be protected.",
                        "",
                        "Talk to the Lumbridge cook on how to make one.",
                },
                {
                        "",
                        "The lumbridge cook said I need to bring to him:",
                        "",
                        QuestManager.hasItem(player, GROUND_COD, "Ground Cod"),
                        QuestManager.hasItem(player, BREADCRUMBS, "Breadcrumbs"),
                        QuestManager.hasItem(player, GROUND_CRAB_MEAT, "Ground crab meat"),
                        QuestManager.hasItem(player, GROUND_KELP, "Ground Kelp"),
                        "",
                        "To make the fish cake..",
                },
                {"",
                        "I have the fish cake, now, I need to use it on pirate",
                        "pete.",
                },
                {""},
        };
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == COOKED_FISHCAKE.getId()) {
            if (object.getId() == 12337) {
                if (getStage(player) == 2) {
                    player.getInventory().delete(COOKED_FISHCAKE);
                    increaseStage(player);
                    QuestManager.complete(player, quest, new String[]{
                            "100,000 Cooking XP",
                            "100,000 Fishing XP",
                            "100,000 Smithing XP",
                            "100,000 Crafting XP",
                            "Increased Culinaromancer's",
                            "Chest Access",
                    }, COOKED_FISHCAKE.getId());
                    player.getSkillManager().addFixedDelayedExperience(Skill.COOKING, 100_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.FISHING, 100_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.MINING, 100_000);
                    player.getSkillManager().addFixedDelayedExperience(Skill.CRAFTING, 100_000);
                    RecipeForDisaster.completeAllSubquest(player);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4626, 5607};
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 12337:
                QuestManager.sendQuestDisplay(player, quest);
                return true;
        }
        return false;
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 17) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 19) {
            boolean hasAll = player.getInventory().contains(FISH_CAKE_INGREDIENTS);

            sendDialogue(player, hasAll ? 22 : 21);
        } else if (id == 26) {
            boolean hasAll = player.getInventory().contains(FISH_CAKE_INGREDIENTS);

            if (hasAll) {
                player.getInventory().delete(FISH_CAKE_INGREDIENTS);
                player.getInventory().add(COOKED_FISHCAKE);
                increaseStage(player);
                player.getPacketSender().sendInterfaceRemoval();
            }
        } else if (id == 28) {
            boolean hasSet = player.getEquipment().contains(DIVING_SET);

            if (!hasSet) {
                sendDialogue(player, 30);
                player.getInventory().addItemSet(DIVING_SET);
            } else {
                sendDialogue(player, 31);
            }
        } else if (id == 31) {

            SkillUtil.stopSkillable(player);
            player.BLOCK_ALL_BUT_TALKING = true;
            player.getPacketSender().sendFadeScreen("", 2, 5);
            TaskManager.submit(new Task(3) {
                                   @Override
                                   public void execute() {
                                       stop();
                                       player.getPacketSender().sendInterfaceRemoval();
                                       player.BLOCK_ALL_BUT_TALKING = false;
                                       player.moveTo(UNDER_WATER);
                                   }
                               });
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 33) {
            boolean hasCake = player.getInventory().contains(COOKED_FISHCAKE);

            sendDialogue(player, hasCake ? 35: 36);
        } else if(id == 37) {
            player.getInventory().add(COOKED_FISHCAKE);

            sendDialogue(player, 39);
        }
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (use.getId() == PESTLE_AND_MORTAR.getId()) {
            if (usedWith.getId() == RAW_COD.getId()) {
                player.getInventory().combine(RAW_COD, RAW_COD, GROUND_COD);
                return true;
            } else if (usedWith.getId() == CRAB_MEAT.getId()) {
                player.getInventory().combine(CRAB_MEAT, CRAB_MEAT, GROUND_CRAB_MEAT);
                return true;
            } else if (usedWith.getId() == KELP.getId()) {
                player.getInventory().combine(KELP, KELP, GROUND_KELP);
                return true;
            }
            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
        } else if (usedWith.getId() == PESTLE_AND_MORTAR.getId()) {
                if (use.getId() == RAW_COD.getId()) {
                    player.getInventory().combine(RAW_COD, RAW_COD, GROUND_COD);
                    return true;
                } else if (use.getId() == CRAB_MEAT.getId()) {
                    player.getInventory().combine(CRAB_MEAT, CRAB_MEAT, GROUND_CRAB_MEAT);
                    return true;
                } else if (use.getId() == KELP.getId()) {
                    player.getInventory().combine(KELP, KELP, GROUND_KELP);
                    return true;
                }
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
        } else if (use.getId() == ItemID.KNIFE) {
            if (usedWith.getId() == BREAD.getId()) {
                player.getInventory().combine(BREAD, BREAD, BREADCRUMBS);
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                return true;
            }
        } else if (usedWith.getId() == ItemID.KNIFE) {
            if (use.getId() == BREAD.getId()) {
                player.getInventory().combine(BREAD, BREAD, BREADCRUMBS);
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                return true;
            }
        }
        return false;
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
