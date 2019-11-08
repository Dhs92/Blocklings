package willr27.blocklings.inventory;

import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.item.ToolType;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.InventoryMessage;
import willr27.blocklings.utilities.Utility;

public class EquipmentInventory extends AbstractInventory
{
    public static final int MAIN_SLOT = 0;
    public static final int OFF_SLOT = 1;
    public static final int UTILITY_SLOT_1 = 2;
    public static final int UTILITY_SLOT_2 = 3;
    public static final int INVENTORY_START_SLOT = 4;

    public static final int invSize = 22;

    public EquipmentInventory(BlocklingEntity blockling)
    {
        super(blockling, invSize);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        super.setInventorySlotContents(index, stack);

        if (!blockling.world.isRemote && (index == MAIN_SLOT || index == OFF_SLOT))
        {
            blockling.getStats().updateItemBonuses();
        }

        if (!blockling.world.isRemote)
        {
            if (index == UTILITY_SLOT_1)
            {
                Utility utility = Utility.getUtility(stack);
                blockling.getUtilityManager().setUtility1(utility);

            }
            else if (index == UTILITY_SLOT_2)
            {
                Utility utility = Utility.getUtility(stack);
                blockling.getUtilityManager().setUtility2(utility);
            }
        }
    }

    public ItemStack addItem(ItemStack stack)
    {
        int maxStackSize = stack.getMaxStackSize();

        for (int i = INVENTORY_START_SLOT; i < invSize && !stack.isEmpty(); i++)
        {
            ItemStack slotStack = getStackInSlot(i);
            if (ItemStack.areItemsEqual(stack, slotStack))
            {
                int amountToAdd = stack.getCount();
                amountToAdd = Math.min(amountToAdd, maxStackSize - slotStack.getCount());
                stack.shrink(amountToAdd);
                slotStack.grow(amountToAdd);
                setInventorySlotContents(i, slotStack);
                markDirty();
            }
        }
        for (int i = INVENTORY_START_SLOT; i < invSize && !stack.isEmpty(); i++)
        {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack.isEmpty())
            {
                setInventorySlotContents(i, stack.copy());
                stack.setCount(0);
            }
        }

        return stack;
    }

    public int findToolType(ToolType type)
    {
        return findToolType(type, 0, getSizeInventory() - 1);
    }
    public int findToolType(ToolType type, int startIndex, int endIndex)
    {
        for (int i = startIndex; i < endIndex + 1; i++)
        {
            if (ToolType.isTooltype(type, getStackInSlot(i).getItem()))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void detectAndSendChanges()
    {
        if (!blockling.world.isRemote)
        {
            for (int i = 0; i < invSize; i++)
            {
                ItemStack oldStack = stacksCopy[i];
                ItemStack newStack = stacks[i];
                if (!ItemStack.areItemStacksEqual(oldStack, newStack))
                {
                    NetworkHandler.sync(blockling.world, new InventoryMessage(null, -1, newStack, i, blockling.getEntityId()));
                    stacksCopy[i] = newStack.copy();
                }
            }
        }
    }
}
