package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class LightningOlmAttack implements OlmAttack {

    private static final Graphic GREEN_LIGHTNING = new Graphic(OlmConfiguration.GREEN_LIGHTNING);

    private ArrayList<Position> lightning = new ArrayList<>();

    @Override
    public void execute(NPC npc, Player target) {

        lightning.clear();

        target.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.FLASHING_LIGHTNING_LEFT_HAND);

        int height = target.getPosition().getZ();

        for (int i = 0; i < 2; i++) {
            Position random = OlmConfiguration.randomLocation(height);

            Position lightning = new Position(random.getX(), 5748, height);

            this.lightning.add(lightning);
        }

        for (int i = 0; i < 2; i++) {
            Position random = OlmConfiguration.randomLocation(height);

            Position lightning = new Position(random.getX(), 5731, height);

            this.lightning.add(lightning);
        }

        for (Position pos : lightning) {
            target.getPacketSender().sendGlobalGraphic(GREEN_LIGHTNING, pos);
        }

        TaskManager.submit(new Task() {
            int ticks = 0;

            @Override
            protected void execute() {
                for (int i = 0; i < 2; i++) {
                    Position pos = lightning.get(i).transform(0, 1, 0);
                    lightning.get(i).setAs(pos);
                }

                for (int i = 2; i < 4; i++) {
                    Position pos = lightning.get(i).transform(0, -1, 0);
                    lightning.get(i).setAs(pos);
                }

                for (Position l : lightning) {
                    if (l.getY() > 5748 || l.getY() < 5731) {
                        stop();
                        return;
                    }

                    target.getPacketSender().sendGlobalGraphic(GREEN_LIGHTNING, l);

                    for (Player p : npc.getLocalPlayers()) {
                        if (p.getPosition().sameAs(l)) {
                            p.getMotion().impairMovement(2);
                            PrayerHandler.deactivatePrayers(p);
                            p.getCombat().queue(Damage.create(10, 25));
                            p.getPacketSender().sendMessage("You've been electrocuted!");
                        }
                    }
                }

                if (ticks++ == 20) {
                    stop();
                }
            }
        });
    }
}
