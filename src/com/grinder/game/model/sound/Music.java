package com.grinder.game.model.sound;

import com.grinder.game.entity.agent.player.Player;

public class Music {

	/**
	 * Last music played to this player
	 */
	private int lastPlayed;

	public boolean playAreaRandomMusic(Player player) {
		boolean startedAmusic = false;
		for (AreaMusics songs : AreaMusics.values()) {
			if (songs.isInArea(player)) {
				int musicID = songs.getFilteredRandomMusic(player);
				player.getPacketSender().sendMusic(musicID, 4, 25);
				//System.out.println("playing +" + musicID + "");
				//player.getPacketSender().sendMessage("Playing music: "+ musicID +"");
				startedAmusic = true;
				break;
			}
		}
		return startedAmusic;
	}

	public int getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(final int lastPlayed) {
		this.lastPlayed = lastPlayed;
	}
}
