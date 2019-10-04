package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.IMessage;
import willr27.blocklings.utilities.Utility;

import java.util.function.Supplier;

public class InventoryMessage implements IMessage
{
    Utility utility;
    int utilityIndex;
    ItemStack stack;
    int slot;
    int entityId;

    private InventoryMessage() {}
    public InventoryMessage(Utility utility, int utilityIndex, ItemStack stack, int slot, int entityId)
    {
        this.utility = utility;
        this.utilityIndex = utilityIndex;
        this.stack = stack;
        this.slot = slot;
        this.entityId = entityId;
    }

    public static void encode(InventoryMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.utility == null ? -1 : msg.utility.ordinal());
        buf.writeInt(msg.utilityIndex);
        buf.writeItemStack(msg.stack);
        buf.writeInt(msg.slot);
        buf.writeInt(msg.entityId);
    }

    public static InventoryMessage decode(PacketBuffer buf)
    {
        InventoryMessage msg = new InventoryMessage();
        int utility = buf.readInt();
        msg.utility = utility == -1 ? null : Utility.values()[utility];
        msg.utilityIndex = buf.readInt();
        msg.stack = buf.readItemStack();
        msg.slot = buf.readInt();
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
                    if (utility != null) blockling.getUtilityManager().getInventory(utility, utilityIndex).setInventorySlotContents(slot, stack);
                    else blockling.inventory.setInventorySlotContents(slot, stack);
                }
            }
        });
    }
}