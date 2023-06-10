package com.grinder.game.content.quest.impl;

import com.grinder.game.World;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class DesertTreasure extends Quest {

    private static final int FAREED = 3456;

    private static final Position FAREED_SPAWN = new Position(3313, 9376);

    private static final int DAMIS = 6346;

    private static final int DESSOUS = 6344;

    private static final int KAMIL = 1129;

    private static final Position DESSOUS_SPAWN = new Position(3572, 3404);

    private static final Position DAMIS_SPAWN = new Position(2741, 5086);

    private static final Position KAMIL_SPAWN = new Position(2893, 3762);

    private static final Item ETCHINGS = new Item(4654);

    private static final Item TRANSLATION = new Item(4655);

    private static final Item MAGIC_LOGS = new Item(1513, 12);
    private static final Item MOLTEN_GLASS = new Item(1775, 6);
    private static final Item STEEL_BAR = new Item(2353, 6);
    private static final Item BLOOD_RUNE = new Item(565);
    private static final Item BONES = new Item(526);
    private static final Item CHARCOAL = new Item(973);
    private static final Item ASHES = new Item(592);

    private static final Item BLOOD_DIAMOND = new Item(4670);
    private static final Item ICE_DIAMOND = new Item(4671);
    private static final Item SMOKE_DIAMOND = new Item(4672);
    private static final Item SHADOW_DIAMOND = new Item(4673);
    private static final Item BLESSED_POT = new Item(4667);
    private static final Item[] ELBIS_REQUIRED = {
            MAGIC_LOGS, MOLTEN_GLASS, STEEL_BAR, BONES, BLOOD_RUNE, CHARCOAL, ASHES
    };

    private static final Item[] ALL_DIAMONDS = {
            BLOOD_DIAMOND, ICE_DIAMOND, SMOKE_DIAMOND, SHADOW_DIAMOND
    };

    public static void spawnDamis(Player p) {
        if (QuestManager.getStage(p, "Desert Treasure") != 5) {
            return;
        }

        if (p.getQuest().spawnedNpcs.size() > 0) {
            return;
        }

        NPC damis = NPCFactory.INSTANCE.create(DAMIS, DAMIS_SPAWN);

        damis.setOwner(p);

        World.getNpcAddQueue().add(damis);

        p.getQuest().spawnedNpcs.add(damis);

    }

    public DesertTreasure() {
        super("Desert Treasure", false, 3, 6);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {"",
                        "Rumours abound throughout Varrock and Al-Kharid that",
                        "an archaeologist has discovered clues to a hoard of",
                        "treasure hidden deep in the desert, south of Al-Kharid.",
                        "Anybody prepared to offer him assistance in his treasure",
                        "hunting could well find themselves benefitting from a",
                        "very large reward indeed...",
                        "",
                        "This quest requires the following:",
                        "",
                        QuestManager.hasLevel(player, Skill.THIEVING, 53),
                        QuestManager.hasLevel(player, Skill.MAGIC, 50),
                        QuestManager.hasLevel(player, Skill.FIREMAKING, 50),
                        QuestManager.hasLevel(player, Skill.SLAYER, 10),
                },
                {"",
                        "The archaeologist gave me some etchings to bring",
                        "to the expert in Varrock museum.",
                },
                {"",
                        "The expert read the etchings and gave me the",
                        "translations.. I should go back to the",
                        "archaeologist.",
                },
                {"",
                        "I should search the Bandit Camp..",
                },
                {"",
                        "I found someone interesting named Elbis.",
                        "He said to bring him:",
                        "",
                        QuestManager.hasItem(player, MAGIC_LOGS, "12 Magic logs"),
                        QuestManager.hasItem(player, MOLTEN_GLASS, "6 Molten glass"),
                        QuestManager.hasItem(player, STEEL_BAR, "6 Steel bars"),
                        QuestManager.hasItem(player, BLOOD_RUNE, "1 Blood rune"),
                        QuestManager.hasItem(player, BONES, "1 Bones"),
                        QuestManager.hasItem(player, CHARCOAL, "1 Charcoal"),
                        QuestManager.hasItem(player, ASHES, "1 Ashes"),

                },
                {"",
                        "Eblis spoke of diamonds and said to bring them",
                        "to him when I have found all four:",
                        "",
                        QuestManager.hasItem(player, BLOOD_DIAMOND, "Blood Diamond"),
                        QuestManager.hasItem(player, ICE_DIAMOND, "Ice Diamond"),
                        QuestManager.hasItem(player, SMOKE_DIAMOND, "Smoke Diamond"),
                        QuestManager.hasItem(player, SHADOW_DIAMOND, "Shadow Diamond"),
                },
                {""},

        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{684, 3639, 688, 686};
    }

    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 7) {
            increaseStage(player);
            sendDialogue(player, 9);
        } else if (id == 12) {
            player.getInventory().add(ETCHINGS);
            sendDialogue(player, 14);
        } else if (id == 15) {
            boolean hasEtchings = player.getInventory().contains(ETCHINGS);

            sendDialogue(player, hasEtchings ? 18 : 17);
        } else if (id == 19) {
            player.getInventory().add(ETCHINGS);
            sendDialogue(player, 21);
        } else if (id == 41) {
            increaseStage(player);
            player.getInventory().add(TRANSLATION);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 43) {
            boolean hasManual = player.getInventory().contains(TRANSLATION);

            sendDialogue(player, hasManual ? 45 : 46);
        } else if (id == 47) {
            player.getInventory().add(TRANSLATION);

            sendDialogue(player, 49);
        } else if (id == 66) {
            player.getInventory().delete(ETCHINGS);
            player.getInventory().delete(TRANSLATION);

            increaseStage(player);
            sendDialogue(player, 68);
        } else if (id == 117) {
            increaseStage(player);
            sendDialogue(player, 119);
        } else if (id == 129) {
            boolean hasAll = player.getInventory().contains(ELBIS_REQUIRED);

            sendDialogue(player, hasAll ? 132 : 131);
        } else if (id == 132) {
            increaseStage(player);
            player.getInventory().delete(ELBIS_REQUIRED);
            sendDialogue(player, 134);
        } else if (id == 136) {
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 143) {
            boolean hasDiamonds = player.getInventory().contains(ALL_DIAMONDS);

            sendDialogue(player, hasDiamonds ? 146 : 145);
        } else if (id == 149) {
            boolean hasCoins = player.getInventory().contains(995, 200);

            sendDialogue(player, hasCoins ? 151 : 152);

            if (hasCoins) {
                player.getInventory().delete(995, 200);
                player.getInventory().add(BLESSED_POT);
            }
        } else if (id == 153) {
            player.getInventory().delete(ALL_DIAMONDS);
            sendDialogue(player, 155);
        } else if (id == 155) {
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{"Ability to use Ancient Magicks"}, 4675);
        }
    }

    @Override
    public boolean hasRequirements(Player player) {
        if (player.getSkillManager().getCurrentLevel(Skill.THIEVING) < 53) {
            return false;
        }
        if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) < 50) {
            return false;
        }
        if (player.getSkillManager().getCurrentLevel(Skill.FIREMAKING) < 50) {
            return false;
        }
        if (player.getSkillManager().getCurrentLevel(Skill.SLAYER) < 10) {
            return false;
        }
        return true;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 6440:
                player.moveTo(new Position(2874, 3720));

                if (getStage(player) != 5) {
                    return true;
                }

                QuestManager.despawnNpcs(player);

                NPC kamil = NPCFactory.INSTANCE.create(KAMIL, KAMIL_SPAWN);

                kamil.setOwner(player);

                World.getNpcAddQueue().add(kamil);

                player.getQuest().spawnedNpcs.add(kamil);
                return true;
            case 6451:
            case 6452:
                if (getStage(player) == 5) {
                    player.moveTo(new Position(3305, 9376));

                    if (player.getQuest().spawnedNpcs.size() == 0) {
                        NPC fareed = NPCFactory.INSTANCE.create(FAREED, FAREED_SPAWN);

                        fareed.setOwner(player);

                        World.getNpcAddQueue().add(fareed);

                        player.getQuest().spawnedNpcs.add(fareed);
                    }
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if (item.getId() == BLESSED_POT.getId()) {
            if (object.getId() == 6437) {
                player.getInventory().delete(BLESSED_POT);

                NPC dessous = NPCFactory.INSTANCE.create(DESSOUS, DESSOUS_SPAWN);

                dessous.setOwner(player);

                World.getNpcAddQueue().add(dessous);

                player.getQuest().spawnedNpcs.add(dessous);

                player.getPacketSender().sendEntityHint(dessous);
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
