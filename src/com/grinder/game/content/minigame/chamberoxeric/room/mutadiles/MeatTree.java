package com.grinder.game.content.minigame.chamberoxeric.room.mutadiles;

import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.content.minigame.chamberoxeric.skills.COXWoodcutting;
import com.grinder.game.content.skill.skillable.impl.woodcutting.AxeType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class MeatTree extends PacketInteraction {

    public static final int ALIVE_MEAT_TREE = 30012;

    private static final int DEAD_MEAT_TREE = 30013;

    public static final Position MEAT_TREE_SPAWN = new Position(3303, 5321);

    public int health;

    public MeatTree() {
        this.health = 250;
    }

    public void chop(Player p, GameObject object) {
        if (health < 1) {
            return;
        }

        int lvl = COXManager.getAccumulativeLevel(p, Skill.WOODCUTTING);

        int decrement = lvl / 10;

        int height = p.getHeight();

        Position pos = MEAT_TREE_SPAWN.clone().transform(0, 0, height);

        AxeType axe = COXWoodcutting.getAxe(p);

        if (axe == null) {
            p.getPacketSender().sendMessage("You need a woodcutting axe to chop down the tree.");
            return;
        }

        p.performAnimation(axe.getAnimation());

        Task task = new Task(5) {
            @Override
            protected void execute() {
                if (p.getPosition().getDistance(object.getPosition()) > 1) {
                    stop();
                    return;
                }

                if (health < 1) {
                    stop();
                    return;
                }

                p.performAnimation(axe.getAnimation());

                health -= decrement;

                p.getCOX().points += decrement;

                p.getPacketSender().sendMessage("Health left: " + health);

                if (health < 1) {
                    p.getPacketSender().sendMessage("You fully chop down the meat tree.");
                    ObjectManager.add(StaticGameObjectFactory.produce(DEAD_MEAT_TREE, pos, 10, 0), true);
                    stop();
                }
            }
        };

        TaskManager.submit(task);
    }


    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case ALIVE_MEAT_TREE:
                player.getCOX().getParty().mutadiles.meatTree.chop(player, object);
                return true;
        }
        return false;
    }
}
