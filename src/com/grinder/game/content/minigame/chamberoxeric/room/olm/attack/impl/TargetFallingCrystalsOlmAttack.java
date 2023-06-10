package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class TargetFallingCrystalsOlmAttack implements OlmAttack {

    private boolean initiated;

    @Override
    public void execute(NPC npc, Player target) {
        ArrayList<Player> players = npc.getLocalPlayers();

        if(players.size() == 0) {
            return;
        }

        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        Collections.shuffle(players);

        Player victim = players.get(0);

        if(victim == null) {
            return;
        }

        initiated = true;

        int height = victim.getPosition().getZ();

        Task task = new Task(1) {

            int tick = 0;

            @Override
            protected void execute() {
                if(tick == 1) {
                    room.olmNPC.resetAnimation(room);
                }

                if(tick % 2 == 0) {
                    Position pos = victim.getPosition().clone();

                    Position rightCrystal = new Position(pos.getX(), pos.getY() - 1, height);

                    new Projectile(pos, rightCrystal, OlmConfiguration.CRYSTAL, 80, 55,240, 0, 0).sendProjectile();

                    TaskManager.submit(new Task(5) {
                        @Override
                        protected void execute() {
                            victim.getPacketSender().sendGlobalGraphic(new Graphic(OlmConfiguration.LEFTOVER_CRYSTALS, GraphicHeight.MIDDLE), pos);

                            for(Player p : players) {
                                if(p.getPosition().sameAs(rightCrystal)) {
                                    p.getCombat().queue(Damage.create(20, 30));
                                    p.getPacketSender().sendMessage("The falling crystal shatters into you.");
                                } else if(p.getPosition().isWithinDistance(rightCrystal, 1)) {
                                    p.getCombat().queue(Damage.create(10, 20));
                                    p.getPacketSender().sendMessage("The falling crystal shatters into you.");
                                } else if(p.getPosition().isWithinDistance(rightCrystal, 2)) {
                                    p.getCombat().queue(Damage.create(5, 10));
                                    p.getPacketSender().sendMessage("The falling crystal shatters into you.");
                                }
                            }
                            stop();
                        }
                    });
                }

                if(tick == 20) {
                    initiated = false;
                    room.startedFallingCrystals = false;
                    stop();
                }

                tick++;
            }
        };

        victim.instance.addTask(task);

        TaskManager.submit(task);
    }
}
