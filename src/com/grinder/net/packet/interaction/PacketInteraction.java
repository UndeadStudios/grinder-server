package com.grinder.net.packet.interaction;


import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.item.Item;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class PacketInteraction {

    public boolean handleButtonInteraction(Player player, int button) {
        return false;
    }

    public boolean handleItemInteraction(Player player, Item item, int type) {
        return false;
    }

    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        return false;
    }

    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        return false;
    }

    public boolean handleItemOnEntityInteraction(Player player, Item item, Agent entity) {
        return false;
    }

    public boolean handleEquipItemInteraction(Player player, Item item, int slot) {
        return false;
    }

    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        return false;
    }

    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        return false;
    }

    public boolean handlePickupItem(Player player, ItemOnGround item) {
        return false;
    }

    public boolean handleNpcDeath(Player player, NPC npc) {
        return false;
    }

    public boolean handleCommand(Player player, String command, String[] args) {
        return false;
    }
}
