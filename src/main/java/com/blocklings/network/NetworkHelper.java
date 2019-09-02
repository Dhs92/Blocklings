package com.blocklings.network;

import com.blocklings.Blocklings;
import com.blocklings.network.messages.*;
import com.blocklings.network.messages.whitelist.BlocklingWhitelistAllMessage;
import com.blocklings.network.messages.whitelist.BlocklingWhitelistSingleMessage;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHelper
{
    private static SimpleNetworkWrapper network = net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.newSimpleChannel(Blocklings.MODID);
    private static int id = 0;

    public static void registerMessages()
    {
        network.registerMessage(BlocklingWhitelistAllMessage.Handler.class, BlocklingWhitelistAllMessage.class, id++, Side.CLIENT);
        network.registerMessage(BlocklingWhitelistAllMessage.Handler.class, BlocklingWhitelistAllMessage.class, id++, Side.SERVER);
        network.registerMessage(BlocklingWhitelistSingleMessage.Handler.class, BlocklingWhitelistSingleMessage.class, id++, Side.CLIENT);
        network.registerMessage(BlocklingWhitelistSingleMessage.Handler.class, BlocklingWhitelistSingleMessage.class, id++, Side.SERVER);

        network.registerMessage(BlocklingTypeMessage.Handler.class, BlocklingTypeMessage.class, id++, Side.CLIENT);
        network.registerMessage(BlocklingTypeMessage.Handler.class, BlocklingTypeMessage.class, id++, Side.SERVER);
        network.registerMessage(CombatIntervalMessage.Handler.class, CombatIntervalMessage.class, id++, Side.CLIENT);
        network.registerMessage(CombatIntervalMessage.Handler.class, CombatIntervalMessage.class, id++, Side.SERVER);
        network.registerMessage(CombatLevelMessage.Handler.class, CombatLevelMessage.class, id++, Side.CLIENT);
        network.registerMessage(CombatLevelMessage.Handler.class, CombatLevelMessage.class, id++, Side.SERVER);
        network.registerMessage(CombatTimerMessage.Handler.class, CombatTimerMessage.class, id++, Side.CLIENT);
        network.registerMessage(CombatTimerMessage.Handler.class, CombatTimerMessage.class, id++, Side.SERVER);
        network.registerMessage(CombatXpMessage.Handler.class, CombatXpMessage.class, id++, Side.CLIENT);
        network.registerMessage(CombatXpMessage.Handler.class, CombatXpMessage.class, id++, Side.SERVER);
        network.registerMessage(CurrentGuiTabMessage.Handler.class, CurrentGuiTabMessage.class, id++, Side.CLIENT);
        network.registerMessage(CurrentGuiTabMessage.Handler.class, CurrentGuiTabMessage.class, id++, Side.SERVER);
        network.registerMessage(FarmingIntervalMessage.Handler.class, FarmingIntervalMessage.class, id++, Side.CLIENT);
        network.registerMessage(FarmingIntervalMessage.Handler.class, FarmingIntervalMessage.class, id++, Side.SERVER);
        network.registerMessage(FarmingLevelMessage.Handler.class, FarmingLevelMessage.class, id++, Side.CLIENT);
        network.registerMessage(FarmingLevelMessage.Handler.class, FarmingLevelMessage.class, id++, Side.SERVER);
        network.registerMessage(FarmingRangeMessage.Handler.class, FarmingRangeMessage.class, id++, Side.CLIENT);
        network.registerMessage(FarmingRangeMessage.Handler.class, FarmingRangeMessage.class, id++, Side.SERVER);
        network.registerMessage(FarmingTimerMessage.Handler.class, FarmingTimerMessage.class, id++, Side.CLIENT);
        network.registerMessage(FarmingTimerMessage.Handler.class, FarmingTimerMessage.class, id++, Side.SERVER);
        network.registerMessage(FarmingXpMessage.Handler.class, FarmingXpMessage.class, id++, Side.CLIENT);
        network.registerMessage(FarmingXpMessage.Handler.class, FarmingXpMessage.class, id++, Side.SERVER);
        network.registerMessage(InventoryStackMessage.Handler.class, InventoryStackMessage.class, id++, Side.CLIENT);
        network.registerMessage(InventoryStackMessage.Handler.class, InventoryStackMessage.class, id++, Side.SERVER);
        network.registerMessage(MiningIntervalMessage.Handler.class, MiningIntervalMessage.class, id++, Side.CLIENT);
        network.registerMessage(MiningIntervalMessage.Handler.class, MiningIntervalMessage.class, id++, Side.SERVER);
        network.registerMessage(MiningLevelMessage.Handler.class, MiningLevelMessage.class, id++, Side.CLIENT);
        network.registerMessage(MiningLevelMessage.Handler.class, MiningLevelMessage.class, id++, Side.SERVER);
        network.registerMessage(MiningRangeMessage.Handler.class, MiningRangeMessage.class, id++, Side.CLIENT);
        network.registerMessage(MiningRangeMessage.Handler.class, MiningRangeMessage.class, id++, Side.SERVER);
        network.registerMessage(MiningTimerMessage.Handler.class, MiningTimerMessage.class, id++, Side.CLIENT);
        network.registerMessage(MiningTimerMessage.Handler.class, MiningTimerMessage.class, id++, Side.SERVER);
        network.registerMessage(MiningXpMessage.Handler.class, MiningXpMessage.class, id++, Side.CLIENT);
        network.registerMessage(MiningXpMessage.Handler.class, MiningXpMessage.class, id++, Side.SERVER);
        network.registerMessage(OpenGuiMessage.Handler.class, OpenGuiMessage.class, id++, Side.CLIENT);
        network.registerMessage(OpenGuiMessage.Handler.class, OpenGuiMessage.class, id++, Side.SERVER);
        network.registerMessage(ScaleMessage.Handler.class, ScaleMessage.class, id++, Side.CLIENT);
        network.registerMessage(ScaleMessage.Handler.class, ScaleMessage.class, id++, Side.SERVER);
        network.registerMessage(SkillPointsMessage.Handler.class, SkillPointsMessage.class, id++, Side.CLIENT);
        network.registerMessage(SkillPointsMessage.Handler.class, SkillPointsMessage.class, id++, Side.SERVER);
        network.registerMessage(StateMessage.Handler.class, StateMessage.class, id++, Side.CLIENT);
        network.registerMessage(StateMessage.Handler.class, StateMessage.class, id++, Side.SERVER);
        network.registerMessage(TaskActiveMessage.Handler.class, TaskActiveMessage.class, id++, Side.CLIENT);
        network.registerMessage(TaskActiveMessage.Handler.class, TaskActiveMessage.class, id++, Side.SERVER);
        network.registerMessage(TaskPriorityMessage.Handler.class, TaskPriorityMessage.class, id++, Side.CLIENT);
        network.registerMessage(TaskPriorityMessage.Handler.class, TaskPriorityMessage.class, id++, Side.SERVER);
        network.registerMessage(WoodcuttingIntervalMessage.Handler.class, WoodcuttingIntervalMessage.class, id++, Side.CLIENT);
        network.registerMessage(WoodcuttingIntervalMessage.Handler.class, WoodcuttingIntervalMessage.class, id++, Side.SERVER);
        network.registerMessage(WoodcuttingLevelMessage.Handler.class, WoodcuttingLevelMessage.class, id++, Side.CLIENT);
        network.registerMessage(WoodcuttingLevelMessage.Handler.class, WoodcuttingLevelMessage.class, id++, Side.SERVER);
        network.registerMessage(WoodcuttingRangeMessage.Handler.class, WoodcuttingRangeMessage.class, id++, Side.CLIENT);
        network.registerMessage(WoodcuttingRangeMessage.Handler.class, WoodcuttingRangeMessage.class, id++, Side.SERVER);
        network.registerMessage(WoodcuttingTimerMessage.Handler.class, WoodcuttingTimerMessage.class, id++, Side.CLIENT);
        network.registerMessage(WoodcuttingTimerMessage.Handler.class, WoodcuttingTimerMessage.class, id++, Side.SERVER);
        network.registerMessage(WoodcuttingXpMessage.Handler.class, WoodcuttingXpMessage.class, id++, Side.CLIENT);
        network.registerMessage(WoodcuttingXpMessage.Handler.class, WoodcuttingXpMessage.class, id++, Side.SERVER);
    }

    public static void sendToAll(IMessage message)
    {
        network.sendToAll(message);
    }

    public static void sendToServer(IMessage message)
    {
        network.sendToServer(message);
    }

    public static void sync(World world, IMessage message)
    {
        if (!world.isRemote)
        {
            sendToAll(message);
        }
        else
        {
            sendToServer(message);
        }
    }
}