package com.grinder.game.entity.agent.player.death;

import com.grinder.game.content.GameMode;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.item.ItemsKeptOnDeath;
import com.grinder.game.content.pvp.bountyhunter.reward.Emblem;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.content.item.degrading.DegradableType;
import com.grinder.game.model.item.BrokenItems;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.SkullType;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseLostDeathLogs;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-04
 */
public class PlayerDeathUtil {

    static ItemContainer[] containersRiskedOnDeath(final Player player){

        final Set<ItemContainer> containers = new HashSet<>();

        containers.add(player.getInventory());
        containers.add(player.getEquipment());

        if(player.getInventory().contains(ItemID.RUNE_POUCH))
            containers.add(player.getRunePouch());

        if(player.getInventory().contains(ItemID.LOOTING_BAG))
            containers.add(player.getLootingBag().getContainer());

        return containers.toArray(new ItemContainer[]{});
    }

    static int maxItemsKeptOnDeath(final Player player){

        final boolean skulled = player.isSkulled();

        if (skulled && player.getSkullType() == SkullType.RED_SKULL)
            return 0;

        return (skulled ? 0 : 3) + (PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM) ? 1 : 0);
    }

    public static Item getRandomKillReward(){
        return new Item(12746 + Misc.getRandomInclusive(5));
    }

    public static boolean rollRandomKillReward(){
        return Misc.randomChance(35.0F);
    }

    static boolean keepItem(final Item item){
        return ItemsKeptOnDeath.isKeptOnDeath(item.getId());
    }

    static boolean breakItem(final Item item){
        return BrokenItems.breaksOnDeath(item.getId());
    }

    static boolean dropItem(final Item item){
        if(Emblem.isMysteriousEmblem(item.getId()))
            return false;
        if(DegradableType.findBrokenBarrowsVersionItemId(item.getId()).isPresent())
            return true;
        return item.getDefinition().isTradeable();
    }

    static boolean loseItem(final Item item){
//        if(item.getId() == ItemID.RUNE_POUCH)
//            return true;
        if (Emblem.isMysteriousEmblem(item.getId())) {
            return true;
        }
        if (item.getDefinition().getName().contains("(broken)")) {
            return true;
        }
        final int itemId = item.getId();

        return itemId == ItemID.LOOTING_BAG
                || itemId == 15716
                || itemId == 15717
                || itemId == 15718
                || itemId == 15719
                || itemId == ItemID.CLUE_BOX
                || itemId == ItemID.BOLT_POUCH
                || itemId == ItemID.FLAMTAER_BAG
                || itemId == ItemID.LUNAR_AMULET
                || itemId == ItemID.LUNAR_BOOTS
                || itemId == ItemID.LUNAR_GLOVES
                || itemId == ItemID.LUNAR_HELM
                || itemId == ItemID.LUNAR_LEGS
                || itemId == ItemID.LUNAR_TORSO
                || itemId == ItemID.LUNAR_RING
                || itemId == ItemID.LUNAR_STAFF
                || itemId == ItemID.RING_OF_CHAROS
                || itemId == ItemID.RING_OF_CHAROS_A_
                || itemId == ItemID.CHRONICLE
                || itemId == ItemID.STEEL_GAUNTLETS
                || itemId == ItemID.MAGIC_BUTTERFLY_NET
                || itemId == ItemID.JAR_GENERATOR
                || itemId == ItemID.AMULET_OF_THE_DAMNED;
    }

    static void onHardcoreIronmanDeath(final Player player){
        player.setGameMode(GameMode.IRONMAN);
        player.getPacketSender().sendRights();
        //player.setCrown(player.getGameMode().getCrown());
        player.getInventory().replaceFirst(20792, 12810);
        player.getInventory().replaceFirst(20794, 12811);
        player.getInventory().replaceFirst(20796, 12812);
        BankUtil.replaceAll(player.getBanks(), 20792, 12810);
        BankUtil.replaceAll(player.getBanks(), 20794, 12811);
        BankUtil.replaceAll(player.getBanks(), 20796, 12812);
        player.setTitle("@bla@Ironman</col>");
        player.updateAppearance();
        player.getPacketSender().sendMessage("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
        PlayerUtil.broadcastMessage("" + PlayerUtil.getImages(player) + "" + player.getUsername() + " has fallen as an Hardcore Iron Man!");
    }

    static void onOneLifeGameModeDeath(final Player player){
        player.setFallenOneLifeGameMode(true);
        player.BLOCK_ALL_BUT_TALKING = true;
        player.updateAppearance();
        player.getPacketSender().sendMessage("You have fallen as a One life, your account can no longer be used or do any actions.");
        PlayerUtil.broadcastMessage("" + PlayerUtil.getImages(player) + "" + player.getUsername() + " has fallen as One life game mode! Combat level: " + player.getSkillManager().calculateCombatLevel() +", Total level: " + player.getSkillManager().countTotalLevel() +".");
    }

    static void logItemIfValuable(final Player player, final Item item){
        // Disable loggin for spawn game modes
        if (player.getGameMode().isSpawn()) {
            return;
        }
        final long highAlchValue = item.getValue(ItemValueType.HIGH_ALCHEMY);
        final long priceEstValue = item.getValue(ItemValueType.PRICE_CHECKER);
        final long tokenValue = item.getValue(ItemValueType.OSRS_STORE);

        if ((highAlchValue * item.getAmount() >= 50000) || (item.getAmount() * priceEstValue >= 5000000)
                || (item.getAmount() * tokenValue > 5000000) || BrokenItems.breaksOnDeath(item.getId())
                || (item.getId() >= 15200 && item.getId() <= 15350)
                || item.getAmount() >= 100000 || item.getAmount() > 1000
                || item.getAmount() > 1000) {
            Logging.log("lostOnDeath", "" + player.getUsername() + " lost: " + item.getDefinition().getName() + " x: " + Misc.insertCommasToNumber(item.getAmount()) + " on death");

            // Database logging
            new DatabaseLostDeathLogs(
                    SQLManager.Companion.getINSTANCE(),
                    player.getUsername(),
                    item.getDefinition().getName(),
                    item.getAmount(),
                    (player.getArea() != null ? player.getArea().toString() : " null")
            ).schedule(player);
        }
    }
}
