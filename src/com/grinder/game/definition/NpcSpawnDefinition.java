package com.grinder.game.definition;

import com.grinder.game.model.FacingDirection;

public class NpcSpawnDefinition extends DefaultSpawnDefinition {

    public NpcSpawnDefinition copy(int newId){
        NpcSpawnDefinition copy = new NpcSpawnDefinition();
        copy.facing = facing;
        copy.radius = radius;
        copy.id = newId;
        copy.position = position;
        return copy;
    }

    private FacingDirection facing;
    private int radius;

    public FacingDirection getFacing() {
        return facing;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NpcSpawnDefinition))
            return false;
        NpcSpawnDefinition def = (NpcSpawnDefinition) o;
        return def.getPosition().equals(getPosition())
                && def.getId() == getId()
                && def.getFacing() == getFacing()
                && def.getRadius() == getRadius();
    }
}
