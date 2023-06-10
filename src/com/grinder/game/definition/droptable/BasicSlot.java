package com.grinder.game.definition.droptable;

import com.grinder.util.Misc;
import com.grinder.util.random.Utils;

import java.util.Objects;
import java.util.Random;

public final class BasicSlot extends TableSlot {
    private final int id;
    private final int minAmount;
    private final int maxAmount;

    public BasicSlot(int id, int minAmount, int maxAmount) {
        this.id = id;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public String toString() {
        return "BasicSlot{" +
                "id=" + id +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicSlot basicSlot = (BasicSlot) o;
        return id == basicSlot.id && minAmount == basicSlot.minAmount && maxAmount == basicSlot.maxAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, minAmount, maxAmount);
    }

    public int getId() {
        return id;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public RollResult evaluate(Random random) {
        if (minAmount == maxAmount) {
            return new RollResult(id, minAmount);
        }

        return new RollResult(id, Utils.random(random, minAmount, maxAmount));
    }

}
