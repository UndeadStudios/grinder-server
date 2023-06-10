package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Priority;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class LifeSiphonOlmAttack implements OlmAttack {

    private ArrayList<Position> siphons = new ArrayList<>();

    @Override
    public void execute(NPC npc, Player target) {
        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        siphons.clear();

        TaskManager.submit(new Task() {

            int tick = 0;

            @Override
            protected void execute() {
                if (tick == 1) {
                    for (int i = 0; i < 2; i++) {
                        Position pos = OlmConfiguration.randomLocation(target.getPosition().getZ());

                        new Projectile(room.olmNPC.getPosition(), pos, OlmConfiguration.BLUE_SMALL_PROJECTILE, 8, 60, 70, 10, 0)
                                .sendProjectile();

                        siphons.add(pos);

                        target.getPacketSender().sendGlobalGraphic(new Graphic(1363, Priority.LOW), pos);
                    }
                } else if (tick == 2) {
                    room.olmNPC.resetAnimation(room);
                } else if (tick == 10) {
                    for (Player p : npc.getLocalPlayers()) {
                        if (!p.getPosition().sameAs(siphons.get(0))
                                && !p.getPosition().sameAs(siphons.get(1))) {
                            Damage damage = Damage.create(10, 30);
                            p.getCombat().queue(damage);
                            room.olmNPC.heal(damage.damage * 4);
                        }
                    }
                    stop();
                }
                tick++;
            }
        });
    }
}
