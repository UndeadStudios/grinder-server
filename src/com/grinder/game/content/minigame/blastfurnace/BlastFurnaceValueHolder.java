package com.grinder.game.content.minigame.blastfurnace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.model.attribute.AttributeValueHolderTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * @author L E G E N D
 * @date 2/15/2021
 * @time 6:23 AM
 * @discord L E G E N D#4380
 */
public final class BlastFurnaceValueHolder extends AttributeValueHolderTemplate<BlastFurnace> {

    private static final Type TYPE = new TypeToken<BlastFurnace>() {}.getType();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public BlastFurnaceValueHolder() {
        this(new BlastFurnace());
    }

    public BlastFurnaceValueHolder(BlastFurnace value) {
        super(value);
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void reset() {
        setValue(new BlastFurnace());
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
