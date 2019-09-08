package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class CustomNameMessage implements IMessage
{
    String name;
    int entityId;

    private CustomNameMessage() {}
    public CustomNameMessage(String name, int entityId)
    {
        this.name = name;
        this.entityId = entityId;
    }

    public static void encode(CustomNameMessage msg, PacketBuffer buf)
    {
        buf.writeString(msg.name);
        buf.writeInt(msg.entityId);
    }

    public static CustomNameMessage decode(PacketBuffer buf)
    {
        CustomNameMessage msg = new CustomNameMessage();
        msg.name = buf.readString();
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
                    blockling.setName(name, !isClient);
                }
            }
        });
    }
}