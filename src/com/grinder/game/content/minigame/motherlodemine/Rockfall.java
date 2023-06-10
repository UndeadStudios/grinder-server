package com.grinder.game.content.minigame.motherlodemine;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.skill.skillable.impl.Mining;
import com.grinder.game.content.skill.skillable.impl.mining.PickaxeType;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.task.TaskManager;

import java.util.Optional;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 1:52 AM
 * @discord L E G E N D#4380
 */
public final class Rockfall extends GameObject {

    private static final int RESPAWN_TIME = 42;

    private Optional<PickaxeType> pickaxe = Optional.empty();

    public Rockfall(int id, Position position, int type, int orientation) {
        super(id, position, type, orientation);
    }

    public GameObject create() {
        return DynamicGameObject.createPublic(getId(), getPosition(), getObjectType(), getFace());
    }

    public void mine(Player player) {
        if (Mining.findPickaxe(player).isEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT)
                    .setText("You need a pickaxe to clear this rockfall. You don't have a pickaxe", "Which you have the Mining level to use.")
                    .start(player);
            return;
        }
        if (pickaxe.get().getAnimaion() != null)
        player.performAnimation(pickaxe.get().getAnimaion());
        TaskManager.submit(2, () -> {
            player.getSkillManager().addExperience(Skill.MINING, 10);
            DynamicGameObject rock = DynamicGameObject.createPublic(-1, getPosition());
            rock.setOriginalObject(ObjectManager.findStaticObjectAt(getId(), getPosition()).get()); // set original object as the static object so clipping can be reverted
            ObjectManager.add(rock, true);
            CollisionManager.removeObjectClipping(this); // remove collision of the removed object
            player.resetAnimation();
        });
        TaskManager.submit(RESPAWN_TIME, this::respawn);
    }


    public void respawn() {
        Region region = World.getRegions().get(getPosition().getRegionCoordinates());
        if (region.getPlayers().stream().anyMatch(p -> p.getPosition().equals(getPosition())) || region.getGroundItems(getPosition().getZ() & 3).stream().anyMatch(item -> item.getPosition().equals(getPosition()))) {
            TaskManager.submit(RESPAWN_TIME, this::respawn);
            return;
        }
        var template = new ProjectileTemplateBuilder(645)
                .setStartHeight(300).setEndHeight(25).setSpeed(40).setCurve(30);
        new Projectile(getPosition().transform(2, 2, 0), getPosition(), template.build()).sendProjectile();
        var projectile = new Projectile(getPosition().transform(-5, -5, 0), getPosition(), template.setDelay(10).setCurve(15).build());
        projectile.sendProjectile();
        projectile.onArrival(() -> {
            Optional<GameObject> despawner = ObjectManager.findDynamicObjectAt(getPosition());
            if (despawner.isPresent()) {
                ((DynamicGameObject)despawner.get()).despawn();
            }
            Graphic.sendGlobal(new Graphic(305, 2), getPosition());
        });
    }

    @Override
    public boolean viewableBy(Player player) {
        return true;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DYNAMIC_OBJECT;
    }

    public static Rockfall get(GameObject object) {
        return new Rockfall(object.getId(), object.getPosition(), object.getObjectType(), object.getFace());
    }
}
