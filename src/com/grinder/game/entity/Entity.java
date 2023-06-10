package com.grinder.game.entity;

import com.google.gson.annotations.Expose;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.entity.agent.player.event.impl.PositionChangedEvent;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionRepository;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeMap;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.util.math.Vector2i;
import com.grinder.util.timing.TimerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Entity {

    /**
     * The default position the entity is in.
     */
    @Expose private Position position;

    /**
     * The entity's unique index.
     */
    private int index;
    /**
     * The entity's tile size.
     */
    private int size = 1;

    private boolean cachedAreaDirty = true;
    private Area cachedArea;

    private final AttributeMap attributeMap = new AttributeMap();
    public AttributeMap getAttributes() {
        return attributeMap;
    }
    
    /**
     * The {@link TimerRepository} which manages all of the
     * timerRepository/delays for this {@link Entity}.
     */
    protected final TimerRepository timerRepository = new TimerRepository();

    /**
     * Get center point of this NPC based on it's size.
     *
     * @return half of NPC size, that represents it center;
     */
    public int getCenterPoint() {
        if(getSize() == 1)
            return 0;
        return (int) Math.floor(getSize() / 2.0);
    }
    public int getX() {
        return getPosition().getX() + getCenterPoint();
    }
    public int getY() {
        return getPosition().getY() + getCenterPoint();
    }
    public int getPlane() {
        return getPosition().getZ();
    }
    public Position getCenterPosition() {
        return new Position(getX(), getY(), getPlane());
    }

	public int executeProjectile(Projectile projectile) {
		if (projectile == null) {
			return 0;
		}
		Position source = projectile.getStart();
		Position target = projectile.getTarget();
		if (source == null || target == null) {
			return 0;
		}
		Position delta = projectile.getTarget().getDelta(source);

		int distance = source.getDistance(target);
		int duration = projectile.getDuration(distance);

		if (distance <= 60) {
			int creatorSize = projectile.getCreatorSize() == -1 ? getSize() : projectile.getCreatorSize();

            Projectile.sendGlobalProjectile(projectile, source, delta, duration, creatorSize);
        }
		return projectile.getHitDelay(distance);
	}

    /**
     * The Entity constructor.
     *
     * @param position The position the entity is currently in.
     */
    public Entity(Position position) {
        if(position == null){
            System.err.println("Attempted to set null position for "+this);
            position = GameConstants.DEFAULT_POSITION.clone();
            Thread.dumpStack();
        }
        this.position = position;
    }

 /*   public List<Position> getSurroundings() {
        List<Position> surroundings = new ArrayList<>();
        int absX = getPosition().getX();
        int absY = getPosition().getY();
        int size = getSize();

        if(size > 1)
        {
            Position center = getPosition();
            for(Direction direction : Direction.values()){
                Vector2i v = direction.getDirectionVector().mul(getCenterPoint());
                Position next = center.clone().move(v);
//                if(this instanceof NPC){
//                    getAsNpc().getMotion().translatePosition(next, direction);
//                }
                surroundings.add(next);
                //World.spawn(new TileGraphic(next, new Graphic(800)));
            }
            return surroundings;
        }
        for (int x = absX - 1; x <= absX + size; x++) {
            for (int y = absY - 1; y <= absY + size; y++) {
                if (x == absX - 1 || y == absY - 1 || y == absY + size || x == absX + size) {
                    surroundings.add(new Position(x, y, getPosition().getZ()));
                }
            }
        }
        return surroundings;
    }*/

  /*  public List<Position> getValidSurroundings() {

        List<Position> surrounding = getSurroundings();

        surrounding.removeIf(position -> CollisionManager.blocked(position) || !CollisionManager.canMove(position, getPosition(), 1, 1));

        return surrounding;
    }*/

    public Direction getDirection(Position position) {
        int otherX = position.getX();
        int otherY = position.getY();
        int x = getPosition().getX();
        int y = getPosition().getY();

        if (otherX < x && otherY < y) {
            return Direction.SOUTH_WEST;
        }
        if (otherX < x && otherY >= y + getSize()) {
            return Direction.NORTH_WEST;
        }
        if (otherX >= x + getSize() && otherY < y) {
            return Direction.SOUTH_EAST;
        }
        if (otherX >= x + getSize() && otherY >= y + getSize()) {
            return Direction.NORTH_EAST;
        }
        if (otherX < x) {
            return Direction.WEST;
        }
        if (otherX >= x + getSize()) {
            return Direction.EAST;
        }
        if (otherY < y) {
            return Direction.SOUTH;
        }
        if (otherY >= y + getSize()) {
            return Direction.NORTH;
        }
        return Direction.NONE;
    }

    public Direction getMinimalDirection(Position position) {
        int otherX = position.getX();
        int otherY = position.getY();
        int x = getPosition().getX();
        int y = getPosition().getY();

        if (otherX < x && otherY < y) {
            return Direction.SOUTH;
        }
        if (otherX < x && otherY >= y + getSize()) {
            return Direction.NORTH;
        }
        if (otherX >= x + getSize() && otherY < y) {
            return Direction.SOUTH;
        }
        if (otherX >= x + getSize() && otherY >= y + getSize()) {
            return Direction.NORTH;
        }
        if (otherX < x) {
            return Direction.WEST;
        }
        if (otherX >= x + getSize()) {
            return Direction.EAST;
        }
        if (otherY < y) {
            return Direction.SOUTH;
        }
        if (otherY >= y + getSize()) {
            return Direction.NORTH;
        }
        return Direction.NONE;
    }

    /**
     * Gets the entity's unique index.
     *
     * @return The entity's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns this mobs interacting index.
     *
     * @return The interaction index of this mob.
     */
    public int getInteractionIndex() {
        return index;
    }

    public int getLockIndex(){
        return this instanceof Player ? -index - 1 : index + 1;
    }

    /**
     * Sets the entity's index.
     *
     * @param index The value the entity's index will contain.
     * @return The Entity instance.
     */
    public Entity setIndex(int index) {
        this.index = index;
        return this;
    }

    /**
     * Gets the entity position.
     *
     * @return the entity's world position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the {@link Position} of this entity.
     *
     * Please do not use this method directly, unless for testing purposes,
     * use {@link com.grinder.game.entity.agent.Agent#moveTo(Position)} instead.
     *
     * @param position the position of this entity.
     */
    public Entity setPosition(Position position) {
        if (position == null) {
            position = GameConstants.DEFAULT_POSITION.clone();
            System.err.println("Attempted to reset null position for " + this);
            Thread.dumpStack();
        }
        if(!position.sameAs(this.position)) {
            final Position old = this.position;
            final RegionRepository repository = World.getRegions();
            final Region current = repository.fromPosition(old);
            final Region next = repository.fromPosition(position);

            current.removeEntity(this, false);
            this.position = position;
            cachedAreaDirty = true;

            next.addEntity(this, false);

            if(this instanceof Player){
                final Player player = (Player) this;
                player.notify(PlayerEvents.MOVED);
                player.notify(new PositionChangedEvent(old, position));
            }
            if (this instanceof Agent)
                AreaManager.checkAreaChanged((Agent) this);

        } else {
            System.err.println("Attempted to set same position for "+this+" at position "+position.toString());
        }

        return this;
    }



    /**
     * Performs an animation.
     *
     * @param animation The animation to perform.
     */
    public void performAnimation(Animation animation) {

    }

    /**
     * Performs a graphic.
     *
     * @param graphic The graphic to perform.
     */
    public void performGraphic(Graphic graphic) {

    }

    /**
     * gets the entity's tile size.
     *
     * @return The size the entity occupies in the world.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the entity's tile size
     *
     * @return The Entity instance.
     */
    public Entity setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * gets the entity's tile width.
     *
     * @return The width the entity occupies in the world.
     */
    public int getWidth() {
        return getSize();
    }

    /**
     * gets the entity's tile height.
     *
     * @return The height the entity occupies in the world.
     */
    public int getHeight() {
        return getSize();
    }

    public boolean isNpc() {
        return this instanceof NPC;
    }

    public boolean isPlayer() {
        return this instanceof Player;
    }

    public boolean isItemOnGround() {
        return this instanceof ItemOnGround;
    }

    public boolean isGameObject() {
        return this instanceof GameObject;
    }
    
    public Optional<Player> getAsOptionalPlayer() {
    	if (!(this instanceof Player)) {
    		return Optional.empty();
    	}
    	
        return Optional.of((Player) this);
    }

    public Player getAsPlayer() {
        return ((Player) this);
    }

    public NPC getAsNpc() {
        return ((NPC) this);
    }

    public ItemOnGround getAsItem() {
        return ((ItemOnGround) this);
    }

    public GameObject getAsGameObject() { return (GameObject)this; }

	public TimerRepository getTimerRepository() {
		return timerRepository;
	}

    /**
     * Gets the {@link EntityType} of this Entity.
     *
     * @return The EntityType.
     */
    public abstract EntityType getEntityType();

    public boolean isCachedAreaDirty() {
        return cachedAreaDirty;
    }

    public void setCachedAreaDirty(boolean cachedAreaDirty) {
        this.cachedAreaDirty = cachedAreaDirty;
    }

    public Area getCachedArea() {
        return cachedArea;
    }

    public void setCachedArea(Area cachedArea) {
        this.cachedArea = cachedArea;
    }

    public final boolean isUnder(Position other, int width, int height) {
        return isUnder(other.getX(), other.getY(), width, height);
    }

    public final boolean isUnder(int x, int y, int width, int height) {
        return position.getX() < x + width && position.getX() + getWidth() > x &&
                position.getY() < y + height && position.getY() + getHeight() > y;
    }

    public boolean isUnder(Entity other) {
        return isUnder(other.getPosition(), other.getWidth(), other.getHeight());
    }

    /**
     * Position within distance check taking into account entity size
     */
    public final boolean isWithinDistance(Entity other, int distance) {
        return getNearest(other.getPosition()).isWithinDistance(other.getNearest(getPosition()), distance);
    }

    /**
     * Position within distance check taking into account entity size
     */
    public final boolean isWithinDistance(Position other, int distance) {
        return getNearest(other).isWithinDistance(other, distance);
    }

    /**
     * @return the [Position] [entity] is standing over which is nearest to [target]
     */
    public final Position getNearest(Position target) {
        return getNearest(getPosition(), getWidth(), getHeight(), target);
    }

    /**
     * @return the [Position] [entity] is standing over which is nearest to [target]
     */
    public static Position getNearest(Position source, int width, int height, Position target) {
        Position position = source.copy();
        position.setX(getNearest(position.getX(), width, target.getX()));
        position.setY(getNearest(position.getY(), height, target.getY()));
        return position;
    }

    /**
     * @return the coordinate the entity at [source] with [size] is standing over which is nearest to [target]
     */
    private static int getNearest(int source, int size, int target) {
        int max = source + size - 1;
        if (target > max) {
            return max;
        } else {
            return Math.max(target, source);
        }
    }

    public final boolean inPestControl() {
        return (getPosition().getX() >= 2623 && getPosition().getX() <= 2689 && getPosition().getY() >= 2555 && getPosition().getY() <= 2628);
    }
}
