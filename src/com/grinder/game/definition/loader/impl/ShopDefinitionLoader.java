package com.grinder.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ShopDefinition;
import com.grinder.game.definition.loader.DefinitionLoader;
import com.grinder.game.model.item.container.shop.LimitedShop;
import com.grinder.game.model.item.container.shop.Shop;
import com.grinder.game.model.item.container.shop.ShopManager;

public class ShopDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Throwable {

        final FileReader reader = new FileReader(file());
        final ShopDefinition[] shopDefinitions = new Gson().fromJson(reader, ShopDefinition[].class);

        ShopManager.shops.clear();

        for (ShopDefinition shopDefinition : shopDefinitions) {
            ShopManager.shops.put(shopDefinition.getId(), new Shop(shopDefinition.getId(), shopDefinition.getName(), shopDefinition.isNewInterface(), shopDefinition.getOriginalStock()));
        }
        reader.close();

        /**
         * Loads limited shop
         */
        LimitedShop.loadStore();
    }

    @Override
    public String file() {
        return GameConstants.DEFINITIONS_DIRECTORY + "shops.json";
    }
}
