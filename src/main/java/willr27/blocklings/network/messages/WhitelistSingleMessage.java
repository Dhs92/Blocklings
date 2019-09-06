package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class WhitelistSingleMessage implements IMessage
{
    int whitelistId;
    ResourceLocation entry;
    boolean value;
    int entityId;

    private WhitelistSingleMessage() {}
    public WhitelistSingleMessage(int whitelistId, ResourceLocation entry, boolean value, int entityId)
    {
        this.whitelistId = whitelistId;
        this.entry = entry;
        this.value = value;
        this.entityId = entityId;
    }

    public static void encode(WhitelistSingleMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.whitelistId);
        buf.writeString(msg.entry.toString());
        buf.writeBoolean(msg.value);
        buf.writeInt(msg.entityId);
    }

    public static WhitelistSingleMessage decode(PacketBuffer buf)
    {
        WhitelistSingleMessage msg = new WhitelistSingleMessage();
        msg.whitelistId = buf.readInt();
        msg.entry = new ResourceLocation(buf.readString());
        msg.value = buf.readBoolean();
        msg.entityId = buf.readInt();

        return msg;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            NetworkEvent.Context context = ctx.get();
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            PlayerEntity player = isClient ? Minecraft.getInstance().player : ctx.get().getSender();
            if (player != null)
            {
                BlocklingEntity blockling = (BlocklingEntity) player.world.getEntityByID(entityId);
                if (blockling != null)
                {
                    blockling.aiManager.getWhitelist(whitelistId).setEntry(entry, value, !isClient);
                }
            }
        });
    }
}