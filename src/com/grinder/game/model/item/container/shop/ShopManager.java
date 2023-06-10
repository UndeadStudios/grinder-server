package com.grinder.game.model.item.container.shop;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.grinder.Config;
import com.grinder.game.World;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.definition.*;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.syntax.impl.BuyX;
import com.grinder.game.model.interfaces.syntax.impl.SellX;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ShopRestockTask;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseStoreSales;
import com.grinder.util.*;

public class ShopManager extends ShopIdentifiers {

    public static final int SHOP_CURRENCY = 24126;

    /**
     * A {@link Map} with all of our shops and their ids.
     */
    public static final Map<Integer, Shop> shops = new HashMap<Integer, Shop>();

    /**
     * Attempts to open the shop with the given id.
     */
    public static void open(Player player, int id) {
        Shop shop = shops.get(id);
        if (shop != null) {
            open(player, shop, true);
        }
    }

    /**
     * Items that are forbidden to be bought by iron man players based on the given item id.
     */

    public static final int[] FORBIDDEN_SHOP_ITEMS_IRONMAN = new int[]{
            ItemID.RUNE_ESSENCE_2,
            ItemID.PURE_ESSENCE_2,
            10586, 5574, 5575, 5576, 11126, ItemID.CLIMBING_BOOTS,
            ItemID.MYSTIC_HAT, ItemID.MYSTIC_BOOTS, ItemID.MYSTIC_GLOVES, ItemID.MYSTIC_ROBE_TOP,
            ItemID.MYSTIC_ROBE_BOTTOM,
            //ItemID.COIF,
            //ItemID.STUDDED_BODY,
            //ItemID.STUDDED_CHAPS,
            ItemID.SPIKY_VAMBRACES,
            ItemID.FROG_LEATHER_BODY,
            ItemID.FROG_LEATHER_BOOTS,
            ItemID.FROG_LEATHER_CHAPS,
            ItemID.SNAKESKIN_BANDANA,
            ItemID.SNAKESKIN_BODY,
            ItemID.SNAKESKIN_BOOTS,
            ItemID.SNAKESKIN_CHAPS,
            ItemID.SNAKESKIN_VAMBRACES,
            22272, 22269,
            ItemID.ADAMANT_ARROW,
            ItemID.BLACK_KNIFE,
            ItemID.BLACK_DART,
            ItemID.MITHRIL_THROWNAXE,
            ItemID.ADAMANT_THROWNAXE,
            ItemID.RUNE_THROWNAXE,
            ItemID.ANTIFIRE_POTION_3_2,
            ItemID.ANTIPOISON_3_2,
            ItemID.ZAMORAK_BREW_3_2,
            ItemID.ENERGY_POTION_3_2,
            ItemID.RUNE_PICKAXE,
            ItemID.UNCUT_EMERALD_2,
            ItemID.UNCUT_RUBY_2,
            ItemID.UNCUT_DIAMOND_2,
            ItemID.GREEN_DRAGONHIDE_2,
            ItemID.BLUE_DRAGONHIDE_2,
            ItemID.RED_DRAGONHIDE_2,
            ItemID.BLACK_DRAGONHIDE_2,
            ItemID.GRIMY_GUAM_LEAF_2, 202, 204, 206, 208, 210, 212, 214, 216, // Herbs
            ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED, ItemID.HARRALANDER_SEED, ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED,
            ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED, ItemID.DWARF_WEED_SEED,
            1351, 1352, 1267, 1268, 1349, 1353, 1361, 1355, 1357, 1359, 1513, 1515, 1517, 1519, 1521, 1511,
            4587, 5698, 3204, 1305, 1377, 1434, 1478, 1725, 1727, 1729, 1731, 1712,
            3749, 3751, 3753, 3755, 863, 865, 866, 867, 868, 830, 1321, 1323,
            1325, 1327, 1329, 1331, 1333, 1363, 1365, 1367, 1369, 1371, 1373, 554,
            555, 556, 557, 558, 559, 560, 561, 556, 565, 564, 563, 562,
            /*9140, 9141, 9142, 9143, 9144,*/ 1099, 1135, 1065, 2493, 2499, 2487, 316,
            320, 326, 348, 334, 340, 352, 330, 362, 366, 374, 398, 386, 380, 374,
            122, 116, 134, 146, 158, 164, 170, 3043, 140, 1153, 1115, 1067, 1191,
            1157, 1119, 1069, 1193, 1159, 1121, 1071, 1197, 1161, 1123, 1073, 1199,
            1127, 1079, 1201, 1081, 1083, 1085, 1091, 1093, 2570, 11090, 2550,
            2351, 2353, 2355, 2357, 2359, 2361, 2363, 2572, 2441, 2437, 2443, 2445, 3041,
            2435, 2552, 2435, 3025, 6686, 2449, 5953, 10926, 2453, 4418, 7946, 7947, 3144, 3145,
            526, 527, 528, 529, 534, 535, 536, 537, 3123, 3124, 4830, 4831, 4832, 4833, 4834, 3835, 6812,
            6813, 6729, 6730, 11943, 11944, 22124, 22125, 1137, 1138, 1101, 1102, 1141, 1142, 1105, 1106,
            1177, 1178, 1143, 1144, 1109, 1110, 1181, 1182, 1145, 1146, 1111, 1112, 1183, 1184,
            1147, 1148, 1113, 1114, 1185, 1186, 1432, 1433, 1213, 1214, 1319, 1320, 1215, 1216, 2947, 2948,
            1704, 1705, 808, 810, 811, 2489, 2490, 2495, 2496, 2501, 2502, 379, 315, 316, 317, 318,
            321, 322, 345, 346, 347, 348, 333, 334, 335, 336, 329, 330, 359, 360, 363, 364, 391, 392,
            1269, 1270, 1271, 1272, 1273, 1274, 1276, 1353, 1354, 1355, 1356, 1357, 1358, 1359, 1360,
            20527, 12898, 10833, 10944,
            1249,
            6525, 1155, 1117, 1075, 1189, 1139, 1103, 1087, 1139,
            1167, 1129, 1095, 1063, 1061, 1131, 888, 892, 853,
            /* 6326, 6322, 6324, 6330,*/ // Snakeskin cannot be made thus it must be purchaseable by iron man for now
            9141, 9142, 9143, 9144, 9305, 9181, 9183, 810, 801, 802,
            // Seed shop below to be enabled when we have perfect farming
            // 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304,
            // Herblore store to be enabled when herbloring is 1:1 and u can find herbs in game
            // 199, 201, 203, 205, 207, 209, 211, 213, 215, 217, 219, 3049, 3051, 2485, 200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 3050, 3052, 2486,
            1694, 1696, 1698, 1700, 356, 11937, 13342, 15891,
            11331, 11333, 22827, 22830, 22836, 21356, 3381, 5003, 13339, 21293, 2149, 10136, 5004
            // Later to add All staffs, Soul rune, god capes,
    };

    /**
     * Opens the given shop.
     *
     * @param player
     * @param shop
     */
    public static void open(Player player, Shop shop, boolean scrollReset) {
        // Update current shop
        player.setShop(shop);

        // TODO: Make it not show the FORBIDDEN_SHOP_ITEMS_IRONMAN when opening the store if your an iron man. Only the purchaseable items will show.

        int interfaceId;
        int titleId;
        int containerId;

        if (shop.isNewInterface()) {
            interfaceId = Shop.NEW_INTERFACE_ID;
            titleId = Shop.NEW_NAME_INTERFACE_CHILD_ID;
            containerId = Shop.NEW_ITEM_CHILD_ID;
        } else {
            interfaceId = Shop.INTERFACE_ID;
            titleId = Shop.NAME_INTERFACE_CHILD_ID;
            containerId = Shop.ITEM_CHILD_ID;
        }

        if (shop.getId() == 37) { // Slayer rewards shop
            if (!(player.getEnterSyntax() instanceof SellX || player.getEnterSyntax() instanceof BuyX)) {
                player.getPacketSender().sendInterfaceSet(SlayerManager.SHOP, Shop.INVENTORY_INTERFACE_ID - 1);
            }
            int size = (shop.getOriginalStock().length / 8) * 62;
            player.getPacketSender().sendScrollbarHeight(22_653, size);
            player.getPacketSender().sendInterfaceItems(SlayerManager.SHOP_CONTAINER, shop.getCurrentStock());

        } else {
            // Send shop items
            player.getPacketSender().sendInterfaceItems(containerId, shop.getCurrentStock());

            if (!(player.getEnterSyntax() instanceof SellX || player.getEnterSyntax() instanceof BuyX)) {
                player.getPacketSender().sendInterfaceSet(interfaceId, Shop.INVENTORY_INTERFACE_ID - 1);
            }
        }
        player.getPacketSender().sendItemContainer(player.getInventory(), Shop.INVENTORY_INTERFACE_ID);

        // Send shop name
        player.getPacketSender().sendString(titleId, shop.getName());

        // Reset scroll bar if needed.
        if (scrollReset && shop.isNewInterface()) {
            player.getPacketSender().sendInterfaceScrollReset(Shop.NEW_SCROLL_BAR_INTERFACE_ID);
        }

        sendPoints(player, shop);
        // Update player's status..
        player.setStatus(PlayerStatus.SHOPPING);
    }

    private static void sendPoints(Player player, Shop shop) {
        String currency = "";
        String description = "";
        switch (shop.getId()) {
            case DISCOUNTED_PREMIUM_STORE:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE:
            case DISCOUNTED_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE_IRONMAN:
            case PREMIUM_STORE:
            case PVP_PREMIUM_STORE:
            case PVP_PREMIUM_STORE_IRONMAN:
            case PREMIUM_CLUE_STORE:
            case HOLIDAY_PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE_IRONMAN:
            case LIMITED_SHOP:
            case HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES:
            case HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS:
            case HOLIDAY_PREMIUM_STORE_PETS_MISC:
            case HOLIDAY_PREMIUM_STORE_RESOURCES:
                currency = "Premium Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.PREMIUM_POINTS));
                break;
            case VOTING_STORE:
            case VOTING_STORE_IRONMAN:
                currency = "Voting Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.VOTING_POINTS));
                break;
            case SLAYER_REWARDS:
                currency = "Slayer Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.SLAYER_POINTS));
                break;
            case COMMENDATION_POINTS_EXCHANGE:
            case COMMENDATION_POINTS_EXCHANGE_2:
                currency = "Commendation Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.COMMENDATION));
                break;
            /*case SLAYER_EQUIPMENTS_STORE:
                currency = "Slayer Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.SLAYER_POINTS));
                break;*/
            case MINIGAME_STORE:
                currency = "Minigame Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.MINIGAME_POINTS));
                break;
            case SKILLING_POINTS_STORE:
                currency = "Skilling Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.SKILLING_POINTS));
                break;
            case THIEVING_MASTER_STORE:
                currency = "Thieving Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.THIEVING));
                break;
            case WOODCUTTING_STORE:
                currency = "Woodcutting Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.WOODCUTTING));
                break;
            case FLETCHING_SKILL_MASTER:
                currency = "Fletching Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.FLETCHING));
                break;
            case 34:
                currency = "Mining Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.MINING));
                break;
            case 35:
                currency = "Smithing Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.SMITHING));
                break;
            case 36:
                currency = "Fishing Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.FISHING));
                break;
            case MASTER_RUNECRAFTING_STORE:
                currency = "Runecrafting Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.RUNECRAFTING));
                break;
            case MASTER_FARMING_STORE:
                currency = "Farming Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.FARMING));
                break;
            case MASTER_CRAFTING_STORE:
                currency = "Crafting Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.CRAFTING));
                break;
            case MASTER_COOKING_STORE:
                currency = "Cooking Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.COOKING));
                break;
            case MASTER_PRAYER_STORE:
                currency = "Prayer Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.PRAYER));
                break;
            case MASTER_HERBLORE_STORE:
                currency = "Herblore Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.HERBLORE));
                break;
            case MASTER_AGILITY_STORE:
                currency = "Agility Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.AGILITY));
                break;
            case MASTER_FIREMAKING_STORE:
                currency = "Firemaking Points: "
                        + NumberFormat.getInstance().format(SkillTaskManager.getPoints(player, Skill.FIREMAKING));
                break;
            case PARTICIPATION_POINTS_EXCHANGE:
                currency = "Participation Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.PARTICIPATION_POINTS));
                break;
            case BOSS_CONTRACT_STORE:
                currency = "Boss Contract Points: "
                        + NumberFormat.getInstance().format(player.getPoints().get(Points.BOSS_CONTRACT_POINTS));
                break;
        }
        player.getPacketSender().sendString(SHOP_CURRENCY, currency);
    }

    /**
     * Refreshes the given shop for all players who are viewing it.
     */
    public static void refresh(Shop shop) {
        World.playerStream()
                .filter(player -> viewingShop(player, shop.getId()))
                .forEach(player -> open(player, shop, false));
    }

    /**
     * Attempts to price check an item.
     *
     * @param player   The player pricechecking.
     * @param itemId   The item id to price check.
     * @param slot     The item's slot.
     * @param shopItem Are we pricechecking a shop item or player item?
     */
    public static void priceCheck(Player player, int itemId, int slot, boolean shopItem) {
        // Get the shop..
        Shop shop = player.getShop();

        // First, we will attempt to verify the shop and the item.
        boolean flag = false;
        if (shop == null || player.getStatus() != PlayerStatus.SHOPPING
                || !Shop.isInterfaceOpen(player)) {
            flag = true;
        }

        // Searching, find the slot for the item
        if (player.getShop().getCurrentStock()[slot] != null && shopItem && player.getShop().getCurrentStock()[slot].getId() != itemId) {
            int searchSlot = player.getShop().getSlot(itemId, false);
            if (searchSlot > -1) {
                slot = searchSlot;
            } else {
                flag = true;
            }
        }

        if (shopItem && (slot >= player.getShop().getCurrentStock().length
                || player.getShop().getCurrentStock()[slot] == null
                || player.getShop().getCurrentStock()[slot].getId() != itemId)) {
            flag = true;
        } else if (!shopItem && (slot >= player.getInventory().capacity()
                || player.getInventory().getItems()[slot].getId() != itemId)) {
            flag = true;
        }

        // If we failed to verify, simply close the shop for the player.
        if (flag) {
            //player.getPacketSender().sendInterfaceRemoval();
            return;
        }
/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("You can't sell items to shops on this account.");
            return;
        }*/

        if (shop.getId() == ITEM_DISPLAY_STORE) {
            player.getPacketSender().sendMessage("You can't buy items from this store.");
            return;
        }

        if (player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't buy items from the shops in spawn game mode.");
            return;
        }

        // Check if the shop sells the item.
        if (!buysItems(shop, itemId) && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
            // If the player is trying to pricecheck an item in their inventory,
            // let them know they can't sell it here.
            // Only allow items to be sold to shops which sell them originally.
            if (!shopItem) {
                player.getPacketSender().sendMessage("You can't sell this item to this shop.", 1000);
                return;
            }
/*            if (player.getUsername().equals("Mod Hellmage") && !shopItem) {
                player.sendMessage("You can't sell items to shops on this account.");
                return;
            }*/
            if ((player.getGameMode().isAnyIronman()) && !shopItem) {
                player.getPacketSender().sendMessage("You can't sell items to shops as an Iron Man.", 1000);
                return;
            }
            if ((player.getGameMode().isSpawn()) && !shopItem) {
                player.getPacketSender().sendMessage("You can't sell items to shops in spawn game mode.", 1000);
                return;
            }
        }

        // Forbid Iron Man from Buying Certain Items
        if (player.getGameMode().isAnyIronman() && shopItem && shop.getId() != OUTLET_BLOOD_STORE && shop.getId() != PVP_STORE && shop.getId() != BLOOD_SKILLING_STORE
                    && (shop.getId() == CONSUMEABLES_STORE
                || shop.getId() == CRAFTING_STORE
                || shop.getId() == FARMING_STORE
                || shop.getId() == PURE_ITEMS_STORE
                || shop.getId() == RANGE_GEAR_STORE
                || shop.getId() == MAGE_STORE
                || shop.getId() == MELEE_STORE
                || shop.getId() == HERBLORING_STORE
                || shop.getId() == HIGH_PRIEST_STORE
                || shop.getId() == STARTER_SUPPLIES_STORE
                || shop.getId() == SKILLING_STORE
                || shop.getId() == QUICK_SUPPLIES_STORE
                || shop.getId() == WILDERNESS_ITEMS_STORE
                || shop.getId() == RUNECRAFTING_STORE
                )) {
            for (int forbiddenItem : FORBIDDEN_SHOP_ITEMS_IRONMAN) {
                if (itemId == forbiddenItem) {
                    player.getPacketSender().sendMessage("As an Iron Man you're not allowed to buy " + ItemDefinition.forId(itemId).getName() + ". You can still buy the same item from other stores in the game.");
                    return;
                }
            }
        }
        if ((player.getGameMode().isSpawn() && shop.getId() != GENERAL_STORE && shop.getId() != LIMITED_SHOP)) {
            player.getPacketSender().sendMessage("You can't buy items from stores in spawn game mode.", 1000);
            return;
        }
        if (player.getGameMode().isAnyIronman()) {
            if (ItemDefinition.forId(itemId).getName().contains(" tome") || itemId == ItemID.COMBAT_LAMP) {
                player.getPacketSender().sendMessage("Your game mode does not allow you to buy " + ItemDefinition.forId(itemId).getName() + ".");
                return;
            }
        }

        // Get the item's definition..
        ItemDefinition def = ItemDefinition.forId(itemId);

        if (def.getId() == 22482 || def.getId() == 22483) {
            player.getPacketSender().sendMessage("You can't sell this item to this shop.");
            return;
        }

        long itemValue = getItemValue(def, shop.getId());

        if (def.isNoted() && shop.getId() == GENERAL_STORE) {
            itemValue = getItemValue(ItemDefinition.forId(def.unNote()), shop.getId());
        }
        // Get the item's value..
        if (shop.getId() == OSRS_TOKENS_STORE) {
            itemValue = ItemValueDefinition.Companion.getValue(itemId, ItemValueType.OSRS_STORE);
        }

/*        if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!def.getName().contains(" premium points")) {
                itemValue /= 2;
            }
        }*/
/*                if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!def.getName().contains(" premium points")) {
                itemValue *= 0.75;
            }
        }*/
/*        if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!def.getName().contains(" premium points")) {
                itemValue *= 0.70;
            }
        }*/
        if (shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!def.getName().contains(" premium points")) {
                itemValue *= 0.50;
            }
        }
        // Get the currency's name..
        String currency = getCurrencyName(shop.getId());


        // If player isn't price checking a shop item..
        if (!shopItem) {

            // Player trying to price check an unsellable item.
            if ((!def.isSellable() && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) || (itemValue <= 1000 && shop.getId() != GENERAL_STORE)) {
                player.getPacketSender().sendMessage("You can't sell this item to this shop.", 1000);
                return;
            }

            itemValue = getGeneralStoreTaxAppliedItemValue(itemId, shop, itemValue);

            if (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) {
                for (Item isShopItem : shop.getCurrentStock()) {
                    if (isShopItem != null && itemId == isShopItem.getId()) {
                        itemValue = (int) (itemValue / 3.75);
                        String message = "" + def.getName() + "" + ": shop will buy for " + "" + Misc.insertCommasToNumber(Long.toString(itemValue)) + ": " + currency + ".";
                        player.getPacketSender().sendMessage(message);
                        return;
                    }
                }
            }
            if (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) {
                for (Item isShopItem : shop.getCurrentStock()) {
                    // if you call isShopItem here make sure to null check
                    player.getPacketSender().sendMessage("You can't sell this item to this shop.", 1000);
                    return;
                }
            }
        }

        if (def.getId() >= 15290 && def.getId() <= 15460 && shop.getId() == COMMENDATION_POINTS_EXCHANGE_2) {
            player.sendMessage("You can only obtain this item by upgrading your void gear.");
            return;
        }

        // Verify value..
        // if (itemValue <= 0 || currency == "coins") {
        if (itemValue <= 0) {
            String message = "" + def.getName() + "" + " is for free!";
            player.getPacketSender().sendMessage(message);
            return;
        }
        // Send value..
        if (currency.equals("coins") && shop.getId() != WILDERNESS_ITEMS_STORE && shop.getId() != HIGH_PRIEST_STORE && shop.getId() != OSRS_TOKENS_STORE
                && shop.getId() != OSRS_TOKENS_ONLY_STORE && shop.getId() != MEMBERS_STORE && shop.getId() != GAMBLING_STORE) {

            itemValue *= 3;


            if (shop.getId() == GENERAL_STORE && def.isNoted()) {
                itemValue *= getModifier(shop.getAmount(def.unNote(), false));
            } else if (shop.getId() == GENERAL_STORE && !def.isNoted()) {
                itemValue *= getModifier(shop.getAmount(itemId, false));
            }

            // Extra 10 multiplier for consumeables
            else if (currency == "coins" && shop.getId() == CONSUMEABLES_STORE || shop.getId() == FISHING_GUILD_SHOP || shop.getId() == FISHING_STORE || shop.getId() == HARRYS_FISHING_SHOP) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == SLAYER_EQUIPMENTS_STORE && itemValue <= 50_000) {
                itemValue *= 5;
            } else if (currency == "coins" && shop.getId() == SKILLING_STORE && itemValue <= 20_000) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for runes shop
            else if (currency == "coins" && shop.getId() == MAGE_RUNES_STORE || shop.getId() == MAGE_STORE || shop.getId() == BETTYS_MAGIC_EMPORIUM || shop.getId() == BABA_YAGAS_MAGIC_SHOP ||
                    shop.getId() == TZHAAR_MEJ_ROHS_RUNE_STORE || shop.getId() == AUBURYS_RUNE_SHOP || shop.getId() == REGATHS_WARES ||
                    shop.getId() == THYRIAS_WARES || shop.getId() == MAGIC_STALL || shop.getId() == VOID_KNIGHT_MAGIC_STORE
                    || shop.getId() == BATTLE_RUNES || shop.getId() == LUNDAILS_ARENA_SIDE_RUNE_SHOP) {
                itemValue *= 10;
            }
            // Extra 5 multiplier for herblore shop/crafting store
            else if (currency == "coins" && shop.getId() == HERBLORING_STORE || shop.getId() == CRAFTING_STORE || shop.getId() == FARMING_STORE) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for range store
            else if (currency == "coins" && shop.getId() == RANGE_GEAR_STORE) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == ANCIENT_WIZARD_STORE) {
                itemValue /= 10;
            }
            String message = "" + def.getName() + "" + (!shopItem ? ": shop will buy for " : ": currently costs ") + ""
                    + Misc.insertCommasToNumber(Long.toString(!shopItem ? itemValue : (long) (itemValue))) + " " + currency + ".";
            player.getPacketSender().sendMessage(message);
        } else if (currency == "coins" && (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) && shopItem) {
            String message = "" + def.getName() + "" + (!shopItem ? ": shop will buy for " : " currently costs ") + ""
                    + Misc.insertCommasToNumber(Long.toString(itemValue)) + ": " + currency + ".";
            player.getPacketSender().sendMessage(message);
        } else {
            String message = "" + def.getName() + "" + (!shopItem ? ": shop will buy for " : " currently costs ") + ""
                    + Misc.insertCommasToNumber(Long.toString(itemValue)) + ": " + currency + ".";
            player.getPacketSender().sendMessage(message);
        }
    }

    private static long getGeneralStoreTaxAppliedItemValue(int itemId, Shop shop, long itemValue) {
        if (shop.getId() == GENERAL_STORE && shop.getAmount(itemId, true) == 0) {
//				System.out.println(itemValue);
            itemValue /= 2;
            // 1k, 2.5k, 10k, 500k, 1m, 5m, 10m
            if (itemValue > 20_000_000) {
                itemValue /= 13;
            } else if (itemValue > 10_000_000) {
                itemValue /= 9;
            } else if (itemValue > 5_000_000) {
                itemValue /= 8;
            } else if (itemValue > 1_000_000) {
                itemValue /= 3;
            } else if (itemValue > 500_000) {
                itemValue /= 4;
            } else if (itemValue > 10_000) {
                itemValue /= 4;
            } else if (itemValue > 2000) {
                itemValue /= 5;
            }
            if (itemId == 2572 || itemId == 2573) {
                itemValue /= 19.7;
            }
        } else if (itemValue > 1 && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
            itemValue = (int) (itemValue * Shop.SALES_TAX_MODIFIER);
        }
        return itemValue;
    }

    public static void buyItem(Player player, int slot, int itemId, int amount) {

        // Get the shop..
        final Shop shop = player.getShop();

        if (shop == null)
            return;

        if (!Config.shopping_enabled) {
            player.sendMessage("The @red@[SHOPPING]</col> system has been switched @red@OFF</col> by the server administrator.");
            return;
        }

        // First, we will attempt to verify the shop and the item.
        boolean flag = false;

        if (player.getStatus() != PlayerStatus.SHOPPING || !Shop.isInterfaceOpen(player)) {
            flag = true;
        }

        // Searching, find the slot for the item
        if (shop.getCurrentStock()[slot] != null && shop.getCurrentStock()[slot].getId() != itemId) {
            int searchSlot = shop.getSlot(itemId, false);
            if (searchSlot > -1) {
                slot = searchSlot;
            } else {
                flag = true;
            }
        }

        if (slot >= shop.getCurrentStock().length || shop.getCurrentStock()[slot] == null
                || shop.getCurrentStock()[slot].getId() != itemId) {
            flag = true;
        }

        // Check if we failed the verification.
        if (flag) {
            return;
        }

        if (shop.getId() == ITEM_DISPLAY_STORE) {
            player.getPacketSender().sendMessage("You can't buy items from this store.");
            return;
        }

        if (player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't buy items from the shops in spawn game mode.");
            return;
        }

/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("You can't buy items to shops on this account.");
            return;
        }*/

        if (player.getGameMode().isAnyIronman() && shop.getSlot(itemId, true) == -1) {
            player.getPacketSender().sendMessage("You can't buy over-stocked items from the shops as an Iron Man.");
            return;
        }
        if (player.getGameMode().isSpawn() && shop.getSlot(itemId, true) == -1) {
            player.getPacketSender().sendMessage("You can't buy over-stocked items from the shops in spawn game mode.");
            return;
        }
        if (player.getGameMode().isSpawn() && shop.getId() == GENERAL_STORE && shop.getCurrentStock()[slot].getAmount() <= 999_999_999) { // Fix so Ironmans can't buy general store player sold items.
            player.getPacketSender().sendMessage("You can't buy general store items sold by players in spawn game mode.");
            return;
        }
        if (player.getGameMode().isAnyIronman() && shop.getId() == GENERAL_STORE && shop.getCurrentStock()[slot].getAmount() <= 999_999_999) { // Fix so Ironmans can't buy general store player sold items.
            player.getPacketSender().sendMessage("You can't buy general store items sold by players as an Iron Man.");
            return;
        }


        // Forbid Iron Man from Buying Certain Items
        if (player.getGameMode().isAnyIronman() && shop.getId() != OUTLET_BLOOD_STORE && shop.getId() != PVP_STORE && shop.getId() != BLOOD_SKILLING_STORE
                && (shop.getId() == CONSUMEABLES_STORE
                || shop.getId() == CRAFTING_STORE
                || shop.getId() == FARMING_STORE
                || shop.getId() == PURE_ITEMS_STORE
                || shop.getId() == RANGE_GEAR_STORE
                || shop.getId() == MAGE_STORE
                || shop.getId() == MELEE_STORE
                || shop.getId() == HERBLORING_STORE
                || shop.getId() == HIGH_PRIEST_STORE
                || shop.getId() == STARTER_SUPPLIES_STORE
                || shop.getId() == SKILLING_STORE
                || shop.getId() == QUICK_SUPPLIES_STORE
                || shop.getId() == WILDERNESS_ITEMS_STORE
                || shop.getId() == RUNECRAFTING_STORE)) {
            for (int forbiddenItem : FORBIDDEN_SHOP_ITEMS_IRONMAN) {
                if (itemId == forbiddenItem) {
                    player.getPacketSender().sendMessage("As an Iron Man you're not allowed to buy " + ItemDefinition.forId(itemId).getName() + ". You can still buy the same item from other stores in the game.");
                    return;
                }
            }
        }
        if (player.getGameMode().isAnyIronman()) {
            if (ItemDefinition.forId(itemId).getName().contains(" tome") || itemId == ItemID.COMBAT_LAMP) {
                player.getPacketSender().sendMessage("Your game mode does not allow you to buy " + ItemDefinition.forId(itemId).getName() + ".");
                return;
            }
        }

        if (itemId >= 15290 && itemId <= 15460 && shop.getId() == COMMENDATION_POINTS_EXCHANGE_2) {
            player.sendMessage("You can only obtain this item by upgrading your void gear.");
            return;
        }

        if (itemId == ItemID.RUNE_ESSENCE || itemId == ItemID.RUNE_ESSENCE_2 || itemId == ItemID.PURE_ESSENCE || itemId == ItemID.PURE_ESSENCE_2) {
            if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
                player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to buy this.");
                return;
            }
        }

        // Max buy limit..
        /*
         * if (amount > 5000) { player.getPacketSender().
         * sendMessage("You can only buy a maximum of 5000 at a time."); return;
         * }
         */

        // Get the item's definition..
        ItemDefinition itemDef = ItemDefinition.forId(itemId);

        // Get the item's value..
        long itemValue = getItemValue(itemDef, shop.getId());


        if (itemDef.isNoted() && shop.getId() == GENERAL_STORE) {
            itemValue = getItemValue(ItemDefinition.forId(itemDef.unNote()), shop.getId());
        }

        // Get the item's value..
        if (shop.getId() == OSRS_TOKENS_STORE) {
            itemValue = ItemValueDefinition.Companion.getValue(itemId, ItemValueType.OSRS_STORE);
        }

/*        if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!itemDef.getName().contains(" premium points")) {
                itemValue /= 2;
            }
        }*/
/*                if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!itemDef.getName().contains(" premium points")) {
                itemValue *= 0.75;
            }
        }*/

        if (shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!itemDef.getName().contains(" premium points")) {
                itemValue *= 0.50;
            }
        }

/*        if (shop.isLimitedShop() || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            if (!itemDef.getName().contains(" premium points")) {
                itemValue *= 0.70;
            }
        }*/

        /*
         * if (itemValue <= 0) { return; }
         */

        // Used for checking if a player
        // actually managed to buy an item.
        AtomicBoolean bought = new AtomicBoolean(false);

        // Start buying the item.
        //for (int i = amount; i > 0; i--) {

        // Get the player's currency amount..
        int currencyAmount = getCurrencyAmount(player, shop.getId());

        // Make sure the item is still in the shop..
        if (shop.getCurrentStock()[slot] == null) {
            return;
        }

        // The amount of this item in the shop.
        int shopItemAmount = shop.getCurrentStock()[slot].getAmount();

        // Verify the item's amount.
        if (shopItemAmount < 1/* && !deletesItems(shop.getId()) *//*&& ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEMS_VALUE) > 0*/) {
            player.getPacketSender().sendMessage("This item is currently out of stock. Come back later.");
            return;
        }

        // Inventory space..
        if (player.getInventory().isFull()) {
            if (!(itemDef.isStackable() && player.getInventory().contains(itemId)) && !(itemDef.getId() == 8465 && player.getInventory().contains(995)) && !(itemDef.getId() == 15198 && player.getInventory().contains(995))
                    && !(itemDef.getId() == 8322 && player.getInventory().contains(13307))) {
                player.getInventory().full();
                return;
            }
        }
        if (itemValue > 0) {
            String currency = getCurrencyName(shop.getId());
            /*
             * if (currency == "coins") { itemValue = 0; }
             */
            if (currency == "coins" && shop.getId() != WILDERNESS_ITEMS_STORE && shop.getId() != HIGH_PRIEST_STORE && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE && shop.getId() != MEMBERS_STORE
                    && shop.getId() != GAMBLING_STORE) {
                itemValue *= 3;
            }
            if (shop.getId() == GENERAL_STORE && itemDef.isNoted()) {
                itemValue *= getModifier(shop.getAmount(itemDef.unNote(), false));
            } else if (shop.getId() == GENERAL_STORE && !itemDef.isNoted()) {
                itemValue *= getModifier(shop.getAmount(itemId, false));
                // Extra 10 multiplier for consumeables
            } else if (currency == "coins" && shop.getId() == CONSUMEABLES_STORE || shop.getId() == FISHING_GUILD_SHOP || shop.getId() == FISHING_STORE || shop.getId() == HARRYS_FISHING_SHOP) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == SLAYER_EQUIPMENTS_STORE && itemValue <= 50_000) {
                itemValue *= 5;
            } else if (currency == "coins" && shop.getId() == SKILLING_STORE && itemValue <= 20_000) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for runes shop
            else if (currency == "coins" && shop.getId() == MAGE_RUNES_STORE || shop.getId() == MAGE_STORE || shop.getId() == BETTYS_MAGIC_EMPORIUM || shop.getId() == BABA_YAGAS_MAGIC_SHOP ||
                    shop.getId() == TZHAAR_MEJ_ROHS_RUNE_STORE || shop.getId() == AUBURYS_RUNE_SHOP || shop.getId() == REGATHS_WARES ||
                    shop.getId() == THYRIAS_WARES || shop.getId() == MAGIC_STALL || shop.getId() == VOID_KNIGHT_MAGIC_STORE
                    || shop.getId() == BATTLE_RUNES || shop.getId() == LUNDAILS_ARENA_SIDE_RUNE_SHOP) {
                itemValue *= 10;
            }
            // Extra 5 multiplier for herblore shop/crafting store
            else if (currency == "coins" && shop.getId() == HERBLORING_STORE || shop.getId() == CRAFTING_STORE || shop.getId() == FARMING_STORE) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for range store
            else if (currency == "coins" && shop.getId() == RANGE_GEAR_STORE) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == ANCIENT_WIZARD_STORE) {
                itemValue /= 10;
            }
        }

        // Check if we can afford the item or not.
        if (currencyAmount < itemValue) {
            String currency = getCurrencyName(shop.getId());
            player.getPacketSender().sendMessage("You don't have enough " + currency + " to buy that.");
            return;
        }

        int removedAmount = 0;

        // Handle actual purchase..
        if (!itemDef.isStackable()) {
            //Sets the amount to the maximum the player can buy with the given amount of coins in their inventory, if it's less than the amount they have.
            if (itemValue > 0) {
                if ((currencyAmount) / (itemValue) < amount)
                    amount = (int) Math.min(Integer.MAX_VALUE, currencyAmount / itemValue);
            }
            //Sets the amount to how many free inventory slots a player has
            if (itemId != 14158 && itemId != 14159 && itemId != 14160 && itemId != 14161 && itemId != 8322 && itemId != 15031 && itemId != 8465 && itemId != 15198
                    && itemId != 384) {
                if (amount > player.getInventory().countFreeSlots())
                    amount = player.getInventory().countFreeSlots();
            }
            if (amount == 0)
                return;
            if (amount > shopItemAmount && (shop.getId() == GENERAL_STORE || shop.isLimitedShop() || deletesItems(shop.getId())))
                amount = shopItemAmount;

            // Pre check for corrupted items
            if (shop.getId() == 2 || shop.getId() == 42 || shop.getId() == 381 || shop.getId() == VOTING_STORE || shop.getId() == VOTING_STORE_IRONMAN) {
                if (itemId == 15023 || itemId == 15024 || itemId == 15025 || itemId == 15020 || itemId == 15021 || itemId == 15022
                        || itemId == 15026 || itemId == 15027 || itemId == 15028 || itemId == 15029 || itemId == 15030 || itemId == 15160
                        || itemDef.getName().contains("Vesta's") || itemDef.getName().contains("Statius's") || itemDef.getName().contains("Zuriel's") || itemDef.getName().contains("Morrigan's")) {
                    long finalItemValue = itemValue;
                    int finalAmount = amount;
                    boolean showInterface;
                    new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(itemId, 200)
                            .setText("The item you are trying to buy @red@degrades</col> after being used.", "Are you absolutely sure you want to buy it?")
                            .add(DialogueType.OPTION).setOptionTitle("Select an Option.")
                            .firstOption("Proceed.", player2 -> {
                                // Inventory space..
                                if (player.getInventory().isFull()) {
                                    if (!(itemDef.isStackable() && player.getInventory().contains(itemId)) && !(itemDef.getId() == 8465 && player.getInventory().contains(995)) && !(itemDef.getId() == 15198 && player.getInventory().contains(995))
                                            && !(itemDef.getId() == 8322 && player.getInventory().contains(13307))) {
                                        player.getInventory().full();
                                        return;
                                    }
                                }

                                // Check if we can afford the item or not.
                                if (currencyAmount < finalItemValue) {
                                    String currency = getCurrencyName(shop.getId());
                                    player.getPacketSender().sendMessage("You don't have enough " + currency + " to buy that.");
                                    return;
                                }


                                // Deduct player's currency..
                                decrementCurrency(player, finalItemValue * finalAmount, shop.getId());

                                player.getInventory().add(itemId, finalAmount);
                                bought.set(true);
                                player.getPacketSender().sendInterfaceRemoval();

                                Logging.log("corruptedBuying", "" + player.getUsername() + " bought: " + itemDef.getName() + " x " + finalAmount + " of value: " + finalItemValue + " from store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                            }).addCancel("Cancel.").start(player);
                    return;
                }
            }

            // Deduct player's currency..
            decrementCurrency(player, itemValue * amount, shop.getId());

  /*          // Remove item from shop..
            if (shop.getId() == GENERAL_STORE && shop.getAmount(itemId, true) == 0) {
                shop.removeItem(itemId, amount);
            }*/
            removedAmount = amount;
            // Add item to player's inventory..
            player.getInventory().add(itemId, amount);
            if (!(shop.getId() >= 3 && shop.getId() <= 11)) {
                Logging.log("Shopbuying", "" + player.getUsername() + " bought: " + itemDef.getName() + " x " + amount + " of value: " + itemValue + " from store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
            }

            // Flag as bought..
            bought.set(true);

            if (shop.getId() == AQUAIS_NEIGE_STORE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Aquais Neige", new Item(itemId, amount));
            } else if (shop.getId() == GAMBLING_STORE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Medallion Casino", new Item(itemId, amount));
            } else if (shop.getId() == COMMENDATION_POINTS_EXCHANGE || shop.getId() == COMMENDATION_POINTS_EXCHANGE_2) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(itemId, amount));
            } else if (shop.getId() == CASTLEWARS_TICKET_EXCHANGE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Castle Wars", new Item(itemId, amount));
            } else if (shop.getId() == PREMIUM_CLUE_STORE || shop.getId() == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shop.getId() == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS || shop.getId() == HOLIDAY_PREMIUM_STORE_PETS_MISC || shop.getId() == HOLIDAY_PREMIUM_STORE_RESOURCES) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Premium Store", new Item(itemId, amount));
            } else if (shop.getId() == GADRIN_MINING_MASTER || shop.getId() == MINING_STORE || shop.getId() == WOODCUTTING_STORE || shop.getId() == MASTER_SMITHING_STORE || shop.getId() == MASTER_FIREMAKING_STORE || shop.getId() == MASTER_COOKING_STORE
                    || shop.getId() == MASTER_CRAFTING_STORE || shop.getId() == MASTER_PRAYER_STORE || shop.getId() == MASTER_RUNECRAFTING_STORE
                    || shop.getId() == MASTER_FARMING_STORE || shop.getId() == FISHING_STORE_MASTER || shop.getId() == THIEVING_MASTER_STORE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Skilling Masters Outfits", new Item(itemId, amount));
            } else if (shop.getId() == LIMITED_SHOP) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Limited Items", new Item(itemId, amount));
            } else if (shop.getId() == SLAYER_REWARDS) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Slayer Rewards", new Item(itemId, amount));
            } else if (shop.getId() == VOTING_STORE || shop.getId() == VOTING_STORE_IRONMAN || shop.getId() == VOTING_STORE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Voting Rewards", new Item(itemId, amount));
            } else if (shop.getId() == AGILITY_TICKET_EXCHANGE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Agility Tickets", new Item(itemId, amount));
            } else if (shop.getId() == GRACES_GRACEFUL_CLOTHING) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Grace's Graceful Clothing", new Item(itemId, amount));
            } else if (shop.getId() == SKILLING_POINTS_STORE) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Skilling Points ", new Item(itemId, amount));
            } else if (shop.getId() == PROSPECTOR_PERCYS_NUGGET_SHOP) {
                player.getCollectionLog().createOrUpdateEntry(player,  "Motherlode Mine", new Item(itemId, amount));
            } else {

                player.getCollectionLog().createOrUpdateEntry(player,  shop.getName(), new Item(itemId, amount));
            }
        } else {
            if (itemValue > 0) {
                int canBeBought = (int) Math.min(Integer.MAX_VALUE, currencyAmount / itemValue);

                    if (canBeBought >= amount) {
                        canBeBought = amount;
                    }
                // Make sure player can't buy more than the stock amount
                // allows.
                if (canBeBought >= shopItemAmount && shop.getId() != VOTING_STORE && shop.getId() != VOTING_STORE_IRONMAN && shop.getId() != PVP_STORE && shop.getId() != HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_PETS_MISC
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_RESOURCES) {
                    canBeBought = deletesItems(shop.getId()) ? shopItemAmount : shopItemAmount - 1;
                }

                //stops player from buying more than what's in the stock
                if (shop.getId() != VOTING_STORE && shop.getId() != VOTING_STORE_IRONMAN && shop.getId() != PVP_STORE && shop.getId() != HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_PETS_MISC
                        && shop.getId() != HOLIDAY_PREMIUM_STORE_RESOURCES) {
                    if ((shop.isLimitedShop() || deletesItems(shop.getId())) && canBeBought > shopItemAmount) {
                        canBeBought = shopItemAmount;
                    }
                }

				/*if (shop.getId() == VOTING_STORE && canBeBought == 0 && itemId == 8465) {
					canBeBought = (currencyAmount) / (itemValue);
				}*/
                if (canBeBought == 0)
                    return;
                if (PlayerUtil.isMember(player) && canBeBought > 2500) {
                    player.getPacketSender().sendMessage("The buying limit of shops is restricted to a maximum quantity of 2,500 at a time.");
                    return;
                } else if (!PlayerUtil.isMember(player) && canBeBought > 1000) {
                    player.getPacketSender().sendMessage("The buying limit of shops is restricted to a maximum quantity of 1,000 at a time.");
                    return;
                }
                // Deduct player's currency..
                decrementCurrency(player, itemValue * canBeBought, shop.getId());

   /*             // Remove items from shop..
                if (shop.getId() == GENERAL_STORE && shop.getAmount(itemId, true) == 0) {
                    shop.removeItem(itemId, canBeBought);
                }*/


                // Add items to player's inventory..
                player.getInventory().add(itemId, canBeBought);
                if (!(shop.getId() >= 3 && shop.getId() <= 11) && !player.getGameMode().isSpawn()) {
                    Logging.log("Shopbuying", "" + player.getUsername() + " bought: " + itemDef.getName() + " x " + canBeBought + " of value: " + itemValue + " from store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                }
                removedAmount = canBeBought;

                // Flag as bought..
                bought.set(true);
            } else {
                bought.set(true);
                player.getInventory().add(itemId, amount);
                if (!(shop.getId() >= 3 && shop.getId() <= 11) && !player.getGameMode().isSpawn()) {
                    Logging.log("Shopbuyingfree", "" + player.getUsername() + " bought: " + itemDef.getName() + " x " + amount + " of value: " + itemValue + " from store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                }
            }
            //return;
            //}
        }

        if (bought.get()) {
            if (deletesItems(shop.getId())) {
                shop.removeItem(itemId, removedAmount);
            }
            if (shop.isLimitedShop()) {
                //shop.removeItem(itemId, removedAmount);
                LimitedShop.saveStore();
            }
            if (!shop.isRestocking()) {
                TaskManager.submit(new ShopRestockTask(shop));
                shop.setRestocking(true);
            }
            sendPoints(player, shop);
            ShopManager.refresh(shop);
        }

        if (shop.getId() == SLAYER_REWARDS) {
            player.getPacketSender().sendString(62101,
                    "" + NumberFormat.getInstance().format(player.getPoints().get(Points.SLAYER_POINTS)));
        }
    }

    public static void sellItem(Player player, int slot, int itemId, int amount) {

        // Get the shop..
        Shop shop = player.getShop();

        // First, we will attempt to verify the shop and the item.
        boolean flag = false;

        if (shop == null || player.getStatus() != PlayerStatus.SHOPPING
                || !Shop.isInterfaceOpen(player)) {
            flag = true;
        } else if (slot >= player.getInventory().capacity()
                || player.getInventory().getItems()[slot].getId() != itemId) {
            flag = true;
        }
        if (amount <= 0) {
            return;
        }
        if (!Config.shopping_enabled) {
            player.sendMessage("The @red@[SHOPPING]</col> system has been switched @red@OFF</col> by the server administrator.");
            return;
        }
/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("You can't sell items to shops on this account.");
            return;
        }*/
        if (player.getMinigame() != null) {
            Logging.log("sellitemdupe", "" + player.getUsername() + " tried to sell item while inside a minigame " + player.getMinigame() + " " + player.getPosition() + "");
            PlayerUtil.broadcastPlayerDeveloperMessage("<img=750>" + player.getUsername() + " tried to sell item while inside a minigame " + player.getMinigame() + " " + player.getPosition() + "");
            return;
        }

        // Check if we failed the verification.
        if (flag) {
            return;
        }

        if (shop.getId() == ITEM_DISPLAY_STORE) {
            player.getPacketSender().sendMessage("You can't buy items from this store.");
            return;
        }

        if (player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't sell items to shops in spawn game mode.");
            return;
        }

        // Check if shop buys items..
        if (!buysItems(shop, itemId) && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
            player.getPacketSender().sendMessage("You can't sell this item to this shop.");
            return;
        }

        final Item item = new Item(itemId, amount);

        if (item.hasAttributes()) {
            return;
        }

        if (item.getAmount() <= 0) {
            return;
        }

        if (item.getId() == 22482 || item.getId() == 22483) {
            player.getPacketSender().sendMessage("You can't sell this item to this shop.");
            return;
        }

        // Check if this item can be sold via their definition
        ItemDefinition itemDef = ItemDefinition.forId(itemId);

        if (!itemDef.isSellable() && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
            // player.getPacketSender().sendMessage("This item can't be
            // sold.");
            player.getPacketSender().sendMessage("You can't sell this item to this shop.");
            return;
        }
        if (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) {
            for (Item isShopItem : shop.getCurrentStock()) {
                if (isShopItem != null && itemId == isShopItem.getId()) {
                    if (player.getInventory().getAmount(itemId) < amount) {
                        amount = player.getInventory().getAmount(itemId);
                    }
                    // Check if player has the correct amount of the item
                    // they're trying to sell
                    int playerAmount = player.getInventory().getAmount(itemId);
                    if (amount > playerAmount)
                        amount = playerAmount;
                    if (amount == 0)
                        return;

                    // Only allow 5k max.
                    if (amount > 5000) {
                        player.getPacketSender().sendMessage("You can only sell a maximum of 5000 at a time.");
                        return;
                    }

                    // Get item value..
                    long itemValue = getItemValue(itemDef, shop.getId());
                    if (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) {
                        itemValue = (long) (ItemValueDefinition.Companion.getValue(itemDef.getId(), ItemValueType.OSRS_STORE) / 3.75);
                    }

                    // Apply taxes.
                    if (itemValue > 1 && shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
                        itemValue = (long) (itemValue * Shop.SALES_TAX_MODIFIER);
                    }
                    if (shop.getId() == OSRS_TOKENS_ONLY_STORE) {
                        itemValue /= 1000;
                    }
                    // Verify item value..
                    if (itemValue <= 0) {
                        player.getPacketSender().sendMessage("This item has no value.");
                        return;
                    } else if (shop.getId() != GENERAL_STORE && (itemValue > 0 && itemValue <= 1000)) {
                        player.getPacketSender().sendMessage("Item price too low to be sold.");
                        return;
                    }

                    // A flag which indicates if an item was sold.
                    boolean sold = false;

                    // Perform sale..
                    for (int amountRemaining = amount; amountRemaining > 0; amountRemaining--) {
                        // Check if the shop is full..
                        if (shop.isFull()) {
                            player.getPacketSender().sendMessage("The shop is currently full.");
                            break;
                        }

                        // Check if player still has the item..
                        if (!player.getInventory().contains(itemId)) {
                            break;
                        }
                        if (player.getInventory().getAmount(995) + itemValue < 0 || player.getInventory().getAmount(995) + itemValue > Integer.MAX_VALUE) {
                            player.getPacketSender().sendMessage("You couldn't hold anymore coins in your inventory.", 1000);
                            break;
                        }
                        // Verify inventory space..
                        if (player.getInventory().countFreeSlots() == 0) {
                            boolean allow = false;

                            // If we're selling the exact amount of what we have..
                            if (itemDef.isStackable()) {
                                if (amount == player.getInventory().getAmount(itemId)) {
                                    allow = true;
                                }
                            }

                            // If their inventory has the coins..
                            if (getCurrencyName(shop.getId()).equals("coins")) {
                                if (player.getInventory().contains(ItemID.COINS)) {
                                    allow = true;
                                }
                            } else if (getCurrencyName(shop.getId()).equalsIgnoreCase("blood money")) {
                                if (player.getInventory().contains(ItemID.BLOOD_MONEY)) {
                                    allow = true;
                                }
                            } else if (getCurrencyName(shop.getId()).toLowerCase().equals("platinum tokens")) {
                                if (player.getInventory().contains(ItemID.PLATINUM_TOKEN)) {
                                    allow = true;
                                }
                            }

                            if (!allow) {
                                player.getInventory().full();
                                break;
                            }
                        }
                        PlayerExtKt.tryRandomEventTrigger(player, 2.8F);

                        if (!itemDef.isStackable()) {
                            // Remove item from player's inventory..
                            player.getInventory().delete(itemId, 1);

                            // Log
                            Logging.log("Shopselling", "" + player.getUsername() + " sold: " + itemDef.getName() + " of value: " + itemValue + " to: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                            if (ItemUtil.logItemIfValuable(item)) {
                                StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.SHOP, player.getUsername(), " sold: @red@" + itemDef.getName() + "@bla@ with a value of " + Misc.insertCommasToNumber((int) itemValue) + " to " + shop.getName() + ".");
                            }

                            // Add player currency..
                            incrementCurrency(player, itemValue, shop.getId());

                            // Add item to shop..
                            if (shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
                                shop.addItem(itemId, 1);
                            }

                            // Random event
                            PlayerExtKt.tryRandomEventTrigger(player, 1.3F);
                            sold = true;
                        } else {

                            // Remove item from player's inventory..
                            player.getInventory().delete(itemId, amountRemaining);

                            // Log
                            Logging.log("Shopselling", "" + player.getUsername() + " sold: " + itemDef.getName() + " x " + amount + " of value: " + itemValue + " to: " + Misc.capitalizeWords(shop.getName().toLowerCase()));

                            if (ItemUtil.logItemIfValuable(item) || itemValue * amount > 10_000_000) {
                                StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.SHOP, player.getUsername(), " sold: @red@" + itemDef.getName() + "@bla@ x: @red@" + Misc.insertCommasToNumber(amount) + "@bla@ with a value of " + Misc.insertCommasToNumber((int) itemValue) + " to " + shop.getName() + ".");
                                // Db logging
                                new DatabaseStoreSales(
                                        SQLManager.Companion.getINSTANCE(),
                                        player.getUsername(),
                                        itemDef.getName(),
                                        amount,
                                        itemValue,
                                        (shop.getName() != null ? shop.getName() : " null")
                                ).schedule(player);
                            }

                            // Random event
                            PlayerExtKt.tryRandomEventTrigger(player, 1.3F);

                            // Add player currency..
                            incrementCurrency(player, itemValue * amountRemaining, shop.getId());

                            // Add item to shop..
                            if (shop.getId() != OSRS_TOKENS_STORE && shop.getId() != OSRS_TOKENS_ONLY_STORE) {
                                shop.addItem(itemId, amountRemaining);
                            }
                            sold = true;
                            if (sold) {
                                ShopManager.refresh(shop);
                            }
                            break;
                        }
                        // Refresh shop..
                        if (sold) {
                            ShopManager.refresh(shop);
                        }
                    }
                    return;
                }
            }
            player.sendMessage("You can't sell this item to this shop.", 1000);
            return;
        }

        if (player.getInventory().getAmount(itemId) < amount) {
            amount = player.getInventory().getAmount(itemId);
        }
        // Check if player has the correct amount of the item
        // they're trying to sell
        int playerAmount = player.getInventory().getAmount(itemId);
        if (amount > playerAmount)
            amount = playerAmount;
        if (amount == 0)
            return;

        // Only allow 250 max.
        if (amount > 250) {
            player.getPacketSender().sendMessage("You can only sell a maximum of 250 at a time.");
            return;
        }

        // Get item value..
        long itemValue = getItemValue(itemDef, shop.getId());

        if (itemDef.isNoted() && shop.getId() == GENERAL_STORE) {
            itemValue = getItemValue(ItemDefinition.forId(itemDef.unNote()), shop.getId());
        }

        if (itemValue <= 0) {
            player.sendMessage("This item has no value.");
            return;
        }
        if (shop.getId() == OSRS_TOKENS_STORE || shop.getId() == OSRS_TOKENS_ONLY_STORE) {
            itemValue = (int) (ItemValueDefinition.Companion.getValue(itemId, ItemValueType.OSRS_STORE) / 3.75);
        }

        // Apply taxes.
        itemValue = getGeneralStoreTaxAppliedItemValue(itemId, shop, itemValue);
        // Get the currency's name..
        String currency = getCurrencyName(shop.getId());

        // Send value..
        if (currency.equals("coins") && shop.getId() != WILDERNESS_ITEMS_STORE && shop.getId() != HIGH_PRIEST_STORE && shop.getId() != OSRS_TOKENS_STORE
                && shop.getId() != OSRS_TOKENS_ONLY_STORE && shop.getId() != MEMBERS_STORE && shop.getId() != GAMBLING_STORE) {

            itemValue *= 3;

            if (shop.getId() == GENERAL_STORE && itemDef.isNoted()) {
                itemValue *= getModifier(shop.getAmount(itemDef.unNote(), false));
            } else if (shop.getId() == GENERAL_STORE && !itemDef.isNoted()) {
                itemValue *= getModifier(shop.getAmount(itemId, false));
            }

            // Extra 10 multiplier for consumeables
            else if (currency == "coins" && shop.getId() == CONSUMEABLES_STORE || shop.getId() == FISHING_GUILD_SHOP || shop.getId() == FISHING_STORE || shop.getId() == HARRYS_FISHING_SHOP) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == SLAYER_EQUIPMENTS_STORE && itemValue <= 50_000) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for runes shop
            else if (currency == "coins" && shop.getId() == MAGE_RUNES_STORE || shop.getId() == MAGE_STORE || shop.getId() == BETTYS_MAGIC_EMPORIUM || shop.getId() == BABA_YAGAS_MAGIC_SHOP ||
                    shop.getId() == TZHAAR_MEJ_ROHS_RUNE_STORE || shop.getId() == AUBURYS_RUNE_SHOP || shop.getId() == REGATHS_WARES ||
                    shop.getId() == THYRIAS_WARES || shop.getId() == MAGIC_STALL || shop.getId() == VOID_KNIGHT_MAGIC_STORE
                    || shop.getId() == BATTLE_RUNES || shop.getId() == LUNDAILS_ARENA_SIDE_RUNE_SHOP) {
                itemValue *= 10;
            }
            // Extra 5 multiplier for herblore shop/crafting store
            else if (currency == "coins" && shop.getId() == HERBLORING_STORE || shop.getId() == CRAFTING_STORE || shop.getId() == FARMING_STORE) {
                itemValue *= 5;
            }
            // Extra 10 multiplier for range store
            else if (currency == "coins" && shop.getId() == RANGE_GEAR_STORE) {
                itemValue *= 10;
            } else if (currency == "coins" && shop.getId() == ANCIENT_WIZARD_STORE) {
                itemValue /= 10;
            }
        }

        // Verify item value..
        if (itemValue <= 0) {
            player.getPacketSender().sendMessage("This item has no value.");
            return;
        } else if (shop.getId() != GENERAL_STORE && itemValue <= 1000) {
            player.getPacketSender().sendMessage("Item price too low to be sold.");
            return;
        }

        // A flag which indicates if an item was sold.
        boolean sold = false;

        // Perform sale..
        for (int amountRemaining = amount; amountRemaining > 0; amountRemaining--) {
            // Check if the shop is full..
            if (shop.isFull()) {
                player.getPacketSender().sendMessage("The shop is currently full.");
                break;
            }

            // Check if player still has the item..
            if (!player.getInventory().contains(itemId)) {
                break;
            }

            // Overflow conscious
            if (getCurrencyName(shop.getId()).equals("coins")
                    && ((long) player.getInventory().getAmount(ItemID.COINS) + itemValue) > Integer.MAX_VALUE) {
                player.getPacketSender().sendMessage("Please bank some of your coins before trying to sell more items.");
                break;
            }

            // Verify inventory space..
            if (player.getInventory().countFreeSlots() == 0) {
                boolean allow = false;

                // If we're selling the exact amount of what we have..
                if (itemDef.isStackable()) {
                    if (amount == player.getInventory().getAmount(itemId)) {
                        allow = true;
                    }
                }

                // If their inventory has the coins..
                if (getCurrencyName(shop.getId()).equals("coins")) {
                    if (player.getInventory().contains(ItemID.COINS)) {
                        allow = true;
                    }
                }

                if (!allow) {
                    player.getInventory().full();
                    break;
                }
            }
            if (!itemDef.isStackable()) {
                // Remove item from player's inventory..
                player.getInventory().delete(itemId, 1);


                // Disable loggin for spawn game modes
                if (!player.getGameMode().isSpawn()) {

                    // Log
                    Logging.log("Shopselling", "" + player.getUsername() + " sold: " + itemDef.getName() + " x 1 of value: " + itemValue + " from store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                    if ((amount >= 100 || itemValue >= 500_000) && shop.getId() == GENERAL_STORE) {
                        Logging.log("GeneralStoreSelling", "" + player.getUsername() + " sold: " + itemDef.getName() + " x 1 of value: " + itemValue + " from general store");

                        if (DiscordBot.ENABLED)
                            DiscordBot.INSTANCE.sendServerLogs("[GENERAL STORE]: " + player.getUsername() + " sold: " + itemDef.getName() + " x 1 of value: " + NumberFormat.getInstance().format(itemValue) + " to general store");
                    }

                    if (ItemUtil.logItemIfValuable(item) || itemValue * amount > 10_000_000) {
                        // Db logging
                        new DatabaseStoreSales(
                                SQLManager.Companion.getINSTANCE(),
                                player.getUsername(),
                                itemDef.getName(),
                                amount,
                                itemValue,
                                (shop.getName() != null ? shop.getName() : " null")
                        ).schedule(player);

                    }
                }
                // Add player currency..
                incrementCurrency(player, itemValue, shop.getId());

                // Add item to shop..
                if (shop.getId() != OSRS_TOKENS_STORE || shop.getId() != OSRS_TOKENS_ONLY_STORE) {
                    if (ItemDefinition.forId(itemId).isNoted()) {
                        itemId = ItemDefinition.forId(itemId).unNote();
                    }
                    shop.addItem(itemId, 1);
                }
                sold = true;
            } else {

                // Remove item from player's inventory..
                player.getInventory().delete(itemId, amountRemaining);

                // Log
                int moneyMade = 0;
                moneyMade += itemValue * amountRemaining;
                // Disable loggin for spawn game modes
                if (!player.getGameMode().isSpawn()) {
                    if (moneyMade >= 25_000_000) {
                        StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.SHOP_PROFIT, player.getUsername(), " is selling " + itemDef.getName() + " X " + amountRemaining + " of value: " + itemValue * amountRemaining + " and has made > 25m of this.");
                    }
                    if ((amount >= 100 || itemValue >= 500_000) && shop.getId() == GENERAL_STORE) {
                        Logging.log("GeneralStoreSelling", "" + player.getUsername() + " sold: " + amount + " x " + itemDef.getName() + " with item value of " + itemValue + " to general store");
                        if (DiscordBot.ENABLED)
                            DiscordBot.INSTANCE.sendServerLogs("[GENERAL STORE]: " + player.getUsername() + " sold: " + itemDef.getName() + " x 1 of value: " + NumberFormat.getInstance().format(itemValue) + " to general store");
                    }
                    if (ItemUtil.logItemIfValuable(item) || itemValue * amount > 10_000_000) {
                        Logging.log("Shopselling", "" + player.getUsername() + " sold: " + itemDef.getName() + " x " + amountRemaining + " of value: " + NumberFormat.getInstance().format(itemValue * amountRemaining) + " to store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                        if (DiscordBot.ENABLED)
                            DiscordBot.INSTANCE.sendServerLogs("[REGULAR STORE]: " + player.getUsername() + " sold: " + itemDef.getName() + " x " + amountRemaining + " of value: " + NumberFormat.getInstance().format(itemValue * amountRemaining) + " to store: " + Misc.capitalizeWords(shop.getName().toLowerCase()));
                        // Db logging
                        new DatabaseStoreSales(
                                SQLManager.Companion.getINSTANCE(),
                                player.getUsername(),
                                itemDef.getName(),
                                amount,
                                itemValue,
                                (shop.getName() != null ? shop.getName() : " null")
                        ).schedule(player);
                    }
                }
                // Add player currency..
                incrementCurrency(player, itemValue * amountRemaining, shop.getId());

                // Add item to shop..
                if (shop.getId() != OSRS_TOKENS_STORE || shop.getId() != OSRS_TOKENS_ONLY_STORE) {
                    if (ItemDefinition.forId(itemId).isNoted()) {
                        itemId = ItemDefinition.forId(itemId).unNote();
                    }
                    shop.addItem(itemId, amountRemaining);
                }
                sold = true;
                ShopManager.refresh(shop);
                if (!shop.isRestocking()) {
                    TaskManager.submit(new ShopRestockTask(shop));
                    shop.setRestocking(true);
                }
                break;
            }
        }

        // Refresh shop..
        if (sold) {
            ShopManager.refresh(shop);
            if (!shop.isRestocking()) {
                TaskManager.submit(new ShopRestockTask(shop));
                shop.setRestocking(true);
            }
        }
    }

    /**
     * Get's the item value for a given item in a shop.
     */
    private static long getItemValue(ItemDefinition itemDef, int shopId) {

        final int itemId = itemDef.getId();

        if (shopId == GENERAL_STORE && shops.get(shopId).getAmount(itemDef.getId(), true) == 0) {
            return ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEM_PRICES) * 2;
        } else if (shopId == SLAYER_REWARDS) {
            switch (itemDef.getId()) {
                case ItemID.SLAYER_HOOD:
                case ItemID.SLAYER_CAPE:
                    return 250;
                case ItemID.SLAYER_CAPE_T_:
                    return 500;
                case ItemID.BLACK_MASK_10_:
                    return 150;
                case 15198:
                    return 8;

                case ItemID.SLAYER_RING_7_:
                    return 1;

                case ItemID.ROYAL_SEED_POD:
                    return 50;

                case ItemID.SLAYER_RING_8_:
                    return 5;
                case ItemID.SLAYER_RING_ETERNAL_:
                    return 50;

                case 4081:
                    return 35;

                case 10588:
                    return 50;

                case ItemID.BRACELET_OF_SLAUGHTER:
                    return 25;

                case ItemID.EXPEDITIOUS_BRACELET:
                    return 35;

                case 21817: // Bracelet of ethereum
                    return 25;

                case ItemID.RING_OF_WEALTH_SCROLL:
                    return 75;

                case 7780:
                    return 50;

                case 7783:
                    return 50;

                case 7789:
                    return 50;

                case 7798:
                    return 50;

                case 7793:
                    return 50;

                case 7785:
                    return 250;

                case 7786:
                    return 125;

                case 7787:
                    return 50;

                case 15273:
                    return 90;

                case 10586:
                    return 75;

                case 11924:
                    return 35;

                case 11926:
                    return 35;

                case 13229:
                    return 85;

                case 13227:
                    return 85;

                case 13231:
                    return 85;

                case 19707:
                    return 500;

                case 6585:
                    return 15;

                case 7462:
                    return 5;

                case 1052:
                    return 5;

                case 6714:
                    return 100;

                case 11665:
                    return 25;

                case 11664:
                    return 25;

                case 11663:
                    return 20;

                case 8840:
                    return 75;

                case 8839:
                    return 75;

                case 8842:
                    return 5;

                case 13072:
                    return 125;

                case 10146:
                    return 10;

                case 10147:
                    return 25;

                case 10148:
                    return 50;

                case 13073:
                    return 125;

                case 15154:
                    return 85;

                case 22978:
                    return 350;

                case 24268:
                    return 300;

                case 20724:
                    return 350;

                case 10858:
                    return 150;

                case 776:
                    return 55;

                case 777:
                    return 55;

                case 11902:
                    return 250;

                case 20727:
                    return 350;

                case 11200:
                    return 95;

                case 12863:
                    return 250;

                case 23206:
                    return 250;

                case 24271:
                    return 250;

                case 430:
                    return 75;

                case 6547:
                    return 75;

                case 12337:
                    return 80;

                case 19699:
                    return 55;

                case 4251:
                    return 50;

                case 13116:
                    return 125;

                case 22803:
                    return 15;

                case 22941:
                    return 25;

                case 22943:
                    return 25;

                case 22945:
                    return 25;

                case 22947:
                    return 25;
            }
        } else if (shopId == FISHING_STORE_MASTER) {
            switch (itemDef.getId()) {
                case ItemID.FISHING_HOOD:
                case ItemID.FISHING_CAPE:
                    return 1500;
                case ItemID.FISHING_CAPE_T_:
                    return 2000;
                case ItemID.ANGLER_TOP:
                    return 1250;
                case 22838:
                    return 2500;
                case ItemID.ANGLER_WADERS:
                    return 1100;
                case ItemID.ANGLER_BOOTS:
                    return 600;
                case ItemID.ANGLER_HAT:
                    return 800;
                case ItemID.DRAGON_HARPOON:
                    return 1500;
                case ItemID.INFERNAL_HARPOON:
                    return 3250;
                case 22842:
                    return 1000;
                case ItemID.RAW_SHARK:
                case ItemID.RAW_SHARK_2:
                    return 5;
                case ItemID.SANDWORMS_PACK:
                    return 120;
                default:
                    return 0;
            }
        } else if (shopId == FLETCHING_SKILL_MASTER) {
            switch (itemDef.getId()) {
                case ItemID.BRONZE_ARROWTIPS:
                    return 2;
                case ItemID.IRON_ARROWTIPS:
                    return 3;
                case ItemID.STEEL_ARROWTIPS:
                    return 5;
                case ItemID.MITHRIL_ARROWTIPS:
                    return 7;
                case ItemID.ADAMANT_ARROWTIPS:
                    return 8;
                case ItemID.RUNE_ARROWTIPS:
                    return 10;
                case ItemID.FLETCHING_HOOD:
                case ItemID.FLETCHING_CAPE:
                    return 1500;
                case ItemID.FLETCHING_CAPE_T_:
                    return 2000;
                case ItemID.ANGLER_TOP:
                    return 1250;
                case 22838:
                    return 2500;
                case ItemID.ANGLER_WADERS:
                    return 1100;
                case ItemID.ANGLER_BOOTS:
                    return 600;
                case ItemID.ANGLER_HAT:
                    return 800;
                case ItemID.DRAGON_HARPOON:
                    return 1500;
                case ItemID.INFERNAL_HARPOON:
                    return 3250;
                case 22842:
                    return 1000;
                case ItemID.RAW_SHARK:
                case ItemID.RAW_SHARK_2:
                    return 5;
                case ItemID.SANDWORMS_PACK:
                    return 120;
                default:
                    return 0;
            }
        } else if (shopId == WOODCUTTING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.WOODCUTTING_HOOD:
                case ItemID.WOODCUTTING_CAPE:
                    return 1500;
                case ItemID.WOODCUT_CAPE_T_:
                    return 2000;
                case ItemID._3RD_AGE_AXE:
                    return 5_000;
                case ItemID.INFERNAL_AXE:
                    return 3_250;
                case ItemID.DRAGON_AXE:
                    return 1_500;
                case ItemID.RUNE_AXE:
                    return 5;
                case ItemID.LUMBERJACK_HAT:
                    return 500;
                case ItemID.LUMBERJACK_TOP:
                case ItemID.LUMBERJACK_LEGS:
                    return 1250;
                case ItemID.LUMBERJACK_BOOTS:
                    return 350;
                default:
                    return 1000;
            }
        } else if (shopId == MINIGAME_STORE) { // Minigame store
            switch (itemDef.getId()) {

                case 15750:
                    return 10_000;
                case 15753:
                    return 2500;
                case ItemID.GOLDEN_ARMADYL_SPECIAL_ATTACK:
                    return 1500;
                case ItemID.GOLDEN_BANDOS_SPECIAL_ATTACK:
                case ItemID.GOLDEN_SARADOMIN_SPECIAL_ATTACK:
                case ItemID.GOLDEN_ZAMORAK_SPECIAL_ATTACK:
                    return 1000;
                case 24664:
                    return 1500;

                case 24666:
                    return 2500;

                case 24668:
                    return 2500;

                case 25001:
                    return 500;

                case 25004:
                    return 600;

                case 25007:
                    return 600;

                case 25010:
                    return 350;

                case 25013:
                    return 350;

                case 25110:
                    return 1000;

                case 25112:
                    return 1000;

                case 25114:
                    return 1000;

                case 25056:
                    return 3000;

                case 25322:
                    return 250;

                case 25324:
                    return 350;

                case 25326:
                    return 350;

                case 25330:
                    return 100;

                case 25328:
                    return 100;

                case 25332:
                    return 100;

                case 25334:
                    return 250;

                case 25042:
                    return 5000;

                case 25044:
                    return 3500;

                case 25046:
                    return 2500;

                case 25048:
                    return 1500;

                case 25050:
                    return 750;

                case 25054:
                    return 500;

                case 25052:
                    return 250;

                case 24862:
                    return 5000;

                case 26260:
                    return 1500;

                case 9017:
                    return 5;
                case 7651:
                    return 5;
                case 24034:
                    return 1500;

                case 24037:
                    return 1750;

                case 24040:
                    return 1750;

                case 24043:
                    return 500;

                case 24046:
                    return 500;

                case 23522:
                    return 1000;

                case 13283:
                    return 750;

                case 24315:
                    return 500;

                case 24317:
                    return 500;

                case 24319:
                    return 500;

                case 24321:
                    return 250;

                case 23785:
                    return 750;

                case 23787:
                    return 950;

                case 23789:
                    return 850;

                case 23911:
                    return 350;

                case 23913:
                    return 350;

                case 23915:
                    return 350;

                case 23917:
                    return 350;

                case 23919:
                    return 350;

                case 23921:
                    return 350;

                case 23923:
                    return 350;

                case 23925:
                    return 350;

                case 23995:
                case 23997:
                    return 5000;

                case 24009:
                    return 700;

                case 24012:
                    return 700;

                case 24021:
                    return 500;

                case 24003:
                    return 250;

                case 24006:
                    return 250;

                case 24015:
                    return 700;

                case 24018:
                    return 700;

                case 24027:
                    return 500;

                case 24413:
                    return 2500;

                case 24428:
                    return 1000;

                case 24430:
                    return 1000;

                case 24431:
                    return 1000;

                case 24130:
                    return 750;

                case 12783:
                    return 350;

                case 14158:
                    return 1500;

                case 11739:
                    return 50;

                case 15202:
                    return 3000;

                case 15272:
                    return 5000;

                case 23771:
                    return 20;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_SMITHING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.SMITHING_HOOD:
                case ItemID.SMITHING_CAPE:
                    return 1500;
                case ItemID.SMITHING_CAPE_T_:
                    return 2000;
                case ItemID.RUNITE_BAR_2:
                    return 7;
                case 23101:
                    return 750;
                case 23097:
                    return 1500;
                case 23095:
                    return 1250;
                case 23091:
                    return 500;
                case 23093:
                    return 350;
                case 23099:
                    return 850;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_CRAFTING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.CRAFTING_HOOD:
                case ItemID.CRAFTING_CAPE:
                    return 1500;
                case ItemID.CRAFTING_CAPE_T_:
                    return 2000;
                case ItemID.CRAB_CLAW:
                case ItemID.BLOODNTAR_SNELM:
                    return 2500;
                case ItemID.CRAB_HELMET:
                    return 1350;
                case ItemID.UNCUT_ZENYTE:
                case ItemID.UNCUT_ZENYTE_2:
                    return 500;
                case ItemID.MYRE_SNELM:
                case ItemID.OCHRE_SNELM:
                case ItemID.BRUISE_BLUE_SNELM:
                    return 950;
                case 21690:
                    return 15;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_FARMING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.FARMING_HOOD:
                case ItemID.FARMING_CAPE:
                    return 1500;
                case ItemID.FARMING_CAPE_T_:
                    return 2000;
                case ItemID.FARMERS_STRAWHAT:
                    return 750;
                case ItemID.FARMERS_JACKET:
                    return 1350;
                case ItemID.FARMERS_BORO_TROUSERS:
                    return 1250;
                case ItemID.FARMERS_BOOTS:
                    return 350;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_FIREMAKING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.FIREMAKING_HOOD:
                case ItemID.FIREMAKING_CAPE:
                    return 1500;
                case ItemID.FIREMAKING_CAPE_T_:
                    return 2000;
                case ItemID.PYROMANCER_HOOD:
                    return 1350;
                case ItemID.PYROMANCER_ROBE:
                case ItemID.PYROMANCER_GARB:
                    return 2500;
                case ItemID.PYROMANCER_BOOTS:
                    return 500;
                case ItemID.MAGIC_LOGS_2:
                    return 15;
                case ItemID.RED_LOGS:
                case ItemID.GREEN_LOGS:
                case ItemID.BLUE_LOGS:
                case ItemID.WHITE_LOGS:
                case ItemID.PURPLE_LOGS:
                    return 50;
                default:
                    return 500;
            }
        } else if (shopId == GRACES_GRACEFUL_CLOTHING) {
            switch (itemDef.getId()) {
                case 15198:
                case ItemID.SPOTTED_CAPE:
                case ItemID.SPOTTIER_CAPE:
                    return 50;
                case ItemID.GRACEFUL_HOOD:
                    return 100;
                case ItemID.PENANCE_GLOVES:
                    return 250;
                case ItemID.GRACEFUL_BOOTS:
                case ItemID.GRACEFUL_CAPE:
                    return 25;
                case ItemID.GRACEFUL_GLOVES:
                    return 50;
                case ItemID.GRACEFUL_TOP:
                case ItemID.GRACEFUL_LEGS:
                    return 150;
                case ItemID.BOOTS_OF_LIGHTNESS:
                    return 100;
                case ItemID.AMYLASE_PACK:
                    return 50;
                default:
                    return 1_000;
            }
        } else if (shopId == MASTER_HERBLORE_STORE) {
            switch (itemDef.getId()) {
                case ItemID.HERBLORE_HOOD:
                case ItemID.HERBLORE_CAPE:
                    return 1500;
                case ItemID.HERBLORE_CAPE_T_:
                    return 2000;
                case ItemID.TOADFLAX_POTION_UNF_2:
                    return 7;
                case ItemID.TORSTOL_POTION_UNF_2:
                    return 6;
                case ItemID.LANTADYME_POTION_UNF_2:
                    return 5;
                case ItemID.DWARF_WEED_POTION_UNF_2:
                    return 4;
                case ItemID.SNAPDRAGON_POTION_UNF_2:
                    return 3;
                case ItemID.GUAM_TAR:
                    return 2;
                case ItemID.MARRENTILL_TAR:
                    return 3;
                case ItemID.TARROMIN_TAR:
                    return 4;
                case ItemID.HARRALANDER_TAR:
                    return 5;
                case ItemID.SHORT_GREEN_GUY:
                    return 35;
                case ItemID.GREENMANS_ALE:
                    return 25;
                case ItemID.BOTANICAL_PIE:
                    return 50;
                case ItemID.ORANGE_SALAMANDER:
                    return 2;
                case ItemID.RED_SALAMANDER:
                    return 5;
                case ItemID.BLACK_SALAMANDER:
                    return 10;
                default:
                    return 1_000;
            }
        } else if (shopId == MASTER_AGILITY_STORE) {
            switch (itemDef.getId()) {
                case ItemID.AGILITY_HOOD:
                case ItemID.AGILITY_CAPE:
                    return 1500;
                case ItemID.AGILITY_CAPE_T_:
                    return 2000;
                case ItemID.MARK_OF_GRACE:
                    return 5;
                case ItemID.STAMINA_POTION_4_2:
                    return 25;
                case ItemID.AGILITY_POTION_4_2:
                    return 15;
                case ItemID.AGILITY_TOME_2:
                    return 1500;
                case ItemID.GRACEFUL_HOOD_4:
                    return 250;
                case ItemID.GRACEFUL_HOOD_6:
                    return 300;
                case ItemID.GRACEFUL_HOOD_8:
                    return 350;
                case ItemID.GRACEFUL_HOOD_10:
                    return 400;
                case ItemID.GRACEFUL_HOOD_12:
                    return 450;
                case ItemID.GRACEFUL_HOOD_14:
                    return 500;
                case ItemID.GRACEFUL_HOOD_16:
                    return 750;
                case ItemID.GRACEFUL_CAPE_4:
                    return 150;
                case ItemID.GRACEFUL_CAPE_6:
                    return 175;
                case ItemID.GRACEFUL_CAPE_8:
                    return 200;
                case ItemID.GRACEFUL_CAPE_10:
                    return 225;
                case ItemID.GRACEFUL_CAPE_12:
                    return 250;
                case ItemID.GRACEFUL_CAPE_14:
                    return 275;
                case ItemID.GRACEFUL_CAPE_16:
                    return 300;
                case ItemID.GRACEFUL_TOP_4:
                    return 650;
                case ItemID.GRACEFUL_TOP_6:
                    return 750;
                case ItemID.GRACEFUL_TOP_8:
                    return 850;
                case ItemID.GRACEFUL_TOP_10:
                    return 900;
                case ItemID.GRACEFUL_TOP_12:
                    return 1000;
                case ItemID.GRACEFUL_TOP_14:
                    return 1250;
                case ItemID.GRACEFUL_TOP_16:
                    return 1500;
                case ItemID.GRACEFUL_LEGS_4:
                    return 600;
                case ItemID.GRACEFUL_LEGS_6:
                    return 650;
                case ItemID.GRACEFUL_LEGS_8:
                    return 750;
                case ItemID.GRACEFUL_LEGS_10:
                    return 850;
                case ItemID.GRACEFUL_LEGS_12:
                    return 900;
                case ItemID.GRACEFUL_LEGS_14:
                    return 1000;
                case ItemID.GRACEFUL_LEGS_16:
                    return 1250;
                case ItemID.GRACEFUL_GLOVES_4:
                    return 125;
                case ItemID.GRACEFUL_GLOVES_6:
                    return 150;
                case ItemID.GRACEFUL_GLOVES_8:
                    return 175;
                case ItemID.GRACEFUL_GLOVES_10:
                    return 200;
                case ItemID.GRACEFUL_GLOVES_12:
                    return 225;
                case ItemID.GRACEFUL_GLOVES_14:
                    return 250;
                case ItemID.GRACEFUL_GLOVES_16:
                    return 275;
                case ItemID.GRACEFUL_BOOTS_4:
                    return 125;
                case ItemID.GRACEFUL_BOOTS_6:
                    return 150;
                case ItemID.GRACEFUL_BOOTS_8:
                    return 175;
                case ItemID.GRACEFUL_BOOTS_10:
                    return 200;
                case ItemID.GRACEFUL_BOOTS_12:
                    return 225;
                case ItemID.GRACEFUL_BOOTS_14:
                    return 250;
                case ItemID.GRACEFUL_BOOTS_16:
                    return 275;
                default:
                    return 1_000;
            }
        } else if (shopId == RUNECRAFTING_STORE) { // Runecrafting store mage of zamorak
            switch (itemDef.getId()) {
                case ItemID.RUNE_ESSENCE:
                case ItemID.RUNE_ESSENCE_2:
                    return 1500;
                case ItemID.PURE_ESSENCE:
                case ItemID.PURE_ESSENCE_2:
                    return 3000;
                case ItemID.RING_OF_THE_ELEMENTS:
                    return 15_000_000;
                case 5509:
                    return 10000000;
                case 5510:
                    return 25000000;
                case 5512:
                    return 50000000;
                case 5514:
                    return 65000000;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_PRAYER_STORE) {
            switch (itemDef.getId()) {
                case ItemID.ZEALOTS_HELM:
                    return 500;
                case ItemID.ZEALOTS_ROBE_TOP:
                    return 1500;
                case ItemID.ZEALOTS_ROBE_BOTTOM:
                    return 1500;
                case ItemID.ZEALOTS_BOOTS:
                    return 250;
                case ItemID.PRAYER_HOOD:
                case ItemID.PRAYER_CAPE:
                    return 1500;
                case ItemID.PRAYER_CAPE_T_:
                    return 2000;
                case ItemID.BABYDRAGON_BONES:
                case ItemID.BABYDRAGON_BONES_2:
                    return 4;
/*                case ItemID.DRAGON_BONES:
                case ItemID.DRAGON_BONES_2:
                    return 9;
                case ItemID.SHAIKAHAN_BONES:
                case ItemID.SHAIKAHAN_BONES_2:
                    return 12;*/
                case ItemID.WYVERN_BONES:
                case ItemID.WYVERN_BONES_2:
                    return 9;
                case ItemID.FAYRG_BONES:
                case ItemID.FAYRG_BONES_2:
                    return 15;
                case ItemID.LAVA_DRAGON_BONES:
                case ItemID.LAVA_DRAGON_BONES_2:
                    return 17;
                case ItemID.RAURG_BONES:
                case ItemID.RAURG_BONES_2:
                    return 19;
                case ItemID.DAGANNOTH_BONES:
                case ItemID.DAGANNOTH_BONES_2:
                    return 42;
                case ItemID.OURG_BONES:
                case ItemID.OURG_BONES_2:
                    return 48;
                case 22124:
                case 22125:
                    return 52;
                case 15261:
                case 15262:
                    return 110;
                default:
                    return 500;
            }
        } else if (shopId == MASTER_COOKING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.COOKING_HOOD:
                case ItemID.COOKING_CAPE:
                    return 1500;
                case ItemID.COOKING_CAPE_T_:
                    return 2000;
                case ItemID.CHEFS_HAT:
                    return 250;
                case ItemID.BROWN_APRON:
                    return 0;
                case ItemID.WHITE_APRON:
                    return 200;
                case ItemID.GOLDEN_APRON:
                case ItemID.GOLDEN_CHEFS_HAT:
                    return 5000;
                case ItemID.COOKING_GAUNTLETS:
                    return 500;
                case ItemID.CHEFS_DELIGHT:
                    return 50;
                case ItemID.STEW:
                case ItemID.STEW_2:
                    return 25;
                default:
                    return 550;
            }
        } else if (shopId == MASTER_RUNECRAFTING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.RUNECRAFTING_HOOD:
                case ItemID.RUNECRAFT_CAPE:
                    return 1500;
                case ItemID.RUNECRAFT_CAPE_T_:
                    return 2000;
                case ItemID.DECORATIVE_ARMOUR_13:
                    return 1750;
                case ItemID.DECORATIVE_ARMOUR_11:
                    return 3500;
                case ItemID.DECORATIVE_ARMOUR_12:
                    return 2500;
                case ItemID.ABYSSAL_BRACELET_5_:
                    return 250;
                case ItemID.AIR_RUNE:
                case ItemID.MIND_RUNE:
                case ItemID.FIRE_RUNE:
                case ItemID.WATER_RUNE:
                case ItemID.EARTH_RUNE:
                case ItemID.BODY_RUNE:
                    return 1;
                case ItemID.CHAOS_RUNE:
                    return 2;
                case ItemID.DEATH_RUNE:
                    return 3;

                case ItemID.MIST_RUNE:
                case ItemID.STEAM_RUNE:
                case ItemID.DUST_RUNE:
                case ItemID.MUD_RUNE:
                case ItemID.SMOKE_RUNE:
                case ItemID.LAVA_RUNE:
                    return 10;
                case ItemID.WRATH_RUNE:
                    return 18;
                default:
                    return 500;
            }
        } else if (shopId == GAMBLING_STORE) {
            switch (itemDef.getId()) {
                case 10944:
                    return 5_000_000;
                default:
                    return 0;
            }
        } else if (shopId == RANGING_GUILD_STORE) {
            switch (itemDef.getId()) {
//                case ItemID.RANGING_HOOD:
//                    return 500;
//                case ItemID.RANGING_CAPE:
//                    return 1000;
//                case ItemID.RANGING_CAPE_T_:
//                    return 2500;
                case ItemID.RANGING_HOOD:
                    return 0;
                case ItemID.RANGING_CAPE:
                    return 0;
                case ItemID.RANGING_CAPE_T_:
                    return 0;
                case 1478:
                    return 75;
                case 3844:
                    return 50;
                case 12610:
                    return 225;
                case 10499:
                    return 1000;
                case 10498:
                    return 2500;
                case 22109:
                    return 7000;
                case 12788:
                    return 1250;
                case 9185:
                    return 750;
                case 21902:
                    return 3000;
                case 11785:
                    return 15000;
                case 3749:
                    return 100;
                case 2577:
                    return 4500;
                case 19994:
                    return 6500;
                case 12596:
                    return 8000;
                case 21000:
                    return 8500;
                case 22284:
                    return 2500;
                case 11926:
                    return 3750;
                case 12883:
                    return 7500;
                case 19478:
                    return 25000;
                case 5667:
                    return 2;
                case 22812:
                    return 5;
                case 22810:
                    return 10;
                case 78:
                    return 1;
                case 5627:
                    return 2;
                case 11212:
                    return 3;
                case 11229:
                    return 7;
                case 21326:
                    return 5;
                case 21336:
                    return 12;
                case 9305:
                    return 2;
                case 9241:
                    return 1;
                case 9242:
                    return 2;
                case 9243:
                    return 2;
                case 9244:
                    return 4;
                case 9245:
                    return 5;
                case 21905:
                    return 2;
                case 21928:
                    return 5;
                case 21955:
                    return 4;
                case 21957:
                    return 5;
                case 21959:
                    return 6;
                case 21961:
                    return 7;
                case 21963:
                    return 8;
                case 21965:
                    return 9;
                case 21967:
                    return 12;
                case 21969:
                    return 12;
                case 21971:
                    return 15;
                case 21973:
                    return 18;
                case 20849:
                    return 3;
                case 19484:
                    return 4;
                case 19490:
                    return 8;
                case 11230:
                    return 2;
                case 11234:
                    return 5;
            }
        } else if (shopId == HIGH_PRIEST_STORE) {
            switch (itemDef.getId()) {
                case 526:
                case 527:
                    return 25000;
                case 528:
                case 529:
                    return 30_000;
                case 534:
                    return 500000;
                case 536:
                case 537:
                    return 1500000;
                case 3123:
                    return 2500000;
                case 4830:
                    return 3000000;
                case 4832:
                    return 3100000;
                case 4834:
                    return 4000000;
                case 6812:
                    return 4500000;
                case 6729:
                    return 5000000;
                case 11943:
                    return 7000000;
                case 22124:
                    return 22000000;
            }
        } else if (shopId == CASTLEWARS_TICKET_EXCHANGE) {
            switch (itemDef.getId()) {
                case 25169:
                    return 60;
                case 25167:
                    return 35;
                case 25165:
                    return 6;
                case 25163:
                    return 3;
                case 25174:
                    return 600;
                case 25171:
                    return 250;
                case 4055:
                    return 0;
                case 24192:
                    return 125;
                case 24201:
                    return 125;
                case 24195:
                    return 125;
                case 24198:
                    return 250;
                case 24204:
                    return 250;
                case 4071:
                    return 4;
                case 4069:
                    return 8;
                case 4070:
                case 11893:
                case 4072:
                    return 6;
                case 4068:
                    return 5;
                case 4506:
                    return 40;
                case 4504:
                    return 80;
                case 4505:
                case 11894:
                case 4507:
                    return 60;
                case 4503:
                    return 50;
                case 4511:
                    return 400;
                case 4509:
                    return 800;
                case 4510:
                case 11895:
                case 4512:
                    return 600;
                case 4508:
                    return 500;
                case 4513:
                case 4514:
                case 4515:
                case 4516:
                    return 10;
                case 11891:
                case 11892:
                    return 100;
                case 11898:
                    return 20;
                case 11896:
                    return 40;
                case 11897:
                    return 30;
                case 11899:
                    return 40;
                case 11900:
                    return 30;
                case 11901:
                    return 40;
                case 12637:
                case 12638:
                case 12639:
                    return 75;

            }
        } else if (shopId == COMMENDATION_POINTS_EXCHANGE || shopId == COMMENDATION_POINTS_EXCHANGE_2) {
            switch (itemDef.getId()) {
                case 26477:
                    return 250;
                case 26475:
                    return 250;
                case 26473:
                    return 250;
                case 26469:
                    return 350;
                case 26471:
                    return 350;
                case 26467:
                    return 100;
                case 11739:
                    return 125;
                case 11666:
                    return 15;
                case 11665:
                case 11664:
                case 11663:
                    return 25;
                case 8839:
                    return 40;
                case 8840:
                    return 35;
                case 8842:
                    return 8;
                case 8841:
                    return 40;
                case 13072:
                    return 150;
                case 13073:
                    return 125;
                case 10586:
                    return 150;
                default:
                    return 1000;

            }
        } else if (shopId == GADRIN_MINING_MASTER) {
            switch (itemDef.getId()) {
                case ItemID.MINING_HOOD:
                case ItemID.MINING_CAPE:
                    return 1500;
                case ItemID.MINING_CAPE_T_:
                    return 2000;
                case ItemID.DRAGON_PICKAXE:
                    return 1500;
                case ItemID.PROSPECTOR_HELMET:
                    return 625;
                case ItemID.PROSPECTOR_JACKET:
                    return 1200;
                case ItemID.PROSPECTOR_LEGS:
                    return 1100;
                case ItemID.PROSPECTOR_BOOTS:
                    return 350;
                case ItemID.GOLD_ORE:
                case ItemID.GOLD_ORE_2:
                    return 1;
                case ItemID.COAL_2:
                    return 2;
                case ItemID.MINING_GLOVES:
                    return 1200;
                case ItemID.SUPERIOR_MINING_GLOVES:
                    return 2500;
                case ItemID.EXPERT_MINING_GLOVES:
                    return 4500;
                case ItemID.RUNE_PICKAXE:
                    return 500;
                case ItemID._3RD_AGE_PICKAXE:
                    return 5000;
                case ItemID.INFERNAL_PICKAXE:
                    return 3250;
            }
        } else if (shopId == WOODCUTTING_STORE) {
            switch (itemDef.getId()) {
                case 6739:
                    return 1500;
                case 1351:
                case 1353:
                case 1355:
                case 1357:
                    return 0;
                case 20011:
                    return 5000;
                case 13241:
                    return 3250;
                case 1359:
                    return 500;
                case 10941:
                    return 500;
                case 10939:
                case 10940:
                    return 1250;
                case 10933:
                    return 350;
            }
        } else if (shopId == AQUAIS_NEIGE_STORE) {
            switch (itemDef.getId()) {

                case 15749:
                    return 50_000;
                case 15301:
                case 15302:
                case 15303:
                    return 25000;


                case 15304:
                case 15305:
                    return 12500;

                case 26488:
                    return 2500;
                case 26490:
                    return 3500;
                case 26492:
                    return 3500;
                case 26494:
                    return 3500;
                case 26496:
                    return 2500;
                case 26498:
                    return 2500;
                case 26870:
                    return 4500;
                case 26872:
                    return 5500;
                case 26874:
                    return 5500;
                case 26549:
                    return 2000;
                case 26225:
                    return 8000;
                case 26221:
                    return 9000;
                case 26223:
                    return 9000;
                case 26227:
                    return 2500;
                case 26229:
                    return 2500;

                case ItemID.HUNTING_KNIFE:
                    return 20_000;

                case ItemID.APPRENTICE_WAND:
                    return 5500;

                case 15690: // Hydro helmet
                    return 12_500;

                case 15692: // Hydro platebody
                    return 18_500;

                case 15694: // Hydro platelegs
                    return 18_500;

                case ItemID.PHASMATYS_FLAG:
                case ItemID.GUILDED_SMILE_FLAG:
                    return 50_000;

                case ItemID.ICE_ARROWS:
                    return 25;

                case ItemID.HERB_BOX:
                    return 8_500;

                case 8962:
                    return 2500;

                case 8955:
                    return 3500;

                case 8994:
                    return 3500;

                case 8959:
                    return 2500;

                case 8952:
                    return 3500;

                case 8991:
                    return 3500;

                case 8960:
                    return 2500;

                case 8953:
                    return 3500;

                case 8992:
                    return 3500;

                case 8965:
                    return 2500;

                case 8958:
                    return 3500;

                case 8997:
                    return 3500;

                case 8964:
                    return 2500;

                case 8957:
                    return 3500;

                case 8996:
                    return 3500;

                case 8963:
                    return 2500;

                case 8956:
                    return 3500;

                case 8995:
                    return 3500;

                case 8961:
                    return 2500;

                case 8954:
                    return 3500;

                case 8993:
                    return 3500;

            }
        } else if (shopId == PVP_STORE || shopId == OUTLET_BLOOD_STORE) { // PVP_STORE
            switch (itemDef.getId()) {
                case ItemID.TOME_OF_WATER_EMPTY:
                    return 150_000;
                case ItemID.SUPER_COMBAT_POTION_4_2:
                    return 500;
                case ItemID.RANGER_HAT:

                    return 25_000;
                case ItemID.FIGHTER_HAT:
                    return 100_000;
                case ItemID.RUNNER_HAT:
                    return 50_000;
                case ItemID.HEALER_HAT:
                    return 10_000;
                case ItemID.BARROWS_GLOVES:
                    return 25_000;
                case ItemID.DRAGON_BOOTS:
                    return 60_000;
                case ItemID.BLOODBARK_HELM:
                    return 3500;
                case ItemID.BLOODBARK_BODY:
                    return 4000;
                case ItemID.BLOODBARK_LEGS:
                    return 4000;
                case ItemID.BLOODBARK_GAUNTLETS:
                    return 2000;
                case ItemID.BLOODBARK_BOOTS:
                    return 2000;
                case 3105:
                    return 5000;
                case ItemID.NIGHTMARE_STAFF:
                    return 375_000;
                case ItemID.VOLATILE_NIGHTMARE_STAFF:
                    return 1_275_000;
                case ItemID.ELDRITCH_NIGHTMARE_STAFF:
                    return 1_500_000;
                case ItemID.HARMONISED_NIGHTMARE_STAFF:
                    return 2_275_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_1:
                    return 50_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_2:
                    return 75_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_3:
                    return 100_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_4:
                    return 150_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_5:
                    return 200_000;
                case ItemID.BOUNTY_HUNTER_HAT_TIER_6:
                    return 250_000;
                case ItemID.WIZARD_BOOTS:
                    return 35_000;
                case 3840:
                case 3842:
                case 3844:
                    return 15000;
                case 12608:
                    return 30000;
                case ItemID.SARADOMIN_HALO:
                case ItemID.ZAMORAK_HALO:
                case ItemID.GUTHIX_HALO:
                    return 125_000;
                case 24192:
                    return 125_000;
                case 24201:
                    return 125_000;
                case 24195:
                    return 175_000;
                case 24198:
                    return 255_000;
                case 24204:
                    return 255_000;
                case ItemID.MEAT_TENDERISER:
                    return 85_000;
                case ItemID.HOLY_SANDALS:
                    return 25_000;
                case ItemID.MONKS_ROBE_TOP_G_:
                case ItemID.MONKS_ROBE_G_:
                    return 550_000;
                case ItemID.WOODEN_SHIELD_G_:
                    return 50_000;
                case ItemID.CABBAGE_ROUND_SHIELD:
                    return 65_000;
                case ItemID.MUSIC_HOOD:
                case ItemID.MUSIC_CAPE:
                case ItemID.MUSIC_CAPE_T_:
                    return 100_000;
                case ItemID.MUMMYS_HEAD:
                case ItemID.MUMMYS_LEGS:
                case ItemID.MUMMYS_BODY:
                    return 250_000;
                case ItemID.RING_OF_COINS:
                    return 375_000;
                case ItemID.MUMMYS_HANDS:
                case ItemID.MUMMYS_FEET:
                    return 125_000;
                case ItemID.RING_OF_NATURE:
                    return 350_000;
                case ItemID.BRIEFCASE:
                    return 90_000;
                case ItemID.GRIM_REAPER_HOOD:
                    return 30_000;
                case ItemID.FALADOR_SHIELD_1:
                    return 25_000;
                case ItemID.MUSKETEER_HAT:
                    return 75_000;
                case ItemID.COW_MASK:
                case ItemID.COW_TOP:
                case ItemID.COW_TROUSERS:
                    return 200_000;
                case ItemID.COW_GLOVES:
                case ItemID.COW_SHOES:
                    return 95_000;
                case 23285:
                case 23288:
                case 23291:
                case 23294:
                    return 15_000;
                case 15348: // Dragon scimitar (p++)
                    return 45_000;
                case 21802:
                    return 1500;
                case 4151:
                    return 55_000;
                case 7461:
                    return 10_000;
                case 21433: // Wilderness amulet
                case 23351: // Cape of Skulls
                    return 10_000_000;
                case 22616: // Vesta body
                case 22619: // Vesta legs
                    return 350_000;
                case ItemID.IMBUED_SARADOMIN_CAPE:
                case ItemID.IMBUED_GUTHIX_CAPE:
                case ItemID.IMBUED_ZAMORAK_CAPE:
                    return 200_000;
                case 23615: // Vesta long
                case 22613: // Vesta long
                    return 420_000;
                case 22610: // Vesta spear
                    return 400_000;
                case 22622: // Statius hammer
                    return 380_000;
                case 22625: // Statius helm
                    return 150_000;
                case 22628: // Statius body
                    return 250_000;
                case 22631: // Statius legs
                    return 250_000;
                case 22638: // Morrigans coif
                    return 150_000;
                case 22641: // Morrignas body
                    return 250_000;
                case 22644: // Morrigans legs
                    return 250_000;
                case 22647: // Zuriels staff
                    return 350_000;
                case 22650: // Zuriels hood
                case 22653: // Zuriels body
                case 22656: // Zuriels legs
                    return 250_000;
                case 23336: // Third age druidic body
                    return 550_000;
                case 23339: // Third age druidic legs
                    return 550_000;
                case 23345: // Third age druidic cloak
                    return 350_000;
                case 23242: // Third age plateskirt
                    return 1_550_000;
                case 24144: // Staff of balance
                    return 800_000;
                case 24219: // Swift blade
                    return 150_000;
                case 22446:
                    return 2500;
                case 22461:
                case 22449:
                    return 1500;
                case ItemID.SKULL_SCEPTRE:
                    return 95_000;
                case 24229:
                case 24230:
                    return 120000;
                case 20527:
                    return 250_000;
                case 15274:
                    return 5000;
                case 12424:
                    return 1000000;
                case 12426:
                    return 850000;
                case 15275:
                    return 1000;
                case 12437:
                    return 350000;
                case 3144:
                case 3145:
                    return 50;
                case 4160:
                    return 10;
                case 7581:
                case 7583:
                    return 150000;
                case 19675:
                    return 42000;
                case 10159:
                    return 5;
                case 21905:
                    return 250;
                case 22324:
                    return 1_250_000;
                case 21295:
                    return 500000;
                case 6731:
                case 6737:
                case 6733:
                case 6735:
                    return 100_000;
                case 12601:
                    return 50_000;
                case 11061:
                    return 125_000;
                case 861:
                    return 12500;
                case 22284:
                    return 35000;
                case 6914:
                    return 40000;
                case 6889:
                    return 92000;
                case 2581:
                    return 70000;
                case 9144:
                    return 40;
                case 9305:
                    return 75;
                case 892:
                    return 35;
                case 4418:
                    return 250;
                case 2441:
                case 2437:
                case 2445:
                case 3041:
                    return 250;
                case 2443:
                    return 150;
                case 2435:
                    return 350;
                case 6686:
                    return 500;
                case 12596:
                    return 125000;
                case 2577:
                    return 75000;
                case 19994:
                    return 70000;
                case 12853:
                case 19547:
                    return 35000;
                case 12603:
                case 12605:
                    return 30000;
                case 6918:
                case 6924:
                case 6916:
                    return 50000;
                case 6920:
                case 6922:
                    return 20000;
                case 15026:
                case 15027:
                case 15028:
                case 15029:
                case 15030:
                    return 25000;
                case 13121:
                    return 35000;
                case 13122:
                    return 55000;
                case 13123:
                    return 65000;
                case 13124:
                    return 75000;
                case 21326:
                    return 500;
                case 21318:
                case 22636:
                case 22634:
                case 2491:
                    return 1000;
                case 15433:
                    return 180_000;
                case 11791:
                    return 78_000;
                case 3753:
                case 3755:
                    return 6000;
                case 6110:
                case 6111:
                case 6106:
                    return 1000;
                case 6109:
                    return 1500;
                case 6107:
                case 6108:
                    return 2000;
                case 4089:
                    return 3500;
                case 4091:
                case 4093:
                    return 5000;
                case 4095:
                case 4097:
                    return 1500;
                case ItemID.AHRIMS_HOOD:
                    return 18_500;
                case ItemID.AHRIMS_ROBETOP:
                case ItemID.AHRIMS_ROBESKIRT:
                    return 22_500;
                case ItemID.AHRIMS_STAFF:
                    return 25_000;
                case ItemID.KARILS_COIF:
                    return 18_500;
                case ItemID.KARILS_LEATHERTOP:
                case ItemID.KARILS_LEATHERSKIRT:
                    return 22_500;
                case ItemID.KARILS_CROSSBOW:
                    return 25_000;
                case ItemID.DHAROKS_HELM:
                    return 25_000;
                case ItemID.DHAROKS_PLATEBODY:
                case ItemID.DHAROKS_PLATELEGS:
                    return 28_500;
                case ItemID.DHAROKS_GREATAXE:
                    return 30_000;
                case ItemID.GUTHANS_HELM:
                case ItemID.VERACS_HELM:
                    return 22_500;
                case ItemID.VERACS_BRASSARD:
                case ItemID.VERACS_PLATESKIRT:
                case ItemID.GUTHANS_PLATEBODY:
                case ItemID.GUTHANS_CHAINSKIRT:
                    return 26_500;
                case ItemID.VERACS_FLAIL:
                case ItemID.GUTHANS_WARSPEAR:
                    return 28_000;
                case ItemID.TORAGS_HELM:
                    return 18_500;
                case ItemID.TORAGS_PLATEBODY:
                case ItemID.TORAGS_PLATELEGS:
                    return 22_500;
                case ItemID.TORAGS_HAMMERS:
                    return 20_000;
                case 11235:
                    return 75_000;
                case 21009:
                    return 45000;
                case 11838:
                    return 120_000;
                case 11808:
                    return 240_000;
                case 2497:
                    return 2500;
                case 4153:
                    return 35000;
                case 6524:
                    return 10_000;
                case 1249:
                    return 11_500;
                case 5698:
                    return 15_000;
                case 3204:
                    return 65000;
                case 1377:
                    return 68_000;
                case 4587:
                    return 27_500;
                case 21944:
                case 21928:
                    return 1500;
                case 2503:
                case 3749:
                    return 5000;
                case 3751:
                    return 7000;
                case 9185:
                    return 18000;
                case 4675:
                    return 7500;
                case 14162:
                    return 75000;
                case 6585:
                    return 10000;
                case 12788:
                    return 10000;
                case ItemID.SEERCULL:
                case ItemID.AVAS_ACCUMULATOR:
                    return 10000;
                case 10828:
                    return 15000;
                case 4087:
                case 1187:
                    return 15000;
                case 11335:
                    return 20000;
                case 4212:
                case 4224:
                    return 20000;
                case 22109:
                    return 85000;
                case 19550:
                    return 20000;
                case ItemID.TELEPORT_TO_HOUSE:
                    return 500;
                case 19478:
                    return 150000;
                case 19481:
                    return 350_000;
                case 19592:
                    return 150000;
                case 19601:
                    return 125000;
                case 19610:
                    return 120000;
                case ItemID.OVERLOAD_4:
                    return 2500;
                case 6528:
                    return 15000;
                case 11128:
                    return 7500;
                case 12626:
                    return 100;
                case 11952:
                    return 1500;
                case 2453:
                    return 50;
                case 21006:
                    return 450000;
                case 22325:
                    return 500000;
                case 13652:
                    return 666_000;
                case 20997:
                    return 750000;
                case ItemID.ANTI_VENOM_4_2:
                    return 1500;
            }
        } else if (shopId == MEMBERS_STORE) {
            switch (itemDef.getId()) {
                case 20834:
                    return 24000000;
                case 9906:
                    return 3000000;
                case 21902:
                    return 50000000;
                case 15171:
                    return 2000000000;
                case 13655:
                    return 9000000;
                case 22331:
                case 22333:
                case 22335:
                    return 27000000;
                case 6465:
                    return 192000000;
                case 22316:
                    return 147000000;
                case 9013:
                    return 30000000;
                case 21720:
                    return 75000000;
                case 21209:
                    return 30000000;
                case 21354:
                case 21314:
                    return 18000000;
                case 21211:
                    return 30000000;
                case 20243:
                case 12319:
                case 20240:
                    return 13000000;
                default:
                    return 0;
            }
        } else if (shopId == WILDERNESS_ITEMS_STORE) {
            switch (itemDef.getId()) {
                case 4151:
                    return 12000000;
                case 4708:
                case 4710:
                case 4712:
                case 4714:
                case 4716:
                case 4718:
                case 4720:
                case 4722:
                case 4724:
                case 4726:
                case 4728:
                case 4730:
                case 4732:
                case 4734:
                case 4736:
                case 4738:
                case 4747:
                case 4749:
                case 4751:
                case 4753:
                case 4755:
                case 4757:
                case 4759:
                    return 10000000;
                case 4153:
                case 9185:
                case 4675:
                    return 8000000;
                case 11128:
                    return 5000000;
                case 7461:
                    return 3000000;
                case 4587:
                    return 1000000;
                default:
                    return 0;
            }
        } else if (shopId == DISCOUNTED_PREMIUM_STORE || shopId == DISCOUNTED_HOLIDAY_PREMIUM_STORE ||
                shopId == DISCOUNTED_PREMIUM_STORE_IRONMAN || shopId == DISCOUNTED_HOLIDAY_PREMIUM_STORE_IRONMAN) {
            switch (itemDef.getId()) {
                case 20716:
                    return 2500;
                case 22486:
                    return 35000;
                case 24419:
                    return 50000;
                case 24420:
                    return 55000;
                case 24421:
                    return 45000;
                case 24417:
                    return 55000;
                case 20997:
                    return 35000;
                case 12863:
                    return 7500;
                case 22324:
                    return 31500;
                case 22325:
                    return 17500;
                case 13263:
                    return 11500;
                case 13652:
                    return 8400;
                case 21295:
                    return 21000;
                case 22322:
                    return 15000;
                case 12821:
                    return 7500;
                case 12825:
                    return 11500;
                case 12817:
                    return 14500;
                case 11284:
                    return 3400;
                case 13239:
                    return 8000;
                case 13237:
                    return 8000;
                case 13235:
                    return 8000;
                case 13576:
                    return 2200;
                case 21003:
                    return 9200;
                case 11802:
                    return 6500;
                case 15160:
                    return 24500;
                case 22296:
                    return 10_000;
                case 21006:
                    return 12600;
                case 19481:
                    return 13650;
                case 12006:
                    return 2400;
                case 15153:
                    return 17500;
                case 15155:
                    return 17500;
                case 15156:
                    return 17500;
                case 15157:
                    return 11500;
                case 15158:
                    return 11500;
                case 15164:
                    return 11500;
                case 21015:
                    return 7700;
                case 21000:
                    return 3800;
                case 15216:
                    return 9150;
                case 15217:
                    return 12250;
                case 15218:
                    return 8500;
                case 15219:
                    return 7800;
                case 21018:
                    return 3800;
                case 21021:
                    return 6200;
                case 21024:
                    return 5500;
                case 22326:
                    return 10600;
                case 22327:
                    return 11200;
                case 22328:
                    return 11200;
                case 12417:
                    return 8500;
                case 12414:
                    return 12400;
                case 22242:
                    return 19500;
                case 12415:
                    return 6400;
                case 12416:
                    return 4950;
                case 12418:
                    return 8800;
                case 22244:
                    return 19500;
                case 22234:
                    return 3200;
                case 20000:
                    return 4900;
                case 11826:
                    return 3800;
                case 11828:
                    return 5500;
                case 11830:
                    return 4200;
                case 11832:
                    return 4800;
                case 11834:
                    return 4950;
                case 21634:
                    return 7850;
                case 22003:
                    return 5750;
                case 11770:
                    return 3200;
                case 11772:
                    return 3200;
                case 11771:
                    return 3450;
                case 11773:
                    return 3450;
                case 12691:
                    return 7400;
                case 12692:
                    return 7400;
                case 13202:
                    return 7400;
                case 22981:
                    return 11500;
                case 22975:
                    return 11500;
                case 19710:
                    return 3250;
                case 21733:
                    return 8900;
                case 14158:
                    return 10_000;
                case 14159:
                    return 1850;
                case 14160:
                    return 1250;
                case 14161:
                    return 350;
                case 22318:
                    return 29500;
                case 6199:
                    return 500;
                case 11738:
                    return 5000;
                case 7779:
                    return 9000;
                case 7782:
                    return 9000;
                case 7791:
                    return 9000;
                case 7796:
                    return 9000;
                case 7797:
                    return 9000;
                case 7785:
                    return 5500;
                case 15272:
                    return 15500;
                case 15161:
                    return 21500;
                case 11863:
                    return 16500;
                case 11862:
                    return 14350;
                case 1038:
                    return 9750;
                case 1040:
                    return 9750;
                case 1042:
                    return 9750;
                case 1044:
                    return 9750;
                case 1046:
                    return 9750;
                case 1048:
                    return 14500;
                case 11847:
                    return 14500;
                case 1053:
                    return 12500;
                case 1055:
                    return 12500;
                case 1057:
                    return 12500;
                case 13343:
                    return 16500;
                case 13344:
                    return 16500;
                case 1050:
                    return 7750;
                case 21859:
                    return 9900;
                case 12399:
                    return 5600;
                case 19707:
                    return 8500;
                case 6465:
                    return 3650;
                case 12457:
                    return 2800;
                case 12458:
                    return 2800;
                case 12459:
                    return 2800;
                case 12419:
                    return 2650;
                case 12420:
                    return 2650;
                case 12421:
                    return 2650;
                case 21847:
                    return 1400;
                case 21849:
                    return 1400;
                case 21851:
                    return 1400;
                case 21853:
                    return 1400;
                case 21855:
                    return 1400;
                case 21857:
                    return 1400;
                case 1961:
                    return 350;
                case 1907:
                    return 65;
            }
        } else if (shopId == PREMIUM_STORE || shopId == HOLIDAY_PREMIUM_STORE || shopId == PVP_PREMIUM_STORE
                || shopId == HOLIDAY_PREMIUM_STORE_IRONMAN || shopId == PVP_PREMIUM_STORE_IRONMAN || shopId == PREMIUM_CLUE_STORE
                || shopId == HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES || shopId == HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS || shopId == HOLIDAY_PREMIUM_STORE_PETS_MISC || shopId == HOLIDAY_PREMIUM_STORE_RESOURCES) {
            switch (itemDef.getId()) {

                case 16073:
                case 16074:
                case 16075:
                case 16076:
                case 16077:
                case 16078:
                    return 2500; //1000
                case 16079:
                case 16080:
                case 16081:
                case 16082:
                case 16083:
                case 16084:
                case 16085:
                case 16086:
                case 16087:
                case 16088:
                case 16089:
                case 16090:
                    return 3500;//1500
                case 16091:
                case 16092:
                    return 5000;
                case 16093:
                    return 7500;
                case 16094:
                    return 10000;

                case 15828:
                    return 12_500;
                case 15829:
                    return 28_500;
                case 15830:
                    return 115_000;
                case 13190:
                    return 55_000;
                case 15831:
                    return 275_000;

                case 15827:
                    return 19_500;


                case 10556:
                case 10557:
                case 10558:
                case 10559:
                    return 11_000;

                case ItemID.TORVA_FULL_HELM:
                    return 75_000;
                case ItemID.TORVA_PLATEBODY:
                case ItemID.TORVA_PLATELEGS:
                    return 85_000;
                case 15720:
                    return 45_000;
                case 15405:
                    return 5_000;

                case 15399:
                    return 5_000;

                case 15400:
                    return 5_000;

                case 15401:
                    return 5_000;

                case 15402:
                    return 5_000;

                case 15403:
                    return 5_000;

                case 15404:
                    return 5_000;

                case 15308:
                    return 5_000;

                case 15306:
                    return 5_000;

                case 15307:
                    return 5_000;

                case 15309:
                    return 5_000;

                case 15414:
                    return 5_000;

                case 15416:
                    return 5_000;

                case 15406:
                    return 5_000;

                case 15408:
                    return 5_000;

                case 15410:
                    return 5_000;

                case 15426:
                    return 5_000;

                case 15428:
                    return 5_000;

                case 15418:
                    return 5_000;

                case 15420:
                    return 5_000;

                case 15422:
                    return 5_000;

                case 15424:
                    return 5_000;

                case 15345:
                    return 5_000;

                case 15349:
                    return 5_000;

                case 15350:
                    return 5_000;

                case 15372:
                    return 75_000;

                case 15373:
                    return 75_000;

                case 15374:
                    return 75_000;

                case 24863:
                case 24865:
                    return 50_000;

                case 15731:
                    return 5000;

                case 15732:
                    return 10_000;

                case 15733:
                    return 25_000;

                case 15734:
                    return 50_000;

                case 15735:
                    return 100_000;

                case 15722:
                    return 40_000;

                case 15370:
                    return 32_000;
                case 15351:
                    return 25_000;
                case ItemID.GOLDEN_PROSPECTOR_HELMET:
                    return 5000;
                case ItemID.GOLDEN_PROSPECTOR_JACKET:
                    return 7500;
                case ItemID.GOLDEN_PROSPECTOR_LEGS:
                    return 7500;
                case ItemID.GOLDEN_PROSPECTOR_BOOTS:
                    return 2500;
                case ItemID.HALLOWED_HAMMER:
                    return 2500;
                case ItemID.BOOK_OF_THE_DEAD:
                    return 5000;
                case ItemID.GOLD_COFFIN:
                    return 25_000;
                case ItemID.AMYS_SAW:
                    return 25_000;
                case ItemID.GREGGS_EASTDOOR:
                    return 75_000;
                case ItemID.GIANT_BOULDER:
                    return 45_000;
                case ItemID.SEVERED_LEG_24792:
                    return 8000;
                case ItemID.TWISTED_CANE:
                    return 4000;
                case ItemID.FAIRY_MUSHROOM:
                    return 3500;
                case ItemID.CRYSTAL_GRAIL:
                    return 7_500;
                case ItemID.IMCANDO_HAMMER:
                    return 2500;
                case ItemID.SKELETON_LANTERN:
                    return 9000;
                case ItemID.HALLOWED_FOCUS:
                    return 6500;
                case ItemID.VS_SHIELD_24266:
                    return 18500;
                case ItemID.ZARYTE_CROSSBOW:
                    return 35_000;
                case ItemID.ANCIENT_GODSWORD:
                    return 35_000;
                case 26708:
                    return 38_000;
                case 26712:
                    return 24_000;
                case 26710:
                    return 7000;
                case ItemID.ZARYTE_VAMBRACES:
                    return 12_000;
                case ItemID.GOLDEN_ARMADYL_SPECIAL_ATTACK:
                    return 15_000;
                case ItemID.GOLDEN_BANDOS_SPECIAL_ATTACK:
                case ItemID.GOLDEN_SARADOMIN_SPECIAL_ATTACK:
                case ItemID.GOLDEN_ZAMORAK_SPECIAL_ATTACK:
                    return 10_000;

                case ItemID.SAUCEPAN:
                    return 4_000;
                case ItemID.HEADLESS_HEAD:
                    return 5_000;
                case 23252:
                    return 3500;
                case 25336:
                    return 5000;
                case 26424:
                    return 15_000;
                case ItemID.BANANA_CAPE:
                    return 7500;
                case ItemID.BANANA_HAT:
                    return 7500;
                case 24867:
                    return 35000;
                case 25500:
                    return 25000;
                case 24866:
                    return 35000;
                case 24864:
                    return 50000;
                case ItemID.TOME_OF_WATER_EMPTY:
                    return 2_500;
                case ItemID.NIGHTMARE_STAFF:
                    return 7500;
                case ItemID.VOLATILE_NIGHTMARE_STAFF:
                    return 42_500;
                case ItemID.ELDRITCH_NIGHTMARE_STAFF:
                    return 33_000;
                case ItemID.HARMONISED_NIGHTMARE_STAFF:
                    return 75_000;
                case 6199:
                    return 1500;
                case 1038:
                case 1040:
                case 1042:
                case 1044:
                case 1046:
                case 1048:
                    return 24_000;
                case 11863:
                    return 30_000;
                case 13343:
                    return 24000;
                case 1053:
                case 1055:
                case 1057:
                    return 29000;
                case 11847:
                    return 32000;
                case 15162:
                    return 26000;
                case 15368:
                case 15369:
                    return 22000;
                case 20546:
                    return 1000;
                case 20545:
                    return 2500;
                case 20544:
                    return 4000;
                case 20543:
                    return 5500;
                case 11705:
                    return 25000;
                case 12727:
                    return 15000;
                case 13652:
                    return 19000;
                case 23300:
                    return 25000;
                case 22689:
                    return 7500;
                case 22695:
                    return 5000;
                case 22698:
                    return 5000;
                case 22701:
                    return 2500;
                case 15175:
                    return 5000;
                case 15173:
                    return 5000;
                case 15174:
                    return 5000;
                case 15172:
                    return 5000;
                case 20029:
                    return 5000;
                case 20023:
                    return 5000;
                case 20020:
                    return 5000;
                case 20026:
                    return 5000;
                case 23255:
                    return 3500;
                case 24325:
                    return 5000;
                case 12251:
                    return 8000;
                case 23273:
                    return 5000;
                case 12371:
                    return 6500;
                case 23360:
                    return 1500;
                case 23363:
                    return 4500;

                case 22396:
                    return 15000;
                case 23859:
                    return 12500;
                case 19556:
                    return 25000;
                case 23357:
                    return 7500;

                case 12221:
                    return 1000;

                case 12215:
                    return 3000;

                case 12217:
                    return 2000;

                case 12223:
                    return 2500;

                case 12219:
                    return 2000;

                case 12231:
                    return 1000;

                case 12225:
                    return 3000;

                case 12227:
                    return 2000;

                case 12233:
                    return 2500;

                case 12229:
                    return 2000;

                case 20193:
                    return 1000;

                case 20184:
                    return 3000;

                case 20187:
                    return 2000;

                case 20196:
                    return 2500;

                case 20190:
                    return 2000;

                case 2587:
                    return 1000;

                case 2583:
                    return 3000;

                case 2585:
                    return 2000;

                case 2589:
                    return 2500;

                case 12293:
                    return 2000;

                case 12289:
                    return 3000;

                case 12287:
                    return 3000;

                case 12291:
                    return 2500;

                case 12295:
                    return 2000;

                case 3472:
                    return 2000;

                case 2605:
                    return 1000;

                case 2599:
                    return 3000;

                case 2601:
                    return 2000;

                case 2603:
                    return 2500;

                case 3474:
                    return 2000;

                case 2627:
                    return 1000;

                case 2623:
                    return 3000;

                case 2625:
                    return 2000;

                case 2629:
                    return 2500;

                case 3477:
                    return 2000;

                case 7364:
                    return 2000;

                case 7368:
                    return 1500;

                case 7372:
                    return 2500;

                case 7380:
                    return 1500;

                case 7376:
                    return 2500;

                case 7384:
                    return 1500;

                case 12331:
                    return 2500;

                case 12333:
                    return 1500;

                case 12385:
                    return 3500;

                case 12387:
                    return 2500;

                case 7396:
                    return 1500;

                case 7392:
                    return 1000;

                case 7388:
                    return 1000;

                case 12455:
                    return 1500;

                case 12451:
                    return 1500;

                case 12447:
                    return 1500;

                case 10362:
                    return 1500;

                case 10364:
                    return 1000;

                case 10366:
                    return 1000;

                case 12211:
                    return 1000;

                case 12205:
                    return 3000;

                case 12207:
                    return 2000;

                case 12213:
                    return 2500;

                case 12209:
                    return 2000;

                case 12241:
                    return 1000;

                case 12235:
                    return 3000;

                case 12237:
                    return 2000;

                case 12243:
                    return 2500;

                case 12239:
                    return 2000;

                case 20178:
                    return 1000;

                case 20169:
                    return 3000;

                case 20172:
                    return 2000;

                case 20181:
                    return 2500;

                case 20175:
                    return 2000;

                case 2595:
                    return 1000;

                case 2591:
                    return 3000;

                case 2593:
                    return 2000;

                case 2597:
                    return 2500;

                case 3473:
                    return 2000;

                case 12283:
                    return 1000;

                case 12277:
                    return 3000;

                case 12279:
                    return 2000;

                case 12281:
                    return 2500;

                case 12285:
                    return 2000;

                case 2613:
                    return 1000;

                case 2607:
                    return 3000;

                case 2609:
                    return 2000;
                case 2611:
                    return 2500;
                case 3475:
                    return 2000;
                case 2619:
                    return 1000;
                case 2615:
                    return 3000;
                case 2617:
                    return 2000;
                case 2621:
                    return 2500;
                case 3476:
                    return 2000;
                case 23381:
                    return 1500;
                case 23384:
                    return 1500;
                case 23413:
                    return 5000;
                case 7362:
                    return 1500;
                case 7366:
                    return 1000;
                case 7370:
                    return 1000;
                case 7378:
                    return 2000;
                case 7374:
                    return 1000;
                case 7382:
                    return 2000;
                case 12327:
                    return 1000;
                case 12329:
                    return 2000;
                case 12381:
                    return 2500;
                case 12383:
                    return 3500;
                case 12453:
                    return 2500;
                case 12449:
                    return 2200;
                case 12445:
                    return 2000;
                case 7394:
                    return 1500;
                case 7390:
                    return 2200;
                case 7386:
                    return 2000;
                case 2657:
                    return 2000;
                case 2653:
                    return 1000;
                case 2655:
                    return 3000;
                case 2659:
                    return 2000;
                case 3478:
                    return 2500;
                case 2665:
                    return 2000;
                case 2661:
                    return 1000;
                case 2663:
                    return 3000;
                case 2667:
                    return 2000;
                case 3479:
                    return 2500;
                case 2673:
                    return 2000;
                case 2669:
                    return 1000;
                case 2675:
                    return 3000;
                case 3480:
                    return 2000;
                case 20716:
                    return 2500;
                case 22486:
                    return 35000;
                case 24419:
                    return 50000;
                case 24420:
                    return 55000;
                case 24421:
                    return 45000;
                case 24417:
                    return 55000;
                case 12863:
                    return 7500;
                case 15200:
                    return 2500;
                case 15205:
                    return 4000;
                case 15206:
                    return 5500;
                case 15202:
                    return 7500;
                case 15201:
                    return 12500;
                case 15203:
                    return 19_000;
                case 15204:
                    return 29_000;
                case 15724:
                    return 50_000;
                case 13173:
                    return 100_000;
                case 13175:
                    return 75_000;
                case 15265:
                    return 29000;
                case 15266:
                    return 45000;
                case 15210:
                    return 24000;
                case 15211:
                    return 24000;
                case 15212:
                    return 24000;
                case 21049:
                    return 17500;
                case 15213:
                    return 3500;
                case 15214:
                    return 5000;
                case 15208:
                    return 29000;
                case 15209:
                    return 49000;
                case 15726:
                    return 84_000;
                case 15216:
                    return 14000;
                case 15217:
                    return 14000;
                case 15218:
                    return 12000;
                case 15219:
                    return 12000;
                case 15264:
                    return 7500;
                case 15263:
                    return 35000;
                case 3486:
                case 3488:
                case 3484:
                case 3481:
                case 3483:
                    return 2500;
                case 15196:
                case 15193:
                case 15194:
                    return 75000;
                case 6914:
                    return 2500;
                case 6918:
                case 6916:
                case 6924:
                    return 1500;
                case 22325:
                    return 25000;
                case 13263:
                    return 5000;
                case 22324:
                    return 25000;
                case 22322:
                    return 25000;
                case 11283:
                case 11284:
                    return 4500;
                case 13576:
                    return 3500;
                case 15160:
                    return 17000;
                case 12006:
                    return 3500;
                case 15153:
                case 15155:
                case 15156:
                    return 18000;
                case 15157:
                case 15158:
                case 15164:
                    return 20000;
                case 21000:
                    return 4500;
                case 22981:
                    return 15000;
                case 22975:
                    return 25000;
                case 15177:
                    return 5000;
                case 21733:
                    return 12000;
                case 22318:
                    return 50000;
                case 15161:
                    return 20000;
                case 22234:
                    return 4500;
                case 19481:
                    return 12000;
                case 22242:
                case 22244:
                    return 25000;
                case 11738:
                    return 5000;
                case 21859:
                    return 15000;
                case 12817:
                    return 40000;
                case 12821:
                    return 30000;
                case 12825:
                    return 35000;
                case 19710:
                    return 5000;
                case 14158:
                    return 5000;
                case 14159:
                case 14160:
                    return 3500;
                case 14161:
                    return 2500;
                case 13202:
                    return 10000;
                case 11770:
                    return 5000;
                case 11771:
                    return 7500;
                case 11772:
                    return 5000;
                case 11773:
                    return 7500;
                case 12691:
                    return 10000;
                case 12692:
                    return 10000;
                case 21012:
                    return 10000;
                case 12419:
                case 12420:
                case 12421:
                    return 3500;
                case 12457:
                case 12458:
                case 12459:
                    return 4500;
                case 1961:
                    return 500;
                case 21015:
                    return 12000;
                case 21003:
                    return 10000;
                case 11838:
                    return 3000;
                case 22550:
                    return 15000;
                case 22323:
                    return 15000;
                case 22555:
                    return 12000;
                case 22545:
                    return 12000;
                case 22625:
                    return 3000;
                case 22628:
                    return 4000;
                case 22631:
                    return 4000;
                case 22622:
                    return 3500;
                case 22613:
                    return 3500;
                case 22610:
                    return 4000;
                case 22616:
                    return 7000;
                case 22619:
                    return 6000;
                case 22638:
                    return 4000;
                case 22641:
                    return 6000;
                case 22644:
                    return 5000;
                case 22650:
                    return 4000;
                case 22003:
                case 22002:
                    return 7500;
                case 22653:
                    return 6000;
                case 22656:
                    return 5000;
                case 22647:
                    return 3500;
                case 22326:
                case 22327:
                case 22328:
                    return 16000;
                case 22296:
                    return 16_000;
                case 20997:
                    return 30000;
                case 990:
                case 992:
                    return 750;
                case ItemID.COLOSSAL_POUCH:
                    return 5000;
                case 15152:
                    return 9200;
                case 11785:
                    return 15000;
                // case 11802:
                // return 30000;
                case 11804:
                    return 5000;
                case 11806:
                    return 5000;
                case 11808:
                    return 5000;
                case 11832:
                    return 4500;
                case 11834:
                    return 4500;
                case 11826:
                    return 2000;
                case 11828:
                    return 4000;
                case 11830:
                    return 4000;
                case 21633:
                case 21634:
                    return 10000;
                case 6570:
                    return 5000;
                case 19484:
                    return 25;
                case 12424:
                    return 5000;
                case 11802:
                    return 10000;
                case 12426:
                    return 5000;
                case 12422:
                    return 5000;
                case 20014:
                    return 2500;
                case 10342:
                    return 5000;
                case 10334:
                    return 5000;
                case 10350:
                    return 6000;
                case 21018:
                    return 14000;
                case 21021:
                    return 18000;
                case 21024:
                    return 18000;
                case 13239:
                    return 10000;
                case 13237:
                    return 10000;
                case 13235:
                    return 10000;
                case 20128:
                    return 5000;
                case 20131:
                case 20137:
                    return 6500;
                case 20134:
                case 20140:
                    return 1000;
                case 10336:
                    return 1000;
                case 20011:
                    return 2500;
                case 10338:
                    return 5000;
                case 10348:
                    return 6000;
                case 19707:
                    return 13000;
                case 19550:
                    return 1500;
                case 19544:
                    return 2500;
                case 10352:
                    return 6000;
                case 10340:
                    return 5000;
                case 10330:
                    return 5000;
                case 10332:
                    return 5000;
                case 10346:
                    return 6000;
                case 19553:
                    return 3500;
                case 10344:
                    return 5000;
                case 12437:
                    return 5000;
                case 12399:
                    return 7500;
                case 6465:
                    return 5000;
                case 21847:
                case 21849:
                case 21851:
                case 21853:
                case 21855:
                case 21857:
                    return 2500;
                case 21006:
                    return 24000;
                case 7779:
                case 7782:
                case 7791:
                case 7794:
                case 7796:
                case 7797:
                    return 10_000;
                case 7785:
                    return 5000;
                case 15272:
                    return 12500;
            }
        } else if (shopId == LIMITED_SHOP) { // Limited items shop
            switch (itemDef.getId()) {
                case 15918:
                    return 55_000;
                case 15824:
                    return 8500;
                case 15825:
                    return 25_000;
                case 15750:
                    return 100_000;
                case 15749:
                    return 75_000;
                case 21433:
                case 23351:
                    return 250_000;
                case 15901:
                    return 250_000;
                case 15751:
                    return 19_000;
                case 15848:
                case 15846:
                case 15850:
                    return 35_000;
                case 24780:
                    return 10_000;
                case 26718:
                case 26719:
                    return 15_000;
                case 26720:
                    return 5000;
                case 26714:
                case 26715:
                case 26716:
                    return 10_000;
                case 15370:
                    return 15_000;
                case 1053:
                case 1055:
                case 1057:
                    return 15_000;
                case 11847:
                    return 25_000;
                case 15920:
                case 15921:
                case 15922:
                    return 5_000;
                case 15923:
                case 15924:
                    return 2_500;
                case 15203:
                    return 9_000;
                case 15204:
                    return 19_000;
                case 15724:
                    return 35_000;
                case 15871:
                    return 250_000;
                case 15873:
                    return 250_000;
                case 15875:
                    return 250_000;
                case ItemID.HOLY_SANGUINESTI_STAFF_UNCHARGED:
                    return 75_000;
                case 10556:
                case 10557:
                case 10558:
                case 10559:
                    return 50_000;
                case ItemID.SANGUINE_SCYTHE_OF_VITUR_UNCHARGED:
                    return 150_000;
                case ItemID.HOLY_SCYTHE_OF_VITUR_UNCHARGED:
                    return 150_000;
                case ItemID.HOLY_GHRAZI_RAPIER:
                    return 150_000;
                case 24863:
                    return 50_000;
                case 24865:
                    return 50_000;
                case ItemID.TORVA_FULL_HELM:
                    return 75_000;
                case ItemID.TORVA_PLATEBODY:
                case ItemID.TORVA_PLATELEGS:
                    return 125_000;
                case 15720:
                    return 150_000;
                case 15193:
                case 15194:
                case 15196:
                case 15300:
                    return 250_000;
                case 15301:
                    return 80_000;

                case 15302:
                    return 95_000;

                case 15303:
                    return 85_000;

                case 15304:
                    return 20_000;

                case 15305:
                    return 20_000;

                case 15372:
                    return 150_000;

                case 15373:
                    return 150_000;

                case 15374:
                    return 150_000;

                case 15405:
                    return 25_000;

                case 15399:
                    return 25_000;

                case 15400:
                    return 25_000;

                case 15401:
                    return 25_000;

                case 15402:
                    return 25_000;

                case 15403:
                    return 25_000;

                case 15404:
                    return 25_000;

                case 15308:
                    return 25_000;

                case 15306:
                    return 25_000;

                case 15307:
                    return 25_000;

                case 15309:
                    return 25_000;

                case 15414:
                    return 25_000;

                case 15416:
                    return 25_000;

                case 15406:
                    return 25_000;

                case 15408:
                    return 25_000;

                case 15410:
                    return 25_000;

                case 15426:
                    return 25_000;

                case 15428:
                    return 25_000;

                case 15418:
                    return 25_000;

                case 15420:
                    return 25_000;

                case 15422:
                    return 25_000;

                case 15424:
                    return 25_000;

                case 15345:
                    return 25_000;

                case 15349:
                    return 25_000;

                case 15350:
                    return 25_000;

                case 15351:
                    return 25_000;
            }
        } else if (shopId == VOTING_STORE || shopId == VOTING_STORE_IRONMAN) {
            switch (itemDef.getId()) {

                case 15245:
                case 15246:
                case 15247:
                    return 150;

                case 15248:
                case 15249:
                    return 75;

                case 962:
                    return 200;

                case 25606:
                    return 75;

                case ItemID.HARD_HAT:
                    return 25;


                case 26486:
                    return 25;


                case 26822:
                    return 15;


                case 25557:
                    return 35;


                case 25106:
                    return 10;


                case 24942:
                    return 15;


                case 24727:
                    return 75;


                case 26427:
                    return 15;


                case 26430:
                    return 25;


                case 26433:
                    return 25;


                case 26436:
                    return 5;


                case 26517:
                    return 30;


                case 26850:
                    return 8;


                case 26852:
                    return 10;


                case 26854:
                    return 10;


                case 26856:
                    return 10;


                case 24806:
                    return 3;


                case 24808:
                    return 25;


                case 25979:
                    return 25;
                case ItemID.TORN_CURTAINS: // 5k Blood money 8322
                    return 5;
                case 8465:
                    return 1;
                case 20113:
                case 20116:
                case 20119:
                case 20122:
                case 20125:
                    return 35;
                case 20251:
                case 20254:
                case 20263:
                    return 50;
                case 9013:
                    return 10;
                case 23297:
                case 23389:
                    return 15;
                case 12518:
                    return 35;
                case 15251:
                case 15252:
                case 15253:
                    return 55;
                case 20433:
                    return 10;
                case 12363:
                    return 25;
                case 12365:
                    return 35;
                case 12367:
                    return 45;
                case 12369:
                    return 55;
                case 23270:
                    return 75;
                case 12520:
                    return 35;
                case 12522:
                    return 45;
                case 12524:
                    return 75;
                case 20439:
                case 20436:
                case 20442:
                    return 20;
                case 23108:
                    return 100;
                case 23303:
                case 23306:
                    return 15;
                case 15198:
                    return 5;
                case 15274:
                    return 1;
                case 6199:
                    return 5;
                case 11665:
                    return 10;
                case 11664:
                    return 12;
                case 11663:
                    return 10;
                case 8840:
                    return 15;
                case 8839:
                    return 10;
                case 6585:
                    return 5;
                case 8842:
                    return 3;
                case 11738:
                    return 25;
                case 10586:
                    return 10;
                case 15199:
                    return 15;
                case 11771:
                    return 30;
                case 11773:
                    return 30;
                case 14160:
                    return 28;
                case 14161:
                    return 6;
                case 1040:
                    return 150;
                case 15366:
                    return 150;
                case 1050:
                    return 100;
                case 7780:
                case 7783:
                case 7789:
                case 7798:
                    return 12;
                case 15273:
                    return 25;
                case 15167:
                case 15168:
                case 15169:
                case 15170:
                    return 150;
                case 15437:
                    return 250;
                case 15381:
                case 15382:
                case 15383:
                case 15384:
                case 15385:
                    return 25;
                case 2581:
                    return 25;
                case 12596:
                    return 50;
                case 23249: // Ranger thighs
                    return 50;
                case 2577:
                    return 25;
                case 19994:
                    return 25;
                case 1419:
                    return 40;
                case 15031:
                    return 1;
                case 1907:
                    return 2;
                case 2402:
                    return 10;
                case 9472:
                    return 35;
                case 9946:
                    return 45;
                case 20355:
                    return 15;
                case 21214:
                    return 30;
                case 15154:
                    return 50;
                case 19970:
                    return 25;
                case 20405:
                case 20408:
                    return 5;
                case 12538:
                case 20002:
                case 12536:
                case 12534:
                case 12532:
                case 20143:
                    return 50;
                case 4151:
                    return 25;
                case 12887:
                    return 15;
                case 12888:
                    return 15;
                case 12889:
                    return 15;
                case 12890:
                    return 5;
                case 12891:
                    return 5;
                case 12892:
                    return 20;
                case 12893:
                    return 20;
                case 12894:
                    return 20;
                case 12895:
                    return 5;
                case 12896:
                    return 5;
                case 13104:
                    return 25;
                case 13679:
                    return 15;
                case 1037:
                    return 75;
                case 13663:
                    return 35;
                case 13664:
                    return 35;
                case 13665:
                    return 5;
                case 1961:
                    return 5;
                case 12357:
                    return 40;
                case 4083:
                case 4084:
                    return 50;
                case 4566:
                    return 45;
                case 4567:
                    return 10;
                case 4565:
                    return 75;
                case 5608:
                    return 150;
                case 5609:
                    return 150;
                case 5607:
                    return 150;
                case 9470:
                    return 35;
                case 9471:
                    return 35;
                case 6818:
                    return 50;
                case 12397:
                    return 25;
                case 12393:
                    return 25;
                case 12395:
                    return 25;
                case 12430:
                    return 10;
                case 8652:
                    return 50;
                case 8963:
                    return 5;
                case 8956:
                    return 5;
                case 8995:
                    return 5;
                case 2643:
                    return 10;
                case 981:
                    return 35;
                case 11277:
                case 11280:
                    return 15;
                case 7668:
                    return 45;
                case 7927:
                    return 25;
                case 20050:
                    return 35;
                case 19727:
                    return 25;
                case 12526:
                    return 500;
                case 20062:
                    return 30;
                case 20065:
                    return 30;
                case 20071:
                    return 30;
                case 20068:
                    return 30;
                case 20074:
                    return 30;
                case 20077:
                    return 30;
                case 12757:
                    return 30;
                case 12759:
                    return 30;
                case 12761:
                    return 30;
                case 12763:
                    return 30;
                case 12769:
                    return 30;
                case 12771:
                    return 30;
                case 12432:
                    return 15;
                case 7918:
                    return 50;
                case 12514:
                    return 30;
                case 12516:
                    return 10;
                case 13116:
                    return 35;
                case 9920:
                    return 5;
                case 10507:
                    return 5;
                case 9005:
                    return 15;
                case 9006:
                    return 15;
                case 12375:
                    return 10;
                case 12377:
                    return 15;
                case 12379:
                    return 25;
                case 10394:
                    return 15;
                case 10392:
                    return 15;
                case 10396:
                    return 15;
                case 10398:
                    return 15;
                case 7671:
                    return 75;
                case 7673:
                    return 75;
                case 7675:
                    return 5;
                case 7676:
                    return 5;
                case 6665:
                    return 5;
                case 6666:
                    return 5;
                case 6549:
                    return 25;
                case 1052:
                    return 10;
            }
        } else if (shopId == TZHAAR_STORE) {
            switch (itemDef.getId()) {
                case 6575:
                    return 5500;
                case 6571:
                    return 5000;
                case 6524:
                    return 2500;
                case 6568:
                    return 2500;
                case 6526:
                    return 2500;
                case 6528:
                    return 2500;
                case 6527:
                    return 2500;
                case 6523:
                    return 2500;
                case 6525:
                    return 2500;
                case 6522:
                    return 15;
            }
        } else if (shopId == AGILITY_TICKET_EXCHANGE) {
            switch (itemDef.getId()) {
                case 4502:
                    return 250;
                case 22353:
                    return 425;
                case 747:
                    return 150;
                case 8322:
                    return 150;
                case 12361:
                case 11919:
                case 12249:
                    return 250;
                case ItemID.PIRATE_HAT_AND_PATCH:
                    return 1_750;
                case ItemID.DOUBLE_EYE_PATCH:
                    return 2_000;
                case ItemID.BIG_PIRATE_HAT:
                    return 1_500;
                case ItemID.PIRATES_HOOK:
                case ItemID.PIRATES_HAT:
                case ItemID.PIRATE_HAT:
                    return 1_000;
                case ItemID.PIRATE_BOOTS:
                    return 500;
                case ItemID.LEFT_EYE_PATCH:
                    return 200;
                case ItemID.HAT_EYEPATCH:
                    return 500;
                case ItemID.BANDANA_EYEPATCH:
                case ItemID.BANDANA_EYEPATCH_2:
                case ItemID.BANDANA_EYEPATCH_3:
                case ItemID.BANDANA_EYEPATCH_4:
                    return 450;
                case ItemID.PIRATE_BANDANA:
                case ItemID.PIRATE_BANDANA_3:
                case ItemID.PIRATE_BANDANA_5:
                case ItemID.PIRATE_BANDANA_7:
                case ItemID.PIRATE_BANDANA_9:
                case ItemID.STRIPY_PIRATE_SHIRT:
                case ItemID.STRIPY_PIRATE_SHIRT_3:
                case ItemID.STRIPY_PIRATE_SHIRT_5:
                case ItemID.STRIPY_PIRATE_SHIRT_7:
                case ItemID.PIRATE_LEGGINGS:
                case ItemID.PIRATE_LEGGINGS_3:
                case ItemID.PIRATE_LEGGINGS_5:
                case ItemID.PIRATE_LEGGINGS_7:
                    return 50;
                case 1052:
                    return 35;
                case 8960:
                    return 25;
                case 8962:
                    return 25;
                case 8961:
                    return 25;
                case 8964:
                    return 25;
                case 10394:
                    return 25;
                case 2984:
                    return 25;
                case 2985:
                    return 25;
                case 2986:
                    return 25;
                case 2987:
                    return 25;
                case 2988:
                    return 25;
                case 2989:
                    return 25;
                case 2978:
                    return 20;
                case 2979:
                    return 20;
                case 2980:
                    return 20;
                case 2981:
                    return 20;
                case 2982:
                    return 20;
                case 2983:
                    return 20;
                case 6665:
                    return 15;
                case 6666:
                    return 15;
                case 8738:
                case 8744:
                case 8724:
                case 8730:
                case 8722:
                case 8716:
                case 8714:
                    return 100;
                case 3008:
                    return 2;
                case 3016:
                    return 5;
            }
        } else if (shopId == SLAYER_EQUIPMENTS_STORE) {
            switch (itemDef.getId()) {
                case 4155:
                    return 0;
                case 4156:
                    return 10000000;
                case 4161:
                    return 15;
                case 4162:
                    return 856;
                case 4164:
                case 11874:
                case 11876:
                    return 55;
                case 4166:
                    return 85;
                case 4168:
                    return 303;
                case 4170:
                    return 5000000;
                case 4551:
                    return 3500000;
                case 6696:
                    return 134;
                case 6708:
                    return 5018;
                case 7051:
                    return 1274;
                case 7159:
                    return 250000;
                case 7421:
                    return 1288;
                case 7432:
                    return 1233;
                case 8923:
                    return 578;
                case 10952:
                    return 15721;
                case 4158:
                    return 25000000;
                case 11875:
                    return 60;
                case 6660:
                    return 40;
                case 4150:
                case 4160:
                    return 60;
                case 23037:
                    return 50000;

                case 11885:
                case 11887:
                    return 5500;

                case 21338:
                    return 204;

                case 13560: // Shayzien armour (5)
                case 13562:
                case 13561:
                case 13558:
                case 13559:
                    return 250_000;


                default:
                    return 1000;
            }
        } else if (shopId == BLOOD_SKILLING_STORE) {
            switch (itemDef.getId()) {
                case ItemID.SARADOMIN_HALO:
                case ItemID.ZAMORAK_HALO:
                case ItemID.GUTHIX_HALO:
                    return 175_000;
                case ItemID.MONKS_ROBE_TOP_G_:
                case ItemID.MONKS_ROBE_G_:
                    return 550_000;
                case ItemID.WOODEN_SHIELD_G_:
                    return 50_000;
                case ItemID.CABBAGE_ROUND_SHIELD:
                    return 65_000;
                case ItemID.MUSIC_HOOD:
                case ItemID.MUSIC_CAPE:
                case ItemID.MUSIC_CAPE_T_:
                    return 100_000;
                case ItemID.MUMMYS_HEAD:
                case ItemID.MUMMYS_LEGS:
                case ItemID.MUMMYS_BODY:
                    return 250_000;
                case ItemID.RING_OF_COINS:
                    return 375_000;
                case ItemID.MUMMYS_HANDS:
                case ItemID.MUMMYS_FEET:
                    return 125_000;
                case ItemID.RING_OF_NATURE:
                    return 350_000;
                case ItemID.BRIEFCASE:
                    return 90_000;
                case ItemID.GRIM_REAPER_HOOD:
                    return 30_000;
                case ItemID.FALADOR_SHIELD_1:
                    return 25_000;
                case ItemID.MUSKETEER_HAT:
                    return 75_000;
                case ItemID.COW_MASK:
                case ItemID.COW_TOP:
                case ItemID.COW_TROUSERS:
                    return 200_000;
                case ItemID.COW_GLOVES:
                case ItemID.COW_SHOES:
                    return 95_000;
                case 23285:
                case 23288:
                case 23291:
                case 23294:
                    return 15_000;
                default:
                    return 100_000;

            }
        } else if (shopId == OSRS_TOKENS_ONLY_STORE) {
            switch (itemDef.getId()) {
                case 20527:
                    return 500_000;
            }
        } else if (shopId == THIEVING_MASTER_STORE) {
            switch (itemDef.getId()) {

                case ItemID.THIEVING_HOOD:
                case ItemID.THIEVING_CAPE:
                    return 1500;
                case ItemID.THIEVING_CAPE_T_:
                    return 2000;
                case ItemID.DODGY_NECKLACE:
                    return 3000;
                case ItemID.BANDITS_BREW:
                    return 1500;
                case ItemID.ROGUE_MASK:
                    return 800;
                case ItemID.ROGUE_TOP:
                    return 1200;
                case ItemID.ROGUE_TROUSERS:
                    return 1000;
                case ItemID.ROGUE_GLOVES:
                case ItemID.ROGUE_BOOTS:
                    return 600;
                case ItemID.GLOVES_OF_SILENCE:
                    return 250;
                case 23224:
                    return 2500;

            }
        } else if (shopId == SKILLING_POINTS_STORE) {
            switch (itemDef.getId()) {
                case 3049:
                case 3050:
                    return 4;

                case 3051:
                case 3052:
                    return 4;

                case 2485:
                case 2486:
                    return 6;

                case 217:
                case 218:
                    return 8;

                case 5304:
                    return 25;

                case 7409:
                    return 300;

                case 1631:
                case 1632:
                    return 10;

                case 2349:
                case 2350:
                    return 2;

                case 2351:
                case 2352:
                    return 3;

                case 2353:
                case 2354:
                    return 4;

                case 2357:
                case 2358:
                    return 3;

                case 2359:
                case 2360:
                    return 5;

                case 2361:
                case 2362:
                    return 6;

                case 2363:
                case 2364:
                    return 8;

                case 395:
                case 396:
                    return 2;

                case 389:
                case 390:
                    return 4;

                case 11934:
                case 11935:
                    return 6;

                case 13439:
                case 13440:
                    return 8;

                case 775:
                    return 300;

                case ItemID.PURE_ESSENCE_2:
                    return 1;

                case ItemID.ESSENCE_PACK:
                    return 10;

                case 11738:
                    return 3000;

                case 12757:
                case 12759:
                case 12761:
                case 12763:
                    return 250;
                case 10172:
                    return 50;
                case 20246:
                    return 500;
                case 23448:
                    return 750;
                case 20269:
                case 20266:
                    return 750;
                case 10400:
                case 10402:
                case 10420:
                case 10422:
                    return 450;
                case 10416:
                case 10418:
                case 10436:
                case 10438:
                    return 250;
                case 12769:
                case 12771:
                    return 300;
                case 22842:
                    return 750;
                case 8465:
                    return 10;
                case 8322:
                    return 25;
                case 15190:
                    return 5000;
                case 22351:
                    return 1200;
                case 21031:
                case 13243:
                case 13241:
                    return 500;
                case 11920:
                    return 1000;
                case 11990:
                    return 300;
                case 6548:
                    return 775;
                case 12514:
                    return 375;
                case ItemID.HELM_OF_RAEDWALD:
                    return 750;
                case ItemID.CLUE_HUNTER_BOOTS:
                    return 450;
                case ItemID.CLUE_HUNTER_CLOAK:
                    return 550;
                case ItemID.CLUE_HUNTER_GLOVES:
                    return 450;
                case ItemID.CLUE_HUNTER_TROUSERS:
                    return 750;
                case ItemID.CLUE_HUNTER_GARB:
                    return 750;
                case 19699:
                    return 125;
                case 12600:
                    return 1500;
                case 13203:
                    return 50;
                case 4202:
                    return 750;
                case 12299:
                    return 15;
                case 12301:
                    return 15;
                case 12303:
                    return 15;
                case 12305:
                    return 15;
                case 12307:
                    return 15;
                case 12309:
                    return 15;
                case 12311:
                    return 15;
                case 12313:
                    return 15;
                case 12315:
                    return 150;
                case 12317:
                    return 150;
                case 12339:
                    return 150;
                case 12341:
                    return 150;
                case 12343:
                    return 150;
                case 12345:
                    return 150;
                case 12347:
                    return 150;
                case 12349:
                    return 150;
                case 12355:
                    return 35;
                case 12359:
                    return 350;
                case 12434:
                    return 800;
                case 13258:
                    return 100;
                case 13259:
                    return 100;
                case 13260:
                    return 100;
                case 13261:
                    return 25;
                case 5554:
                    return 125;
                case 5553:
                    return 125;
                case 5555:
                    return 125;
                case 5556:
                    return 50;
                case 5557:
                    return 50;
                case 10941:
                    return 75;
                case 10939:
                    return 75;
                case 10940:
                    return 75;
                case 10933:
                    return 25;
                case 11136:
                    return 50;
                case 11138:
                    return 50;
                case 11140:
                    return 50;
            }
        } else if (shopId == WOODCUTTING_STORE) {
            switch (itemDef.getId()) {
                case 6739:
                    return 1500;

                case 10941:
                    return 500;
                case 10939:
                    return 1250;
                case 10940:
                    return 1250;
                case 10933:
                    return 350;

                default:
                    return 0;
            }
        } else if (shopId == WARRIORS_GUILD_STORE) {
            switch (itemDef.getId()) {
                case ItemID.ATTACK_HOOD:
                case ItemID.STRENGTH_HOOD:
                case ItemID.DEFENCE_HOOD:
                case ItemID.HITPOINTS_HOOD:
                    return 250;
                case ItemID.ATTACK_CAPE:
                case ItemID.STRENGTH_CAPE:
                case ItemID.DEFENCE_CAPE:
                case ItemID.HITPOINTS_CAPE:
                    return 500;
                case ItemID.ATTACK_CAPE_T_:
                case ItemID.STRENGTH_CAPE_T_:
                case ItemID.DEFENCE_CAPE_T_:
                case ItemID.HITPOINTS_CAPE_T_:
                    return 1000;
                case 8844:
                    return 50;
                case 8845:
                    return 150;
                case 8846:
                    return 300;
                case 8847:
                    return 400;
                case 8848:
                    return 550;
                case 8849:
                    return 700;
                case 8850:
                    return 900;
                case 12954:
                    return 1500;
                case 10551:
                    return 1000;
                case 22477:
                    return 3000;
                default:
                    return 0;
            }
        } else if (shopId == PARTICIPATION_POINTS_EXCHANGE) {
            switch (itemDef.getId()) {
                case 12526:
                case 20065:
                case 20062:
                    return 500;
                case 20071:
                case 20068:
                case 20074:
                case 20077:
                case 20143:
                case 12532:
                case 12534:
                case 12536:
                case 12538:
                case 20002:
                    return 1000;
                case 22236:
                    return 2500;
                case 23237:
                    return 3500;
                case 22246:
                    return 1500;
                case 13190:
                    return 4000;
                case 6746:
                    return 150;
                case 15189:
                    return 2500;
                case 12791:
                    return 500;
                case 15177:
                    return 300;
                case 13116:
                    return 250;
                case 2528:
                    return 100;
                case 35:
                    return 25;
                case 15020:
                case 15021:
                case 15022:
                    return 50;
                case 15023:
                    return 120;
                case 15024:
                case 15025:
                    return 80;
                case 7774:
                    return 20;
                case 7775:
                    return 35;
                case 7776:
                    return 80;
            }
        } else if (shopId == BOSS_CONTRACT_STORE) {
            switch (itemDef.getId()) {
                case 22978:
                    return 500;
                case 15152:
                    return 350;
                case 11785:
                    return 150;
                case 13271:
                    return 150;
                case 22296:
                    return 125;
                case 20724:
                    return 115;
                case 4675:
                    return 12;
                case 11283:
                case 11284:
                    return 35;
                case 22003:
                case 22002:
                    return 60;
                case 21633:
                case 21634:
                    return 75;
                case 12954:
                    return 25;
                case 6563:
                    return 15;
                case 12002:
                    return 80;
                case 19553:
                    return 75;
                case 19550:
                    return 72;
                case 13256:
                    return 50;
                case 10828:
                    return 18;
                case 21902:
                    return 70;
                case 6918:
                    return 30;
                case 6916:
                    return 35;
                case 6924:
                    return 32;
                case 6889:
                    return 28;
                case 2579:
                    return 12;
                case 11791:
                    return 38;
                case 2577:
                    return 24;
                case 12596:
                    return 90;
                case 19994:
                    return 80;
                case 12924:
                case 12926:
                    return 120;
                case 21000:
                    return 90;
                case 22109:
                    return 95;
                case 22284:
                    return 34;
                case 11832:
                    return 62;
                case 11834:
                    return 65;
                case 11826:
                    return 48;
                case 11828:
                    return 56;
                case 11830:
                    return 54;
                case 4212:
                    return 25;
                case 4224:
                    return 16;
                case 10551:
                    return 30;
                case 6585:
                    return 22;
                case 7462:
                    return 8;
                default:
                    return ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEMS_VALUE);
            }
        } else if (shopId == OSRS_TOKENS_STORE) {
            return ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEMS_VALUE);
        }
        return ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEMS_VALUE);
    }

    /**
     * Get's the currency name for a given item in a shop.
     */
    public static String getCurrencyName(int shopId) {
        switch (shopId) {
            case PVP_STORE:
            case OUTLET_BLOOD_STORE:
            case UNTRADEABLES_STORE:
            case BLOOD_SKILLING_STORE:
                return "blood money";
            case CASTLEWARS_TICKET_EXCHANGE:
                return "castle wars tickets";
            case WARRIORS_GUILD_STORE:
                return "warrior guild token";
            case OSRS_TOKENS_ONLY_STORE:
                return "platinum tokens";
            case RANGING_GUILD_STORE:
                return "archery tickets";
            case COMMENDATION_POINTS_EXCHANGE:
            case COMMENDATION_POINTS_EXCHANGE_2:
                return "Commendation points";
            case TZHAAR_STORE:
                return "tokkuls";
            case GRACES_GRACEFUL_CLOTHING:
                return "mark of grace";
            case AQUAIS_NEIGE_STORE:
                return "hydro coins";
            case AGILITY_TICKET_EXCHANGE:
                return "agility tickets";
            //case SLAYER_EQUIPMENTS_STORE:
            //    return "slayer points";
            case POINTS_STORE:
                return "points";
            case PREMIUM_STORE:
            case PREMIUM_CLUE_STORE:
            case HOLIDAY_PREMIUM_STORE:
            case PVP_PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE_IRONMAN:
            case PVP_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_PREMIUM_STORE:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE:
            case DISCOUNTED_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE_IRONMAN:
            case LIMITED_SHOP:
            case HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES:
            case HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS:
            case HOLIDAY_PREMIUM_STORE_PETS_MISC:
            case HOLIDAY_PREMIUM_STORE_RESOURCES:
                return "premium points";
            case SLAYER_REWARDS:
                return "slayer points";
            case VOTING_STORE:
            case VOTING_STORE_IRONMAN:
                return "voting points";
            case MINIGAME_STORE:
                return "minigame points";
            case SKILLING_POINTS_STORE:
                return "skilling points";
            case BOSS_CONTRACT_STORE:
                return "boss contract points";
            case PARTICIPATION_POINTS_EXCHANGE:
                return "participation points";
            // case THIEVING_STORE:
            case 34:
                return "mining points";
            case 35:
                return "smithing points";
            case THIEVING_MASTER_STORE:
                return "thieving points";
            case WOODCUTTING_STORE:
                return "woodcutting points";
            case FLETCHING_SKILL_MASTER:
                return "fletching points";
            case 36:
                return "fishing points";
            case MASTER_CRAFTING_STORE:
                return "crafting points";
            case MASTER_FARMING_STORE:
                return "farming points";
            case MASTER_RUNECRAFTING_STORE:
                return "runecrafting points";
            case MASTER_COOKING_STORE:
                return "cooking points";
            case MASTER_PRAYER_STORE:
                return "prayer points";
            case MASTER_HERBLORE_STORE:
                return "herblore points";
            case MASTER_AGILITY_STORE:
                return "agility points";
            case MASTER_FIREMAKING_STORE:
                return "firemaking points";
            case PROSPECTOR_PERCYS_NUGGET_SHOP:
                return "golden nuggets";
        }
        return "coins";
    }

    /**
     * Gets a player's currency amount for the given item in a shop.
     */
    private static int getCurrencyAmount(Player player, int shopId) {
        switch (shopId) {
            case POINTS_STORE:
                return player.getPoints().get(Points.GRINDERSCAPE_POINTS);
            case COMMENDATION_POINTS_EXCHANGE:
            case COMMENDATION_POINTS_EXCHANGE_2:
                return player.getPoints().get(Points.COMMENDATION);
            case CASTLEWARS_TICKET_EXCHANGE:
                return player.getInventory().getAmount(ItemID.CASTLE_WARS_TICKET);
            case WARRIORS_GUILD_STORE:
                return player.getInventory().getAmount(ItemID.WARRIOR_GUILD_TOKEN);
            case OSRS_TOKENS_ONLY_STORE:
                return player.getInventory().getAmount(ItemID.PLATINUM_TOKEN);
            case RANGING_GUILD_STORE:
                return player.getInventory().getAmount(ItemID.ARCHERY_TICKET);
            case PVP_STORE:
            case OUTLET_BLOOD_STORE:
            case UNTRADEABLES_STORE:
            case BLOOD_SKILLING_STORE:
                return player.getInventory().getAmount(ItemID.BLOOD_MONEY);
            case TZHAAR_STORE:
                return player.getInventory().getAmount(ItemID.TOKKUL);
            case GRACES_GRACEFUL_CLOTHING:
                return player.getInventory().getAmount(ItemID.MARK_OF_GRACE);
            case AQUAIS_NEIGE_STORE:
                return player.getInventory().getAmount(23497);
            case AGILITY_TICKET_EXCHANGE:
                return player.getInventory().getAmount(ItemID.AGILITY_ARENA_TICKET);
            case SKILLING_POINTS_STORE:
                return player.getPoints().get(Points.SKILLING_POINTS);
            case BOSS_CONTRACT_STORE:
                return player.getPoints().get(Points.BOSS_CONTRACT_POINTS);
            case PARTICIPATION_POINTS_EXCHANGE:
                return player.getPoints().get(Points.PARTICIPATION_POINTS);
            case PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE:
            case PVP_PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE_IRONMAN:
            case PREMIUM_CLUE_STORE:
            case PVP_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_PREMIUM_STORE:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE:
            case DISCOUNTED_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE_IRONMAN:
            case LIMITED_SHOP:
            case HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES:
            case HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS:
            case HOLIDAY_PREMIUM_STORE_PETS_MISC:
            case HOLIDAY_PREMIUM_STORE_RESOURCES:
                return player.getPoints().get(Points.PREMIUM_POINTS);
            case SLAYER_REWARDS:
                return player.getPoints().get(Points.SLAYER_POINTS);
            case VOTING_STORE:
            case VOTING_STORE_IRONMAN:
                return player.getPoints().get(Points.VOTING_POINTS);
            //case SLAYER_EQUIPMENTS_STORE:
            //    return player.getPoints().get(Points.SLAYER_POINTS);
            case MINIGAME_STORE:
                return player.getPoints().get(Points.MINIGAME_POINTS);
            case WOODCUTTING_STORE:
                return SkillTaskManager.getPoints(player, Skill.WOODCUTTING);
            case FLETCHING_SKILL_MASTER:
                return SkillTaskManager.getPoints(player, Skill.FLETCHING);
            case THIEVING_MASTER_STORE:
                return SkillTaskManager.getPoints(player, Skill.THIEVING);
            case 34:
                return SkillTaskManager.getPoints(player, Skill.MINING);
            case 35:
                return SkillTaskManager.getPoints(player, Skill.SMITHING);
            case 36:
                return SkillTaskManager.getPoints(player, Skill.FISHING);
            case MASTER_CRAFTING_STORE:
                return SkillTaskManager.getPoints(player, Skill.CRAFTING);
            case MASTER_FARMING_STORE:
                return SkillTaskManager.getPoints(player, Skill.FARMING);
            case MASTER_RUNECRAFTING_STORE:
                return SkillTaskManager.getPoints(player, Skill.RUNECRAFTING);
            case MASTER_COOKING_STORE:
                return SkillTaskManager.getPoints(player, Skill.COOKING);
            case MASTER_PRAYER_STORE:
                return SkillTaskManager.getPoints(player, Skill.PRAYER);
            case MASTER_HERBLORE_STORE:
                return SkillTaskManager.getPoints(player, Skill.HERBLORE);
            case MASTER_AGILITY_STORE:
                return SkillTaskManager.getPoints(player, Skill.AGILITY);
            case MASTER_FIREMAKING_STORE:
                return SkillTaskManager.getPoints(player, Skill.FIREMAKING);
            case PROSPECTOR_PERCYS_NUGGET_SHOP:
                return player.getInventory().getAmount(ItemID.GOLDEN_NUGGET);
            default:
                return player.getInventory().getAmount(ItemID.COINS);
        }
    }

    /**
     * Decrements a player's currency
     */
    private static void decrementCurrency(Player player, long amount, int shopId) {
        decrementCurrency(player, (int) Math.min(amount, Integer.MAX_VALUE), shopId);
    }

    private static void decrementCurrency(Player player, int amount, int shopId) {
        switch (shopId) {
            case POINTS_STORE:
                player.getPoints().decrease(Points.GRINDERSCAPE_POINTS, amount);
                break;
            case COMMENDATION_POINTS_EXCHANGE:
            case COMMENDATION_POINTS_EXCHANGE_2:
                player.getPoints().decrease(Points.COMMENDATION, amount);
                break;
            case CASTLEWARS_TICKET_EXCHANGE:
                player.getInventory().delete(ItemID.CASTLE_WARS_TICKET, amount);
                break;
            case WARRIORS_GUILD_STORE:
                player.getInventory().delete(ItemID.WARRIOR_GUILD_TOKEN, amount);
                break;
            case OSRS_TOKENS_ONLY_STORE:
                player.getInventory().delete(ItemID.PLATINUM_TOKEN, amount);
                break;
            case RANGING_GUILD_STORE:
                player.getInventory().delete(ItemID.ARCHERY_TICKET, amount);
                break;
            case PVP_STORE:
            case OUTLET_BLOOD_STORE:
            case UNTRADEABLES_STORE:
            case BLOOD_SKILLING_STORE:
                player.getInventory().delete(ItemID.BLOOD_MONEY, amount);
                break;
            case SKILLING_POINTS_STORE:
                AchievementManager.processFor(AchievementType.SKILLING_ADDICT, amount, player);
                player.getPoints().decrease(Points.SKILLING_POINTS, amount);
                break;
            case PARTICIPATION_POINTS_EXCHANGE:
                player.getPoints().decrease(Points.PARTICIPATION_POINTS, amount);
                break;
            case BOSS_CONTRACT_STORE:
                player.getPoints().decrease(Points.BOSS_CONTRACT_POINTS, amount);
                break;
            case PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE:
            case PREMIUM_CLUE_STORE:
            case PVP_PREMIUM_STORE:
            case HOLIDAY_PREMIUM_STORE_IRONMAN:
            case PVP_PREMIUM_STORE_IRONMAN:
            case LIMITED_SHOP:
            case DISCOUNTED_PREMIUM_STORE:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE:
            case DISCOUNTED_PREMIUM_STORE_IRONMAN:
            case DISCOUNTED_HOLIDAY_PREMIUM_STORE_IRONMAN:
            case HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES:
            case HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS:
            case HOLIDAY_PREMIUM_STORE_PETS_MISC:
            case HOLIDAY_PREMIUM_STORE_RESOURCES:
                player.getPoints().decrease(Points.PREMIUM_POINTS, amount);
                break;
            case SLAYER_REWARDS:
                player.getPoints().decrease(Points.SLAYER_POINTS, amount);
                break;
            case VOTING_STORE:
            case VOTING_STORE_IRONMAN:
                player.getPoints().decrease(Points.VOTING_POINTS, amount);
                break;
            //case SLAYER_EQUIPMENTS_STORE:
            //    player.getPoints().decrease(Points.SLAYER_POINTS, amount);
            //    break;
            case MINIGAME_STORE:
                player.getPoints().decrease(Points.MINIGAME_POINTS, amount);
                break;
            case AGILITY_TICKET_EXCHANGE:
                player.getInventory().delete(ItemID.AGILITY_ARENA_TICKET, amount);
                break;
            case TZHAAR_STORE:
                player.getInventory().delete(ItemID.TOKKUL, amount);
                break;
            case GRACES_GRACEFUL_CLOTHING:
                player.getInventory().delete(ItemID.MARK_OF_GRACE, amount);
                break;
            case AQUAIS_NEIGE_STORE:
                player.getInventory().delete(23497, amount);
                break;
            case WOODCUTTING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.WOODCUTTING.ordinal()] -= amount;
                break;
            case FLETCHING_SKILL_MASTER:
                player.getSkillTaskManager().getPoints()[Skill.FLETCHING.ordinal()] -= amount;
                break;
            case THIEVING_MASTER_STORE:
                player.getSkillTaskManager().getPoints()[Skill.THIEVING.ordinal()] -= amount;
                break;
            case 34:
                player.getSkillTaskManager().getPoints()[Skill.MINING.ordinal()] -= amount;
                break;
            case 35:
                player.getSkillTaskManager().getPoints()[Skill.SMITHING.ordinal()] -= amount;
                break;
            case 36:
                player.getSkillTaskManager().getPoints()[Skill.FISHING.ordinal()] -= amount;
                break;
            case MASTER_CRAFTING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.CRAFTING.ordinal()] -= amount;
                break;
            case MASTER_FARMING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.FARMING.ordinal()] -= amount;
                break;
            case MASTER_RUNECRAFTING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.RUNECRAFTING.ordinal()] -= amount;
                break;
            case MASTER_COOKING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.COOKING.ordinal()] -= amount;
                break;
            case MASTER_PRAYER_STORE:
                player.getSkillTaskManager().getPoints()[Skill.PRAYER.ordinal()] -= amount;
                break;
            case MASTER_HERBLORE_STORE:
                player.getSkillTaskManager().getPoints()[Skill.HERBLORE.ordinal()] -= amount;
                break;
            case MASTER_FIREMAKING_STORE:
                player.getSkillTaskManager().getPoints()[Skill.FIREMAKING.ordinal()] -= amount;
                break;
            case MASTER_AGILITY_STORE:
                player.getSkillTaskManager().getPoints()[Skill.AGILITY.ordinal()] -= amount;
                break;
            case PROSPECTOR_PERCYS_NUGGET_SHOP:
                player.getInventory().delete(ItemID.GOLDEN_NUGGET, amount);
                break;
            default:
                player.getInventory().delete(ItemID.COINS, amount);
                break;
        }
    }

    /**
     * Increments a player's currency Used when selling things to a store.
     */
    private static void incrementCurrency(Player player, long amount, int shopId) {
        incrementCurrency(player, (int) Math.min(amount, Integer.MAX_VALUE), shopId);
    }

    private static void incrementCurrency(Player player, int amount, int shopId) {
        switch (shopId) {
            case POINTS_STORE:
                player.getPoints().increase(Points.GRINDERSCAPE_POINTS, amount);
                break;
            case COMMENDATION_POINTS_EXCHANGE:
            case COMMENDATION_POINTS_EXCHANGE_2:
                player.getPoints().increase(Points.COMMENDATION, amount);
                break;
            case TZHAAR_STORE:
                player.getInventory().add(ItemID.TOKKUL, amount);
                break;
            case GRACES_GRACEFUL_CLOTHING:
                player.getInventory().add(ItemID.MARK_OF_GRACE, amount);
                break;
            case AQUAIS_NEIGE_STORE:
                player.getInventory().add(23497, amount);
                break;
            case CASTLEWARS_TICKET_EXCHANGE:
                player.getInventory().add(ItemID.CASTLE_WARS_TICKET, amount);
                break;
            case WARRIORS_GUILD_STORE:
                player.getInventory().add(ItemID.WARRIOR_GUILD_TOKEN, amount);
                break;
            case OSRS_TOKENS_ONLY_STORE:
                player.getInventory().add(ItemID.PLATINUM_TOKEN, amount);
                break;
            case RANGING_GUILD_STORE:
                player.getInventory().add(ItemID.ARCHERY_TICKET, amount);
                break;
            case PVP_STORE:
            case OUTLET_BLOOD_STORE:
            case UNTRADEABLES_STORE:
            case BLOOD_SKILLING_STORE:
                player.getInventory().add(ItemID.BLOOD_MONEY, amount);
                break;
            case AGILITY_TICKET_EXCHANGE:
                player.getInventory().add(ItemID.AGILITY_ARENA_TICKET, amount);
                break;
            case PROSPECTOR_PERCYS_NUGGET_SHOP:
                player.getInventory().add(ItemID.GOLDEN_NUGGET, amount);
                break;
            default:
                player.getInventory().add(ItemID.COINS, amount);
                break;
        }
    }

    /**
     * Does this shop buy items?
     */
    public static boolean buysItems(Shop shop, int itemId) {
        if (shop.getId() == ShopIdentifiers.GENERAL_STORE)
            return true;
        return /*shop.getId() == ShopIdentifiers.PVP_STORE && shop.getAmount(itemId, true) >= 1*/false;
    }

    /**
     * Does the given shop fully delete items?
     */
//    public static boolean deletesItems(int shopId) {
//		return shopId == GENERAL_STORE;
//	}
    public static boolean deletesItems(int shopId) {
        return shopId != VOTING_STORE && shopId != VOTING_STORE_IRONMAN
                && shopId != PREMIUM_CLUE_STORE
                && shopId != PREMIUM_STORE
                && shopId != PREMIUM_MISC_STORE
                && shopId != HOLIDAY_PREMIUM_STORE_MYSTERY_BOXES
                && shopId != HOLIDAY_PREMIUM_STORE_LUXURY_ITEMS
                && shopId != HOLIDAY_PREMIUM_STORE_PETS_MISC
                && shopId != HOLIDAY_PREMIUM_STORE_RESOURCES
                ;
    }

    /**
     * Does the given shop restock on items?
     */
    public static boolean restocksItem(int shopId) {
        return shopId != GENERAL_STORE;
    }

    /**
     * Checks if the player is viewing the given shop.
     */
    public static boolean viewingShop(Player player, int id) {
        return player.getShop() != null && player.getShop().getId() == id;
    }

    /**
     * Gets the modifier for calculating the price of a player sold item being sold in the general store
     */
    public static double getModifier(int amount) {
        if (amount > 10000) {
            return 1.15;
        } else if (amount > 2500) {
            return 1.35;
        } else if (amount > 1000) {
            return 1.60;
        } else if (amount > 500) {
            return 1.85;
        } else if (amount > 250) {
            return 2.00;
        } else if (amount > 100) {
            return 2.15;
        } else if (amount > 50) {
            return 2.30;
        } else if (amount > 20) {
            return 2.50;
        } else if (amount > 10) {
            return 2.65;
        } else if (amount > 5) {
            return 2.75;
        } else if (amount > 2) {
            return 2.85;
        } else {
            return 3.0;
        }
    }
}
