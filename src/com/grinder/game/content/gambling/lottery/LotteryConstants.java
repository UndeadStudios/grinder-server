package com.grinder.game.content.gambling.lottery;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-27
 */
class LotteryConstants {

    static final double PRICE_REWARD_RATIO = 1/1000D;

    /**
     * The price per ticket
     */
    static final int PRICE = 1_000_000;

    static final int TICKET_VALUE = (int) (PRICE * 0.70D);

    /**
     * The maximum total lottery worth
     */
    static final int MAX_TOTAL_WORTH = 2_000_000_000;

    /**
     * The maximum number of tickets that can be put in the {@link LotteryPool}.
     */
    static final int MAX_TICKETS_IN_POOL = 10_000;

    /**
     * Duration of the broadcast when the lottery is drawn.
     */
    static final int BROADCAST_DURATION = 360;
}
