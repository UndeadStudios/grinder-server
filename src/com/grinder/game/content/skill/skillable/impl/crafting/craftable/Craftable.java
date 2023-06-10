package com.grinder.game.content.skill.skillable.impl.crafting.craftable;

import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.SoundLoop;

public interface Craftable {
	
	String getName();
	
	Item getUse();
	
	Item getWith();
	
	CraftableItem[] getCraftableItems();
	
	RequiredItem[] getRequiredItems(int index);
	
	AnimationLoop getAnimationLoop();

	SoundLoop getSoundLoop();

}