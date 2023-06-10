package com.grinder.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.content.collectionLog.CLData;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;

public class ItemDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {

        final FileReader reader = new FileReader(file());
        final ItemDefinition[] itemDefinitions = new Gson().fromJson(reader, ItemDefinition[].class);

        ItemDefinition.definitions.clear();

        for (ItemDefinition def : itemDefinitions) {
            if(def != null) {
                ItemDefinition.definitions.put(def.getId(), def);
                ItemDefinition.definitionNames.add(def.getName());

                if(CLData.isCollectionLogItem(def.getId())) {
                    def.setCollectable(true);
                }
            }
        }
        reader.close();
    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "items.json";
    }
}
