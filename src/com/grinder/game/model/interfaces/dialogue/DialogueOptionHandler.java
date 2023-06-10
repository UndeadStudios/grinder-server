package com.grinder.game.model.interfaces.dialogue;

import com.grinder.game.entity.agent.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * Created by Stan van der Bend on 28/10/2017.
 * project: live
 * package: runeworld.model.dialogue
 */
public class DialogueOptionHandler extends DialogueOptions {

    private final List<Consumer<Player>> actions = new ArrayList<>(5);

    public DialogueOptionHandler addAction(int buttonIndex, Consumer<Player> consumer){
        actions.add(buttonIndex, consumer);
        return this;
    }

    public int actionCount(){
        return actions.size();
    }

    @Override
    public void handleOption(Player player, int option) {
        //player.sendMessage("Handling option "+option);
        // TODO: Fix error which happens if you click on "Next.." in a dialogue 2 times (fast clicking)

        /*java.lang.IndexOutOfBoundsException: Index 4 out of bounds for length 4
        at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
        at java.base/java.util.Objects.checkIndex(Objects.java:372)
        at java.base/java.util.ArrayList.get(ArrayList.java:458)
        at com.grinder.game.model.interfaces.dialogue.DialogueOptionHandler.handleOption(DialogueOptionHandler.java:32)
        at com.grinder.game.entity.agent.player.PlayerSettings$configureDialogueButtons$5.invoke(PlayerSettings.kt:173)
        at com.grinder.game.entity.agent.player.PlayerSettings$configureDialogueButtons$5.invoke(PlayerSettings.kt:25)
        at com.grinder.game.model.ButtonActions.handleClick(ButtonActions.kt:43)
        at com.grinder.net.packet.impl.ButtonClickPacketListener.handlers(ButtonClickPacketListener.java:284)
        at com.grinder.net.packet.impl.ButtonClickPacketListener.handleMessage(ButtonClickPacketListener.java:92)*/


        actions.get(option-1).accept(player);
    }
}
