package com.grinder.game.content.skill.skillable.impl.hunter_new.traps.box;

import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.random.RandomUtil;

import static com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter.getMaximumTraps;
import static com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter.lay;

/**
 * The box trap implementation of the {@link com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap} class which represents a single box trap.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class BoxTrap extends HunterTrap {

    public static boolean ENABLED = true;

    /**
     * Constructs a new {@link BoxTrap}.
     * @param player {@link #getPlayer()}.
     */
    public BoxTrap(Player player) {
        super(player, TrapType.BOX_TRAP);
    }

    /**
     * The npc trapped inside this box.
     */
    private NPC trapped;

    /**
     * Determines if an animal is going to the trap.
     */
    private Task event;

    /**
     * The object identification for a caught box trap.
     */
    private static final int CAUGHT_ID = 9384;

    /**
     * The distance the npc has to have from the box trap before it gets triggered.
     */
    private static final int DISTANCE_PORT = 10;

    /**
     * Kills the specified {@code mob}.
     * @param mammal the mob to kill.
     */
    private void kill(NPC mammal) {
        mammal.moveTo(new Position(0, 0, 0));
        mammal.appendDeath();
        trapped = mammal;
    }

    public static void configure() {
        configureBoxTrap();
        ObjectActions.INSTANCE.onClick(new int[]{CAUGHT_ID}, action -> {
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

            BoxData data = BoxData.getByID(((BoxTrap) trap).trapped.getId());

            for(Item item : data.reward) {
                player.getInventory().add(item);
            }

            player.performAnimation(new Animation(827));
            trap.onPickUp();
            PetHandler.onSkill(player, Skill.HUNTER);
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{TrapType.BOX_TRAP.getFailedObjectId(), TrapType.BOX_TRAP.getObjectId()}, action -> {
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

    public static void configureBoxTrap() {
        ItemActions.INSTANCE.onClick(new int[]{TrapType.BOX_TRAP.getItemId()}, action -> {
            if(action.isInInventory() && !action.isDropAction()) {
                lay(action.getPlayer(), new BoxTrap(action.getPlayer()));
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
        return BoxData.VALUES.stream().anyMatch(b -> b.npcId == mob.getId());
    }

    @Override
    public void onPickUp() {
        remove();
        player.getInventory().add(getType().getItemId(), 1);
        player.sendMessage("You pick up your box trap.");
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
        player.sendMessage("You set-up your box trap.");
    }

    @Override
    public void onCatch(NPC mob) {
        if(event != null || !getState().equals(TrapState.PENDING)) {
            return;
        }

        if(RandomUtil.getRandomInclusive(100) < 80) {
            return;
        }

        BoxData data = BoxData.getByID(mob.getId());

        setState(TrapState.CATCHING, mob);

        BoxTrap trap = this;

        event = new Task(1, false) {

            @Override
            public void execute() {
                mob.getMotion().clearAndEnqueue(getObject().getPosition().copy());

                if(!getState().equals(TrapState.CATCHING)) {
                    this.stop();
                    return;
                }

                if(isAbandoned() || !player.hunterTraps.contains(trap)) {
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
                    updateObject(CAUGHT_ID);
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
        return BoxData.getByID(trapped.getId()).reward;
    }

    @Override
    public double experience() {
        if(trapped == null) {
            throw new IllegalStateException("No npc is trapped.");
        }
        return BoxData.getByID(trapped.getId()).experience;
    }

    @Override
    public boolean canClaim() {
        if(trapped == null || !getState().equals(TrapState.CAUGHT)) {
            return false;
        }
        if(player.getInventory().countFreeSlots() < 2) {
            player.sendMessage("You need atleast 2 free inventory slots to loot this trap!");
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
            this.updateObject(TrapType.BOX_TRAP.getFailedObjectId());
            mob.getMotion().enqueueStepAwayWithCollisionCheck();
        }
        player.sendMessage("Your trap has been triggered by something...");
        super.setState(state, mob);
    }

}
