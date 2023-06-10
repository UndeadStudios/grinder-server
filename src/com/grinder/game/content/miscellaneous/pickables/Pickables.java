package com.grinder.game.content.miscellaneous.pickables;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.Optional;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 1:10 AM
 * @discord L E G E N D#4380
 */
public class Pickables {

    static {
        for (var pickable : PickableType.values()) {
            ObjectActions.INSTANCE.onClick(pickable.getObjectIds(), action -> {
                var player = action.getPlayer();
                if (player.getInventory().countFreeSlots() > 0) {
                    pick(player, action.getObject(), pickable);
                } else {
                    player.sendMessage(pickable.getFullInventoryMessage());
                }
                return true;
            });
        }
    }

    public static void pick(Player player, GameObject object, PickableType pickable) {
        player.performAnimation(new Animation(827));
        player.playSound(new Sound(2581));
        player.getInventory().add(pickable.getItemId(), 1);
        player.sendMessage(pickable.getMessage());
        rollRemove(object, pickable);
    }

    public static void rollRemove(GameObject object, PickableType pickable) {

        if (pickable.getChanceOfDeSpawn() > 0) {
            if (Misc.random(1, pickable.getChanceOfDeSpawn()) % pickable.getChanceOfDeSpawn() != 0) {
                return;
            }
        }

        DynamicGameObject removedObj = DynamicGameObject.createPublic(-1, object.getPosition(), object.getObjectType(), object.getFace());
        removedObj.setOriginalObject(object);
        ObjectManager.add(removedObj, true);
        TaskManager.submit(pickable.getRespawnTime(), () -> removedObj.despawn());
    }

    public static Optional<GameObject> getFlax(Position position) {
        return ClippedMapObjects.findObject(ObjectID.FLAX, position);
    }
}
