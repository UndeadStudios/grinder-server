package com.grinder.game.entity.agent.player.death;

import com.grinder.game.content.item.charging.Chargeables;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.ItemContainer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static com.grinder.game.entity.agent.player.death.PlayerDeathUtil.*;
import static com.grinder.game.entity.agent.player.death.PlayerDeathUtil.keepItem;

/**
 * This class takes {@link ItemContainer} objects and determines what
 * should happen to the items contained within (if any) when a {@link Player} dies.
 *
 * @see PlayerDeathTask for player death implementation
 * @see ItemsKeptOnDeath for items kept on death interface
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-03
 */
public class ItemsKeptOnDeathGenerator {

    private final int maxItemsKept;
    private final List<Item> itemPool = new ArrayList<>();
    private Consumer<Player> playerConsumer;

    ItemsKeptOnDeathGenerator(final Player player, final boolean delete){
        this(delete,
                maxItemsKeptOnDeath(player),
                containersRiskedOnDeath(player));
    }

    private ItemsKeptOnDeathGenerator(final boolean delete, final int maxItemsKept, final ItemContainer... containers) {
        this.maxItemsKept = maxItemsKept;
        for(final ItemContainer container : containers) {
            itemPool.addAll(container.cloneItems());
            if(delete)
                container.resetItems();
        }
    }

    public Result generate(){

        // TODO: Make it so it properly shows that broken items above level 20 wilderness will ALWAYS be lost on death (they wont be broken)
        // TODO: Make it show the lost items as they go invisible in the interface for example rune pouch, or chinchompas, they are always lost even if 1 item in inventory, death mechanics works fine but not the interface showing.

        final ArrayList<Item> keep = new ArrayList<>();
        final ArrayList<Item> broken = new ArrayList<>();
        final ArrayList<Item> lost = new ArrayList<>();
        final ArrayList<Item> dropped = new ArrayList<>();

        for(final Item item : itemPool){
            if(ItemUtil.isValidItem(item)){
                if(breakItem(item)) broken.add(item);
                else if(keepItem(item)) keep.add(item);
                else if(loseItem(item)) lost.add(item);
                else dropped.add(item);
            }
        }

        final ArrayList<ItemOnDeath> itemsOnDeath = new ArrayList<>();

        for (Item item: dropped)
            itemsOnDeath.add(new ItemOnDeath(item, ItemOnDeathPolicy.DROPPED));

        for (Item item: broken)
            itemsOnDeath.add(new ItemOnDeath(item, ItemOnDeathPolicy.BROKEN));

        int itemsKept = 0;

        itemsOnDeath.sort(Comparator
                .comparingLong((ToLongFunction<ItemOnDeath>)
                        value -> value.item.getValue(ItemValueType.PRICE_CHECKER))
                .reversed());

        for (ItemOnDeath itemOnDeath : itemsOnDeath) {

            if (itemsKept == maxItemsKept)
                break;

            final Item item = itemOnDeath.item;
            final ItemOnDeathPolicy policy = itemOnDeath.policy;

            boolean remove;
            if (policy != ItemOnDeathPolicy.BROKEN) { // We don't count broken items from 3 items kept, so even if u die with ONLY fire cape, it will still break.
                if (item.getAmount() > 1) {

                    final int oldAmount = item.getAmount();
                    final int newAmount = Math.min(oldAmount, maxItemsKept - itemsKept);

                    itemsKept += newAmount;

                    keep.add(new Item(item.getId(), newAmount));

                    item.setAmount(oldAmount - newAmount);

                    remove = item.getAmount() <= 0;
                } else {
                    itemsKept++;
                    keep.add(item);
                    remove = true;
                }

                if (remove) {
                    if (policy == ItemOnDeathPolicy.DROPPED)
                        dropped.remove(item);
                    else if (policy == ItemOnDeathPolicy.BROKEN)
                        broken.remove(item);
                    else
                        System.err.println("Invalid policy {" + policy + "} provided in itemsOnDeath list.");
                }
            }
        }

        playerConsumer = Chargeables.INSTANCE.postDropEvaluation(keep, dropped);

        return new Result(keep, broken, dropped, lost);
    }

    int getMaxItemsKept() {
        return maxItemsKept;
    }

    public Consumer<Player> getPlayerConsumer() {
        return playerConsumer;
    }

    private static class ItemOnDeath {

        private final Item item;
        private final ItemOnDeathPolicy policy;

        public ItemOnDeath(Item item, ItemOnDeathPolicy policy) {
            this.item = item;
            this.policy = policy;
        }

        public Item getItem() {
            return item;
        }

        public ItemOnDeathPolicy getPolicy() {
            return policy;
        }
    }

    private enum ItemOnDeathPolicy {
        KEEP,
        DROPPED,
        LOST,
        BROKEN
    }

    /**
     * This class contains the results of the {@link ItemsKeptOnDeathGenerator#generate()} method.
      */
    public static class Result {

        private final List<Item> keep;
        private final List<Item> broken;
        private final List<Item> dropped;
        private final List<Item> lost;

        Result(List<Item> keep, List<Item> broken, List<Item> dropped, List<Item> lost) {
            this.keep = keep;
            this.broken = broken;
            this.dropped = dropped;
            this.lost = lost;
        }

        public List<Item> getKeep() {
            return keep;
        }

        List<Item> getBroken(){
            return broken;
        }

        public List<Item> getDropped() {
            return dropped;
        }

        public List<Item> getLost(){
            return lost;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "\n keep["+keep.size()+"]\t=" + keep +
                    "\n broken["+broken.size()+"]\t=" + broken +
                    "\n dropped["+dropped.size()+"]\t=" + dropped +
                    "\n lost["+lost.size()+"]\t=" + lost +
                    "\n}";
        }
    }
}
