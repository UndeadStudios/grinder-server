package com.grinder.game.model.passages.link;

import com.google.gson.annotations.Expose;
import com.grinder.game.model.Position;

import java.util.Objects;

public final class PassageLinkData {


    @Expose
    private final int id;
    @Expose
    private final int face;
    @Expose
    private final Position position;

    public PassageLinkData(int id, int face, Position position) {
        this.id = id;
        this.face = face;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassageLinkData)) return false;
        PassageLinkData that = (PassageLinkData) o;
        return id == that.id && face == that.face && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, face, position);
    }

    public int getId() {
        return id;
    }

    public int getFace() {
        return face;
    }

    public Position getPosition() {
        return position;
    }
}
