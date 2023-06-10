package com.grinder.game.content.cluescroll;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grinder.game.World;
import com.grinder.game.content.cluescroll.agent.ClueAgent;
import com.grinder.game.content.cluescroll.scroll.ScrollConstants;
import com.grinder.game.content.cluescroll.scroll.ScrollManager;
import com.grinder.game.content.cluescroll.scroll.type.PuzzleType;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskFactory;
import com.grinder.game.content.cluescroll.task.ClueType;
import com.grinder.game.content.miscellaneous.Emotes;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.util.ItemID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-07
 */
public class ClueScrollManager {

    private final static boolean ENABLED = true;

    private final Player player;
    private final ScrollManager scrollManager;

    public PuzzleType savedPuzzle;
    public int[][] puzzleProgress;
    public int completedPuzzles;
    public long fastestPuzzle;
    public long puzzleStartTime;

    public boolean scrollSuccess;

    public int scrollReroll;

    public int easyScrollCount;
    public int mediumScrollCount;
    public int hardScrollCount;
    public int eliteScrollCount;

    public ClueTask easyScroll;
    public ClueTask mediumScroll;
    public ClueTask hardScroll;
    public ClueTask eliteScroll;

    public ClueScrollManager(Player player) {
        this.player = player;
        scrollManager = new ScrollManager(player);
    }

    public boolean isDoingTask(ClueTask task) {
        return easyScroll == task || mediumScroll == task || hardScroll == task || eliteScroll == task;
    }

    public JsonElement serialize(){

        final JsonObject object = new JsonObject();

        if(savedPuzzle != null)
            object.addProperty("savedPuzzle", savedPuzzle.name());

        if(puzzleProgress != null) {
            final JsonArray nn = new JsonArray();
            for(int[] pp : puzzleProgress){
                final JsonArray n = new JsonArray();
                for(int p  : pp)
                    n.add(p);
                nn.add(n);
            }
            object.add("puzzleProgress", nn);
        }
        if(completedPuzzles > 0) object.addProperty("completedPuzzles", completedPuzzles);
        if(fastestPuzzle > 0) object.addProperty("fastestPuzzle", fastestPuzzle);
        if(scrollReroll > 0) object.addProperty("scrollReroll", scrollReroll);
        if(easyScrollCount > 0) object.addProperty("easyScrollCount", easyScrollCount);
        if(mediumScrollCount > 0) object.addProperty("mediumScrollCount", mediumScrollCount);
        if(hardScrollCount > 0) object.addProperty("hardScrollCount", hardScrollCount);
        if(eliteScrollCount > 0) object.addProperty("eliteScrollCount", eliteScrollCount);

        Optional.ofNullable(easyScroll).ifPresent(scrollTask -> object.add("easyScroll", scrollTask.toJson()));
        Optional.ofNullable(mediumScroll).ifPresent(scrollTask -> object.add("mediumScroll", scrollTask.toJson()));
        Optional.ofNullable(hardScroll).ifPresent(scrollTask -> object.add("hardScroll", scrollTask.toJson()));
        Optional.ofNullable(eliteScroll).ifPresent(scrollTask -> object.add("eliteScroll", scrollTask.toJson()));
        return object;
    }

    public void read(JsonElement element){

        final JsonObject object = element.getAsJsonObject();

        if(object.has("savedPuzzle"))
            savedPuzzle = PuzzleType.valueOf(object.get("savedPuzzle").getAsString());
        if(object.has("puzzleProgress")) {
            final ArrayList<Integer[]> pp = new ArrayList<>();
            for (JsonElement element2 : object.getAsJsonArray("puzzleProgress")) {
                final ArrayList<Integer> p = new ArrayList<>();
                for (JsonElement element1 : element2.getAsJsonArray())
                    p.add(element1.getAsInt());
                pp.add(p.toArray(new Integer[]{}));
            }
            final Iterator<Integer[]> iterator = pp.iterator();
            puzzleProgress = new int[pp.size()][];
            for(int i = 0; i < puzzleProgress.length; i++){
                Integer[] arr =iterator.next();
                puzzleProgress[i] = new int[arr.length];
                for(int j = 0; j < arr.length; j++){
                    puzzleProgress[i][j] = arr[j];
                }
            }
        }
        if(object.has("completedPuzzles")) completedPuzzles = object.get("completedPuzzles").getAsInt();
        if(object.has("fastestPuzzle")) fastestPuzzle = object.get("fastestPuzzle").getAsInt();
        if(object.has("scrollReroll")) scrollReroll = object.get("scrollReroll").getAsInt();
        if(object.has("easyScrollCount")) easyScrollCount = object.get("easyScrollCount").getAsInt();
        if(object.has("mediumScrollCount")) mediumScrollCount = object.get("mediumScrollCount").getAsInt();
        if(object.has("hardScrollCount")) hardScrollCount = object.get("hardScrollCount").getAsInt();
        if(object.has("eliteScrollCount")) eliteScrollCount = object.get("eliteScrollCount").getAsInt();
        if(object.has("easyScroll")) easyScroll = ClueTaskFactory.getInstance().deserialize(object.get("easyScroll")).orElse(null);
        if(object.has("mediumScroll")) mediumScroll = ClueTaskFactory.getInstance().deserialize(object.get("mediumScroll")).orElse(null);
        if(object.has("hardScroll")) hardScroll = ClueTaskFactory.getInstance().deserialize(object.get("hardScroll")).orElse(null);
        if(object.has("eliteScroll")) eliteScroll = ClueTaskFactory.getInstance().deserialize(object.get("eliteScroll")).orElse(null);
    }

    public boolean handleNPCAction(int option, NPC npc){

        if(!ENABLED)
            return false;

        if(option == 1){
            switch (npc.getId()){
                case ScrollConstants.NPC_URI_AGENT_ID :
                    if(npc instanceof ClueAgent)
                        if (scrollManager.interactWithAgent((ClueAgent) npc))
                            return true;
                    break;
                case ScrollConstants.NPC_KING_ROALD:

                    if(ClueTaskFactory.executeOperation(player, ClueType.NPC_CLICK, npc.getId(), option))
                        return true;

                    if (!scrollManager.hasBown(10_000)) {
                        Optional<NPC> searchResult = World.findNpcById(3774);
                        if(searchResult.isPresent()){
                            final NPC guard = searchResult.get();
                            guard.say("Hey!");
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setText("Where are your means to the King!")
                                    .setNpcChatHead(3774)
                                    .setExpression(DialogueExpression.ANGRY)
                                    .start(player);
                            guard.setPositionToFace(player.getPosition());
                        }
                    } else
                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                             .setText("Hey " + player.getUsername(), "Sorry I'm too busy to talk to you right now.")
                                .setNpcChatHead(ScrollConstants.NPC_KING_ROALD)
                                .start(player);
                    return true;
            }
        }

        return ClueTaskFactory.executeOperation(player, ClueType.NPC_CLICK, npc.getId(), option);
    }

    public void onEmote(Emotes.EmoteData emoteData){

        if(!ENABLED)
            return;

        if(emoteData == Emotes.EmoteData.BOW)
            scrollManager.onBow();

        scrollManager.setLastEmote(emoteData.ordinal());

        //TODO: verify ordinal points to the correct EmoteData, old grinder had this shit done differently.
        ClueTaskFactory.executeOperation(player, ClueType.DANCE, emoteData);
    }

    public boolean handleEquipAction(int itemId){

        if(!ENABLED)
            return false;

        final ItemDefinition definition = ItemDefinition.forId(itemId);
        final String name = Optional.ofNullable(definition).map(ItemDefinition::getName).orElse("").toLowerCase();
    
        switch(itemId){
            case ScrollConstants.ITEM_ELITE_SCROLL:
                scrollManager.scanOperation();
                return true;
            case ScrollConstants.ITEM_EASY_SCROLL:
            case ScrollConstants.ITEM_MEDIUM_SCROLL:
            case ScrollConstants.ITEM_HARD_SCROLL:
              /*  player.sendMessage("You haven't started this clue yet.");
                else if(save.remaining == 1)
                player.sendMessage("There is 1 step remaining in this clue.");
            else
                player.sendMessage("There are " + save.remaining + " steps remaining in this clue.");*/
                player.sendMessage("There is nothing to scan!");
                return true;
        }

        if (definition != null && definition.getId() != 19941 && definition.getId() != 15254) {
            if(name.contains("casket") || name.contains("clue scroll")) {
                player.sendMessage("There is nothing to scan!");
                return true;
            }
        }
    	return false;
    }

    public boolean handleObjectAction(int option, Position position){

        if(!ENABLED)
            return false;

        final int objectX = position.getX();
        final int objectY = position.getY();
        return ClueTaskFactory.executeOperation(player, ClueType.OBJECT_CLICK, objectX, objectY, option);
    }

    public boolean handleItemDrop(int interfaceId, int itemId, int itemSlot) {

        if(!ENABLED)
            return false;

        if(interfaceId == 6980){
            scrollManager.move(itemId, itemSlot);
            return true;
        }

        return false;
    }

    public boolean handleItemAction(int option, int itemId, int itemSlot){

        if(!ENABLED)
            return false;

        if (option == 1) {
            switch (itemId) {

                case ScrollConstants.ITEM_PUZZLE_SOLVER:
                    scrollManager.solvePuzzle();
                    return true;
                case ScrollConstants.ITEM_EASY_SCROLL_BOX:
                case ScrollConstants.ITEM_MEDIUM_SCROLL_BOX:
                case ScrollConstants.ITEM_HARD_SCROLL_BOX:
                case ScrollConstants.ITEM_ELITE_SCROLL_BOX:
                    scrollManager.openScrollBox(itemId, itemSlot);
                    return true;

                case ScrollConstants.ITEM_BRIDGE_PUZZLE_BOX:
                case ScrollConstants.ITEM_PLANE_PUZZLE_BOX:
                case ScrollConstants.ITEM_CASTLE_PUZZLE_BOX:
                case ScrollConstants.ITEM_TREE_PUZZLE_BOX:
                case ScrollConstants.ITEM_TROLL_PUZZLE_BOX:
                case ScrollConstants.ITEM_ELITE_PUZZLE_BOX:
                    scrollManager.openPuzzle(itemId);
                    return true;
                case ItemID.REWARD_CASKET_EASY_:
                case ItemID.REWARD_CASKET_MEDIUM_:
                case ItemID.REWARD_CASKET_HARD_:
//                case 2714:
//                case 2802:
//                case 2724:
                case ScrollConstants.ITEM_ELITE_SCROLL_COMPLETION_CASKET:
                    ClueTaskFactory.getInstance().openCasket(player, itemId, itemSlot);
                    return true;
                case 2717:
                case 2806:
                case 2728:
                case ScrollConstants.ITEM_ELITE_SCROLL_REWARD_CASKET:
                    ClueTaskFactory.getInstance().findScroll(player, itemSlot, itemId);
                    return true;
                case 2677 :
                case 2722 :
                case 2801 :
                case ScrollConstants.ITEM_ELITE_SCROLL:
                    ClueTaskFactory.getInstance().openScroll(player, itemId);
                    return true;
            }
        }
        return false;
    }

    public ScrollManager getScrollManager() {
        return scrollManager;
    }

    public Optional<ClueTask> findTask(Class<ClueTask> taskClass) {
        return Stream.of(easyScroll, mediumScroll, hardScroll, eliteScroll)
                .filter(Objects::nonNull)
                .filter(task -> task.getClass().equals(taskClass))
                .findFirst();
    }

    public Optional<ClueTask> findTask() {
        return Stream.of(easyScroll, mediumScroll, hardScroll, eliteScroll)
                .filter(Objects::nonNull)
                .findFirst();
    }
}
