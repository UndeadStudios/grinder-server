package com.grinder.game.model.passages;

import com.google.common.base.CaseFormat;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author L E G E N D
 */
public enum PassageCategory {
    DOOR(60, 62) {
        @Override
        public boolean open(Player player, Passage passage) {
            if (passage.isLocked()) {
                player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                player.sendMessage("The door is locked.");
                return true;
            }
            if (passage.isBroken()) {
                player.playSound(new Sound(Sounds.BROKEN_DOOR_OR_GATE));
                player.sendMessage("The door seems to be broken.");
                return true;
            }
            if (passage.getCurrentState() == PassageState.OPENED) {
                player.sendMessage("The door is already open.");
                return true;
            }
            passage.playSound(player, PassageState.OPENED);
            passage.playAnimation(player);
            passage.switchAttachment();
            passage.switchState();
            if (passage.getMode() == PassageMode.FORCE) {
                passage.pass(player);
            } else {
                passage.autoRevert();
            }
            return true;
        }

        @Override
        public boolean close(Player player, Passage passage) {
            if (passage.getCurrentState() == PassageState.CLOSED) {
                player.sendMessage("The door is already closed.");
                return false;
            }
            if (passage.isBroken()) {
                player.sendMessage("The door seems to be broken.");
                return true;
            }
            passage.checkCloseCount();
            if (passage.isStuck()) {
                player.playSound(new Sound(Sounds.DOOR_STUCK));
                player.sendMessage("This door seems to be stuck.");
                return true;
            }
            passage.switchAttachment();
            passage.switchState();
            passage.playSound(player, PassageState.CLOSED);
            passage.setCloseCounter(passage.getCloseCounter() + 1);
            return true;
        }
    },
    WOODEN_GATE(60, 67) {
        @Override
        public boolean open(Player player, Passage passage) {
            if (passage.isLocked()) {
                player.sendMessage("The gate is locked.");
                player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                return true;
            }
            if (passage.isBroken()) {
                player.sendMessage("The gate is broken.");
                player.getPacketSender().sendSound(Sounds.BROKEN_DOOR_OR_GATE);
                return true;
            }
            if (passage.getCurrentState() == PassageState.OPENED) {
                player.sendMessage("The gate is already open.");
                return true;
            }
            if (passage.isStuck()) {
                player.playSound(new Sound(Sounds.DOOR_STUCK));
                player.sendMessage("This gate seems to be stuck.");
                return true;
            }

            passage.playSound(player, PassageState.OPENED);
            passage.playAnimation(player);
            passage.switchAttachment();
            passage.switchState();
            if (passage.getMode() == PassageMode.FORCE) {
                passage.pass(player);
            } else {
                passage.autoRevert();
            }
            return true;
        }

        @Override
        public boolean close(Player player, Passage passage) {
            if (passage.getCurrentState() == PassageState.CLOSED) {
                player.sendMessage("The gate is already closed.");
                return true;
            }
            passage.checkCloseCount();
            if (passage.isStuck()) {
                player.playSound(new Sound(Sounds.DOOR_STUCK));
                player.sendMessage("This gate seems to be stuck.");
                return true;
            }
            passage.switchAttachment();
            passage.switchState();
            passage.autoRevert();
            passage.playSound(player, PassageState.CLOSED);
            passage.setCloseCounter(passage.getCloseCounter() + 1);
            return true;
        }
    },
    GATE(68, 69) {
        @Override
        public boolean open(Player player, Passage passage) {
            if (passage.isLocked()) {
                player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                player.sendMessage("The gate is locked.");
                return true;
            }
            if (passage.isBroken()) {
                player.getPacketSender().sendSound(Sounds.BROKEN_DOOR_OR_GATE);
                player.sendMessage("This gate is broken.");
                return true;
            }
            if (passage.getCurrentState() == PassageState.OPENED) {
                player.sendMessage("The gate is already open.");
                return true;
            }
            if (passage.isStuck()) {
                player.playSound(new Sound(Sounds.DOOR_STUCK));
                player.sendMessage("This gate seems to be stuck.");
                return true;
            }

            passage.playSound(player, PassageState.OPENED);
            passage.playAnimation(player);
            passage.switchAttachment();
            passage.switchState();
            if (passage.getMode() == PassageMode.FORCE) {
                passage.pass(player);
            } else {
                passage.autoRevert();
            }
            return true;
        }

        @Override
        public boolean close(Player player, Passage passage) {
            if (passage.getCurrentState() == PassageState.CLOSED) {
                player.sendMessage("The gate is already closed.");
                return true;
            }
            if (passage.isBroken()) {
                player.sendMessage("The gate seems to be broken.");
                return true;
            }
            passage.checkCloseCount();
            if (passage.isStuck()) {
                player.playSound(new Sound(Sounds.DOOR_STUCK));
                player.sendMessage("This gate seems to be stuck.");
                return true;
            }

            passage.switchAttachment();
            passage.switchState();
            passage.autoRevert();
            passage.playSound(player, PassageState.CLOSED);
            passage.setCloseCounter(passage.getCloseCounter() + 1);
            return true;
        }
    },
    CURTAIN(59, 59) {
        @Override
        public boolean open(Player player, Passage passage) {
            passage.playSound(player, PassageState.OPENED);
            passage.switchState();
            passage.autoRevert();
            return true;
        }

        @Override
        public boolean close(Player player, Passage passage) {
            passage.playSound(player, PassageState.CLOSED);
            passage.switchState();
            passage.autoRevert();
            return true;
        }
    },
    TRAPDOOR(60, 62) {
        @Override
        public boolean open(Player player, Passage passage) {
            if (passage.isLocked()) {
                player.sendMessage("This trapdoor is locked.");
                return true;
            }
            if ((passage.getId(PassageState.OPENED) == -1 && passage.climbPosition == null)) {
                player.sendMessage("This trapdoor seems locked from the inside.");
                return true;
            }

            if (passage.getCurrentState() == PassageState.CLOSED) {
                if (passage.getId(passage.getCurrentState().opposite()) != -1) {
                    passage.playSound(player, PassageState.OPENED);
                    passage.switchState();
                    passage.autoRevert();
                    player.sendMessage("You open the trapdoor.");
                    player.getPacketSender().sendSound(64);

                    var forceClimbAndClose = false;
                    var hasClimbDownOption = false;
                    var definitions = ObjectDefinition.forId(passage.getId(PassageState.OPENED));
                    if (definitions != null) {
                        var options = definitions.actions;
                        hasClimbDownOption = Arrays.stream(options).filter(Objects::nonNull).anyMatch(option -> option.toLowerCase().contains("climb"));
                        if (options.length != 0 && Arrays.stream(options).filter(Objects::nonNull).noneMatch(option -> option.toLowerCase().contains("close"))) {
                            forceClimbAndClose = true;
                        }
                    }
                    if (forceClimbAndClose && !hasClimbDownOption) {
                        Passage.climbTrapdoor(player, passage);
                    }

                } else {
                    Passage.climbTrapdoor(player, passage);
                }

            }
            return true;
        }

        @Override
        public boolean close(Player player, Passage passage) {
            if (passage.getId(PassageState.OPENED) == -1) {
                player.sendMessage("A magical force prevents you from closing this trapdoor.");
                return true;
            }
            if (passage.getCurrentState() == PassageState.OPENED) {
                player.sendMessage("You close the trapdoor.");
                passage.playSound(player, PassageState.CLOSED);
                passage.switchState();
            }
            return true;
        }

    };

    public abstract boolean open(Player player, Passage passage);

    public abstract boolean close(Player player, Passage passage);

    private final int openSound;

    private final int closeSound;

    public void playSound(Player player, PassageState passageState) {
        if (getSound(passageState) > 0) {
            player.playSound(new Sound(getSound(passageState)));
        }
    }

    public int getSound(PassageState passageState) {
        return passageState == PassageState.CLOSED ? closeSound : openSound;
    }

    public String getName(){
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }
    PassageCategory(int closeSound, int openSound) {
        this.closeSound = closeSound;
        this.openSound = openSound;

    }
}
