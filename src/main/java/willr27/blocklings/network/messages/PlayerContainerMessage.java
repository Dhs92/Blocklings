package willr27.blocklings.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import willr27.blocklings.network.IMessage;

import java.util.function.Supplier;

public class PlayerContainerMessage implements IMessage
{
    ItemStack stack;
    int slot;

    private PlayerContainerMessage() {}
    public PlayerContainerMessage(ItemStack stack, int slot)
    {
        this.stack = stack;
        this.slot = slot;
    }

    public static void encode(PlayerContainerMessage msg, PacketBuffer buf)
    {
        buf.writeItemStack(msg.stack);
        buf.writeInt(msg.slot);
    }

    public static PlayerContainerMessage decode(PacketBuffer buf)
    {
        PlayerContainerMessage msg = new PlayerContainerMessage();
        msg.stack = buf.readItemStack();
        msg.slot = buf.readInt();

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
                if (slot >= 27 && slot < 36)
                {
                    player.container.putStackInSlot(slot + 9, stack);
                }

                player.openContainer.putStackInSlot(slot, stack);
            }
        });
    }
}