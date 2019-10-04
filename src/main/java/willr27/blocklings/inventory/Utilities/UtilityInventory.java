package willr27.blocklings.inventory.Utilities;

import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.inventory.AbstractInventory;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.InventoryMessage;
import willr27.blocklings.utilities.Utility;

public class UtilityInventory extends AbstractInventory
{
    public final Utility utility;
    public final int utilityIndex;

    public UtilityInventory(BlocklingEntity blockling, int invSize, Utility utility, int utilityIndex)
    {
        super(blockling, invSize);
        this.utility = utility;
        this.utilityIndex = utilityIndex;
    }

    @Override
    public void detectAndSendChanges()
    {
        for (int i = 0; i < invSize; i++)
        {
            ItemStack oldStack = stacksCopy[i];
            ItemStack newStack = stacks[i];
            if (!ItemStack.areItemStacksEqual(oldStack, newStack))
            {
                NetworkHandler.sync(blockling.world, new InventoryMessage(utility, utilityIndex, newStack, i, blockling.getEntityId()));
                stacksCopy[i] = newStack.copy();
            }
        }
    }
}
