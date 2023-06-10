package com.grinder.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.model.interfaces.dialogue.Dialogue;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

public class DialogueDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {
        FileReader fileReader = new FileReader(file());
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(fileReader);
        Gson builder = new GsonBuilder().create();
        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);
            parse(reader, builder);
        }
        //convert(defs);
        fileReader.close();
    }

    private void parse(JsonObject reader, Gson builder) {
        final int id = reader.get("id").getAsInt();
        
        final DialogueType type = DialogueType.valueOf(reader.get("type").getAsString());
        final DialogueExpression anim = reader.has("anim") ? DialogueExpression.valueOf(reader.get("anim").getAsString()) : null;
        final int lines = reader.get("lines").getAsInt();
        String[] dialogueLines = new String[lines];
        for (int i = 0; i < lines; i++) {
            dialogueLines[i] = reader.get("line" + (i + 1)).getAsString();
        }
        final int next = reader.get("next").getAsInt();
        final int npcId = reader.has("npcId") ? reader.get("npcId").getAsInt() : -1;
        final String[] item = reader.has("item") ? (builder.fromJson(reader.get("item"), String[].class)) : null;

        Dialogue dialogue = new Dialogue() {
            @Override
            public int id() {
                return id;
            }

            @Override
            public DialogueType type() {
                return type;
            }

            @Override
            public DialogueExpression animation() {
                return anim;
            }

            @Override
            public String[] dialogue() {
                return dialogueLines;
            }

            @Override
            public int nextDialogueId() {
                return next;
            }

            @Override
            public int npcId() {
                return npcId;
            }

            @Override
            public String[] item() {
                return item;
            }
        };
        DialogueManager.dialogues.put(id, dialogue);
    }

//    private static final void convert(NpcDefinition[] npcDefinitions) throws Exception{
//        Map<Integer, Integer> npcMap = Maps.newConcurrentMap();//new Gson().fromJson(new FileReader(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\179-181npcs.json")), new TypeToken<Map<Integer, Integer>>(){}.getType());
//        Object[] converted = Stream.of(npcDefinitions).sorted(Comparator.comparingInt(NpcDefinition::getId)).map(npcDefinition -> npcDefinition.copy(npcMap.getOrDefault(npcDefinition.getId(), npcDefinition.getId()))).collect(Collectors.toList()).toArray(new Object[0]);
//
//        try(FileWriter fw = new FileWriter(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npc_defs_conv.json"))) {
//            new GsonBuilder().setPrettyPrinting().create().toJson(converted, fw);
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "dialogues.json";
    }
}
