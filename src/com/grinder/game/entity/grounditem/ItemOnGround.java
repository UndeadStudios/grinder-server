package com.grinder.game.entity.grounditem;

import com.grinder.game.World;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.movement.pathfinding.target.PointTargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.EntityUpdateType;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.update.GroupableEntity;
import com.grinder.game.model.area.update.ItemUpdateOperation;
import com.grinder.game.model.area.update.UpdateOperation;
import com.grinder.game.model.item.Item;

import java.util.Optional;

/**
 * Represents an item on the ground.
 *
 * @author Professor Oak
 */
public class ItemOnGround extends Entity implements GroupableEntity {

	/**
	 * The current {@link State} of this {@link ItemOnGround}.
	 */
	private State state;

	/**
	 * The owner of this {@link ItemOnGround}.
	 */
	private Optional<String> owner;

	/**
	 * The {@link Item} of this {@link ItemOnGround}.
	 */
	private final Item item;

	/**
	 * Does this {@link ItemOnGround} go global?
	 */
	private final boolean goesGlobal;

	/**
	 * A tick counter, used for processing.
	 */
	private int tick;

	/**
	 * Is this item in the process of being deleted?
	 */
	private boolean pendingRemoval;

	/**
	 * When the item is removed from the region, instead of looping through the world item list to remove the item, we let the item handler remove it during loop.
	 */
	private boolean removedFromRegion;

	/**
	 * The amount of cycles it takes for this {@link ItemOnGround} to respawn.
	 */
	private int respawnTimer = -1;

	/**
	 * The old amount of the item, used when modifying its amounts.
	 */
	private int oldAmount;

	/**
	 * Used to make sure the item always respawns with the same amount!
	 */
	private final int initialAmount;
	private final int ticksTillStateChange;
	private boolean isBeingGrabbed;

	private int index;

	/**
	 * Strategy to check if within interaction range of this entity
	 */
	public TargetStrategy interactTarget = new PointTargetStrategy(this);

	/**
	 * Constructs a new {@link ItemOnGround}.
	 */
	public ItemOnGround(State state, Optional<String> owner, Position position, Item item, boolean goesGlobal,
						int respawnTimer, int ticksTillStateChange) {
		super(position);
		this.state = state;
		this.owner = owner;
		this.item = item;
		this.goesGlobal = goesGlobal;
		this.respawnTimer = respawnTimer;
		this.ticksTillStateChange = ticksTillStateChange;
		initialAmount = item.getAmount();
	}

	/**
	 * Processes the ground item depending on its current state.
	 */
	public void process() {
		incrementTick();
		switch (state) {
		case SEEN_BY_PLAYER:
		case SEEN_BY_EVERYONE:
			if (getTick() >= ticksTillStateChange)
				updateItem();
			break;
		}
	}

	//NOTE: Stackables don't stack up when becoming public on OSRS
	private void updateItem() {

		setTick(0);
		// Check if item is currently private and needs to go global..
		if (state == State.SEEN_BY_PLAYER && goesGlobal) {
			setState(State.SEEN_BY_EVERYONE);
			//Send update to region and filter owner
			Region region = World.getRegions().get(getPosition().getRegionCoordinates());
			region.displayPublicItem(this, EntityUpdateType.MAKE_PUBLIC);
			return;
		}

		// Item needs to be deleted.
		// However, there's no point in deleting items that will just
		// respawn..
		if (!respawns())
			ItemOnGroundManager.deregister(this);
	}

	public Optional<String> findOwner() {
		return owner;
	}

	public Item getItem() {
		return item;
	}

	public int getTick() {
		return tick;
	}

	public ItemOnGround setTick(int tick) {
		this.tick = tick;
		return this;
	}

	public void incrementTick() {
		this.tick++;
	}

	public boolean goesGlobal() {
		return goesGlobal;
	}

	public State getState() {
		return state;
	}

	public ItemOnGround setState(State state) {
		this.state = state;
		return this;
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public boolean respawns() {
		return respawnTimer > 0;
	}

	public int getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(int oldAmount) {
		this.oldAmount = oldAmount;
	}

	public boolean isPendingRemoval() {
		return pendingRemoval;
	}

	public void setPendingRemoval(boolean pendingRemoval) {
		this.pendingRemoval = pendingRemoval;
	}

	public boolean isRemovedFromRegion() {
		return removedFromRegion;
	}

	public ItemOnGround setRemovedFromRegion(boolean removed) {
		this.removedFromRegion = removed;
		return this;
	}

	/**
	 * Gets the index of the {@link Player} who dropped this GroundItem, or {@code -1} if this GroundItem was not
	 * dropped by a Player.
	 *
	 * @return The index.
	 */
	public int getOwnerIndex() {
		return -1;
	}

	@Override
	public ItemOnGround clone() {
		return new ItemOnGround(state, owner, getPosition(), item, goesGlobal, respawnTimer, ItemOnGroundManager.STATE_UPDATE_DELAY);
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof ItemOnGround) {
//			final ItemOnGround other = (ItemOnGround) obj;
//			if (other.findOwner().isPresent() && this.findOwner().isPresent()) {
//				if (!other.findOwner().get().equals(this.findOwner().get())) {
//					return false;
//				}
//			}
//			return other.getItem().equals(this.getItem())
//					&& other.getPosition().equals(this.getPosition())
//					&& other.getState() == this.getState()
//					&& other.getTick() == this.getTick();
//		}
//		return false;
//	}


	@Override
	public String toString() {
		return "ItemOnGround, id: " + item.getId() + ", amount: " + item.getAmount() + ", current state: "
				+ state.toString() + ", goesGlobal: " + goesGlobal + ", tick: " + tick + ", respawns: " + respawns();
	}

//	@Override
//	public int hashCode() {
//		if (getIndex() == 0)return super.hashCode();
//		return getPosition().hashCode() * 31 + getIndex();
//	}

	@Override
	public EntityType getEntityType() {
		return EntityType.ITEM;
	}

	@Override
	public UpdateOperation<ItemOnGround> toUpdateOperation(Region region, EntityUpdateType operation) {
		return new ItemUpdateOperation(region, operation, this);
	}

	public boolean isPublic() {
		return state == State.SEEN_BY_EVERYONE;
	}

	public boolean isOwnedBy(Player player){
		return findOwner().filter(ownerName -> ownerName.equals(player.getUsername())).isPresent();
	}

	public int getInitialAmount() {
		return initialAmount;
	}
	public void setBeingGrabbed(boolean beingGrabbed) {
		isBeingGrabbed = beingGrabbed;
	}

	public boolean isBeingGrabbed() {
		return isBeingGrabbed;
	}
	/**
	 * All the possible states a {@link ItemOnGround} can have.
	 */
	public enum State {
		SEEN_BY_PLAYER, SEEN_BY_EVERYONE;
	}
}
