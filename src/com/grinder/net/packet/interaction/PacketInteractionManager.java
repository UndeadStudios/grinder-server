package com.grinder.net.packet.interaction;

import com.google.common.reflect.ClassPath;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class PacketInteractionManager {

	public static final ArrayList<PacketInteraction> INTERACTIONS = new ArrayList<PacketInteraction>();

	public static void init() {
		try {
			loadRecursive("com.grinder");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Loaded " + INTERACTIONS.size() + " packet interactions");
	}

	private static void loadRecursive(String directory) throws Exception {
		ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		Set<Class<?>> clazzes = classPath.getTopLevelClassesRecursive(directory).stream().map(ClassPath.ClassInfo::load)
				.collect(Collectors.toSet());
		
		clazzes.stream().forEach(clazz -> {
			try {
				load(clazz);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	public static boolean handleNPCDeath(Player player, NPC npc)  {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleNpcDeath(player, npc)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleNpcInteraction(Player player, NPC npc, int type)  {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleNpcInteraction(player, npc, type)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleButtonInteraction(Player player, int button) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleButtonInteraction(player, button)) {
				return true;
			}
		}
		return false;
	}
	public static boolean handleCommand(Player player, String command, String[] args) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleCommand(player, command, args)) {
				return true;
			}
		}
		return false;
	}
	public static boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleItemOnItemInteraction(player, use, usedWith)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleItemInteraction(Player player, Item item, int type) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleItemInteraction(player, item, type)) {
				return true;
			}
		}
		return false;
	}

	public static boolean handleEquipItem(Player player, Item item, int slot) {
		for(PacketInteraction interaction : INTERACTIONS) {
			if(interaction.handleEquipItemInteraction(player,item,slot)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleItemOnEntity(Player player, Item item, Agent entity) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleItemOnEntityInteraction(player, item, entity)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleObjectInteraction(Player player, GameObject object, int type) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleObjectInteraction(player, object, type)) {
				return true;
			}
		}
		return false;
	}

	public static boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handleItemOnObjectInteraction(player, item, object)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean handlePickupItem(Player player, ItemOnGround item) {
		for (PacketInteraction interaction : INTERACTIONS) {
			if (interaction.handlePickupItem(player, item)) {
				return true;
			}
		}
		return false;
	}

	private static final Logger logger = LogManager.getLogger(PacketInteractionManager.class.getSimpleName());

	private static void load(Class<?> clazz) throws IllegalAccessException {
		if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isAnonymousClass() || clazz.isEnum()
				|| clazz.isInterface()) {
			return;
		}

		if (hasDefaultConstructor(clazz) && isSuperClass(clazz, PacketInteraction.class)) {
			try {
				// Try to create an instance of that type
				PacketInteraction interaction = (PacketInteraction) clazz.newInstance();

				if (!INTERACTIONS.contains(interaction)) {
					INTERACTIONS.add(interaction);
				}
			} catch (InstantiationException ex) {
			}
		}
	}

	private static boolean hasDefaultConstructor(Class<?> clazz) {
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() == 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean isSuperClass(Class<?> clazz, Class<?> superClass) {
		if (clazz == null)
			return false;
		if (clazz.getSuperclass() == superClass)
			return true;
		return isSuperClass(clazz.getSuperclass(), superClass);
	}
}
