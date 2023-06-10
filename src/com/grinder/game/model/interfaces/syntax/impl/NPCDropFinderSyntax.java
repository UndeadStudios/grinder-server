package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.content.pvm.NpcInformation;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.Misc;
import com.grinder.util.TextUtil;

import java.util.*;
import java.util.function.Consumer;

/**
 * Attempts to find npcs with drops by name
 * 
 * @author 2012
 * @author Stan van der Bend
 */
public class NPCDropFinderSyntax implements EnterSyntax {

	private static final int SMART_SEARCH_DELAY = 500;
	private static final int MAXIMUM_DISTANCE_FROM_NAME = 5;
	private static final int MAXIMUM_OPTIONS = 3;
	private static final int MINIMAL_INPUT_LENGTH = 1;

	@Override
	public void handleSyntax(Player player, final String input) {

		if(input == null){
			player.sendMessage("You have entered an invalid input!");
			return;
		}

		final String searchTerm = input.toLowerCase();
		final NpcDefinition definition = NpcDefinition.forName(searchTerm);

		if(definition == null) {

			// if the input is of a minimal length, use an algorithm to detect the closest match.
			if(searchTerm.length() > MINIMAL_INPUT_LENGTH){

				// add a delay so that this can't be exploited by sending artificial packets.
				if (!player.getClickDelay().elapsed(SMART_SEARCH_DELAY)) {
					player.sendMessage("Please wait a few more seconds before trying again.");
					return;
				} else
					player.getClickDelay().reset();

				final Iterator<String> names = NpcDropDefinition.names.keySet().iterator();
				final HashMap<Integer, HashSet<String>> candidates = new LinkedHashMap<>();

				// iterate over all npc drop definition names.
				while (names.hasNext()){

					final String name = names.next();

					final boolean nameStartsWithInput = name.startsWith(searchTerm);
					final boolean nameContainsInput = name.contains(searchTerm);
					final boolean likelyMatch = nameStartsWithInput || nameContainsInput;

					// in case of the name being a likely match, set the distance to 0 or otherwise use an algorithmic evaluation.
					final int distance = likelyMatch ? 0 : TextUtil.calculateLevensteinDistance(name, searchTerm);

					// if the distance is lesser than the maximum required distance
					if(distance < MAXIMUM_DISTANCE_FROM_NAME) {
						candidates.putIfAbsent(distance, new HashSet<>());
						candidates.get(distance).add(name);
					}
					// in the case of the distance being 0 and the possible candidates have reached the maximum, exit the loop.
					if(distance == 0 && candidates.get(0).size() == MAXIMUM_OPTIONS)
						break;
				}

				if(candidates.size() >= 1){

					final DialogueBuilder builder = new DialogueBuilder(DialogueType.OPTION);

					builder.setOptionTitle("Did you mean:");

					int optionsCount = 0;

					for(int i = 0; i < MAXIMUM_DISTANCE_FROM_NAME; i++){

						if(optionsCount+1 == MAXIMUM_OPTIONS)
							break;

						final HashSet<String> weightedNames = candidates.get(i);

						if(weightedNames == null)
							continue;

						for(final String name : weightedNames){

							if(optionsCount+1 == MAXIMUM_OPTIONS)
								break;

							final NpcDefinition npcDefinition = NpcDefinition.forName(name);

							if(npcDefinition == null)
								continue;
							builder.option(optionsCount++, Misc.capitalizeWords(name), CLOSE_INTERFACE.andThen(futurePlayer -> searchNPCDrops(futurePlayer, npcDefinition, name)));
						}
					}
					builder.addCancel();
					builder.start(player);
				} else
					player.sendMessage("No monster found for: @dre@" + Misc.capitalize(searchTerm) + "</col>!");
			} else
				player.sendMessage("Your input length is too short.");
		} else
			searchNPCDrops(player, definition, definition.getName().toLowerCase());

	}

	@Override
	public void handleSyntax(Player player, int input) {

	}

	private static void searchNPCDrops(Player player, NpcDefinition definition, String name){
		if (name.equals("kalphite queen")) {
			definition.setId(6501);
		} else if (name.equals("venenatis")) {
			definition.setId(6504);
		} else if (name.equals("king black dragon")) {
			definition.setId(239);
		} else if (name.equals("chaos elemental")) {
			definition.setId(2054);
		} else if (name.equals("black knight titan")) {
			definition.setId(4067);
		} else if (name.equals("cerberus")) {
			definition.setId(5862);
		} else if (name.equals("commander zilyana")) {
			definition.setId(2205);
		} else if (name.equals("giant mole")) {
			definition.setId(5779);
		} else if (name.equals("ice troll king")) {
			definition.setId(5822);
		} else if (name.equals("jungle demon")) {
			definition.setId(1443);
		} else if (name.equals("k'ril tsutsaroth")) {
			definition.setId(3129);
		} else if (name.equals("the untouchable")) {
			definition.setId(3475);
		} else if (name.equals("kree'arra")) {
			definition.setId(3162);
		} else if (name.equals("kamil")) {
			definition.setId(3458);
		} else if (name.equals("general graardor")) {
			definition.setId(2215);
		} else if (name.equals("dagannoth supreme")) {
			definition.setId(2265);
		} else if (name.equals("dagannoth prime")) {
			definition.setId(2266);
		} else if (name.equals("dagannoth rex")) {
			definition.setId(2267);
		} else if (name.equals("greater skeleton hellhound")) {
			definition.setId(6614);
		}

		final Optional<NpcDropDefinition> dropDefinition = NpcDropDefinition.get(definition.getId());

		if(!dropDefinition.isPresent()) {
			player.sendMessage(name+" doesn't appear to have a drop table.");
			return;
		}

		NpcInformation.display(player, definition, dropDefinition);
	}

	private static final Consumer<Player> CLOSE_INTERFACE = player -> player.getPacketSender().sendInterfaceRemoval();
}
