package com.grinder.game.content.minigame.warriorsguild.tokens;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

/**
 * @author L E G E N D
 */
public enum Armour {
    BRONZE(NpcID.ANIMATED_BRONZE_ARMOUR, ItemID.BRONZE_FULL_HELM, ItemID.BRONZE_PLATEBODY, ItemID.BRONZE_PLATELEGS),
    IRON(NpcID.ANIMATED_IRON_ARMOUR, ItemID.IRON_FULL_HELM, ItemID.IRON_PLATEBODY, ItemID.IRON_PLATELEGS),
    STEEL(NpcID.ANIMATED_STEEL_ARMOUR, ItemID.STEEL_FULL_HELM, ItemID.STEEL_PLATEBODY, ItemID.STEEL_PLATELEGS),
    BLACK(NpcID.ANIMATED_BLACK_ARMOUR, ItemID.BLACK_FULL_HELM, ItemID.BLACK_PLATEBODY, ItemID.BLACK_PLATELEGS),
    MITHRIL(NpcID.ANIMATED_MITHRIL_ARMOUR, ItemID.MITHRIL_FULL_HELM, ItemID.MITHRIL_PLATEBODY, ItemID.MITHRIL_PLATELEGS),
    ADAMANT(NpcID.ANIMATED_ADAMANT_ARMOUR, ItemID.ADAMANT_FULL_HELM, ItemID.ADAMANT_PLATEBODY, ItemID.ADAMANT_PLATELEGS),
    RUNE(NpcID.ANIMATED_RUNE_ARMOUR, ItemID.RUNE_FULL_HELM, ItemID.RUNE_PLATEBODY, ItemID.RUNE_PLATELEGS);

    private static final Position LOCATION_1 = new Position(2851, 3536);
    private static final Position LOCATION_2 = new Position(2857, 3536);

    private final int npcId;
    private final int helmId;
    private final int platebodyId;
    private final int platelegsId;

    Armour(int npcId, int helmId, int platebodyId, int platelegsId) {
        this.npcId = npcId;
        this.helmId = helmId;
        this.platebodyId = platebodyId;
        this.platelegsId = platelegsId;
    }

    public void animate(Player player, Position position) {
        player.performAnimation(new Animation(827, 0));
        player.setForceMovement(new ForceMovement(player.getPosition(), new Position(0, 3), 4, 100, 6, 820));
        TaskManager.submit(3, () -> {
            player.moveTo(player.getPosition().transform(0, 3, 0));
            player.setForceMovement(null);
        });
        var builder = new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE);
        builder.setText("You place your armour on the platform where it disappears...", "The animator hums; something appears to be working...");
        player.playSound(new Sound(2234, 10));
        builder.setPostAction($ -> {
            var animator_position = position.getDistance(LOCATION_1) < position.getDistance(LOCATION_2) ? LOCATION_1 : LOCATION_2;
            var animatedArmour = new AnimatedArmour(this, animator_position);
            player.getInventory().delete(helmId, 1);
            player.getInventory().delete(platebodyId, 1);
            player.getInventory().delete(platelegsId, 1);
            animatedArmour.setOwner(player);
            animatedArmour.spawn();
            animatedArmour.say("I'm ALIVE!");
            TaskManager.submit(3, () -> {
                // TODO: maybe remove clipping mask because atm armour cannot reach player.
                animatedArmour.getCombat().target(player);
                player.getPacketSender().sendEntityHint(animatedArmour);
            });
            animatedArmour.performAnimation(new Animation(4166));
            player.playSound(new Sound(1909));
            player.playSound(new Sound(1904));
            DialogueManager.start(player, -1);
            player.getWarriorsGuild().setLastAnimatedArmour(animatedArmour);
            startDespawnTask(player, animatedArmour);
        });
        builder.start(player);
    }

    private void startDespawnTask(Player player, AnimatedArmour animatedArmour) {
        var armour = this;
        final Task task = new Task(500, false) {
            @Override
            protected void execute() {
                stop();
                if(player.getWarriorsGuild().getLastAnimatedArmour() != null) {
                    player.getWarriorsGuild().setLostArmour(armour);
                    player.getWarriorsGuild().setLastAnimatedArmour(null);
                    World.remove(animatedArmour);
                }
            }
        };
        animatedArmour.setLostTask(task);
        TaskManager.submit(task);
    }

    public boolean has(Player player) {
        return player.getInventory().contains(helmId)
                && player.getInventory().contains(platebodyId)
                && player.getInventory().contains(platelegsId);
    }

    public int getNpcId() {
        return npcId;
    }

    public Item[] getArmour() {
        return new Item[]{new Item(helmId), new Item(platebodyId), new Item(platelegsId)};
    }

    public static Armour forId(int itemId) {
        for (var armor : values()) {
            if (armor.helmId == itemId || armor.platelegsId == itemId || armor.platebodyId == itemId) {
                return armor;
            }
        }
        return null;
    }
}
