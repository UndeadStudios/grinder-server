package com.grinder.util.tools;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Used for dumping definitions from oldschool wikia.
 * 
 * @author Blake
 *
 */
public class DefinitionDumper {
	
	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public static void main(String[] args) throws IOException {
		NpcDefinitionDumper npc = new NpcDefinitionDumper();
		ItemDefinitionDumper item = new ItemDefinitionDumper();
		
		//npc.dump("Ancient Wyvern", 7795, -1);
		item.dump("Corporeal critter", 22318, 12650);
	}
	
	public String getNextSecond(Element link, String title) {
		return link.selectFirst("a[title=" + title + "]").parent().nextElementSibling().text();
	}
	
	public String getNextSingle(Element link, String title) {
		return link.selectFirst("a[title=" + title + "]").parent().parent().nextElementSibling().text();
	}
	
	public static <T> void writeJSON(T t, String name) {
		try (FileOutputStream outputStream = new FileOutputStream("./data/" + name + ".json")) {
			outputStream.write(GSON.toJson(t).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
