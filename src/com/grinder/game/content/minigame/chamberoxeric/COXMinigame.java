package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.content.minigame.Minigame;
import com.grinder.game.content.minigame.MinigameRestriction;
import com.grinder.game.content.minigame.chamberoxeric.room.COXMap;
import com.grinder.game.content.minigame.chamberoxeric.room.COXPassage;
import com.grinder.game.content.minigame.chamberoxeric.room.icedemon.IceDemonCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.MutadilesCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.mystics.MysticsCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.shamans.ShamanCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.tekton.TektonCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vanguard.VanguardCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio.VasaNistirioCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vespula.VespulaCOXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXMinigame extends Minigame {

    private static final Position PASS_THE_SHIELD = new Position(3233, 5730);

    private static final Item RAKE = new Item(5341);
    private static final Item SPADE = new Item(952);
    private static final Item SEED_DIBBER = new Item(5343);

    private static final Item[] TOOLS = {
            RAKE, SPADE, SEED_DIBBER,
    };
    private static final Item EMPTY_GOURD_VIAL = new Item(20800);
    private static final Item WATER_FILLED_GOURD_VIAL = new Item(20801);
    private static final Animation TAKE = new Animation(832);

    @Override
    public void start(Player player) {
        Position pos = COXMap.START.position.clone();

        int height = player.getIndex() * 4;

        pos.transform(0, 0, height);

        COXInstance instance = new COXInstance();

        player.getCOX().getParty().sharedStorage.resetItems();

        for (Player p : player.getCurrentClanChat().players()) {
            p.moveTo(pos);

            p.getCOX().setRaidParty(player.getCOX().getParty());
            p.instance = instance;

            p.getCOX().points = 0;

            p.getCOX().storage.privateStorage.resetItems();

            p.getPacketSender().sendMessage("The raid has begun!");

            TaskManager.submit(new Task(1) {
                @Override
                protected void execute() {
                    if(p.instance == null) {
                        p.getPacketSender().sendWalkableInterface(-1);
                        stop();
                        return;
                    }

                    COXInterface.sendIngameWidget(p);
                }
            });
        }

        player.getCOX().getParty().mutadiles = new MutadilesCOXRoom(player);
        player.getCOX().getParty().tekton = new TektonCOXRoom(player);
        player.getCOX().getParty().iceDemon = new IceDemonCOXRoom(player);
        player.getCOX().getParty().shaman = new ShamanCOXRoom(player);
        player.getCOX().getParty().mystics = new MysticsCOXRoom(player);
        player.getCOX().getParty().vanguard = new VanguardCOXRoom(player);
        player.getCOX().getParty().vasa = new VasaNistirioCOXRoom(player);
        player.getCOX().getParty().vespula = new VespulaCOXRoom(player);
        player.getCOX().getParty().olm = new OlmCOXRoom(player);

        player.getCOX().getParty().time.reset();
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public MinigameRestriction getRestriction() {
        return MinigameRestriction.NONE;
    }

    @Override
    public String getName() {
        return "Raids 1: Chambers of Xeric";
    }

    @Override
    public int[] getUnuseablePrayer() {
        return new int[0];
    }

    @Override
    public boolean canUsePresets() {
        return false;
    }

    @Override
    public boolean removeItems() {
        return false;
    }

    @Override
    public boolean canUnEquip() {
        return true;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29789:
                if(object.getPosition().sameAs(new Position(3352, 5196))) {
                    COXPassage.move(player, COXMap.SKILLING_AREA.position);
                } else if(object.getPosition().sameAs(new Position(3310, 5306, 1))) {
                     new DialogueBuilder(DialogueType.OPTION)
                                    .firstOption("Tekton", $ -> COXPassage.move(player, TektonCOXRoom.PASSAGE_ENTRY))
                                    .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                                                    .start(player);
                } else if(object.getPosition().sameAs(new Position(3309, 5274, 1))) {
                    COXPassage.move(player, COXMap.ICE_DEMON.position);
                } else if(object.getPosition().sameAs(new Position(3268, 5365))) {
                    new DialogueBuilder(DialogueType.OPTION)
                            .firstOption("Lizard Shaman", $ -> COXPassage.move(player, COXMap.SHAMAN.position))
                            .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                            .start(player);
                } else if(object.getPosition().sameAs(new Position(3311, 5276))) {
                    new DialogueBuilder(DialogueType.OPTION)
                            .firstOption("Skeletal Mystics", $ -> COXPassage.move(player, COXMap.SKELETAL_MYSTIC.position))
                            .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                            .start(player);
                } else if(object.getPosition().sameAs(new Position(3354, 5264))) {
                    new DialogueBuilder(DialogueType.OPTION)
                            .firstOption("Vasa Nistirio", $ -> COXPassage.move(player, COXMap.VASA_NISTIRIO.position))
                            .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                            .start(player);
                } else if(object.getPosition().sameAs(new Position(3265, 5295))) {
                    new DialogueBuilder(DialogueType.OPTION)
                            .firstOption("Vespula", $ -> COXPassage.move(player, COXMap.VESPULA.position))
                            .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                            .start(player);
                } else if(object.getPosition().sameAs(new Position(3264, 5295, 2))) {
                    new DialogueBuilder(DialogueType.OPTION)
                            .firstOption("Great Olm", $ -> COXPassage.move(player, COXMap.OLM.position))
                            .secondOption("Skilling area", $ -> COXPassage.move(player, COXMap.SKILLING_AREA.position))
                            .start(player);
                }
                return true;
            case 29879:
                COXPassage.move(player, PASS_THE_SHIELD);

                player.getCOX().getParty().olm.init();
                return true;
            case 29771:
                if (type == 1) {
                    player.getInventory().addItemSet(TOOLS);
                    player.getPacketSender().sendMessage("You select a group of tools.");
                } else if (type == 2) {
                    player.getInventory().add(RAKE);
                    player.getPacketSender().sendMessage("You pick a rake.");
                } else if (type == 3) {
                    player.getInventory().add(SPADE);
                    player.getPacketSender().sendMessage("You pick a spade.");
                } else if (type == 4) {
                    player.getInventory().add(SEED_DIBBER);
                    player.getPacketSender().sendMessage("You pick a seed dibber.");
                }
                player.performAnimation(TAKE);
                return true;
            case 29772:
                if (type == 1) {
                    player.getInventory().add(EMPTY_GOURD_VIAL);
                    player.getPacketSender().sendMessage("You pick an empty gourd.");
                } else if (type == 2) {
                    player.getInventory().add(EMPTY_GOURD_VIAL.getId(), 10);
                    player.getPacketSender().sendMessage("You pick 10 empty gourds.");
                }
                return true;
            case 29878:
                int amount = player.getInventory().getAmount(EMPTY_GOURD_VIAL);

                if (amount == 0) {
                    player.getPacketSender().sendMessage("You don't have any empty gourds to fill with water.");
                    return true;
                }

                player.getInventory().delete(EMPTY_GOURD_VIAL.getId(), amount);
                player.getInventory().add(WATER_FILLED_GOURD_VIAL.getId(), amount);

                player.performAnimation(TAKE);

                player.getPacketSender().sendMessage("You fill the gourds.");
                return true;
        }
        return false;
    }
}
