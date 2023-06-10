package com.grinder.game.content.skill.skillable.impl.hunter_new.catching.tables;

import com.grinder.game.definition.droptable.DropTableBuilder;
import com.grinder.game.model.item.Item;

public interface ImplingJarTable {

    Item roll();

    default DropTableBuilder builder() {
        return new DropTableBuilder();
    }

}
