package com.grinder.game.definition.loader.impl;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;

public class NpcDropDefinitionLoader extends DefinitionLoader {

	private static final Logger LOGGER = LogManager.getLogger(NpcDropDefinitionLoader.class.getSimpleName());

	@Override
	public void load() throws Throwable {

		final FileReader reader = new FileReader(file());
		final NpcDropDefinition[] dropDefinitions = new Gson().fromJson(reader, NpcDropDefinition[].class);

		NpcDropDefinition.definitions.clear();
		NpcDropDefinition.names.clear();
		NpcDropDefinition.nameDefinitionAlphabetical.clear();

		//convert(defs);
		for (NpcDropDefinition def : dropDefinitions) {

			for (int npcId : def.getNpcIds()) {

				NpcDropDefinition.definitions.put(npcId, def);

				final NpcDefinition npcDefinition = NpcDefinition.forId(npcId);
				if (npcDefinition != null) {
					final String npcName = npcDefinition.getName().toLowerCase();
					try {
						NpcDropDefinition.names.put(npcName, def);
						NpcDropDefinition.nameDefinitionAlphabetical.put(npcName, npcDefinition);
					} catch(Exception ex){
						LOGGER.error("Could not parse npc name for npc {"+npcId+"}", ex);
					}
				}
			}
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "npc_drops.json";
	}

}
