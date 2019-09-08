package willr27.blocklings.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import willr27.blocklings.Blocklings;
import willr27.blocklings.network.messages.*;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Blocklings.MODID, "channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void init()
    {
        int id = 0;

        HANDLER.registerMessage(id++, CustomNameMessage.class, CustomNameMessage::encode, CustomNameMessage::decode, CustomNameMessage::handle);
        HANDLER.registerMessage(id++, GoalActiveMessage.class, GoalActiveMessage::encode, GoalActiveMessage::decode, GoalActiveMessage::handle);
        HANDLER.registerMessage(id++, GoalPriorityMessage.class, GoalPriorityMessage::encode, GoalPriorityMessage::decode, GoalPriorityMessage::handle);
        HANDLER.registerMessage(id++, GuiInfoMessage.class, GuiInfoMessage::encode, GuiInfoMessage::decode, GuiInfoMessage::handle);
        HANDLER.registerMessage(id++, InventoryMessage.class, InventoryMessage::encode, InventoryMessage::decode, InventoryMessage::handle);
        HANDLER.registerMessage(id++, OpenGuiMessage.class, OpenGuiMessage::encode, OpenGuiMessage::decode, OpenGuiMessage::handle);
        HANDLER.registerMessage(id++, WhitelistAllMessage.class, WhitelistAllMessage::encode, WhitelistAllMessage::decode, WhitelistAllMessage::handle);
        HANDLER.registerMessage(id++, WhitelistSingleMessage.class, WhitelistSingleMessage::encode, WhitelistSingleMessage::decode, WhitelistSingleMessage::handle);
    }

    public static void sendToServer(IMessage message)
    {
        HANDLER.sendToServer(message);
    }

    public static void sendTo(PlayerEntity player, IMessage message)
    {
        HANDLER.sendTo(message, ((ServerPlayerEntity)player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAll(World world, IMessage message)
    {
        for (PlayerEntity player : world.getPlayers())
        {
            sendTo(player, message);
        }
    }

    public static void sync(World world, IMessage message)
    {
        if (world.isRemote) sendToServer(message);
        else sendToAll(world, message);
    }
}
