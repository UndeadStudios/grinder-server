package com.grinder.game.content.minigame.chamberoxeric.storage;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.party.COXParty;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.model.ui.UserContainerInterface;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class StorageUnitManager extends PacketInteraction {

    private static final int CREATION_INTERFACE = 67_000;

    private static final int CREATION_CONTAINER = 67005;

    public static final int SHARED_STORAGE_INTERFACE = 28010;

    public static final int SHARED_STORAGE_CONTAINER = 28016;

    private static final int PRIVATE_STORAGE_INTERFACE = 28020;

    private static final int PRIVATE_STORAGE_CONTAINER = 28029;

    private static final int EMPTY_STORAGE = 29_769;

    private static final int MALLIGNUM_PLANK = 21_036;

    private static final Animation HAMMER = new Animation(898);

    private static final Position[] STORAGE_POSITION = {new Position(3287, 5168),};

    public ItemContainer privateStorage;

    public StorageUnitManager() {

    }

    public StorageUnitManager(Player p) {
        this.privateStorage = new ItemContainer(p) {

            @Override
            public StackType stackType() {
                return StackType.STACKS;
            }

            @Override
            public ItemContainer refreshItems() {
                return null;
            }

            @Override
            public ItemContainer full() {
                return null;
            }

            @Override
            public int capacity() {
                return 90;
            }
        };
        new UserContainerInterface(CREATION_CONTAINER) {

            @Override
            public boolean handleOption(Player player, int id, int slot, int option) {
                if (player.getInterfaceId() != CREATION_INTERFACE) {
                    return false;
                }
                handleCreation(player, id);
                return true;
            }
        };

        new UserContainerInterface(SHARED_STORAGE_CONTAINER) {

            @Override
            public boolean handleOption(Player player, int id, int slot, int option) {
                if (player.getInterfaceId() != SHARED_STORAGE_INTERFACE) {
                    return false;
                }

                if (player.getCOX().getParty() == null) {
                    return false;
                }

                int amount = getAmount(option);

                if(amount == 4) {
                    player.setEnterSyntax(new EnterSyntax() {
                        @Override
                        public void handleSyntax(Player player, String input) {

                        }

                        @Override
                        public void handleSyntax(Player player, int input) {
                            if(input < 0) {
                                return;
                            }
                            player.getCOX().getParty().withdrawShared(player, id, input, slot);
                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                    return true;
                }

                player.getCOX().getParty().withdrawShared(player, id, amount, slot);
                return true;
            }
        };

        new UserContainerInterface(PRIVATE_STORAGE_CONTAINER) {

            @Override
            public boolean handleOption(Player player, int id, int slot, int option) {
                if (player.getInterfaceId() != PRIVATE_STORAGE_INTERFACE) {
                    return false;
                }

                if (player.getCOX().getParty() == null) {
                    return false;
                }

                int amount = getAmount(option);

                if(amount == 4) {
                    player.setEnterSyntax(new EnterSyntax() {
                        @Override
                        public void handleSyntax(Player player, String input) {

                        }

                        @Override
                        public void handleSyntax(Player player, int input) {
                            if(input < 0) {
                                return;
                            }
                            player.getCOX().getParty().withdraw(player, privateStorage, id, input, slot);
                            sendPrivateStorage(player);
                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                    return true;
                }

                player.getCOX().getParty().withdraw(player, privateStorage, id, amount, slot);
                sendPrivateStorage(player);
                return true;
            }
        };

        new UserContainerInterface(5064) {

            @Override
            public boolean handleOption(Player player, int id, int slot, int option) {
                if (player.getInterfaceId() == SHARED_STORAGE_INTERFACE) {

                    if (player.getCOX().getParty() == null) {
                        return false;
                    }

                    int amount = getAmount(option);

                    if(amount == 4) {
                        player.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {

                            }

                            @Override
                            public void handleSyntax(Player player, int input) {
                                if(input < 0) {
                                    return;
                                }
                                player.getCOX().getParty().storeShared(player, id, input, slot);
                            }
                        });
                        player.getPacketSender().sendEnterAmountPrompt("How many would you like to store?");
                        return true;
                    }
                    player.getCOX().getParty().storeShared(player, id, amount, slot);
                    return true;
                } else if (player.getInterfaceId() == PRIVATE_STORAGE_INTERFACE) {

                    if (player.getCOX().getParty() == null) {
                        return false;
                    }

                    int amount = getAmount(option);

                    if(amount == 4) {
                        player.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {

                            }

                            @Override
                            public void handleSyntax(Player player, int input) {
                                if(input < 0) {
                                    return;
                                }
                                player.getCOX().getParty().store(player, privateStorage, id, input, slot);
                                sendPrivateStorage(player);
                            }
                        });
                        player.getPacketSender().sendEnterAmountPrompt("How many would you like to store?");
                        return true;
                    }

                    player.getCOX().getParty().store(player, privateStorage, id, amount, slot);
                    sendPrivateStorage(player);
                    return true;
                }

                return false;
            }
        };
    }

    public static void openCreation(Player player) {
        player.getPacketSender().sendInterface(CREATION_INTERFACE);
    }

    private static void handleCreation(Player player, int id) {
        if (player.getCOX().getParty() == null) {
            return;
        }

        if (StorageUnit.FOR_ITEM.get(id) == null) {
            return;
        }

        StorageUnit unit = StorageUnit.FOR_ITEM.get(id);

        if (!Skill.hasCorrectLevel(player, Skill.CONSTRUCTION, unit.level)) {
            return;
        }

        int planks = player.getInventory().getAmount(MALLIGNUM_PLANK);

        if (planks < unit.planks) {
            player.getPacketSender().sendMessage(
                    "You don't have enough planks to build this storage. You need at least " + unit.planks + " planks");
            return;
        }

        if (player.getCOX().getParty().storage == unit) {
            player.getPacketSender().sendMessage("This storage is already selected.");
            return;
        }

        if (player.getCOX().getParty().storage.ordinal() > unit.ordinal()) {
            player.getPacketSender().sendMessage("You cannot downgrade your storage");
            return;
        }

        player.getInventory().delete(MALLIGNUM_PLANK, unit.planks);

        player.getPacketSender().sendInterfaceRemoval();
        player.performAnimation(HAMMER);

        player.getCOX().getParty().storage = unit;

        player.getCOX().points += 100;

        TaskManager.submit(new Task(3) {

            @Override
            protected void execute() {
                for (Position p : STORAGE_POSITION) {
                    Position pos = p.clone().setZ(player.getPosition().getZ());
                    DynamicGameObject storage = DynamicGameObject.createPublic(unit.id, pos);
                    World.addObject(storage);
                }
                stop();
            }
        });
    }

    private static void upgrade(Player player, int id) {
        if (StorageUnit.FOR_OBJECT.get(id) == null) {
            return;
        }

        openCreation(player);
    }

    private static void openSharedStorage(Player player, int id) {
        if (StorageUnit.FOR_OBJECT.get(id) == null) {
            return;
        }

        if (player.getCOX().getParty() == null) {
            return;
        }

        sendSharedStorage(player);
    }

    private static void sendSharedStorage(Player p) {
        COXParty party = p.getCOX().getParty();

        p.getPacketSender().sendInterfaceSet(StorageUnitManager.SHARED_STORAGE_INTERFACE, 5063);

        party.updateStorage();
    }

    private static void openPrivateStorage(Player p, int id) {
        if (StorageUnit.FOR_OBJECT.get(id) == null) {
            return;
        }

        if (p.getCOX().getParty() == null) {
            return;
        }

        sendPrivateStorage(p);
    }

    private static void sendPrivateStorage(Player p) {
        p.getPacketSender().sendInterfaceSet(StorageUnitManager.PRIVATE_STORAGE_INTERFACE, 5063);

        p.getPacketSender().sendItemContainer(p.getCOX().storage.privateStorage, StorageUnitManager.PRIVATE_STORAGE_CONTAINER);

        p.getPacketSender().sendItemContainer(p.getInventory(), 5064);

        int size = p.getCOX().storage.privateStorage.getValidItems().size();
        int maxSize = p.getCOX().getParty().storage.capacity;

        p.getPacketSender().sendString(28025, ""+size);
        p.getPacketSender().sendString(28026, ""+maxSize);
    }

    private static void sendDepositToPrivateStorage(Player p) {
        for(Item item : p.getInventory().getValidItems()) {
            int slot = p.getInventory().getSlot(item.getId());
            p.getCOX().getParty().store(p, p.getCOX().storage.privateStorage, item.getId(), item.getAmount(), slot);
        }
        sendPrivateStorage(p);
    }

    private static void sendWithdrawFromPrivateStorage(Player p) {
        for(Item item : p.getCOX().storage.privateStorage.getValidItems()) {
            int slot = p.getCOX().storage.privateStorage.getSlot(item.getId());
            p.getCOX().getParty().withdraw(p, p.getCOX().storage.privateStorage, item.getId(), item.getAmount(), slot);
        }
        sendPrivateStorage(p);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (type) {
            case 1:
                openSharedStorage(player, object.getId());
                break;
            case 2:
                openPrivateStorage(player, object.getId());
                break;
            case 5:
                upgrade(player, object.getId());
                switch (object.getId()) {
                    case EMPTY_STORAGE:
                        openCreation(player);
                        return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        switch (button) {
            case 28019:
                sendPrivateStorage(player);
                return true;
            case 28027:
                sendSharedStorage(player);
                return true;
            case 28030:
                sendWithdrawFromPrivateStorage(player);
                return true;
            case 28031:
                sendDepositToPrivateStorage(player);
                return true;
        }
        return false;
    }
}
