package com.grinder.game.content.minigame.warriorsguild.rooms.catapult;

import com.grinder.game.model.Animation;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.ObjectID;


/**
 * @author L E G E N D
 */
public enum CatapultAttackStyle {
    DEFAULT(ObjectID.CATAPULT_DEFAULT, 0, 0, 0, 0),
    STAB(ObjectID.CATAPULT_STAB, 679, 1914, 4169, 4173),
    BLUNT(ObjectID.CATAPULT_BLUNT, 680, 1911, 4168, 4172),
    SLASH(ObjectID.CATAPULT_SLASH, 681, 1913, 4170, 4174),
    MAGIC(ObjectID.CATAPULT_MAGIC, 682, 1919, 4171, 4175);

    public final int objectId;
    public final int projectileId;
    public final Sound sound;
    public final Animation defenceAnimation;
    public final Animation attackAnimation;

    CatapultAttackStyle(int objectId, int projectileId, int soundId, int defenceAnimation, int attackAnimation) {
        this.objectId = objectId;
        this.projectileId = projectileId;
        this.sound = new Sound(soundId);
        this.defenceAnimation = new Animation(defenceAnimation);
        this.attackAnimation = new Animation(attackAnimation);
    }
}
