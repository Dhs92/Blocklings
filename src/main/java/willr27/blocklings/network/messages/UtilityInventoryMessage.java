package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;
import willr27.blocklings.utilities.Utility;

import java.util.function.Supplier;

public class UtilityInventoryMessage implements IMessage
{
    Utility utility;
    int index;
    int entityId;

    private UtilityInventoryMessage() {}
    public UtilityInventoryMessage(Utility utility, int index, int entityId)
    {
        this.utility = utility;
        this.index = index;
        this.entityId = entityId;
    }

    public static void encode(UtilityInventoryMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.utility.ordinal());
        buf.writeInt(msg.index);
        buf.writeInt(msg.entityId);
    }

    public static UtilityInventoryMessage decode(PacketBuffer buf)
    {
        UtilityInventoryMessage msg = new UtilityInventoryMessage();
        msg.utility = Utility.values()[buf.readInt()];
        msg.index = buf.readInt();
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
                    blockling.getUtilityManager().setInventory(utility, index);
                }
            }
        });
    }
}