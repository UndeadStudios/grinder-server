package com.grinder.game.definition;

import com.grinder.game.model.Position;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a definition for a basic item-ground-spawn, such as
 * Lumbrdige under ground cabbage spawn..etc
 *
 */
public class ItemGroundDefinition {

    private final int id;
    private final int x, y, z;
    private final int amount;
    private final int respawnTimer;

    public ItemGroundDefinition(int id, int amount, int x, int y, int z, int respawnTimer) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
        this.respawnTimer = respawnTimer;
    }

    /**
     * Item on ground ID.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Item on ground amount.
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Item on ground position X.
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Item on ground position Y.
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Item on ground position Z.
     * @return z
     */
    public int getZ() {
        return z;
    }

    /**
     * Item on ground respawn timer after being picked.
     * @return respawnTimer
     */
    public int getRespawnTimer() {
        return respawnTimer;
    }

    public ItemGroundDefinition copy(int newId){
        ItemGroundDefinition copy = new ItemGroundDefinition(id, amount, x, y, z, respawnTimer);
        return copy;
    }
}
