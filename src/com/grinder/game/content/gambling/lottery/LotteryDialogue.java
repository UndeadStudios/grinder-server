package com.grinder.game.content.gambling.lottery;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.impl.EnterLotterySyntax;
import com.grinder.util.NpcID;

import java.text.NumberFormat;
import java.util.function.Consumer;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 3-7-19
 */
class LotteryDialogue implements Consumer<Player> {

    private final LotteryPool pool;

    LotteryDialogue(final LotteryPool pool){
        this.pool = pool;
    }

    @Override
    public void accept(Player player) {
        /*new DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setExpression(DialogueExpression.CALM)
                .setNpcChatHead(NpcIdentifiers.GAMER)
                .setText(
                        "The lottery is temporarily closed for maintenance.", "Please check again later."
                ).start(player);*/
        if(pool == null){
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setExpression(DialogueExpression.CALM)
                    .setNpcChatHead(NpcID.GAMER)
                    .setText(
                            "There is currently no active lottery in which",
                            "you can participate. Please come back again,",
                            "later."
                    )
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("I'll be back later then!")
                    .start(player);
            return;
        }

        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                .setExpression(DialogueExpression.CALM)
                .setNpcChatHead(NpcID.GAMER)
                .setText(
                        "Join the lottery! "+format(LotteryConstants.PRICE)+" coins per ticket!",
                        "The lottery is at "+format(pool.getPoolValue())+" coins.",
                        "Your odds of winning are: "+format(pool.calculateWinningOdds(player.getUsername()))+""
                )
                .add(DialogueType.OPTION)
                .firstOption("Purchase Tickets.", this::promptForTicketPurchase)
                .addCancel("Never mind!")
        .start(player);
    }

    static String format(long numberOfCoins){
        return NumberFormat.getInstance().format(numberOfCoins);
    }

    private void promptForTicketPurchase(final Player player){
        player.setEnterSyntax(new EnterLotterySyntax());
        player.getPacketSender().sendEnterAmountPrompt("How many tickets would you like to buy? (1 = "+format(LotteryConstants.PRICE)+" gp)");
    }
}
