package com.grinder.game.model.passages;

import com.grinder.game.model.Position;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PassageMap {

    private final Map<String, Passage> lookupByName = new HashMap<>();
    private final Map<Long, Passage> lookupByPosition = new HashMap<>();

    public void add(Passage passage) {
        for (PassageState state : PassageState.values()) {
            addStatePosition(passage, passage.getPosition(state), state);
        }
        if (passage.getAttachment() != null) {
            for (PassageState state : PassageState.values()) {
                addStatePosition(passage, passage.getAttachment().getPosition(state), state);
            }
        }
        if (passage.getName() != null) {
            lookupByName.put(passage.getName(), passage);
        }
    }

    private void addStatePosition(Passage passage, Position position, PassageState state) {
        if (position == null) {
            return;
        }
        var uid = pack(position, state.ordinal());
        if (lookupByPosition.containsKey(uid)) {
            return;
        }
        lookupByPosition.put(uid, passage);
    }

    public Passage lookup(String name) {
        return lookupByName.get(name);
    }

    public Passage lookup(Position position) {
        for (int type = 0; type < 2; type++) {
            var passage = lookupByPosition.get(pack(position, type));
            if (passage != null) {
                return passage;
            }
        }
        return null;
    }

    public Collection<Passage> list() {
        return Collections.unmodifiableCollection(lookupByPosition.values());
    }

    private static long pack(Position position, int type) {
        return (long) type << 32L | position.pack();
    }
}
