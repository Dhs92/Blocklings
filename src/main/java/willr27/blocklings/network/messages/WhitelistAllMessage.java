package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;
import willr27.blocklings.whitelist.BlocklingWhitelist;
import willr27.blocklings.whitelist.Whitelist;

import java.util.function.Supplier;

public class WhitelistAllMessage implements IMessage
{
    int whitelistId;
    Whitelist<ResourceLocation> whitelist;
    int entityId;

    private WhitelistAllMessage() {}
    public WhitelistAllMessage(BlocklingWhitelist whitelist, int entityId)
    {
        this.whitelistId = whitelist.id;
        this.whitelist = whitelist;
        this.entityId = entityId;
    }

    public static void encode(WhitelistAllMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.whitelistId);
        buf.writeInt(msg.whitelist.size());
        for (ResourceLocation entry : msg.whitelist.keySet())
        {
            buf.writeString(entry.toString());
            buf.writeBoolean(msg.whitelist.get(entry));
        }
        buf.writeInt(msg.entityId);
    }

    public static WhitelistAllMessage decode(PacketBuffer buf)
    {
        WhitelistAllMessage msg = new WhitelistAllMessage();
        msg.whitelistId = buf.readInt();
        msg.whitelist = new Whitelist<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            msg.whitelist.put(new ResourceLocation(buf.readString()), buf.readBoolean());
        }
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
                    blockling.aiManager.getWhitelist(whitelistId).setWhitelist(whitelist, !isClient);
                }
            }
        });
    }
}