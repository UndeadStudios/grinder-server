package com.grinder.game.definition.loader.impl;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ItemGroundDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Optional;

public class ItemOnGroundDefinitionLoader extends DefinitionLoader {


	@Override
	public void load() throws Throwable {

		final File directory = Paths.get(file()).toFile();
		final File[] files = directory.listFiles();

		for (File file : files){

			if (file.isDirectory() || !file.getPath().endsWith(".json"))
				continue;

			final FileReader reader = new FileReader(file);
			final ItemGroundDefinition[] itemOnGroundDefinitions = new Gson().fromJson(reader, ItemGroundDefinition[].class);

			for (final ItemGroundDefinition groundDefinition : itemOnGroundDefinitions) {

				// Defs
				final Item item = new Item(groundDefinition.getId(), groundDefinition.getAmount());
				Position position = new Position(groundDefinition.getX(), groundDefinition.getY(), groundDefinition.getZ()).clone();
				int respawnTimer = groundDefinition.getRespawnTimer();

				/*
				 * Safety measure just in case
				 */
				if (item.getAmount() <= 0) {
					item.setAmount(1);
				}
				if (respawnTimer <= 0) {
					respawnTimer = 25;
				}
				if (position.getZ() <= 0) {
					position.setZ(0);
				}


				ItemOnGround groundItems = new ItemOnGround(ItemOnGround.State.SEEN_BY_EVERYONE, Optional.empty(), position, new Item(groundDefinition.getId(), item.getAmount()), true, respawnTimer, ItemOnGroundManager.STATE_UPDATE_DELAY);
				ItemOnGroundManager.register(groundItems);
			}

			reader.close();
		}

	}

	@Override
	public String file() {
		return GameConstants.DATA_DIRECTORY+"/definitions/ground/";
	}
}
