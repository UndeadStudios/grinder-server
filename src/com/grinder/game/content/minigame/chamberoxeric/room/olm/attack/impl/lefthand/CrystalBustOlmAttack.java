package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class CrystalBustOlmAttack implements OlmAttack {

    private static final int CRYSTAL = 30033;

    private ArrayList<Position> crystalBursts = new ArrayList<>();


    @Override
    public void execute(NPC npc, Player target) {

        crystalBursts.clear();

        target.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.FLASHING_CRYSTAL_LEFT_HAND);

        for (Player p : npc.getLocalPlayers()) {
            if (npc.getPosition().getDistance(p.getPosition()) > 20) {
                continue;
            }

            StaticGameObject crystal = StaticGameObjectFactory.produce(CRYSTAL, p.getPosition(), 10, 0);

            ObjectManager.add(crystal, true);

            crystalBursts.add(p.getPosition());

            TaskManager.submit(new Task(3) {
                @Override
                protected void execute() {
                    ObjectManager.remove(crystal, true);

                    target.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.LEFT_HAND);

                    for (Position pos : crystalBursts) {
                        StaticGameObject crystal = StaticGameObjectFactory.produce(CRYSTAL, pos, 10, 0);

                        ObjectManager.add(crystal, true);

                        TaskManager.submit(new Task(2) {
                            @Override
                            protected void execute() {
                                ObjectManager.remove(crystal, true);
                                target.getPacketSender().sendGlobalGraphic(new Graphic(OlmConfiguration.LEFTOVER_CRYSTALS), pos);
                                stop();
                            }
                        });

                        if(p.getPosition().sameAs(pos)) {
                            p.getPacketSender().sendMessage("The crystal beneath your feet grows rapidly and shunts you to the side.");
                            p.getCombat().queue(Damage.create(20, 40));
                            p.getMotion().traceTo(p.getPosition().clone().transform(0,1,0));
                        }
                    }
                    stop();
                }
            });
        }
    }
}
