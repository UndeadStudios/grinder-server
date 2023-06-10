package com.grinder.game.content.clan;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-15
 */
public class ClanMember {

    @Expose private final String name;
    @Expose private ClanChatRank rank;

    ClanMember(String name, ClanChatRank rank) {
        this.name = name;
        this.rank = rank;
    }

    public boolean is(final Player player){
        return name.equals(player.getUsername());
    }

    void messageIfOnline(final String message){
        getPlayerIfOnline().ifPresent(player -> player.sendMessage(message));
    }

    void ifOnline(final Consumer<Player> action){
        getPlayerIfOnline().ifPresent(action);
    }

    private Optional<Player> getPlayerIfOnline(){
        return World.findPlayerByName(name);
    }

    public String getName() {
        return name;
    }

    public void setRank(ClanChatRank rank) {
        this.rank = rank;
    }

    public ClanChatRank getRank() {
        return rank;
    }
}
