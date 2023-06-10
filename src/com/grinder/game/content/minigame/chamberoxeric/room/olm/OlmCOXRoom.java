package com.grinder.game.content.minigame.chamberoxeric.room.olm;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.COXRewards;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmCombatAttack;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class OlmCOXRoom extends COXRoom {

    public static final int OLM_LEFT_HAND_NPC = 7555;

    private static final int OLM_HEAD_NPC = 7554;

    public static final int OLM_RIGHT_HAND_NPC = 7553;

    public Position greatOlmPosition;
    public Position leftHandPosition;
    public Position rightHandPosition;

    public GameObject greatOlmObject;
    public GameObject leftHandObject;
    public GameObject rightHandObject;

    public OlmNPC olmNPC;

    public NPC leftHandNPC;

    public NPC rightHandNPC;

    public int leftHandAttack;

    private boolean initiated;

    public boolean leftHandDead;

    public boolean rightHandDead;

    public boolean startedFallingCrystals;

    private boolean clenchedHandFirst;
    private boolean clenchedHandSecond;

    private boolean unClenchedHandFirst;
    private boolean unClenchedHandSecond;

    public boolean clenchedHand;

    public boolean leftHandProtected;

    public boolean leftHandAttacking;

    public boolean olmAttack;

    private boolean lastPhaseStarted;

    public Player player;

    public OlmCOXRoom() {

    }

    public OlmCOXRoom(Player player) {
        this.player = player;
    }

    private void complete() {
        player.instance.stopAllTasks();

        greatOlmObject.performAnimation(OlmConfiguration.GOING_DOWN);

        int height = player.getPosition().getZ();

        StaticGameObject obj1 = StaticGameObjectFactory.produce(29885, new Position(3238, 5733, height), 10, 1);
        StaticGameObject obj2 = StaticGameObjectFactory.produce(29882, new Position(3238, 5738, height), 10, 1);
        StaticGameObject obj3 = StaticGameObjectFactory.produce(29888, new Position(3238, 5743, height), 10, 1);
        StaticGameObject obj4 = StaticGameObjectFactory.produce(29885, new Position(3220, 5743, height), 10, 3);
        StaticGameObject obj5 = StaticGameObjectFactory.produce(29882, new Position(3220, 5738, height), 10, 3);
        StaticGameObject obj6 = StaticGameObjectFactory.produce(29888, new Position(3220, 5733, height), 10, 3);

        ObjectManager.add(obj1, true);
        ObjectManager.add(obj2, true);
        ObjectManager.add(obj3, true);
        ObjectManager.add(obj4, true);
        ObjectManager.add(obj5, true);
        ObjectManager.add(obj6, true);
        TaskManager.submit(new Task(3) {
            @Override
            public void execute() {
                StaticGameObject obj1 = StaticGameObjectFactory.produce(30018, new Position(3232, 5749, height), 10, 3);
                ObjectManager.add(obj1, true);
                obj1.performAnimation(new Animation(7506));

                player.instance.addObject(obj1);

                StaticGameObject obj2 = StaticGameObjectFactory.produce(30028, new Position(3233, 5751, height), 10, 0);
                ObjectManager.add(obj2, true);

                player.instance.addObject(obj2);

                World.getNpcRemoveQueue().add(olmNPC);
                World.getNpcRemoveQueue().add(rightHandNPC);
                World.getNpcRemoveQueue().add(leftHandNPC);
                stop();
            }
        });

        TaskManager.submit(new Task(5) {
            @Override
            public void execute() {
                StaticGameObject blockage = StaticGameObjectFactory.produce(30018, new Position(3232, 5749, height), 10, 0);
                ObjectManager.remove(blockage, true);
                player.instance.addObject(blockage);
                stop();
            }
        });

        for(Player p : player.getCurrentClanChat().players()) {
            COXRewards.grantReward(p);

            p.getPoints().increase(AttributeManager.Points.COX_COMPLETIONS);

            int total = p.getPoints().get(AttributeManager.Points.COX_COMPLETIONS);

            p.getPacketSender().sendMessage("Congratulations! You've completed the raid. You've completed it "+total+" times.");
            p.getPacketSender().sendMessage("Total duration: "+p.getCOX().getParty().time.getTimeElapsed());
        }
    }

    private void clenchHand() {
        leftHandObject.performAnimation(OlmConfiguration.CLINCHING_LEFT_HAND);
        clenchedHand = true;
        leftHandProtected = true;
        TaskManager.submit(new Task(2) {
            @Override
            public void execute() {
                leftHandObject.performAnimation(OlmConfiguration.CLENCHED_LEFT_HAND);
                player.getCOX().getParty().sendMessage("The Great Olm's left claw clenches to protect itself temporarily.");
                stop();
            }
        });
    }

    private void unClenchHand() {
        leftHandObject.performAnimation(OlmConfiguration.BACK_TO_NORMAL_LEFT_HAND);
        clenchedHand = false;
        leftHandProtected = false;
        TaskManager.submit(new Task(2) {
            @Override
            public void execute() {
                leftHandObject.performAnimation(OlmConfiguration.LEFT_HAND);
                stop();
            }
        });
    }

    @Override
    public void init() {
        if (initiated) {
            return;
        }

        initiated = true;

        int height = player.getPosition().getZ();

        leftHandPosition = new Position(3238, 5733, height);
        greatOlmPosition = new Position(3238, 5738, height);
        rightHandPosition = new Position(3238, 5743, height);

        greatOlmObject = StaticGameObjectFactory.produce(OlmConfiguration.GREAT_OLM_OBJECT, greatOlmPosition, 10, 1);
        leftHandObject = StaticGameObjectFactory.produce(OlmConfiguration.LEFT_HAND_OBJECT, leftHandPosition, 10, 1);
        rightHandObject = StaticGameObjectFactory.produce(OlmConfiguration.RIGHT_HAND_OBJECT, rightHandPosition, 10, 1);

        player.instance.addObject(greatOlmObject);
        player.instance.addObject(leftHandObject);
        player.instance.addObject(rightHandObject);

        ObjectManager.add(greatOlmObject, true);
        ObjectManager.add(leftHandObject, true);
        ObjectManager.add(rightHandObject, true);

        leftHandObject.performAnimation(OlmConfiguration.GOING_UP_LEFT_HAND);
        greatOlmObject.performAnimation(OlmConfiguration.GOING_UP);
        rightHandObject.performAnimation(OlmConfiguration.GOING_UP_RIGHT_HAND);

        StaticGameObject blockage = StaticGameObjectFactory.produce(30018, new Position(3232, 5749, height), 10, 3);
        ObjectManager.add(blockage, true);

        player.instance.addObject(blockage);

        StaticGameObject chest = StaticGameObjectFactory.produce(30027, new Position(3233, 5751, height), 10, 0);
        ObjectManager.add(chest, true);

        player.instance.addObject(chest);

        OlmNPC olm = new OlmNPC(OLM_HEAD_NPC, greatOlmPosition.clone().transform(2, 2, 0));
        NPC leftHand = NPCFactory.INSTANCE.create(OLM_LEFT_HAND_NPC, leftHandPosition.clone().transform(2, 2, 0));
        NPC rightHand = NPCFactory.INSTANCE.create(OLM_RIGHT_HAND_NPC, rightHandPosition.clone().transform(2, 1, 0));

        olmNPC = olm;
        leftHandNPC = leftHand;
        rightHandNPC = rightHand;

        player.instance.addAgent(olmNPC);
        player.instance.addAgent(leftHandNPC);
        player.instance.addAgent(rightHandNPC);

        World.getNpcAddQueue().add(olmNPC);
        World.getNpcAddQueue().add(leftHandNPC);
        World.getNpcAddQueue().add(rightHandNPC);

        olmNPC.phase = 1;
        olmNPC.switchingPhases = false;
        olmNPC.attackTimer = 6;

        leftHandDead = false;
        rightHandDead = false;

        OlmCOXRoom room = this;

        olmNPC.rise(this);

        TaskManager.submit(new Task(1) {

            int tick = 0;

            @Override
            protected void execute() {
                olmNPC.phaseChange(room);

                if (tick >= 11) {
                    if (olmNPC.phase == 3 && olmNPC.switchingPhases) {
                        olmNPC.switchingPhases = false;
                        olmNPC.transitioning = false;
                    }
                    if (!olmNPC.switchingPhases) {
                        olmNPC.changeDirection(room, tick);
                    }
                }

                boolean lastPhase = (olmNPC.phase == 3 && room.leftHandDead && room.rightHandDead && olmNPC.getHitpoints() > 0);

                if ((olmNPC.transitioning || lastPhase) && !startedFallingCrystals) {
                    startedFallingCrystals = true;
                    OlmCombatAttack.FALLING_CRYSTALS.attack.execute(olmNPC, player);
                }

                if (olmNPC.phase < 3 && leftHandNPC.getHitpoints() <= 490 && !rightHandDead && !clenchedHand && !clenchedHandFirst
                        && leftHandNPC.getHitpoints() > 0) {
                    clenchHand();
                    clenchedHandFirst = true;
                }

                if (olmNPC.phase < 3
                        && leftHandNPC.getHitpoints() <= 400
                        && !rightHandDead && !clenchedHand && !clenchedHandSecond
                        && leftHandNPC.getHitpoints() > 0) {
                    clenchHand();
                    clenchedHandSecond = true;
                }

                if (olmNPC.phase < 3 && rightHandNPC.getHitpoints() <= 480
                        && rightHandDead && !leftHandDead && clenchedHand
                        && !unClenchedHandFirst) {
                    unClenchedHandFirst = true;
                    unClenchHand();
                }

                if (olmNPC.phase < 3 && rightHandNPC.getHitpoints() <= 250
                        && rightHandDead && !leftHandDead && clenchedHand
                        && !unClenchedHandSecond) {
                    unClenchedHandSecond = true;
                    unClenchHand();
                }

                if (olmNPC.phase < 3 && rightHandNPC.getHitpoints() <= 0
                        && rightHandDead && !leftHandDead && clenchedHand) {
                    unClenchHand();
                }

                if (!lastPhaseStarted && olmNPC.phase == 3 && leftHandDead && rightHandDead) {
                    lastPhaseStarted = true;
                    player.getCOX().getParty().sendMessage("The Great Olm is giving its all. This is its final stand.");
                }

                if (rightHandNPC.getHitpoints() <= 0) {
                    //instance.setCanAttackLeftHand(true);
                }

                if (olmNPC.getHitpoints() <= 0) {
                    greatOlmObject.performAnimation(OlmConfiguration.GOING_DOWN_ENRAGED);
                    complete();
                    stop();
                }

                tick++;
            }
        });
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        if (npc.getId() == OLM_LEFT_HAND_NPC) {
            player.getCOX().getParty().olm.leftHandObject.performAnimation(OlmConfiguration.GOING_DOWN_LEFT_HAND);
            TaskManager.submit(new Task(2) {
                @Override
                public void execute() {
                    StaticGameObject obj1 = StaticGameObjectFactory.produce(29885, new Position(3238, 5733, npc.getPosition().getZ()), 10, 1);
                    StaticGameObject obj2 = StaticGameObjectFactory.produce(29885, new Position(3220, 5743, npc.getPosition().getZ()), 10, 3);

                    ObjectManager.add(obj1, true);
                    ObjectManager.add(obj2, true);
                    stop();
                }
            });

            player.getCOX().getParty().olm.leftHandDead = true;
            return true;
        } else if (npc.getId() == OLM_RIGHT_HAND_NPC) {
            player.getCOX().getParty().olm.rightHandObject.performAnimation(OlmConfiguration.GOING_DOWN_RIGHT_HAND);
            TaskManager.submit(new Task(2) {
                @Override
                public void execute() {
                    player.getCOX().getParty().olm.rightHandObject.performAnimation(OlmConfiguration.DEAD_LEFT_HAND);
                    stop();
                }
            });

            player.getCOX().getParty().olm.rightHandDead = true;
            return true;
        }
        return false;
    }
}
