package com.grinder.util.json;

import com.google.gson.*;
import com.grinder.util.time.SecondsTimer;

import java.lang.reflect.Type;

/**
 * @author Stan van der Bend
 */
public class SecondsTimerAdapter implements JsonSerializer<SecondsTimer>, JsonDeserializer<SecondsTimer> {

    @Override
    public JsonElement serialize(SecondsTimer stopwatch, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();
        object.addProperty("remaining_seconds", stopwatch.secondsRemaining());
        return object;
    }

    @Override
    public SecondsTimer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final SecondsTimer object = new SecondsTimer();
        final int secondsRemaining = json.getAsJsonObject().get("remaining_seconds").getAsInt();
        object.start(secondsRemaining);
        return object;
    }

}
