package com.grinder.game.model.areas.impl;

import com.grinder.game.content.miscellaneous.rugmerchant.RugMerchant;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaListener;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Optional;

import static com.grinder.util.ItemID.WATERSKIN_1_;
import static com.grinder.util.ItemID.WATERSKIN_4_;

/**
 * @author L E G E N D
 * @date 2/15/2021
 * @time 9:59 AM
 * @discord L E G E N D#4380
 */
public class AlKharidianDesertArea extends Area implements AreaListener {

    private int timer;
    public static final int HEAT_INTERVAL = 90;

    public AlKharidianDesertArea() {
        super(
                new Boundary(3201, 3390, 2814, 2940),
                new Boundary(3137, 3199, 2881, 2968),
                new Boundary(3201, 3331, 2943, 3010),
                new Boundary(3135, 3199, 2992, 3019),
                new Boundary(3184, 3274, 3010, 3067),
                new Boundary(3203, 3294, 3068, 3134),
                new Boundary(3275, 3454, 3039, 3066),
                new Boundary(3379, 3455, 2942, 3038),
                new Boundary(3314, 3378, 3012, 3038),
                new Boundary(3299, 3516, 3067, 3104),
                new Boundary(3312, 3351, 3109, 3133),
                new Boundary(3352, 3508, 3109, 3122),
                new Boundary(3397, 3486, 3123, 3159),
                new Boundary(3392, 3396, 3123, 3143),
                new Boundary(3386, 3391, 3123, 3135),
                new Boundary(3411, 3440, 3160, 3170),
                new Boundary(3295, 3298, 3067, 3108),
                new Boundary(3295, 3311, 3109, 3111) //Entrance
        );
    }

    @Override
    public void enter(Agent agent) {
        super.enter(agent);
    }

    @Override
    public void leave(Agent agent) {
        super.leave(agent);
    }


    @Override
    public void process(Agent agent) {
        if (agent instanceof Player) {
            var player = agent.getAsPlayer();
            if (++timer % (isWearingDesertRobes(player) ? HEAT_INTERVAL * 2 : HEAT_INTERVAL) == 0) {
                decreaseOrHit(player);
            }
        }
    }


    public void decreaseOrHit(Player player) {
        if (isRidingCarpet(player)) {
            return;
        }

        var haveWaterSkin = false;
        for (int id = WATERSKIN_4_; id <= WATERSKIN_1_; id += 2) {
            if (player.getInventory().contains(id)) {
                player.getInventory().replaceFirst(id, id + 2, true);
                player.sendMessage("You take a drink of water.");
                haveWaterSkin = true;
                break;
            }
        }

        if (!haveWaterSkin) {
            player.sendMessage("You should get a waterskin for any travelling in the desert.");
            player.sendMessage("You start dying of thirst while you're in the desert.");
            player.getCombat().queue(Damage.create(Misc.random(4)));
            player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 521 : 519));
        }

    }

    public boolean isRidingCarpet(Player player) {
        if (player.getAppearance().getBas() == null) {
            return false;
        }
        return player.getAppearance().getBas().getIdle() == RugMerchant.BAS_ID;
    }

    public boolean isWearingDesertRobes(Player player) {
        return (player.getEquipment().contains(ItemID.DESERT_ROBE) &&
                player.getEquipment().contains(ItemID.DESERT_SHIRT) &&
                player.getEquipment().contains(ItemID.DESERT_BOOTS))
                || player.getEquipment().contains(ItemID.DESERT_AMULET_4);
    }


    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !attacker.isPlayer() || !target.isPlayer();
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }
}