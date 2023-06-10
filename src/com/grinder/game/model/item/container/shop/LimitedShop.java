package com.grinder.game.model.item.container.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinder.Server;
import com.grinder.game.GameConstants;
import com.grinder.game.definition.ShopDefinition;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.service.tasks.TaskRequest;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LimitedShop {

    public static final String FILE = GameConstants.DATA_DIRECTORY + "/limit_shop.json";

    private static ShopDefinition[] shopDefinitions;

    public static void loadStore() throws Throwable {

        final FileReader reader = new FileReader(LimitedShop.FILE);
        shopDefinitions = new Gson().fromJson(reader, ShopDefinition[].class);

        for (ShopDefinition shopDefinition : shopDefinitions) {
            ShopManager.shops.put(shopDefinition.getId(), new Shop(shopDefinition.getId(), shopDefinition.getName(), shopDefinition.isNewInterface(), shopDefinition.getOriginalStock()).setAsLimitedShop());
        }

        reader.close();
    }

    public static void saveStore() {
        ServiceManager.INSTANCE.getTaskService().addTaskRequest(new TaskRequest(()-> {

            final Gson gson = new GsonBuilder().setPrettyPrinting().create();

            for (ShopDefinition shopDefinition : shopDefinitions) {
                Shop shop = ShopManager.shops.get(shopDefinition.getId());
                for (int i = 0, l = shop.getOriginalStock().length; i < l; i++) {
                    if (shop.getCurrentStock()[i] != null) {
                        shopDefinition.getOriginalStock()[i] = shop.getCurrentStock()[i];
                    }
                }
            }

            try {
                final FileWriter writer = new FileWriter(FILE);

                gson.toJson(shopDefinitions, writer);

                writer.flush();
                writer.close();

            } catch (IOException e) {
                Server.getLogger().warn("Could not save the Limited Shop", e);
            }

        }, true));
    }
}
