package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.task.EditBirdHouseTask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.time.Instant;

/**
 * @author Zach S <zach@findzach.com>
 * @since 12/21/2020
 * <p>
 * Represents a Simple BirdHouse
 */
public class BirdHouse {

    public static transient int PARROT_ITEM_ID = 23300;

    private int birdHouseConfig;
    private transient Player owner;

    /**
     * Simple Boolean to check if our user has looted this BirdHouse already or not
     */
    private transient boolean hasLooted = false;

    /**
     * The active state of the BirdHouse
     * TODO: Update state when logged in based on filledSeedTimpStamp
     */
    @Expose
    private BirdHouseState state;
    @Expose
    private BirdHouseTier tier;
    @Expose
    private BirdHouseSpot spot;
    /**
     * How many seeds we have {values=hopseed = 1, herbseed = 2}
     */
    @Expose
    private int seedLevel = 0;

    /**
     * The time the user has made the bird house 'operable'
     */
    @Expose
    private long filledSeedsTimeStamp;


    /**
     * an object for every birdhouse space
     *
     * @param player
     * @param state
     * @param tier
     */
    BirdHouse(Player player, BirdHouseSpot spot, BirdHouseState state, BirdHouseTier tier) {
        this.owner = player;
        this.spot = spot;
        this.tier = tier;
        this.state = state;
    }

    /**
     * Will be called when we want to fill our birdhouses
     */
    private boolean depositSeed(int seedId) {

        Seeds seedGroup = null;

        if (seedId == -1) {
            for (Item item : owner.getInventory().getItems()) {
                if (Seeds.seedType(item.getId()).isPresent()) {
                    seedGroup = Seeds.seedType(item.getId()).get();
                    continue; // redundant continue
                }
            }
        } else {
            if (Seeds.seedType(seedId).isPresent()) {
                seedGroup = Seeds.seedType(seedId).get();
            }
        }

        if (seedGroup == null) {
            DialogueManager.sendStatement(owner,
                    "You need at least 10 Hop Seeds, or any 5 Herb seeds; Ranarr and above");
            return false;
        }

        // seed group is never null at this point
        if (seedGroup != null) {
            // no optional.isPresent check
            int deleteSeedId = seedGroup.getSeedList().stream().filter(seed -> owner.getInventory().contains(seed)).findFirst().get();

            int seedCost = deleteSeedId == ItemID.WILDBLOOD_SEED ? 5 : seedGroup.getSeedCost();
            int amtAdded = owner.getInventory().getAmount(deleteSeedId);
            int newSeedLev = seedGroup == Seeds.HERB ? amtAdded * 2 : amtAdded;
            int thresShold = newSeedLev + seedLevel > 10 ? 10 : newSeedLev + seedLevel; // Math.min :P
            if (thresShold < 10) {
                owner.sendMessage("You add seeds to your trap!");
                seedLevel += newSeedLev;
                owner.getInventory().delete(deleteSeedId, amtAdded);
                owner.performAnimation(new Animation(543, 1));
            } else {
                setState(BirdHouseState.BUILT_COLLECTING);
                EditBirdHouseTask editTask = new EditBirdHouseTask(owner, this);

                DialogueManager.sendStatement(owner, "The " + Misc.capitalize(tier.name().toLowerCase()) + " trap is now full of seed and will start to catch birds.");

                owner.BLOCK_ALL_BUT_TALKING = true;
                TaskManager.submit(editTask);

                int divAmt = seedGroup == Seeds.HERB ? 2 : 1;
                owner.getInventory().delete(deleteSeedId, seedCost - (seedLevel / divAmt));

                seedLevel = 10;
                filledSeedsTimeStamp = Instant.now().getEpochSecond();
            }
            return true;
        }

        return false;
    }

    public final static int DELAY_IN_SECONDS = 60 * 50;

    public void handleItemOnObject(int itemId) {
        if (state == BirdHouseState.NOT_BUILT || state == BirdHouseState.BUILT_FULL_COLLECTED) {
            BirdHouseActions.INSTANCE.handleSpaceClick(owner, spot.getHotSpotPos());
        }
        if (state == BirdHouseState.BUILT_EMPTY) {
            depositSeed(itemId);
        }
    }

    public void handleClick(int clickType) {

        switch (state) {
            case NOT_BUILT:
            case BUILT_FULL_COLLECTED:
                BirdHouseActions.INSTANCE.handleSpaceClick(owner, spot.getHotSpotPos());
                break;
            case BUILT_EMPTY:
                if (clickType == 252) {
                    sendSeedLevel();
                    break;
                }
                depositSeed(-1);
                break;
            case BUILT_COLLECTING:
                if(clickType == 70) {
                    dismantle();
                    break;
                }
                //Provides the user with an estimated time frame of when the birdhouses should be full of birds
                long lowerEndTimeRemaining = ((timeRemaining() / 60) - 1) < 0 ? 0 : ((timeRemaining() / 60) - 1);
                DialogueManager.sendStatement(owner, "The " + Misc.capitalize(tier.name().toLowerCase()) + " trap is fully baited and waiting for birds! Return back @blu@Time Remaining: @red@" + lowerEndTimeRemaining + " - " + +((timeRemaining() / 60) + 4) + "@blu@ minutes");
                verifyState();
                break;
            case BUILT_FULL:
                if (owner.getInventory().countFreeSlots() < 3) {
                 owner.sendMessage("@red@Please make more room in your inventory before doing this.");
                } else {
                    rewardPlayer();
                }
                break;
        }
    }

    /**
     * This checks if we should update to BUILT_FULL
     */
    public void verifyState() {
        long delayInSeconds = Instant.now().getEpochSecond() - getFilledSeedsTimeStamp();
        if (hasLooted) {
            state = BirdHouseState.BUILT_FULL_COLLECTED;
        }
        if (delayInSeconds > DELAY_IN_SECONDS && state == BirdHouseState.BUILT_COLLECTING) {
            state = BirdHouseState.BUILT_FULL;
            refreshBirdHouse();
            owner.sendMessage("@blu@Your @red@" + Misc.capitalize(getTier().name().toLowerCase()) + "@blu@ birdhouse located at @red@" + Misc.capitalizeWords(getSpot().name().toLowerCase()).replaceAll("_", " ") + "@blu@ is now full!");
            return;
        }
        if (state != BirdHouseState.BUILT_COLLECTING) {
            refreshBirdHouse();
        }
    }

    /**
     * Returns the difference in Epoch Seconds
     *
     * unused @return
     *
     * @return
     */
    public long timeRemaining() {
        return (60 * 50) - (Instant.now().getEpochSecond() - filledSeedsTimeStamp);
    }

    private void dismantle() {
        owner.performAnimation(new Animation(827));
        owner.getInventory().add(ItemID.CLOCKWORK, 1);
        state = BirdHouseState.NOT_BUILT;
        refreshBirdHouse();
    }

    /**
     * Will handle the rewards for the player and resets the BirdHouse
     */
    private void rewardPlayer() {
        if (hasLooted) return;


        owner.performAnimation(new Animation(827));
        //OSRS they get the clockwork back
        owner.getInventory().add(ItemID.CLOCKWORK, 1);


        /*
         * We will give user xp and rewards here
         */
        hasLooted = true;
        state = BirdHouseState.BUILT_FULL_COLLECTED;
        refreshBirdHouse();

        int nestAmount = Misc.random(1, tier == BirdHouseTier.WOOD ? 2: tier.ordinal() + 2);
        int divideAmt = tier == BirdHouseTier.WOOD ? 1 : tier.ordinal();

        boolean parrotRoll = Misc.random(1, 5000 / divideAmt) < 3;
        int coinAmount = tier == BirdHouseTier.WOOD ? 500_000 : 500_000 * tier.ordinal();

        owner.getInventory().add(ItemID.COINS, coinAmount);
        owner.getInventory().add(ItemID.CRUSHED_NEST_2, nestAmount);

        //random collectable item
        int randomFeatherAmt = tier == BirdHouseTier.WOOD ? 100 : 100 * tier.ordinal();

        owner.getInventory().add(ItemID.BLUE_FEATHER, Misc.random(randomFeatherAmt));

        if (parrotRoll) {
            PlayerUtil.broadcastMessage("<img=760> @yel@Congratulations! " + PlayerUtil.getImages(owner) + "" + owner.getUsername() +" has just received the Shoulder parrot from a birdhouse!");
            owner.getInventory().add(PARROT_ITEM_ID, 1);
        }

        owner.getSkillManager().addExperience(Skill.HUNTER, tier.getExpGained());
    }


    /**
     * Gets the objectID we for this BirdHouse
     *
     * @return The ID based on current state
     */
    public int getCurrentObjectID() {
        return state.equals(BirdHouseState.BUILT_FULL_COLLECTED) || state.equals(BirdHouseState.NOT_BUILT) ? spot.objectId : tier.getObjectId() + state.index;
    }

    /**
     * Gets the objectID we for this BirdHouse
     *
     * @return The ID based on current state
     */
    public int getCurrentObjectID(BirdHouseState updatedState) {
        if (updatedState == BirdHouseState.BUILT_FULL_COLLECTED) {
            return ObjectID.SPACE;
        }
        return (updatedState == BirdHouseState.NOT_BUILT) ? tier.getObjectId() : tier.getObjectId() + updatedState.ordinal();
    }


    /**
     * Updates the birdhouse for our player
     *
     * @param newState - The state we want our birdhouse to be
     */
    public final void updateBirdHouse(BirdHouseState newState) {

//        int newStateId = 1 + (getTier().ordinal() * 3) + (getState().equals(BirdHouseState.BUILT_FULL_COLLECTED) ? 2 : getState().equals(BirdHouseState.NOT_BUILT) ? 0 : getState().ordinal());

//        if (newState == BirdHouseState.BUILT_FULL_COLLECTED) {
//            newStateId = 2;
//        } else if(newState == BirdHouseState.NOT_BUILT) {
//            newStateId = 0;
//        }

//        setBirdHouseConfig(newStateId);

        Graphic gfx = new Graphic(542, 1, GraphicHeight.LOW);
        owner.getPacketSender().sendIndividualGraphic(gfx, getSpot().getHotSpotPos());

        this.state = newState;

        //Minor delay for graphic to render
        DynamicGameObject obj = DynamicGameObject.createLocal(getCurrentObjectID(), getSpot().getHotSpotPos(), 10, 0);
        owner.addObject(obj);
        TaskManager.submit(this, 1, () -> owner.getPacketSender().sendObject(obj));
//        TaskManager.submit(this, 1, () -> owner.getPacketSender().sendConfig(1626, getBirdHouseConfig()));
    }

    private void sendSeedLevel() {
        DialogueManager.sendStatement(owner, "Your birdhouse seed level is: " + seedLevel + "/10. @red@It must be full of @red@seed before it will start catching birds.");
    }

    public long getFilledSeedsTimeStamp() {
        return filledSeedsTimeStamp;
    }

    public void setFilledSeedsTimeStamp(long filledSeedsTimeStamp) {
        this.filledSeedsTimeStamp = filledSeedsTimeStamp;
    }

    /**
     * Updates the birdhouse for our player
     */
    public final void refreshBirdHouse() {
        //verifyState();
        updateBirdHouse(state);
    }

    public int getBirdHouseConfig() {
        return birdHouseConfig;
    }

    public void setBirdHouseConfig(int birdHouseConfig) {
        this.birdHouseConfig = birdHouseConfig;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public BirdHouseState getState() {
        return state;
    }

    public void setState(BirdHouseState state) {
        this.state = state;
    }

    public BirdHouseTier getTier() {
        return tier;
    }

    public void setTier(BirdHouseTier tier) {
        this.tier = tier;
    }

    public BirdHouseSpot getSpot() {
        return spot;
    }
}
