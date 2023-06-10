package com.grinder.game.entity.agent.player.death;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.death.ItemsKeptOnDeathGenerator.Result;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;

/**
 * Handles items kept on death.
 *
 * @author Swiffy
 */
public class ItemsKeptOnDeath {

    /**
     * Sends the items kept on death interface for a player.
     *
     * @param player Player to send the items kept on death interface for.
     */
    public static void open(Player player) {
		player.getMotion().clearSteps();
		player.getPacketSender().sendMinimapFlagRemoval();
        updateInterface(player); //Send info on the interface.
        player.getPacketSender().sendInterface(17100); //Open the interface.
    }

    /**
     * Sends the items kept on death data for a player.
     *
     * @param player Player to send the items kept on death data for.
     */
    public static void updateInterface(Player player) {
        
        final PacketSender packetSender = player.getPacketSender();
        final ItemsKeptOnDeathGenerator generator = new ItemsKeptOnDeathGenerator(player, false);

        final int maxItemsKept = generator.getMaxItemsKept();
        final Result result = generator.generate();
        final List<Item> keptItems = Stream.concat(result.getKeep().stream(), result.getBroken().stream()).collect(Collectors.toList());
        final List<Item> droppedItems = result.getDropped();

        // Info text
        if (player.getSkullTimer() > 0 && PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM)) {
            packetSender.sendString(18410, "You're marked with a @red@PK skull<col=ff981f>.");
            packetSender.sendString(18411, "This reduces the items you");
            packetSender.sendString(18412, "keep from three to zero!");

            packetSender.sendString(18413, "However, you also have the");
            packetSender.sendString(18414, "@red@Protect Items<col=ff981f> prayer active,");
            packetSender.sendString(18415, "which saves you one extra");
            packetSender.sendString(18416, "item!");
        } else if (player.getSkullTimer() > 0) {
            packetSender.sendString(18410, "You're marked with a @red@PK skull<col=ff981f>.");
            packetSender.sendString(18411, "This reduces the items you");
            packetSender.sendString(18412, "keep from three to zero!");

            packetSender.sendString(18413, "");
            packetSender.sendString(18414, "");
            packetSender.sendString(18415, "");
            packetSender.sendString(18416, "");
        } else if (PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM)) {
            packetSender.sendString(18410, "You have the @red@Protect Items");
            packetSender.sendString(18411, "prayer active, which saves");
            packetSender.sendString(18412, "you one extra item!");

            packetSender.sendString(18413, "");
            packetSender.sendString(18414, "");
            packetSender.sendString(18415, "");
            packetSender.sendString(18416, "");
        } else {
            packetSender.sendString(18410, "You have no factors affecting");
            packetSender.sendString(18411, "the items you keep.");
            packetSender.sendString(18412, "");

            packetSender.sendString(18413, "");
            packetSender.sendString(18414, "");
            packetSender.sendString(18415, "");
            packetSender.sendString(18416, "");
        }
        
        packetSender.sendString(17104, "Items you will keep on death" + (player.getSkullTimer() > 0 ? ":" : " if not skulled:"));
        packetSender.sendString(17105, "Items you will lose on death" + (player.getSkullTimer() > 0 ? ":" : " if not skulled:"));
        packetSender.sendString(17107, " ~ " + String.valueOf(maxItemsKept) + " ~");

        updateKeptItems(packetSender, keptItems);

        long value = updateLostItems(packetSender, droppedItems);
        if (value > Integer.MAX_VALUE)
            packetSender.sendString(18407, "Too high!");
        else
            packetSender.sendString(18407, NumberFormat.getIntegerInstance().format(value) + " gp");
    }

    private static long updateLostItems(PacketSender packetSender, List<Item> droppedItems) {

        final ItemContainer loseContainer = Misc.convertToItemContainer(droppedItems, StackType.DEFAULT);

        packetSender.sendItemContainer(loseContainer, 17116);

        long value = 0;

        for (Item item : loseContainer.getValidItems()) {
            if (item.getId() > 0 && item.getAmount() > 0) {
                int itemAmt = item.getAmount();
                long price = item.getValue(ItemValueType.PRICE_CHECKER);
                if (!ItemUtil.bypassPriceMultiplier(item.getId())) {
                    if (price < 50_000 && price > 100) {
                        price *= 3;
                    } else {
                        if (price != 1)
                            price *= 0.92;
                    }
                }
                value += (long) itemAmt * price;
            }
        }
        return value;
    }

    private static void updateKeptItems(PacketSender packetSender, List<Item> keptItems) {
        for (int i = 0; i < 8; i++) {
            if (i < keptItems.size()) {
                final Item itemKept = keptItems.get(i);
                packetSender.sendItemOnInterface(17108 + i, itemKept.getId(), 0, itemKept.getAmount());
            } else
                packetSender.clearItemOnInterface(17108 + i);
        }
    }

}
