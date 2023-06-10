package com.grinder.game.definition.loader.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.definition.NpcSpawnDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.model.FacingDirection;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcSpawnDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {

		final FileReader reader = new FileReader(file());
		final NpcSpawnDefinition[] npcSpawnDefinitions = new Gson().fromJson(reader, NpcSpawnDefinition[].class);

		for (final NpcSpawnDefinition spawnDefinition : npcSpawnDefinitions) {
			final NPC npc = NPCFactory.INSTANCE.create(spawnDefinition.getId(), spawnDefinition.getPosition());
			npc.getMovementCoordinator().setRadius(spawnDefinition.getRadius());
			if (spawnDefinition.getFacing() == null)
				npc.setFace(FacingDirection.NORTH);
			else
				npc.setFace(spawnDefinition.getFacing());
			npc.setSpawnFace(npc.getFace());
			npc.setLastFacingDirection(spawnDefinition.getFacing().getDirection());
			World.getNpcAddQueue().add(npc);
		}

		// Loading Custom-Class NPC Spawns
		NpcCustomSpawns.load();

		reader.close();
	}

	private static final void convert(NpcSpawnDefinition[] npcSpawnDefinitions) throws Exception{
		Map<Integer, Integer> npcMap = new Gson().fromJson(new FileReader(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\179-181npcs.json")), new TypeToken<Map<Integer, Integer>>(){}.getType());
		NpcSpawnDefinition[] converted = Stream.of(npcSpawnDefinitions).map(npcSpawnDefinition -> npcSpawnDefinition.copy(npcMap.getOrDefault(npcSpawnDefinition.getId(), npcSpawnDefinition.getId()))).collect(Collectors.toList()).toArray(new NpcSpawnDefinition[0]);

		try(FileWriter fw = new FileWriter(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npc_spawns_conv.json"))) {
			new GsonBuilder().setPrettyPrinting().create().toJson(converted, fw);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "npc_spawns.json";
	}
}
