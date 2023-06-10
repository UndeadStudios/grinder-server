package com.grinder.game.content.skill.skillable.impl.hunter_new.traps;

import com.grinder.game.model.Position;
import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;

/**
 * Represents a single trap on the world.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public abstract class HunterTrap {

    /**
     * The owner of this trap.
     */
    protected final Player player;

    /**
     * The type of this trap.
     */
    private final TrapType type;

    /**
     * The global object spawned on the world.
     */
    private DynamicGameObject object;

    /**
     * Determines if this trap has been abandoned.
     */
    private boolean abandoned;

    /**
     * The state of this trap.
     */
    private TrapState state;

    /**
     * Constructs a new {@link HunterTrap}.
     * @param player {@link #player}.
     * @param type {@link #type}.
     */
    public HunterTrap(Player player, TrapType type) {
        this.player = player;
        this.type = type;
        this.state = TrapState.PENDING;
        this.object = DynamicGameObject.createPublic(type.objectId, player.getPosition());
    }

    /**
     * Submits the trap task for this trap.
     */
    public final void submit() {
        player.performAnimation(new Animation(827));
        player.getInventory().delete(new Item(type.getItemId(), 1));
        this.onSetup();
        World.addObject(this.object);
    }

    /**
     * Attempts to trap the specified {@code mob} by checking the prerequisites and initiating the
     * abstract {@link #onCatch} method.
     * @param npc the mob to trap.
     */
    protected void trap(NPC npc) {
        if(!this.getState().equals(TrapState.PENDING)) {
            return;
        }
        onCatch(npc);
    }

    /**
     * The array containing every larupia item set.
     */
    private static final int[] LARUPIA_SET = new int[]{10041, 10043, 10045};

    /**
     * Determines fi the player has equiped any set that boosts the success formula.
     * @return whether the player has the larupia set equipped.
     */
    public boolean hasLarupiaSetEquipped() {
        return player.getEquipment().containsAll(LARUPIA_SET);
    }

    /**
     * Calculates the chance for the bird to be lured <b>or</b> trapped.
     * @return the double value which defines the chance.
     */
    public int successFormula() {
        Player player = this.getPlayer();
        if(player == null) {
            return 0;
        }
        int chance = 70;
        if(this.hasLarupiaSetEquipped()) {
            chance = chance + 10;
        }

        int hunterLvl = player.getSkills().getLevel(Skill.HUNTER);

        chance = chance + (int) (hunterLvl / 1.5) + 10;

        if(hunterLvl < 25) {
            chance = (int) (chance * 1.5) + 8;
        } else if(hunterLvl < 40) {
            chance = (int) (chance * 1.4) + 3;
        } else if(hunterLvl < 50) {
            chance = (int) (chance * 1.3) + 1;
        } else if(hunterLvl < 55) {
            chance = (int) (chance * 1.2);
        } else if(hunterLvl < 60) {
            chance = (int) (chance * 1.1);
        } else if(hunterLvl < 65) {
            chance = (int) (chance * 1.05) + 3;
        }
        return chance;
    }

    public abstract boolean canLay();

    /**
     * Determines if the trap can catch.
     * @param npc the mob to check.
     * @return {@code true} if the player can, {@code false} otherwise.
     */
    public abstract boolean canCatch(NPC npc);

    /**
     * The functionality that should be handled when the trap is picked up.
     */
    public abstract void onPickUp();

    /**
     * Determines whether this trap can be picked up.
     * @return {@code true} if the trap can, {@code false} if the trap can't.
     */
    public abstract boolean canPickup();

    /**
     * The functionality that should be handled when the trap is being set-up.
     */
    public abstract void onSetup();

    /**
     * The functionality that should be handled when the trap has catched.
     * @param npc the mob that was catched.
     */
    public abstract void onCatch(NPC npc);

    /**
     * The functionality that should be handled every 600ms.
     */
    public abstract void onSequence();

    /**
     * The reward for this player.
     * return an array of items defining the reward.
     */
    public abstract Item[] reward();

    /**
     * The experience gained for catching this npc.
     * @return a numerical value defining the amount of experience gained.
     */
    public abstract double experience();

    /**
     * Determines if the trap can be claimed.
     * @return {@code true} if the trap can, {@code false} otherwise.
     */
    public abstract boolean canClaim();

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the type
     */
    public TrapType getType() {
        return type;
    }

    /**
     * @return the state
     */
    public TrapState getState() {
        return state;
    }

    /**
     * @param state the state to set.
     */
    public void setState(TrapState state, NPC npc) {
        this.state = state;
    }

    /**
     * @return the object
     */
    public DynamicGameObject getObject() {
        return object;
    }

    /**
     * Sets the object id.
     * @param id the id to set.
     */
    public final void updateObject(int id) {
        Position p = object.getPosition().copy();
        World.deSpawn(object);
        this.object = DynamicGameObject.createPublic(id, p);
        World.addObject(this.object);
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    public final void remove() {
        player.hunterTraps.remove(this);
        World.deSpawn(object);
    }

    public final void abandon(boolean logout) {
        if(!player.hunterTraps.contains(this)) {
            return;
        }

        remove();
        Item groundItem = new Item(type.itemId);

        if(logout) {
            ItemOnGroundManager.registerGlobal(player, groundItem, getObject().getPosition());
        } else {
            ItemOnGroundManager.register(player, groundItem, getObject().getPosition());
        }

        setAbandoned(true);
    }

    /**
     * The enumerated type whose elements represent a set of constants
     * used to define the type of a trap.
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    public enum TrapType {
        BOX_TRAP(9380,9385, 10008),
        BIRD_SNARE(9345, 9344, 10006);

        /**
         * The object id for this trap.
         */
        private final int objectId;

        /**
         * The failed object id for this trap.
         */
        private final int failedObjectId;

        /**
         * The item id for this trap.
         */
        private final int itemId;

        /**
         * Constructs a new {@link TrapType}.
         * @param objectId          {@link #objectId}.
         * @param failedObjectId    {@link #failedObjectId}.
         * @param itemId            {@link #itemId}.
         */
        TrapType(int objectId, int failedObjectId, int itemId) {
            this.objectId = objectId;
            this.failedObjectId = failedObjectId;
            this.itemId = itemId;
        }

        /**
         * @return the object id
         */
        public int getObjectId() {
            return objectId;
        }

        /**
         * @return the failed object id
         */
        public int getFailedObjectId() {
            return failedObjectId;
        }

        /**
         * @return the item id
         */
        public int getItemId() {
            return itemId;
        }

    }

    /**
     * The enumerated type whose elements represent a set of constants
     * used to define the state of a trap.
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    public enum TrapState {
        PENDING, CATCHING, CAUGHT, FALLEN
    }
}
