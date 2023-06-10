package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.updating.task.PostPlayerUpdateTask;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class InstancedArea extends Area {

    protected final Logger logger = LogManager.getLogger(getClass());

    public final List<Player> players;
    public final List<NPC> npcs;
    public final List<GameObject> objects;

    public boolean destroyed = false;

    public InstancedArea(Boundary... boundaries) {
        super(boundaries);
        players = new ArrayList<>();
        npcs = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public boolean canRespawnNPC(NPC npc) {
        return false;
    }

    public boolean hasLargeViewPort() {
        return false;
    }

    @Override
    public void leave(Agent agent) {
        if (agent instanceof Player) {
            super.leave(agent);
        }
        remove(agent);

        if (agent != null && agent.isPlayer()) {
            ((Player) agent).getPacketSender().sendWalkableInterface(-1);
            if (players.isEmpty()) {
                destroy();
            }
        }
    }


    @Override
	public void enter(Agent agent) {
        if (agent instanceof Player) {
            super.enter(agent);
        }
        add(agent);
		if (agent instanceof Player) {
            if (this instanceof MapInstancedBossArea) {

            } else {
                //START OF Z INSTANCE CLEAR
                Player player = agent.getAsPlayer();
                player.getPacketSender().deleteRegionalSpawns(); // clear the objects/items
                PostPlayerUpdateTask.clearFloors(player); // reload the objects/items
                //END OF Z INSTANCE CLEAR CODE
            }
		}

    }

    public void remove(Entity entity) {

        if(entity instanceof Agent){
            removeAgent((Agent) entity);
            if (this instanceof MapInstancedBossArea) {

            } else {
                //START OF Z INSTANCE CLEAR
                if (entity.isPlayer()) {
                    Player player = entity.getAsPlayer();
                    player.getPacketSender().deleteRegionalSpawns(); // clear the objects/items
                    PostPlayerUpdateTask.clearFloors(player); // reload the objects/items
                }
                //END OF Z INSTANCE CLEAR CODE
            }
        } else if(entity instanceof GameObject){
            removeGameObject((GameObject) entity);
        }

    }

    public void removeGameObject(GameObject entity) {
        final GameObject gameObject = entity;
        objects.remove(gameObject);

        boolean isPublicObject = entity instanceof DynamicGameObject ? ((DynamicGameObject)entity).isAlwaysVisible() : true;

        if(isPublicObject)
            World.getRegions().fromPosition(gameObject.getPosition())
                    .removeEntity(gameObject);
    }

    public void removeAgent(Agent agent) {
        if(agent instanceof Player) {
            final Player player = (Player) agent;
            player.setLargeViewport(false);
            players.remove(player);
        } else if(agent instanceof NPC)
            npcs.remove(agent);
        agent.setArea(null);
    }

    /**
     * Gets the object that may be in this region.
     * @param player The Player looking for the object.
     * @param object The objectId being interacted.
     * @param pos The position of the object.
     * @return GameObject.
     */
    public Optional<GameObject> getObject(Player player, int object, Position pos) {
        for(GameObject obj : this.objects) {
            if (obj.getId() == object && obj.viewableBy(player) && pos.equals(obj.getPosition()))
                return Optional.of(obj);
        }
        return super.getObject(player, object, pos);
    }

    public void add(Entity entity) {
        if(entity instanceof Agent){
            addAgent((Agent) entity);
        } else if(entity instanceof GameObject){
            addGameObject((GameObject) entity);
        }
    }

    public void addGameObject(GameObject entity) {
        final GameObject gameObject = entity;

        boolean isPublicObject = entity instanceof DynamicGameObject ? ((DynamicGameObject)entity).isAlwaysVisible() : true;

        if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES && isPublicObject) {
            World.getRegions().fromPosition(gameObject.getPosition())
                    .addEntity(gameObject, true);
        } else
            players.forEach(player -> player.getPacketSender().sendObject(gameObject));
        objects.add(gameObject);
    }

    public void addAgent(Agent agent) {
        if (destroyed) {
            System.out.println("Cannot add agent to destroyed instance. StackTrace:");
            Misc.printStackTrace();
            return;
        }
        if(agent instanceof Player){
            final Player player = (Player) agent;
            player.setLargeViewport(hasLargeViewPort());
            if(!players.contains(player)) {
                players.add(player);
            } else
                logger.warn("Attempted to add "+player+" twice to area.");
        } else if (agent instanceof NPC){
            final NPC npc = (NPC) agent;
            if(!npcs.contains(npc))
                npcs.add(npc);
            else
                logger.warn("Attempted to add "+npc+" twice to area.");
        }
        agent.setArea(this);
    }

    public void destroy() {

        if(destroyed)
            return;

        destroyed = true;

        for(GameObject gameObject: objects) {
            if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES){
                World.deSpawn(gameObject);
            } else
                ObjectManager.remove(gameObject, true);
        }

        npcs.forEach(npc -> {
            npc.setArea(null);
            if (npc.isActive())
                World.getNpcRemoveQueue().add(npc);

            TaskManager.cancelTasks(npc);
        });

        players.forEach(player -> {
            player.setArea(null);
            player.setLargeViewport(false);
        });
        objects.clear();
        npcs.clear();
        players.clear();
    }
}
