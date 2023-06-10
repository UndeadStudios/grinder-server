package com.grinder.game.entity.object;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.Expose;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.movement.pathfinding.target.*;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.update.GroupableEntity;
import com.grinder.game.model.area.update.ObjectUpdateOperation;
import com.grinder.game.model.passages.PassageCategory;
import com.grinder.game.model.passages.PassageManager;
import com.grinder.game.model.passages.PassageMode;
import com.grinder.util.Misc;

import java.util.Optional;

import static com.grinder.util.ObjectID.HOPPER_27;

/**
 * This file manages a game object entity on the globe.
 *
 * @author Relex lawl / iRageQuit2012   (ruse?)
 * @author Chris Fletcher               (apollo)
 * @author Major                        (apollo)
 * @author Stan van der Bend            (grinder-scape)
 */
public abstract class GameObject extends Entity implements GroupableEntity {

    /**
     * The packed value that stores this object's id, type, and orientation.
     */
    @Expose
    private final int packed;

    @Expose
    private final int id;
    /**
     * The {@link Player} which this {@link GameObject}
     * was spawned for.
     */
    private Player spawnedFor = null;

    /**
     * Strategy to check if within interaction range of this entity
     */
    public TargetStrategy interactTarget;

    private boolean isActive = true;

    /**
     * Creates the GameObject
     *
     * @param id          The id of the GameObject
     * @param position    The {@link Position} of the GameObject
     * @param type        The type of the GameObject
     * @param orientation The orientation of the GameObject
     */
    protected GameObject(int id, Position position, int type, int orientation) {
        super(position);
        packed = id << 8 | (type & 0x3F) << 2 | orientation & 0x3;
        this.id = id;
        setTargetStrategy();
    }

    protected GameObject(int packed, Position position) {
        super(position);
        this.packed = packed;
        this.id = packed >>> 8;
        setTargetStrategy();
    }

    private void setTargetStrategy() {
        if (id == -1)
            return;
        int type = getObjectType();
        if ((type >= 0 && type <= 2) || type == 9) {
            interactTarget = new WallTargetStrategy(this);
        } else if ((type >= 3 && type <= 8)) {
            interactTarget = new DecorationTargetStrategy(this);
        } else if (type == 10 || type == 11 || type == 22) {
            int flag = getDefinition().surroundings;
            int rotation = getFace();
            if (rotation != 0) {
                flag = (flag << rotation & 0xf) + (flag >> 4 - rotation);
            }
            interactTarget = new RectangleTargetStrategy(this, flag);
        } else {
            if (PassageManager.isForcePassage(this)) {
                interactTarget = new PositionTargetStrategy(getPosition(), 1, 1);
            }
            interactTarget = new EntityTileTargetStrategy(this);
        }
        if (id == HOPPER_27) {
            interactTarget = new DistanceTargetStrategy(this, 1);
        }
        PassageManager.findOrPredict(this).ifPresent(passage -> {
            if (passage.getMode() == PassageMode.FORCE && (passage.getCategory() == PassageCategory.DOOR || passage.getCategory() == PassageCategory.GATE)) {
                interactTarget = new PointTargetStrategy(this);
            }
        });
    }

    public int getId() {
        return id; //packed >>> 8;
    }

    public int getObjectType() {
        return packed >> 2 & 0x3F;
    }

    public int getFace() {
        return packed & 0x3;
    }

    public ObjectDefinition getDefinition() {
        return ObjectDefinition.forId(getId());
    }

    public void setSpawnedFor(Player player) {
        this.spawnedFor = player;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Gets the player this object was spawned for.
     */
    public Optional<Player> getSpawnedFor() {
        return Optional.ofNullable(spawnedFor);
    }

    @Override
    public void performAnimation(Animation animation) {
        PlayerUtil.playerStreamInDistanceOf(getPosition())
                .forEach(player -> player.getPacketSender().sendObjectAnimation(this, animation));
    }

    @Override
    public void performGraphic(Graphic graphic) {
        PlayerUtil.playerStreamInDistanceOf(getPosition())
                .forEach(player -> player.getPacketSender().sendGraphic(graphic, getPosition()));
    }

    @Override
    public int getSize() {
        ObjectDefinition definition = getDefinition();
        if (definition == null)
            return 1;
        return (definition.getSizeX() + definition.getSizeY()) - 1;
    }

    @Override
    public int getWidth() {
        ObjectDefinition definition = getDefinition();
        if (definition == null)
            return 1;
        return (getFace() & 0x1) == 1 ? definition.getSizeY() : definition.getSizeX();
    }

    @Override
    public int getHeight() {
        ObjectDefinition definition = getDefinition();
        if (definition == null)
            return 1;
        return (getFace() & 0x1) == 1 ? definition.getSizeX() : definition.getSizeY();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameObject))
            return false;
        GameObject object = (GameObject) o;
        if (getSpawnedFor().isPresent()) {
            if (object.getSpawnedFor().isEmpty()) {
                return false;
            }
            if (!getSpawnedFor().get().equals(object.getSpawnedFor().get())) {
                return false;
            }
        }
        return object.getPosition().equals(getPosition())
                && object.getId() == getId()
                && object.getFace() == getFace()
                && object.getObjectType() == getObjectType();
    }

    @Override
    public int hashCode() {
        return packed;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("position", getPosition())
                .add("type", getObjectType())
                .add("orientation", getFace()).toString();
    }

    @Override
    public GameObject clone() {
        return StaticGameObjectFactory.produce(getId(),
                getPosition().clone(),
                getObjectType(),
                getFace());
    }

    public Direction getFacing() {
        return Direction.valueOf(getFace());
    }

    public void setId(int id) {
        System.err.println("Unimplemented setId method called for " + getId() + " at " + getPosition().compactString());
    }

    @Override
    public ObjectUpdateOperation toUpdateOperation(Region region, EntityUpdateType operation) {
        return new ObjectUpdateOperation(region, operation, this);
    }

    public boolean collide(GameObject other) {
        return Misc.objectTypesCollide(other.getObjectType(), getObjectType()) && getPosition().equals(other.getPosition());
    }

    /**
     * Returns whether or not this GameObject can be seen by the specified {@link Player}.
     *
     * @param player The Player.
     * @return {@code true} if the Player can see this GameObject, {@code false} if not.
     */
    public abstract boolean viewableBy(Player player);

    public int getRotatedWidth() {
        if (getDefinition() == null) {
            return 1;
        }
        return (getFace() & 0x1) == 0 ? getDefinition().getSizeX() : getDefinition().getSizeY();
    }

    public int getRotatedLength() {
        if (getDefinition() == null) {
            return 1;
        }
        return (getFace() & 0x1) == 0 ? getDefinition().getSizeY() : getDefinition().getSizeX();
    }
}
