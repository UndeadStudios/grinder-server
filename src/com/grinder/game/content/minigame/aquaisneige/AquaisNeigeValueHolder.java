package com.grinder.game.content.minigame.aquaisneige;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.model.attribute.AttributeValueHolderTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


public class AquaisNeigeValueHolder extends AttributeValueHolderTemplate<AquaisNeige> {

    private static final Type TYPE = new TypeToken<AquaisNeige>() {}.getType();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public AquaisNeigeValueHolder() {
        this(new AquaisNeige());
    }

    public AquaisNeigeValueHolder(AquaisNeige value) {
        super(value);
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void reset() {
        setValue(new AquaisNeige());
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
