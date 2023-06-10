package com.grinder.game.model.areas.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.Area;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Zach S <zach@findzach.com>
 * @since 12/15/2020
 * <p>
 * Will handle GrinderScape's Woodcutting Guild
 */
public class WoodcuttingGuildArea extends Area {

    /**
     * The Boundary of the Woodcutting Guild
     *
     * @see Boundary
     */


    /**
     * Calls its Parent & Cleans up the WC guild a tiny bit for us
     */
    public WoodcuttingGuildArea() {
        super(new Boundary(1562, 1657, 3471, 3517));

        loadClippingPosition();
    }


    @Override
    public void process(Agent agent) {
        if (agent instanceof Player) {
            ((Player) agent).getSkillManager().increaseLevelTemporarily(Skill.WOODCUTTING, 7);
        }
    }

    @Override
    public void enter(Agent agent) {
        if (agent instanceof Player) {
            super.enter(agent);
            Player player = (Player) agent;
            if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= 60) {
            }
        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent instanceof Player) {
            super.leave(agent);
            Player player = (Player) agent;
            if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= 60) {
            }
        }
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return false;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        switch (obj.getId()) {
            //Rope ladders up & down
            case ObjectID.ROPE_LADDER_2:
            case ObjectID.ROPE_LADDER_3:
                handleLadder(player, obj);
                return true;

            //Carved RedWood up & down
            case ObjectID.CARVED_REDWOOD_2:
            case ObjectID.CARVED_REDWOOD_3:
                handleRedWood(player, obj);
                return true;

        }
        return false;
    }

    /**
     * Will figure out which ladder and will move the player accordingly.
     *
     * @param player
     * @param object
     */
    private void handleLadder(Player player, GameObject object) {
        int objX = object.getPosition().getX();
        int objY = object.getPosition().getY();

        int z = object.getId() == 28858 ? 0 : 1;

        int x = objX > 1570 ? 1574 : 1567;
        //East Side Ladders
        int y = objY > 3488 ? 3493 : 3483;

        int animId = z == 0 ? 827 : 828;

        String direction = z == 1 ? "up" : "down";

        //If we're going down a level, we need to move forward or back
        x = z == 0 ? ((objX > 1570) ? x + 2 : x - 2) : x;

        player.performAnimation(new Animation(animId));

        player.sendMessage("You climb " + direction + " the ladder!");

        player.BLOCK_ALL_BUT_TALKING = true;
        int finalX = x;
        player.setTeleportingTask(new Task(2) {
            @Override
            protected void execute() {
                player.moveTo(new Position(finalX, y, z));
                player.BLOCK_ALL_BUT_TALKING = false;
                stop();
            }
        });
        TaskManager.submit(player.getTeleportingTask());
    }

    /**
     * Navigates the Player throughout the RedWood stories
     *
     * @param player - The Player
     * @param object - The Carved RedWood
     */
    private void handleRedWood(Player player, GameObject object) {
        //Determines up or down
        int z = object.getId() == 29681 ? 2 : 1;

        //Just a random range to lively things up ;p
        int x = 1570 + Misc.random(1);
        //Determines north or south
        int y = object.getY() > 3487 ? 3489 : 3486;

        //Copied the Pipe Animation - Will look into the actual animation
        player.performAnimation(new Animation(749, 2));

        String direction = z == 2 ? "up" : "down";
        player.sendMessage("You climb " + direction + " the tree!");

        player.BLOCK_ALL_BUT_TALKING = true;

        player.setTeleportingTask(new Task(2) {
            @Override
            protected void execute() {
                player.moveTo(new Position(x, y, z));
                player.BLOCK_ALL_BUT_TALKING = false;
                stop();
            }
        });
        TaskManager.submit(player.getTeleportingTask());
    }

    private static boolean clippingRemoved = false;

    private final List<Position> clipClear = new ArrayList<>();

    /**
     * Clears clipping spots that were unwalkable for whatever reason
     */
    private void firstTimeClipRemoval() {
        if (clippingRemoved) return;

        for (Position pos : clipClear)
            CollisionManager.clearClipping(pos);
        clippingRemoved = true;
    }

    /**
     * Hardcoded Positions that seem to be unwalkable, but should be walkable.
     */
    private void loadClippingPosition() {
        clipClear.add(new Position(1572, 3488, 0));


        clipClear.add(new Position(1649, 3505, 0));

        //Shop lady  so we can trade her
        for (int i = 0; i < 2; i++)
            clipClear.add(new Position(1623 + i, 3500, 0));

        //Corners around the redwood trees seem to have clipping issues
        for (int i = 1; i < 3; i++) {
            clipClear.add(new Position(1567, 3486, i));
            clipClear.add(new Position(1567, 3496, i));
        }

        //Positions near Magic Trees and Redwood Rope Ladders
        clipClear.add(new Position(1576, 3489, 0));
        for (int i = 0; i < 4; i++)
            clipClear.add(new Position(1575, 3488 + i, 0));

        firstTimeClipRemoval();
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }
}
