package com.grinder.game.definition;

import com.grinder.game.model.Position;

/**
 * Represents a definition for a basic entity-spawn, such as
 * for an npc or object.
 *
 * @author Professor Oak
 */
public class DefaultSpawnDefinition {

    protected int id;
    protected Position position;

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }
}
