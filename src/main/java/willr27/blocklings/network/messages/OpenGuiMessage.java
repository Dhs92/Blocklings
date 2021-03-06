package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.gui.util.GuiHandler;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class OpenGuiMessage implements IMessage
{
    int guiId;
    int windowId;
    int entityId;
    boolean openContainerFromClient = false;

    private OpenGuiMessage() {}
    public OpenGuiMessage(int guiId, int windowId, int entityId)
    {
        this.guiId = guiId;
        this.windowId = windowId;
        this.entityId = entityId;
    }

    public OpenGuiMessage(int guiId, int windowId, int entityId, boolean openContainerFromClient)
    {
        this.guiId = guiId;
        this.windowId = windowId;
        this.entityId = entityId;
        this.openContainerFromClient = openContainerFromClient;
    }

    public static void encode(OpenGuiMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.guiId);
        buf.writeInt(msg.windowId);
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.openContainerFromClient);
    }

    public static OpenGuiMessage decode(PacketBuffer buf)
    {
        OpenGuiMessage msg = new OpenGuiMessage();
        msg.guiId = buf.readInt();
        msg.windowId = buf.readInt();
        msg.entityId = buf.readInt();
        msg.openContainerFromClient = buf.readBoolean();

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
                    if (openContainerFromClient) GuiHandler.openGui(guiId, blockling, player);
                    else GuiHandler.openGui(guiId, windowId, blockling, player, false);
                }
            }
        });
    }
}