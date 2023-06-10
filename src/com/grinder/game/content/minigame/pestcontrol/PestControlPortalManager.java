package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.pestcontrol.*;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.Optional;

import static com.grinder.util.NpcID.DAGANNOTH_PRIME;

/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */
public class PestControlPortalManager {

    private final PestControlInstance instance;

    private final int WEST_PORTAL_SHIELD = NpcID.PORTAL_1743;
    private final int SOUTH_WEST_PORTAL_SHIELD = NpcID.PORTAL_1746;
    private final int SOUTH_EAST_PORTAL_SHIELD = NpcID.PORTAL_1745;
    private final int EAST_PORTAL_SHIELD = NpcID.PORTAL_1744;
    private final int WEST_PORTAL = NpcID.PORTAL;
    private final int SOUTH_WEST_PORTAL = NpcID.PORTAL_1742;
    private final int SOUTH_EAST_PORTAL = NpcID.PORTAL_1741;
    private final int EAST_PORTAL = NpcID.PORTAL_1740;

    private final NPC WEST_PORTAL_NPC;
    private final NPC SOUTH_WEST_PORTAL_NPC;
    private final NPC SOUTH_EAST_PORTAL_NPC;
    private final NPC EAST_PORTAL_NPC;

    private int timer;

    private int chanceOfSpawning;

    private Position base = new Position(2624, 2560);

    private final Position[][] SPAWN_POSITIONS = {
            {new Position(7, 31), new Position(7, 30), new Position(8, 31)},
            {new Position(55, 28), new Position(54, 29), new Position(57, 25)},
            {new Position(47, 13), new Position(48, 14), new Position(47, 15), new Position(44, 12), new Position(49, 11)},
            {new Position(22, 13), new Position(21, 14), new Position(21, 12), new Position(26, 10), new Position(22, 7)}
    };

    private final int[][] dropShieldOrders =
            {
                    {EAST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD, SOUTH_EAST_PORTAL_SHIELD, WEST_PORTAL_SHIELD},
                    {EAST_PORTAL_SHIELD, WEST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD, SOUTH_EAST_PORTAL_SHIELD},
                    {WEST_PORTAL_SHIELD, EAST_PORTAL_SHIELD, SOUTH_EAST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD},
                    {WEST_PORTAL_SHIELD, SOUTH_EAST_PORTAL_SHIELD, EAST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD},
                    {SOUTH_EAST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD, WEST_PORTAL_SHIELD, EAST_PORTAL_SHIELD},
                    {SOUTH_EAST_PORTAL_SHIELD, WEST_PORTAL_SHIELD, SOUTH_WEST_PORTAL_SHIELD, EAST_PORTAL_SHIELD}
            };

    private final int dropShieldOrder;

    PestControlPortalManager(PestControlInstance instance) {
        this.instance = instance;

        WEST_PORTAL_NPC = instance.addNPC(WEST_PORTAL_SHIELD, 4, 31); //Shield Portal
        SOUTH_WEST_PORTAL_NPC = instance.addNPC(SOUTH_WEST_PORTAL_SHIELD, 21, 9); //Shield Portal
        SOUTH_EAST_PORTAL_NPC = instance.addNPC(SOUTH_EAST_PORTAL_SHIELD, 45, 10); //Shield Portal
        EAST_PORTAL_NPC = instance.addNPC(EAST_PORTAL_SHIELD, 56, 28); //Shield Portal

        //removePortalClipping();

        WEST_PORTAL_NPC.getMotion().update(MovementStatus.DISABLED);
        SOUTH_WEST_PORTAL_NPC.getMotion().update(MovementStatus.DISABLED);
        SOUTH_EAST_PORTAL_NPC.getMotion().update(MovementStatus.DISABLED);
        EAST_PORTAL_NPC.getMotion().update(MovementStatus.DISABLED);

        dropShieldOrder = Misc.random(dropShieldOrders.length-1);

        chanceOfSpawning = 100;
        for (int i=0; i<10; i++)
            spawnEnemy(); //Spawn 10 enemies at start.

        chanceOfSpawning = 20;
    }

    public void process() {
        if (instance.finished) {
            return;
        }

        timer++;

        if (timer == 25) {
            dropShield(1);
        }
        else if (timer == 75) {
            dropShield(2);
        }
        else if (timer == 125) {
            dropShield(3);
        }
        else if (timer == 175) {
            dropShield(4);
        }

        spawnEnemy();
    }

    private void removePortalClipping() {

        CollisionManager.clearClipping(base.transform(4, 31, 0));
        CollisionManager.clearClipping(base.transform(4, 32, 0));
        CollisionManager.clearClipping(base.transform(4, 33, 0));
        CollisionManager.clearClipping(base.transform(5, 31, 0));
        CollisionManager.clearClipping(base.transform(5, 32, 0));
        CollisionManager.clearClipping(base.transform(5, 33, 0));
        CollisionManager.clearClipping(base.transform(6, 31, 0));
        CollisionManager.clearClipping(base.transform(6, 32, 0));
        CollisionManager.clearClipping(base.transform(6, 33, 0));

        CollisionManager.clearClipping(base.transform(21, 9, 0));
        CollisionManager.clearClipping(base.transform(22, 9, 0));
        CollisionManager.clearClipping(base.transform(23, 9, 0));
        CollisionManager.clearClipping(base.transform(21, 10, 0));
        CollisionManager.clearClipping(base.transform(22, 10, 0));
        CollisionManager.clearClipping(base.transform(23, 10, 0));
        CollisionManager.clearClipping(base.transform(21, 11, 0));
        CollisionManager.clearClipping(base.transform(22, 11, 0));
        CollisionManager.clearClipping(base.transform(23, 11, 0));

        CollisionManager.clearClipping(base.transform(45, 10, 0));
        CollisionManager.clearClipping(base.transform(46, 10, 0));
        CollisionManager.clearClipping(base.transform(47, 10, 0));
        CollisionManager.clearClipping(base.transform(45, 11, 0));
        CollisionManager.clearClipping(base.transform(46, 11, 0));
        CollisionManager.clearClipping(base.transform(47, 11, 0));
        CollisionManager.clearClipping(base.transform(45, 12, 0));
        CollisionManager.clearClipping(base.transform(46, 12, 0));
        CollisionManager.clearClipping(base.transform(47, 12, 0));

        CollisionManager.clearClipping(base.transform(56, 28, 0));
        CollisionManager.clearClipping(base.transform(56, 29, 0));
        CollisionManager.clearClipping(base.transform(56, 30, 0));
        CollisionManager.clearClipping(base.transform(57, 28, 0));
        CollisionManager.clearClipping(base.transform(57, 29, 0));
        CollisionManager.clearClipping(base.transform(57, 30, 0));
        CollisionManager.clearClipping(base.transform(58, 28, 0));
        CollisionManager.clearClipping(base.transform(58, 29, 0));
        CollisionManager.clearClipping(base.transform(58, 30, 0));
    }

    public boolean allPortalsDead() {
        if (!WEST_PORTAL_NPC.isAlive() && !EAST_PORTAL_NPC.isAlive() && !SOUTH_EAST_PORTAL_NPC.isAlive() && !SOUTH_WEST_PORTAL_NPC.isAlive())
            return true;
        return false;
    }

    private void spawnEnemy() {
        if (Misc.randomChance(chanceOfSpawning)) {
            //Stop spawning if portal is dead..
            int portalToSpawn = Misc.random(3);

            if (portalToSpawn == 0 && WEST_PORTAL_NPC.getHitpoints() <= 0)
                return;

            if (portalToSpawn == 1 && EAST_PORTAL_NPC.getHitpoints() <= 0)
                return;

            if (portalToSpawn == 2 && SOUTH_EAST_PORTAL_NPC.getHitpoints() <= 0)
                return;

            if (portalToSpawn == 3 && SOUTH_WEST_PORTAL_NPC.getHitpoints() <= 0)
                return;

            Position randomPosition = Misc.randomElement(SPAWN_POSITIONS[portalToSpawn]);

            int randomMonster = RANDOM_MONSTER();
            if (randomMonster == -1)
                return;

            if (randomMonster >= NpcID.RAVAGER && randomMonster <= NpcID.RAVAGER_1708) {
                Ravager npc = (Ravager) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addRavager(npc);
            }
            else if (randomMonster >= NpcID.SPLATTER && randomMonster <= NpcID.SPLATTER_1693) {
                Splatter npc = (Splatter) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addSplatter(npc);
            }
            else if (randomMonster >= NpcID.DEFILER && randomMonster <= NpcID.DEFILER_1733) {
                Defiler npc = (Defiler) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addDefiler(npc);
            }
            else if (randomMonster >= NpcID.TORCHER && randomMonster <= NpcID.TORCHER_1723) {
                Torcher npc = (Torcher) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addTorcher(npc);
            }
            else if (randomMonster >= NpcID.BRAWLER && randomMonster <= NpcID.BRAWLER_1738) {
                Brawler npc = (Brawler) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addBrawler(npc);
            }
            else if (randomMonster >= NpcID.SHIFTER && randomMonster <= NpcID.SHIFTER_1703) {
                Shifter npc = (Shifter) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                instance.addShifter(npc);
            }
            else if (randomMonster >= NpcID.SPINNER && randomMonster <= NpcID.SPINNER_1713) {
                Spinner npc = (Spinner) instance.addNPC(randomMonster, randomPosition.getX(), randomPosition.getY());
                NPC portalToHeal = WEST_PORTAL_NPC;
                switch (portalToSpawn) {
                    case 3:
                        portalToHeal = SOUTH_WEST_PORTAL_NPC;
                        break;
                    case 2:
                        portalToHeal = SOUTH_EAST_PORTAL_NPC;
                        break;
                    case 1:
                        portalToHeal = EAST_PORTAL_NPC;
                        break;
                }
                npc.setPortal(portalToHeal);
                instance.addSpinner(npc);
            }
        }
    }

    private void dropShield(int shieldNumber) {
        int shield = dropShieldOrders[dropShieldOrder][shieldNumber-1];

        if (shield == EAST_PORTAL_SHIELD) {
            EAST_PORTAL_NPC.setNpcTransformationId(EAST_PORTAL);
            dropShield(EAST_PORTAL_NPC);

            instance.playersInInstance.forEach((player -> {
                player.sendMessage("The blue, eastern portal shield has dropped!");
            }));
            Optional<NPC> voidNPC = World.findNpcById(NpcID.VOID_KNIGHT_2953);
            if (voidNPC.isPresent())
            voidNPC.get().say("The blue, eastern portal shield has dropped!");

        }
        else if (shield == SOUTH_EAST_PORTAL_SHIELD) {
            SOUTH_EAST_PORTAL_NPC.setNpcTransformationId(SOUTH_EAST_PORTAL);
            dropShield(SOUTH_EAST_PORTAL_NPC);

            instance.playersInInstance.forEach((player -> {
                player.sendMessage("The yellow, south-eastern shield has dropped!");
            }));
            Optional<NPC> voidNPC = World.findNpcById(NpcID.VOID_KNIGHT_2953);
            if (voidNPC.isPresent())
                voidNPC.get().say("The blue, eastern portal shield has dropped!");
        }
        else if (shield == SOUTH_WEST_PORTAL_SHIELD) {
            SOUTH_WEST_PORTAL_NPC.setNpcTransformationId(SOUTH_WEST_PORTAL);
            dropShield(SOUTH_WEST_PORTAL_NPC);

            instance.playersInInstance.forEach((player -> {
                player.sendMessage("The red, south-western shield has dropped!");
            }));
            Optional<NPC> voidNPC = World.findNpcById(NpcID.VOID_KNIGHT_2953);
            if (voidNPC.isPresent())
                voidNPC.get().say("The blue, eastern portal shield has dropped!");
        }
        else if (shield == WEST_PORTAL_SHIELD) {
            WEST_PORTAL_NPC.setNpcTransformationId(WEST_PORTAL);
            dropShield(WEST_PORTAL_NPC);

            instance.playersInInstance.forEach((player -> {
                player.sendMessage("The purple, western portal shield has dropped!");
            }));
            Optional<NPC> voidNPC = World.findNpcById(NpcID.VOID_KNIGHT_2953);
            if (voidNPC.isPresent())
                voidNPC.get().say("The blue, eastern portal shield has dropped!");
        }
    }

    private void dropShield(NPC npc) {
        npc.fetchDefinition().setAttackable(true);
        npc.setHitpoints(250);
        npc.updateAppearance();
    }

    private int RANDOM_MONSTER() {
        if (Misc.randomChance(15)) {
            return SHIFTER();
        }
        else if (Misc.randomChance(15)) {
            return RAVAGER();
        }
        else if (Misc.randomChance(15)) {
            return DEFILER();
        }
        else if (Misc.randomChance(20)) {
            return SPINNER();
        }
        else if (Misc.randomChance(20)) {
            return TORCHER();
        }
        else if (Misc.randomChance(20)) {
            return SPLATTER();
        }
        else if (Misc.randomChance(25)) {
            return BRAWLER();
        }
        return -1;
    }

    private int SHIFTER() {
        return NpcID.SHIFTER + Misc.random(9);
    }

    private int BRAWLER() { return NpcID.BRAWLER + Misc.random(4); }

    private int DEFILER() {
        return NpcID.DEFILER + Misc.random(9);
    }

    private int RAVAGER() { return NpcID.RAVAGER + Misc.random(4); }

    private int TORCHER() {
        return NpcID.TORCHER + Misc.random(7);
    }

    private int SPLATTER() { return NpcID.SPLATTER + Misc.random(4); }

    private int SPINNER() { return NpcID.SPINNER + Misc.random(4); }

    public NPC getWestPortalNPC() {
        return WEST_PORTAL_NPC;
    }

    public NPC getEastPortalNPC() {
        return EAST_PORTAL_NPC;
    }

    public NPC getSouthWestPortalNPC() {
        return SOUTH_WEST_PORTAL_NPC;
    }

    public NPC getSouthEastPortalNPC() {
        return SOUTH_EAST_PORTAL_NPC;
    }

}
