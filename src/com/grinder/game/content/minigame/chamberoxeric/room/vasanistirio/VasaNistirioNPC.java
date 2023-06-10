package com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio;

import com.grinder.game.World;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.*;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VasaNistirioNPC extends NPC {

    public static final int AWAKE = 7566;

    private static final int SLEEPING = 7565;

    private static final int FIRE_ID = 30019;

    private static final int CRYSTAL_ID = 7568;

    private static final Animation WAKE_UP = new Animation(7408);

    private static final Animation SPECIAL_END = new Animation(7410);

    private static final Graphic PLAYER_TELEPORT = new Graphic(1296);

    private static final Graphic STUNNED = new Graphic(80, GraphicHeight.HIGH);

    private static final Graphic EXPLOSION = new Graphic(1328);

    private static final Position FIRE_POSITION = new Position(3279, 5281);

    private HashMap<Position, NPC> crystals;

    private VasaNistirioState state = VasaNistirioState.SLEEPING;

    private VasaNistirioCrystal crystal;

    private NPC chargingCrystal;

    private int combatTick;

    protected VasaNistirioNPC(Position position) {
        super(SLEEPING, position);
        this.crystals = new HashMap<>();
    }

    private VasaNistirioCrystal getNext() {
        for (VasaNistirioCrystal s : VasaNistirioCrystal.VALUES) {
            if (crystals.get(s.position) == null) {
                return s;
            }
            if (crystals.get(s.position) != null) {
                if (crystals.get(s.position).isAlive()) {
                    return s;
                }
            }
        }
        return null;
    }

    private void performSpecialAttack() {
        if (state == VasaNistirioState.SLEEPING) {
            return;
        }

        List<Player> players = getLocalPlayers();

        List<Player> teleported = new ArrayList<>();

        for (Player p : players) {
            if (getPosition().getDistance(p.getPosition()) < 5) {
                teleported.add(p);
            }
        }

        for (Player p : teleported) {
            p.performGraphic(PLAYER_TELEPORT);
            TaskManager.submit(new Task(3) {
                @Override
                protected void execute() {
                    p.performGraphic(STUNNED);
                    p.getMotion().impairMovement(8);
                    p.playSound(new Sound(2727));
                    stop();
                }
            });
        }

        Position[] pos = new Position[]{
                getPosition().clone().transform(-3, -3, 0), getPosition().clone().transform(-3, 0, 0),
                getPosition().clone().transform(-3, 3, 0), getPosition().clone().transform(3, -3, 0),
                getPosition().clone().transform(3, 0, 0), getPosition().clone().transform(3, 3, 0),
                getPosition().clone().transform(0, -3, 0), getPosition().clone().transform(0, 3, 0)
        };

        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                performAnimation(SPECIAL_END);
                for (Position position : pos) {
                    new Projectile(getPosition(), position, 0, 1327, 55, 50, 43, 21, 0, 3, 0).sendProjectile();
                }
                stop();
            }
        });

        TaskManager.submit(new Task(7) {
            @Override
            protected void execute() {
                for (Player p : players) {
                    int size = p.getCurrentClanChat().players().size();

                    final int damage = (p.getSkillManager().getCurrentLevel(Skill.HITPOINTS) - 50) / size;

                    for (Position position : pos) {
                        p.getPacketSender().sendGlobalGraphic(EXPLOSION, position);
                        if (p.getPosition().sameAs(position)) {
                            p.getCombat().queue(Damage.create(damage));
                        }
                    }
                }

                state = VasaNistirioState.WALKING_TO_CRYSTAL;
                stop();
            }
        });
    }

    @Override
    public void pulse() {
        if (combatTick > 4) {
            combatTick = 0;
        }
        if (combatTick++ == 4 && state != VasaNistirioState.SLEEPING) {
            for (Player p : getLocalPlayers()) {
                Position position = p.getPosition().clone();

                if (getPosition().getDistance(position) > 12) {
                    continue;
                }

                new Projectile(getPosition(), position, 0, 1329, 55, 50, 43, 21, 0, 3, 0).sendProjectile();

                TaskManager.submit(new Task(4) {
                    @Override
                    protected void execute() {
                        p.getPacketSender().sendGlobalGraphic(new Graphic(1330), position);

                        if (p.getPosition().sameAs(position)) {
                            p.getCombat().queue(Damage.create(20));
                        }
                        stop();
                    }
                });
            }
            combatTick = 0;
        }
        if (state == VasaNistirioState.SLEEPING) {
            for (Player p : getLocalPlayers()) {
                if (getPosition().getDistance(p.getPosition()) < 4) {

                    state = VasaNistirioState.TRANSFORMING;

                    performAnimation(WAKE_UP);
                    setNpcTransformationId(AWAKE);

                    TaskManager.submit(new Task(5) {
                        @Override
                        protected void execute() {
                            Position pos = FIRE_POSITION.clone().transform(0, 0, getPosition().getZ());
                            GameObject fire = StaticGameObjectFactory.produce(FIRE_ID, pos, 10, 0);
                            p.instance.addObject(fire);
                            ObjectManager.add(fire, true);
                            performSpecialAttack();
                            stop();
                        }
                    });
                    break;
                }
            }
        } else if (state == VasaNistirioState.WALKING_TO_MIDDLE) {
            Position middle = getSpawnPosition().clone();

            getCombat().reset(false);

            if (getPosition().sameAs(middle)) {
                VasaNistirioCrystal next = getNext();

                if (next != null) {
                    crystal = next;
                    if (next == VasaNistirioCrystal.SOUTH_EAST || next == VasaNistirioCrystal.NORTH_EAST) {
                        state = VasaNistirioState.SPECIAL;
                        performSpecialAttack();
                    } else {
                        state = VasaNistirioState.WALKING_TO_CRYSTAL;
                    }
                }
            } else {
                getMotion().traceTo(middle);
            }
        } else if (state == VasaNistirioState.WALKING_TO_CRYSTAL) {
            if (crystal == null) {
                crystal = VasaNistirioCrystal.SOUTH_EAST;
            }

            getCombat().reset(false);

            Position pos = crystal.position.clone().transform(0, 0, getPosition().getZ());

            if (getPosition().getDistance(pos) < 8) {
                if (crystals.get(pos) == null) {
                    NPC crystal = NPCFactory.INSTANCE.create(CRYSTAL_ID, pos);

                    World.getNpcAddQueue().add(crystal);

                    for (Player p : getLocalPlayers()) {
                        p.instance.addAgent(crystal);
                        break;
                    }
                    crystals.put(pos, crystal);
                }

                state = VasaNistirioState.CHARGING;

                chargingCrystal = crystals.get(pos);
            } else {
                getMotion().traceTo(pos);
            }
        } else if (state == VasaNistirioState.CHARGING) {
            getCombat().reset(false);

            if (chargingCrystal != null && !chargingCrystal.isAlive()) {
                state = VasaNistirioState.WALKING_TO_MIDDLE;
            } else {
                if (getHitpoints() + 1 <= getMaxHitpoints()) {
                    setHitpoints(getHitpoints() + 1);
                }

                if (getHitpoints() >= getMaxHitpoints()) {
                    setHitpoints(getMaxHitpoints());
                    state = VasaNistirioState.WALKING_TO_MIDDLE;
                }
            }
        }
    }
}
