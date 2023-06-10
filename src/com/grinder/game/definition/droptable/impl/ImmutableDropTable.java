package com.grinder.game.definition.droptable.impl;

import com.grinder.game.definition.droptable.DropTable;
import com.grinder.game.definition.droptable.TableSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ImmutableDropTable extends DropTable {

    private boolean built;

    public ImmutableDropTable(Random random) {
        super(random);
    }

    public ImmutableDropTable() {
        super();
    }

    @Override
    protected void appendSlot(@NotNull TableSlot slot, int weight) {
        if (built) {
            return;
        }
        super.appendSlot(slot, weight);
    }

    public ImmutableDropTable build() {
        built = true;
        return this;
    }

}
