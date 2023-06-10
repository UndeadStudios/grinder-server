package com.grinder.util.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.loader.impl.NpcDefinitionLoader;

public class NpcDefinitionDumper extends DefinitionDumper {
	
	public NpcDefinitionDumper() {
		// Load npc definitions...
		new NpcDefinitionLoader().run();
	}
	
	public void dump(String name, int id, int copy) throws IOException {
		Document doc = Jsoup.connect("http://oldschoolrunescape.wikia.com/wiki/" + name).get();
		
		Elements table = doc.getElementsByClass("wikitable infobox");
		
		List<JsonElement> jsonElements = new ArrayList<>(table.size());
		
		for (int i = 0; i < table.size(); i++) {
			Element link = table.get(i);
			
			String examine = getNextSingle(link, "Examine");
			
			boolean aggressive = getNextSecond(link, "Aggressiveness").equals("Yes") ? true : false;
			
			boolean poisonous = getNextSecond(link, "Poison").equals("Yes") ? true : false;
			
			Element slayer = link.selectFirst("span[class=plink-template]").parent().parent().nextElementSibling();
			
			String attackSpeedStr = link.selectFirst("img[alt*=attack speed]").attr("alt");
			
			NpcDefinition def = NpcDefinition.forId(id);
			
			boolean set = def.equals(NpcDefinition.DEFAULT);
			
			if (copy != -1) {
				def = NpcDefinition.forId(copy);
				set = true;
				System.out.println("Copying from: " + copy);
			}
			
			if (set) {
				def.setDefaults();
				def.setId(id);
				def.setName(name);
				def.setExamine(examine);
				def.setStats(new int[18]);
			}
			
			JsonElement jsonElement = GSON.toJsonTree(def);
			
			JsonObject object = jsonElement.getAsJsonObject();
			
			object.addProperty("name", name.replaceAll("_", " "));
			
			object.addProperty("examine", examine);
			
			object.addProperty("attackable", aggressive);
			
			object.addProperty("hitpoints", Integer.parseInt(getNextSecond(link, "Hitpoints")));
			
			object.addProperty("attackSpeed", Integer.parseInt(attackSpeedStr.substring(attackSpeedStr.length() - 1)));
			
			object.addProperty("aggressive", aggressive);
			
			object.addProperty("poisonous", poisonous);
			
			object.addProperty("combatLevel", Integer.parseInt(getNextSecond(link, "Combat level")));
			
			object.addProperty("slayerLevel", Integer.parseInt(slayer.select("td[colspan=15]").text()));
			
			jsonElements.add(i, jsonElement);
		}
		
		Elements table2 = doc.getElementsByClass("nestedInfo");
		
		for (int i = 0; i < table2.size(); i++) {
			Element link = table2.get(i);
			
			Elements statElements = link.select("td[style]");
			
			int[] stats = new int[18];
			
			for (int j = 0; j < statElements.size(); j++) {
				stats[j] = Integer.parseInt(statElements.get(j).text());
			}
			
			JsonElement jsonElement = jsonElements.get(i);
			
			JsonObject object = jsonElement.getAsJsonObject();
			
			object.add("stats", GSON.toJsonTree(stats));
		}
		
		String jsonString = GSON.toJson(jsonElements);
		System.out.println(jsonString);
	}
	
}
