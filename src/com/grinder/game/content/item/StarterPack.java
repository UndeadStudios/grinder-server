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
public class StarterPack {

    private static final Logger LOGGER = LogManager.getLogger(StarterPack.class.getSimpleName());
    private static final Path SAVES_PATH = Paths.get(GameConstants.SAVES_DIRECTORY);

    private static final ArrayList<String> STARTER_RECEIVED_IP_LIST = new ArrayList<>();
    private static final ArrayList<String> STARTER_RECEIVED_MAC_LIST = new ArrayList<>();

    public static void init() {
    	STARTER_RECEIVED_IP_LIST.clear();
        STARTER_RECEIVED_MAC_LIST.clear();

        try {
            initializeList("IPStarter.txt", STARTER_RECEIVED_IP_LIST);
            initializeList("MacStarter.txt", STARTER_RECEIVED_MAC_LIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeList(String fileName, ArrayList<String> list) throws IOException {

        final File file = SAVES_PATH.resolve(fileName).toFile();

        if(!file.exists()){
            if(file.createNewFile()){
                LOGGER.info("Created a new file at '"+ file +"'");
            }
        }

        final BufferedReader reader = new BufferedReader(new FileReader(file));

        String data;

        while ((data = reader.readLine()) != null) {
            list.add(data);
        }

        reader.close();
    }

    public static void addStarterIP(final String ipAddress) {

        if (!STARTER_RECEIVED_IP_LIST.contains(ipAddress))
            FileUtil.appendToFile(SAVES_PATH.resolve("IPStarter.txt"), ipAddress, true);

        STARTER_RECEIVED_IP_LIST.add(ipAddress);

        //init();
    }
    
    public static void addMacStarter(final String macAddress) {

        if (!STARTER_RECEIVED_MAC_LIST.contains(macAddress))
            FileUtil.appendToFile(SAVES_PATH.resolve("MacStarter.txt"), macAddress, true);

        STARTER_RECEIVED_MAC_LIST.add(macAddress);

        //init();
    }

    public static boolean starterReceivedAtIpAddress(String ipAddress) {
        return STARTER_RECEIVED_IP_LIST.contains(ipAddress);
    }
    
    public static boolean starterReceivedAtMacAddress(String macAddress) {
        return STARTER_RECEIVED_MAC_LIST.contains(macAddress);
    }

    public static void openStarterPack(Player player) {
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
            return;
        }
        if (player.getGameMode().isIronman()) {
            player.getPacketSender().sendMessage("You can't use the starter pack on Iron Man.");
            return;
        }
        if (player.getGameMode().isHardcore()) {
            player.getPacketSender().sendMessage("You can't use the starter pack on Hardcore Iron Man.");
            return;
        }
        if (player.getGameMode().isUltimate()) {
            player.getPacketSender().sendMessage("You can't use the starter pack on Hardcore Iron Man.");
            return;
        }
        if (player.getGameMode().isSpawn()) {
            player.getPacketSender().sendMessage("You can't use the starter pack in spawn game mode.");
            return;
        }
        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
        //init();
        /*if (player.getMacAddress().equals("") || player.getMacAddress().equals("null") || player.getMacAddress() == null) {
            player.getPacketSender().sendMessage("You have already claimed your starter pack!");
            return;
        }*/ // Temp removing the host address check to see how players reacts and will add it back later
        /*if (starterReceivedAtIpAddress(player.getHostAddress()) || player.receivedStarter() || starterReceivedAtMacAddress(player.getMacAddress())) {
            player.getPacketSender().sendMessage("You have already claimed your starter pack!");
            return;
        }*/ // Temp removing the host address check to see how players reacts and will add it back later
        if (starterReceivedAtIpAddress(player.getHostAddress()) || player.receivedStarter()) {
            player.getPacketSender().sendMessage("You have already claimed your starter pack!");
            return;
        }
        if (player.getInventory().countFreeSlots() < 17) {
            player.getPacketSender().sendMessage("You need at least 17 free inventory slots to claim your Starter pack.");
            return;
        }
        addMacStarter(player.getMacAddress());
        addStarterIP(player.getHostAddress());
        player.setReceivedStarter(true);
        init();
        player.getInventory().delete(ItemID.FREE_TO_PLAY_STARTER_PACK, 1);
        //player.getInventory().add(ItemID.COINS, 50_000);  // Past 1M
        BankUtil.addToBank(player, new Item(ItemID.COINS, 50_000));
        BankUtil.addToBank(player, new Item(ItemID.SLAYER_RING_8_, 1));
        BankUtil.addToBank(player, new Item(ItemID.CAKE_2, 50));
        BankUtil.addToBank(player, new Item(ItemID.CHOCOLATE_CAKE_2, 50));
        BankUtil.addToBank(player, new Item(21691, 25));
        BankUtil.addToBank(player, new Item(ItemID.COMBAT_POTION_3_2, 10));
        BankUtil.addToBank(player, new Item(ItemID.AIR_RUNE, 250));
        BankUtil.addToBank(player, new Item(ItemID.WATER_RUNE, 250));
        BankUtil.addToBank(player, new Item(ItemID.EARTH_RUNE, 250));
        BankUtil.addToBank(player, new Item(ItemID.FIRE_RUNE, 250));
        BankUtil.addToBank(player, new Item(ItemID.MIND_RUNE, 150));
        BankUtil.addToBank(player, new Item(ItemID.CHAOS_RUNE, 50));
        player.getInventory().add(ItemID.ADAMANT_BATTLEAXE, 1);
        //player.getInventory().add(ItemID.DRAGON_LONGSWORD, 1);
        player.getInventory().add(ItemID.DORGESHUUN_CROSSBOW, 1);
        player.getInventory().add(ItemID.BONE_BOLT_PACK, 1);
        player.getInventory().add(ItemID.BRONZE_KNIFE, 50);
/*        player.getInventory().add(ItemID.INITIATE_SALLET, 1);
        player.getInventory().add(ItemID.INITIATE_HAUBERK, 1);
        if (player.getAppearance().isMale()) {
            player.getInventory().add(ItemID.INITIATE_CUISSE, 1);
        } else {
            player.getInventory().add(ItemID.PROSELYTE_TASSET, 1);
        }*/
        /*final int randomCape;
        randomCape = Misc.getRandomInclusive(6);
        switch (randomCape) {
            case 0:
                player.getInventory().add(1007, 1);
                break;
            case 1:
                player.getInventory().add(1019, 1);
                break;
            case 2:
                player.getInventory().add(1021, 1);
                break;
            case 3:
                player.getInventory().add(1023, 1);
                break;
            case 4:
                player.getInventory().add(1027, 1);
                break;
            case 5:
                player.getInventory().add(1029, 1);
                break;
            case 6:
                player.getInventory().add(1031, 1);
                break;
        }*/
        player.getInventory().add(ItemID.VOTING_MYSTERY_BOX, 1);
        player.getInventory().add(ItemID.SWAMP_LIZARD, 1);
        player.getInventory().add(ItemID.GUAM_TAR, 50);
        player.getInventory().add(ItemID.MYSTERY_BOX, 1);
        player.getInventory().add(ItemID.CLUE_SCROLL_EASY_, 1);
        player.getInventory().add(ItemID.FALADOR_TELEPORT, 10);
        player.getPacketSender().sendMessage("<img=766> You have opened your starter pack!");
        player.getPacketSender().sendMessage("<img=766> Start your adventure journey by teleporting to Experiments to train your stats!");
        player.getPacketSender().sendSound(72);
    }
}
