package com.grinder.game.content.minigame.fightcave;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.model.attribute.AttributeValueHolderTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * @author L E G E N D
 * @date 2/18/2021
 * @time 6:02 AM
 * @discord L E G E N D#4380
 */
public class FightCaveValueHolder extends AttributeValueHolderTemplate<FightCave> {

    private static final Type TYPE = new TypeToken<FightCave>() {}.getType();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public FightCaveValueHolder() {
        this(new FightCave());
    }

    public FightCaveValueHolder(FightCave value) {
        super(value);
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void reset() {
        setValue(new FightCave());
    }
    @NotNull
    @Override
    public JsonElement serialize() {
        return GSON.toJsonTree(getValue(), TYPE);
    }

    @Override
    public void deserialize(@NotNull JsonElement input) {
        setValue(GSON.fromJson(input, TYPE));
    }
}
