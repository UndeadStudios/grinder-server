package com.grinder.game.content.gambling;

import com.grinder.game.content.trading.TradeConstants;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.StackType;
import com.grinder.util.TextUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

/**
 * Represents an {@link ItemContainer} that holds the gambled items of a particular {@link Player}
 * which is managed by the {@link Player#getGambling() gamble controller}.
 */
public final class GambleContainer extends ItemContainer {

    /**
     * Create a new {@link GambleContainer}.
     *
     * @param player the {@link Player} who owns the items in this container.
     */
    public GambleContainer(Player player){
        super(player);
    }

    @Override
    public StackType stackType() {
        return StackType.DEFAULT;
    }

    @Override
    public ItemContainer full() {
        player.getPacketSender().sendMessage("You can't gamble more items.", 1000);
        return this;
    }

    @Override
    public int capacity() {
        return 14;
    }

    @Override
    public ItemContainer refreshItems() {

        String playerValue = ItemContainerUtil.readValueOfContents(this);

        if (TextUtil.isInteger(playerValue))
            playerValue = NumberFormat.getIntegerInstance().format(Long.parseLong(playerValue));

        final Player other = player.getGambling().getOther();

        String otherValue = ItemContainerUtil.readValueOfContents(other.getGambling().getContainer());
        if (TextUtil.isInteger(otherValue))
            otherValue = NumberFormat.getIntegerInstance().format(Long.parseLong(otherValue));

        player.getPacketSender().sendInterfaceSet(GambleConstants.INTERFACE_ID, TradeConstants.CONTAINER_INVENTORY_INTERFACE);
        player.getPacketSender().sendItemContainer(player.getInventory(), TradeConstants.INVENTORY_CONTAINER_INTERFACE);

        player.getPacketSender().sendInterfaceItems(GambleConstants.FIRST_ITEM_CONTAINER_ID, getValidItems());
        player.getPacketSender().sendInterfaceItems(GambleConstants.SECOND_ITEM_CONTAINER_ID, other.getGambling().getContainer().getValidItems());
        player.getPacketSender().sendString(GambleConstants.POT_STRING_ID, "You: (@whi@" + playerValue + "</col>)");
        player.getPacketSender().sendString(GambleConstants.OTHER_POT_STRING_ID, "Other: (@whi@" + otherValue + "</col>)");

        other.getPacketSender().sendInterfaceItems(GambleConstants.FIRST_ITEM_CONTAINER_ID, other.getGambling().getContainer().getValidItems());
        other.getPacketSender().sendInterfaceItems(GambleConstants.SECOND_ITEM_CONTAINER_ID, getValidItems());
        other.getPacketSender().sendString(GambleConstants.POT_STRING_ID, "You: (@whi@" + otherValue + "</col>)");
        other.getPacketSender().sendString(GambleConstants.OTHER_POT_STRING_ID, "Other: (@whi@" + playerValue + "</col>)");

        final ItemContainer rightContainer = other.getGambling().getContainer();
        final List<Item> otherTaxableItems = rightContainer.getValidItems().stream().filter(GambleTax::isTaxableItem).collect(Collectors.toList());
        final int[] otherTaxableIds = new int[3];
        otherTaxableItems.forEach(item -> {
            int index;
            if (item.getId() == COINS) index = 0;
            else if (item.getId() == BLOOD_MONEY) index = 1;
            else index = 2;
            otherTaxableIds[index] = item.getId();
        });
        final ItemContainer leftContainer = this;
        final List<Item> playerTaxableItems = leftContainer.getValidItems().stream().filter(GambleTax::isTaxableItem).collect(Collectors.toList());
        final int[] playerTaxableIds = new int[3];
        playerTaxableItems.forEach(item -> {
            int index;
            if (item.getId() == COINS) index = 0;
            else if (item.getId() == BLOOD_MONEY) index = 1;
            else index = 2;
            playerTaxableIds[index] = item.getId();
        });

        final String taxText = "Taxed:";
        if (!playerTaxableItems.isEmpty()) {
            player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, taxText);
            other.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_RIGHT_CHILD_ID, taxText);
        } else {
            player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, "");
            other.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_RIGHT_CHILD_ID, "");
        }
        for (int i = 0; i < playerTaxableIds.length; i++) {
            final int id = playerTaxableIds[i] == 0 ? -1 : playerTaxableIds[i];
            final int leftId = GambleConstants.LEFT_TAXED_ITEM_SLOTS[i];
            final int rightId = GambleConstants.RIGHT_TAXED_ITEM_SLOTS[i];
            player.getPacketSender().sendInterfaceModel(leftId, id, 70);
            other.getPacketSender().sendInterfaceModel(rightId, id, 70);
        }

        if (!otherTaxableItems.isEmpty()) {
            other.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, taxText);
            player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_RIGHT_CHILD_ID, taxText);
        } else {
            other.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, "");
            player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_RIGHT_CHILD_ID, "");
        }
        for (int i = 0; i < otherTaxableIds.length; i++) {
            final int id = otherTaxableIds[i] == 0 ? -1 : otherTaxableIds[i];
            final int leftId = GambleConstants.LEFT_TAXED_ITEM_SLOTS[i];
            final int rightId = GambleConstants.RIGHT_TAXED_ITEM_SLOTS[i];
            other.getPacketSender().sendInterfaceModel(leftId, id, 70);
            player.getPacketSender().sendInterfaceModel(rightId, id, 70);
        }
        return this;
    }

}
