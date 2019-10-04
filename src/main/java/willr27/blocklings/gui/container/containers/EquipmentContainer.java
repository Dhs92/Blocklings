package willr27.blocklings.gui.container.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import willr27.blocklings.inventory.EquipmentInventory;

public class EquipmentContainer extends Container
{
    private static final int playerInvX = 8;
    private static final int playerInvY = 74;

    public EquipmentContainer(int id, PlayerInventory playerInv)
    {
        super(null, id);
    }

    public EquipmentContainer(int id, PlayerInventory playerInv, EquipmentInventory blocklingInv)
    {
        this(id, playerInv);

        addSlot(new Slot(blocklingInv, EquipmentInventory.MAIN_SLOT, 12, 52));
        addSlot(new Slot(blocklingInv, EquipmentInventory.OFF_SLOT, 32, 52));
        addSlot(new Slot(blocklingInv, EquipmentInventory.UTILITY_SLOT_1, 12, -2));
        addSlot(new Slot(blocklingInv, EquipmentInventory.UTILITY_SLOT_2, 32, -2));

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                addSlot(new Slot(blocklingInv, j + i * 6 + 4, playerInvX + (j * 18) + 50, playerInvY + (i * 18) - 66));
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
