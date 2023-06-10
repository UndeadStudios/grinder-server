package com.grinder.game.content.gambling.lottery;

import com.google.common.base.Preconditions;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-03
 */
public class LotteryPool {

    private final ArrayBlockingQueue<LotteryTicket> queue = new ArrayBlockingQueue<>(LotteryConstants.MAX_TICKETS_IN_POOL);

    private LotteryTicket winningTicket;

    LotteryPool(){
        this(new LotteryTicket[]{}, null);
    }

    LotteryPool(final LotteryTicket[] tickets, final LotteryTicket winningTicket){
        queue.addAll(Arrays.asList(tickets));
        this.winningTicket = winningTicket;
    }

    LotteryTicket getWinner(){
        return winningTicket;
    }

    Optional<LotteryTicket> findWinner(){
        return Optional.ofNullable(winningTicket);
    }

    void reset(){
        queue.clear();
        winningTicket = null;
    }

    boolean isClosed(){
        return winningTicket != null;
    }

    boolean isFull(){
        return getRemainingCapacity() == 0;
    }

    boolean drawWinner() {
        Preconditions.checkArgument(!isClosed(), "Lottery winner is already drawn.");
        final int luckyNumber = Misc.random(0, queue.size());
        int number = 0;
        for(final LotteryTicket ticket: queue){
            if(number == luckyNumber) {
                winningTicket = ticket;
                return true;
            }
            number++;
        }
        return false;
    }

    boolean offer(final LotteryTicket lotteryTicket){
        Preconditions.checkArgument(!isClosed(), "Lottery winner is already drawn.");
        return queue.offer(lotteryTicket);
    }

    boolean isWinner(final String username){
        return findWinner().filter(lotteryTicket -> lotteryTicket.getUsername().equalsIgnoreCase(username)).isPresent();
    }

    ArrayBlockingQueue<LotteryTicket> getQueue() {
        return queue;
    }

    int getRemainingCapacity(){
        return queue.remainingCapacity();
    }

    long getPoolValue(){
        return queue.stream().mapToLong(LotteryTicket::getAmount).sum();
    }

    int calculateWinningOdds(final String username){

        if(queue.isEmpty())
            return 0;

        final long ownedTicketsCount = queue.stream().filter(lotteryTicket -> lotteryTicket.getUsername().equalsIgnoreCase(username)).count();

        return (int) (ownedTicketsCount * 100.0 / queue.size());
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
