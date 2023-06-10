package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.npc.monster.impl.BarricadeEntity;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;

public class BarricadeManager {
    public static void setupBarricade(Player player, int itemSlot) {
        Party party = player.getCurrentParty();
        if (party != CastleWars.zamorakParty && party != CastleWars.saradominParty) {
            return;
        }
        final Position PlayerPosition = player.getPosition();
        if (CastleWars.inZammySafeRoom(PlayerPosition.getX(), PlayerPosition.getY(), PlayerPosition.getZ()) || CastleWars.inSaraSafeRoom(PlayerPosition.getX(), PlayerPosition.getY(), PlayerPosition.getZ()) || !player.inCastleWars() || CollisionManager.blocked(PlayerPosition) || RockManager.isUnderRock(ZAMORAK_NORTH_ROCKS.getX(), ZAMORAK_NORTH_ROCKS.getY(), PlayerPosition.getX(), PlayerPosition.getY()) || RockManager.isUnderRock(ZAMORAK_WEST_ROCKS.getX(), ZAMORAK_WEST_ROCKS.getY(), PlayerPosition.getX(), PlayerPosition.getY()) || RockManager.isUnderRock(SARADOMIN_EAST_ROCKS.getX(), SARADOMIN_EAST_ROCKS.getY(), PlayerPosition.getX(), PlayerPosition.getY()) || RockManager.isUnderRock(SARADOMIN_SOUTH_ROCKS.getX(), SARADOMIN_SOUTH_ROCKS.getY(), PlayerPosition.getX(), PlayerPosition.getY())) {
            player.sendMessage("You can't set up barricade here.");
            return;
        }
        CopyOnWriteArrayList<NPC> barricadeList = party == CastleWars.zamorakParty ? CastleWars.zammyBarricades : CastleWars.saraBarricades;

        if (barricadeList.size() >= LIMIT_BARRICADES) {
            player.sendMessage("Your team has already setup " + LIMIT_BARRICADES + " barricades.");
            return;
        }

        if (CastleWars.isInCastleWars(player)) {
            player.getPacketSender().sendSound(Sounds.SET_BARRICADE_SOUND);
            player.getInventory().delete(new Item(ItemID.BARRICADE, 1), itemSlot);
            NPC npc = NPCFactory.INSTANCE.create(NpcID.BARRICADE, player.getPosition().clone().add(0, 0));
            npc.getMotion().update(MovementStatus.DISABLED);
            World.getNpcAddQueue().add(npc);
            CollisionManager.addClipping(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), CollisionManager.BLOCKED_TILE);
            player.getMotion().enqueueStepAwayWithCollisionCheck();
            if (player.getCurrentParty() == CastleWars.zamorakParty) {
                CastleWars.zammyBarricades.add(npc);
            } else {
                CastleWars.saraBarricades.add(npc);
            }
            player.getPacketSender().sendSound(1447);
        }
    }

    public static void fireBarricade(Player player, int itemSlot, NPC npc) {
        if (player.getInventory().get(itemSlot).getId() == ITEM_TINDERBOX) {
            if (npc.getNpcTransformationId() == 5723) {
                player.sendMessage("You already set it on fire.");
            }
            player.getPacketSender().sendSound(Sounds.SET_ON_FIRE);
            npc.setNpcTransformationId(5723);

            TaskManager.submit(new Task(1) {
                int step = 0;
                @Override
                protected void execute() {
                    if (step == 10 || npc.getNpcTransformationId() == 5722) {
                        npc.resetTransformation();
                        stop();
                        return;
                    }
                    npc.getCombat().queue(Damage.create(1, 4));

                    step++;
                }
            });
        }
    }

    public static void extinguishBarricade(Player player, int itemSlot, NPC npc) {
        if (npc.getNpcTransformationId() == 5723) {
            player.sendMessage("This barricade is not on fire.");
            return;
        }
        if (player.getInventory().get(itemSlot).getId() == ITEM_BUCKET_OF_WATER) {
            player.sendMessage("You extinguished the barricate fire");
            player.getPacketSender().sendSound(Sounds.EXTINGUISH_FIRE);
            player.getInventory().get(itemSlot).setId(ITEM_BUCKET);
            player.setUpdateInventory(true);
            npc.setNpcTransformationId(5722);
        }
    }

    public static void extinguishBarricade(Player player, NPC npc) {
        if (npc.getNpcTransformationId() == 5723) {
            player.sendMessage("This barricade is not on fire.");
            return;
        }
        if (player.getInventory().contains(ITEM_BUCKET_OF_WATER)) {
            player.sendMessage("You extinguished the barricade fire");
            player.getPacketSender().sendSound(Sounds.EXTINGUISH_FIRE);
            player.getInventory().delete(ITEM_BUCKET, 1);
            player.setUpdateInventory(true);
            npc.setNpcTransformationId(5722);
        }
    }

    public static void explodeBarricade(Player player, int itemSlot, NPC npc) {
        if (player.getInventory().get(itemSlot).getId() == ITEM_EXPLOSIVE) {
            if (npc.getNpcTransformationId() == 5723) {
                player.sendMessage("Nothing interesting happens.");
            }
            npc.performGraphic(new Graphic(157, GraphicHeight.MIDDLE));
            npc.appendDeath();
            player.getInventory().delete(new Item(ITEM_EXPLOSIVE, 1), itemSlot);

        }
    }
}
