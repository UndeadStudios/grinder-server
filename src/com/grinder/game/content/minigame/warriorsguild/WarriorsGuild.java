package com.grinder.game.content.minigame.warriorsguild;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.content.minigame.warriorsguild.drops.Defender;
import com.grinder.game.content.minigame.warriorsguild.rooms.Jimmy;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.Catapult;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.CatapultAttackStyle;
import com.grinder.game.content.minigame.warriorsguild.rooms.dummy.Dummy;
import com.grinder.game.content.minigame.warriorsguild.rooms.shotput.ShotPut;
import com.grinder.game.content.minigame.warriorsguild.tokens.AnimatedArmour;
import com.grinder.game.content.minigame.warriorsguild.tokens.Armour;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.passages.PassageManager;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;

import java.util.HashMap;
import java.util.Map;

import static com.grinder.util.NpcID.KAMFREENA;

/**
 * @author L E G E N D
 */
public final class WarriorsGuild {

    private static final Position BASEMENT = new Position(2909, 9969, 0);
    private static final Position UPPER_FLOOR = new Position(2843, 3540, 2);

    @Expose
    private Defender lastDefender;
    @Expose
    private Armour lostArmour;
    @Expose
    private int gamesTokens;
    private boolean inside;
    private AnimatedArmour lastAnimatedArmour;
    private CatapultAttackStyle selectedCatapultAttackStyle;
    private int tick;
    private boolean escorting;
    private boolean checked;
    private Task kegsTask;
    private final Map<Integer, GameObject> usedKegs = new HashMap<>();
    private boolean handsDusted;

    public WarriorsGuild() {
        selectedCatapultAttackStyle = CatapultAttackStyle.STAB;
    }

    static {
        // Basement
        /*ObjectActions.INSTANCE.onClick(new int[]{ObjectID.DOOR_232}, action -> {
            var player = action.getPlayer();
            if (player.getPosition().getX() >= 2912) {
                reset(player);
            } else {
                if (!enterBaseRoom(player)) {
                    return false;
                }
            }
            player.playSound(new Sound(77));
            PassageManager.open(player, action.getObject());

            return true;
        });*/

        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.MAGICAL_ANIMATOR}, action -> {
            handleAnimator(action.getPlayer());
            return true;
        });
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.HEAVY_DOOR, ObjectID.HEAVY_DOOR_3}, action -> {
            var player = action.getPlayer();
            player.performAnimation(new Animation(2572));
            player.playSound(new Sound(1927));
            player.getSkillManager().addExperience(Skill.STRENGTH, 1);
            PassageManager.open(player, action.getObject());
            player.playSound(new Sound(1926));
            return false;
        });
        try {
            Class.forName(Dummy.class.getName());
            Class.forName(Catapult.class.getName());
            Class.forName(ShotPut.class.getName());
            Class.forName(Jimmy.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addTokens(int tokens) {
        gamesTokens += tokens;
    }

    public void removeTokens(int tokens) {
        gamesTokens -= tokens;
    }

    public static boolean enterBaseRoom(Player player) {
        if (player.getPosition().getX() >= 2912) {
            reset(player);
            return true;
        }
        if (!checkTokens(player)) {
            return false;
        }
        if (!hasDefender(player, Defender.RUNE) && !hasDefender(player, Defender.DRAGON) && !hasDefender(player, Defender.AVERNIC_HILT)) {
            DialogueManager.sendStatement(player, "You need a Rune defender at-least to enter this room.");
            return false;
        }

        //player.playSound(new Sound(77));
        start(player);
        return true;
    }

    public static boolean canEnterUpperRoom(Player player) {
        if (player.getX() != 2846) { // player is inside the arena already probably
            reset(player);
            return true;
        }
        if (!checkTokens(player)) {
            return false;
        }

        if (player.getWarriorsGuild().getLastDefender() == Defender.DRAGON) {
            new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(KAMFREENA).setExpression(DialogueExpression.CALM).
                    setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(KAMFREENA).setExpression(DialogueExpression.CALM).
                            setText("If you'd rather stay up here, I've released the cyclopes", "that can drop rune defenders. Enter when you're", "ready.")).
                    setText("I see you have one of the dragon defenders already!",
                            "Well done, But you can only get more dragon",
                            "defenders from the basement.").start(player);
            return true;
        } else if (player.getWarriorsGuild().getLastDefender() == Defender.RUNE) {
            new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(KAMFREENA).setExpression(DialogueExpression.CALM).
                    setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(KAMFREENA).setExpression(DialogueExpression.CALM).
                            setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(KAMFREENA).setExpression(DialogueExpression.CALM).
                                    setText("The Cyclopes up here will continue to have a chance of", "dropping rune defenders. Have fun in there.")).
                            setText("You should speak to my apprentice Lorelai in the", "basement and test yourself against even stronger", "Cyclopes!")).
                    setText("I see you have one of the rune defenders",
                            "already! Well done.").start(player);
            return true;
        }

        //can enter, so start minigame
        player.playSound(new Sound(44));
        start(player);
        return true;
    }

    public static boolean checkTokens(Player player) {
        if (!isAttackCapeEquipped(player))
            if (!player.getInventory().contains(new Item(ItemID.WARRIOR_GUILD_TOKEN, 100))) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT).setItem(ItemID.WARRIOR_GUILD_TOKEN, 250).
                        setText("You don't have enough Warrior Guild Tokens to enter", "the cyclops enclosure yet, collect at least 100 then", "come back.").
                        start(player);
                return false;
            }
        return true;
    }

    public static Defender getNextDefender(Player player) {
        Defender defender = Defender.BRONZE;
        for (int index = Defender.values().length - 1; index >= 0; index--) {
            defender = Defender.values()[index];
            if (player.getWarriorsGuild().getLastDefender() == Defender.RUNE || player.getWarriorsGuild().getLastDefender() == Defender.DRAGON) {
                return Misc.random(0, 2) % 2 == 0 ? defender : Defender.DRAGON;
            }
            if (hasDefender(player, defender)) {
                defender = defender.getNext();
                break;
            }
        }
        if (defender.ordinal() <= Defender.RUNE.ordinal()) {
            if (player.getWarriorsGuild().getLastDefender() != null) {
                if (defender.ordinal() == player.getWarriorsGuild().getLastDefender().ordinal() - 2) {
                    return player.getWarriorsGuild().getLastDefender().getNext();
                }
                if (defender.ordinal() - 1 > player.getWarriorsGuild().getLastDefender().ordinal()) {
                    return player.getWarriorsGuild().getLastDefender().getNext();
                }
                if (defender == player.getWarriorsGuild().getLastDefender()) {
                    return defender.getNext();
                }
                return defender;
            }
        }
        return defender;
    }

    public static boolean hasRequirements(Player player) {
        var attackLevel = player.getSkillManager().getCurrentLevel(Skill.ATTACK);
        var strengthLevel = player.getSkillManager().getCurrentLevel(Skill.STRENGTH);
        return attackLevel == 99 || strengthLevel == 99 || attackLevel + strengthLevel > 130;
    }

    public static boolean isAttackCapeEquipped(Player player) {
        return player.getEquipment().contains(ItemID.ATTACK_CAPE) || player.getEquipment().contains(ItemID.ATTACK_CAPE_T_) || player.getEquipment().contains(ItemID.MAX_CAPE) || player.getEquipment().contains(ItemID.AVAS_MAX_CAPE) || player.getEquipment().contains(ItemID.ARDOUGNE_MAX_CAPE)
                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE)
                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE_2)
                || player.getEquipment().contains(ItemID.GUTHIX_MAX_CAPE)
                || player.getEquipment().contains(ItemID.INFERNAL_MAX_CAPE)
                || player.getEquipment().contains(ItemID.INFERNAL_MAX_CAPE_2)
                || player.getEquipment().contains(ItemID.SARADOMIN_MAX_CAPE)
                || player.getEquipment().contains(ItemID.ZAMORAK_MAX_CAPE)
                || player.getEquipment().contains(ItemID.MAX_CAPE_2)
                || player.getEquipment().contains(ItemID.MYTHICAL_MAX_CAPE)
                || player.getEquipment().contains(ItemID.MAX_CAPE_3);
    }

    public static boolean hasDefender(Player player, Defender defender) {
        var avernic_hilt = false;
        if (defender == Defender.AVERNIC_HILT) {
            avernic_hilt = player.getInventory().contains(ItemID.AVERNIC_DEFENDER_HILT) ||
                    player.getEquipment().contains(ItemID.AVERNIC_DEFENDER_HILT) ||
                    BankUtil.contains(player, ItemID.AVERNIC_DEFENDER_HILT);

        }
        if (avernic_hilt || player.getInventory().contains(defender.getId()) || player.getEquipment().contains(defender.getId())) {
            return true;
        }
        return BankUtil.contains(player, defender.getId());
    }

    public static void setLastDefender(Player player, Defender defender) {
        while (defender != null && defender != Defender.BRONZE && !hasDefender(player, defender)) {
            defender = defender.getPrevious();
        }
        if (defender == Defender.BRONZE && !hasDefender(player, defender)) {
            defender = null;
        }
        player.getWarriorsGuild().setLastDefender(defender);
    }

    private static void start(Player player) {
        player.getWarriorsGuild().setInside(true);
        player.getWarriorsGuild().tick = 100;
    }

    public static void reset(Player player) {
        player.getWarriorsGuild().setInside(false);
        player.getWarriorsGuild().setLastAnimatedArmour( null);
        player.getWarriorsGuild().setLostArmour(null);
    }

    public static void process(Player player) {
        if (!player.getWarriorsGuild().isInside()) {
            return;
        }
        if (player.getWarriorsGuild().tick++ % 100 == 0) {
            if (isAttackCapeEquipped(player))
                return;
            var tokens = new Item(ItemID.WARRIOR_GUILD_TOKEN, 10);
            if (player.getInventory().contains(tokens)) {
                player.getInventory().delete(tokens);
                return;
            }
            TaskManager.submit(67, () -> {
                if (!player.getWarriorsGuild().isEscorting()) {
                    player.getWarriorsGuild().setEscorting(true);
                    player.sendMessage("You have ran out of tokens!");
                    player.sendMessage("You will be escorted from the room within 20 seconds, please leave.");
                }
            });
            TaskManager.submit(33, () -> {
                if (player.getWarriorsGuild().isEscorting()) {
                    leaveRoom(player);
                    new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY).
                            setNpcChatHead(player.getZ() == 2 ? KAMFREENA : NpcID.LORELAI).
                            setNext(null).
                            setText("Next time please leave as soon as your time is up.").start(player);
                    player.getWarriorsGuild().setEscorting(false);
                }
            });
        }
    }

    public static void onLogout(Player player) {
        if (player.getWarriorsGuild().isInside()) {
            leaveRoom(player);
        }
        var lastAnimatedArmour = player.getWarriorsGuild().getLastAnimatedArmour();
        if (lastAnimatedArmour != null) {
            World.remove(lastAnimatedArmour);
            player.getWarriorsGuild().setLostArmour(lastAnimatedArmour.getArmour());
            player.getWarriorsGuild().setLastAnimatedArmour(null);
        }

        Catapult.onLogout(player);
        Jimmy.onLogout(player);
    }

    public static void leaveRoom(Player player) {
        if (!player.getWarriorsGuild().isInside()) {
            return;
        }
        if (player.getZ() == 2) {
            player.moveTo(UPPER_FLOOR);
        } else {
            player.moveTo(BASEMENT);
        }
        reset(player);
    }

    public static void sendAnimatorMessage(Player player, int id) {
        if (id == 1) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("You already have some armour animated.", "Kill it before animating another.").start(player);
        }
        if (id == 2) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("You need a suitable platebody, legs and full helm of the same type to", "activate the armour animator.").start(player);
        }
        if (id == 3) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("You already had some armour animated.", "See if Shanomi can return it to you.").start(player);
        }
    }

    public static void handleAnimator(Player player) {
        if (player.getWarriorsGuild().getLastAnimatedArmour() != null) {
            sendAnimatorMessage(player, 1);
            return;
        }
        if (player.getWarriorsGuild().getLostArmour() != null) {
            sendAnimatorMessage(player, 3);
            return;
        }

        boolean has = false;
        for (int i = Armour.values().length - 1; i >= 0; i--) {
            var armor = Armour.values()[i];
            if (armor.has(player)) {
                armor.animate(player, player.getPosition());
                has = true;
                break;
            }
        }
        if (!has) {
            sendAnimatorMessage(player, 2);
        }
    }

    public static void handleAnimator(Player player, int id) {
        if (player.getWarriorsGuild().getLastAnimatedArmour() != null) {
            sendAnimatorMessage(player, 1);
            return;
        }
        if (player.getWarriorsGuild().getLostArmour() != null) {
            sendAnimatorMessage(player, 3);
            return;
        }
        Armour armour = Armour.forId(id);
        if (armour == null) {
            return;
        }
        if (!armour.has(player)) {
            sendAnimatorMessage(player, 2);
            return;
        }
        armour.animate(player, player.getPosition());
    }

    public static void showDefender(Player player, Defender defender) {
        if (defender == Defender.AVERNIC_HILT)
            return;
        var npc = player.getInteractingEntity().getAsNpc();
        var next = new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).
                setNext(null);
        if (npc.getId() == KAMFREENA)
            switch (defender) {
                case DRAGON:
                    next.setText("Well done! You can only get more Dragon defenders", "from the basement.")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).
                                    setText("If you'd rather stay up here, I've released the cyclopes",
                                            "that can drop rune defenders. Enter when you're",
                                            "ready."));
                    break;
                case RUNE:
                    next.setText("Well done, you truly are a brilliant warrior! You should", "speak to my apprentice Lorelai in the basement for an additional challenge.")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).
                                    setText("If you'd rather stay up here, I've released the cyclopes.",
                                            "Enter when you're ready."));
                    break;
                case ADAMANT:
                    next.setText("An adamant defender, eh? Trying for the Rune", "defender I'm betting. Okay, I've released those cyclopes.", "enter when you're ready.");
                    break;
                case MITHRIL:
                    next.setText("Mithril? Getting good at this aren't you? Okay, I've", "released those cyclopes that sometimes drop adamant", "defenders, enter when you're ready.");
                    break;
                case BLACK:
                    next.setText("Black, hmmm, about half way to Rune then! Keep", "going! Cyclopes have been released, enter when you're", "ready.");
                    break;
                case STEEL:
                    next.setText("Strong as steel eh? The next cyclopes are ready, toddle", "in when you're ready.");
                    break;
                case IRON:
                    next.setText("Wow, iron eh? The next cyclopes are ready, toddle in", "when you're ready.");
                    break;
                case BRONZE:
                    next.setText("Bronze eh? The next cyclopes are ready, toddle in", "when you're ready.");
                    break;
            }
        else {
            if (defender == Defender.DRAGON) {
                next.setText("Congratulations you've found a Dragon defender!", "You're welcome to go back inside and try to get more.");
            } else {
                if (player.getWarriorsGuild().getLastDefender() == Defender.RUNE) {
                    next.setText("You've already proved yourself to me, the door is", "unlocked.");
                } else {
                    next = null;
                }
            }
        }
        if (player.getWarriorsGuild().getLastDefender() == null || defender.ordinal() > player.getWarriorsGuild().getLastDefender().ordinal())
            setLastDefender(player, defender);
        new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("I have a " + defender.getName() + ", please let me in to kill some", "more Cyclopes!").setNext(next).start(player);
    }

    public void setSelectedCatapultAttackStyle(CatapultAttackStyle selectedCatapultAttackStyle) {
        this.selectedCatapultAttackStyle = selectedCatapultAttackStyle;
    }

    public Armour getLostArmour() {
        return lostArmour;
    }

    public void setLostArmour(Armour armour) {
        lostArmour = armour;
    }

    public Defender getLastDefender() {
        return lastDefender;
    }

    public void setLastDefender(Defender lastDefender) {
        this.lastDefender = lastDefender;
    }

    public AnimatedArmour getLastAnimatedArmour() {
        return lastAnimatedArmour;
    }

    public void setLastAnimatedArmour(AnimatedArmour lastAnimatedArmour) {
        this.lastAnimatedArmour = lastAnimatedArmour;
    }

    public Task getKegsTask() {
        return kegsTask;
    }

    public void setKegsTask(Task kegsTask) {
        this.kegsTask = kegsTask;
    }

    public CatapultAttackStyle getSelectedCatapultAttackStyle() {
        return selectedCatapultAttackStyle;
    }

    public int getTokens() {
        return gamesTokens;
    }

    public Map<Integer, GameObject> getKegs() {
        return usedKegs;
    }

    public boolean isHandsDusted() {
        return handsDusted;
    }

    public void setHandsDusted(boolean handsDusted) {
        this.handsDusted = handsDusted;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public boolean isEscorting() {
        return escorting;
    }

    public void setEscorting(boolean escorting) {
        this.escorting = escorting;
    }

    public void setTokens(int tokens) {
        gamesTokens = tokens;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}