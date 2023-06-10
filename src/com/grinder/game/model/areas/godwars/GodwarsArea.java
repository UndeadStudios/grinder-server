package com.grinder.game.model.areas.godwars;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.attribute.Attribute;

import java.util.Arrays;
import java.util.Optional;

public class GodwarsArea extends Area {

    public static boolean isBandosMinions(int id) {
        return Arrays.asList(2240, 2234, 2241, 2096, 7271, 2215, 2216, 2217, 2218).contains(id);
    }

    public static boolean isArmadylMinions(int id) {
        return Arrays.asList(3166, 3167, 3168, 3169, 3170, 3171, 3172, 3173, 3174, 3175).contains(id);
    }

    public static boolean isZamorakMinions(int id) {
        return Arrays.asList(3137, 3138, 3139, 3140, 3141).contains(id);
    }

    public static boolean isSaradominMinions(int id) {
        return Arrays.asList(2209, 2212, 2213, 2214).contains( id);
    }

    public GodwarsArea() {
        super(new Boundary(2798, 2944, 5255, 5294),
                new Boundary(2812, 2862, 5310, 5375),
                new Boundary(2844, 2944, 5295, 5309),
                new Boundary(2863, 2916, 5310, 5349),
                new Boundary(2878, 2942, 5350, 5369),
                new Boundary(2917, 2942, 5333, 5349),
                new Boundary(2909, 2930, 5256, 5276));
    }

    @Override
    public void enter(Agent agent) {
        super.enter(agent);
    }

    public static void sendInterface(Player player) {
        player.getPacketSender().sendString(16217, "@cya@" + player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT));
        player.getPacketSender().sendString(16216, "@cya@" + player.getAttributes().numInt(Attribute.ARMADYL_KILL_COUNT));
        player.getPacketSender().sendString(16218, "@cya@" + player.getAttributes().numInt(Attribute.SARADOMIN_KILL_COUNT));
        player.getPacketSender().sendString(16219, "@cya@" + player.getAttributes().numInt(Attribute.ZAMORAK_KILL_COUNT));
        player.getPacketSender().sendWalkableInterface(16210);
    }

    public void removeInterfaceAndReset(Player player) {
        GodChamber.resetKillCounts(player);
        player.getPacketSender().sendWalkableInterface(-1);
    }

    @Override
    public void leave(Agent agent) {
        super.leave(agent);
        if (agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            if (player.getArea() instanceof GodChamberArea || player.getArea() instanceof GodwarsArea) {
                removeInterfaceAndReset(player);
            }/* else {
                player.getPacketSender().sendWalkableInterface(-1);
            }*/
        }

    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
        String attributeKey = null;
        if (isBandosMinions(agent.getAsNpc().getId())) {
            attributeKey = Attribute.BANDOS_KILL_COUNT;
        } else if (isArmadylMinions(agent.getAsNpc().getId())) {
            attributeKey = Attribute.ARMADYL_KILL_COUNT;
        } else if (isZamorakMinions(agent.getAsNpc().getId())) {
            attributeKey = Attribute.ZAMORAK_KILL_COUNT;
        } else if (isSaradominMinions(agent.getAsNpc().getId())) {
            attributeKey = Attribute.SARADOMIN_KILL_COUNT;
        }
        if (attributeKey != null) {
            player.getAttributes().numAttr(attributeKey, 0).incJ(1);
        }
    }

    @Override
    public void process(Agent agent) {
        if (agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            if (!(player.getArea() instanceof GodwarsArea) && !(player.getArea() instanceof GodChamberArea)) {
                removeInterfaceAndReset(player);
            } else {
                sendInterface(player);
            }

        }
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}