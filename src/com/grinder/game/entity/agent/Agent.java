package com.grinder.game.entity.agent;

import com.grinder.game.content.item.degrading.DegradingType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.Skills;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.combat.Combat;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.movement.Motion;
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalStrategy;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.entity.updating.UpdateBlockSet;
import com.grinder.game.entity.updating.block.InteractingMobBlock;
import com.grinder.game.entity.updating.block.TurnToPositionBlock;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.InstancedArea;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;
import com.grinder.util.benchmark.Monitored;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.timing.Stopwatch;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An agent is a {@link Entity} that can act, operate autonomously, perceive its environment,
 * persist over a prolonged time period, adapt to change, and create and pursue goals.
 *
 * @author Swiffy
 * @author Stan van der Bend
 */
public abstract class Agent extends Entity implements Monitored {

	public InstancedArea instancedArea;

	private String forcedChat;

	private Position positionToFace;
	private Position lastPosition;
	public Position actualLastPosition = getPosition().clone().add(-1, 0);

	private Direction walkingDirection = Direction.NONE, runningDirection = Direction.NONE;
	private Direction lastFacingDirection = Direction.SOUTH;

	private int faceDegrees;
	private Animation animation;
	private Graphic graphic;
	private Entity interactingEntity;
	private int npcTransformationId = -1;
	private int poisonDamage;
	private int venomDamage;
	private boolean[] prayerActive = new boolean[30];
	private boolean[] curseActive = new boolean[20];
	private boolean untargetable;
	private int specialPercentage = 100;
	private boolean specialActivated;
	private boolean recoveringSpecialAttack;
	private TeleportType teleportingType = null;
	private Task teleportingTask = null;
	private boolean hide = false;
	public ForceMovement forceMovement;

	private final Stopwatch lastCombat = new Stopwatch();
	private final SecondsTimer vengeanceEffectTimer = new SecondsTimer();
	public Agent lastAgentHitBy;

	private Area area;
	private Damage primaryHit;
	private Damage secondaryHit;

	//private Boolean stallingDamage = false;

	/**
	 * Strategy to check if within interaction range of this entity
	 */
	public TargetStrategy interactTarget;
	public TraversalStrategy traversal;
	public boolean unpassable = false;

	/**
	 * This mob's set of synchronization blocks.
	 */
	protected UpdateBlockSet blockSet = new UpdateBlockSet();

	/**
	 * Is this entity registered.
	 */
	private boolean registered;

	private Position teleportPosition;

	private boolean pendingTeleportUpdate;

	private boolean inMulti;

    public void setInMulti(boolean inMulti) {
		this.inMulti = inMulti;
	}

	public boolean isInMulti() {
		return inMulti;
	}

	private boolean shouldNoClip;

	public void setShouldNoClip(boolean shouldNoClip) {
		this.shouldNoClip = shouldNoClip;
	}

	public boolean isShouldNoClip() {
		return shouldNoClip;
	}

	private MapInstance mapInstance;

	public void setMapInstance(MapInstance map) {
		this.mapInstance = map;
	}

	public MapInstance getMapInstance() {
		return mapInstance;
	}

	/**
	 * Create a new {@link Agent}.
	 *
	 * @param type		the {@link EntityType} of this agent.
	 * @param position	the {@link Position} to place this agent at.
	 */
	public Agent(EntityType type, Position position) {
		super(position);
	}

	/**
	 * An abstract method used for handling actions once this entity has been
	 * added to the world.
	 */
	public abstract void onAdd();

	/**
	 * An abstract method used for handling actions once this entity has been
	 * removed from the world.
	 */
	public abstract void onRemove();

	public abstract void updateAppearance();

	public abstract Motion<? extends Agent> getMotion();

	public abstract Combat<? extends Agent> getCombat();

	public abstract void debug(String message);

	public abstract int getHitpoints();

	public abstract int getMaxHitpoints();

	public abstract void heal(int damage);

	public abstract Agent setHitpoints(int hitpoints);

	public abstract Skills getSkills();

	public abstract void appendDeath();

	public abstract int getBaseAttackSpeed();

	public abstract int getAttackAnim();

	public abstract int getBlockAnim();

	public abstract double protectionPrayerReductionMultiplier(AttackType type);

	/**
	 * Sets if should stall hits
	 */
	//public final void setStallingDamage(boolean stallingDamage) {
	//	this.stallingDamage = stallingDamage;
	//}

	/**
	 * Resets this mob's block set.
	 */
	public final void resetBlockSet() {
		blockSet = new UpdateBlockSet();
	}

	/**
	 * Resets all flags related to updating.
	 */
	public void resetUpdateStates() {
		resetBlockSet();
		walkingDirection = Direction.NONE;
		runningDirection = Direction.NONE;
		pendingTeleportUpdate = false;
		animation = null;
		graphic = null;
	}

	/**
	 * Checks if this mob is active.
	 *
	 * @return {@code true} if the mob is active, {@code false} if not.
	 */
	public final boolean isActive() {
		return getIndex() != -1 && isRegistered();
	}

	/**
	 * Gets this mob's movement {@link Direction}s, as an array.
	 *
	 * @return A zero, one or two element array containing the directions (in order).
	 */
	public final Direction[] getDirections() {
		if (walkingDirection != Direction.NONE) {
			return runningDirection == Direction.NONE
					? new Direction[]{walkingDirection}
					: new Direction[]{walkingDirection, runningDirection};
		}
		return Direction.EMPTY_DIRECTION_ARRAY;
	}

	@Override
	public Entity setPosition(Position position) {
		if(getPosition().isWithinDistance(position, 2) && getPosition() != position) {
			actualLastPosition = getPosition();
		} else {
			actualLastPosition = position.clone().add(-1, 0);
		}
		return super.setPosition(position);
	}

	public Position getCenterLocation() {
		final int offset = getSize() >> 1;
		return getPosition().transform(offset, offset, 0);
	}

	/**
	 * Teleports the character to a target location
	 */
	public Agent moveTo(Position teleportTarget) {
		setTeleportPosition(teleportTarget.clone());
		setEntityInteraction(null);
		return this;
	}

	/**
	 * Teleports the character to a target location via fade screen
	 */
	public Agent moveToByFadeScreen(Player p, Position teleportTarget, String text) {
		p.BLOCK_ALL_BUT_TALKING = true;
		SkillUtil.stopSkillable(p);

		p.getPacketSender().sendFadeScreen(text, 2, 6);
		p.getPacketSender().sendJinglebitMusic(253, 15);

		TaskManager.submit(p, 4, () -> {
			setTeleportPosition(teleportTarget.clone());
			setEntityInteraction(null);
			p.getPacketSender().sendInterfaceRemoval();
			p.BLOCK_ALL_BUT_TALKING = false;
		});
		return this;
	}

	public Agent delayedMoveTo(Position pos, int delay) {
		TaskManager.submit(new Task(delay) {
			@Override
			protected void execute() {
				moveTo(pos);
				stop();
			}
		});
		return this;
	}
	public void switchMove(Position pos1, Position pos2) {
		if(getPosition().sameAs(pos1)) {
			moveTo(pos2);
		} else if(getPosition().sameAs(pos2)) {
			moveTo(pos1);
		}
	}

	public Agent say(String message) {
		setForcedChat(message);
		blockSet.add(UpdateBlock.Companion.createForceChatBlock(message));
		return this;
	}

	public Agent setEntityInteraction(Entity entity) {
		this.interactingEntity = entity;
		blockSet.add(UpdateBlock.Companion.createInteractingMobBlock(entity));
		return this;
	}

	public void forceAnimation(Animation animation){
		this.animation = animation;
		blockSet.add(UpdateBlock.Companion.createAnimationBlock(animation));
	}

	@Override
	public void performAnimation(Animation animation) {

		if (this.animation != null && animation != null) {
			if (this.animation.getPriority().ordinal() > animation.getPriority().ordinal()) {
				return;
			}
		}

		this.animation = animation;

		if(animation != null)
			forceAnimation(animation);
	}

	@Override
	public void performGraphic(Graphic graphic) {
		if(graphic == null)
			return;
		if (this.graphic != null) {
			if (this.graphic.getPriority().ordinal() > graphic.getPriority().ordinal()) {
				return;
			}
		}

		this.graphic = graphic;
		blockSet.add(UpdateBlock.Companion.createGraphicBlock(graphic));
	}

	public int decrementPoisonDamage(){
	    return --poisonDamage;
    }

	public boolean isPoisoned() {
		return poisonDamage > 0;
	}

	public int increaseVenomDamage() { return venomDamage-=1; }

	public boolean isVenomed() { return venomDamage > 0; }

	public Position getPositionToFace() {
		return positionToFace;
	}
	
	public Position getLastPositionToFace() {
		return getMotion().getLastPositionToFace();
	}

	public Agent setPositionToFace(Position positionToFace, boolean force) {
		return setPositionToFace(positionToFace, force, 1, 1);
	}

	public Agent setPositionToFace(Position positionToFace, boolean force, int width, int length) {

		final boolean isFlagged = blockSet.contains(TurnToPositionBlock.class);

		if (isFlagged && !force)
			return this;

		if(positionToFace == null)
			return this;

		this.positionToFace = positionToFace;

		if(this instanceof Player){
			int srcX = getPosition().getX() * 64;
			int srcY = getPosition().getY() * 64;
			int dstX = positionToFace.getX() * 64;
			int dstY = positionToFace.getY() * 64;
			double degreesX = (srcX - dstX);
			double degreesY = (srcY - dstY);
			degreesX += (Math.floor(width / 2.0)) * 32;
			degreesY += (Math.floor(length / 2.0)) * 32;
			faceDegrees = ((int)(Math.atan2(degreesX, degreesY) * 325.949)) & 0x7ff;
		} else {
			int faceX = (positionToFace.getX() * 2) + 1;
			int faceY = (positionToFace.getY() * 2) + 1;
			faceDegrees = (faceX << 16) | faceY;
		}

		Position delta = positionToFace.getDelta(getPosition());
		if(delta.getX() == 0) {
			if(delta.getY() > 0) {
				setLastFacingDirection(Direction.NORTH);
			} else {
				setLastFacingDirection(Direction.SOUTH);
			}
		} else {
			if(delta.getX() > 0) {
				setLastFacingDirection(Direction.EAST);
			} else {
				setLastFacingDirection(Direction.WEST);
			}
		}

		if(interactingEntity != null) {
			if (blockSet.contains(InteractingMobBlock.class)) {
				blockSet.remove(InteractingMobBlock.class);
			}
			interactingEntity = null;
			blockSet.add(UpdateBlock.Companion.createInteractingMobBlock(null));
		}
		blockSet.add(UpdateBlock.Companion
				.createTurnToPositionBlock(faceDegrees, lastFacingDirection));
		return this;
	}

	public Agent setForceMovement(ForceMovement forceMovement) {
		this.forceMovement = forceMovement;
		if (this.forceMovement != null) {
			blockSet.add(UpdateBlock.Companion.createForceMovementBlock(this));
		}
		return this;
	}


	public Agent setPositionToFace(Position positionToFace) {
		return setPositionToFace(positionToFace, false);
	}

	public boolean isTeleporting() {
		return teleportingType != null;
	}

	public TeleportType getTeleportingType() {
		return teleportingType;
	}

	public void setTeleporting(TeleportType type) {
		this.teleportingType = type;
	}

	public void setTeleportingTask(Task teleportingTask) {
		this.teleportingTask = teleportingTask;
	}

	public Task getTeleportingTask() {
		return teleportingTask;
	}

	public Graphic getGraphic() {
		return graphic;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * @return the lastCombat
	 */
	public Stopwatch getLastCombat() {
		return lastCombat;
	}

	public int getPoisonDamage() {
		return poisonDamage;
	}

	public void setPoisonDamage(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	public int getVenomDamage() { return venomDamage; }

	public void setVenomDamage(int venomDamage) { this.venomDamage = venomDamage; }

	public Entity getInteractingEntity() {
		return interactingEntity;
	}

	public String getForcedChat() {
		return forcedChat;
	}

	public Agent setForcedChat(String forcedChat) {
		this.forcedChat = forcedChat;
		return this;
	}

	public boolean hasActivePrayer(final int prayerId){
		return prayerActive[prayerId];
	}

	public boolean[] getPrayerActive() {
		return prayerActive;
	}

	public Agent setPrayerActive(boolean[] prayerActive) {
		this.prayerActive = prayerActive;
		return this;
	}

	public boolean[] getCurseActive() {
		return curseActive;
	}

	public Agent setCurseActive(boolean[] curseActive) {
		this.curseActive = curseActive;
		return this;
	}

	public Agent setPrayerActive(int id, boolean prayerActive) {
		this.prayerActive[id] = prayerActive;
		ifPlayer(player -> {
			final PrayerType type = PrayerHandler.getPrayer(id);
			if(type != null) {
				if(prayerActive)
					player.getPrayer().activatePrayer(type);
				else
					player.getPrayer().deActivatePrayer(type);
			} else
				System.err.println("Attempted to toggle non-existen prayer {"+id+", "+prayerActive+"}");
		});
		return this;
	}

	public Agent setCurseActive(int id, boolean curseActive) {
		this.curseActive[id] = curseActive;
		return this;
	}

	public void resetTransformation() {
		debug("reset transform");
		setNpcTransformationId(-1);
	}

	public int getNpcTransformationId() {
		return npcTransformationId;
	}

	public boolean isMorphed(){
		return npcTransformationId != -1;
	}

	public Agent setNpcTransformationId(int npcTransformationId) {
		this.npcTransformationId = npcTransformationId;
		updateAppearance();
		return this;
	}

	public Damage decrementHealth(Damage damage) {

		if (getHitpoints() <= 0)
			return damage;


		if (this instanceof NPC) {
			if (getAsNpc().getId() == NpcID.COMBAT_DUMMY || getAsNpc().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
				setHitpoints(getHitpoints());
				if(damage.getDamageListener() != null)
					damage.getDamageListener().onDamageDealt(damage.getValue());
				return damage;
			}
		}

		if (damage.getValue() > getHitpoints())
			damage.set(getHitpoints());
		if (damage.getValue() < 0)
			damage.set(0);

		if(this instanceof Player)
			getAsPlayer().getItemDegradationManager().degrade(DegradingType.LOSE_HEALTH, damage.getValue());

		if(damage.getDamageListener() != null)
			damage.getDamageListener().onDamageDealt(damage.getValue());

		int outcome = getHitpoints() - damage.getValue();
		if (outcome < 0)
			outcome = 0;

		if (damage.getValue() <= 0)
			return damage;

		setHitpoints(outcome);
		return damage;
	}

	public Boundary getBoundary() {
		int x = getPosition().getX();
		int y = getPosition().getY();
		int size = (getSize() - 1);
		return new Boundary(x, x + size, y, y + size);
	}

	/**
	 * Get the primary hit for this entity.
	 *
	 * @return the primaryHit.
	 */
	public Damage getPrimaryHit() {
		return primaryHit;
	}

	public void setPrimaryHit(Damage hit) {
		this.primaryHit = hit;
	}

	/**
	 * Get the secondary hit for this entity.
	 *
	 * @return the secondaryHit.
	 */
	public Damage getSecondaryHit() {
		return secondaryHit;
	}

	public void setSecondaryHit(Damage hit) {
		this.secondaryHit = hit;
	}

	/*
	 * Motion queue
	 */

	public Direction getWalkingDirection() {
		return walkingDirection;
	}

	public void setWalkingDirection(Direction walkDirection) {
		this.walkingDirection = walkDirection;
	}

	public Direction getRunningDirection() {
		return runningDirection;
	}

	public void setRunningDirection(Direction runDirection) {
		this.runningDirection = runDirection;
	}

	/**
	 * Gets if this entity is registered.
	 *
	 * @return the unregistered.
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * Sets if this entity is registered,
	 *
	 * @param registered
	 *            the unregistered to set.
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public boolean hasPendingTeleportUpdate() {
		return pendingTeleportUpdate;
	}

	public void setPendingTeleportUpdate(boolean pendingTeleportUpdate) {
		this.pendingTeleportUpdate = pendingTeleportUpdate;
	}

	public PrayerType[] prayersUsed() {
		return Stream.of(PrayerType.values()).filter(prayerType -> hasActivePrayer(prayerType.ordinal())).toArray(PrayerType[]::new);
	}

	public SecondsTimer getVengeanceEffect() {
		return vengeanceEffectTimer;
	}

	public boolean isSpecialActivated() {
		return specialActivated;
	}

	// Doesn't update player's orb state, use Player.setSpecialActivatedAndSendState instead
	public void setSpecialActivated(boolean specialActivated) {
		this.specialActivated = specialActivated;
	}

	public int getSpecialPercentage() {
		return specialPercentage;
	}

	public void setSpecialPercentage(int specialPercentage) {
		this.specialPercentage = specialPercentage;
	}

	public void decrementSpecialPercentage(int drainAmount) {
		this.specialPercentage -= drainAmount;

		if (specialPercentage < 0) {
			specialPercentage = 0;
		}
	}

	public void incrementSpecialPercentage(int gainAmount) {
		this.specialPercentage += gainAmount;

		if (specialPercentage > 100) {
			specialPercentage = 100;
		}
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
	}

	public boolean isUntargetable() {
		return untargetable;
	}

	public void setUntargetable(boolean untargetable) {
		this.untargetable = untargetable;
	}

	public boolean inDungeon() {
		return false;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public boolean isAlive() {
    	return getHitpoints() > 0;
    }

    //public boolean isStallingDamage() {
	//	return stallingDamage;
	//}

	public boolean canBeTargeted() {
		return !isUntargetable();
	}

	public int executeProjectile(Projectile projectile) {
		projectile.sendProjectile();
		return projectile.calculateTimeTillArrival();
	}

	public void ifNPC(final Consumer<NPC> action){
		if(this instanceof NPC)
			action.accept((NPC) this);
	}

	public void ifPlayer(final Consumer<Player> action){
		if(this instanceof Player)
			action.accept((Player) this);
	}

	public void ifPlayerWith(PlayerRights rights, final Consumer<Player> action){
		if(this instanceof Player && ((Player) this).getRights() == rights)
			action.accept((Player) this);
	}

	public void messageIfPlayer(String message) {
		ifPlayer(player -> player.sendMessage(message));
	}

	public void messageIfPlayer(String message, int delayInMillis) {
		ifPlayer(player -> player.getPacketSender().sendMessage(message, delayInMillis));
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public int getFaceDegrees() {
		return faceDegrees;
	}

	public void setFaceDegrees(int faceDegrees) {
		this.faceDegrees = faceDegrees;
	}

	public void setTeleportPosition(Position teleportPosition) {
		this.teleportPosition = teleportPosition;
	}

	public Position getTeleportPosition() {
		return teleportPosition;
	}

	public Position getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(Position lastPosition) {
		this.lastPosition = lastPosition;
	}

	public Direction getLastFacingDirection() {
		return lastFacingDirection;
	}

	public void setLastFacingDirection(Direction lastFacingDirection) {
		this.lastFacingDirection = lastFacingDirection;
	}

	/**
	 * Gets this mob's {@link UpdateBlockSet}.
	 *
	 * @return The block set.
	 */
	public final UpdateBlockSet getBlockSet() {
		return blockSet;
	}
}