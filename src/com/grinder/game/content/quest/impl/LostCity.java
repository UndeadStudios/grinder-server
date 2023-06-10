package com.grinder.game.content.quest.impl;

import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class LostCity extends Quest {

    private static final int SHAMUS = 1162;

    private static final int DRAMEN_TREE = 1292;

    public static final int SPIRIT_TREE = 1163;

    private static final Position SHAMUS_SPAWN = new Position(3140, 3212);

    private static final Position ENTRANA_ENTRY = new Position(2834, 3335);

    private static final Position TREE_SPIRIT_SPAWN = new Position(2858, 9734);

    private static final Position ZANARIS = new Position(2452, 4473);

    private void travelToEntrana(Player p) {
        for (Item item : p.getInventory().getValidItems()) {
            if (item.getDefinition().getBonuses() == null) {
                continue;
            }
            for (int i = 0; i < item.getDefinition().getBonuses().length; i++) {
                if (item.getDefinition().getBonuses()[i] > 0) {
                    p.sendMessage("You cannot bring any items that give bonuses into Entrana.");
                    return;
                }
            }
        }

        for (Item item : p.getEquipment().getValidItems()) {
            if (item.getDefinition().getBonuses() == null) {
                continue;
            }
            for (int i = 0; i < item.getDefinition().getBonuses().length; i++) {
                if (item.getDefinition().getBonuses()[i] > 0) {
                    p.sendMessage("You cannot bring any items that give bonuses into Entrana.");
                    return;
                }
            }
        }
        if (!EntityExtKt.passedTime(p, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true))
            return;

        p.BLOCK_ALL_BUT_TALKING = true;
        SkillUtil.stopSkillable(p);

        p.getPacketSender().sendFadeScreen("Traveling to Entrana", 2, 6);
        p.getPacketSender().sendJinglebitMusic(253, 15);

        TaskManager.submit(p, 4, () -> {
            p.moveTo(ENTRANA_ENTRY);
            p.getPacketSender().sendInterfaceRemoval();
            p.BLOCK_ALL_BUT_TALKING = false;
        });
    }


    public LostCity() {
        super("Lost City", false, 3, 4);
    }

    private void spawnShamus(Player p) {
        if (getStage(p) == 1) {
            if (!EntityExtKt.passedTime(p, Attribute.LAST_COFFIN_USE, 60, TimeUnit.SECONDS, false, false)) {
                return;
            }
            EntityExtKt.markTime(p, Attribute.LAST_COFFIN_USE);
            NPC shamus = NPCFactory.INSTANCE.create(SHAMUS, SHAMUS_SPAWN);

            World.getNpcAddQueue().add(shamus);

            TaskManager.submit(new Task(30) {
                @Override
                protected void execute() {
                    World.getNpcRemoveQueue().add(shamus);
                    stop();
                }
            });
        }
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "Can you find the legendary lost city of Zanaris?",
                },
                {
                        "",
                        "I have found about Zanaris, a leprechaun is hiding",
                        "in a nearby tree.. I should find him to find out more."
                },
                {
                        "",
                        "I have found a leprechaun hiding in a nearby tree",
                        "who knows how to get to Zanaris. He said that I",
                        "need a Dramen staff made from a Dramen tree branch",
                        "cut from the Dramen tree in Entrana Dungeon beneath",
                        "Entrana."
                },
                {
                        "",
                        "I have killed the spirit tree guarding the tree,",
                        "I should now make the staff and enter the shed in",
                        "Lumbridge swamp."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{1157, SHAMUS};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 8) {
            increaseStage(player);
            sendDialogue(player, 10);
        } else if (id == 27) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        if (object.getPosition().sameAs(new Position(3138, 3212))) {
            if (getStage(player) == 1) {
                spawnShamus(player);
                return true;
            } else {
                player.sendMessage("This tree looks suspicious, it's not a very good idea to cut it.");
                return true;
            }
        }
        if (object.getPosition().sameAs(new Position(3202, 3169))) {
            if (player.getEquipment().contains(ItemID.DRAMEN_STAFF)) {
                new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
                        .setText("The world stars to shimmer.").start(player);
                SkillUtil.stopSkillable(player);
                player.BLOCK_ALL_BUT_TALKING = true;
                player.getPacketSender().sendFadeScreen("", 2, 3);
                TaskManager.submit(new Task(3) {
                    @Override
                    public void execute() {
                        stop();
                        player.getPacketSender().sendInterfaceRemoval();
                        player.BLOCK_ALL_BUT_TALKING = false;
                        player.moveTo(ZANARIS);
                        if (getStage(player) == 3) {
                            increaseStage(player);
                            QuestManager.complete(player, quest, new String[]{"Access to Zanaris.", "Ability to equip the Dragon longsword."}, 772);
                        }
                    }
                });


                return false;
            }
        }
        switch (object.getId()) {
            case DRAMEN_TREE:
                if (getStage(player) == 2) {
                    if (!player.getQuest().spawnedTreeSpirit) {

                        if (!player.getInventory().containsAny(ItemID.BRONZE_AXE, ItemID.IRON_AXE, ItemID.STEEL_AXE, ItemID.MITHRIL_AXE, ItemID.ADAMANT_AXE, ItemID.RUNE_AXE)) {
                            player.getPacketSender().sendMessage("You need an axe to chop down this tree.", 1000);
                            return true;
                        }
                        if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 36) {
                            player.getPacketSender()
                                    .sendMessage("You do not have an axe which you have the Woodcutting level to use.", 1000);
                            return true;
                        }
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_COFFIN_USE, 60, TimeUnit.SECONDS, false, false)) {
                            return true;
                        }
                        if (player.getCombat().isInCombat()) {
                            player.sendMessage("You already have an active tree spawn.");
                            return true;
                        }
                        EntityExtKt.markTime(player, Attribute.LAST_COFFIN_USE);
                        NPC tree = NPCFactory.INSTANCE.create(SPIRIT_TREE, TREE_SPIRIT_SPAWN);


                        tree.setOwner(player);
                        World.getNpcAddQueue().add(tree);
                        tree.getMotion().followTarget(player);
                        tree.setEntityInteraction(player);
                        player.getQuest().spawnedTreeSpirit = true;

                        player.getPacketSender().sendMessage("NOTICE: You must kill the Tree Spirit to progress in this quest!");
/*
                    TaskManager.submit(new Task(3) {
                        @Override
                        protected void execute() {
                            if (tree.getOwner().getPosition().getDistance(tree.getPosition()) > 15) {
                                stop();
                                World.getNpcRemoveQueue().add(tree);
                                player.getQuest().spawnedTreeSpirit = false;
                            }
                        }
                    });*/
                        TaskManager.submit(new Task(100) {
                            @Override
                            protected void execute() {
                                if (!tree.getCombat().isInCombat()) {
                                    stop();
                                    World.getNpcRemoveQueue().add(tree);
                                    player.getQuest().spawnedTreeSpirit = false;
                                }
                            }
                        });
                        return true;
                    } else if (player.getQuest().spawnedTreeSpirit) {
                        if (player.getCombat().isInCombat() || player.getCombat().isUnderAttack()) {
                            player.sendMessage("You can't do that while in combat.");
                            return true;
                        }
                        player.getPacketSender().sendMessage("NOTICE: You must kill the Tree Spirit to progress in this quest!");
                        return true;
                    }
                    return true;
                } else if (getStage(player) == 3) {
                    if (!player.getInventory().containsAny(ItemID.BRONZE_AXE, ItemID.IRON_AXE, ItemID.STEEL_AXE, ItemID.MITHRIL_AXE, ItemID.ADAMANT_AXE, ItemID.RUNE_AXE)) {
                        player.getPacketSender().sendMessage("You need an axe to chop down this tree.", 1000);
                        return true;
                    }
                    if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 36) {
                        player.getPacketSender()
                                .sendMessage("You do not have an axe which you have the Woodcutting level to use.", 1000);
                        return true;
                    }
                    return false;
                }
        }
        return false;
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        if (npc.getId() == SPIRIT_TREE) {
            if (getStage(player) == 2) {
                increaseStage(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case 1166:
            case 1167:
            case 1165:
                travelToEntrana(player);
                return true;

        }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == ItemID.KNIFE && usedWith.getId() == ItemID.DRAMEN_BRANCH)
         || (use.getId() == ItemID.DRAMEN_BRANCH && usedWith.getId() == ItemID.KNIFE)) {

            if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) < 31) {
                DialogueManager.sendStatement(player, "You need a Crafting level of at least 31 to cut the branch.");
                return true;
            }


            player.performAnimation(new Animation(1248));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(new Task(2) {
                @Override
                public void execute() {
                    stop();
                    player.BLOCK_ALL_BUT_TALKING = false;
                    player.getInventory().delete(ItemID.DRAMEN_BRANCH, 1);
                    player.getInventory().add(ItemID.DRAMEN_STAFF, 1);
                    new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(772, 250)
                            .setText("You cut the branch into a Dramen staff!").start(player);
                    player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                }
            });

            return true;
        }
        return false;
    }


    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
