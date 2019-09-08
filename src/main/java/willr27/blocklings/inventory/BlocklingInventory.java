package willr27.blocklings.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.InventoryMessage;

public class BlocklingInventory implements IInventory
{
    public static final int MAIN_SLOT = 0;
    public static final int OFF_SLOT = 1;
    public static final int MATERIAL_SLOT = 2;
    public static final int INVENTORY_START_SLOT = 3;
    public static final int INVENTORY_END_SLOT = 38;

    private static final int INV_SIZE = 39;

    private BlocklingEntity blockling;

    private ItemStack[] stacks = new ItemStack[INV_SIZE];
    private ItemStack[] stacksCopy = new ItemStack[INV_SIZE];

    private boolean dirty = false;

    public BlocklingInventory(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        clear();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            stacksCopy[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory()
    {
        return INV_SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (!getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return stacks[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack stack = getStackInSlot(index);
        ItemStack copy = stack.copy();
        stack.shrink(count);
        return copy;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return stacks[index] = ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        setInventorySlotContents(index, stack, false);
    }

    public void setInventorySlotContents(int index, ItemStack stack, boolean sendPacket)
    {
        stacks[index] = stack;

        if (sendPacket)
        {
            NetworkHandler.sync(blockling.world, new InventoryMessage(stack, index, blockling.getEntityId()));
        }
    }

    @Override
    public void markDirty()
    {
        dirty = true;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            removeStackFromSlot(i);
        }
    }

    public int find(Item item)
    {
        for (int i = 0; i < this.getSizeInventory(); i++)
        {
            if (getStackInSlot(i).getItem() == item)
            {
                return i;
            }
        }

        return -1;
    }

    public ItemStack addItem(ItemStack stack)
    {
        int maxStackSize = stack.getMaxStackSize();

        for (int i = INVENTORY_START_SLOT; i < INVENTORY_END_SLOT + 1 && !stack.isEmpty(); i++)
        {
            ItemStack slotStack = getStackInSlot(i);
            if (ItemStack.areItemsEqual(stack, slotStack))
            {
                int amountToAdd = stack.getCount();
                amountToAdd = Math.min(amountToAdd, maxStackSize - slotStack.getCount());
                stack.shrink(amountToAdd);
                slotStack.grow(amountToAdd);
                setInventorySlotContents(i, slotStack);
            }
        }
        for (int i = INVENTORY_START_SLOT; i < INVENTORY_END_SLOT + 1 && !stack.isEmpty(); i++)
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

    public void detectAndSendChanges()
    {
        for (int i = 0; i < INV_SIZE; i++)
        {
            ItemStack oldStack = stacksCopy[i];
            ItemStack newStack = stacks[i];
            if (!ItemStack.areItemStacksEqual(oldStack, newStack))
            {
                NetworkHandler.sync(blockling.world, new InventoryMessage(newStack, i, blockling.getEntityId()));
                stacksCopy[i] = newStack.copy();
            }
        }
    }
}
