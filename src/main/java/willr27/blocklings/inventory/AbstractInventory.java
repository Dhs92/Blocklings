package willr27.blocklings.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import willr27.blocklings.entity.blockling.BlocklingEntity;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.network.messages.InventoryMessage;

public abstract class AbstractInventory implements IInventory
{
    public final int invSize;

    protected BlocklingEntity blockling;
    protected World world;

    protected ItemStack[] stacks;
    protected ItemStack[] stacksCopy;

    private boolean dirty = false;

    public AbstractInventory(BlocklingEntity blockling, int invSize)
    {
        this.blockling = blockling;
        this.world = blockling.world;
        this.invSize = invSize;

        stacks = new ItemStack[invSize];
        stacksCopy = new ItemStack[invSize];

        clear();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            stacksCopy[i] = ItemStack.EMPTY;
        }
    }

    public int getSize()
    {
        return invSize;
    }

    @Override
    public int getSizeInventory()
    {
        return invSize;
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
        setInventorySlotContents(index, stack);
        return copy;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = getStackInSlot(index);
        setInventorySlotContents(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        stacks[index] = stack;
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
        return find(item, 0, getSizeInventory() - 1);
    }
    public int find(Item item, int startIndex, int endIndex)
    {
        for (int i = startIndex; i < endIndex + 1; i++)
        {
            if (getStackInSlot(i).getItem() == item)
            {
                return i;
            }
        }
        return -1;
    }

    public boolean couldAddItem(ItemStack stack, int slot)
    {
        boolean couldAdd = true;

        ItemStack slotStack = getStackInSlot(slot);
        if (ItemStack.areItemsEqual(stack, slotStack))
        {
            couldAdd = slotStack.getCount() + stack.getCount() <= slotStack.getMaxStackSize();
        }

        return couldAdd;
    }

    public ItemStack addItem(ItemStack stack, int slot)
    {
        ItemStack slotStack = getStackInSlot(slot);
        if (ItemStack.areItemsEqual(stack, slotStack))
        {
            int amountToAdd = stack.getCount();
            amountToAdd = Math.min(amountToAdd, slotStack.getMaxStackSize() - slotStack.getCount());
            stack.shrink(amountToAdd);
            slotStack.grow(amountToAdd);
            setInventorySlotContents(slot, slotStack);
            markDirty();
        }
        else
        {
            setInventorySlotContents(slot, stack.copy());
            stack.shrink(stack.getCount());
        }

        return stack;
    }

    public ItemStack addItem(ItemStack stack)
    {
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
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
        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
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

