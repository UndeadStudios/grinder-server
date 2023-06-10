package com.grinder.game.entity.agent.player.bot.script.impl;

import com.grinder.game.content.item.coloring.ItemColorCustomizer;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.bot.script.BotScript;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.Appearance;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.net.packet.impl.RegionChangePacketListener;
import com.grinder.util.Misc;

@SuppressWarnings("unused")
public class AfkScript extends BotScript {

	// Helm, Cape, Amulet, Weapon, Body, Kite, None, Legs, None, Gloves, Boots
	//	1		2	3		4		5		6	7		8	9		10		11
	private int[][] EQUIPMENT = new int[][] {
			{ 19641, 6570, 19553, 12006, 5575, 8850, -1, 2497, -1, 2497, 3105 },
			{ 12417, 9763, 6585, 4151, 12414, 11284, -1, 12415, -1, 7462, 11840 },
			{ 7534, 1052, 6585, 4151, 544, -1, -1, 542, -1, 7462, 11840 },
			{ 4708, 2412, 1712, 4675, 4757, 8740, -1, 4714, -1, 7462, 11840 },
			{ 4745, 21295, 589, 4747, 4749, -1, -1, 4751, -1, 7462, 21733 },
			{ 11665, 9748, 19707, 15155, 8839, 22322, -1, 8840, -1, 22981, 13239 },
			{ 12211, 9804, 10470, 8678, 12205, 7053, -1, 12207, -1, 2922, 2914 },
			{ 9629, 1007, -1, 827, 4757, 1187, -1, 1083, -1, 2489, 6349 },
			{ 7394, 1021, 1727, 1383, 7390, 1540, -1, 7386, -1, 2922, 2914 },
			{ 4753, 6570, 6585, 11905, 4720, 11283, -1, 4759, -1, 7462, 11840 },
			{ -1, -1, -1, 13263, -1, -1, -1, -1, -1, -1, -1 },
			{ 9785, 9783, -1, 6739, -1, -1, -1, -1, -1, -1, -1 },
			{ 10828, 6570, 6585, 15156, 4736, 12954, -1, 4714, -1, 7462, 13239 },
			{ 6109, 2412, 6585, 4151, 6107, 21633, -1, 6108, -1, 7462, 2579 },
			{ 1153, 1007, 1731, 1333, 1115, 1191, -1, 1099, -1, 1065, 1061 },
			{ 9925, 21295, 6585, 15160, -1, -1, 9923, -1, -1, 7462, 11840 },
			{ 2637, 1052, 10364, 2474, 6186, -1, -1, 6187, -1, 775, 1837 },
			{ 10390, 2412, 10364, 12926, 10386, -1, -1, 10388, -1, 10384, 2579 },
			{ 9767, 9765, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ -1, -1, 12002, 13652, -1, -1, -1, -1, -1, -1, 13239 },
			{ 21018, 9763, 19707, 22555, 21021, 12821, -1, 21024, -1, 7462, 6920 },
			{ 9812, 9810, 6585, 7409, 10412, -1, -1, 10414, -1, -1, 628 },
			{ 11847, 13222, 6585, 1283, 9634, -1, -1, 9636, -1, 7457, 9638 },
			{ 21298, 6570, 6585, 15157, 21301, 11283, -1,21304, -1, 7462, 11840 },
			{ 5574, 6568, 1712, 1377, 5575, 1540, -1, 5576, -1, 7461, 11840 },
			{ 3486, 9804, 1731, 15156, 3481, 3488, -1, 3485, -1, 2922, 12391 },
			{ 12931, 21295, 19533, 22622, 10551, -1, -1, 11834, -1, 7462, 13239 },
		{ 11864, 6570, 6585, 4151, 11832, 11284, -1, 11834, -1, 7462, 11840 }, // Full Bandos & Slayer helm
		{ 4089, 2412, 1712, 4675, 4091, 1540, -1, 4093, -1, 4095, 4097 }, // Mage gear
		{ 10828, 6570, 1704, 9185, 2503, 1540, -1, 2497, -1, 2491, 2577 }, // Blach d'hide & Rune crossbow
		{ 3749, 10499, 1712, 861, 2503, -1, -1, 2497, -1, 2491, 6328 }, // Black d'hide & Magic shortbow
		{ 10828, 6570, 1725, 4587, 1127, 1201, -1, 1079 }, //Neitz, full rune & dscim
		{ 4716, 6570, 6585, 4718, 4720, -1, -1, 4722, -1, 7462, 11840 }, // Dharok's
		{ 4753, 6570, 6585, 4755, 4757, -1, -1, 4759, -1, 7462, 11840 }, // Verac's
		{ 9096, 9101, 6585, 9084, 9097, -1, -1, 9098, -1, 9099, 9100 }, // Lunar
		{ 6109, 6111, -1, -1, 6107, -1, -1, 6108, -1, 6110, 6106 }, // Ghostly
		{ -1, -1, -1, 4151 }, // Whip
		{ 3751, 6070, 1704, 4587, 1127, 6524, -1, 4087, -1, 7460, 4131 },
		{ 3749, 4365, 1704, 861, 2503, -1, -1, 2497, -1, 2491, -1 },
		{ 3749, 10071, 1704, 9185, 4736, 1540, -1, 2497, -1, 2491, 2577 },
		{ 3751, 1052, 1704, 4587, 10564, 3122, -1, 4087, -1, 7460, 11840 },
		{ 3749, -1, 1704, 5698 }, // dds
		{ 1149, -1, 1704, 5698, 1127, -1, 1079, -1, -1, 7460, 3105 }, // dds
		{ -1, -1, -1, 4153 }, // gmaul
		{ 9779, 9777, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2920, -1, -1, -1, -1, -1, -1, -1, -1, 2922, 2914 },
		{ -1, 1052, 6585, 4151, 10551, 12954, -1, 4087, -1, 7462, 11840 },
		{ -1, 1023, -1, 1387, -1, 2890 }, // Whip
		{ -1, -1, 1725, 1305 }, // D long and amulet
		{ -1, -1, 6585, 11802 }, // AGS and fury
		{ 4089, 2414, 1727, 6908 }, // Wand
		
		{ 1050, 20050, 10344, 2462, 12393, -1, -1, 12395, -1, 12890, 1837 }, // Skilling
		
		{ 12359, 1027, 10354, 2472, 638, 10878, -1, 648, -1, 3799, 88 }, // Skilling
		
		{ 12432, -1, -1, -1, -1, -1, -1, -1, -1, 1580, 88 }, // Skilling
		
		{ -1, 21295, 6585, 4151, 3140, 8850, -1, 4087, -1, 7462, 11840 }
	};

	@Override
	public void initialize() {
		player.getPacketSender().sendDetails();
		player.getPacketSender().sendTabs();
		player.randomizeLevels();
		player.setPositionToFace(new Position(Misc.getRandomInclusive(9000), Misc.getRandomInclusive(9999), 0));
		player.setHostAddress("" + Misc.random(100, 250) +"." + Misc.random(100, 180) +". "+ Misc.random(120) +"." + Misc.random(50) +"");
		player.setMacAddress("2A-4C-19-36-B7");
		player.setSnAddress("1234852911");
		if (player.getUsername().contains("Iron ")) {
			player.setTitle("@bla@Ironman</col>");
		} else {

			if (Misc.random(8) == 1) {
				player.setTitle("@red@Ruby member</col>");
			} else if (Misc.random(15) == 1) {
				player.setTitle("<col=FF8080>The Notorious</col>");
			} else if (Misc.random(8) == 1) {
				player.setTitle("<col=" + String.format("%06X", (0xFFFFFF & 0xfc9b06)) + ">Lord</col>");
			} else if (Misc.random(8) == 1) {
				player.setTitle("<col=" + String.format("%06X", (0xFFFFFF & 0x23d500)) + ">Loyal</col>");
			} else if (Misc.random(5) == 1) {
				player.setTitle("<col=" + String.format("%06X", (0xFFFFFF & 0xab0000)) +"Newbie</col>");
			} else if (Misc.random(8) == 1) {
				player.setTitle("<col=" + String.format("%06X", (0xFFFFFF & 0x23d500)) +"Pioneer</col>");
			} else if (Misc.random(15) == 1) {
				player.setTitle("<col=7F07C3>The Betrayer</col>");
			} else if (Misc.random(10) == 1) {
				player.setTitle("<col=01E8FF>AFK</col>");
			}
		}

		int[] randomEquipment = Misc.randomElement(EQUIPMENT);
        player.getAppearance().set(Appearance.HEAD, Misc.getRandomInclusive(7));
        player.getAppearance().set(Appearance.CHEST, 18 + Misc.getRandomInclusive(5));
        player.getAppearance().set(Appearance.ARMS, 26);
        player.getAppearance().set(Appearance.HANDS, 34);
        player.getAppearance().set(Appearance.LEGS, 36 + Misc.getRandomInclusive(3));
        player.getAppearance().set(Appearance.FEET, 42);
        player.getAppearance().set(Appearance.BEARD, 10 + Misc.getRandomInclusive(5));

        //Colors
        player.getAppearance().set(Appearance.HAIR_COLOUR, Misc.getRandomInclusive(10));
        player.getAppearance().set(Appearance.TORSO_COLOUR, 0 + Misc.getRandomInclusive(10));
        player.getAppearance().set(Appearance.LEG_COLOUR, Misc.getRandomInclusive(10));
        player.getAppearance().set(Appearance.FEET_COLOUR, Misc.getRandomInclusive(4));
        player.getAppearance().set(Appearance.SKIN_COLOUR, Misc.getRandomInclusive(5));
        player.updateAppearance();
		EquipPacketListener.resetWeapon(player);
		WeaponInterfaces.INSTANCE.assign(player);
		EquipmentBonuses.update(player);
		player.getPacketSender().sendRights();
		ItemColorCustomizer.onLogin(player);
		//AreaManager.load();
		AreaManager.checkAreaChanged(player);
		player.setPin(-1);
		for (int i = 0; i < randomEquipment.length; i++) {
			if (randomEquipment[i] == -1) {
				continue;
			}
			
			player.getEquipment().set(i, new Item(randomEquipment[i]));
		}
		player.isLoggedIn = true;
	}

	@Override
	public void execute() {

	}

	@Override
	public void onStop() {

	}

}
