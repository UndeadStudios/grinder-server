package com.grinder.game.content.minigame.chamberoxeric.room.tekton;

import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class TektonNPC extends NPC {

    public static final int TEKTON_HAMMERING = 7540;

    private static final int TEKTON_WALKING = 7541;

    private static final int TEKNTON_NON_ENRAGED = 7542;

    private static final int TEKTON_ENRAGED = 7544;

    public static final Position HAMMERING_POSITION = new Position(3307, 5294, 1);

    private static final Position ATTACK_POSITION = new Position(3312, 5294, 1);

    private static final Position FIRE_BALLS = new Position(3308, 5288, 1);

    private Player p;

    private int rage;

    private int inCombatTicks;

    public TektonNPC(Player p, int id, Position position) {
        super(id, position);
        this.p = p;
    }

    public TektonState state = TektonState.HAMMERING;

    @Override
    public void pulse() {
        if (state == TektonState.HAMMERING) {
            getCombat().reset(true);
            if (getHitpoints() + 1 <= getMaxHitpoints()) {
                setHitpoints(getHitpoints() + 1);
            }
            setFace(FacingDirection.WEST);
            for (Player p : getLocalPlayers()) {
                if (p == null) {
                    continue;
                }
                if (getPosition().getDistance(p.getPosition()) < 5) {
                    state = TektonState.WALKING_FROM_ANVIL;
                    setNpcTransformationId(TEKTON_WALKING);
                }
            }
        } else if (state == TektonState.WALKING_FROM_ANVIL) {
            getMotion().traceTo(ATTACK_POSITION);

            getCombat().reset(true);

            if (getPosition().getDistance(ATTACK_POSITION) < 3) {
                state = TektonState.IN_COMBAT;
                setNpcTransformationId(TEKNTON_NON_ENRAGED);
            }
        } else if (state == TektonState.IN_COMBAT) {
            boolean found = false;

            rage++;

            if (rage == 20) {
                setNpcTransformationId(TEKTON_ENRAGED);
            }

            for (Player p : getLocalPlayers()) {
                if (p == null) {
                    continue;
                }
                if (getPosition().getDistance(p.getPosition()) < 8) {
                    found = true;
                    break;
                }
            }

            if (inCombatTicks++ == 50) {
                inCombatTicks = 0;
                found = false;
            }

            if (!found) {
                state = TektonState.WALKING_TO_ANVIL;
                setNpcTransformationId(TEKTON_WALKING);
                getCombat().reset(true);
                rage = 0;


                TaskManager.submit(new Task(4) {
                    int count = 0;

                    @Override
                    protected void execute() {
                        Position position = FIRE_BALLS.clone().transform(Misc.random(10), Misc.random(10), 0);

                        new Projectile(getPosition(), position, 0, 660, 55, 50, 43, 21, 0, 3, 0).sendProjectile();

                        TaskManager.submit(new Task(4) {


                            @Override
                            protected void execute() {
                                for (Player p : getLocalPlayers()) {

                                    p.getPacketSender().sendGlobalGraphic(new Graphic(659), position);

                                    if (p.getPosition().sameAs(position)) {
                                        p.getCombat().queue(Damage.create(20));
                                    }
                                }
                                stop();
                            }
                        });

                        if (count++ == 10) {
                            stop();
                        }
                    }
                });

            }
        } else if (state == TektonState.WALKING_TO_ANVIL) {
            getMotion().traceTo(HAMMERING_POSITION);

            getCombat().reset(true);

            if (getPosition().getDistance(HAMMERING_POSITION) < 1) {
                state = TektonState.HAMMERING;
                setNpcTransformationId(TEKTON_HAMMERING);
            }
        }
    }
}
