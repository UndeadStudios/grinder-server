package com.grinder.game.content.minigame.warriorsguild.rooms.dummy;

import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.sound.Sound;

/**
 * @author L E G E N D
 */
public final class DummyObject extends GameObject {

    private final DummyType type;

    public DummyObject(DummyType type, Position position) {
        super(type.getObjectId(), position, 10, 2);
        this.type = type;
        ObjectActions.INSTANCE.onClick(new int[]{type.getObjectId()}, action -> {
            if (Dummy.isActive(this))
                attack(action.getPlayer());
            return true;
        });
    }

    public void attack(Player player) {
        if (type.getAttackStyle().isActive(player)) {
            player.sendMessage("You whack the dummy successfully!");
            player.getWarriorsGuild().addTokens(2);
            player.playSound(new Sound(2566));
            player.performAnimation(new Animation(player.getAttackAnim()));
            player.getSkillManager().addExperience(Skill.ATTACK, 15);
        } else {
            stun(player);
            player.sendMessage("You whack the dummy with the wrong attack style.");
        }
    }

    public void stun(Player player) {
        player.performGraphic(new Graphic(245, GraphicHeight.HIGH));
        player.getMotion().impairMovement(2);
        player.playSound(new Sound(2727));
    }

    @Override
    public boolean viewableBy(Player player) {
        return true;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.STATIC_OBJECT;
    }
}
