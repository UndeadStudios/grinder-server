package com.grinder.game.definition.droptable;

import com.grinder.game.definition.droptable.impl.ImmutableDropTable;
import com.grinder.game.model.item.Item;
import com.grinder.util.random.Utils;
import kotlin.Pair;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DropTable extends TableSlot {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Random random;

    private final List<Pair<TableSlot, Integer>> table = new ArrayList<>(); // TableSlot, weight

    private int slots;

    public DropTable(Random random) {
        this.random = random;
    }

    public DropTable() {
        this(SECURE_RANDOM);
    }

    public int getSlots() {
        return slots;
    }

    public DropTable append(int id, int weight, int amount) {
        return append(id, weight, amount, amount);
    }

    public DropTable append(int id, int weight) {
        return append(id, weight, 1);
    }

    public DropTable append(int id) {
        return append(id, 1);
    }

    public DropTable append(int id, int weight, int minAmount, int maxAmount) {
        assert minAmount <= maxAmount;
        assert id >= 1;
        assert minAmount >= 1;

        appendSlot(new BasicSlot(id, minAmount, maxAmount), weight);

        return this;
    }

    public DropTable append(TableSlot table, int weight) {
        appendSlot(table, weight);
        return this;
    }

    public DropTable append(TableSlot table) {
        return append(table, 1);
    }

    protected void appendSlot(TableSlot slot, int weight) {
        slots += weight;
        table.add(new Pair<>(slot, weight));
    }

    public RollResult roll() {
        int randomSlot = Utils.random(random, 1, slots);
        int currentSlot = 0;

        for (Pair<TableSlot, Integer> it : table) {
            currentSlot += it.getSecond();

            if (currentSlot >= randomSlot) {
                return it.getFirst().evaluate(random);
            }
        }

        if (table.isEmpty()) {
            throw new InvalidRollException("Drop could not be generated: empty table");
        } else {
            throw new InvalidRollException("Drop could not be generated: rolled slot $randomSlot/$slots was not found");
        }
    }

    public RollResult[] roll(int numberOfTimes) {
        List<RollResult> results = new ArrayList<>();

        for (int i = 0; i < numberOfTimes; i++) {
            results.add(roll());
        }

        return results.toArray(new RollResult[0]);
    }

    public int rollInt() {
        return roll().getId();
    }

    public Item rollItem() {
        RollResult roll = roll();

        return new Item(roll.getId(), roll.getQuantity());
    }

    public ImmutableDropTable toImmutable() {
        ImmutableDropTable immutableTable = new ImmutableDropTable(random);

        for (Pair<TableSlot, Integer> it : table) {
            immutableTable.append(it.getFirst(), it.getSecond());
        }

        return immutableTable.build();
    }

    @Override
    public RollResult evaluate(Random random) {
        return roll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DropTable dropTable = (DropTable) o;
        return slots == dropTable.slots
                && Objects.equals(random, dropTable.random)
                && Objects.equals(table, dropTable.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(random, table, slots);
    }

}
