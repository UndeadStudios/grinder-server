package com.grinder.game.content.minigame.chamberoxeric.room.olm;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmCombatAttack;
import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class OlmNPC extends NPC {

    public int phase;

    public int attackTimer;

    public Direction previousDirectionFacing;

    public Direction directionFacing;

    public boolean transitioning;

    public boolean switchAfterAttack;

    public boolean switchingPhases;

    public boolean attacking;

    public int attackCount;

    public boolean turning;

    private int tick;

    private Player closestTarget;

    public ArrayList<OlmPhase> phases;

    protected OlmNPC(int id, Position position) {
        super(id, position);
        this.phase = 0;
        this.previousDirectionFacing = Direction.NONE;
        this.directionFacing = Direction.NONE;
        this.phases = new ArrayList<>();
    }

    @Override
    public void pulse() {
        if (getHitpoints() <= 0) {
            return;
        }

        if (closestTarget != null) {
            OlmCOXRoom room = closestTarget.getCOX().getParty().olm;

            if (tick >= 2) {
                if (tick % 20 == 0) {
                    room.leftHandAttacking = true;
                }
                if (tick % attackTimer == 0 && !switchingPhases) {
                    room.olmAttack = true;

                    if(phase != 3) {
                        heal(100);
                    }
                }
            }

            if (!transitioning) {
                if (room.leftHandAttacking && !room.leftHandDead && !room.leftHandProtected) {
                    room.leftHandAttacking = false;

                    OlmCombatAttack leftHandAttack = OlmCombatAttack.SWAP;

                    if (room.leftHandAttack <= 0) {
                        room.leftHandAttack = 1;
                        leftHandAttack = OlmCombatAttack.SWAP;
                    } else if (room.leftHandAttack == 1) {
                        room.leftHandAttack = 2;
                        leftHandAttack = OlmCombatAttack.LIGHTNING;
                    } else if (room.leftHandAttack == 2) {
                        if (phase == 3) {
                            room.leftHandAttack = 3;
                        } else {
                            room.leftHandAttack = 0;
                        }
                        leftHandAttack = OlmCombatAttack.CRYSTAL_BURST;
                    } else if (room.leftHandAttack == 3) {
                        room.leftHandAttack = 0;
                        leftHandAttack = OlmCombatAttack.AUTO_HEAL;
                    }

                    leftHandAttack.attack.execute(this, closestTarget);
                }

                if (!turning && room.olmAttack && previousDirectionFacing == directionFacing) {
                    room.olmAttack = false;

                    attacking = true;

                    if (attackCount == 2) {
                        attackCount = 0;

                        OlmCombatAttack olmAttack = OlmCombatAttack.ACID_DRIP;

                        if (phases.contains(OlmPhase.ACID)) {
                            if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.ACID_DRIP;
                            } else if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.ACID_SPRAY;
                            }
                        }

                        if (phases.contains(OlmPhase.CRYSTAL)) {
                            if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.FALLING_CRYSTALS;
                            } else if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.CRYSTAL_BOMBS;
                            }
                        }

                        if (phases.contains(OlmPhase.FLAME)) {
                            if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.FALLING_CRYSTALS;
                            } else if (Misc.random(2) == 1) {
                                olmAttack = OlmCombatAttack.CRYSTAL_BOMBS;
                            }
                        }

                        if (phase == 3 && Misc.random(3) == 1) {
                            olmAttack = OlmCombatAttack.LIFE_SIPHON;
                        }

                        olmAttack.attack.execute(this, closestTarget);
                    } else {
                        attackCount++;

                        OlmCombatAttack regularAttack = OlmCombatAttack.MAGIC_ATTACK;

                        if (Misc.random(2) == 1) {
                            regularAttack = OlmCombatAttack.RANGE_ATTACK;
                        } else if (Misc.random(3) == 1) {
                            regularAttack = OlmCombatAttack.SPHERE;
                        }

                        regularAttack.attack.execute(this, closestTarget);
                    }
                }
            }
        }

        tick++;
    }

    public void rise(OlmCOXRoom room) {
        OlmPhase phase = OlmPhase.getRandom();

        if (!phases.contains(phase)) {
            phases.add(phase);
            room.player.getCOX().getParty().sendMessage("The Great Olm rises with the power of " + phase.name().toLowerCase());
        } else {
            for (OlmPhase p : OlmPhase.VALUES) {
                if (!phases.contains(p)) {
                    phases.add(p);
                    room.player.getCOX().getParty().sendMessage("The Great Olm rises with the power of " + p.name().toLowerCase());
                    break;
                }
            }
        }
    }

    public void performGreatOlmAttack(OlmCOXRoom room) {
        if (directionFacing != null) {
            if (phase == 3) {
                if (getPosition().getX() >= 3238) {
                    if (directionFacing.equals(Direction.SOUTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_LEFT_ENRAGED);
                    } else if (directionFacing.equals(Direction.NORTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_RIGHT_ENRAGED);
                    } else if (directionFacing.equals(Direction.NONE)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_MIDDLE_ENRAGED);
                    }
                } else {
                    if (directionFacing.equals(Direction.SOUTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_RIGHT_ENRAGED);
                    } else if (directionFacing.equals(Direction.NORTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_LEFT_ENRAGED);
                    } else if (directionFacing.equals(Direction.NONE)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_MIDDLE_ENRAGED);
                    }
                }
            } else {
                if (getPosition().getX() >= 3238) {
                    if (directionFacing.equals(Direction.SOUTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_LEFT);
                    } else if (directionFacing.equals(Direction.NORTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_RIGHT);
                    } else if (directionFacing.equals(Direction.NONE)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_MIDDLE);
                    }
                } else {
                    if (directionFacing.equals(Direction.SOUTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_RIGHT);
                    } else if (directionFacing.equals(Direction.NORTH)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_LEFT);
                    } else if (directionFacing.equals(Direction.NONE)) {
                        room.greatOlmObject.performAnimation(OlmConfiguration.SHOOT_MIDDLE);
                    }
                }
            }
        }
    }

    public void resetAnimation(OlmCOXRoom room) {
        if (phase == 3) {
            if (getPosition().getX() >= 3238) {
                if (directionFacing == Direction.NONE) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_MIDDLE_ENRAGED);
                } else if (directionFacing == Direction.NORTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_RIGHT_ENRAGED);
                } else if (directionFacing == Direction.SOUTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_LEFT_ENRAGED);
                }
            } else {
                if (directionFacing == Direction.NONE) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_MIDDLE_ENRAGED);
                } else if (directionFacing == Direction.NORTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_LEFT_ENRAGED);
                } else if (directionFacing == Direction.SOUTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_RIGHT_ENRAGED);
                }
            }
        } else {
            if (getPosition().getX() >= 3238) {
                if (directionFacing == Direction.NONE) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_MIDDLE);
                } else if (directionFacing == Direction.NORTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_RIGHT);
                } else if (directionFacing == Direction.SOUTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_LEFT);
                }
            } else {
                if (directionFacing == Direction.NONE) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_MIDDLE);
                } else if (directionFacing == Direction.NORTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_LEFT);
                } else if (directionFacing == Direction.SOUTH) {
                    room.greatOlmObject.performAnimation(OlmConfiguration.FACE_RIGHT);
                }
            }
        }

        attacking = false;
    }

    public void phaseChange(OlmCOXRoom room) {
        if (room.leftHandNPC.getHitpoints() <= 0 && room.rightHandNPC.getHitpoints()<=0 && !switchingPhases) {
            switchingPhases = true;
            transitioning = true;
            TaskManager.submit(new Task(2) {
                @Override
                public void execute() {
                    if (phase == 1) {
                        sendSecondPhase(room);
                    } else if(phase == 2) {
                        sendLastPhase(room);
                    }
                    stop();
                }
            });
        }
    }

    public void changeDirection(OlmCOXRoom room, int tick) {

        int middlePositions = 0;
        int southPositions = 0;
        int northPositions = 0;

        HashMap<Direction, ArrayList<Player>> players = new HashMap<>();

        for (Player member : getLocalPlayers()) {
            if (member.getPosition().getY() >= 5743) {
                northPositions++;
                players.computeIfAbsent(Direction.NORTH, k -> new ArrayList<>());
                players.get(Direction.NORTH).add(member);
            } else if (member.getPosition().getY() <= 5737) {
                southPositions++;
                players.computeIfAbsent(Direction.SOUTH, k -> new ArrayList<>());
                players.get(Direction.SOUTH).add(member);
            } else {
                middlePositions++;
                players.computeIfAbsent(Direction.NONE, k -> new ArrayList<>());
                players.get(Direction.NONE).add(member);
            }
        }

        boolean switchDirections = false;

        if (tick % attackTimer == 0) {
            switchDirections = true;
        }

        if ((switchDirections && !transitioning) || switchAfterAttack) {
            if (attacking && !switchAfterAttack) {
                switchAfterAttack = true;
            } else {
                turning = true;
                switchAfterAttack = true;

                if (previousDirectionFacing == directionFacing) {
                    turning = false;
                } else {
                    TaskManager.submit(new Task(2) {
                        @Override
                        public void execute() {
                            turning = false;
                            stop();
                        }
                    });
                }

                previousDirectionFacing = directionFacing;

                if (northPositions > southPositions && northPositions > middlePositions) {
                    directionFacing = Direction.NORTH;
                } else if (southPositions > northPositions && southPositions > middlePositions) {
                    directionFacing = Direction.SOUTH;
                } else {
                    directionFacing = Direction.NONE;
                }

                if (directionFacing != previousDirectionFacing) {
                    if (getPosition().getX() >= 3238) {
                        switchDirectionsEast(room);
                    } else {
                        switchDirectionsWest(room);
                    }

                    if (players.get(directionFacing) != null) {
                        ArrayList<Player> closePlayers = players.get(directionFacing);

                        Collections.shuffle(closePlayers);

                        closestTarget = closePlayers.get(0);
                    }
                }
            }
        }
    }

    private void switchDirectionsWest(OlmCOXRoom room) {
        if (previousDirectionFacing == Direction.NORTH && directionFacing == Direction.SOUTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.LEFT_TO_RIGHT);
        }
        if (previousDirectionFacing == Direction.NORTH && directionFacing == Direction.NONE) {
            room.greatOlmObject.performAnimation(OlmConfiguration.LEFT_TO_MIDDLE);
        }
        if (previousDirectionFacing == Direction.SOUTH && directionFacing == Direction.NORTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.RIGHT_TO_LEFT);
        }
        if (previousDirectionFacing == Direction.SOUTH && directionFacing == Direction.NONE) {
            room.greatOlmObject.performAnimation(OlmConfiguration.RIGHT_TO_MIDDLE);
        }
        if (previousDirectionFacing == Direction.NONE && directionFacing == Direction.NORTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.MIDDLE_TO_LEFT);
        }
        if (previousDirectionFacing == Direction.NONE && directionFacing == Direction.SOUTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.MIDDLE_TO_RIGHT);
        }

        TaskManager.submit(new Task(2) {
            @Override
            public void execute() {
                resetAnimation(room);
                stop();
            }
        });
    }

    private void switchDirectionsEast(OlmCOXRoom room) {
        if (previousDirectionFacing == Direction.NORTH && directionFacing == Direction.SOUTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.RIGHT_TO_LEFT);
        }
        if (previousDirectionFacing == Direction.NORTH && directionFacing == Direction.NONE) {
            room.greatOlmObject.performAnimation(OlmConfiguration.RIGHT_TO_MIDDLE);
        }
        if (previousDirectionFacing == Direction.SOUTH && directionFacing == Direction.NORTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.LEFT_TO_RIGHT);
        }
        if (previousDirectionFacing == Direction.SOUTH && directionFacing == Direction.NONE) {
            room.greatOlmObject.performAnimation(OlmConfiguration.LEFT_TO_MIDDLE);
        }
        if (previousDirectionFacing == Direction.NONE && directionFacing == Direction.NORTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.MIDDLE_TO_RIGHT);
        }
        if (previousDirectionFacing == Direction.NONE && directionFacing == Direction.SOUTH) {
            room.greatOlmObject.performAnimation(OlmConfiguration.MIDDLE_TO_LEFT);
        }
        TaskManager.submit(new Task(2) {
            @Override
            public void execute() {
                resetAnimation(room);
                stop();
            }
        });
    }

    private void sendSecondPhase(OlmCOXRoom room) {

        room.greatOlmObject.performAnimation(OlmConfiguration.GOING_DOWN);

        room.olmNPC.setVisible(false);

        TaskManager.submit(new Task(3) {

            int tick = 0;

            @Override
            public void execute() {
                if (tick == 1 || tick == 3) {
                    StaticGameObject obj1 = StaticGameObjectFactory.produce(29885, new Position(3238, 5733, getPosition().getZ()), 10, 1);
                    StaticGameObject obj2 = StaticGameObjectFactory.produce(29882, new Position(3238, 5738, getPosition().getZ()), 10, 1);
                    StaticGameObject obj3 = StaticGameObjectFactory.produce(29888, new Position(3238, 5743, getPosition().getZ()), 10, 1);

                    ObjectManager.add(obj1, true);
                    ObjectManager.add(obj2, true);
                    ObjectManager.add(obj3, true);

                    room.player.instance.addObject(obj1);
                    room.player.instance.addObject(obj2);
                    room.player.instance.addObject(obj3);
                }

                if (tick == 30) {
                    room.olmNPC.setVisible(true);
                    room.olmNPC.transitioning = false;
                    room.leftHandDead = false;
                    room.rightHandDead = false;
                    room.clenchedHand = false;
                    room.leftHandProtected = false;

                    room.leftHandPosition = new Position(3220, 5743, getPosition().getZ());
                    room.greatOlmPosition = new Position(3220, 5738, getPosition().getZ());
                    room.rightHandPosition = new Position(3220, 5733, getPosition().getZ());

                    StaticGameObject leftHandObject = StaticGameObjectFactory.produce(29883, room.leftHandPosition, 10, 3);
                    StaticGameObject greatOlmObject = StaticGameObjectFactory.produce(29880, room.greatOlmPosition, 10, 3);
                    StaticGameObject rightHandObject = StaticGameObjectFactory.produce(29886, room.rightHandPosition, 10, 3);

                    room.leftHandObject = leftHandObject;
                    room.greatOlmObject = greatOlmObject;
                    room.rightHandObject = rightHandObject;

                    ObjectManager.add(leftHandObject, true);
                    ObjectManager.add(greatOlmObject, true);
                    ObjectManager.add(rightHandObject, true);

                    room.player.instance.addObject(leftHandObject);
                    room.player.instance.addObject(greatOlmObject);
                    room.player.instance.addObject(rightHandObject);

                    Position leftHandNpc = room.leftHandPosition.clone().transform(2, 2, 0);
                    Position rightHandNpc = room.rightHandPosition.clone().transform(2, 2, 0);
                    Position greatolmNpc = room.greatOlmPosition.clone().transform(1, 2, 0);

                    NPC leftHand = NPCFactory.INSTANCE.create(OlmCOXRoom.OLM_LEFT_HAND_NPC, leftHandNpc);
                    NPC rightHand = NPCFactory.INSTANCE.create(OlmCOXRoom.OLM_RIGHT_HAND_NPC, rightHandNpc);

                    room.leftHandNPC = leftHand;
                    room.rightHandNPC = rightHand;

                    World.getNpcAddQueue().add(leftHand);
                    World.getNpcAddQueue().add(rightHand);

                    room.player.instance.addAgent(leftHand);
                    room.player.instance.addAgent(rightHand);

                    room.olmNPC.moveTo(greatolmNpc);

                    room.greatOlmObject.performAnimation(OlmConfiguration.GOING_UP);
                    room.leftHandObject.performAnimation(OlmConfiguration.GOING_UP_LEFT_HAND);
                    room.rightHandObject.performAnimation(OlmConfiguration.GOING_UP_RIGHT_HAND);

                    TaskManager.submit(new Task(5) {
                        @Override
                        public void execute() {
                            StaticGameObject leftHandObject = StaticGameObjectFactory.produce(29884, room.leftHandPosition, 10, 3);
                            StaticGameObject greatOlmObject = StaticGameObjectFactory.produce(29881, room.greatOlmPosition, 10, 3);
                            StaticGameObject rightHandObject = StaticGameObjectFactory.produce(29887, room.rightHandPosition, 10, 3);

                            room.leftHandObject = leftHandObject;
                            room.greatOlmObject = greatOlmObject;
                            room.rightHandObject = rightHandObject;

                            switchingPhases = false;
                            phase = 2;

                            rise(room);

                            stop();
                        }
                    });
                    stop();
                }
                tick++;
            }
        });
    }

    private void sendLastPhase(OlmCOXRoom room) {
        int height = getPosition().getZ();

        room.leftHandPosition = new Position(3238, 5733, height);
        room.greatOlmPosition = new Position(3238, 5738, height);
        room.rightHandPosition = new Position(3238, 5743, height);

        room.greatOlmObject.performAnimation(OlmConfiguration.GOING_DOWN);

        setVisible(false);
        TaskManager.submit(new Task(3) {
            @Override
            public void execute() {
                StaticGameObject obj1 = StaticGameObjectFactory.produce(29885, new Position(3220, 5743, height), 10, 3);
                StaticGameObject obj2 = StaticGameObjectFactory.produce(29882, new Position(3220, 5738, height), 10, 3);
                StaticGameObject obj3 = StaticGameObjectFactory.produce(29888, new Position(3220, 5733, height), 10, 3);

                ObjectManager.add(obj1, true);
                ObjectManager.add(obj2, true);
                ObjectManager.add(obj3, true);
                stop();
            }
        });

        TaskManager.submit(new Task(30) {
            @Override
            public void execute() {
                moveTo(room.greatOlmPosition.transform(2, 2, 0));

                StaticGameObject leftHand = StaticGameObjectFactory.produce(29883, room.leftHandPosition, 10, 3);
                StaticGameObject greatOlm = StaticGameObjectFactory.produce(29880, room.greatOlmPosition, 10, 3);
                StaticGameObject rightHand = StaticGameObjectFactory.produce(29886, room.rightHandPosition, 10, 3);

                room.leftHandObject = leftHand;
                room.greatOlmObject = greatOlm;
                room.rightHandObject = rightHand;

                room.player.instance.addObject(leftHand);
                room.player.instance.addObject(greatOlm);
                room.player.instance.addObject(rightHand);

                ObjectManager.add(leftHand, true);
                ObjectManager.add(greatOlm, true);
                ObjectManager.add(rightHand, true);

                NPC leftHandNPC = NPCFactory.INSTANCE.create(OlmCOXRoom.OLM_LEFT_HAND_NPC, room.leftHandPosition.clone().transform(2, 2, 0));
                NPC rightHandNPC = NPCFactory.INSTANCE.create(OlmCOXRoom.OLM_RIGHT_HAND_NPC, room.rightHandPosition.clone().transform(2, 1, 0));

                World.getNpcAddQueue().add(leftHandNPC);
                World.getNpcAddQueue().add(rightHandNPC);

                room.player.instance.addAgent(leftHandNPC);
                room.player.instance.addAgent(rightHandNPC);

                room.leftHandObject.performAnimation(OlmConfiguration.GOING_UP_LEFT_HAND);
                room.greatOlmObject.performAnimation(OlmConfiguration.GOING_UP_ENRAGED);
                room.rightHandObject.performAnimation(OlmConfiguration.GOING_UP_RIGHT_HAND);

                TaskManager.submit(new Task(5) {
                    @Override
                    public void execute() {
                        setVisible(true);

                        StaticGameObject leftHand = StaticGameObjectFactory.produce(29884, room.leftHandPosition, 10, 1);
                        StaticGameObject greatOlm = StaticGameObjectFactory.produce(29881, room.greatOlmPosition, 10, 1);
                        StaticGameObject rightHand = StaticGameObjectFactory.produce(29887, room.rightHandPosition, 10, 1);

                        room.leftHandObject = leftHand;
                        room.greatOlmObject = greatOlm;
                        room.rightHandObject = rightHand;

                        room.player.instance.addObject(leftHand);
                        room.player.instance.addObject(greatOlm);
                        room.player.instance.addObject(rightHand);

                        ObjectManager.add(leftHand, true);
                        ObjectManager.add(greatOlm, true);
                        ObjectManager.add(rightHand, true);

                        room.greatOlmObject.performAnimation(OlmConfiguration.FACE_MIDDLE_ENRAGED);

                        room.leftHandDead = false;
                        room.rightHandDead = false;
                        room.clenchedHand = false;
                        room.leftHandProtected = false;

                        room.olmNPC.transitioning = false;
                        room.olmNPC.switchingPhases = false;
                        room.olmNPC.phase = 3;

                        room.olmNPC.rise(room);

                        stop();
                    }
                });
                stop();
            }
        });

    }
}
