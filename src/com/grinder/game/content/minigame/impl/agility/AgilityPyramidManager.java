package com.grinder.game.content.minigame.impl.agility;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AgilityPyramidManager extends Area {

    /**
     * A list of players currently in this area.
     */
    public static Set<Player> playersInArea = new HashSet<>();

    public AgilityPyramidManager() {
        super(new Boundary(3348, 3380, 2826, 2856), new Boundary(3038, 3052, 4690, 4708));
        start();
    }

    private void start() {
        TaskManager.submit(new Task(1) {
            int step = 0;

            @Override
            protected void execute() {
                for (Player player : playersInArea) {
                    PacketSender playerPacketSender = player.getPacketSender();

                    /* Moving stone block, makes it vanish but no animation
                    if (step == 0)
                        playerPacketSender.sendVarbit(1550, 1);

                    if (step == 3)
                        playerPacketSender.sendVarbit(1550, 0);
                     */

                    //Check if failed last step, if so teleport and deal damage..
                    if (EntityExtKt.getInt(player, Attribute.DOING_PYRAMID_STEP, 0) == step &&
                            EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_DAMAGE, false) && player.agilityFailGoToLocation != null) {
                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_DAMAGE, false, false);
                        player.moveTo(player.agilityFailGoToLocation);
                        player.getCombat().queue(new Damage(6, DamageMask.REGULAR_HIT));
                        playerPacketSender.sendVarbit(1550, 0);
                        playerPacketSender.sendVarbit(1551, 0);
                        playerPacketSender.sendVarbit(1552, 0);
                        playerPacketSender.sendVarbit(1553, 0);
                        playerPacketSender.sendVarbit(1554, 0);
                        playerPacketSender.sendVarbit(1555, 0);
                        continue;
                    }

                    //Check if doing current jump and if at same step to prevent code running twice
                    if (EntityExtKt.getInt(player, Attribute.DOING_PYRAMID_STEP, 0) == step &&
                            EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {
                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, false, false);
                        playerPacketSender.sendVarbit(1550, 0);
                        playerPacketSender.sendVarbit(1551, 0);
                        playerPacketSender.sendVarbit(1552, 0);
                        playerPacketSender.sendVarbit(1553, 0);
                        playerPacketSender.sendVarbit(1554, 0);
                        playerPacketSender.sendVarbit(1555, 0);
                        continue;
                    }

                    int playerX = player.getPosition().getX();
                    int playerY = player.getPosition().getY();

                    //Set which side the player came from before stepping on the block to determine what direction the player should jump
                    if ((playerX >= 3354 && playerX <= 3355 &&
                            (playerY == 2840 || playerY == 2839)) ||

                            (playerX >= 3374 && playerX <= 3375 &&
                                    (playerY == 2833 || playerY == 2834)) ||

                            (playerX >= 3048 && playerX <= 3049 &&
                                    (playerY == 4697 || playerY == 4698))) {
                        player.cameFromDirection = Direction.SOUTH;
                    }
                    if ((playerX >= 3354 && playerX <= 3355 &&
                            (playerY == 2843 || playerY == 2844)) ||

                            (playerX >= 3374 && playerX <= 3375 &&
                                    (playerY == 2837 || playerY == 2838)) ||

                            (playerX >= 3048 && playerX <= 3049 &&
                                    (playerY == 4701 || playerY == 4702))) {
                        player.cameFromDirection = Direction.NORTH;
                    }
                    if ((playerX >= 3366 && playerX <= 3367 &&
                            (playerY == 2849 || playerY == 2850)) ||

                            (playerX >= 3046 && playerX <= 3047 &&
                                    (playerY == 4699 || playerY == 4700))) {
                        player.cameFromDirection = Direction.WEST;
                    }
                    if ((playerX >= 3370 && playerX <= 3371 &&
                            (playerY == 2849 || playerY == 2850)) ||

                            (playerX >= 3042 && playerX <= 3043 &&
                                    (playerY == 4699 || playerY == 4700))) {
                        player.cameFromDirection = Direction.EAST;
                    }

                    boolean didFail = false;
                    Position moveTo = null;
                    Position teleTo = null;
                    int chance = (int)((float) (player.getSkillManager().getCurrentLevel(Skill.AGILITY)/99f) * 50) + Misc.random(15);
                    int directionToFace = 0;

                    if (chance <= Misc.random(50)) {
                        didFail = true;
                    }

                    //Stones - missing object animations

                    //check if player is now on stones, if so, use previous direction to jump over or fail
                    if (playerX >= 3354 && playerX <= 3355 &&
                            playerY >= 2841 && playerY <= 2842 &&
                            !EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {

                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, true, false);
                        EntityExtKt.setInt(player, Attribute.DOING_PYRAMID_STEP, step, 0);

                        //Do object animation here... needs fixing
                        playerPacketSender.sendVarbit(1551, 1);

                        playerPacketSender.sendSound(1396);

                        if (player.cameFromDirection == Direction.SOUTH)
                            moveTo = new Position(0, 2843 - playerY);
                        else {
                            directionToFace = 2;
                            moveTo = new Position(0, 2840 - playerY);
                        }

                        if (didFail) {
                            directionToFace = 3;
                            moveTo = new Position(3353 - playerX, 2841 - playerY);
                            teleTo = new Position(3353, 2841, 0);
                        }
                    }

                    if (playerX >= 3374 && playerX <= 3375 &&
                            playerY >= 2835 && playerY <= 2836 &&
                            !EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {

                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, true, false);
                        EntityExtKt.setInt(player, Attribute.DOING_PYRAMID_STEP, step, 0);
                        playerPacketSender.sendVarbit(1553, 1);
                        playerPacketSender.sendSound(1396);

                        if (player.cameFromDirection == Direction.SOUTH)
                            moveTo = new Position(0, 2837 - playerY);
                        else {
                            directionToFace = 2;
                            moveTo = new Position(0, 2834 - playerY);
                        }

                        if (didFail) {
                            directionToFace = 1;
                            moveTo = new Position(3376 - playerX, 2835 - playerY);
                            teleTo = new Position(3376, 2835, 0);
                        }
                    }

                    if (playerX >= 3048 && playerX <= 3049 &&
                            playerY >= 4699 && playerY <= 4700 &&
                            !EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {

                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, true, false);
                        EntityExtKt.setInt(player, Attribute.DOING_PYRAMID_STEP, step, 0);

                        playerPacketSender.sendVarbit(1554, 1);
                        playerPacketSender.sendSound(1396);

                        if (player.cameFromDirection == Direction.SOUTH) {
                            moveTo = new Position(0, 4701 - playerY);
                        } else {
                            directionToFace = 2;
                            moveTo = new Position(0, 4698 - playerY);
                        }

                        if (didFail) {
                            directionToFace = 1;
                            moveTo = new Position(3050 - playerX, 4700 - playerY);
                            teleTo = new Position(3370, 2844, 3);
                        }
                    }

                    if (playerX >= 3368 && playerX <= 3369 &&
                            playerY >= 2849 && playerY <= 2850 &&
                            !EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {

                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, true, false);
                        EntityExtKt.setInt(player, Attribute.DOING_PYRAMID_STEP, step, 0);
                        playerPacketSender.sendVarbit(1551, 1);
                        playerPacketSender.sendSound(1396);

                        if (player.cameFromDirection == Direction.WEST) {
                            moveTo = new Position(3370 - playerX, 0);
                            directionToFace = 1;
                        } else {
                            directionToFace = 3;
                            moveTo = new Position(3367 - playerX, 0);
                        }

                        if (didFail) {
                            directionToFace = 0;
                            moveTo = new Position(3369 - playerX, 2851 - playerY);
                            teleTo = new Position(3369, 2851, 1);
                        }
                    }

                    if (playerX >= 3044 && playerX <= 3045 &&
                            playerY >= 4699 && playerY <= 4700 &&
                            !EntityExtKt.getBoolean(player, Attribute.DOING_PYRAMID_JUMP, false)) {

                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_JUMP, true, false);
                        EntityExtKt.setInt(player, Attribute.DOING_PYRAMID_STEP, step, 0);
                        playerPacketSender.sendVarbit(1555, 1);
                        playerPacketSender.sendSound(1396);

                        if (player.cameFromDirection == Direction.WEST) {
                            moveTo = new Position(3043 - playerX, 0);
                            directionToFace = 3;
                        } else {
                            directionToFace = 1;
                            moveTo = new Position(3046 - playerX, 0);
                        }

                        if (didFail) {
                            directionToFace = 0;
                            moveTo = new Position(3044 - playerX, 4701 - playerY);
                            teleTo = new Position(3044, 4701, 2);
                        }
                    }

                    //Run animation code for jumping / failing
                    if (!didFail && moveTo != null) {
                        //Successful jump
                        TaskManager.submit(new ForceMovementTask(player, 1,
                                new ForceMovement(player.getPosition().clone(), moveTo, 1, 20,
                                        directionToFace, 1115)));
                    } else if (moveTo != null) {
                        //Failed, fall to move to location..
                        TaskManager.submit(new ForceMovementTask(player, 2,
                                new ForceMovement(player.getPosition().clone(), moveTo, 2, 20,
                                        directionToFace, 3064)));
                        player.agilityFailGoToLocation = teleTo;
                        EntityExtKt.setBoolean(player, Attribute.DOING_PYRAMID_DAMAGE, true, false);
                    }
                }

                step++;
                if (step > 2)
                    step = 0;
            }
        });
    }

    @Override
    public void enter(Agent agent) {

        if (agent instanceof Player) {
            super.enter(agent);
            final Player player = (Player) agent;

            playersInArea.add(player);

            if (EntityExtKt.getBoolean(player, Attribute.GRABBED_PYRAMID_TOP, false)) {
                DynamicGameObject updatedObj = DynamicGameObject.createLocal(10870, new Position(3044, 4697, 3), 10, 0);
                updatedObj.addTo(player);
                ObjectManager.add(updatedObj, false);
            }

        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent instanceof Player) {

            super.leave(agent);
            final Player player = (Player) agent;
            playersInArea.remove(player);
        }
    }

    @Override
    public void process(Agent agent) {
        if (agent instanceof Player) {
        }
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        if (obj.getId() == ObjectID.CLIMBING_ROCKS_2) {
            player.setPositionToFace(new Position(3043, 4697));
            player.forceAnimation(new Animation(3063));
            player.playSound(new Sound(2454));

            TaskManager.submit(2, () -> {
                if (EntityExtKt.getBoolean(player, Attribute.GRABBED_PYRAMID_TOP, false)) {
                    player.sendMessage("You find nothing on top of the pyramid.");
                    return;
                }
                player.getInventory().add(new Item(6970, 1));
                DialogueBuilder db = new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER);
                db.setItem(6970, 175);
                db.setText("You find a golden pyramid!");
                db.start(player);
                DynamicGameObject updatedObj = DynamicGameObject.createLocal(10870, new Position(3044, 4697, 3), 10, 0);
                updatedObj.addTo(player);
                ObjectManager.add(updatedObj, false);
                EntityExtKt.setBoolean(player, Attribute.GRABBED_PYRAMID_TOP, true, false);
            });
            return true;

        } else if (obj.getId() == ObjectID.STAIRS_50 && obj.getPosition().sameAs(new Position(3042, 4695, 2))) {
            player.forceAnimation(new Animation(828));

            TaskManager.submit(new Task(1) {
                int step = 0;

                @Override
                protected void execute() {
                    if (step == 1) {
                        player.moveTo(new Position(3043, 4697, 3));
                    } else if (step == 2) {
                        if (!EntityExtKt.getBoolean(player, Attribute.GRABBED_PYRAMID_TOP, false)) {
                            DynamicGameObject updatedObj = DynamicGameObject.createLocal(10869, new Position(3044, 4697, 3), 10, 0);
                            updatedObj.addTo(player);
                            ObjectManager.add(updatedObj, false);
                        }
                        stop();
                    }
                    step++;
                }
            });
            return true;


        //Set grabbed back to false once clicked stairs and at ground level 0
        } else if (obj.getId() == ObjectID.STAIRS_50 && player.getPosition().getZ() == 0) {
            EntityExtKt.setBoolean(player, Attribute.GRABBED_PYRAMID_TOP, false, false);
        } else if (obj.getId() == ObjectID.DOORWAY_15 || obj.getId() == ObjectID.DOORWAY_16) {
            player.moveTo(new Position(3364, 2830, 0));
            return true;
        } else if (obj.getId() == ObjectID.STAIRS_51 && obj.getPosition().sameAs(new Position(3040, 4693, 2))) {
            player.moveTo(new Position(3361, 2836, 3));
            return true;
        }

        //Entering this area seems to be bugged, this fixes it..
        if (obj.getId() == ObjectID.CLIMBING_ROCKS_5) {
            if (player.getPosition().getX() <= 3350)
                enter(player);
        }
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return true;
    }
}
