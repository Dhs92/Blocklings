package willr27.blocklings.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import willr27.blocklings.entity.blockling.BlocklingEntity;

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

    private boolean dirty = false;

    public BlocklingInventory(BlocklingEntity blockling)
    {
        this.blockling = blockling;
        clear();
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
}
