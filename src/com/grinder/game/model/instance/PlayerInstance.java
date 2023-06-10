package com.grinder.game.model.instance;

import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.task.Task;
import com.grinder.net.packet.interaction.PacketInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public abstract class PlayerInstance extends PacketInteraction {

    public List<Player> players;

    public List<NPC> npcs;

    public List<GameObject> objects;

    public List<Task> tasks;

    public PlayerInstance() {
        this.players = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void addAgent(Agent agent) {
        if (agent.isPlayer()) {
            if(!players.contains(agent.getAsPlayer())) {
                players.add(agent.getAsPlayer());
            }
        } else if (agent.isNpc()) {
            if(!npcs.contains(agent.getAsNpc())) {
                npcs.add(agent.getAsNpc());
            }
        }
    }

    public void removeAgent(Agent agent) {
        if (agent.isPlayer()) {
            Player p = agent.getAsPlayer();
            if (players.contains(p)) {
                players.remove(p);
            }
        } else if (agent.isNpc()) {
            NPC npc = agent.getAsNpc();
            if (npcs.contains(npc)) {
                npcs.add(npc);
            }
        }
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void removeObject(GameObject object) {
        if (objects.contains(object)) {
            objects.remove(object);
        }
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        if(tasks.contains(task)) {
            task.stop();
            tasks.remove(task);
        }
    }

    public void stopAllTasks() {
        for(Task task : tasks) {
            if(task == null) {
                continue;
            }
            task.stop();
        }
    }

    public void destroy() {

        for (NPC npc : npcs) {
            World.getNpcRemoveQueue().add(npc);
        }

        for (GameObject gameObject : objects) {
            World.deSpawn(gameObject);
        }

        stopAllTasks();

        players.clear();
        npcs.clear();
        objects.clear();
        tasks.clear();
    }

    public abstract void handlePlayerDeath(Player p);

    public abstract boolean isSafe(Player p);

    public abstract boolean canTeleport(Player p);

    public abstract void onLogout(Player p);

    public abstract boolean inMulti(Player p);

    public abstract boolean doesNPCRespawn(NPC npc);

}
