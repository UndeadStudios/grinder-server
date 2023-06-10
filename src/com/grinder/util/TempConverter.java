package com.grinder.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TempConverter {


	/* Set to true to remap switch statement cases using NPC names from old NpcIdentifiers into ids*/
	public static final boolean CONVERT_TO_NUMBERS = false;
	/* Set to true to remap the given txt files switch statement cases to names from NpcIdentifiers */
	public static final boolean CONVERT_TO_NPC_IDENTS = true;
	/* Set to true to remap NpcIdentifiers class */
	public static final boolean CONVERT_NPC_IDENS_CLASS = false;

    public static void main(String[] args) throws IOException {

      	Map<Integer, Integer> npcMap = new Gson().fromJson(new FileReader(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\179-181npcs.json")), new TypeToken<Map<Integer, Integer>>(){}.getType());

		if(CONVERT_NPC_IDENS_CLASS) {
			List<String> l = Files.readAllLines(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npcidentifiers.txt").toPath());
			List<String> l2 = Files.readAllLines(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\runelitenpcidents.txt").toPath());
			Map<Integer, String> lineNew = Maps.newConcurrentMap();
			for (int i = 0; i < l.size(); i++) {
				String line = l.get(i);
				if (!line.isEmpty()) {
					try {
						String subStr = line.substring(line.lastIndexOf("=") + 1).replace(";", "").trim();
						int id = Integer.parseInt(subStr);
						if (npcMap.containsKey(id)) {
							id = npcMap.get(id);
							line = line.substring(0, line.lastIndexOf("=") + 1) + " " + id + ";";
						}

						lineNew.put(id, line);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
			List<String> converted = lineNew.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(entry -> entry.getValue()).collect(Collectors.toList());
			Files.write(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npcidentifiers_conv.txt").toPath(), converted);
		}


        Map<Integer, String> nameIdentifiers = Maps.newConcurrentMap();

        for(Field field : NpcID.class.getFields()){
            try {
                String name = field.getName();
                field.setAccessible(true);
                int value = (int) field.get(null);
                nameIdentifiers.put(value, name);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }

		Map<Integer, String> nameIdentifiersOld = Maps.newConcurrentMap();

		for(Field field : NpcIdentifiers3.class.getFields()){
			try {
				String name = field.getName();
				field.setAccessible(true);
				int value = (int) field.get(null);
				nameIdentifiersOld.put(value, name);
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}


		if(CONVERT_TO_NUMBERS){
			List<String> lines = Files.readAllLines(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npcoptions.txt").toPath());
			List<String> newLines = new ArrayList<String>();
			boolean skip = false;
			for(int lineIdx = 0;lineIdx<lines.size();lineIdx++) {
				String line = lines.get(lineIdx);
				if(line.contains("/*") && !line.contains("*/"))
					skip = true;
				if(skip && line.contains("*"))
					skip = true;
				if(skip && line.contains("*/"))
					skip = false;

				if(skip)
					System.out.println("Skip");
				if(!skip)
					if(line.contains("case ")) {
						line = line.replace("NpcIdentifiers.", "");
						System.out.println("Found case on line " + lineIdx);
						int commentIndex = line.indexOf("//");

						int startPos = line.indexOf("case ");
						int endPos = line.indexOf(":", startPos);

						System.out.println(commentIndex + " | " + startPos + " | " + endPos);
						if(commentIndex < 0 || commentIndex > 0 && commentIndex > endPos)
							if(startPos >= 0 && endPos > 0) {
								try {
									int existingVal = Integer.parseInt(line.substring(startPos + 5, endPos));

								} catch(Exception ex) {
									String existingVal = line.substring(startPos + 5, endPos);
									Optional<Map.Entry<Integer, String>> newVal = nameIdentifiersOld.entrySet().stream().filter(entry -> entry.getValue().equals(existingVal)).findFirst();
									if(newVal.isPresent()) {
										System.out.println("existing: " + existingVal + " new: " + newVal.get().getKey());
										line = "case " + newVal.get().getKey() + ":";
									}
								}
							}
					}

				newLines.add(line);
			}

			Files.write(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\npcoptionsdone.txt").toPath(), newLines);
		}

		if(CONVERT_TO_NPC_IDENTS){

			List<String> lines = Files.readAllLines(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\testswitch.txt").toPath());
			List<String> newLines = new ArrayList<String>();
			boolean skip = false;
			for(int lineIdx = 0;lineIdx<lines.size();lineIdx++) {
				String line = lines.get(lineIdx);
				if(line.contains("/*") && !line.contains("*/"))
					skip = true;
				if(skip && line.contains("*"))
					skip = true;
				if(skip && line.contains("*/"))
					skip = false;

				if(skip)
					System.out.println("Skip");
				if(!skip)
					if(line.contains("case ")) {
						System.out.println("Found case on line " + lineIdx);
						int commentIndex = line.indexOf("//");

						int startPos = line.indexOf("case ");
						int endPos = line.indexOf(":", startPos);

						System.out.println(commentIndex + " | " + startPos + " | " + endPos);
						if(commentIndex < 0 || commentIndex > 0 && commentIndex > endPos)
							if(startPos >= 0 && endPos > 0) {
								try {
									int existingVal = Integer.parseInt(line.substring(startPos + 5, endPos));
									String newVal = nameIdentifiers.getOrDefault(npcMap.getOrDefault(existingVal, existingVal), existingVal + "");
									System.out.println("existing: " + existingVal + " new: " + newVal);
									line = "case NpcIdentifiers." + newVal + ":";
								} catch(Exception ex) {

								}
							}
					}

				newLines.add(line);
			}

			Files.write(new File("C:\\Users\\Lou\\Downloads\\Map Stuff\\testswitchdone.txt").toPath(), newLines);
		}

    }
}
