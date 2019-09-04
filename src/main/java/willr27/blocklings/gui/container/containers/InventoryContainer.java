package willr27.blocklings.gui.container.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
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
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
