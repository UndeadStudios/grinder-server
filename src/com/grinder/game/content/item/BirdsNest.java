package com.grinder.game.content.item;

import com.grinder.game.GameConstants;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.io.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * TODO: redo this
 */
public class BirdsNest {

    public static void openRingNest(Player player) {
        Item ring = new Item(ItemID.GOLD_RING, 1);

        if (Misc.randomChance(5)) {
            ring = new Item(ItemID.DIAMOND_RING, 1);
        } else if (Misc.randomChance(15)) {
            ring = new Item(ItemID.RUBY_RING, 1);
        } else if (Misc.randomChance(25)) {
            ring = new Item(ItemID.EMERALD_RING, 1);
        } else if (Misc.randomChance(35)) {
            ring = new Item(ItemID.SAPPHIRE_RING, 1);
        }
        player.getInventory().delete(new Item(ItemID.BIRD_NEST_5, 1));
        player.getInventory().add(ring);
        player.getInventory().add(new Item(ItemID.BIRD_NEST_6, 1));
    }

    public static void openSeedNest(Player player) {
        Item seeds = new Item(ItemID.ACORN, 1);

        if (Misc.random(202) == 0) {
            seeds = new Item(ItemID.MAGIC_SEED, 1);
        } else if (Misc.random(91) == 0) {
            seeds = new Item(ItemID.SPIRIT_SEED, 1);
        } else if (Misc.random(59) == 0) {
            seeds = new Item(ItemID.CALQUAT_TREE_SEED, 1);
        } else if (Misc.random(45) == 0) {
            seeds = new Item(ItemID.PALM_TREE_SEED, 1);
        } else if (Misc.random(38) == 0) {
            seeds = new Item(ItemID.YEW_SEED, 1);
        } else if (Misc.random(30) == 0) {
            seeds = new Item(ItemID.PAPAYA_TREE_SEED, 1);
        } else if (Misc.random(24) == 0) {
            seeds = new Item(ItemID.PINEAPPLE_SEED, 1);
        } else if (Misc.random(18) == 0) {
            seeds = new Item(ItemID.MAPLE_SEED, 1);
        } else if (Misc.random(14) == 0) {
            seeds = new Item(ItemID.CURRY_TREE_SEED, 1);
        } else if (Misc.random(12) == 0) {
            seeds = new Item(ItemID.ORANGE_TREE_SEED, 1);
        } else if (Misc.random(10) == 0) {
            seeds = new Item(ItemID.BANANA_TREE_SEED, 1);
        } else if (Misc.random(7) == 0) {
            seeds = new Item(ItemID.WILLOW_SEED, 1);
        } else if (Misc.random(6) == 0) {
            seeds = new Item(ItemID.APPLE_TREE_SEED, 1);
        }

        player.getInventory().delete(new Item(ItemID.BIRD_NEST_4, 1));
        player.getInventory().add(seeds);
        player.getInventory().add(new Item(ItemID.BIRD_NEST_6, 1));
    }

}
