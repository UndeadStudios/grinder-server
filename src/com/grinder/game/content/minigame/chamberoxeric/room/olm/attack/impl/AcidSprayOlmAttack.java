package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.event.impl.PoisonEvent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class AcidSprayOlmAttack implements OlmAttack {

    private static final int ACID_OBJECT = 30032;

    private ArrayList<Position> acidSpots = new ArrayList<>();

    @Override
    public void execute(NPC npc, Player target) {

        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        acidSpots.clear();

        for (int i = 0; i < 10; i++) {
            Position pos = OlmConfiguration.randomLocation(target.getPosition().getZ());

            acidSpots.add(pos);

            new Projectile(room.olmNPC.getPosition(), pos, 0, OlmConfiguration.DARK_GREEN_SMALL_PROJECTILE, 55, 8, 70, 0, 0, 3, 0).sendProjectile();
        }

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

                for(Position pos : acidSpots) {
                    StaticGameObject acid = StaticGameObjectFactory.produce(ACID_OBJECT, pos, 10, 0);

                    ObjectManager.add(acid, true);

                    TaskManager.submit(new Task(15) {
                        @Override
                        protected void execute() {
                            ObjectManager.remove(acid, true);
                            stop();
                        }
                    });
                }
                stop();
            }
        });

        TaskManager.submit(new Task(1) {

            int tick = 0;
            @Override
            protected void execute() {
                if(tick++ >= 2) {
                    for(Position n : acidSpots) {
                        for(Player p : npc.getLocalPlayers()) {
                            if(p.getPosition().sameAs(n)) {
                                p.getCombat().queue(Damage.create(3, 6));
                                p.getCombat().submit(new PoisonEvent(PoisonType.WEAK));
                            }
                        }
                    }
                }

                if(tick == 20) {
                    stop();
                }
            }
        });
    }
}
