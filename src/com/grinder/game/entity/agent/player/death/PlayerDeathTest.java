package com.grinder.game.entity.agent.player.death;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.agent.player.bot.script.impl.AfkScript;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-04
 */
public class PlayerDeathTest {

    private final static boolean RANDOMIZE_GEAR = false;

    private final static Item[] INVENTORY = new Item[]{
            new Item(ItemID.RUNE_POUCH),
            new Item(ItemID.LOOTING_BAG),
            new Item(ItemID.MANTA_RAY),
            new Item(ItemID.IRONMAN_HELM),
            new Item(ItemID.IRONMAN_PLATEBODY),
            new Item(ItemID.IRONMAN_PLATELEGS),
            new Item(ItemID.COINS, 10_000),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE),
            new Item(ItemID.DHAROKS_GREATAXE)
    };

    private final static Item[] EQUIPMENT = new Item[]{
           /* new Item(ItemIdentifiers.FIGHTER_HAT),
            new Item(ItemIdentifiers.FIGHTER_TORSO),
            new Item(ItemIdentifiers.BLACK_ELEGANT_LEGS),
            new Item(ItemIdentifiers.BARROWS_GLOVES),
            new Item(ItemIdentifiers.FANCY_BOOTS),
            new Item(ItemIdentifiers.FIRE_CAPE),
            new Item(ItemIdentifiers.ABYSSAL_WHIP),
            new Item(ItemIdentifiers.DRAGONFIRE_SHIELD),
            new Item(ItemIdentifiers.BRONZE_ARROW),
            new Item(ItemIdentifiers.AMULET_OF_FURY_OR_),*/
            new Item(ItemID.DRAGONFIRE_SHIELD_2),
            new Item(ItemID.ANGLER_TOP),
            new Item(ItemID.HITPOINTS_CAPE),
            new Item(ItemID.ANGLER_HAT)
    };

    public static void main(String[] args) throws Throwable {

        new ItemDefinitionLoader().load();
        final BotPlayer player = new BotPlayer("test", AreaManager.WILD.boundaries().get(0).getRandomPosition());
        setItems(player);

        System.out.println("Running simulation of item dropping when a player dies in the Wilderness.");
        final List<Item> inventoryItems = player.getInventory().getValidItems();
        final List<Item> equipmentItems = player.getEquipment().getValidItems();
        System.out.println();
        System.out.println("equipment["+equipmentItems.size()+"]\t="+equipmentItems);
        System.out.println("inventory["+inventoryItems.size()+"]\t="+inventoryItems);
        System.out.println();
        System.out.println("Without protect item prayer and unskulled:");
        System.out.println();
        System.out.println(new ItemsKeptOnDeathGenerator(player, false).generate());
        System.out.println();

        player.setSkullTimer(1);
        player.setSkullType(SkullType.RED_SKULL);
        System.out.println("With red skulled:");
        System.out.println();
        System.out.println(new ItemsKeptOnDeathGenerator(player, false).generate());
        System.out.println();

        player.setSkullType(SkullType.WHITE_SKULL);
        player.setPrayerActive(PrayerHandler.PROTECT_ITEM, true);
        System.out.println("With white skulled and protect item prayer:");
        System.out.println();
        System.out.println(new ItemsKeptOnDeathGenerator(player, false).generate());
        System.out.println();


    }

    private static void setItems(final BotPlayer player){
        if(RANDOMIZE_GEAR) {
            player.setActiveScript(new AfkScript());
            player.getActiveScript().setPlayer(player);
            player.getActiveScript().initialize();
        } else {
            player.getInventory().addItemSet(INVENTORY);
            player.getEquipment().addItemSet(EQUIPMENT);
        }
    }
}
