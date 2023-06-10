package com.grinder.game.model.projectile;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.grinder.game.World;
import com.grinder.game.collision.CollisionPolicy;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.update.GroupableEntity;
import com.grinder.game.model.area.update.ProjectileUpdateOperation;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.time.TimeUtil;

/**
 * A graphic propelled through the air by some sort of spell, weapon, or other
 * miscellaneous force.
 *
 * @author lare96
 * @author Stan van der Bend
 */
public final class Projectile extends Entity implements GroupableEntity {

	/**
	 * The starting position of the projectile.
	 */
	private Position start;
	private Position target;
	private int creatorSize;
	private final int startDistanceOffset;

	/**
	 * The offset position of the projectile.
	 */
	private Position offset;

	/**
	 * The speed of the projectile.
	 */
	private final int speed;

	private int lifespan;

	/**
	 * The id of the projectile.
	 */
	private final int projectileId;

	/**
	 * The starting height of the projectile.
	 */
	private final int startHeight;

	/**
	 * The ending height of the projectile.
	 */
	private final int endHeight;

	/**
	 * The lock on value of the projectile.
	 */
	private int lockon;

	/**
	 * The delay of the projectile.
	 */
	private final int delay;

	/**
	 * The curve angle of the projectile.
	 */
	private final int curve;

	private Sound arrivalSound;
	private Sound departureSound;

	/**
	 * Create a new {@link Projectile}.
	 *
	 * @param start        the starting position of the projectile.
	 * @param end          the ending position of the projectile.
	 * @param lockon       the lock on value of the projectile.
	 * @param projectileId the id of the projectile.
	 * @param speed        the speed of the projectile.
	 * @param delay        the delay of the projectile.
	 * @param startHeight  the starting height of the projectile.
	 * @param endHeight    the ending height of the projectile.
	 * @param curve        the curve angle of the projectile.
	 */

	public Projectile(Position start, Position end, int lockon, int projectileId, int speed, int delay, int startHeight,
					  int endHeight, int curve, int creatorSize, int startDistanceOffset) {
		super(start.clone());

		if (creatorSize > 1) {
			getPosition().addX((creatorSize / 2));
			getPosition().addY((creatorSize / 2));
		}
		this.start = getPosition();
		this.target = end;
		this.offset = new Position((end.getX() - start.getX()), (end.getY() - start.getY()));
		this.creatorSize = creatorSize;
		this.startDistanceOffset = startDistanceOffset;
		this.lockon = lockon;
		this.projectileId = projectileId;
		this.delay = delay;
		this.speed = speed;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.curve = curve;

//		this.lifespan = getDuration(calculateTravelDistance());
	}

	public Projectile(Agent actor, Agent target, ProjectileTemplate template) {
		super(actor.getCenterPosition());
		focus(actor);
		lock(target);
		this.creatorSize = template.sourceSize();
		this.startDistanceOffset = template.sourceOffset();
		this.projectileId = template.projectileId();
		this.delay = template.delay();
		this.speed = template.lifetime();
		this.startHeight = template.startHeight();
		this.endHeight = template.endHeight();
		this.curve = template.curve();
		arrivalSound = template.arrivalSound().orElse(null);
		departureSound = template.departureSound().orElse(null);

//		this.lifespan = getDuration(calculateTravelDistance());
	}

	public Projectile(Position start, Agent target, ProjectileTemplate template) {
		super(start);
		this.start = start.clone();
		lock(target);
		this.creatorSize = template.sourceSize();
		this.startDistanceOffset = template.sourceOffset();
		this.projectileId = template.projectileId();
		this.delay = template.delay();
		this.speed = template.lifetime();
		this.startHeight = template.startHeight();
		this.endHeight = template.endHeight();
		this.curve = template.curve();
		arrivalSound = template.arrivalSound().orElse(null);
		departureSound = template.departureSound().orElse(null);

//		this.lifespan = getDuration(calculateTravelDistance());
	}

	public Projectile(Position start, Position end, ProjectileTemplate template) {
		super(start);
		this.creatorSize = template.sourceSize();
		this.startDistanceOffset = template.sourceOffset();
		this.projectileId = template.projectileId();
		this.delay = template.delay();
		this.speed = template.lifetime();
		this.startHeight = template.startHeight();
		this.endHeight = template.endHeight();
		this.curve = template.curve();
		this.offset = new Position((end.getX() - start.getX()), (end.getY() - start.getY()));
		this.start = start.clone();
		this.target = end.clone();
		arrivalSound = template.arrivalSound().orElse(null);
		departureSound = template.departureSound().orElse(null);

//		this.lifespan = getDuration(calculateTravelDistance());
	}

	public Projectile(Position start, Position end, int lockon, int projectileId, int speed, int delay, int startHeight,
					  int endHeight, int curve) {
		this(start, end, lockon, projectileId, speed, delay, startHeight, endHeight, curve, 1, 0);
	}

	public Projectile(Position start, Position end, int projectileId, int speed, int delay, int startHeight,
					  int endHeight, int curve) {
		this(start, end, 0, projectileId, speed, delay, startHeight, endHeight, curve, 1, 0);
	}

	/**
	 * Create a new {@link Projectile}.
	 *
	 * @param source       the entity that is firing this projectile.
	 * @param victim       the victim that this projectile is being fired at.
	 * @param projectileId the id of the projectile.
	 * @param speed        the speed of the projectile.
	 * @param delay        the delay of the projectile.
	 * @param startHeight  the starting height of the projectile.
	 * @param endHeight    the ending height of the projectile.
	 * @param curve        the curve angle of the projectile.
	 */
	public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight,
					  int endHeight, int curve) {
		this(source.getPosition(), victim.getPosition(),
				(victim.isPlayer() ? -victim.getIndex() - 1 : victim.getIndex() + 1), projectileId, speed, delay,
				startHeight, endHeight, curve, source.getSize(), 0);
	}

	public static void sendGlobalProjectile(Projectile projectile, Position source, Position delta, int duration, int creatorSize) {
		new Projectile(
				source,
				projectile.getTarget(),
				projectile.getLockon(),
				projectile.getProjectileID(),
				duration,
				projectile.getDelay(),
				projectile.getStartHeight(),
				projectile.getEndHeight(),
				projectile.getCurve(),
				creatorSize,
				projectile.startDistanceOffset
		).sendProjectile();
	}

	/**
	 * Sends one projectiles using the values set when the {@link Projectile}
	 * was constructed.
	 */
	public void sendProjectile() {
		World.spawn(this);

		lifespan = getDuration(calculateTravelDistance());
		if (departureSound != null) {
			playSound(getCenterPosition(), departureSound);
		}
	}

	private void playSound(Position position, Sound sound){
		if(sound != null) {
			AgentUtil.getPlayersInProximity(position, 1, Player.NORMAL_VIEW_DISTANCE, CollisionPolicy.NONE)
					.forEach(player -> player.playSound(sound));
		}
	}

	public Projectile onArrival(final Runnable consumer){

		final int timeTillArrival = calculateTimeTillArrival();
		final int ticksTillArrival = Math.max(1, TimeUtil.CLIENT_CYCLES.toGameCycles(timeTillArrival) - 1);
		TaskManager.submit(new Task(ticksTillArrival, false) {
			@Override
			protected void execute() {
				stop();
				consumer.run();
				if (arrivalSound != null) {
					final int delay = Integer.max(0, (int) (timeTillArrival - TimeUtil.GAME_CYCLES.toClientCycles(ticksTillArrival)));
					arrivalSound.setDelay(delay);
					playSound(getTarget(), arrivalSound);
				}
			}
		});
		return this;
	}

	public int getDuration(int distance) {
		if (distance > 0) {
			return delay + speed + distance * 5;
		}
		return 0;
	}
	public int calculateTimeTillArrival() {
		return calculateTimeTillArrival(calculateTravelDistance());
	}

	/**
	 * Calculates the amount of time it will take for the projectile to arrive at its {@link Projectile#target}.
	 *
	 * @param distanceToTravel the distance between the {@link Projectile#start} and {@link Projectile#target}.
	 *
	 * @return a time delay in {@link TimeUtil#CLIENT_CYCLES};
	 */
	private int calculateTimeTillArrival(int distanceToTravel) {
		return (int) Math.floor(calculateTrajectoryDuration(distanceToTravel));
	}

	/**
	 * Calculates the duration of this {@link Projectile}'s trajectory.
	 *
	 * @param distanceToTravel the distance to travel.
	 *
	 * @return a time duration in {@link TimeUtil#CLIENT_CYCLES};
	 */
	private int calculateTrajectoryDuration(int distanceToTravel) {

		final int multiplier;
		if(distanceToTravel > 5)
			multiplier = 4;
		else if(distanceToTravel > 3)
			multiplier = 5;
		else if(distanceToTravel > 2)
			multiplier = 6;
		else
			multiplier = 7;

		if (distanceToTravel > 0)
			return (speed + 85) + delay + distanceToTravel * multiplier;

		return 0;
	}

	/**
	 * Calculates the distance between the {@link Projectile#start} and {@link Projectile#target}.
	 *
	 * @return distance measured in tiles.
	 */
	private int calculateTravelDistance(){
		return start.getDistance(target);
	}


	public int getHitDelay(int distance) {
		return (int) Math.floor((getDuration(distance) * 0.02857D));
	}

	public Position getTarget() {
		return target;
	}

	public int getCreatorSize() {
		return creatorSize;
	}

	public int getProjectileID() {
		return projectileId;
	}

	public int getStartDistanceOffset() {
		return startDistanceOffset;
	}

	/**
	 * Gets the starting position of the projectile.
	 *
	 * @return the starting position of the projectile.
	 */
	public Position getStart() {
		return start;
	}

	/**
	 * Gets the offset position of the projectile.
	 *
	 * @return the offset position of the projectile.
	 */
	public Position getOffset() {
		return offset;
	}

	/**
	 * Gets the speed of the projectile.
	 *
	 * @return the speed of the projectile.
	 */
	public int getLifetime() {
		return speed;
	}

	public int getLifespan() {
		return lifespan;
	}
	/**
	 * Gets the id of the projectile.
	 *
	 * @return the id of the projectile.
	 */
	public int getProjectileId() {
		return projectileId;
	}

	/**
	 * Gets the starting height of the projectile.
	 *
	 * @return the starting height of the projectile.
	 */
	public int getStartHeight() {
		return startHeight;
	}

	/**
	 * Gets the ending height of the projectile.
	 *
	 * @return the ending height of the projectile
	 */
	public int getEndHeight() {
		return endHeight;
	}

	/**
	 * Gets the lock on value of the projectile.
	 *
	 * @return the lock on value of the projectile.
	 */
	public int getLockon() {
		return lockon;
	}

	/**
	 * Gets the delay of the projectile.
	 *
	 * @return the delay of the projectile.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the curve angle of the projectile.
	 *
	 * @return the curve angle of the projectile.
	 */
	public int getCurve() {
		return curve;
	}

	public void lock(Agent agent) {
		lockon = agent instanceof Player ? -agent.getIndex() - 1 : agent.getIndex() + 1;
		target = agent.getPosition().clone();
		offset = target.getDelta(start);
	}

	public void focus(Agent actor) {
		start = actor.getCenterPosition();
		creatorSize = actor.getSize();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Projectile) {
			Projectile other = (Projectile) obj;

			return start.equals(other.start)
					&& target.equals(other.target)
					&& delay == other.delay
					&& speed == other.speed
					&& target == other.target
					&& startHeight == other.startHeight
					&& endHeight == other.endHeight
					&& curve == other.curve
					&& offset == other.offset
					&& projectileId == other.projectileId;
		}

		return false;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.PROJECTILE;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(start, target, delay, speed, target, startHeight,
				endHeight, curve, offset, projectileId);
	}

	@Override
	public ProjectileUpdateOperation toUpdateOperation(Region region, EntityUpdateType type) {
		Preconditions.checkArgument(type == EntityUpdateType.ADD, "Projectiles cannot be removed from the client");

		return new ProjectileUpdateOperation(region, type, this);
	}
}
