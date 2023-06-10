package com.grinder.game.model;

import java.util.HashMap;
import java.util.Map;

import com.grinder.game.content.miscellaneous.Emotes;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;

public enum Skillcape {
    ATTACK(new int[]{9747, 9748, 10639},
            4959, 823, 7),
    DEFENCE(new int[]{9753, 9754, 10641},
            4961, 824, 10),
    STRENGTH(new int[]{9750, 9751, 10640},
            4981, 828, 25),
    CONSTITUTION(new int[]{9768, 9769, 10647},
            4971, 833, 12),
    RANGED(new int[]{9756, 9757, 10642},
            4973, 832, 12),
    PRAYER(new int[]{9759, 9760, 10643, 15806, 15807, 15808, 15809, 15810, 15811, 15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820, 15821, 15822, 15823, 15855, 15856},
            4979, 829, 15),
    MAGIC(new int[]{9762, 9763, 10644},
            4939, 813, 6),
    COOKING(new int[]{9801, 9802, 10658},
            4955, 821, 36),
    WOODCUTTING(new int[]{9807, 9808, 10660},
            4957, 822, 25),
    FLETCHING(new int[]{9783, 9784, 10652},
            4937, 812, 20),
    FISHING(new int[]{9798, 9799, 10657},
            4951, 819, 19),
    FIREMAKING(new int[]{9804, 9805, 10659},
            4975, 831, 14),
    CRAFTING(new int[]{9780, 9781, 10651},
            4949, 818, 15),
    SMITHING(new int[]{9795, 9796, 10656},
            4943, 815, 23),
    MINING(new int[]{9792, 9793, 10655},
            4941, 814, 8),
    HERBLORE(new int[]{9774, 9775, 10649},
            4969, 835, 16),
    AGILITY(new int[]{9771, 9772, 10648},
            4977, 830, 8),
    THIEVING(new int[]{9777, 9778, 10650},
            4965, 826, 16),
    SLAYER(new int[]{9786, 9787, 10653},
            4967, -1, 8),
    FARMING(new int[]{9810, 9811, 10661},
            4963, -1, 16),
    RUNECRAFTING(new int[]{9765, 9766, 10645},
            4947, 817, 10),
    CONSTRUCTION(new int[]{9789, 9790, 10654},
            4953, 820, 16),
    HUNTER(new int[]{9948, 9949, 10646},
            5158, 907, 14),
    QUEST_POINT(new int[]{9813, 9814, 10662, 13068},
            4945, 816, 19),
    
    
	MAX_CAPE(new int[]{13329, 13337, 13331, 13333, 13335, 21285, 13342, 20760, 24133, 24134, 24233, 24324, 24135, 24232, 21776, 21780, 21784, 21898, 15195},
			Emotes.MAX_CAPE_ANIM.getId(), Emotes.MAX_CAPE_GRAPHIC.getId(), 14),
    
    ;

    private static final Map<Integer, Skillcape> dataMap = new HashMap<>();

    static {
        for (Skillcape data : Skillcape.values()) {
            for (Item item : data.item) {
                dataMap.put(item.getId(), data);
            }
        }
    }

    private final Item[] item;
    private final Animation animation;
    private final Graphic graphic;
    private final int delay;

    Skillcape(int[] itemId, int animationId, int graphicId, int delay) {
        item = new Item[itemId.length];
        for (int i = 0; i < itemId.length; i++) {
            item[i] = new Item(itemId[i]);
        }
        animation = new Animation(animationId);
        graphic = new Graphic(graphicId);
        this.delay = delay;
    }

    public static Skillcape forId(int id) {
        return dataMap.get(id);
    }

    public Animation getAnimation() {
        return animation;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public int getDelay() {
        return delay;
    }

    public static void claim200mCapes(Player player, NPC npc) {

        // If the player doesn't have any skills of 200m experience return
        //skills.experience[skill.ordinal()]
        //         for (int i = 0; i < SkillConstants.SKILL_COUNT; i++) {


        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("I want to claim my 200M experience capes.")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
                .setText("The 200,000,000 experience milestone capes are", "for players who master their skills to the maximum.", "The cape provides the best stats and benefits.")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
                .setText("You don't seem to master any skill yet.", "Come back later when you've mastered any skill.");

        // If the player has one or more skills 200m experience
        // } else {

        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("I want to claim my 200M experience capes.")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
                .setText("The 200,000,000 experience milestone capes are", "for players who master their skills to the maximum.", "The cape provides the best stats and benefits.")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
                .setText("I am glad to meet you master!", "Please accept this as a gift from me.")
                .add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15774, 200)
                .setText("The Wise old man hands you the master cape.")
                .setAction(player2 -> {

                    new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("Please take care of it master!")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
                                    .start(player);
                    // Loop for all skills with 200m experience
                    // Get total amount of capes and make sure u have enough inventory space or else return a dialogue
                    // Add capes
                    ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(15774, 1));

                }).start(player);
    }
}
