package willr27.blocklings.gui.container.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import willr27.blocklings.inventory.BlocklingInventory;

public class InventoryContainer extends Container
{
    private static final int playerInvX = 36;
    private static final int playerInvY = 74;

    public InventoryContainer(int id, PlayerInventory playerInv)
    {
        super(null, id);
    }

    public InventoryContainer(int id, PlayerInventory playerInv, BlocklingInventory blocklingInv)
    {
        this(id, playerInv);

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(blocklingInv, j + i * 9 + BlocklingInventory.INVENTORY_START_SLOT, playerInvX + (j * 18), -2 + (i * 18)));
            }
        }

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, playerInvX + (j * 18), playerInvY + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(playerInv, i, playerInvX + (i * 18), playerInvY + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack = slot.getStack();
            stack = itemStack.copy();

            if (slotIndex >= 36)
            {
                int unlockedSlots = 36;
                int u = unlockedSlots / 12;

                if (!this.mergeItemStack(itemStack, 0, 3 * u, false))
                {
                    if (!this.mergeItemStack(itemStack, 9, 12 * u, false))
                    {
                        if (!this.mergeItemStack(itemStack, 18, 21 * u, false))
                        {
                            if (!this.mergeItemStack(itemStack, 27, 30 * u, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
            }
            else
            {
                if (!this.mergeItemStack(itemStack, 36, 72, false))
                {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
