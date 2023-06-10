package com.grinder.game.definition.factory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcStatsDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-08-20
 */
public class NpcStatsFactory {

    private static NpcStatsDefinition[] npcStatsDefinitions;
    private static Set<JsonObject> missingNpcs = new HashSet<>();
    private static int noMatchesCount = 0;
    private static int matchesCount = 0;
    private static int noIdMatchCount = 0;

    public static Optional<NpcStatsDefinition> produce(final NpcDefinition definition){
        for(NpcStatsDefinition stats : npcStatsDefinitions){
            if(stats.getIds().contains(definition.getId()) ||
                    (stats.getName().equalsIgnoreCase(definition.getName())
                            && stats.getCombatLevel() == definition.getCombatLevel())){
                stats.getIds().add(definition.getId());
                return Optional.of(stats);
            }
        }
        noMatchesCount++;
        return Optional.empty();
    }

    public static void printDebug(){
        System.out.println("nomatch = "+noMatchesCount);
//        File file = Paths.get(GameConstants.DEFINITIONS_DIRECTORY + "npc_stats.json").toFile();
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            FileWriter writer = new FileWriter(file);
//            new GsonBuilder().setPrettyPrinting().create().toJson(npcStatsDefinitions, writer);
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void setNpcStatsDefinitions(NpcStatsDefinition[] npcStatsDefinitions) {
        NpcStatsFactory.npcStatsDefinitions = npcStatsDefinitions;
    }
}
