package com.grinder.game.content.skill.skillable.impl.slayer.superior;

import com.grinder.game.World;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMonsterType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Optional;

/**
 * @author Luka Furlan, luka.furlan9@gmail.com
 * @since 15/7/2020
 */
public class SuperiorSlayerManager {

    /**
     * The chance of spawning the superior slayer monster
     */
    private final static float SUPERIOR_SLAYER_MONSTER_SPAWN_CHANCE = 5.0F; // Originally 0.5F
    /**
     * First superior slayer monster drop table
     */
    private final static Item[] FIRST_SUPERIOR_DROP_TABLE = new Item[]{
            new Item(ItemID.IMBUED_HEART), new Item(ItemID.DUST_BATTLESTAFF), new Item(ItemID.MIST_BATTLESTAFF)
    };
    /**
     * Second superior slayer monster drop table
     */
    private final static Item[] SECOND_SUPERIOR_DROP_TABLE = new Item[]{
            new Item(ItemID.ETERNAL_GEM)
    };

    /**
     * Checks if superior slayer monster exists for
     * this specific slayer monster and spawns it if
     * the roll hits the 1/200 chance
     *
     * @param player        {@link Player}
     * @param slayerMonster {@link SlayerMonsterType}
     * @param position      {@link Position}
     */
    public static void handleSuperiorMonster(Player player, SlayerMonsterType slayerMonster, Position position) {
        if (!player.getSlayer().getUnlocked()[SlayerRewards.Rewards.BIGGER_AND_BADDER.ordinal()]) {
            return;
        }

        Optional<SuperiorSlayerMonsters> optionalSuperiorSlayerMonster = SuperiorSlayerMonsters.forSlayerMonster(slayerMonster);

        optionalSuperiorSlayerMonster.ifPresent(superiorSlayerMonster -> {
            if (Misc.randomChance(SUPERIOR_SLAYER_MONSTER_SPAWN_CHANCE)) {
                spawnSuperiorMonster(player, superiorSlayerMonster, position);
            }
        });
    }

    /**
     * Spawns the superior slayer monster on the
     * same position where the normal slayer monster died
     * <p>
     * The superior slayer monster can only be killed by the
     * original killer
     *
     * @param player                {@link Player}
     * @param superiorSlayerMonster {@link SuperiorSlayerMonsters}
     * @param position              {@link Position}
     */
    private static void spawnSuperiorMonster(Player player, SuperiorSlayerMonsters superiorSlayerMonster, Position position) {
        SuperiorSlayerMonster superiorSlayerMonsterNPC
                = new SuperiorSlayerMonster(superiorSlayerMonster.getNpcId(), position, player);

        World.getNpcAddQueue().add(superiorSlayerMonsterNPC);

        player.sendMessage("<col=ff0000>A superior foe has appeared...</col>");
    }

    /**
     * If npc is a superior slayer monster, then it drops the ensouled
     * head (if that slayer monster has one), and rolls two superior
     * drop tables.
     *
     * @param player {@link Player}
     * @param npc    {@link NPC}
     */
    public static void handleSuperiorSlayerMonsterDrop(Player player, NPC npc) {
        Optional<SuperiorSlayerMonsters> optionalSuperiorSlayerMonster = SuperiorSlayerMonsters.forId(npc.getId());

        optionalSuperiorSlayerMonster.ifPresent(superiorSlayerMonster -> {
            SlayerMonsterType slayerMonster = superiorSlayerMonster.getSlayerMonster();

            final double rollChance = getRollChance(slayerMonster.getRequiredSlayerLevel());

            dropEnsouledHead(player, npc, superiorSlayerMonster);

            rollDropTable(player, npc, rollChance, FIRST_SUPERIOR_DROP_TABLE);
            rollDropTable(player, npc, rollChance, SECOND_SUPERIOR_DROP_TABLE);
        });
    }

    /**
     * Drops the ensouled head if it exists for specific superior
     * slayer monster
     *
     * @param player {@link Player}
     * @param npc    {@link NPC}
     */
    private static void dropEnsouledHead(Player player, NPC npc, SuperiorSlayerMonsters superiorSlayerMonster) {
        Optional<Integer> optionalEnsouledHeadId = superiorSlayerMonster.getEnsouledHeadId();

        optionalEnsouledHeadId.ifPresent(ensouledHeadId -> {
            ItemOnGroundManager.register(player, new Item(ensouledHeadId), npc.getPosition());
        });
    }

    /**
     * Drops one of rewards from the dropTable array
     * if player is lucky.
     *
     * @param player     {@link Player}
     * @param npc        {@link NPC}
     * @param rollChance {@link Double}
     * @param dropTable  {@link Item[]}
     */
    private static void rollDropTable(Player player, NPC npc, double rollChance, Item[] dropTable) {
        if (Misc.randomChances(rollChance * 5)) {
            final int totalRewards = dropTable.length;

            final int roll = Misc.randomInclusive(0, 7);

            if (roll < totalRewards) {
                Item reward = dropTable[roll];

                NpcDefinition npcDefinition = NpcDefinition.forId(npc.getId());
                ItemDefinition itemDefinition = reward.getDefinition();

                PlayerUtil.broadcastMessage("<img=783> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just received @dre@" + itemDefinition.getName() + "s</col> as a rare drop from @dre@" + npcDefinition.getName() + "</col>!");
                ItemOnGroundManager.register(player, reward, npc.getPosition());
            }
        }
    }

    /**
     * Algorithm that determines chance of drop
     *
     * @param slayerRequirement {@link Integer}
     * @return {@link Double}
     */
    private static double getRollChance(int slayerRequirement) {
        return 100 / (200 - (Math.pow((slayerRequirement + 55), 2) / 125));
    }

}
