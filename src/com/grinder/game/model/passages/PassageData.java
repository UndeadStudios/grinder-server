package com.grinder.game.model.passages;

import com.google.gson.annotations.Expose;
import com.grinder.game.model.Position;

import java.util.Objects;

public final class PassageData {

    @Expose
    private final int id;
    @Expose
    private final int sound;
    @Expose
    private final int face;
    @Expose
    private final int shape;
    @Expose
    private final Position position;

    public PassageData(int id, int sound, int face, int shape, Position position) {
        this.id = id;
        this.sound = sound;
        this.face = face;
        this.shape = shape;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassageData)) return false;
        PassageData that = (PassageData) o;
        return id == that.id && sound == that.sound && face == that.face && shape == that.shape && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sound, face, shape, position);
    }

    public int getId() {
        return id;
    }

    public int getFace() {
        return face;
    }

    public int getShape() {
        return shape;
    }

    public int getSound() {
        return sound;
    }

    public Position getPosition() {
        return position;
    }
}
