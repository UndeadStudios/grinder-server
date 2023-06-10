package com.grinder.game.content.skill.skillable.impl.hunter_new.catching;

import com.google.common.collect.ImmutableSet;
import com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables.*;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.box.BoxTrap;
import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.random.RandomUtil;
import com.grinder.util.random.Utils;

import java.util.HashMap;
import java.util.Map;

public final class Impling {

    public static boolean ENABLED_LOOTING_JARS = true;

    public static boolean ENABLED_CATCHING_IMPS = true;

    public static void configure() {
        for(ImplingData data : ImplingData.VALUES) {
            NPCActions.INSTANCE.onClick(new int[]{data.npcId}, action -> {
                catchImpling(action.getPlayer(), action.getNpc(), data);
                return true;
            });
        }

        for(ImplingJar data : ImplingJar.VALUES) {
            ItemActions.INSTANCE.onClick(new int[]{data.itemID}, action -> {
                if(!ENABLED_LOOTING_JARS) {
                    action.getPlayer().sendMessage("Looting impling jars is currently disabled.");
                    return true;
                }
                if(action.isInInventory() && !action.isDropAction()) {
                    if (action.getPlayer().getInventory().countFreeSlots() < 2) {
                        action.getPlayer().sendMessage("You need at least 2 spaces in your pack before attempting to catch this impling barehanded.");
                        return true;
                    }

                    ImplingJar jar = ImplingJar.get(action.getItemId());
                    Item[] items = jar.generateLoot(action.getPlayer());

                    action.getPlayer().getInventory().delete(jar.getJarItem());

                    if (items.length == 1) {
                        action.getPlayer().getInventory().add(items[0].getId(), items[0].getAmount());
                        return true;
                    }

                    action.getPlayer().getInventory().addItemSet(items);
                }
                return false;
            });
        }
    }

    public static void catchImpling(Player player, NPC npc, ImplingData impling) {
        if(!ENABLED_CATCHING_IMPS) {
            player.sendMessage("Catching implings is currently disabled.");
            return;
        }

        boolean barehands = !player.getEquipment().containsAny(ItemID.BUTTERFLY_NET, ItemID.MAGIC_BUTTERFLY_NET);

        if(barehands && player.getEquipment().isSlotOccupied(EquipSlot.WEAPON)) {
            player.sendMessage("You can't catch this impling barehanded while wielding something!");
            return;
        }

        if(!barehands && !player.getInventory().contains(ItemID.IMPLING_JAR)) {
            player.sendMessage("You don't have an empty impling jar in which to keep an impling.");
            return;
        }

        if(!player.getPosition().isWithinDistance(npc.getPosition().copy(), 1)) {
            return;
        }

        int hunterLevel = player.getSkillManager().getCurrentLevel(Skill.HUNTER);
        int levelReq = barehands ? impling.bareHandLevelReq : impling.levelReq;

        if(hunterLevel < levelReq) {
            player.sendMessage("You need a Hunter level of at least " + levelReq + " to catch this impling" + (barehands ? " barehanded." : "."));
            return;
        }

        if (barehands && player.getInventory().countFreeSlots() < 2) {
            player.sendMessage("You need at least 2 spaces in your pack before attempting to catch this impling barehanded.");
            return;
        }

        if(!player.getPosition().isWithinDistance(npc.getPosition(), 1)) {
            return;
        }

        npc.getMotion().clearSteps();

        player.setPositionToFace(npc.getPosition().copy());
        npc.setPositionToFace(player.getPosition().copy());

        player.BLOCK_ALL_BUT_TALKING = true;

        Animation animation = new Animation(barehands ? 7171 : player.getEquipment().contains(ItemID.BUTTERFLY_NET) ? 6606 : 6605);
        player.performAnimation(animation);

        boolean success = success(player, Math.min(npc.fetchDefinition().getCombatLevel() + (barehands ? 10 : 0), 99));

        TaskManager.submit(new Task(1, player, false) {
            @Override
            protected void execute() {
                stop();

                if(!barehands && !player.getInventory().contains(ItemID.IMPLING_JAR)) {
                    player.sendMessage("You don't have an empty impling jar in which to keep an impling.");
                    return;
                }

                if(success) {
                    npc.moveTo(new Position(0, 0, 0));
                    npc.appendDeath();
                    player.getSkillManager().addExperience(Skill.HUNTER, impling.worldExp * (barehands ? 1.2 : 1.0));

                    if(!barehands) {
                        player.getInventory().delete(ItemID.IMPLING_JAR, 1);
                        player.getInventory().add(impling.jarId, 1);
                    } else {
                        ImplingJar jar = ImplingJar.get(impling.jarId);
                        Item[] items = jar.generateLoot(player);

                        if(items.length == 1) {
                            player.getInventory().add(items[0].getId(), items[0].getAmount());
                            return;
                        }

                        player.getInventory().addItemSet(items);
                    }
                }
            }

            @Override
            public void onStop() {
                player.BLOCK_ALL_BUT_TALKING = false;
            }
        });

    }

    public static boolean success(final Player player, final int requiredLevel) {
        final boolean hasMagicNet = player.getInventory().contains(ItemID.MAGIC_BUTTERFLY_NET) || !player.getEquipment().isSlotOccupied(EquipSlot.WEAPON);
        final int level = player.getSkillManager().getCurrentLevel(Skill.HUNTER);
        final double n = Math.floor((306.0F * (level - 1.0F + (hasMagicNet ? 8 : 0))) / 98.0F) - requiredLevel;
        final double chance = n / 255.0F;
        final double rand = RandomUtil.RANDOM.nextDouble();
        return rand < chance;
    }

    private enum ImplingData {
        BABY(1635, 17, 27, 18, 20, 11238, 0, 10),
        YOUNG(1636, 22, 32, 20, 22, 11240, 0, 10),
        GOURMET(1637, 28, 38, 22, 24, 11242, 0, 10),
        EARTH(1638, 36, 46, 25, 27, 11244, 0, 10),
        ESSENCE(1639, 42, 52, 27, 29, 11246, 100, 10),
        ECLECTIC(1640, 50, 60, 30, 32, 11248, 0, 10),
        NATURE(1641, 58, 68, 34, 36, 11250, 100, 10),
        MAGPIE(1642, 65, 75, 44, 54, 11252, 50, 10),
        NINJA(1643, 74, 85, 50, 60, 11254, 40, 10),
        DRAGON(1644, 83, 93, 65, 75, 11256, 10, 10),
        LUCKY(7233, 89, 99, 0, 0, 19732, 1, 1);

        private static final ImmutableSet<ImplingData> VALUES = ImmutableSet.copyOf(values());

        public final int npcId, levelReq, bareHandLevelReq, jarId, puroPuroSpawnWeight, overworldSpawnWeight;
        public final double puroExp, worldExp;

        ImplingData(int npcId, int levelReq, int bareHandLevelReq, double puroExp, double worldExp, int jarId, int puroPuroSpawnWeight, int overworldSpawnWeight) {
            this.npcId = npcId;
            this.levelReq = levelReq;
            this.bareHandLevelReq = bareHandLevelReq;
            this.puroExp = puroExp;
            this.worldExp = worldExp;
            this.jarId = jarId;
            this.puroPuroSpawnWeight = puroPuroSpawnWeight;
            this.overworldSpawnWeight = overworldSpawnWeight;
        }
    }

    public enum ImplingJar {
        BABY(ItemID.BABY_IMPLING_JAR, BabyImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_BEGINNER, 50), new ClueDrop(ItemID.SCROLL_BOX_EASY, 100)),
        YOUNG(ItemID.YOUNG_IMPLING_JAR, YoungImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_BEGINNER, 25), new ClueDrop(ItemID.SCROLL_BOX_EASY, 50)),
        GOURMET(ItemID.GOURMET_IMPLING_JAR, GourmetImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_EASY, 25)),
        EARTH(ItemID.EARTH_IMPLING_JAR, EarthImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_MEDIUM, 100)),
        ESSENCE(ItemID.ESSENCE_IMPLING_JAR, EssenceImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_MEDIUM, 50)),
        ECLECTIC(ItemID.ECLECTIC_IMPLING_JAR, EclecticImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_MEDIUM, 25)),
        NATURE(ItemID.NATURE_IMPLING_JAR, NatureImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_HARD, 100)),
        MAGPIE(ItemID.MAGPIE_IMPLING_JAR, MagpieImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_HARD, 50)),
        NINJA(ItemID.NINJA_IMPLING_JAR, NinjaImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_HARD, 25)),
        DRAGON(ItemID.DRAGON_IMPLING_JAR, DragonImplingJarTable.table, new ClueDrop(ItemID.SCROLL_BOX_ELITE, 50)),
//        LUCKY(ItemID.LUCKY_IMPLING_JAR, null) {
//            @Override
//            public Item[] generateLoot(Player player) {
//                Item[] items = LuckyImplingJarTable.table.rollRewards(player);
//                for (Item item : items) {
//                    player.getCollectionLog().add(item);
//                }
//                return items;
//            }
//        };
        ;

        public static final ImmutableSet<ImplingJar> VALUES = ImmutableSet.copyOf(values());
        public static final Map<Integer, ImplingJar> jars;
        private static final ImplingJar[] values = values();

        static {
            Utils.populateMap(values, jars = new HashMap<>(values.length), ImplingJar::getItemID);
        }

        private final int itemID;
        private final DropTable table;
        private final ClueDrop[] clueDrops;

        ImplingJar(int ItemID, DropTable table, ClueDrop... clueDrops) {
            this.itemID = ItemID;
            this.table = table;
            this.clueDrops = clueDrops;
        }

        public static ImplingJar get(final int id) {
            return jars.get(id);
        }

        public Item getJarItem() {
            return new Item(itemID);
        }

        public Item[] generateLoot(Player player) {
            for (ClueDrop drop : clueDrops) {
                if (Utils.random(drop.getChance()) == 0) {
                    return new Item[] {table.rollItem(), new Item(drop.getItemID())};
                }
            }
            return new Item[] {table.rollItem()};
        }

        public int getItemID() {
            return this.itemID;
        }

        public DropTable getTable() {
            return this.table;
        }

        public ClueDrop[] getClueDrops() {
            return this.clueDrops;
        }
    }


    private static class ClueDrop {
        private final int ItemID;
        private final int chance;

        public ClueDrop(final int ItemID, final int chance) {
            this.ItemID = ItemID;
            this.chance = chance;
        }

        public int getItemID() {
            return this.ItemID;
        }

        public int getChance() {
            return this.chance;
        }
    }
}
