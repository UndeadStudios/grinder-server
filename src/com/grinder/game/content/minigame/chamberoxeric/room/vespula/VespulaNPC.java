package com.grinder.game.content.minigame.chamberoxeric.room.vespula;

import com.grinder.game.World;
import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Animation;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VespulaNPC extends NPC {

    private static final Position ROOT_POSITION = new Position(3268, 5302);

    public static final Animation STING = new Animation(7454);

    public static final int LUX_GRUB = 7535;

    private static final int SOLDIER = 7538;

    private static final Position[] LUX_GRUB_SPAWN = {
            new Position(3276, 5284),
            new Position(3277, 5293),
            new Position(3284, 5293),
            new Position(3286, 5288),};

    private ArrayList<NPC> luxes;

    private boolean spawnedLuxGrubs;

    private VespulaState state = VespulaState.AIR_BORNE;

    private int luxAttackTimer;

    protected VespulaNPC(int id, Position position) {
        super(id, position);
        luxes = new ArrayList<>();
    }

    private void transform(VespulaState state) {
        this.state = state;

        setNpcTransformationId(state.id);
    }

    @Override
    public void pulse() {
        if (!spawnedLuxGrubs) {
            for (Player p : getLocalPlayers()) {
                if (getPosition().getDistance(p.getPosition()) < 8) {
                    spawnedLuxGrubs = true;

                    for (int i = 0; i < LUX_GRUB_SPAWN.length; i++) {
                        Position pos = LUX_GRUB_SPAWN[i].clone().transform(0, 0, getPosition().getZ());

                        NPC lux = NPCFactory.INSTANCE.create(LUX_GRUB, pos);

                        World.getNpcAddQueue().add(lux);

                        luxes.add(lux);

                        p.instance.addAgent(lux);

                        if (i == 3) {
                            lux.setFace(FacingDirection.WEST);
                        }
                    }

                    Position pos = ROOT_POSITION.clone().transform(0, 0, getPosition().getZ());

                    ObjectManager.add(StaticGameObjectFactory.produce(30068, pos, 10, 0), true);
                    break;
                }
            }
        } else {
            if (state == VespulaState.AIR_BORNE) {
                if (getHitpoints() <= getMaxHitpoints() * 0.2) {
                    transform(VespulaState.GROUND);

                    TaskManager.submit(new Task(50) {
                        @Override
                        protected void execute() {
                            transform(VespulaState.AIR_BORNE);
                            stop();
                        }
                    });
                }
            }

            if (state != VespulaState.GROUND) {
                if (luxAttackTimer++ == 5 ) {
                    luxAttackTimer = 0;

                    performAnimation(STING);

                    for(Player p : getLocalPlayers()) {
                        if(getPosition().getDistance(p.getPosition()) > 8) {
                            continue;
                        }
                        p.getCombat().queue(Damage.create(Misc.random(7)));
                    }

                    if(luxes.size() > 0) {
                        NPC lux = luxes.get(Misc.random(luxes.size() - 1));

                        lux.getCombat().queue(Damage.create(8));

                        if (!lux.isAlive()) {
                            luxes.remove(lux);

                            NPC soldier = NPCFactory.INSTANCE.create(7538, getPosition().clone());

                            World.getNpcAddQueue().add(soldier);

                            for (Player p : getLocalPlayers()) {
                                p.instance.removeAgent(lux);
                                p.instance.addAgent(soldier);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
