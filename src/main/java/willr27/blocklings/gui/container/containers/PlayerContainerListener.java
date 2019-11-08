package willr27.blocklings.gui.container.containers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.PlayerContainerMessage;

public class PlayerContainerListener implements IContainerListener
{
    private final ServerPlayerEntity player;

    public PlayerContainerListener(ServerPlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
    {

    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        NetworkHandler.sendTo(player, new PlayerContainerMessage(stack, slotInd));
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
    {

    }
}
