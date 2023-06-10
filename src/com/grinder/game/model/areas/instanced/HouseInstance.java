package com.grinder.game.model.areas.instanced;

import com.grinder.game.content.skill.skillable.impl.cons.HousePortal;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.InstancedArea;
import com.grinder.game.model.areas.constructed.Palette;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * HouseInstance Instance. To be referenced by all players entering this HouseInstance.
 * @author  Simplex
 * @since  Mar 27, 2020
 */
public class HouseInstance extends InstancedArea {

    /*
     * Constructor
     */
    public HouseInstance(Player creator) {
        super(new Boundary(1880, 1951, 5720, 5791));
        players = new ArrayList<>();
        npcs = new ArrayList<>();
        houseOwner = creator;
    }

    /**
     * If applicable, returns given player's area instance casted to HouseInstance.
     */
    public static HouseInstance get(Player p) {
        if (p.getArea() != null && p.getArea() instanceof HouseInstance)
            return ((HouseInstance) p.getArea());
        else return null;
    }

    public boolean isOwner(Player player) {
        return player == houseOwner;
    }

    public boolean isBuildMode() {
        return buildMode;
    }

    public void setBuildMode(boolean buildMode) {
        this.buildMode = buildMode;
    }

    public boolean buildMode = false;

    /*
     * House owner reference.
     */
    private Player houseOwner;

    public Player getHouseOwner() { return houseOwner; }

    /*
     * Above ground palette tiles.
     */
    private Palette surfacePalette;

    public Palette getSurfacePalette() { return this.surfacePalette; }
    public void setSurfacePalette(Palette palette) { this.surfacePalette = palette; }

    /**
     * Dungeon palette tiles
     */
    private Palette dungeonPalette;

    public Palette getDungeonPalette() { return this.dungeonPalette; }
    public void setDungeonPalette(Palette secondaryPalette) { this.dungeonPalette = secondaryPalette; }


    /**
     * HouseInstance locked from guests entering. (Right click portal)
     */
    private boolean locked;

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    /**
     * Reference to house portals
     */
    private ArrayList<HousePortal> housePortals = new ArrayList<>();

    public ArrayList<HousePortal> getHousePortals() { return housePortals; }

    /**
     * Players inside house instance
     */
    public final List<Player> players;

    /**
     * NPCs inside house instance
     */
    public final List<NPC> npcs;

    @Override
    public void process(Agent agent) { }

    /**
     * Remove minigame items, restore object states (chairs, dungeon stuff, etc)
     * @param agent
     */
    @Override
    public void leave(Agent agent) {
        if (agent != null && agent.isPlayer()) {
            // Player player = agent.getAsPlayer();
            agent.getAsPlayer().setInHouseDungeon(false);
            expel(agent.getAsPlayer());
            //super.leave(agent);
        }
    }

    /**
     * Expel all guests who are not the HouseInstance owner
     */
    private void expelGuests() {
        List<Player> guests = super.players.stream().filter(p -> p != null && p != houseOwner).collect(Collectors.toList());
        guests.forEach(this::expel);
    }

    /**
     * Move outside instance, {@link Area} will clean up next tick.
     * @param player
     */
    private void expel(Player player) {
        player.moveTo(player.pohPortalReturnPosition);
    }

    /**
     * On death, move the player outside of house portal
     * TODO Death in combat ring / boxing ring should move outside of ring
     * @param player
     * @param killer
     * @return
     */
    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        expel(player);
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
        if (agent.isNpc()) {
            // NPC npc = agent.getAsNpc();
        }
    }
    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }
    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }

    @Override public boolean canTeleport(Player player) {return true; }
    @Override public boolean canAttack(Agent attacker, Agent target) {return !attacker.isPlayer() || !target.isPlayer(); }
    @Override public boolean canTrade(Player player, Player target) { return false; }
    @Override public boolean isMulti(Agent agent) { return true; }
    @Override public boolean canEat(Player player, int itemId) { return true; }
    @Override public boolean canDrink(Player player, int itemId) { return true; }
    @Override public boolean dropItemsOnDeath(Player player, Optional<Player> killer) { return false; }
    @Override public void onPlayerRightClick(Player player, Player rightClicked, int option) { }
    @Override public boolean isSafeForHardcore() { return true; }

    public int getZ() {
        return houseOwner.getIndex() * 4;
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
