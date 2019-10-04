package willr27.blocklings.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.gui.container.containers.EquipmentContainer;
import willr27.blocklings.gui.container.containers.utilities.UtilityContainer;
import willr27.blocklings.gui.screens.*;
import willr27.blocklings.gui.screens.utilities.UtilityScreen;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.OpenGuiMessage;

public class GuiHandler
{
    public static final int STATS_ID = 0;
    public static final int TASKS_ID = 1;
    public static final int EQUIPMENT_ID = 2;
    public static final int UTILITY_ID = 3;
    public static final int GENERAL_ID = 4;
    public static final int COMBAT_ID = 5;
    public static final int MINING_ID = 6;
    public static final int WOODCUTTING_ID = 7;
    public static final int FARMING_ID = 8;

    public static final int WHITELIST_ID = 9;


    public static void openGui(int guiId, BlocklingEntity blockling, PlayerEntity player)
    {
        openGui(guiId, blockling, player, true);
    }

    public static void openGui(int guiId, BlocklingEntity blockling, PlayerEntity player, boolean sync)
    {
        if (isClientOnly(guiId))
        {
            openGui(guiId, -1, blockling, player, !blockling.world.isRemote && sync);
        }
        else
        {
            if (!blockling.world.isRemote)
            {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                openGui(guiId, serverPlayer.currentWindowId, blockling, player, sync);
            }
            else
            {
                NetworkHandler.sendToServer(new OpenGuiMessage(guiId, 0, blockling.getEntityId(), true));
            }
        }
    }

    public static void openGui(int guiId, int windowId, BlocklingEntity blockling, PlayerEntity player, boolean sync)
    {
        if (!blockling.world.isRemote)
        {
            Container container = getContainer(guiId, windowId, blockling, player);
            if (container != null) ((ServerPlayerEntity)player).openContainer = container;
            if (sync) NetworkHandler.sendTo(player, new OpenGuiMessage(guiId, windowId, blockling.getEntityId()));
        }
        else
        {
            Container container = getContainer(guiId, windowId, blockling, player);
            if (container != null) ((ClientPlayerEntity)player).openContainer = container;
            Screen screen = getScreen(guiId, container, blockling, player);
            if (screen != null) Minecraft.getInstance().displayGuiScreen(screen);
            if (sync) NetworkHandler.sendToServer(new OpenGuiMessage(guiId, windowId, blockling.getEntityId()));
        }
    }

    private static boolean isClientOnly(int guiId)
    {
        switch (guiId)
        {
            case EQUIPMENT_ID:
            case UTILITY_ID:
                return false;

            case STATS_ID:
            case TASKS_ID:
            case GENERAL_ID:
            case COMBAT_ID:
            case MINING_ID:
            case WOODCUTTING_ID:
            case FARMING_ID:
            case WHITELIST_ID:
                return true;
        }

        return true;
    }

    private static Container getContainer(int guiId, int windowId, BlocklingEntity blockling, PlayerEntity player)
    {
        switch (guiId)
        {
            case EQUIPMENT_ID: return new EquipmentContainer(windowId, player.inventory, blockling.inventory);
            case UTILITY_ID: return new UtilityContainer(windowId, player.inventory, blockling);
        }

        return null;
    }

    private static Screen getScreen(int guiId, Container container, BlocklingEntity blockling, PlayerEntity player)
    {
        switch (guiId)
        {
            case STATS_ID: return new StatsScreen(blockling, player);
            case TASKS_ID: return new TasksScreen(blockling, player);
            case EQUIPMENT_ID: return new EquipmentScreen((EquipmentContainer) container, blockling, player);
            case UTILITY_ID: return new UtilityScreen(container, blockling, player);
            case GENERAL_ID: return new AbilitiesScreen(blockling, player);
            case COMBAT_ID: return new AbilitiesScreen(blockling, player);
            case MINING_ID: return new AbilitiesScreen(blockling, player);
            case WOODCUTTING_ID: return new AbilitiesScreen(blockling, player);
            case FARMING_ID: return new AbilitiesScreen(blockling, player);
            case WHITELIST_ID: return new WhitelistScreen(blockling, player);
        }

        return null;
    }
}
