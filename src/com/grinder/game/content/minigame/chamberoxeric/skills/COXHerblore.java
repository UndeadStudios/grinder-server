package com.grinder.game.content.minigame.chamberoxeric.skills;

import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXHerblore extends PacketInteraction {

    public enum COXOverload {

        OVERLOAD_(ItemID.OVERLOAD___4_, 60, ItemID.NOXIFER, Arrays.asList(new Item(ItemID.ELDER___4_), new Item(ItemID.TWISTED___4_), new Item(ItemID.KODAI___4_)), 34),

        OVERLOAD(ItemID.OVERLOAD_4_2, 75, ItemID.NOXIFER, Arrays.asList(new Item(ItemID.ELDER_POTION_4_), new Item(ItemID.TWISTED_POTION_4_), new Item(ItemID.KODAI_POTION_4_)), 50),

        OVERLOAD_PLUS(ItemID.OVERLOAD__PLUS_4_, 90, ItemID.NOXIFER, Arrays.asList(new Item(ItemID.ELDER__PLUS_4_), new Item(ItemID.TWISTED__PLUS_4_), new Item(ItemID.KODAI__PLUS_4_)), 67);

        private int product;

        private int levelRequired;

        private int herb;

        private List<Item> secondary;

        private int experience;

        COXOverload(int product, int levelRequired, int herb, List<Item> secondary, int experience) {
            this.product = product;
            this.levelRequired = levelRequired;
            this.herb = herb;
            this.secondary = secondary;
            this.experience = experience;
        }

        private static final COXOverload[] VALUES = values();

        public static COXOverload getOverload(Player p) {
            COXOverload overload = null;
            int lvl = COXManager.getAccumulativeLevel(p, Skill.HERBLORE);
            for (COXOverload o : VALUES) {
                if (lvl >= o.levelRequired) {
                    overload = o;
                }
            }
            return overload;
        }

        static {
            for(COXOverload o : VALUES) {
                COXManager.RAID_ITEMS.add(o.product);
            }
        }
    }

    public enum Combination {

        ELDER(Arrays.asList(
                new HerbloreCombination(ItemID.ELDER___4_, 47, 7),
                new HerbloreCombination(ItemID.ELDER_POTION_4_, 59, 10),
                new HerbloreCombination(ItemID.ELDER__PLUS_4_, 70, 13)
        ), ItemID.GOLPAR, ItemID.STINKHORN_MUSHROOM),

        TWISTED(Arrays.asList(
                new HerbloreCombination(ItemID.TWISTED___4_, 47, 7),
                new HerbloreCombination(ItemID.TWISTED_POTION_4_, 59, 10),
                new HerbloreCombination(ItemID.TWISTED__PLUS_4_, 70, 13)
        ), ItemID.GOLPAR, ItemID.CICELY),

        KODAI(Arrays.asList(
                new HerbloreCombination(ItemID.KODAI___4_, 47, 7),
                new HerbloreCombination(ItemID.KODAI_POTION_4_, 59, 10),
                new HerbloreCombination(ItemID.KODAI__PLUS_4_, 70, 13)
        ), ItemID.GOLPAR, ItemID.ENDARKENED_JUICE),

        REVITALISATION(Arrays.asList(
                new HerbloreCombination(ItemID.REVITALISATION___4_, 52, 14),
                new HerbloreCombination(ItemID.REVITALISATION_POTION_4_, 65, 20),
                new HerbloreCombination(ItemID.REVITALISATION__PLUS_4_, 78, 27)
        ), ItemID.BUCHU_LEAF, ItemID.STINKHORN_MUSHROOM),

        PRAYER_ENHANCE(Arrays.asList(
                new HerbloreCombination(ItemID.PRAYER_ENHANCE___4_, 52, 14),
                new HerbloreCombination(ItemID.PRAYER_ENHANCE_4_, 65, 20),
                new HerbloreCombination(ItemID.PRAYER_ENHANCE__PLUS_4_, 78, 27)
        ), ItemID.BUCHU_LEAF, ItemID.CICELY),

        XERIC_AID(Arrays.asList(
                new HerbloreCombination(ItemID.XERICS_AID___4_, 52, 14),
                new HerbloreCombination(ItemID.XERICS_AID_4_, 65, 20),
                new HerbloreCombination(ItemID.XERICS_AID__PLUS_4_, 78, 27)
        ), ItemID.BUCHU_LEAF, ItemID.ENDARKENED_JUICE),

        ANTIPOISON(Arrays.asList(
                new HerbloreCombination(ItemID.ANTIPOISON_4, 60, 14),
                new HerbloreCombination(ItemID.ANTIPOISON_POTION_4, 75, 20),
                new HerbloreCombination(ItemID.ANTIPOISON_4_25765, 90, 27)
        ), ItemID.NOXIFER, ItemID.CICELY),

        ;

        private List<HerbloreCombination> combinations;

        private int herb;

        private int secondary;

        Combination(List<HerbloreCombination> combinations, int herb, int secondary) {
            this.combinations = combinations;
            this.herb = herb;
            this.secondary = secondary;
        }

        private static final Combination[] VALUES = values();

        private static final HashMap<Integer, Combination> FOR_SEC = new HashMap<>();

        private static HerbloreCombination getCombination(Player p, int secondary) {
            Combination combination = Combination.FOR_SEC.get(secondary);

            if (combination == null) {
                return null;
            }

            HerbloreCombination found = null;

            int lvl = COXManager.getAccumulativeLevel(p, Skill.HERBLORE);

            for (HerbloreCombination combo : combination.combinations) {
                if (lvl >= combo.levelRequired) {
                    if (p.getInventory().contains(combination.herb)) {
                        found = combo;
                    }
                }
            }
            return found;
        }

        static {
            for (Combination b : VALUES) {
                FOR_SEC.put(b.secondary, b);
                for(HerbloreCombination c : b.combinations) {
                    COXManager.RAID_ITEMS.add(c.id);
                }
                COXManager.RAID_ITEMS.add(b.secondary);
                COXManager.RAID_ITEMS.add(b.herb);
            }
        }
    }

    private static boolean mix(Player p, int id) {
        HerbloreCombination combination = Combination.getCombination(p, id);

        if (combination == null) {
            p.getPacketSender().sendMessage("No possible action found, perhaps, your Herblore level it too low.");
            return false;
        }

        Combination combo = Combination.FOR_SEC.get(id);

        if (combo == null) {
            p.getPacketSender().sendMessage("Invalid secondary potion.");
            return false;
        }

        if (!Skill.hasCorrectLevel(p, Skill.HERBLORE, combination.levelRequired)) {
            return true;
        }

        if (!p.getInventory().contains(new Item[]{new Item(combo.herb), new Item(combo.secondary)})) {
            p.getPacketSender().sendMessage("You need the primary clean herb and secondary ingredients to mix a potion.");
            return true;
        }

        p.getInventory().delete(combo.herb, 1);
        p.getInventory().delete(combo.secondary, 1);
        p.getInventory().delete(ItemID.WATER_FILLED_GOURD_VIAL, 1);

        p.getInventory().add(combination.id, 1);

        p.getSkillManager().addExperience(Skill.HERBLORE, combination.experience);

        p.getCOX().points += combination.levelRequired;

        p.getPacketSender().sendMessage("You mix the potion and create a " + ItemDefinition.getName(combination.id) + " potion.");

        p.performAnimation(new Animation(363));
        return true;
    }

    private static boolean mixOverload(Player p) {
        COXOverload overload = COXOverload.getOverload(p);

        if (overload == null) {
            return false;
        }

        if (!Skill.hasCorrectLevel(p, Skill.HERBLORE, overload.levelRequired)) {
            return true;
        }

        if (!p.getInventory().contains(overload.secondary)) {
            p.getPacketSender().sendMessage("You are missing secondary ingredients to the potion.");
            return true;
        }

        if (!p.getInventory().contains(overload.herb)) {
            p.getPacketSender().sendMessage("You are missing noxifer as the main ingredient to the overload potion.");
            return true;
        }

        p.getInventory().delete(overload.secondary);
        p.getInventory().delete(overload.herb, 1);
        p.getInventory().delete(ItemID.WATER_FILLED_GOURD_VIAL, 1);

        p.getSkillManager().addExperience(Skill.HERBLORE, overload.experience);

        p.getPacketSender().sendMessage("You combine all the potions and make an overload potion.");

        p.performAnimation(new Animation(363));

        p.getInventory().add(overload.product, 1);

        p.getCOX().points += overload.levelRequired;
        return true;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (usedWith.getId() == ItemID.WATER_FILLED_GOURD_VIAL) {
            if(mix(player, use.getId())) {
                return true;
            }
            if(mixOverload(player)) {
                return true;
            }
            return true;
        }
        return false;
    }

    private static class HerbloreCombination {
        private int id;
        private int levelRequired;
        private int experience;

        public HerbloreCombination(int id, int levelRequired, int experience) {
            this.id = id;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }
    }
}
