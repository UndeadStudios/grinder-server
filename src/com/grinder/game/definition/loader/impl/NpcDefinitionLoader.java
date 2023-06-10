package com.grinder.game.definition.loader.impl;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.util.NpcID;
import com.grinder.util.Priority;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Graphic.class, (JsonDeserializer<Graphic>) (json, typeOfT, context) -> {
            JsonObject object = json.getAsJsonObject();

            int id = object.get("id").getAsInt();
            int delay = object.get("delay").getAsInt();
            JsonPrimitive heightPrimitive = object.getAsJsonPrimitive("height");
            JsonPrimitive priorityPrimitive = object.getAsJsonPrimitive("priority");
            int height;
            if(heightPrimitive.isString()){
                height = GraphicHeight.valueOf(heightPrimitive.getAsString().toUpperCase()).ordinal() * 50;
            } else
                height = heightPrimitive.getAsInt();
            Priority priority;
            if(priorityPrimitive == null){
                priority = Priority.LOW;
            } else
                priority = Priority.valueOf(priorityPrimitive.getAsString());
            return new Graphic(id, delay, height, priority);
        });

        FileReader reader = new FileReader(file());
        var definitions = gson.create().fromJson(reader, NpcDefinition[].class);
        for (NpcDefinition definition : definitions) {
            final int id = definition.getId();
            final int respawn = definition.getRespawn();
            final int combatLevel = definition.getCombatLevel();
            definition.setRespawn(((respawn < 30 && combatLevel > 40) && id != NpcID.GIANT_MOLE && id != NpcID.SCORPIA && !(respawn <= 0)) ? (int) (combatLevel / 1.2) : respawn);
            NpcDefinition.getDefinitions().put(definition.getId(), definition);
            NpcDefinition.getDefinitionsByName().put(definition.getName().toLowerCase(), definition);
        }
        reader.close();
    }


    private static void convert(NpcDefinition[] npcDefinitions) {
        Map<Integer, Integer> npcMap = Maps.newConcurrentMap();
        //new Gson().fromJson(new FileReader(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\179-181npcs.json")), new TypeToken<Map<Integer, Integer>>(){}.getType());
        Object[] converted = Stream.of(npcDefinitions).sorted(Comparator.comparingInt(NpcDefinition::getId)).map(npcDefinition -> npcDefinition.copy(npcMap.getOrDefault(npcDefinition.getId(), npcDefinition.getId()))).collect(Collectors.toList()).toArray(new Object[0]);

        try(FileWriter fw = new FileWriter("C:\\Users\\Lou\\Downloads\\Map Stuff\\npc_defs_conv.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(converted, fw);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "npc_defs.json";
    }





}
