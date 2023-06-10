package com.grinder.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ObjectSpawnDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.entity.object.ObjectManager;

public class ObjectSpawnDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {

        final FileReader reader = new FileReader(file());
        final ObjectSpawnDefinition[] spawnDefinitions = new Gson().fromJson(reader, ObjectSpawnDefinition[].class);

        for (final ObjectSpawnDefinition spawnDefinition : spawnDefinitions) {
            final GameObject object = DynamicGameObject.createPublic(spawnDefinition.getId(), spawnDefinition.getPosition(), spawnDefinition.getType(), spawnDefinition.getFace());
            ObjectManager.add(object, true);
        }

        reader.close();
    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "object_spawns.json";
    }
}