package com.grinder.game.content.miscellaneous;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcStatsDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;

import java.util.Arrays;
import java.util.HashMap;

public class NPCStatsViewer {

    private static final int MAIN_INTERFACE = 25_700;

    private static final String[][] TYPE = {
            {"Attack", "Strength", "Defence", "Magic", "Range"},
            {"Stab", "Slash", "Crush", "Magic", "Ranged"}
    };

    public enum Boss {

        SLASH_BASH(882, "", "Poison/Venom", "Melee/Range/Magic", "None."),
        VENENATIS(6504, "", "Poison/Venom", "Magic", "High defensive stats! Using Verac's armour, or Craw's bow is recommended."),
        //Callisto
        //Hydra
        //Vorkath
        //Zulrah
        NEX(11278, "None", "Poison/Venom", "Melee/Magic", "Be careful and keep moving!"),
        MUTANT_TARN(6477, "Crush/Range", "Poison/Venom", "Melee/Range", "Be careful from the sandstorm! Keep moving!"),
        THE_UNTOUCHABLE(3475, "Magic/Range", "Poison/Venom", "Melee", "Be careful of teleblock!"),
        KING_BLACK_DRAGON(3475, "Magic/Range", "Poison/Venom", "Magic/Melee", "Dragonfire shield & Antifire potions are recommended."),
        KING_BLACK_DRAGON_SPIRIT(6502, "Magic/Range", "Poison/Venom", "Magic/Melee", "Dragonfire shield & Antifire potions are recommended."),
        BLACK_KNIGHT_TITAN(4067, "Magic/Stab", "None", "Magic/Melee", "Be careful of red skull! Heavy armour is recommended."),
        CORPOREAL_BEAST(319, "Spears/Hastas", "Poison/Venom", "Magic/Melee", "A team of 4 or more players is recommended."),
        KALPHITE_QUEEN(6500, "Melee Attacks", "None", "Magic", "Verac's armour is recommended."),
        KALPHITE_QUEEN_2(6501, "None", "None", "Magic", "Toxic blowpipe is recommended."),
        KALPHITE_QUEEN_ID(963, "Melee Attacks", "None", "Magic", "Verac's armour is recommended."),
        KALPHITE_QUEEN_2_ID(965, "None", "None", "Magic", "Toxic blowpipe is recommended.")
        ;


        private int id;
        private String weakness;
        private String immunity;
        private String protection;
        private String note;

        Boss(int id, String weakness, String immunity, String protection, String note) {
            this.id = id;
            this.weakness = weakness;
            this.immunity = immunity;
            this.protection = protection;
            this.note = note;
        }

        private static final HashMap<Integer, Boss> FOR_ID = new HashMap<>();

        static {
            for (Boss b : values()) {
                FOR_ID.put(b.id, b);
            }
        }
    }

    public static boolean view(Player player, int id, NPC npc) {
        NpcDefinition def = NpcDefinition.forId(id); // Old Stats

        final NpcStatsDefinition statsDefinition = npc.getStatsDefinition(); // New Stats

        if (def == null) {
            return false;
        }

        if (def.getCombatLevel() < 250) {
            return false;
        }


        if (Boss.FOR_ID.get(id) != null) {
            Boss boss = Boss.FOR_ID.get(id);
            player.getPacketSender().sendString(MAIN_INTERFACE + 27, "Weakness: @whi@" + boss.weakness);
            player.getPacketSender().sendString(MAIN_INTERFACE + 28, "Immunity: @whi@" + boss.immunity);
            player.getPacketSender().sendString(MAIN_INTERFACE + 29, "Protection Prayer: @whi@" + boss.protection);
            player.getPacketSender().sendString(MAIN_INTERFACE + 31, "Extra Notes: @whi@" + boss.note);
        } else {
            player.getPacketSender().sendString(MAIN_INTERFACE + 27, "Weakness: @whi@None");
            player.getPacketSender().sendString(MAIN_INTERFACE + 28, "Immunity: @whi@None");
            player.getPacketSender().sendString(MAIN_INTERFACE + 29, "Protection Prayer: @whi@None");
            player.getPacketSender().sendString(MAIN_INTERFACE + 31, "Extra Notes: @whi@None. ");
        }

        player.getPacketSender().sendString(MAIN_INTERFACE + 6, def.getName());

        int attackStats = MAIN_INTERFACE + 11;
        int defenceStats = MAIN_INTERFACE + 18;

        player.getPacketSender().sendString(MAIN_INTERFACE + 10, "Hitpoints: @whi@" + def.getHitpoints());
        player.getPacketSender().sendString(MAIN_INTERFACE + 23, "");
        //System.out.println(statsDefinition);
        if(statsDefinition != null) { // Load new, otherwise old
            for (int i = 0; i < TYPE[0].length; i++) {
                player.getPacketSender().sendString(attackStats + i, TYPE[0][i] + ": @whi@" + (int) statsDefinition.getCombatStats().getAttackStats()[i]);
            }

            for (int i = 0; i < TYPE[1].length; i++) {
                player.getPacketSender().sendString(defenceStats + i, TYPE[1][i] + ": @whi@" + (int) statsDefinition.getCombatStats().getDefenceStats()[i]);
            }
        } else {
            for (int i = 0; i < TYPE[0].length; i++) {
                player.getPacketSender().sendString(attackStats + i, TYPE[0][i] + ": @whi@" + def.getStats()[i]);
            }

            for (int i = 0; i < TYPE[1].length; i++) {
                player.getPacketSender().sendString(defenceStats + i, TYPE[1][i] + ": @whi@" + def.getStats()[10 + i]);
            }
        }


        player.getPacketSender().sendInterface(MAIN_INTERFACE);
        return true;
    }
}
