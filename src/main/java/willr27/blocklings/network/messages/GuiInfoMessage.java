package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.entity.blockling.BlocklingGuiInfo;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class GuiInfoMessage implements IMessage
{
    BlocklingGuiInfo guiInfo;
    int entityId;

    private GuiInfoMessage() {}
    public GuiInfoMessage(BlocklingGuiInfo guiInfo, int entityId)
    {
        this.guiInfo = guiInfo;
        this.entityId = entityId;
    }

    public static void encode(GuiInfoMessage msg, PacketBuffer buf)
    {
        msg.guiInfo.writeToBuf(buf);
        buf.writeInt(msg.entityId);
    }

    public static GuiInfoMessage decode(PacketBuffer buf)
    {
        GuiInfoMessage msg = new GuiInfoMessage();
        msg.guiInfo = BlocklingGuiInfo.readFromBuf(buf);
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
                    blockling.setGuiInfo(guiInfo, !isClient);
                }
            }
        });
    }
}