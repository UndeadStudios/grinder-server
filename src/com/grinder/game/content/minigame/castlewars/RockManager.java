package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.skill.skillable.impl.Mining;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.*;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.AtomicInteger;
import com.grinder.util.Misc;

import java.util.stream.Stream;

import static com.grinder.game.content.minigame.castlewars.CastleWars.*;
import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;

public class RockManager {
    protected static void mineRock(final Player player, final int objectID, final int objectX, final int objectY, final int height, final int direction, final int type) {
        if (Mining.findPickaxe(player).isEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You need a pickaxe to mine this rock. You do not have a pickaxe",
                            "which you have the Mining level to use.")
                    .start(player);
            return;
        }

        var pickaxe = Mining.findPickaxe(player).get();
        player.performAnimation(pickaxe.getSecondAnimation());

        player.sendMessage("You swing your pick at the rock.");

        Position playerStartPosition = player.getPosition();

        var attemptsTillMine = new AtomicInteger(Misc.random(-3, 3));
        if (attemptsTillMine.getValue() <= 0) {
            attemptsTillMine.setValue(1);
        }
        var level = player.getSkillManager().getCurrentLevel(Skill.MINING);
        var pickaxeSpeed = pickaxe.getSpeed();
        var factor = (int) (pickaxeSpeed * 120);
        var miningSpeed = Misc.random(2, factor);

        TaskManager.submit(new Task(3) {

            @Override
            protected void execute() {
                if (!player.getPosition().equals(playerStartPosition)) {
                    stop();
                    return;
                }
                player.performAnimation(pickaxe.getSecondAnimation());

                if (getRockClearedState(objectX, objectY) == RockState.CLEARED) {
                    player.resetAnimation();
                    stop();
                }

            }
        });

        TaskManager.submit(new Task(miningSpeed) {
            int rockID = objectID;
            @Override
            protected void execute() {

                if (!player.getPosition().equals(playerStartPosition)) {
                    stop();
                    return;
                }
                if (attemptsTillMine.getValue() == 0) {
                    player.getPacketSender().sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND);

                    if (rockID == 4437) {
                        World.addObject(DynamicGameObject.createPublic(4438, new Position(objectX, objectY, height), type, direction));
                        player.sendMessage("You manage to clear the some of the rocks.");
                        rockID++;
                    } else {
                        World.addObject(DynamicGameObject.createPublic(-1, new Position(objectX, objectY, height), type, direction));

                        CollisionManager.clearClipping(new Position(objectX, objectY, height));
                        CollisionManager.clearClipping(new Position(objectX+1, objectY, height));
                        CollisionManager.clearClipping(new Position(objectX, objectY+1, height));
                        CollisionManager.clearClipping(new Position(objectX+1, objectY+1, height));

                        setRockClearedState(objectX, objectY);
                        player.sendMessage("You manage to clear the rest of the rocks.");

                        player.resetAnimation();
                        stop();
                    }


                } else {
                    attemptsTillMine.remove(1);
                }
            }
        }.bind(player));


    }

    protected static void mineWall(final Player player, final int objectX, final int objectY) {
        if (Mining.findPickaxe(player).isEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You need a pickaxe to mine this rock. You do not have a pickaxe",
                            "which you have the Mining level to use.")
                    .start(player);
            return;
        }

        var pickaxe = Mining.findPickaxe(player).get();
        player.performAnimation(pickaxe.getSecondAnimation());

        player.sendMessage("You swing your pick at the rock.");

        Position playerStartPosition = player.getPosition();

        var attemptsTillMine = new AtomicInteger(Misc.random(-3, 3));
        if (attemptsTillMine.getValue() <= 0) {
            attemptsTillMine.setValue(1);
        }
        var level = player.getSkillManager().getCurrentLevel(Skill.MINING);
        var pickaxeSpeed = pickaxe.getSpeed();
        var factor = (int) (pickaxeSpeed * 120);
        var miningSpeed = Misc.random(2, factor);

        TaskManager.submit(new Task(3) {

            @Override
            protected void execute() {
                if (!player.getPosition().equals(playerStartPosition)) {
                    stop();
                    return;
                }
                player.performAnimation(pickaxe.getSecondAnimation());

                if (getRockClearedState(objectX, objectY) == RockState.COLLAPSED) {
                    player.resetAnimation();

                    stop();
                }

            }
        });

        TaskManager.submit(new Task(miningSpeed) {
            @Override
            protected void execute() {

                if (!player.getPosition().equals(playerStartPosition)) {
                    stop();
                    return;
                }
                if (attemptsTillMine.getValue() == 0) {
                    player.getPacketSender().sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND);
                    collapseRock(objectX, objectY);
                    player.sendMessage("You've collapsed the tunnel!");
                    setRockCollapsedState(objectX, objectY);
                    stop();


                } else {
                    attemptsTillMine.remove(1);
                }
            }
        }.bind(player));
    }

    protected static void setRockClearedState(int rockX, int rockY) {

        int rockID = -1;
        int rockTeam = -1;
        if (rockX == 2391 && rockY == 9501) {//Zamorak West rocks
            rockID = 0;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2400 && rockY == 9512) {//Zamorak North rocks
            rockID = 1;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2401 && rockY == 9494) {//Saradomin east rocks
            rockID = 0;
            rockTeam = SARADOMIN_TEAM;
        } else if (rockX == 2409 && rockY == 9503) {//Saradomin south rocks
            rockID = 1;
            rockTeam = SARADOMIN_TEAM;
        }
        if (rockID != -1 && rockTeam != -1) {
            rockState[rockTeam][rockID] = RockState.CLEARED;
        }
        setUpdateInterface(true);
    }

    protected static void setRockCollapsedState(int rockX, int rockY) {

        int rockID = -1;
        int rockTeam = -1;
        if (rockX == 2391 && rockY == 9501) {//Zamorak West rocks
            rockID = 0;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2400 && rockY == 9512) {//Zamorak North rocks
            rockID = 1;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2401 && rockY == 9494) {//Saradomin east rocks
            rockID = 0;
            rockTeam = SARADOMIN_TEAM;
        } else if (rockX == 2409 && rockY == 9503) {//Saradomin south rocks
            rockID = 1;
            rockTeam = SARADOMIN_TEAM;
        }
        if (rockID != -1 && rockTeam != -1) {
            rockState[rockTeam][rockID] = RockState.COLLAPSED;
        }
        setUpdateInterface(true);
    }

    protected static RockState getRockClearedState(int rockX, int rockY) {

        int rockID = -1;
        int rockTeam = -1;
        if (rockX == 2391 && rockY == 9501) {//Zamorak West rocks
            rockID = 0;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2400 && rockY == 9512) {//Zamorak North rocks
            rockID = 1;
            rockTeam = ZAMORAK_TEAM;
        } else if (rockX == 2401 && rockY == 9494) {//Saradomin east rocks
            rockID = 0;
            rockTeam = SARADOMIN_TEAM;
        } else if (rockX == 2409 && rockY == 9503) {//Saradomin south rocks
            rockID = 1;
            rockTeam = SARADOMIN_TEAM;
        }
        if (rockID != -1 && rockTeam != -1) {
            return rockState[rockTeam][rockID];
        }
        return null;
    }

    static void collapseRock(int objectX, int objectY) {

        World.addObject(DynamicGameObject.createPublic(4437, new Position(objectX, objectY, 0), 10, 0));
        smashPlayers(objectX, objectY);

    }

    protected static void collapseAllRocks() {

        collapseRock(2409, 9503);
        collapseRock(2401, 9494);
        collapseRock(2391, 9501);
        collapseRock(2400, 9512);
        for (int rockTeam = 0; rockTeam < rockState.length; rockTeam++) {
            for (int rockID = 0; rockID < rockState[rockTeam].length; rockID++) {
                rockState[rockTeam][rockID] = CastleWars.RockState.COLLAPSED;
            }
        }
        setUpdateInterface(true);

    }

    protected static boolean isUnderRock(int objectX, int objectY, int absX, int absY) {
        return absX >= objectX && absX <= objectX + 1 && absY >= objectY && absY <= objectY + 1;
    }

    private static void smashPlayers(int objectX, int objectY) {
        Stream.of(zamorakParty.getPlayers(), saradominParty.getPlayers()).parallel().forEach(partyPlayers -> {
            for (Player player : partyPlayers) {
                if (player.getPosition().getZ() == 0) {
                    if (isUnderRock(objectX, objectY, player.getPosition().getX(), player.getPosition().getY())) {
                        player.getCombat().queue(new Damage(player.getSkills().getLevel(Skill.HITPOINTS), DamageMask.REGULAR_HIT));
                    }
                }
            }
        });
    }

    public static int explodeRock(Player player, int rockID, int objectX, int objectY, int height) {

        if (rockID == 4437) {
            World.addObject(DynamicGameObject.createPublic(4438, new Position(objectX, objectY, height), 10, 0));
            return 4437;
        } else if (rockID == 4438) {
            World.addObject(DynamicGameObject.createPublic(-1, new Position(objectX, objectY, height), 10, 0));
            CollisionManager.clearClipping(new Position(objectX, objectY, height));
            CollisionManager.clearClipping(new Position(objectX+1, objectY, height));
            CollisionManager.clearClipping(new Position(objectX, objectY+1, height));
            CollisionManager.clearClipping(new Position(objectX+1, objectY+1, height));

            setRockClearedState(objectX, objectY);
            return 4438;
        }
        return -1;
    }
}
