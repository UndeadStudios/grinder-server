package com.grinder.util.oldgrinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.grinder.util.oldgrinder.FieldExclusionStrategy.*;

public class SerializerHandler {
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static Gson gson() {
		return gson;
	}
	public static Object loadDefinitions(File dataFile, Class<?> classType) throws IOException {
		Gson gson = new GsonBuilder().setExclusionStrategies(new FieldExclusionStrategy(StreamType.INPUT)).create();
		
		try (FileReader fileReader = new FileReader(dataFile)) {
			return gson.fromJson(fileReader, classType);
		} catch (Exception exception) {
			throw exception;
		}
	}
	public static Object loadDefinitions(String definitionFile, Class<?> classType) throws FileNotFoundException, IOException {
		Gson gson = new GsonBuilder().setExclusionStrategies(new FieldExclusionStrategy(StreamType.INPUT)).create();
		File dataFile = DataUtil.getDefinition(definitionFile);
		
		try (FileReader fileReader = new FileReader(dataFile)) {
			return gson.fromJson(fileReader, classType);
		} catch (FileNotFoundException fileNotFoundException) {
			throw fileNotFoundException;
		} catch (IOException ioException) {
			throw ioException;
		}
	}

	public static Object loadDefinitions(String definitionFile, Object dest, Type type) throws FileNotFoundException, IOException {
		Gson gson = new GsonBuilder().setExclusionStrategies(new FieldExclusionStrategy(StreamType.INPUT)).create();
		File dataFile = DataUtil.getDefinition(definitionFile);

		try (FileReader fileReader = new FileReader(dataFile)) {
			return gson.fromJson(fileReader, type);
		} catch (FileNotFoundException fileNotFoundException) {
			throw fileNotFoundException;
		} catch (IOException ioException) {
			throw ioException;
		}
	}

	public static JsonElement parse(String definitionFile) {
		File dataFile = DataUtil.getDefinition(definitionFile);

		try (FileReader fileReader = new FileReader(dataFile)) {
			return new JsonParser().parse(fileReader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object loadDefinitions(Gson gson, String definitionFile, Object dest, Type type) throws FileNotFoundException, IOException {
		File dataFile = DataUtil.getDefinition(definitionFile);

		try (FileReader fileReader = new FileReader(dataFile)) {
			return gson.fromJson(fileReader, type);
		} catch (FileNotFoundException fileNotFoundException) {
			throw fileNotFoundException;
		} catch (IOException ioException) {
			throw ioException;
		}
	}

}
