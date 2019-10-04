package willr27.blocklings.gui.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import willr27.blocklings.inventory.Utilities.CraftingTableInventory;

public class CraftingTableSlot extends Slot
{
    private final CraftingTableInventory craftingInventory;

    public CraftingTableSlot(CraftingTableInventory craftingInventory, int index, int xPosition, int yPosition)
    {
        super(craftingInventory, index, xPosition, yPosition);
        this.craftingInventory = craftingInventory;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
    {
        for (int i = 0; i < 9; i++)
        {
            craftingInventory.decrStackSize(i, 1);
        }

        return stack;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }
}
