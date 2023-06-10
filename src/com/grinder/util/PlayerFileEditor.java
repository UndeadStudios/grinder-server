package com.grinder.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinder.game.GameConstants;
import com.grinder.game.content.skill.Skills;

public class PlayerFileEditor {
	
	private static final String CHARACTER_FILES_PATH = GameConstants.PLAYER_DIRECTORY;

	public static void main(String[] args) {
		File[] files = new File(CHARACTER_FILES_PATH).listFiles();
		
		int count = 0;
		
		for (File file : files) {
			Gson builder = new GsonBuilder().create();
			JsonObject reader = getJSON(file);
			
			
			if (reader.has("skills")) {
				Skills skills = builder.fromJson(reader.get("skills"), Skills.class);
				boolean edited = false;
				for (int i = 0; i < 6; i++) {
					if (skills.getExperiences()[i] > 250_000_000) {
						// 	if (skills.getMaxLevels()[i] > 95) {
						skills.getLevels()[i] = 1;
						skills.getMaxLevels()[i] = 1;
						skills.getExperiences()[i] = 0;
						edited = true;
					}
				}
				if (edited) {
					reader.add("skills", builder.toJsonTree(skills));
					writeJSON(file, reader);
					count++;
				}
			}
		}
		
		System.out.println("Finished editing " + count + " files.");
	}
	
	private static JsonObject getJSON(File file) {
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();

			return (JsonObject) fileParser.parse(fileReader);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void writeJSON(File file, JsonObject reader) {
		try (FileWriter writer = new FileWriter(file)) {
			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			writer.write(builder.toJson(reader));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
