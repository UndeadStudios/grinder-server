package com.grinder.net.packet.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.concurrent.TimeUnit;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

public class ExamineItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int itemId = packetReader.readShort();
        int interfaceId = packetReader.readShort();
        //System.out.println(interfaceId);
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true)) {
            return;
        }
        ItemDefinition def = ItemDefinition.forId(itemId);

        if (player.getRights() == PlayerRights.DEVELOPER && player.getUsername().equalsIgnoreCase("dexter")) {
            String output = "private static final Item " + def.getName().replaceAll(" ", "_").replaceAll("'", "").toUpperCase() + " = new Item(" + itemId + ");";
            System.out.println(output);
        }
        if (itemId == COINS) {
            int amount = player.getInventory().getAmount(itemId);
            if (interfaceId >= -15486 && interfaceId <= -15486 + BankConstants.TOTAL_BANK_TABS) {
                for (Bank bank : player.getBanks()) {
                    if (bank.contains(COINS)) {
                        amount = bank.getAmount(COINS);
                        break;
                    }
                }
            }

            player.getPacketSender().sendMessage(amount > 99999 ? Misc.insertCommasToNumber(amount) + " x " + ItemDefinition.forId(itemId).getName() : "Lovely money!", 1000);
            return;
        }

        if (itemId == BLOOD_MONEY) {
            int amount = player.getInventory().getAmount(itemId);
            if (interfaceId >= -15486 && interfaceId <= -15486 + BankConstants.TOTAL_BANK_TABS) {
                for (Bank bank : player.getBanks()) {
                    if (bank.contains(BLOOD_MONEY)) {
                        amount = bank.getAmount(BLOOD_MONEY);
                        break;
                    }
                }
            }

            player.getPacketSender().sendMessage(amount > 99999 ? Misc.insertCommasToNumber(amount) + " x " + ItemDefinition.forId(itemId).getName() : "These can be taken to Leagues Tutor in Edgeville.", 1000);
            return;
        }

        ItemDefinition itemDef = ItemDefinition.forId(itemId);
        if (itemDef != null) {

            if (ItemValueDefinition.Companion.getValue(itemId, ItemValueType.ITEM_PRICES) > 0) {

                int alchValue = (int) (ItemValueDefinition.Companion.getValue(itemId, ItemValueType.PRICE_CHECKER) / 7D);
                if (alchValue >= 200000) {
                    alchValue /= 2.5;
                }
                if (itemDef.getId() == ItemID.DRAGON_GODSWORD) {
                    alchValue /= 3;
                }
                if (itemDef.getId() == ItemID.WRATH_RUNE || itemDef.getId() == ItemID.MAGIC_SEED) {
                    alchValue /= 5;
                }
                if (itemDef.getId() == ItemID.RING_OF_WEALTH || itemDef.getId() == ItemID.RING_OF_WEALTH_2 || itemDef.getId() == ItemID.TOKTZ_XIL_UL) {
                    alchValue /= 17;
                } else if (itemDef.getId() == 861 || itemDef.getId() == 862 || itemDef.getId() == 2497 || itemDef.getId() == 2498 || itemDef.getId() == 2503 || itemDef.getId() == 2504 || (itemDef.getId() >= 8901 && itemDef.getId() <= 8922)) {
                    alchValue /= 4;
                } else if (itemDef.getId() == 22481 || itemDef.getId() == 22482) {
                    alchValue /= 3;
                } else if (itemDef.getId() == ItemID.AMETHYST_JAVELIN || itemDef.getId() == ItemID.AMETHYST_JAVELIN_P_ || itemDef.getId() == ItemID.AMETHYST_JAVELIN_P_PLUS_ || itemDef.getId() == ItemID.AMETHYST_JAVELIN_P_PLUS_PLUS_) {
                    alchValue /= 3;
                }

                alchValue *= 2;
                if (alchValue >= 60000000) {
                    alchValue /= 4.5;
                }
                if (itemDef.getId() == ItemID.MONKS_ROBE_G_ || itemDef.getId() == ItemID.MONKS_ROBE_TOP_G_ || itemDef.getId() == ItemID.MONKS_ROBE_TOP_G_2 || itemDef.getId() == ItemID.MONKS_ROBE_G_2) {
                    alchValue /= 5;
                }
                player.getPacketSender().sendMessage(ItemDefinition.forId(itemId).getName() + " is worth @red@" +
                        Misc.insertCommasToNumber("" + ItemValueDefinition.Companion.getValue(itemId, ItemValueType.PRICE_CHECKER)) + "@bla@ on death. High alch: @red@" + Misc.insertCommasToNumber("" + alchValue), 1000);
            }
            if (itemDef.getExamine().isEmpty() || itemDef.getExamine().toLowerCase().contains("unknown")) {
                if (itemDef.getName().toLowerCase().startsWith("a") || itemDef.getName().toLowerCase().startsWith("e") || itemDef.getName().toLowerCase().startsWith("i") ||
                        itemDef.getName().toLowerCase().startsWith("o") || itemDef.getName().toLowerCase().startsWith("u")) {
                    player.getPacketSender().sendMessage("It's an " + itemDef.getName() + ".", 1000);
                    return;
                } else {
                    player.getPacketSender().sendMessage("It's a " + itemDef.getName() + ".", 1000);
                    return;
                }
            }
            player.getPacketSender().sendMessage(itemDef.getExamine(), 1000);
            if (itemId == 9795 || itemId == 9797) {
                player.getPacketSender().sendMessage("@red@Item Effect: Increases money reward when smithing while equipped!", 1000);
            } else if (itemId == ItemID.COMBAT_LAMP) {
                player.getPacketSender().sendMessage("@red@Rewards 1,000,000 experience to a selected combat skill excluding prayer.", 1000);
			/*} else if (itemId == 7780) {
				player.getPacketSender().sendMessage("@red@Rewards 1,000,000 fishing experience.", 1000);
			} else if (itemId == 7783) {
				player.getPacketSender().sendMessage("@red@Rewards 1,000,000 agility experience.", 1000);
			} else if (itemId == 7789) {
				player.getPacketSender().sendMessage("@red@Rewards 1,000,000 slayer experience.", 1000);
			} else if (itemId == 7798) {
				player.getPacketSender().sendMessage("@red@Rewards 1,000,000 woodcutting experience.", 1000);*/
            } else if (itemId == 9013) {
                player.getPacketSender().sendMessage("@red@Very useful staff for various direct boss teleports.", 1000);
            } else if (itemId == 13190) {
                player.getPacketSender().sendMessage("@red@Adds $50.00 total spent amount into your account, can be stacked. No points are added.", 1000);
            } else if (itemId == 19707) {
                player.getPacketSender().sendMessage("@red@" + itemDef.getExamine(), 1000);
            } else if (itemId == 15152) {
                player.getPacketSender().sendMessage("@red@The blade's high heat can recoil damage from players.");
            } else if (itemId == 9778 || itemId == 9777) {
                player.getPacketSender().sendMessage("@red@Wearing this cape increases your thieving XP by 30%!", 1000);
            } else if (itemId == 9792 || itemId == 9793) {
                player.getPacketSender().sendMessage("@red@Wearing this cape increases your mining XP by 40%!", 1000);
            } else if (itemId == 9798 || itemId == 9799) {
                player.getPacketSender().sendMessage("@red@Wearing this cape increases your fishing XP by 30%!", 1000);
            } else if (itemId == 9807 || itemId == 9808) {
                player.getPacketSender().sendMessage("@red@Wearing this cape increases your woodcutting XP by 30%!", 1000);
            } else if (itemId == 10933 || itemId == 10941 || itemId == 10940 || itemId == 10939) {
                player.getPacketSender().sendMessage("@red@Each piece of this set increases your woodcutting XP by 10%!", 1000);
            } else if (itemId == 13258 || itemId == 13259 || itemId == 13260 || itemId == 13261) {
                player.getPacketSender().sendMessage("@red@Each piece of this set increases your fishing XP by 10%!", 1000);
            } else if (itemId == 12013 || itemId == 12014 || itemId == 12015 || itemId == 12016) {
                player.getPacketSender().sendMessage("@red@Each piece of this set provides +10 % Mining experience!", 1000);
            } else if (itemId == ItemID.GOLDEN_PROSPECTOR_HELMET) {
                player.getPacketSender().sendMessage("@red@Wearing this helmet provides +10 % Mining experience!", 1000);
            } else if (itemId == ItemID.GOLDEN_PROSPECTOR_JACKET) {
                player.getPacketSender().sendMessage("@red@Wearing this jacket provides +15 % Mining experience!", 1000);
            } else if (itemId == ItemID.GOLDEN_PROSPECTOR_LEGS) {
                player.getPacketSender().sendMessage("@red@Wearing these legs provides +15 % Mining experience!", 1000);
            } else if (itemId == ItemID.GOLDEN_PROSPECTOR_BOOTS) {
                player.getPacketSender().sendMessage("@red@Wearing this helmet provides +5 % Mining experience!", 1000);
            } else if (itemId == 5554) {
                player.getPacketSender().sendMessage("@red@Wearing the rogue mask increases your thieving XP by 5%!", 1000);
            } else if (itemId == 5553) {
                player.getPacketSender().sendMessage("@red@Wearing the rogue body increases your thieving XP by 10%!", 1000);
            } else if (itemId == 5555) {
                player.getPacketSender().sendMessage("@red@Wearing the rogue trousers increases your thieving XP by 10%!", 1000);
            } else if (itemId == 5556) {
                player.getPacketSender().sendMessage("@red@Wearing the rogue gloves increases your thieving XP by 2.5%!", 1000);
            } else if (itemId == 5557) {
                player.getPacketSender().sendMessage("@red@Wearing the rogue boots increases your thieving XP by 2.5%!", 1000);
            } else if (itemId == 21143) {
                player.getPacketSender().sendMessage("@red@Wearing this amulet increases your thieving XP by 25%, and allows faster thieving from miscellanous stall!", 1000);
            } else if (itemId == 10075) {
                player.getPacketSender().sendMessage("@red@Wearing this gloves increases your thieving XP by 5% and allows a chance to double steal!", 1000);
                //} else if (itemId == 22323) {
                //	player.getPacketSender().sendMessage("@red@Each charge takes 7 soul runes, 10 revenant ether, and 50 blood money.", 1000);
            } else if (itemId == 20593 || itemId == 20784 || itemId == 20405 || itemId == 20408
                    || itemId == 15020 || itemId == 15021 || itemId == 15022 || itemId == 15023 || itemId == 15024
                    || itemId == 15025 || itemId == 15025 || itemId == 15026 || itemId == 15027 || itemId == 15028
                    || itemId == 15029 || itemId == 15030) {
                player.getPacketSender().sendMessage("@red@Degrades after 30 minutes while equipped.", 1000);
            } else if (itemId == 991) {
                player.getPacketSender().sendMessage("@gre@Hint: You can teleport to KBD and walk north east to reach the chest.", 1000);
            } else if (itemId == 15165) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                //player.getPacketSender().sendMessage("@red@Special attack: Skulls your opponent for 3 minutes if the hit was accurate.", 1000);
                player.getPacketSender().sendMessage("@red@Special attack: Bananas in your inventory multiplies your special attack damage against mobs.", 1000);
            } else if (itemId == ItemID.INFERNAL_CAPE) {
                player.sendMessage("@red@This cape can be infused with Dragon whip to create an Infernal whip.");
            } else if (itemId == 15153) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Freezes opponent for 7 seconds if the hit was accurate.");
            } else if (itemId == 15155) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Summons a devastating blast that continues to hit the opponent with shrapnels.");
            } else if (itemId == 15156) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Ignores opponent's Defence and stats to gurantee an accurate hit!");
            } else if (itemId == 15157) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Reduces your opponent's Attack and Defence stats by 35% if the hit was accurate.");
            } else if (itemId == 15158) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Teleblocks your opponent for 120 seconds if the hit was accurate. Delay is reduced to 60 seconds if using protect from magic prayers.");
            } else if (itemId == 15163) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Disarm opponent's weapon if the hit was accurate.");
            } else if (itemId == 15160) {
                player.sendMessage("@red@Special attack: Launches a fiery magic spell towards the enemy dealing high magic damage.");
            } else if (itemId == 15164) {
                player.sendMessage("@red@You can type @red@::whipeffects</col> for more information, or check our @dre@Wiki</col> page.");
                player.sendMessage("@red@Special attack: Disable the opponent's ability to use Protect Item prayer for 90 seconds.");
            } else if (itemId == 22552) { // Thammaron's sceptre (u)
                player.getPacketSender().sendMessage("In order to use the sceptre, it must first be activated with 1,000 revenant ether.", 1000);
            } else if (itemId == 22555) { // Thammaron's sceptre
                player.getPacketSender().sendMessage("An additional ether must be added (up to 16,000) in order to increase the sceptre's damage output.", 1000);
            } else if (itemId == 22542) { // Viggora Chainmace (u)
                player.getPacketSender().sendMessage("In order to use the chainmace, it must first be activated with 1,000 revenant ether.", 1000);
            } else if (itemId == 22545) { // Viggora Chainmace
                player.getPacketSender().sendMessage("An additional ether must be added (up to 16,000) in order to increase the chainmace's damage output.", 1000);
            } else if (itemId == 22547) { // Craw's bow (u)
                player.getPacketSender().sendMessage("In order to use the bow, it must first be activated with 1,000 revenant ether.", 1000);
            } else if (itemId == 22550) { // Craw's bow
                player.getPacketSender().sendMessage("An additional ether must be added (up to 16,000) in order to increase the bow's damage output.", 1000);


            } else if (itemId == 22481) { // Sanguinesti staff (uncharged)
                player.getPacketSender().sendMessage("In order to use the staff's magic spell, it must be charged with blood runes, holding up to 20,000 charges when fully charged.", 1000);

            } else if (itemId == 22323) { // Sanguinesti staff
                player.getPacketSender().sendMessage("Each cast requires 3 blood runes.", 1000);

            } else if (itemId == 22486 || itemId == 25741 || itemId == 25738) { // Scythe of Vitur (uncharged)
                player.getPacketSender().sendMessage("The scythe may be charged to drastically increase its stats. Charging requires a vial of blood and 300 blood runes.", 1000);

            } else if (itemDef.getName().contains(" tome(") || itemDef.getId() == ItemID.BANDITS_BREW) {
                if (player.getGameMode().isAnyIronman()) {
                    player.sendMessage("@red@Note: Your account's current game mode will receive 40% reduction in experience."); // 40% less experience
                } else if (player.getGameMode().isClassic()) {
                    player.sendMessage("@red@Note: Your account's current game mode will receive 60% reduction in experience."); // 60% less experience
                } else if (player.getGameMode().isRealism()) {
                    player.sendMessage("@red@Note: Your account's current game mode will receive 90% reduction in experience."); // 90% less experience
                }

            }
        }
    }

}
