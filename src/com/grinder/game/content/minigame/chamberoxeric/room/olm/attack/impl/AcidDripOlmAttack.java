package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class AcidDripOlmAttack implements OlmAttack {

    private static final int ACID_OBJECT = 30032;

    private ArrayList<GameObject> acidSpots = new ArrayList<>();

    @Override
    public void execute(NPC npc, Player target) {
        ArrayList<Player> players = npc.getLocalPlayers();

        if(players.size() <= 1) {
            return;
        }

        acidSpots.clear();

        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        Player victim = players.get(Misc.random(players.size()-1));

        if(victim == null) {
            return;
        }

        victim.getPacketSender().sendMessage("@red@The Great Olm has smothered you in acid. It starts to drip off slowly.");

        room.olmNPC.resetAnimation(room);

        TaskManager.submit(new Task(1) {

            int tick = 0;

            @Override
            protected void execute() {

                Position pos = victim.getPosition().clone();

                StaticGameObject acidSpot = StaticGameObjectFactory.produce(ACID_OBJECT, pos, 22, 0);

                ObjectManager.add(acidSpot, true);

                acidSpots.add(acidSpot);

                for(GameObject o : acidSpots) {
                    for(Player p : players) {
                        if(p.getPosition().sameAs(o.getPosition())) {
                            p.getCombat().queue(Damage.create(3, 6));
                        }
                    }
                }

                if(tick++ == 20) {
                    for(GameObject o : acidSpots) {
                        ObjectManager.remove(o, true);
                    }
                    stop();
                }
            }
        });
    }
}
