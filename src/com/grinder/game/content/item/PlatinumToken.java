package com.grinder.game.content.item;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.util.Misc;
import com.grinder.util.PlayerFlagsLogger;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
public class PlatinumToken {

    private static final int COINS_ITEM_ID = 995;
    private static final int PLATINUM_TOKEN_ITEM_ID = 13204;
    private static final int FLAG_AMOUNT = 25_000_000;
    private static final int EXCHANGE_RATE = 1_000;

    public static void convertTokensToCoins(final Player player) {

        final int tokenCount = player.getInventory().getAmount(PLATINUM_TOKEN_ITEM_ID);
        final long coinsFromTokens = ((long) tokenCount * EXCHANGE_RATE);
        final int coinsInInventory = player.getInventory().getAmount(COINS_ITEM_ID);
        final int maxCoinsToAdd = Integer.MAX_VALUE - coinsInInventory;

        if (player.getInventory().countFreeSlots() < 1) {
            player.sendMessage("You do not have enough free inventory slots.");
            return;
        }

        if (tokenCount < 1) {
            player.sendMessage("You need at least one Platinum token to exchange for coins.");
            return;
        }

        if (tokenCount >= FLAG_AMOUNT)
            PlayerFlagsLogger.flag(player.getUsername());

        if (maxCoinsToAdd == 0) {
            player.sendMessage("Your inventory can't carry anymore coins as you have reached the max stack.");
            return;
        }

        if (coinsFromTokens > maxCoinsToAdd) {

            final int tokensToRemove = maxCoinsToAdd / EXCHANGE_RATE;

            player.getInventory().delete(new Item(PLATINUM_TOKEN_ITEM_ID, tokensToRemove));
            player.getInventory().add(new Item(COINS_ITEM_ID, maxCoinsToAdd));

        } else {
            player.getInventory().delete(new Item(PLATINUM_TOKEN_ITEM_ID, tokenCount));
            player.getInventory().add(new Item(COINS_ITEM_ID, (int) coinsFromTokens));
            //player.sendMessage("You have exchanged @dre@" + Misc.format(tokenCount) + " Platinum tokens</col> for @dre@" + Misc.format((int) coinsFromTokens) + "</col> coins.");
        }
        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(1003, 250)
                .setText("The bank exchanges @dre@" + Misc.format(tokenCount) + " Platinum tokens</col> for", "@dre@" + Misc.format((int) coinsFromTokens) + "</col> coins.").start(player);

    }

    public static void convertCoinsToTokens(final Player player) {

        final int coins = player.getInventory().getAmount(COINS_ITEM_ID);
        final int tokensFromCoins = coins / EXCHANGE_RATE;
        final int leftOverCoins = coins % EXCHANGE_RATE;
        final int coinsToRemove = coins - leftOverCoins;
        final int tokens = player.getInventory().getAmount(PLATINUM_TOKEN_ITEM_ID);
        final int tokenCount = tokens + tokensFromCoins;

        if (player.getInventory().countFreeSlots() < 1) {
            player.sendMessage("You do not have enough free inventory slots.");
            return;
        }

        if (coins < EXCHANGE_RATE) {
            player.sendMessage("You need at least 1,000 coins to exchange for Platinum tokens.");
            return;
        }

        if (tokenCount >= FLAG_AMOUNT) {
            PlayerFlagsLogger.flag(player.getUsername());
            player.sendMessage("Your can't carry anymore Platinum tokens.");
            return;
        }

        player.getInventory().delete(new Item(COINS_ITEM_ID, coinsToRemove));
        player.getInventory().add(new Item(PLATINUM_TOKEN_ITEM_ID, tokensFromCoins));
        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(PLATINUM_TOKEN_ITEM_ID, 150)
                .setText("The bank exchanges @dre@" + Misc.format(coinsToRemove) + " coins</col> for", "@dre@" + Misc.format((int) tokensFromCoins) + "</col> Platinum tokens.").start(player);
    }

    public static ConversionResults convertCoins(long coins){
        final int tokensFromCoins = (int) (coins / EXCHANGE_RATE);
        final int leftOverCoins = (int) (coins % EXCHANGE_RATE);
        final int coinsToRemove = (int) (coins - leftOverCoins);
        return new ConversionResults(tokensFromCoins, leftOverCoins, coinsToRemove);
    }

    public static class ConversionResults {

        private final int tokensFromCoins;
        private final int leftOverCoins;
        private final int coinsToRemove;

        public ConversionResults(int tokensFromCoins, int leftOverCoins, int coinsToRemove) {
            this.tokensFromCoins = tokensFromCoins;
            this.leftOverCoins = leftOverCoins;
            this.coinsToRemove = coinsToRemove;
        }

        public int getTokensFromCoins() {
            return tokensFromCoins;
        }

        public int getLeftOverCoins() {
            return leftOverCoins;
        }

        public int getCoinsToRemove() {
            return coinsToRemove;
        }
    }
}
