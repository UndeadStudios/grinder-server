package com.grinder.game.content.minigame.blastfurnace;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

import static com.grinder.game.content.minigame.blastfurnace.conveyor.BlastFurnaceState.BROKEN;
import static com.grinder.game.content.minigame.blastfurnace.conveyor.BlastFurnaceState.RUNNING;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 5:41 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceStove {

    private int temperature;
    private int coal;

    private static final Position LOCATION = new Position(1948, 4963);

    static {
        //spawn furnace
        ObjectManager.add(DynamicGameObject.createPublic(ObjectID.STOVE, LOCATION));
    }

    public BlastFurnaceStove() {
        temperature = 50;
        coal = 100;
    }

    public void update() {
        BlastFurnace.getDumpy().addCoke();
        removeCoal(1);
        temperature = coal / 2;
        if (temperature < 10) {
            if (BlastFurnace.isRunning()) {
                BlastFurnace.switchState(BROKEN);
            }
        }
        var cold = ObjectID.STOVE;
        var normal = ObjectID.STOVE_2;
        var hot = ObjectID.STOVE_3;
        var id = temperature <= 20 ? cold : temperature <= 60 ? normal : hot;
        var optional = ObjectManager.findDynamicObjectAt(LOCATION);
        if (optional.isPresent()) {
            var object = optional.get();
            if (object.getId() != id) {
                ObjectManager.remove(object, false);
                ObjectManager.add(DynamicGameObject.createPublic(id, LOCATION, 10, 0), true);
            }
        }
    }

    public void refuel(Player player) {
        if(!player.getInventory().contains(ItemID.SPADE_FULL_OF_COKE)){
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You need a spade full of coke to refuel the stove.")
                    .start(player);
            return;
        }
            player.performAnimation(new Animation(2442));
            TaskManager.submit(2, () -> {
                player.performAnimation(new Animation(2443));
                player.getSkillManager().addExperience(Skill.FIREMAKING, 5);
                player.playSound(new Sound(1059));
                player.getInventory().replaceFirst(ItemID.SPADE_FULL_OF_COKE, ItemID.SPADE);
                addCoal(10);
            });
    }

    public void collect(Player player) {
        if (!player.getInventory().contains(ItemID.SPADE)) {
            if (!player.getInventory().contains(ItemID.SPADE_FULL_OF_COKE)) {
                new DialogueBuilder(DialogueType.STATEMENT).setText("You will need a spade to collect coke.")
                        .start(player);
                return;
            }
        }
        if (player.getInventory().contains(ItemID.SPADE_FULL_OF_COKE)) {
            if (!player.getInventory().contains(ItemID.SPADE)) {
                new DialogueBuilder(DialogueType.STATEMENT).setText("Your spade is already full of coke.")
                        .start(player);
                return;
            }
        }
        player.performAnimation(new Animation(2441));
        player.playSound(new Sound(1049));
        TaskManager.submit(2, () -> {
            player.getInventory().replaceFirst(ItemID.SPADE, ItemID.SPADE_FULL_OF_COKE);
            player.setPositionToFace(player.getPosition().transform(0, 1, 0));
            player.resetAnimation();
        });
    }

    public boolean canRun(){
        return temperature > 10;
    }

    public void addCoal(int amount) {
        if (coal < 200) {
            coal += amount;
        }
        if (coal > 200) {
            coal = 200;
        }
    }

    public void removeCoal(int amount) {
        coal -= amount;
        if (coal < 0) {
            coal = 0;
        }
    }

    public int getCoal() {
        return coal;
    }

    public int getTemperature() {
        return temperature;
    }
}
