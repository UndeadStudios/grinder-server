package com.grinder.game.content.skill.skillable.impl.hunter_new;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.hunter_new.catching.Impling;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.bird.BirdSnare;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.box.BoxTrap;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.*;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

/**
 * The class which holds static functionality for the hunter skill.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Hunter {

    /**
     * Retrieves the maximum amount of traps a player can lay.
     * @param player the player to lay a trap down for.
     * @return a numerical value determining the amount a player can lay.
     */
    public static int getMaximumTraps(Player player) {
        int hunterLevel = player.getSkills().getLevel(Skill.HUNTER);
        return hunterLevel / 20 + 1;
    }

    private static boolean inHunterArea(Position position) {
        return ((position.getX() >= 3119 && position.getX() <= 3172) && (position.getY() >= 3754 && position.getY() <= 3792))
                ||
                ((position.getX() >= 2688 && position.getX() <= 2751) && (position.getY() >= 3741 && position.getY() <= 3810))
                ||
                ((position.getX() >= 2491 && position.getX() <= 2615) && (position.getY() >= 2875 && position.getY() <= 2939));
    }

    public static void configure() {
        BirdSnare.configure();
        BoxTrap.configure();
        configureWheatPassage();
        Impling.configure();
    }

    public static void clearTraps(Player player, boolean logout) {
        if(logout) {
            player.hunterTraps.forEach(t -> t.abandon(true));
            return;
        }

        player.hunterTraps.forEach(t -> {
            if(!player.getPosition().isWithinDistance(t.getObject().getPosition(), 35)) {
                t.abandon(false);
            }
        });
    }

    public static void sequence(Player player) {
        if(player.hunterTraps.isEmpty()) {
            return;
        }

        player.hunterTraps.forEach(HunterTrap::onSequence);
    }

    /**
     * Attempts to lay down the specified {@code trap} for the specified {@code player}.
     * @param player the player to lay the trap for.
     * @param trap the trap to lay down for the player.
     * @return {@code true} if the trap was laid, {@code false} otherwise.
     */
    public static boolean lay(Player player, HunterTrap trap) {
        if(trap.getType().equals(HunterTrap.TrapType.BOX_TRAP) && !BoxTrap.ENABLED) {
            player.sendMessage("Box traps are currently disabled.");
            return true;
        }

        if(trap.getType().equals(HunterTrap.TrapType.BIRD_SNARE) && !BirdSnare.ENABLED) {
            player.sendMessage("Bird snares are currently disabled.");
            return true;
        }

        Position p = player.getPosition().copy();

        if (ObjectManager.findDynamicObjectAt(p, gameObject -> gameObject.getPosition().equals(p) && Misc.objectTypesCollide(gameObject.getObjectType(), 10)).isPresent()) {
            player.sendMessage("You can't setup a trap here. Try moving around a bit.", 1000);
            return true;
        }

        if (!inHunterArea(p)) {
            player.sendMessage("You can only do this at the Hunter area.");
            return true;
        }

        for(NPC mob : player.getLocalNpcs()) {
            if(mob == null) {
                continue;
            }
            if(mob.getPosition().equals(p) || mob.getSpawnPosition().equals(p)) {
                player.sendMessage("You can't set-up your trap here.");
                return false;
            }
        }

        if(!trap.canLay()) {
            return false;
        }

        trap.submit();
        return true;
    }

    public static void configureWheatPassage() {
        ObjectActions.INSTANCE.onClick(new int[]{25029}, action -> {
            Player player = action.getPlayer();
            int x = player.getPosition().getX(), x2 = x;
            int y = player.getPosition().getY(), y2 = y;
            if (x == 2584) {
                x2 = 2582;
            } else if (x == 2582) {
                x2 = 2584;
            } else if (x == 2599) {
                x2 = 2601;
            } else if (x == 2601) {
                x2 = 2599;
            }
            if (y == 4312) {
                y2 = 4310;
            } else if (y == 4310) {
                y2 = 4312;
            } else if (y == 4327) {
                y2 = 4329;
            } else if (y == 4329) {
                y2 = 4327;
            }
            player.getPacketSender().sendMessage("You use your strength to push through the wheat.");
            player.BLOCK_ALL_BUT_TALKING = true;
            SkillUtil.stopSkillable(player);
            final int goX = x2, goY = y2;
            TaskManager.submit(new Task(1, player, false) {
                int tick = 0;

                @Override
                protected void execute() {
                    if (tick == 1) {
                        BasicAnimationSet wheat_push_anim_set = new BasicAnimationSet(808, 823, 6594, 820, 821, 822, 6594);
                        player.getAppearance().setBas(wheat_push_anim_set);
                        player.updateAppearance();
                        player.getMotion().enqueuePathToWithoutCollisionChecks(goX, goY);
                        player.BLOCK_ALL_BUT_TALKING = false;
                    } else if (tick == 2) {
                        stop();
                        return;
                    }
                    tick++;
                }

                @Override
                public void onStop() {
                    player.resetBas();
                    player.updateAppearance();
                }
            });
            return true;
        });
    }
}
