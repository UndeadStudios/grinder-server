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
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader;

public class ItemDefinitionDumper extends DefinitionDumper {

	public ItemDefinitionDumper() {
		// Load item definitions...
		new ItemDefinitionLoader().run();
	}

	public void dump(String name, int id, int copy) throws IOException {
		Document doc = Jsoup.connect("http://oldschoolrunescape.wikia.com/wiki/" + name).get();

		Elements table = doc.getElementsByClass("wikitable infobox");

		List<JsonElement> jsonElements = new ArrayList<>(table.size());

		for (int i = 0; i < table.size(); i++) {
			Element link = table.get(i);

			String examine = getNextSingle(link, "Examine");
			
			boolean tradeable = getNextSecond(link, "Tradeable").equals("Yes") ? true : false;
			
			boolean equipable = getNextSecond(link, "Equipment").equals("Yes") ? true : false;
			
			boolean stackable = getNextSecond(link, "Stackable items").equals("Yes") ? true : false;
			
			String highAlch = getNextSecond(link, "High Level Alchemy");
			
			boolean alchemy = !highAlch.equals("Cannot be alchemised");
			
			if (alchemy) {
				highAlch = highAlch.substring(highAlch.indexOf(":") + 1, highAlch.indexOf("coins") - 1).replaceAll(",", "");
			}
			
			String lowAlch = alchemy ? getNextSecond(link, "Low Level Alchemy") : new String();
			
			String value = new String();
			
			String dropValue = new String();
			
			if (alchemy) {
				lowAlch = lowAlch.substring(lowAlch.indexOf(":") + 1, lowAlch.indexOf("coins") - 1).replaceAll(",", "");
				
				Document exchangeDoc = Jsoup.connect("http://oldschoolrunescape.wikia.com/wiki/Exchange:" + name).get();
				
				value = exchangeDoc.selectFirst("span[id=GEPrice]").text().replaceAll(",", "");
				
				dropValue = exchangeDoc.selectFirst("a[title=Value]").parent().parent().text();
				
				dropValue = dropValue.substring(dropValue.indexOf(":") + 2).replaceAll(",", "");
			}
			
			String weight = getNextSecond(link, "Weight");
			
			weight = weight.substring(0, weight.indexOf("kg") - 1);
			
			ItemDefinition def = ItemDefinition.forId(id);
			
			if (copy != -1) {
				def = ItemDefinition.forId(copy);
				System.out.println("Copying from: " + copy);
			}

			JsonElement jsonElement = GSON.toJsonTree(def);

			JsonObject object = jsonElement.getAsJsonObject();
			
			object.addProperty("id", id);
			object.addProperty("name", name);
			object.addProperty("examine", examine);
			object.addProperty("tradeable", tradeable);
			object.addProperty("stackable", stackable);
			object.addProperty("value", alchemy ? Integer.parseInt(value) : 0);
			object.addProperty("highAlch", alchemy ? Integer.parseInt(highAlch) : 0);
			object.addProperty("lowAlch", alchemy ? Integer.parseInt(lowAlch) : 0);
			//object.addProperty("weight", new Double(weight));
			
			if (alchemy && !dropValue.equals("Unknown")) {
				object.addProperty("dropValue", Integer.parseInt(dropValue));
			}

			jsonElements.add(i, jsonElement);
		}

		Elements table2 = doc.getElementsByClass("wikitable smallpadding");

		for (int i = 0; i < table2.size(); i++) {
			Element link = table2.get(i);

			Elements statElements = link.select("td[style*=text-align: center; width: 3]");

			int[] stats = new int[14];

			for (int j = 0; j < statElements.size(); j++) {
				String element = statElements.get(j).text().replaceAll("%", "");
				stats[j] = Integer.parseInt(element);
			}

			JsonElement jsonElement = jsonElements.get(i);

			JsonObject object = jsonElement.getAsJsonObject();
			
			object.add("bonuses", GSON.toJsonTree(stats));
		}

		String jsonString = GSON.toJson(jsonElements);
		System.out.println(jsonString);
	}

}
