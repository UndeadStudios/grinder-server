package com.grinder.game.content.minigame.blastfurnace;

import com.google.gson.annotations.Expose;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.ItemActions;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;
import kotlin.Pair;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 6:15 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceCoffer {

    static {
        ItemActions.INSTANCE.onItemOnObjectByItemId(ItemID.COINS, ObjectID.BLAST_FURNACE_COFFER, action -> {
            use(action.getPlayer());
            return true;
        });

        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.BLAST_FURNACE_COFFER}, action -> {
            use(action.getPlayer());
            return true;
        });
    }

    @Expose
    private int amountInCoffer;

    public void depositToCoffer(int amount) {
        amountInCoffer += amount;
    }

    public void withdrawFromCoffer(int amount) {
        amountInCoffer -= amount;
    }

    public static void use(Player player) {

        if (player.getBlastFurnace().getCoffer().getCoinsInCoffer() == 0) {
            if (getAmountOfCoinsInInventory(player) == 0) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                        .setItem(ItemID.COINS_4, 200)
                        .setText("The coffer is empty, and you have no coins to deposit.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.OPTION)
                    .addOptions(Arrays.asList(getDepositOption(), getCancelOption()))
                    .start(player);
        } else if (player.getBlastFurnace().getCoffer().getCoinsInCoffer() == Integer.MAX_VALUE) {
            new DialogueBuilder(DialogueType.OPTION)
                    .addOptions(Arrays.asList(getWithdrawOption(), getCancelOption()))
                    .start(player);
        } else {
            new DialogueBuilder(DialogueType.OPTION)
                    .addOptions(Arrays.asList(getDepositOption(), getWithdrawOption(), getCancelOption()))
                    .start(player);
        }
    }

    private static int getAmountOfCoinsInInventory(Player player) {
        return player.getInventory().getAmount(ItemID.COINS);
    }

    private static Pair<String, Consumer<Player>> getDepositOption() {
        return new Pair<>("Deposit coins.", player ->
        {
            var amountInInventory = getAmountOfCoinsInInventory(player);

            player.requestInput(Integer.class, "Deposit how much? (" + NumberFormat.getIntegerInstance().format(amountInInventory) + ")", amountToDeposit -> {

                if (amountToDeposit > amountInInventory) {
                    amountToDeposit = amountInInventory;
                }
                if (amountToDeposit + player.getBlastFurnace().getCoffer().getCoinsInCoffer() >= Integer.MAX_VALUE) {
                    new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                            .setItem(ItemID.COINS_4, 200)
                            .setText("The coffer cannot that much amount of money.")
                            .start(player);
                    amountToDeposit = 0;
                    return;
                }

                player.getBlastFurnace().getCoffer().depositToCoffer(amountToDeposit);
                player.getInventory().delete(ItemID.COINS, amountToDeposit, true);
                BlastFurnace.startOreNpcs(player);
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                        .setItem(ItemID.COINS_4, 200)
                        .setText("The coffer now contains " + NumberFormat.getIntegerInstance().format(player.getBlastFurnace().getCoffer().getCoinsInCoffer()) + " coins.")
                        .start(player);

            });
        });
    }

    private static Pair<String, Consumer<Player>> getWithdrawOption() {

        return new Pair<>("Withdraw coins.", player -> {
            var amountInCoffer = player.getBlastFurnace().getCoffer().getCoinsInCoffer();
            player.requestInput(Integer.class, "Withdraw how much? (" + NumberFormat.getIntegerInstance().format(amountInCoffer) + ")", amountToWithdraw -> {
                if (amountToWithdraw > amountInCoffer) {
                    amountToWithdraw = amountInCoffer;
                }
                player.getBlastFurnace().getCoffer().withdrawFromCoffer(amountToWithdraw);
                player.getInventory().add(new Item(ItemID.COINS, amountToWithdraw), true);
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                        .setItem(ItemID.COINS_4, 200)
                        .setText("You withdraw " + NumberFormat.getIntegerInstance().format(amountToWithdraw) + " coins.")
                        .start(player);
            });
        });
    }

    private static Pair<String, Consumer<Player>> getCancelOption() {
        return new Pair<>("Cancel", player -> DialogueManager.start(player, -1));
    }

    public int getCoinsInCoffer() {
        return amountInCoffer;
    }
}
