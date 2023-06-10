package com.grinder.game.entity.agent.npc.monster.pestcontrol;

import com.grinder.game.collision.CollisionPolicy;
import com.grinder.game.content.minigame.pestcontrol.PestControlBarricade;
import com.grinder.game.content.minigame.pestcontrol.PestControlBarricadeState;
import com.grinder.game.content.minigame.pestcontrol.PestControlDoorState;
import com.grinder.game.content.minigame.pestcontrol.PestControlDoorsManager;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.event.CombatEvent;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.NPCMotion;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

/**
 * @author minoroin / TealWool#0873
 */
public class Splatter extends Monster {

    //TODOD: Show GFX on death 651 - and deal damage to players around

    boolean goingToAttack = false;
    int deadGFX;
    public Splatter(int id, @NotNull Position position) {
        super(id, position);
        getCombat().subscribe(this::onCombatEvent);
        this.fetchDefinition().setAggressive(false);
        deadGFX = (id - 1689) + 649;
        moveRandom();
    }

    private boolean attacked;//651
    private int toMoveTicks;

    private boolean onCombatEvent(CombatEvent event) {
        if (!(event instanceof IncomingHitApplied)) {
            return false;
        }
        attack(((IncomingHitApplied) event).getHit().getAttacker().getAsPlayer());
        return true;
    }


   public void attack(Player player) {
        if (goingToAttack)
            return;
        if (attacked)
            return;

    }

    public void attackObject(Position base, PestControlBarricade[] barricades, PestControlDoorsManager doorsManager) {
        if (getCombat().isUnderAttack() || getCombat().hasTarget()) {
            return;
        }
        Position position = getPosition();
        NPCMotion motion = getMotion();

        if (goingToAttack) {
            toMoveTicks--;
            if (toMoveTicks <= 0) {
                goingToAttack = false;
            }
        }

        //Attack barricades
        if (Misc.randomChance(20)) {
            if (position.isWithinDistance(base.transform(13, 32, 0), 10)) {
                if (barricadesNotBroken(barricades[7])) {
                    if (shouldAttackWestPortalBarricade(position, base, motion, barricades[7])) {
                        return;
                    }
                }
                //Attack Doors
                if (!barricadesNotBroken(barricades[7])) {
                    if (doorsManager.getWestDoorState() != PestControlDoorState.BROKEN) {
                        attackWestDoor(position, base, motion, doorsManager);
                    }
                }
            } else if (position.isWithinDistance(base.transform(29, 17, 0), 8)) {
                if (barricadesNotBroken(barricades[4])) {
                    if (shouldAttackSouthBarricade(position, base, motion, barricades[4])) {
                        return;
                    }
                }
                if (barricadesNotBroken(barricades[5])) {
                    if (shouldAttackSouthWestPortalNorthBarricade(position, base, motion, barricades[5])) {
                        return;
                    }
                }

                if (!barricadesNotBroken(barricades[4]) && !barricadesNotBroken(barricades[5])) {
                    if (doorsManager.getSouthDoorState() != PestControlDoorState.BROKEN) {
                        attackSouthDoor(position, base, motion, doorsManager);
                    }
                }
            } else if (position.isWithinDistance(base.transform(39, 18, 0), 10)) {
                if (barricadesNotBroken(barricades[3])) {
                    if (shouldAttackSouthEastPortalNorthBarricade(position, base, motion, barricades[3])) {
                        return;
                    }
                }
                if (barricadesNotBroken(barricades[4])) {
                    if (shouldAttackSouthBarricade(position, base, motion, barricades[4])) {
                        return;
                    }
                }

                if (!barricadesNotBroken(barricades[3]) && !barricadesNotBroken(barricades[4])) {
                    if (doorsManager.getSouthDoorState() != PestControlDoorState.BROKEN) {
                        attackSouthDoor(position, base, motion, doorsManager);
                    }
                }

            } else if (position.isWithinDistance(base.transform(50, 29, 0), 12)) {
                if (barricadesNotBroken(barricades[1])) {
                    if (shouldAttackEastBottomBarricade(position, base, motion, barricades[1])) {
                        return;
                    }
                }
                if (barricadesNotBroken(barricades[0])) {
                    if (shouldAttackEastTopBarricade(position, base, motion, barricades[0])) {
                        return;
                    }
                }

                //Attack Doors
                if (!barricadesNotBroken(barricades[0]) && !barricadesNotBroken(barricades[1])) {
                    if (doorsManager.getEastDoorState() != PestControlDoorState.BROKEN) {
                        this.fetchDefinition().setAggressive(false);
                        attackEastDoor(position, base, motion, doorsManager);
                    }
                }
            }
        }

        moveRandom();

    }

    @Override
    public void onDeath() {
        var players = AgentUtil.getPlayersInProximity(this, 3, CollisionPolicy.NONE).collect(Collectors.toList());

        for (var player : players) {
            var p = (Player) player;
            p.getCombat().queue(new Damage(Misc.random(15,20), DamageMask.REGULAR_HIT));
        }
        performGraphic(new Graphic(deadGFX));
        super.onDeath();
    }

    private void moveRandom() {
        if (getCombat().isUnderAttack() || getCombat().hasTarget()) {
            return;
        }
        if (!goingToAttack) {
            if (Misc.randomChance(25)) {
                getMotion().traceTo(new Position(getPosition().getX() - 5 + Misc.random(10), getPosition().getY() - 5 + Misc.random(10)));
            }
        }
    }

    private boolean barricadesNotBroken(PestControlBarricade barricade) {
        if (barricade.getParts()[0].getState() != PestControlBarricadeState.BROKEN ||
                barricade.getParts()[1].getState() != PestControlBarricadeState.BROKEN ||
                barricade.getParts()[2].getState() != PestControlBarricadeState.BROKEN ||
                barricade.getParts()[3].getState() != PestControlBarricadeState.BROKEN) {
            return true;
        }
        return false;
    }

    private boolean goToAttackBarricade(Position position, Position base, PestControlBarricade barricade, int diffX, int diffY, int partID) {
        //Get each side of barricade possible.
        if (position.sameAs(base.transform(diffX + 1, diffY, 0)) || position.sameAs(base.transform(diffX - 1, diffY, 0)) || position.sameAs(base.transform(diffX, diffY + 1, 0)) || position.sameAs(base.transform(diffX, diffY - 1, 0))) {
            if (barricade.getParts()[partID].getState() != PestControlBarricadeState.BROKEN) {
                setPositionToFace(base.transform(diffX, diffY, 0));
                barricade.getParts()[partID].hitBarricade();
            }
            appendDeath();

            goingToAttack = false;
            return true;
        }
        return false;
    }

    private boolean shouldAttackWestPortalBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 12, 31, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 12, 30, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 12, 29, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 12, 28, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(11, 31 - randomBarricade, 0), 8)) {
                    motion.traceTo(base.transform(11, 31 - randomBarricade, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean shouldAttackSouthWestPortalNorthBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 23, 18, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 24, 18, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 25, 18, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 26, 18, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(23 + randomBarricade, 17, 0), 14)) {
                    motion.traceTo(base.transform(23 + randomBarricade, 17, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean shouldAttackSouthBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 32, 15, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 33, 15, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 34, 15, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 35, 15, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(32 + randomBarricade, 14, 0), 15)) {
                    motion.traceTo(base.transform(32 + randomBarricade, 14, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean shouldAttackSouthEastPortalNorthBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 42, 18, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 43, 18, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 44, 18, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 45, 18, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(42 + randomBarricade, 17, 0), 15)) {
                    motion.traceTo(base.transform(42 + randomBarricade, 17, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean shouldAttackEastBottomBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 52, 24, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 52, 25, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 52, 26, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 52, 27, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(53, 24 + randomBarricade, 0), 15)) {
                    motion.traceTo(base.transform(53, 24 + randomBarricade, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean shouldAttackEastTopBarricade(Position position, Position base, NPCMotion motion, PestControlBarricade barricade) {
        int randomBarricade = Misc.random(3);

        if (goingToAttack) {
            if (goToAttackBarricade(position, base, barricade, 49, 30, 0)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 49, 31, 1)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 49, 32, 2)) {
                return true;
            } else if (goToAttackBarricade(position, base, barricade, 49, 33, 3)) {
                return true;
            }
        }
        if (Misc.randomChance(5)) {
            if (barricade.getParts()[randomBarricade].getState() != PestControlBarricadeState.BROKEN && !goingToAttack) {
                if (position.isWithinDistance(base.transform(50, 30 + randomBarricade, 0), 15)) {
                    motion.traceTo(base.transform(50, 30 + randomBarricade, 0));
                    goingToAttack = true;
                    toMoveTicks = 15;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean attackEastDoor(Position position, Position base, NPCMotion motion, PestControlDoorsManager doorsManager) {
        if (Misc.randomChance(20)) {
            if (position.sameAs(base.transform(47, 32, 0)) || position.sameAs(base.transform(47, 33, 0)) && goingToAttack) {
                doorsManager.breakEastDoor(base);
                goingToAttack = false;
                appendDeath();
                return true;
            }
            if (Misc.randomChance(10) && !goingToAttack) {
                getMotion().traceTo(base.transform(47, 33, 0));
                goingToAttack = true;
            }
            if (Misc.randomChance(10) && !goingToAttack) {
                getMotion().traceTo(base.transform(47, 32, 0));
                goingToAttack = true;
            }
        }
        return false;
    }

    private boolean attackWestDoor(Position position, Position base, NPCMotion motion, PestControlDoorsManager doorsManager) {
        if (Misc.randomChance(20)) {
            if (position.sameAs(base.transform(18, 32, 0)) || position.sameAs(base.transform(18, 33, 0)) && goingToAttack) {
                doorsManager.breakWestDoor(base);
                goingToAttack = false;
                appendDeath();
                return true;
            }
            if (Misc.randomChance(10) && !goingToAttack) {
                getMotion().traceTo(base.transform(18, 32, 0));
                goingToAttack = true;
            }
            if (Misc.randomChance(10) && !goingToAttack) {
                getMotion().traceTo(base.transform(18, 33, 0));
                goingToAttack = true;
            }
        }
        return false;
    }

    private boolean attackSouthDoor(Position position, Position base, NPCMotion motion, PestControlDoorsManager doorsManager) {
        if (Misc.randomChance(20)) {
            if (position.sameAs(base.transform(32, 24, 0)) || position.sameAs(base.transform(33, 24, 0)) && goingToAttack) {
                doorsManager.breakSouthDoor(base);
                goingToAttack = false;
                appendDeath();
                return true;
            }
            if (Misc.randomChance(30) && !goingToAttack) {
                getMotion().traceTo(base.transform(32, 24, 0));
                goingToAttack = true;
            }
            if (Misc.randomChance(30) && !goingToAttack) {
                getMotion().traceTo(base.transform(33, 24, 0));
                goingToAttack = true;
            }
        }
        return false;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}


