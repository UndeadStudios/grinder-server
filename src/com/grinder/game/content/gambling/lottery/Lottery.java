package com.grinder.game.content.gambling.lottery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.grinder.ServerIO;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.miscellaneous.Broadcast;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.task.impl.AnnouncementTask;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

import static com.grinder.game.content.gambling.lottery.LotteryConstants.BROADCAST_DURATION;
import static com.grinder.util.ItemID.PLATINUM_TOKEN;


/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-27
 */
public final class Lottery {

    public final static boolean NEW_LOTTERY = true;

    private final static Logger LOGGER = LogManager.getLogger(Lottery.class.getSimpleName());

    private static LotteryPool activePool;
    private static LotteryPool uncollectedPool;

    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadTickets(JsonObject jsonObject){

        if (jsonObject.has("lottery-collection")) {
            final LotteryTicket[] previousTickets = (GSON.fromJson(jsonObject.get("lottery-collection").getAsJsonArray(), LotteryTicket[].class));
            if(previousTickets.length > 0) {
                final LotteryTicket previousWinner = GSON.fromJson(jsonObject.get("lottery-collection-winner"), LotteryTicket.class);
                uncollectedPool = new LotteryPool(previousTickets, previousWinner);
                if (!uncollectedPool.isClosed()) {
                    LOGGER.info("Previous lottery was not yet closed, drawing a winner now.");
                    uncollectedPool.drawWinner();
                    uncollectedPool.findWinner().ifPresent(winningTicket -> LOGGER.info("Winner of previous Lottery is " + winningTicket.getUsername() + "!"));
                } else
                    LOGGER.info("Winner of previous lottery '" + previousWinner + "' still has uncollected funds! ");
            }
        } else
            uncollectedPool = new LotteryPool();

        if (jsonObject.has("lottery-tickets")) {
            activePool = new LotteryPool(GSON.fromJson(jsonObject.get("lottery-tickets").getAsJsonArray(), LotteryTicket[].class), null);
            LOGGER.info("Loaded lottery pool valued at "+ activePool.getPoolValue()+" coins. Remaining capacity = "+ activePool.getRemainingCapacity());
        } else {
            activePool = new LotteryPool();
            LOGGER.info("Created an empty lottery pool of capacity "+activePool.getRemainingCapacity()+".");
        }
    }

    public static void saveTickets(JsonObject object){
        if(uncollectedPool != null) {
            object.add("lottery-collection", GSON.toJsonTree(uncollectedPool.getQueue()));
            final LotteryTicket winner = uncollectedPool.getWinner();
            if (winner != null) {
                object.add("lottery-collection-winner", GSON.toJsonTree(winner));
            }
        }
        if(activePool != null && activePool.getQueue().size() > 0)
            object.add("lottery-tickets", GSON.toJsonTree(activePool.getQueue()));
    }

    public static void startGamblerDialogue(final Player player){
        new LotteryDialogue(activePool).accept(player);
    }

    public static void purchaseTickets(final Player player, int amount){

        if(player.getGameMode().isAnyIronman()){
            player.sendMessage("You can't use the lottery as an Ironman.");
            return;
        }
        if(player.getGameMode().isSpawn()){
            player.sendMessage("You can't use the lottery in spawn game mode.");
            return;
        }

        if(activePool.isFull()){
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("The lottery is currently full!!")
                    .start(player);
            return;
        }

        final int coinsInInventory = player.getInventory().getAmount(ItemID.COINS);

        amount = Math.min(amount, activePool.getRemainingCapacity());
        amount = (int) Math.min(amount, Math.floor(Integer.MAX_VALUE / (double) LotteryConstants.PRICE));

        if(coinsInInventory < LotteryConstants.PRICE * amount) {
            amount = coinsInInventory / LotteryConstants.PRICE;
        }

        if(amount <= 0){
            DialogueManager.sendStatement(player, "You don't have enough coins to purchase that much.");
            return;
        }

        int successfulOfferings = 0;
        while (amount > 0){
            if(activePool.offer(new LotteryTicket(player.getUsername(), LotteryConstants.TICKET_VALUE))){
                successfulOfferings++;
            }
            amount--;
        }
        amount = successfulOfferings;

        final int purchaseCost = LotteryConstants.PRICE * amount;
        player.getInventory().delete(new Item(ItemID.COINS, purchaseCost));
        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setExpression(DialogueExpression.HAPPY)
                .setNpcChatHead(NpcID.GAMER)
                .setText(
                        ""+amount+" tickets have been registered under your name!",
                        "May the odds serve you well."
                )
                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Cheers!")
                .start(player);

        player.sendMessage("You purchases "+amount+" lottery tickets for "+LotteryDialogue.format(purchaseCost)+" coins!");
        ServerIO.save();

        if(activePool.isFull()) {
            pickWinner();
        }
    }

    public static void checkUncollectedWinnings(final Player player){
        if(collectWinnings(player, uncollectedPool)){
            uncollectedPool.reset();
            ServerIO.save();
            LOGGER.debug("Uncollected pool winning was collected by '"+player.getUsername()+"'");
        }
    }

    private static boolean collectWinnings(final Player player, final LotteryPool pool){
        if(pool != null){
            if(pool.isWinner(player.getUsername())){
                if (player.getInventory().canHold(new Item(PLATINUM_TOKEN, (int) ((double) pool.getPoolValue() * LotteryConstants.PRICE_REWARD_RATIO)))) {
                    player.getInventory().add(new Item(PLATINUM_TOKEN, (int) ((double) pool.getPoolValue() * LotteryConstants.PRICE_REWARD_RATIO)));
                } else {
                    BankUtil.addToBank(player, new Item(PLATINUM_TOKEN, (int) ((double) pool.getPoolValue() * LotteryConstants.PRICE_REWARD_RATIO)));
                }
                AchievementManager.processFor(AchievementType.LUCKY_DRAW, player);
                player.sendMessage("<img=749>@red@ [Lottery]: You have won the lottery. The reward is transferred to your bank!");
                return true;
            }
        }
        return false;
    }

    public static void pickWinner(){

        try {

        AnnouncementTask.lotteryWinnerDeclared = true;

        if(activePool.isClosed()){
            LOGGER.info("Could not declare winner, lottery already has a winner '"+activePool.getWinner().getUsername()+"!");
            return;
        }

        if(activePool.isEmpty()){
            LOGGER.info("Could not declare winner yet, lottery is empty!");
            return;
        }

        activePool.drawWinner();

        final LotteryTicket winner = activePool.getWinner();

        if (winner == null) {
            LOGGER.info("Could not declare winner yet, winner is null!");
            return;
        }

        String amount = LotteryDialogue.format((long) (activePool.getPoolValue() * LotteryConstants.PRICE_REWARD_RATIO / 1000));
        String message = "[Lottery] Congratulations! " + winner.getUsername() + " won a whopping " + amount + "m from the lottery!";

        Broadcast.broadcast(null, BROADCAST_DURATION, message, "");

        final Optional<Player> onlinePlayer = World.findPlayerByName(winner.getUsername());

        if(onlinePlayer.isPresent())
            collectWinnings(onlinePlayer.get(), activePool);
        else
            uncollectedPool = activePool;

        activePool = new LotteryPool();
        ServerIO.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
