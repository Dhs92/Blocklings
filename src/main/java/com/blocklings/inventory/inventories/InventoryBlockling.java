package com.blocklings.inventory.inventories;

import com.blocklings.entity.entities.EntityBlockling;
import com.blocklings.network.NetworkHelper;
import com.blocklings.network.messages.InventoryStackMessage;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryBlockling extends InventoryBasic
{
    public static final int UPGRADE_MATERIAL_SLOT = 0;
    public static final int MAIN_HAND_SLOT = 1;
    public static final int OFF_HAND_SLOT = 2;

    private EntityBlockling blockling;

    public InventoryBlockling(EntityBlockling blockling, String inventoryTitle)
    {
        super(inventoryTitle, true, 39);

        this.blockling = blockling;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        setInventorySlotContents(index, stack, !blockling.world.isRemote);
    }

    public void setInventorySlotContents(int index, ItemStack stack, boolean sendPacket)
    {
        super.setInventorySlotContents(index, stack);

        if (sendPacket)
        {
            NetworkHelper.sync(blockling.world, new InventoryStackMessage(index, stack, blockling.getEntityId()));
        }
    }

    @Override
    public ItemStack addItem(ItemStack stack)
    {
        ItemStack itemstack = stack.copy();
        int unlockedSlots = 36;
        int u = unlockedSlots / 12;

        // Each row
        for (int p = 0; p < 4; p++)
        {
            // Cols unlocked in each row
            int startIndex = 3 + (9 * p);
            int endIndex = 3 + (3 * u) + (9 * p);
            for (int i = startIndex; i < endIndex; ++i)
            {
                ItemStack itemstack1 = this.getStackInSlot(i);

                if (itemstack1.isEmpty())
                {
                    this.setInventorySlotContents(i, itemstack);
                    this.markDirty();
                    return ItemStack.EMPTY;
                }

                if (ItemStack.areItemsEqual(itemstack1, itemstack))
                {
                    int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
                    int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());

                    if (k > 0)
                    {
                        itemstack1.grow(k);
                        itemstack.shrink(k);

                        if (itemstack.isEmpty())
                        {
                            this.markDirty();
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }

        if (itemstack.getCount() != stack.getCount())
        {
            this.markDirty();
        }

        return itemstack;
    }

    public boolean contains(Item item)
    {
        return find(item) != -1;
    }

    public int find(Item item)
    {
        for (int i = 3; i < this.getSizeInventory(); i++)
        {
            if (getStackInSlot(i).getItem() == item)
            {
                return i;
            }
        }

        return -1;
    }

    public boolean takeStackFromInventory(ItemStack stack)
    {
        int stackSize = stack.getCount();
        List<ItemStack> slotsToTakeFrom = new ArrayList<>();

        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack.getItem() == stack.getItem() && slotStack.getMetadata() == stack.getMetadata())
            {
                if (stackSize > slotStack.getCount())
                {
                    stackSize -= slotStack.getCount();
                    slotsToTakeFrom.add(slotStack);
                }
                else
                {
                    slotStack.shrink(stackSize);
                    for (ItemStack s : slotsToTakeFrom)
                    {
                        s.setCount(0);
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
