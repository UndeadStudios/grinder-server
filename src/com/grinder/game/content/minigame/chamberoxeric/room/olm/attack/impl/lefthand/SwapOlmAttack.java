package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Priority;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class SwapOlmAttack implements OlmAttack {

    private static final Graphic WHITE_CIRCLE = new Graphic(OlmConfiguration.WHITE_CIRCLE, GraphicHeight.LOW);

    private static final Graphic TELEPORT = new Graphic(OlmConfiguration.TELEPORT, Priority.LOW);

    private ArrayList<Player> swapList = new ArrayList<>();

    private void performMultiSwap(Player first, Player second) {
        if (first.getPosition() != second.getPosition()) {
            first.moveTo(second.getPosition());
            second.moveTo(first.getPosition());

            if (!(first.getPosition().equals(second.getPosition()))) {
                first.sendMessage("Yourself and " + second.getUsername() + " have swapped places!");

                second.sendMessage("Yourself and " + first.getUsername() + " have swapped places!");

                if (first.getPosition().getDistance(second.getPosition()) == 1) {
                    first.getCombat().queue(Damage.create(10));
                    second.getCombat().queue(Damage.create(10));
                } else if (first.getPosition().getDistance(second.getPosition()) == 2) {
                    first.getCombat().queue(Damage.create(10, 20));
                    second.getCombat().queue(Damage.create(10, 20));
                } else {
                    first.getCombat().queue(Damage.create(20, 33));
                    second.getCombat().queue(Damage.create(20, 33));
                }

                second.performGraphic(TELEPORT);
                first.performGraphic(TELEPORT);
            } else {
                first.sendMessage("The teleport attack has no effect!");
                second.sendMessage("The teleport attack has no effect!");
            }
        }
    }
    private static void performSingleSwap(Player player) {
        Position pos = OlmConfiguration.randomLocation(player.getPosition().getZ());

        if (player.getPosition() != pos) {
            player.sendMessage("As you had no pairing... you are taken to a random spot.");

            if (player.getPosition().getDistance(pos) == 1) {
                player.getCombat().queue(Damage.create(10));
            } else if (player.getPosition().getDistance(pos) == 2) {
                player.getCombat().queue(Damage.create(10, 20));
            } else {
                player.getCombat().queue(Damage.create(20, 33));
            }

            player.performGraphic(TELEPORT);

            player.moveTo(pos);
        } else {
            player.sendMessage("The teleport attack has no effect!");
        }
    }

    @Override
    public void execute(NPC npc, Player target) {

        target.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.FLASHING_CIRCLES_LEFT_HAND);

        swapList.clear();

        swapList.addAll(npc.getLocalPlayers());

        for (Player p : swapList) {
            target.getPacketSender().sendGlobalGraphic(WHITE_CIRCLE, p.getPosition());
        }

        for (int i = 0; i < 6; i += 2) {
            if (i == 0 && swapList.size() >= 2 || i > 0 && swapList.size() >= (i * 2)) {
                swapList.get(i).sendMessage("You have been paired with @red@" + swapList.get(i + 1).getUsername() + "</col>! The magical power will enact soon...");
            } else {
                if (swapList.size() - 1 >= i) {
                    swapList.get(i).sendMessage("The Great Olm had no one to pair you with! The magical power will enact soon...");
                }
            }
        }

        for (int i = 1; i < 6; i += 2) {
            if (i == 1 && swapList.size() >= 2 || i > 1 && swapList.size() >= (i * 2)) {
                swapList.get(i).sendMessage("You have been paired with @red@" + swapList.get(i - 1).getUsername() + "</col>! The magical power will enact soon...");
            } else {
                if (swapList.size() - 1 >= i) {
                    swapList.get(i).sendMessage("The Great Olm had no one to pair you with! The magical power will enact soon...");
                }
            }
        }

        TaskManager.submit(new Task(4) {
            @Override
            protected void execute() {
                for (Player p : swapList) {
                    target.getPacketSender().sendGlobalGraphic(WHITE_CIRCLE, p.getPosition());
                }
                stop();
            }
        });

        TaskManager.submit(new Task(7) {
            @Override
            protected void execute() {
                for (Player p : swapList) {
                    target.getPacketSender().sendGlobalGraphic(WHITE_CIRCLE, p.getPosition());
                }
                stop();
            }
        });

        TaskManager.submit(new Task(9) {
            @Override
            protected void execute() {
                for (int i = 0; i < 6; i += 2) {
                    if (i == 0 && swapList.size() >= 2 || i > 0 && swapList.size() >= (i * 2)) {
                        Player first = swapList.get(i);
                        Player second = swapList.get(i + 1);

                        performMultiSwap(first, second);
                    } else {
                        if (swapList.size() - 1 >= i) {
                            performSingleSwap(swapList.get(i));
                        }
                    }
                }

                target.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.LEFT_HAND);

                stop();
            }
        });
    }
}
