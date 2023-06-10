package com.grinder.game.model.consumable;

import com.grinder.game.model.Animation;
import com.grinder.util.Priority;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-25
 */
public class ConsumableConstants {

    static final int EXTRA_ATTACK_DELAY_ON_EDIBLE_CONSUMPTION = 2;

    /**
     * The default item representing the final potion dose.
     */
    public static final int VIAL = 229;

    /**
     * The {@link Sound} that will be played when consuming a potion.
     */
    static final Sound DRINK_SOUND = new Sound(Sounds.POTION_DRINKING);

    /**
     * The {@link Sound} that will be played when consuming food.
     */
    static final Sound SOUND = new Sound(Sounds.EAT_SOUND);

    /**
     * The {@link Animation} that will be played when consuming an edible/drinkable.
     */
    static final Animation ANIMATION = new Animation(829, Priority.HIGH);

}
