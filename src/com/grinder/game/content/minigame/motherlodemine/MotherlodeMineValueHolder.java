package com.grinder.game.content.minigame.motherlodemine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.model.attribute.AttributeValueHolderTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 1:29 PM
 * @discord L E G E N D#4380
 */
public final class MotherlodeMineValueHolder extends AttributeValueHolderTemplate<MotherlodeMine> {

    private static final Type TYPE = new TypeToken<MotherlodeMine>() {}.getType();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public MotherlodeMineValueHolder() {
        this(new MotherlodeMine());
    }

    public MotherlodeMineValueHolder(MotherlodeMine value) {
        super(value);
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void reset() {
        setValue(new MotherlodeMine());
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
