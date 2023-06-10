package com.grinder.game.model.projectile;

import com.grinder.game.model.Graphic;
import com.grinder.game.model.sound.Sound;

import java.util.Optional;

/**
 * Represents a template that can be used to construct {@link Projectile projectiles}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
public interface ProjectileTemplate {

    /**
     * The size of the source entity (if any).
     *
     * This value is used to determine the offset of the projectile's position in the client.
     *
     * @return an {@link Integer int} value that represents the size of the source entity.
     */
    int sourceSize();

    /**
     * The offsets from the source entity (if any).
     *
     * This value is used to determine the offset of the projectile's position in the client.
     *
     * @return an {@link Integer int} value that represents the offset from the source entity.
     */
    int sourceOffset();

    /**
     * The graphic (spot-anim) id of the projectile.
     *
     * @return an {@link Integer int} value that represents the id of the projectile's graphic.
     */
    int projectileId();

    /**
     * The starting vertical offset from the tile at which the projectile is rendered.
     *
     * @return an {@link Integer int} value that is send to the client as a byte.
     */
    int startHeight();

    /**
     * The ending vertical offset from the tile at which the projectile is rendered.
     *
     * @return an {@link Integer int} value that is send to the client as a byte.
     */
    int endHeight();

    /**
     * The curvature of the path from the starting position to the destination position.
     *
     * @return an {@link Integer int} value that is send to the client as a byte.
     */
    int curve();

    /**
     * The client cycles in which the trajectory of the projectile must be completed.
     *
     * @return an {@link Integer int} value that is send to the client as a short.
     */
    int lifetime();

    /**
     * The number of client cycles that this projectile should appear after.
     *
     * @return an {@link Integer int} value that is send to the client as a short.
     */
    int delay();

    /**
     * An optional sound played at arrival of the projectile.
     *
     * @return an {@link Optional<Sound>} to be played at arrival of the projectile.
     */
    default Optional<Sound> arrivalSound() {
        return Optional.empty();
    }

    /**
     * An optional sound played at departure of the projectile.
     *
     * @return an {@link Optional<Sound>} to be played at departure the projectile.
     */
    default Optional<Sound> departureSound() {
        return Optional.empty();
    }

    /**
     * An optional graphic played at the arrival of the projectile.
     *
     * @return an {@link Optional<Graphic>} to be played at arrival the projectile.
     */
    default Optional<Graphic> arrivalGraphic() {
        return Optional.empty();
    }

    /**
     * Create a new {@link ProjectileTemplateBuilder} with the argued graphic.
     *
     * @param graphicId the {@link ProjectileTemplate#projectileId()}.
     * @return a new {@link ProjectileTemplateBuilder}.
     */
    static ProjectileTemplateBuilder builder(int graphicId){
        return new ProjectileTemplateBuilder(graphicId);
    }
}
