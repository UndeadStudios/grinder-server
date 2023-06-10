package com.grinder.game.model.interfaces.menu;

import com.grinder.game.entity.agent.player.Player;

/**
 * A parent class used to handle the creation menu
 * chatbox interface. In the interface, an item is
 * displayed and the player can choose the amount
 * to "create".
 *
 * @author Professor Oak
 */
public abstract class CreationMenu {

    /**
     * The owner of this {@link CreationMenu}.
     */
    private final Player player;
    /**
     * The title of this {@link CreationMenu}.
     */
    private final String title;
    /**
     * The {@link CreationMenuAction} which will be
     * executed when the player has selected
     * an amount to create.
     */
    private final CreationMenuAction action;


    /**
     * Creates a new {@link CreationMenu}.
     *
     * @param player The owner.
     * @param title  The title.
     * @param action The action to execute upon selecting amount.
     */
    public CreationMenu(Player player, String title, CreationMenuAction action) {
        this.player = player;
        this.title = title;
        this.action = action;
    }

    /**
     * Opens this {@link CreationMenu}.
     */
    public abstract CreationMenu open();

    /**
     * Checks if a button pressed is related
     * to the {@link CreationMenu}.
     */
    public abstract boolean handleButton(int id);

    /**
     * Gets the player.
     *
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the title.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the action.
     *
     * @return
     */
    public CreationMenuAction getAction() {
        return action;
    }

    /**
     * Represents a CreationMenu action.
     *
     * @author Professor Oak
     */
    public interface CreationMenuAction {
        
        /**
		 * This method will execute when a player clicks on an item in the
		 * creation menu chatbox interface.
		 *
		 * @param index
		 *            The index.
		 * @param item
		 *            The item clicked on.
		 * @param amount
		 *            The amount selected.
		 */
        public abstract void execute(int index, int item, int amount);
    }
}