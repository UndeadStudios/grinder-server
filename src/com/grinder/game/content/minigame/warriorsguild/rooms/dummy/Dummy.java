package com.grinder.game.content.minigame.warriorsguild.rooms.dummy;

import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author L E G E N D
 */
public final class Dummy {

    private static final Map<Integer, GameObject> dummies = new HashMap<>();
    private static final Map<Integer, GameObject> activeDummies = new HashMap<>();
    private static DummyType lastType;

    static {
        // Could cause issues when the <clinit> is called before the world, but that won't happen here.
        dummies.put(0, ObjectManager.findStaticObjectAt(ObjectID.HOLE_33, new Position(2856, 3554, 0)).get());
        dummies.put(1, ObjectManager.findStaticObjectAt(ObjectID.HOLE_34, new Position(2858, 3554, 0)).get());
        dummies.put(2, ObjectManager.findStaticObjectAt(ObjectID.HOLE_35, new Position(2860, 3553, 0)).get());
        dummies.put(3, ObjectManager.findStaticObjectAt(ObjectID.HOLE_36, new Position(2860, 3551, 0)).get());
        dummies.put(4, ObjectManager.findStaticObjectAt(ObjectID.HOLE_37, new Position(2859, 3549, 0)).get());
        dummies.put(5, ObjectManager.findStaticObjectAt(ObjectID.HOLE_38, new Position(2857, 3549, 0)).get());
        dummies.put(6, ObjectManager.findStaticObjectAt(ObjectID.HOLE_39, new Position(2855, 3550, 0)).get());
        dummies.put(7, ObjectManager.findStaticObjectAt(ObjectID.HOLE_36, new Position(2855, 3552, 0)).get());
        start();

        //spawn dynamic objects to transform
        for (GameObject obj : dummies.values()) {
            ObjectManager.add(DynamicGameObject.createPublic(obj.getId(), obj.getPosition(), obj.getObjectType(), obj.getFace()));
        }
    }

    public static void start() {
        TaskManager.submit(new Task(5, false) {
            @Override
            protected void execute() {
                if (activeDummies.size() > 0) {
                    return;
                }
                DummyType randomType;
                do {
                    randomType = Misc.random(DummyType.values());
                } while (randomType == lastType);
                lastType = randomType;
                var indices = new ArrayList<Integer>();
                int count = Misc.random(1, 3);
                for (int i = 0; i < count; i++) {
                    int index;
                    do {
                        index = Misc.random(7);
                    } while (indices.contains(index));
                    indices.add(index);
                }
                for (var index : indices) {
                    activeDummies.put(index, activateDummy(dummies.get(index), randomType));
                }
                startDespawnTask();
                stop();
            }
        });
    }

    private static void startDespawnTask() {
        TaskManager.submit(8, () -> {
            for (var key : activeDummies.keySet()) {
                replaceDummy(activeDummies.get(key), dummies.get(key));
            }
            activeDummies.clear();
            Dummy.start();
        });
    }

    public static boolean isActive(DummyObject dummyObject) {
        return activeDummies.values().stream().anyMatch(obj -> obj.getPosition().sameAs(dummyObject.getPosition()));
    }

    private static void replaceDummy(GameObject oldDummy, GameObject newDummy) {
        ObjectManager.findDynamicObjectAt(oldDummy.getId(), oldDummy.getPosition()).ifPresent(object -> ObjectManager.remove(object, false));
        ObjectManager.add(newDummy, true);
    }

    private static DummyObject activateDummy(GameObject object, DummyType type) {
        var dummy = new DummyObject(type, object.getPosition());
        replaceDummy(object, dummy);
        return dummy;
    }
}
