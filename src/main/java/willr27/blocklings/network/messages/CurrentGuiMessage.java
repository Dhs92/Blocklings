package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class CurrentGuiMessage implements IMessage
{
    int guiId;
    int entityId;

    private CurrentGuiMessage() {}
    public CurrentGuiMessage(int guiId, int entityId)
    {
        this.guiId = guiId;
        this.entityId = entityId;
    }

    public static void encode(CurrentGuiMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.guiId);
        buf.writeInt(msg.entityId);
    }

    public static CurrentGuiMessage decode(PacketBuffer buf)
    {
        CurrentGuiMessage msg = new CurrentGuiMessage();
        msg.guiId = buf.readInt();
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
                    blockling.setCurrentGuiId(guiId, !isClient);
                }
            }
        });
    }
}