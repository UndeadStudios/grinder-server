package com.grinder.game.content.skill.skillable.impl.hunter_new.traps.bird;

import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.*;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.random.RandomUtil;
import com.grinder.game.model.item.Item;

import static com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter.getMaximumTraps;
import static com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter.lay;

/**
 * The bird snare implementation of the {@link com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap} class which represents a single bird snare.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class BirdSnare extends HunterTrap {

    public static boolean ENABLED = true;
    /**
     * Constructs a new {@link BirdSnare}.
     * @param player {@link #getPlayer()}.
     */
    public BirdSnare(Player player) {
        super(player, TrapType.BIRD_SNARE);
    }

    /**
     * The npc trapped inside this box.
     */
    private NPC trapped;

    /**
     * Determines if a bird is going to the trap.
     */
    private Task event;

    /**
     * The distance the npc has to have from the snare before it gets triggered.
     */
    private static final int DISTANCE_PORT = 10;

    /**
     * Kills the specified {@code mob}.
     * @param mob the mob to kill.
     */
    private void kill(NPC mob) {
        mob.moveTo(new Position(0, 0, 0));
        mob.appendDeath();
        trapped = mob;
    }

    public static void configure() {
        configureBirdSnare();
        for(BirdData data : BirdData.VALUES) {
            ObjectActions.INSTANCE.onClick(new int[]{data.objectId}, action -> {
                Player player = action.getPlayer();
                Position pos = new Position(action.getX(), action.getY(), player.getPlane());
                HunterTrap trap = player.findHunterTrap(pos);

                if(trap == null) {
                    player.sendMessage("This is not your trap!");
                    return true;
                }

                player.setPositionToFace(trap.getObject().getPosition());

                if(!trap.canClaim()) {
                    return true;
                }

                player.getSkillManager().addExperience(Skill.HUNTER, trap.experience());

                for(Item item : data.reward) {
                    int amount = 1;

                    if(item.getDefinition().getName().contains("feather")) {
                        amount = Misc.inclusive(5, 10);
                    }

                    player.getInventory().add(new Item(item.getId(), amount));
                }

                player.performAnimation(new Animation(827));
                trap.onPickUp();
                PetHandler.onSkill(player, Skill.HUNTER);
                return true;
            });
        }
        ObjectActions.INSTANCE.onClick(new int[]{HunterTrap.TrapType.BIRD_SNARE.getFailedObjectId(), HunterTrap.TrapType.BIRD_SNARE.getObjectId()}, action -> {
            Player player = action.getPlayer();
            Position pos = new Position(action.getX(), action.getY(), player.getPlane());
            HunterTrap trap = player.findHunterTrap(pos);

            if(trap == null) {
                player.sendMessage("This is not your trap!");
                return true;
            }

            player.setPositionToFace(trap.getObject().getPosition());

            if(!trap.canPickup()) {
                return true;
            }

            player.performAnimation(new Animation(827));
            trap.onPickUp();
            return true;
        });
    }

    public static void configureBirdSnare() {
        ItemActions.INSTANCE.onClick(new int[]{TrapType.BIRD_SNARE.getItemId()},action -> {
            if(action.isInInventory() && !action.isDropAction()) {
                lay(action.getPlayer(), new BirdSnare(action.getPlayer()));
            }
            return true;
        });
    }

    @Override
    public boolean canLay() {
        int maxTraps = getMaximumTraps(player);
        if(player.hunterTraps.size() >= maxTraps) {
            player.sendMessage("You cannot lay more then " + maxTraps + " with your hunter level.");
            return false;
        }
        return true;
    }

    @Override
    public boolean canCatch(NPC mob) {
        return BirdData.VALUES.stream().anyMatch(b -> b.toString().equalsIgnoreCase(mob.fetchDefinition().getName()));
    }

    @Override
    public void onPickUp() {
        remove();
        player.getInventory().add(getType().getItemId(), 1);
        player.sendMessage("You pick up your bird snare.");
    }

    @Override
    public boolean canPickup() {
        if(player.getInventory().countFreeSlots() < 1) {
            player.sendMessage("You don't have enough inventory slots to dismantle this trap.");
            return false;
        }
        return true;
    }

    @Override
    public void onSetup() {
        player.hunterTraps.add(this);
        player.sendMessage("You set-up your bird snare.");
    }

    @Override
    public void onCatch(NPC mob) {
        if(event != null || !getState().equals(TrapState.PENDING)) {
            return;
        }

        if(RandomUtil.getRandomInclusive(100) < 80) {
            return;
        }

        BirdData data = BirdData.getByID(mob.getId());

        if(data == null) {
            return;
        }

        setState(TrapState.CATCHING, mob);

        BirdSnare snare = this;
        event = new Task(1, false) {

            @Override
            public void execute() {
                mob.getMotion().clearAndEnqueue(getObject().getPosition().copy());

                if(!getState().equals(TrapState.CATCHING)) {
                    this.stop();
                    return;
                }
                if(isAbandoned() || !player.hunterTraps.contains(snare)) {
                    this.stop();
                    return;
                }
                if(mob.getPosition().getX() == getObject().getX() && mob.getPosition().getY() == getObject().getY()) {
                    this.stop();
                    int requiredLevel = data.requirement;
                    int playerLevel = player.getSkillManager().getCurrentLevel(Skill.HUNTER);
                    boolean success = Misc.getRandomInclusive(requiredLevel) <= Misc.getRandomInclusive(playerLevel + 8);
                    if(playerLevel < requiredLevel) {
                        setState(TrapState.FALLEN, mob);
                        return;
                    }
                    if(!success) {
                        setState(TrapState.FALLEN, mob);
                        return;
                    }
                    kill(mob);
                    updateObject(data.objectId);
                    setState(TrapState.CAUGHT, mob);
                }
            }

            @Override
            public void onStop() {
                event = null;
                mob.getMotion().clearSteps();
            }
        }.bind(player);

        TaskManager.submit(event);
    }

    int tick = 0;

    @Override
    public void onSequence() {
        if(this.isAbandoned()) {
            player.hunterTraps.remove(this);
            return;
        }
        if(tick == 10) {
            for (NPC mob : player.getLocalNpcs()) {
                if (mob == null || !mob.isAlive()) {
                    continue;
                }
                if (Misc.getRandomInclusive().nextBoolean() && canCatch(mob) && mob.getPosition().isWithinDistance(getObject().getPosition().copy(), DISTANCE_PORT)) {
                    trap(mob);
                }
            }
            tick = 0;
        }
        tick++;
    }

    @Override
    public Item[] reward() {
        if(trapped == null) {
            throw new IllegalStateException("No npc is trapped.");
        }
        return BirdData.getByID(trapped.getId()).reward;
    }

    @Override
    public double experience() {
        if(trapped == null) {
            throw new IllegalStateException("No npc is trapped.");
        }
        return BirdData.getByID(trapped.getId()).experience;
    }

    @Override
    public boolean canClaim() {
        if(trapped == null || !getState().equals(TrapState.CAUGHT)) {
            return false;
        }

        if(player.getInventory().countFreeSlots() < 4) {
            player.sendMessage("You need atleast 4 free inventory slots to loot this trap!");
            return false;
        }
        return true;
    }

    @Override
    public void setState(TrapState state, NPC mob) {
        if(getState().equals(state)) {
            return;
        }
        if(state.equals(TrapState.CATCHING)) {
            super.setState(state, mob);
            return;
        }
        if(this.getState().equals(TrapState.PENDING) && state.equals(TrapState.PENDING)) {
            throw new IllegalArgumentException("Cannot set trap state back to pending.");
        }
        if(state.equals(TrapState.FALLEN)) {
            this.updateObject(TrapType.BIRD_SNARE.getFailedObjectId());
            mob.getMotion().enqueueStepAwayWithCollisionCheck();
        }
        player.sendMessage("Your trap has been triggered by something...");
        super.setState(state, mob);
    }

}
