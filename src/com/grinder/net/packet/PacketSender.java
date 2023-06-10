package com.grinder.net.packet;

import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.gambling.GambleUtil;
import com.grinder.game.content.pvm.contract.MonsterContract;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.constructed.ConstructedChunk;
import com.grinder.game.model.areas.constructed.Palette;
import com.grinder.game.model.areas.instanced.HouseInstance;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.impl.ChangePassword;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.sound.AreaSound;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.StringUtils;
import com.grinder.util.oldgrinder.StreamHandler;
import kotlin.ranges.IntRange;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class manages making the packets that will be sent (when called upon)
 * onto the associated player's client.
 *
 * @author relex lawl & Gabbe
 */

public class PacketSender {

	private Player player;

	public PacketSender(Player player) {
		this.player = player;
	}

	public void sendAreaPlayerSound(int id){
		sendAreaPlayerSound(id, 0);
	}

	public void sendAreaPlayerSound(int id, int delay){
		sendAreaPlayerSound(id, 1, delay);
	}

	public void sendAreaPlayerSound(int id, int loopCount, int delay){
		sendAreaPlayerSound(id, 12, loopCount, delay);
	}

	public void sendAreaPlayerSound(int id, int radius, int loopCount, int delay){
		sendAreaEntitySound(player, id, radius, loopCount, delay);
	}

	public void sendAreaEntitySound(Entity source, int id){
		sendAreaEntitySound(source, id, 0);
	}

	public void sendAreaEntitySound(Entity source, int id, int delay){
		sendAreaEntitySound(source, id, 1, delay);
	}

	public void sendAreaEntitySound(Entity source, int id, int loopCount, int delay){
		sendAreaEntitySound(source, id, source.getSize(), loopCount, delay);
	}

	public void sendAreaEntitySound(Entity source, int id, int radius, int loopCount, int delay){
		sendAreaSound(source.getCenterPosition().clone(), id, radius, loopCount, delay);
	}

	public void sendAreaPlayerSound(AreaSound sound) {
		sendAreaPlayerSound(sound.getId(), sound.getRadius(), sound.getLoopCount(), sound.getDelay());
	}

	public void sendAreaSound(final Position center, int id, int radius) {
		sendAreaSound(center, id, radius, 1);
	}

	public void sendAreaSound(final Position center, int id, int radius, int loopCount) {
		sendAreaSound(center, id, radius, loopCount, 0);
	}

	/**
	 * Send an area sound to {@link #player} and all players within
	 * 12 tiles of the center position.
	 *
	 * @param center	the center location of the sound source
	 * @param id		the id of the sound
	 * @param radius	the audible range of the sound from the center pos
	 * @param loopCount	the amount of time the sound needs to loop
	 * @param delay		the delay between each play of the sound
	 */
	public void sendAreaSound(final Position center, int id, int radius, int loopCount, int delay) {

		// Method used for ambient sounds
		sendPosition(center);

		if(player instanceof BotPlayer)
			return;

		player.getSession().write(createSoundPacket(id, radius, loopCount, delay));

		if(player.getPosition().sameAs(center)){
			// if position is same as center we can use local players
			player.getLocalPlayers()
					.forEach(nearbyPlayer -> relayAreaSound(nearbyPlayer, center, id, radius, loopCount, delay));
		} else {
			PlayerUtil.playerStreamInDistanceOf(center)
					.forEach(nearbyPlayer -> relayAreaSound(nearbyPlayer, center, id, radius, loopCount, delay));
		}
	}

	private Packet createSoundPacket(int id, int radius, int loopCount, int delay){
		GamePacketBuilder builder = new GamePacketBuilder(239, PacketType.FIXED);
		builder.put(DataType.BYTE, 0); // offset, not properly supported due to hacky region system
		builder.put(DataType.BYTE,  delay);
		builder.put(DataType.BYTE,  radius);
		builder.put(DataType.BYTE,  loopCount);
		builder.put(DataType.SHORT,  id);
		return builder.toGamePacket();
	}

	private void relayAreaSound(Player recipient, Position position, int id, int radius, int loopCount, int delay){
		if(recipient instanceof BotPlayer)
			return;
		if(recipient == player)
			return;
		recipient.getPacketSender().sendPosition(position);
		recipient.getSession().write(createSoundPacket(id, radius, loopCount, delay));
	}

	public PacketSender sendSound(Sound sound) {
		return sendSound(sound.getId(), sound.getDelay(), sound.getLoopCount());
	}

	public PacketSender sendSound(int id) {
		return sendSound(id, 0, 1);
	}

	public PacketSender sendSound(int id, int startDelay) {
		return sendSound(id, startDelay, 1);
	}

	public PacketSender sendSound(int id, int startDelay, int loopCount) {
		return sendSound(id, startDelay, loopCount, 1);
	}

	public PacketSender sendSound(int id, int startDelay, int loopCount, int loopDelay) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(174, PacketType.FIXED);
		out.putShort(id);
		out.putShort(startDelay);
		out.put(loopCount);
//		out.putInt(loop);
		out.putShort(loopDelay);
		player.getSession().write(out);
		return this;
	}

	public void sendStopSound(int id) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(175, PacketType.FIXED);
		out.putShort(id);
		player.getSession().write(out);
	}

	public PacketSender sendMusic(int musicId, int volume, int delay) {
		player.getMusic().setLastPlayed(musicId);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(74, PacketType.FIXED);
		out.putShort(musicId, DataTransformation.ADD, DataOrder.LITTLE);
		out.put(volume);
		out.putShort(delay);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendJinglebitMusic(int musicId, int delay) {
		//player.getMusic().setLastPlayed(musicId);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(74, PacketType.FIXED);
		out.putShort(musicId, DataTransformation.ADD, DataOrder.LITTLE);
		out.put(1); // interlude identifier = 1, music = 4
		out.putShort(delay);
		player.getSession().write(out);
		return this;
	}



	public PacketSender sendObjectsRemoval(int chunkX, int chunkY, int height) {
		// player.getSession().write(new PacketBuilder(153, PacketType.FIXED).put(chunkX).put(chunkY).put(height));
		// TODO configure
		return this;
	}

	public void sendObject_cons(int objectX, int objectY, int objectId, int face, int objectType, int height) {
		//HouseInstance poh = ((HouseInstance) player.getArea());

		GameObject obj = StaticGameObjectFactory.produce(objectId, new Position(objectX, objectY), objectType, face);
		if(HouseInstance.get(player) != null && HouseInstance.get(player).getHouseOwner() == player) {
			ObjectManager.add(obj, true);
		}
	}

	public PacketSender constructMapRegion(Palette palette) {
		PacketBuilder bldr = new PacketBuilder(241, PacketType.VARIABLE_SHORT);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					Palette.PaletteTile tile = palette.getTile(x, y, z);
					boolean b = false;
					if (x < 2 || x > 10 || y < 2 || y > 10)
						b = true;
					int toWrite = !b && tile != null ? 5 : 0;
					bldr.put(toWrite);
					if (toWrite == 5) {
						int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
						//bldr.putString("" + val + "");
						bldr.putLong(val);
					}
				}
			}
		}
		bldr.putShort(player.getPosition().getRegionX() + 6);
		bldr.putShort(player.getPosition().getRegionY() + 6);

		player.getSession().write(bldr);
		return this;
	}

	public PacketSender constructDynamicMapRegion() {

		PacketBuilder bldr = new PacketBuilder(241, PacketType.VARIABLE_SHORT);

		int chunkStartX = (player.getLastKnownRegion().getX() >> 3) - 6;
		int chunkStartY = (player.getLastKnownRegion().getY() >> 3) - 6;

		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					int chunkId = (chunkStartX + x) << 16 | (chunkStartY + y);
					ConstructedChunk chunk = World.getConstructedMapChunks().get(z).get(chunkId);
					int toWrite = chunk != null ? 5 : 0;
					bldr.put(toWrite);
					if (toWrite == 5) {
						int val = chunk.getFromX() << 14 | chunk.getFromY() << 3 | chunk.getFromZ() << 24 | chunk.getOrientation() << 1;
						bldr.putLong(val);
					}
				}
			}
		}
		bldr.putShort(player.getLastKnownRegion().getX() >> 3); //center region chunk x
		bldr.putShort(player.getLastKnownRegion().getY() >> 3); // center region chunk y

		player.getSession().write(bldr);
		return this;
	}

	public PacketSender constructSingleMapChunk(Palette.PaletteTile tile, int x, int y, int z) {
		PacketBuilder bldr = new PacketBuilder(239, PacketType.VARIABLE_SHORT);
		boolean b = false;
		if (x < 2 || x > 10 || y < 2 || y > 10)
			b = true;
		boolean write = !b && tile != null ? true : false;
		if (write) {
			bldr.put(x);
			bldr.put(y);
			bldr.put(z);
			int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
			bldr.putLong(val);
			player.getSession().write(bldr);
		}
		return this;
	}
	public PacketSender constructMapRegion(Palette palette, int height) {
		PacketBuilder bldr = new PacketBuilder(241, PacketType.VARIABLE_SHORT);
		bldr.putShort(player.getPosition().getRegionY() + 6, DataTransformation.ADD);
		for (int x = 0; x < 13; x++) {
			for (int y = 0; y < 13; y++) {
				Palette.PaletteTile tile = palette.getTile(x, y, height);
				boolean b = false;
				if (x < 2 || x > 10 || y < 2 || y > 10)
					b = true;
				int toWrite = !b && tile != null ? 5 : 0;
				bldr.put(toWrite);
				if (toWrite == 5) {
					int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
					//bldr.putString("" + val + "");
					bldr.putLong(val);
				}
			}
		}
		bldr.putShort(player.getPosition().getRegionX() + 6);
		player.getSession().write(bldr);
		return this;
	}


	public void sendFadeScreen(String text, int state, int duration){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(7, PacketType.VARIABLE_SHORT);
		out.putString(text);
		out.put(state);
		out.put(duration);
		out.put(0);
		player.getSession().write(out);
	}

	/*
	 * Sends a option for a object.
	 * @pram id The ID  of the Object that will be edited.
	 * @pram message Options sent to the client
	 * @return The PacketBuilder Instance.
	 */
	public void sendSceneOptions(int id, String[] message){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(255, PacketType.VARIABLE_SHORT);

		String options = Arrays.toString(message).replace("String[","").replace(", ", "&")
				.replace("]","").replace("[", "");

		out.putInt(id);
		out.putString(options);

		player.getSession().write(out);
	}




	public void sendBlackScreen(int opacity){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(7, PacketType.VARIABLE_SHORT);
		out.putString("");
		out.put(3);
		out.put(0);
		out.put(opacity);
		player.getSession().write(out);
	}

	public void sendBlackScreen() {
		sendBlackScreen(255);
	}

	public PacketSender sendGraphic(Graphic graphic, Position position) {
		if (graphic == null) {
			return this;
		}
//		if(Graphic.USE_NEW_UPDATING){
//			World.spawn(new TileGraphic(position, graphic));
//			return this;
//		}
		sendPosition(position);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(4, PacketType.FIXED);
		out.put(0);
		out.putShort(graphic.getId());
		out.put(graphic.getHeight());
		out.putShort(graphic.getDelay());
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a graphic to only the client
	 * @return
	 */
	public PacketSender sendIndividualGraphic(Graphic graphic, Position position) {
		if (graphic == null) {
			return this;
		}
		sendPosition(position);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(4, PacketType.FIXED);
		out.put(0);
		out.putShort(graphic.getId());
		out.put(graphic.getHeight());
		out.putShort(graphic.getDelay());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendFlashingSidebar(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(24, PacketType.FIXED);
		out.put(id, DataTransformation.SUBTRACT);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendItemOnInterface(int frame, Item item, int slot) {
		if(item == null)
			item = new Item(-1, 0);
		return sendItemOnInterface(frame, item.getId(), slot, item.getAmount());
	}

	public PacketSender sendItemOnInterface(int frame, int item, int slot, int amount) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(34, PacketType.FIXED);
		out.putShort(frame);
		out.put(slot);
		out.putInt(amount);
		out.putShort(item + 1);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a broadcast to the client.
	 *
	 * @param message the message to include
	 * @param link the link to include
	 */
	public void sendBroadcastMessage(final String message, final String link) {
		if(player instanceof BotPlayer)
			return;

		final PacketBuilder builder = new PacketBuilder(243, PacketType.VARIABLE_SHORT);

		builder.putString(message);
		builder.putString(link);

		player.getSession().write(builder);
	}
	/**
	 * Removes a broadcast to the client.
	 *
	 * @param message the message to include
	 */
	public void removeBroadcastMessage(final String message) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder builder = new PacketBuilder(245, PacketType.VARIABLE_BYTE);
		builder.putString(message);
		player.getSession().write(builder);
	}

	public void sendNpcDebug(final NPC npc, final boolean toggleOn){

		if(player instanceof BotPlayer)
			return;

		final List<String> debugMessages = npc.getDebugMessages();
		final int messages = Math.min(20, debugMessages.size());

		PacketBuilder builder = new PacketBuilder(231, PacketType.VARIABLE_SHORT);

		builder.put(toggleOn && messages > 0 ? 1 : 0);
		builder.putShort(npc.getIndex());

		if(toggleOn && messages > 0){

			builder.put(messages);

			for(int i = messages-1; i >= 0; i--){
				builder.putString(debugMessages.get(i));
			}

			npc.getDebugMessages().clear();
		}

		player.getSession().write(builder);
	}

	/**
	 * Sends the response for a change password request.
	 *
	 * @see ChangePassword for the incoming packet (the request).
	 *
	 * @param isValid the response on the change password request
	 * @return {@code true} if the password request is deemed valid by the server,
	 * 			{@code false} if the password request is denied by the server
	 */
	public PacketSender sendChangePasswordResponse(final boolean isValid){
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder builder = new PacketBuilder(247, PacketType.FIXED);
		builder.put(isValid ? 1 : 0);
		player.getSession().write(builder);
		return this;
	}

	public PacketSender sendAppearanceConfig(int type, int value) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(203, PacketType.FIXED);
		out.putShort(type);
		out.putShort(value);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendQuickChat(String string) {
		player.say(string);
		for (Player players : player.getLocalPlayers()) {
			if (players == null) {
				continue;
			}
			if (players.getPosition().isWithinDistance(player.getPosition(), 20)) {
				players.getPacketSender().sendMessage("<img=759> "+player.getUsername()+":  @blu@" + string);
			}
		}
		return this;
	}

	/**
	 * Sends information about the player to the client.
	 *
	 * @return The PacketSender instance.
	 */
	public PacketSender sendDetails() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(249, PacketType.FIXED);
		out.put(1, DataTransformation.ADD);
		out.putShort(player.getIndex());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendMapRegion(Position position) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(73, PacketType.FIXED);
		out.putShort(position.getRegionX() + 6, DataTransformation.ADD);
		out.putShort(position.getRegionY() + 6);
		player.getSession().write(out);
		return this;
	}
	/**
	 * Sends the logout packet for the player.
	 *
	 * @return The PacketSender instance.
	 */
	public PacketSender sendLogout() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(109, PacketType.FIXED);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sets the world's system update time, once timer is 0, everyone will be
	 * disconnected.
	 *
	 * @param time
	 *            The amount of seconds in which world will be updated in.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendSystemUpdate(int time) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(114, PacketType.FIXED);
		out.putShort(time, DataOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendTeleportInterface(int menu) {
		player.setTeleportInterfaceOpen(true);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(183, PacketType.FIXED);
		out.put(menu);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendSpecialAttackState(boolean active) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(186, PacketType.FIXED);
		out.put(active ? 1 : 0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendAutocastId(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(38, PacketType.FIXED);
		out.putShort(id);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEnableNoclip() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(250, PacketType.FIXED);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendURL(String url) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(251, PacketType.VARIABLE_BYTE);
		out.putString(url);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a game message to a player in the server.
	 *
	 * @param message
	 *            The message they will receive in chat box.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendMessage(String message) {
		if(player instanceof BotPlayer) {
			if(((BotPlayer) player).isPrintMessages()){
				System.out.println(player+": sendMessage('"+message+"')");
			}
			return this;
		}
		PacketBuilder out = new PacketBuilder(253, PacketType.VARIABLE_BYTE);
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	public void sendMessage(final String message, final long delay) {
		if(message.equalsIgnoreCase(EntityExtKt.getString(player, Attribute.LAST_DELAYED_MESSAGE, ""))){
			if (!EntityExtKt.passedTime(player, Attribute.DELAYED_MESSAGE_TIMER, delay, TimeUnit.MILLISECONDS, false, true)) {
				return;
			}
		}
		EntityExtKt.setString(player, Attribute.LAST_DELAYED_MESSAGE, message, "");
		sendMessage(message);
	}

	public PacketSender sendSpecialMessage(String name, int type, String message) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(252, PacketType.VARIABLE_SHORT);
		out.put(type);
		out.putString(name);
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	public void sendSpecialMessage(String name, int type, final String message, final long delay) {
		if (!EntityExtKt.passedTime(player, Attribute.DELAYED_MESSAGE_TIMER, delay, TimeUnit.MILLISECONDS, false, true) && message.equals(EntityExtKt.getString(player, Attribute.LAST_DELAYED_MESSAGE, ""))) {
			return;
		}
		EntityExtKt.setString(player, Attribute.LAST_DELAYED_MESSAGE, message, "");
		sendSpecialMessage(name, type, message);
	}

	/**
	 * Sends skill information onto the client, to calculate things such as
	 * constitution, prayer and summoning orb and other configurations.
	 *
	 * @param skill
	 *            The skill being sent.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendSkill(Skill skill) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(134, PacketType.FIXED);
		out.put(skill.ordinal());
		out.putInt(player.getSkillManager().getCurrentLevel(skill));
		out.putInt(player.getSkillManager().getMaxLevel(skill));
		out.putInt(player.getSkillManager().getExperience(skill));
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendExpDrop(Skill skill, int exp) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(116, PacketType.FIXED);
		out.put(skill.ordinal());
		out.putInt(exp);
		player.getSession().write(out);
		return this;
	}

    public void sendVarbit(int id, int value) {
        if (player instanceof BotPlayer) {
            return;
        }
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            var builder = new PacketBuilder(181, PacketType.FIXED);
            builder.putShort(id);
            builder.putInt(value);
            player.getSession().write(builder);
        } else {
            var builder = new PacketBuilder(180, PacketType.FIXED);
            builder.putShort(id);
            builder.put(value);
            player.getSession().write(builder);
        }
    }

	/**
	 * Sends a configuration button's state.
	 *
	 * @param id
	 *            The id of the configuration button.
	 * @param state
	 *            The state to set it to.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendConfig(int id, int state) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(36, PacketType.FIXED);
		out.putShort(id, DataOrder.LITTLE);
		out.put(state);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a interface child's toggle.
	 *
	 * @param id
	 *            The id of the child.
	 * @param state
	 *            The state to set it to.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendToggle(int id, int state) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(87, PacketType.FIXED);
		out.putShort(id, DataOrder.LITTLE);
		out.putInt(state, DataOrder.MIDDLE);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends the state in which the player has their chat options, such as
	 * public, private, friends only.
	 */
	public PacketSender sendChatOptions(int[] modes) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(206, PacketType.FIXED);
		for (int mode : modes)
			out.put(mode);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendOrbConfig() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(110, PacketType.FIXED);
		out.put((int)Math.round((player.getRunEnergy() / 10000.0) * 100));
		out.put((player.isPoisoned() ? 2 : (player.isVenomed() ? 1 : 0)));
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendQuickPrayersState(boolean activated) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(111, PacketType.FIXED);
		out.put(activated ? 1 : 0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender updateSpecialAttackOrb() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(137, PacketType.FIXED);
		out.put(player.getSpecialPercentage());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendHeight() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(86, PacketType.FIXED);
		out.put(player.getPosition().getZ());
		player.getSession().write(out);
		return this;
	}
	public PacketSender sendShowClanChatOptions(boolean show) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(115, PacketType.FIXED);
		out.put(show ? 1 : 0); // 0 = no right click options
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendRunStatus() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(113, PacketType.FIXED);
		out.put(player.isRunning() ? 1 : 0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendWeight(int weight) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(240, PacketType.FIXED);
		out.putShort(weight);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterface(int id) {
		player.setInterfaceId(id);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(97, PacketType.FIXED);
		out.putInt(id);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends openInterfaceId2 which overlays on top of openInterfaceId
	 */
	public PacketSender sendOverlayInterface(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(98, PacketType.FIXED);
		out.putInt(id);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendWalkableInterface(int interfaceId) {
		player.setWalkableInterfaceId(interfaceId);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(208, PacketType.FIXED);
		out.putInt(interfaceId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceDisplayState(int interfaceId, boolean hide) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(171, PacketType.FIXED);
		out.put(hide ? 1 : 0);
		out.putInt(interfaceId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendPlayerHeadOnInterface(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(185, PacketType.FIXED);
		out.putShort(id, DataTransformation.ADD, DataOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendNpcHeadOnInterface(int id, int interfaceId) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(75, PacketType.FIXED);
		out.putShort(id, DataTransformation.ADD, DataOrder.LITTLE);
		out.putShort(interfaceId, DataTransformation.ADD, DataOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendStringColour(int id, int colour) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(122, PacketType.FIXED);
		out.putInt(id);
		out.putInt(colour);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEnterAmountPrompt(String title) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(27, PacketType.VARIABLE_BYTE);
		out.putString(title);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEnterInputPrompt(String title) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(187, PacketType.VARIABLE_BYTE);
		out.putString(title);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceReset() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(68, PacketType.FIXED);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Closes a player's client.
	 */
	public void sendExit() {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(62, PacketType.FIXED);
		player.getSession().write(out);
	}

	public void sendInterfaceComponentMoval(int x, int y, int id) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(70, PacketType.FIXED);
		out.putShort(x);
		out.putShort(y);
		out.putShort(id, DataOrder.LITTLE);
		player.getSession().write(out);
	}

	public void sendInterfaceAnimation(int interfaceId, Animation animation) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(200, PacketType.FIXED);
		out.putShort(interfaceId);
		out.putShort(animation.getId());
		player.getSession().write(out);
	}

	public PacketSender sendInterfaceModel(int interfaceId, int itemId, int zoom) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(246, PacketType.FIXED);
		out.putShort(interfaceId, DataOrder.LITTLE);
		out.putShort(zoom);
		out.putShort(itemId);
		player.getSession().write(out);
		return this;
	}

	public void sendInterfaceModel(int interfaceId, int modelId){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(8, PacketType.FIXED);
		out.putShort(interfaceId, DataOrder.LITTLE);
		out.putShort(modelId);
		player.getSession().write(out);
	}

	public int[] sideTabs = new int[16];

	public PacketSender sendTabInterface(int tabId, int interfaceId) {
		if (sideTabs[tabId] == interfaceId) {
			return this;
		}
		sideTabs[tabId] = interfaceId;
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(71, PacketType.FIXED);
		out.putShort(interfaceId);
		out.put(tabId, DataTransformation.ADD);
		player.getSession().write(out);
		return this;
	}

	public void sendTabs() {
		for (int tab = 0; tab < GameConstants.TAB_INTERFACES.length; tab++) {
			int interface_ = GameConstants.TAB_INTERFACES[tab];

			if (tab == 6) {
				interface_ = player.getSpellbook().getInterfaceId();
			}

			sendTabInterface(tab, interface_);
		}
	}

	public PacketSender sendTab(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(106, PacketType.FIXED);
		out.put(id, DataTransformation.NEGATE);
		player.getSession().write(out);
		return this;
	}



	public PacketSender sendChatboxInterface(int id) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(164, PacketType.FIXED);
		out.putShort(id, DataOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendMapState(int state) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(99, PacketType.FIXED);
		out.put(state);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCameraAngle(int x, int y, int level, int speed, int angle) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(177, PacketType.FIXED);
		out.put(x);
		out.put(y);
		out.putShort(level);
		out.put(speed);
		out.put(angle);
		player.getSession().write(out);
		return this;
	}

	public void sendCameraPos(int camX, int camY, int camZ) {
		if(player instanceof BotPlayer)
			return;
		final PacketBuilder out = new PacketBuilder(179, PacketType.FIXED);
		out.putShort(camX);
		out.putShort(camY);
		out.putShort(camZ);
		player.getSession().write(out);
	}

	public void sendCameraShake(int shakeDirection, int magnitude, int aplitude, int pi) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(35, PacketType.FIXED);
		out.put(shakeDirection);
		out.put(magnitude);
		out.put(aplitude);
		out.put(pi);
		player.getSession().write(out);
	}

	public PacketSender sendCameraSpin(int x, int y, int z, int speed, int angle) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(166, PacketType.FIXED);
		out.put(x);
		out.put(y);
		out.putShort(z);
		out.put(speed);
		out.put(angle);
		player.getSession().write(out);
		return this;
	}

	public void sendCameraNeutrality() {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(107, PacketType.FIXED);
		player.getSession().write(out);
	}

	public PacketSender sendInterfaceRemoval() {
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPriceChecker().withdrawAll(false);
		} else if (player.getStatus() == PlayerStatus.TRADING) {
			player.getTrading().closeTrade();
		} else if (player.getStatus() == PlayerStatus.DUELING) {
			if (!player.getDueling().inDuel()) {
				player.getDueling().onCloseDuelInterface();
			}
		} else if (player.getGambling().inGambleWindow()) {
			GambleUtil.onCloseGambleWindow(player);
		}
		if (player.getInterfaceId() == 51200) {
			if (!player.getGameMode().isIronman() && !player.getGameMode().isClassic() && !player.getGameMode().isHardcore()
					&& !player.getGameMode().isUltimate() && !player.getGameMode().isSpawn() && !player.getGameMode().isRealism()
					&& !player.getGameMode().isOneLife() && !player.getGameMode().isPure() && !player.getGameMode().isMaster() && player.isNewPlayer()) {
				player.setGameMode(GameMode.NORMAL);
				player.getPacketSender().sendMessage("You have selected the normal game mode.");
			}
		}

		final boolean sendPacket = player.getInterfaceId() != -1
				|| player.getDestroyItem() != -1
				|| player.getEnterSyntax() != null
				|| player.getDialogue() != null
				|| player.getDialogueOptions() != null
				|| player.getDialogueContinueAction() != null
				|| player.getShop() != null;

		player.setStatus(PlayerStatus.NONE);
		player.setEnterSyntax(null);
		player.setDialogue(null);
		player.setDialogueOptions(null);
		player.setDialogueContinueAction(null);
		player.setShop(null);
		player.setDestroyItem(-1);
		player.setInterfaceId(-1);
		player.setTeleportInterfaceOpen(false);
		player.getAppearance().setCanChangeAppearance(false);
		player.presetInterfaceOpen = false;

		player.getSession().write(new PacketBuilder(219, PacketType.FIXED));
		return this;
	}

	public void sendInterfaceScrollReset(int interfaceId) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(9, PacketType.FIXED);
		out.putInt(interfaceId);
		player.getSession().write(out);
	}

	public PacketSender sendScrollbarHeight(int interfaceId, int scrollMax) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(10, PacketType.FIXED);
		out.putInt(interfaceId);
		out.putShort(scrollMax);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceSet(int interfaceId, int sidebarInterfaceId) {
		player.setInterfaceId(interfaceId);
		player.getMotion().clearSteps();
		if(player instanceof BotPlayer)
			return this;

		PacketBuilder out = new PacketBuilder(248, PacketType.FIXED);
		out.putShort(interfaceId, DataTransformation.ADD);
		out.putShort(sidebarInterfaceId);
		player.getSession().write(out);
		sendMinimapFlagRemoval();
		return this;
	}
	public PacketSender sendItemContainer(final List<Item> container, final int interfaceId) {
		final PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
		out.putInt(interfaceId);
		out.putShort(container.size());
		for (final Item item : container) {
			if (item == null || item.getId() <= 0 || item.getAmount() <= 0) {
				out.putInt(-1);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}
		player.getSession().write(out);
		return this;
	}
	public PacketSender sendItemContainer(ItemContainer container, int interfaceId) {

		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);

		out.putInt(interfaceId);
		out.putShort(container.capacity());
		for (Item item : container.getItems()) {
			if (item == null || item.getId() < 0 || item.getAmount() <= 0 && !(container instanceof Bank)) {
				out.putInt(-1);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}

		player.getSession().write(out);
		return this;
	}

	public void sendCurrentBankTab(int current_tab) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(55, PacketType.FIXED);
		out.put(current_tab);
		player.getSession().write(out);
	}

	public PacketSender sendModifiableXValue() {
		// Resets bank quantity config
		if (player.getBankQuantityConfig() == 3 && player.getModifiableXValue() <= 0) {
			sendConfig(1114, 0);
			player.setBankQuantityConfig(0);
		}
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(56, PacketType.FIXED);
		out.putInt(player.getModifiableXValue());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEffectTimer(int delay, EffectTimer e) {

		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(54, PacketType.FIXED);

		out.putShort(delay);
		out.putShort(e.getClientSprite());

		player.getSession().write(out);
		return this;
	}

	public void sendInterfaceItems(int interfaceId, List<Item> items) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
		out.putInt(interfaceId);
		out.putShort(items.size());
		for (Item item : items) {
			if (item == null) {
				out.putInt(-1);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}
		player.getSession().write(out);
	}

	public void sendInterfaceItems(int interfaceId, Item[] items) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
		out.putInt(interfaceId);
		out.putShort(items.length);
		for (Item item : items) {
			if (item == null) {
				out.putInt(-1);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}
		player.getSession().write(out);
	}

	public PacketSender sendItemOnInterface(int interfaceId, int item, int amount) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
		out.putInt(interfaceId);
		out.putShort(1);
		out.putInt(amount);
		out.putShort(item + 1);
		player.getSession().write(out);
		return this;
	}

	public PacketSender clearItemOnInterface(int frame) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(72, PacketType.FIXED);
		out.putShort(frame);
		player.getSession().write(out);
		return this;
	}

	private String[] interactiveOptions = new String[5];

	public PacketSender sendInteractionOption(String optionText, int option, boolean top) {
		int slot = option-1;
		if (interactiveOptions[slot] != null) {
			if (interactiveOptions[slot].equalsIgnoreCase(optionText)) {
				return this;
			}
		}
		interactiveOptions[slot] = optionText;
		if (optionText != null)
			player.setPlayerInteractingOption(PlayerInteractingOption.forName(optionText));
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(104, PacketType.VARIABLE_BYTE);
		out.put(slot, DataTransformation.NEGATE);
		out.put(top ? 1 : 0, DataTransformation.ADD);
		out.putString(optionText == null ? "null" : optionText);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendString(int id, String string, boolean cache) {

		if(player instanceof BotPlayer) {
			if(((BotPlayer) player).isPrintMessages()){
				System.out.println(player+": sendString("+id+", '"+string+"')");
			}
			return this;
		}

		if(cache && player.getMessageCache().isCached(id, string))
			return this;

		PacketBuilder out = new PacketBuilder(126, PacketType.VARIABLE_SHORT);
		out.putString(string);
		out.putInt(id);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendString(int id, String string) {
		return sendString(id, string, false);
	}

	public PacketSender sendString(String string, int id) {
		return sendString(id, string);
	}

	public void sendTooltip(int id, String tooltip) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(244, PacketType.VARIABLE_BYTE);
		out.putString(tooltip);
		out.putInt(id);
		player.getSession().write(out);
	}

	public PacketSender clearInterfaceText(int start, int end) {
//		for (int i = start; i <= end; i++) {
//			player.getFrameUpdater().interfaceTextMap.remove(i);
//		}
		if(player instanceof BotPlayer)
			return this;

		player.getMessageCache().removeRange(new IntRange(start, end));

		PacketBuilder out = new PacketBuilder(105, PacketType.FIXED);
		out.putInt(start);
		out.putInt(end);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends all of the player's rights to the client.
	 */
	public PacketSender sendRights() {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(127, PacketType.FIXED);
		out.put(player.getRights().ordinal());
		out.put(player.getCrown());
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a hint to specified position.
	 *
	 * @param position
	 *            The position to create the hint.
	 * @param tilePosition
	 *            The position on the square (middle = 2; west = 3; east = 4;
	 *            south = 5; north = 6)
	 */
	public void sendPositionalHint(Position position, int tilePosition) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(254, PacketType.VARIABLE_SHORT);
		out.put(tilePosition);
		out.putShort(position.getX());
		out.putShort(position.getY());
		out.put(position.getZ());
		player.getSession().write(out);
	}

	/**
	 * Sends a hint above an entity's head.
	 *
	 * @param entity
	 *            The target entity to draw hint for.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendEntityHint(Entity entity) {
		int type = entity instanceof Player ? 10 : 1;
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(254, PacketType.VARIABLE_SHORT);
		out.put(type);
		out.putShort(entity.getIndex());
		out.putInt(0, DataOrder.TRIPLE_INT);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a hint removal above an entity's head.
	 *
	 * @param playerHintRemoval
	 *            Remove hint from a player or an NPC?
	 * @return The PacketSender instance.
	 */
	public PacketSender sendEntityHintRemoval(boolean playerHintRemoval) {
		int type = playerHintRemoval ? 10 : 1;
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(254, PacketType.VARIABLE_SHORT);
		out.put(type);
		out.putShort(-1);
		out.putInt(0, DataOrder.TRIPLE_INT);
		player.getSession().write(out);
		return this;
	}

	public void sendMultiIcon(int value) {
		player.setMultiIcon(value);
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(61, PacketType.FIXED);
		out.put(value);
		player.getSession().write(out);
	}

	public PacketSender sendPrivateMessage(Player target, byte[] compressedMessage) {

		if(player instanceof BotPlayer)
			return this;

		GamePacketBuilder builder = new GamePacketBuilder(196, PacketType.VARIABLE_BYTE);

		builder.put(DataType.LONG, StringUtils.encodeBase37(target.getUsername()));
		builder.put(DataType.INT, PlayerRelations.messageCounter.getAndIncrement());
		builder.put(DataType.INT, target.getRightsValue());
		builder.put(DataType.BYTE, target.getCrown());
		builder.putBytes(compressedMessage);
		player.getSession().write(builder.toGamePacket());
		return this;
	}

	public void sendFriendStatus(int status) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(221, PacketType.FIXED);
		out.put(status);
		player.getSession().write(out);
	}

	public void sendFriend(long name, int world) {
		//world = world != 0 ? world + 9 : world;
		if (world != 0) {
			world += 9;
		}
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(50, PacketType.FIXED);
		out.putLong(name);
		out.put(world);
		player.getSession().write(out);
	}

	public void sendDeleteFriend(long name) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(51, PacketType.FIXED);
		out.putLong(name);
		player.getSession().write(out);
	}

	public void sendAddIgnore(long name) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(214, PacketType.VARIABLE_SHORT);
		out.putLong(name);
		player.getSession().write(out);
	}

	public void sendDeleteIgnore(long name) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(215, PacketType.FIXED);
		out.putLong(name);
		player.getSession().write(out);
	}

	public void sendTotalExp(long exp) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(108, PacketType.FIXED);
		out.putLong(exp);
		player.getSession().write(out);
	}

	public void sendAnimationReset() {
//		player.say("resetting animation");
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(1, PacketType.FIXED);
		player.getSession().write(out);
	}



	public PacketSender sendGlobalGraphic(Graphic graphic, Position position) {


		if(Graphic.USE_NEW_UPDATING){
			World.spawn(new TileGraphic(position, graphic));
			return this;
		}

		sendGraphic(graphic, position);
		for (Player p : player.getLocalPlayers()) {
			if (p.getPosition().distanceToPoint(player.getPosition().getX(), player.getPosition().getY()) > 20)
				continue;
			p.getPacketSender().sendGraphic(graphic, position);
		}
		return this;
	}

	public void sendObject(GameObject object) {
		sendObject(object.getPosition(), object.getId(), object.getObjectType(), object.getFace());
	}
	public void sendObject(Position position, int id, int type, int face) {
		if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES && !player.getLocalObject(id, position).isPresent()) {
			System.out.println("Ignored sendObject for "+player+", using new updating!");
			return;
		}
		//System.out.println("Added object "+id+" at "+position);
		sendPosition(position);
		if(player instanceof BotPlayer)
			return;
//		System.out.println("sending "+position.compactString());
		PacketBuilder out = new PacketBuilder(151, PacketType.FIXED);
		out.put(0, DataTransformation.ADD);
		out.putShort(id, DataOrder.LITTLE);
		out.put((byte) ((type << 2) + (face & 3)), DataTransformation.SUBTRACT);
		player.getSession().write(out);
	}
	public void removeObject(int x, int y, int type, int direction) {
		StreamHandler.ReplaceObject2(player, x, y, 6951, direction, type);
	}
	public void removeObjectNew(int x, int y, int type, int direction) {
		final Position position = new Position(x, y, player.getPosition().getZ());
		player.getPacketSender().sendObjectRemoval(position, type, direction);
	}
	public void sendObjectRemoval(Position position, int type, int face) {
		if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES) {
			System.out.println("Ignored sendObjectRemoval for "+player+", using new updating!");
			return;
		}
		sendPosition(position);
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(101, PacketType.FIXED);
		out.put((type << 2) + (face & 3), DataTransformation.NEGATE);
		out.put(0);
		player.getSession().write(out);
	}
	public void sendObjectRemoval(GameObject object) {
		if(ClippedMapObjects.USE_NEW_OBJECT_UPDATES) {
			System.out.println("Ignored sendObjectRemoval for "+player+", using new updating!");
			return;
		}
		sendPosition(object.getPosition());
		if(player instanceof BotPlayer)
			return;

//		final Region region = World.getRegions().fromPosition(object.getPosition());
//		final ObjectUpdateOperation update = object.toUpdateOperation(region, EntityUpdateType.REMOVE);
//		final RegionUpdateMessage message = update.toMessage();
//		@SuppressWarnings("unchecked")
//		final MessageEncoder<RegionUpdateMessage> encoder = (MessageEncoder<RegionUpdateMessage>) World
//				.getPacketMetaData()
//				.getMessageEncoder(message.getClass());
//		player.getSession().write(encoder.encode(message));

		PacketBuilder out = new PacketBuilder(101, PacketType.FIXED);
		out.put((object.getObjectType() << 2) + (object.getFace() & 3), DataTransformation.NEGATE);
		out.put(0);
		player.getSession().write(out);
	}

	public void sendObjectAnimation(GameObject object, Animation anim) {
		sendPosition(object.getPosition());
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(160, PacketType.FIXED);
		out.put(0, DataTransformation.SUBTRACT);
		out.put((object.getObjectType() << 2) + (object.getFace() & 3), DataTransformation.SUBTRACT);
		out.putShort(anim.getId(), DataTransformation.ADD);
		player.getSession().write(out);
	}
	public void sendObjectAnimation(int objectX, int objectY, int objectType, int objectFace, Animation anim) {
		sendPosition(new Position(objectX, objectY));
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(160, PacketType.FIXED);
		out.put(0, DataTransformation.SUBTRACT);
		out.put((objectType << 2) + (objectFace & 3), DataTransformation.SUBTRACT);
		out.putShort(anim.getId(), DataTransformation.ADD);
		player.getSession().write(out);
	}
	public void alterItemOnGround(ItemOnGround item) {
		sendPosition(item.getPosition());
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(84, PacketType.FIXED);
		out.put(0);
		out.putShort(item.getItem().getId());
		out.putShort(item.getOldAmount());
		out.putShort(item.getItem().getAmount());
		player.getSession().write(out);
	}

	public void createItemOnGround(ItemOnGround itemOnGround) {
		final Item item = itemOnGround.getItem();
		createItemOnGround(item.getId(), item.getAmount(), itemOnGround.getPosition());
	}

	public void createItemOnGround(int id, int amount, Position position) {
		sendPosition(position);
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(44, PacketType.FIXED);
		out.putShort(id, DataTransformation.ADD, DataOrder.LITTLE);
		out.putInt(amount);
		out.put(0);
		player.getSession().write(out);
	}

	public void deleteItemOnGround(ItemOnGround item) {
		sendPosition(item.getPosition());
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(156, PacketType.FIXED);
		out.put(0, DataTransformation.ADD);
		out.putShort(item.getItem().getId());
		player.getSession().write(out);
	}

	/**
	 * Deletes spawns related to regions, such as ground items and objects.
	 */
	public void deleteRegionalSpawns() {
		if(player instanceof BotPlayer)
			return;
		player.getSession().write(new PacketBuilder(178, PacketType.FIXED));
	}

	public PacketSender sendMinimapFlagRemoval() {
		if(player instanceof BotPlayer)
			return this;
		return sendMinimapFlag(-1, -1);
	}

	public PacketSender sendMinimapFlag(Position position) {
		if(player instanceof BotPlayer)
			return this;
		if (position == null)
			return sendMinimapFlagRemoval();
		return sendMinimapFlag(position.getX(), position.getY());
	}

	private PacketSender sendMinimapFlag(int x, int y) {
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(78, PacketType.FIXED);
		//convert x/y to player's rendered map coordinates.
		if (x != -1) {
			int mapX = ((player.getLastKnownRegion().getX() >> 3) - 6) << 3;
			int mapY = ((player.getLastKnownRegion().getY() >> 3) - 6) << 3;
			x = x - mapX;
			y = y - mapY;
		}
		out.put(x);
		out.put(y);

		player.getSession().write(out);
		return this;
	}

	public void sendPosition(final Position position) {
		final Position other = player.hasLastKnownRegion()
				? player.getLastKnownRegion()
				: player.getPosition();
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(85, PacketType.FIXED);
		out.put(position.getY() - 8 * other.getRegionY(), DataTransformation.NEGATE);
		out.put(position.getX() - 8 * other.getRegionX(), DataTransformation.NEGATE);
		player.getSession().write(out);
	}

	public void sendConsoleMessage(String message) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(123, PacketType.VARIABLE_SHORT);
		out.putString(message);
		player.getSession().write(out);
	}

	public void sendInterfaceSpriteChange(int interfaceId, int disabledSprite) {
		sendInterfaceSpriteChange(interfaceId, disabledSprite, -1);
	}

	public void sendInterfaceSpriteChange(int interfaceId, int disabledSprite, int enabledSprite) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(140, PacketType.FIXED);
		out.putInt(interfaceId);
		out.putShort(disabledSprite);
		out.putShort(enabledSprite);
		player.getSession().write(out);
	}

	public PacketSender sendProjectile(Position start, Position end, int offset, int speed, int projectileId, int startHeight, int endHeight, int lockon, int delay) {
		sendPosition(start);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(117, PacketType.FIXED);
		out.put(offset);
		out.put((end.getX() - start.getX()));
		out.put((end.getY() - start.getY()));
		out.putShort(lockon);
		out.putShort(projectileId);
		out.put(startHeight);
		out.put(endHeight);
		out.putShort(delay);
		out.putShort(speed);
		out.put(16); // Angle
		out.put(64);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int creatorSize, int startDistanceOffset) {
		sendProjectilePosition(position);
		if(player instanceof BotPlayer)
			return this;
		PacketBuilder out = new PacketBuilder(117, PacketType.FIXED);
		out.put(50);
		out.put(offset.getX());
		out.put(offset.getY());
		out.putShort(lockon);
		out.putShort(gfxMoving);
		out.put(startHeight);
		out.put(endHeight);
		out.putShort(time);
		out.putShort(speed);
		out.put(angle);
		out.put(Math.min(255, (creatorSize * 64) + (startDistanceOffset * 64)));
		player.getSession().write(out);
		return this;
	}

	public void sendProjectilePosition(final Position position) {
		final Position other = player.hasLastKnownRegion()
				? player.getLastKnownRegion()
				: player.getPosition();
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(85, PacketType.FIXED);
		out.put((position.getY() - (other.getRegionY() * 8)) - 2, DataTransformation.NEGATE);
		out.put((position.getX() - (other.getRegionX() * 8)) - 3, DataTransformation.NEGATE);
		player.getSession().write(out);
	}
	public void sendClanMate(long name, boolean add) {
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(242, PacketType.FIXED);
		out.putLong(name);
		out.put(add ? 1 : 0);
		player.getSession().write(out);
	}

	public void sendMonsterHuntTracker(MonsterContract contract){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(238, PacketType.FIXED);
		out.putShort(contract.getBounty().getNpcId());
		out.putLong(contract.getStartTime());
		out.putShort(contract.getBounty().getExpirationTime() / 1000);
		player.getSession().write(out);
	}

	public void sendMonsterHuntTrackerStop(MonsterContract contract){
		if(player instanceof BotPlayer)
			return;
		PacketBuilder out = new PacketBuilder(238, PacketType.FIXED);
		out.putShort(contract.getBounty().getNpcId());
		out.putLong(contract.getStartTime());
		out.putShort(0);
		player.getSession().write(out);
	}
}
