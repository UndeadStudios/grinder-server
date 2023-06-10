package com.grinder.game.content.gambling;

import com.google.common.io.Files;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader;
import com.grinder.game.definition.loader.impl.ItemValueDefinitionLoader;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Utility class for handling the gamble tax reduction.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/01/2020
 */
public final class GambleTax {

    private static final String LOG_DIRECTORY = "TAX";

    //private static final double GAMBLE_TAX_RATIO = 0.975D;
    private static final double GAMBLE_TAX_RATIO = 0.9D;

    private static final int[] TAXABLE_ITEM_IDS = {ItemID.COINS, ItemID.BLOOD_MONEY, ItemID.PLATINUM_TOKEN};

    /**
     * Check whether an item is taxable.
     *
     * @param item the {@link Item} to check.
     *
     * @return {@code true} if {@link #TAXABLE_ITEM_IDS} contains {@link Item#getId()}.
     *          {@code false} otherwise.
     */
    public static boolean isTaxableItem(Item item){
        return IntStream.of(TAXABLE_ITEM_IDS).anyMatch(id -> item.getId() == id);
    }

    /**
     * Multiply the amount of each taxable item in the winnings
     * by the specified {@link #GAMBLE_TAX_RATIO} and set as the new amount.
     *
     * This method logs the applied taxes to a file in the {@link #LOG_DIRECTORY}.
     *
     * @param winner the {@link Player} receiving the winnings.
     * @param loser the {@link Player} losing the winnings.
     * @param winnings the items gambled by the loser.
     */
    public static void applyTaxIfApplicable(Player winner, Player loser, List<Item> winnings) {

        final StringBuilder taxesBuilder = new StringBuilder();

        if (GameConstants.BONUS_DOUBLE_EXP_WEEKEND) {
            winner.sendMessage("@red@Enjoy a full tax rate reduction every weekend while gambling and staking!");
            return;
        }

        winnings.forEach(wonItem -> {
            if (isTaxableItem(wonItem)) {
                final int oldAmount = wonItem.getAmount();
                final int newAmount = Math.toIntExact(Math.round(oldAmount * GAMBLE_TAX_RATIO));
                wonItem.setAmount(newAmount);
                taxesBuilder.append("Tax{").append(wonItem.getId()).append(", ").append(newAmount - oldAmount).append("}").append(',');
            }
        });

        final String taxesString = taxesBuilder.toString();

        if(!taxesString.isEmpty()) {
            if (!loser.getGameMode().isSpawn() && !winner.getGameMode().isSpawn())
            Logging.log(LOG_DIRECTORY,
                    winner.getUsername() + " won gamble from " + loser.getUsername()
                            + ": TAX_RATIO = "+ GAMBLE_TAX_RATIO
                            + ", taxes -> "+taxesString.substring(0, taxesString.length()-1));

            winner.sendMessage("@red@A 10% tax reduction has been applied to some of your items.");
        }
    }

    public static void main(String[] args) throws Throwable {
        new ItemDefinitionLoader().load();
        new ItemValueDefinitionLoader().load();

        long totalValueReduced = 0L;

        for(File file: Objects.requireNonNull(Paths.get("/Users/stanbend/Downloads/TAX").toFile().listFiles())){

            Map<Integer, Long> itemTaxes = new HashMap<>();

            for(String line : Files.readLines(file, Charset.defaultCharset())){

                Pattern logEntry = Pattern.compile("\\{(.*?)\\}");
                Matcher matchPattern = logEntry.matcher(line);

                while(matchPattern.find()) {
                    final String[] split = matchPattern.group(1).split(",");
                    final int id = Integer.parseInt(split[0].trim());
                    final long amount = Long.parseLong(split[1].trim());
                    itemTaxes.putIfAbsent(id, 0L);
                    itemTaxes.put(id, itemTaxes.get(id)+amount);
                    totalValueReduced += (amount * ItemValueDefinition.Companion.getValue(id, ItemValueType.PRICE_CHECKER));
                }
            }

            System.out.println("Taxes for "+file.getName()+":");
            itemTaxes.forEach((id, amount) -> System.out.println("\t taxed["+ ItemDefinition.forId(id).getName() +", "+ NumberFormat.getIntegerInstance().format( amount)+"]"));
        }

        System.out.println("Players got taxed for a total value of "+NumberFormat.getIntegerInstance().format(totalValueReduced)+" (estimate)!");
    }
}
