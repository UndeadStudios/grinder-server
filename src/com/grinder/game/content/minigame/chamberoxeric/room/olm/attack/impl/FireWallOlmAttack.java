package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class FireWallOlmAttack implements OlmAttack {

    private static final int FIRE_WALL_NPC = 12558;

    private ArrayList<NPC> fireWall = new ArrayList<>();

    private NPC fireWallEast;

    private NPC fireWallWest;

    @Override
    public void execute(NPC npc, Player target) {
        ArrayList<Player> players = npc.getLocalPlayers();

        if (players.size() == 1) {
            return;
        }

        fireWall.clear();

        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        Collections.shuffle(players);

        Player victim = players.get(0);

        victim.getMotion().impairMovement(4);

        int height = victim.getPosition().getZ();

        Position insideFireWall = victim.getPosition().clone();

        TaskManager.submit(new Task(1) {
            int tick = 0;

            @Override
            protected void execute() {
                if (tick >= 2 && (victim.getY() < 5732 || victim.getY() > 5747)) {
                    room.olmNPC.resetAnimation(room);
                    stop();
                    return;
                }

                if (tick == 2) {
                    room.olmNPC.resetAnimation(room);

                    Position pos = victim.getPosition().clone();

                    NPC east = NPCFactory.INSTANCE.create(27558, new Position(3228, pos.getY() + 1, height));

                    NPC west = NPCFactory.INSTANCE.create(27558, new Position(3228, pos.getY() - 1, height));

                    fireWallEast = east;
                    fireWallWest = west;

                    TaskManager.submit(new Task(1) {
                        @Override
                        public void execute() {
                            new Projectile(room.olmNPC, fireWallEast, OlmConfiguration.FIRE_BLAST, 8, 60, 70, 31, 0)
                                    .sendProjectile();
                            new Projectile(room.olmNPC, fireWallWest, OlmConfiguration.FIRE_BLAST, 8, 60, 70, 31, 0)
                                    .sendProjectile();

                            World.getNpcAddQueue().add(fireWallEast);
                            World.getNpcAddQueue().add(fireWallWest);

                            stop();
                        }
                    });
                } else if (tick == 5) {
                    int x = 3229;

                    for (int i = 0; i < 9; i++) {
                        Position position = new Position(x++, victim.getY() - 1, height);

                        NPC wall = NPCFactory.INSTANCE.create(FIRE_WALL_NPC, position);

                        fireWall.add(wall);

                        new Projectile(fireWallEast.getPosition(), position, OlmConfiguration.SMALL_FIRE_BLAST, 8, 60, 30, 31, 0)
                                .sendProjectile();
                    }

                    for (int i = 0; i < 9; i++) {
                        Position position = new Position(x++, victim.getY() + 1, height);

                        NPC wall = NPCFactory.INSTANCE.create(FIRE_WALL_NPC, position);

                        fireWall.add(wall);

                        new Projectile(fireWallWest.getPosition(), position, OlmConfiguration.SMALL_FIRE_BLAST, 8, 60, 30, 31, 0)
                                .sendProjectile();
                    }

                    for (NPC n : fireWall) {
                        World.getNpcAddQueue().add(n);
                    }
                } else if (tick == 10) {
                    for (Player p : players) {
                        if (p.getPosition().getY() == insideFireWall.getY()) {
                            p.getCombat().queue(Damage.create(40, 60));
                        }
                    }
                }

                if (tick == 11) {
                    stop();
                }

                tick++;
            }
        });

    }
}
