package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class CrystalBombsOlmAttack implements OlmAttack {

    @Override
    public void execute(NPC npc, Player target) {
        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        TaskManager.submit(new Task(2) {
            @Override
            protected void execute() {
                room.olmNPC.resetAnimation(room);
                stop();
            }
        });

        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                for (int i = 0; i < 2; i++) {
                    Position pos = OlmConfiguration.randomLocation(target.getPosition().getZ());

                    StaticGameObject bomb = StaticGameObjectFactory.produce(29766, pos, 10, 0);

                    ObjectManager.add(bomb, true);

                    TaskManager.submit(new Task(6) {
                        @Override
                        protected void execute() {
                            ObjectManager.remove(bomb, true);
                            stop();
                        }
                    });

                    new Projectile(room.olmNPC.getPosition(), pos, OlmConfiguration.FALLING_CRYSTAL, 8, 60, 70, 10, 0)
                            .sendProjectile();

                    TaskManager.submit(new Task(7) {
                        @Override
                        public void execute() {
                            target.getPacketSender().sendGlobalGraphic(new Graphic(40, GraphicHeight.MIDDLE), pos);

                            for (Player p : npc.getLocalPlayers()) {
                                if (p.getPosition().sameAs(pos)) {
                                    p.getCombat().queue(Damage.create(50, 70));
                                } else if (p.getPosition().getDistance(pos) == 1) {
                                    p.getCombat().queue(Damage.create(30, 50));
                                } else if (p.getPosition().getDistance(pos) == 2) {
                                    p.getCombat().queue(Damage.create(15, 30));
                                }
                            }

                            stop();
                        }
                    });
                }
                stop();
            }
        });
    }
}
