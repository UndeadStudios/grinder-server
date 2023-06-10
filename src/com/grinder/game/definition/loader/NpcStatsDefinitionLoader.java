package com.grinder.game.definition.loader;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.NpcStatsDefinition;
import com.grinder.game.definition.factory.NpcStatsFactory;
import com.grinder.game.definition.loader.DefinitionLoader;

import java.io.FileReader;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-08-20
 */
public class NpcStatsDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {

        final FileReader reader = new FileReader(file());
        final NpcStatsDefinition[] definitions = new Gson().fromJson(reader, NpcStatsDefinition[].class);

        for(NpcStatsDefinition definition : definitions)
            definition.getIds().clear();

        reader.close();

        NpcStatsFactory.setNpcStatsDefinitions(definitions);
    }

    @Override
    public String file()  {
        return GameConstants.DEFINITIONS_DIRECTORY + "npc_stats.json";
    }
}
